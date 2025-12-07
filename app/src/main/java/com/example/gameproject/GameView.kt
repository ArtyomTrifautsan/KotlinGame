package com.example.gameproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

// Константы для игры
private const val BALL_RADIUS = 20f
private const val WALL_THICKNESS = 10f
private const val SPEED_FACTOR = 30f // Множитель для ускорения

// Класс, который хранит данные о лабиринте
class Maze {
    // Простой лабиринт: 4 стены
    // x1, y1, x2, y2
    val walls = listOf(
        floatArrayOf(50f, 50f, 1000f, 50f), // Верхняя
        floatArrayOf(50f, 50f, 50f, 1000f), // Левая
        floatArrayOf(1000f, 50f, 1000f, 1000f), // Правая
        floatArrayOf(50f, 1000f, 1000f, 1000f), // Нижняя

        // Добавим одну внутреннюю стену для усложнения
        floatArrayOf(200f, 200f, 800f, 200f)
    )
    val startX = 100f
    val startY = 100f
    val finishRect = floatArrayOf(850f, 850f, 950f, 950f) // x1, y1, x2, y2
}

class GameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    // --- Переменные состояния игры ---
    private val maze = Maze()

    // В эти переменные будет храниться ТЕКУЩАЯ МАСШТАБИРОВАННАЯ позиция шарика (в пикселях)
    private var ballX = 0f
    private var ballY = 0f

    // Скорость, которую мы получаем из акселерометра (тоже должна быть масштабирована)
    private var velocityX = 0f
    private var velocityY = 0f

    private var isFinished = false

    // Факторы масштабирования (сначала 1f, будут рассчитаны в onSizeChanged)
    private var scaleX: Float = 1f
    private var scaleY: Float = 1f

    private var isInitialized = false // Флаг для однократной инициализации

    // --- Объекты для рисования (Paint) ---
    private val ballPaint = Paint().apply {
        color = Color.RED
    }

    private val wallPaint = Paint().apply {
        color = Color.BLACK
        // Толщина стен берется из абстрактных констант, масштабируется
        // Но для инициализации Paint мы можем использовать фиксированное значение,
        // а strokeWidth можно менять динамически в onDraw, если нужно.
        // Здесь мы просто задаем базовые свойства.
        strokeWidth = 10f
    }

    private val finishPaint = Paint().apply {
        color = Color.GREEN
    }

    private val textPaint = Paint().apply {
        color = Color.BLUE
        textSize = 100f // Размер текста для сообщения о победе
        textAlign = Paint.Align.CENTER
    }

    // --- Пересчет масштаба при изменении размера экрана ---
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Расчет масштаба: Реальный размер / Логический размер
        scaleX = w.toFloat() / GameConstants.GAME_WIDTH
        scaleY = h.toFloat() / GameConstants.GAME_HEIGHT

        // Выполняем инициализацию, только когда известны размеры и масштаб
        if (!isInitialized) {
            initGame()
        }
    }

    // --- Инициализация игры (вызывается 1 раз) ---
    private fun initGame() {
        // Устанавливаем начальную позицию, масштабированную к экрану
        ballX = maze.startX * scaleX
        ballY = maze.startY * scaleY
        isInitialized = true
    }

    // --- Обновление скорости (вызывается из MainActivity) ---
    fun updateAcceleration(accelX: Float, accelY: Float) {
        // Мы берем данные акселерометра и умножаем их на фактор скорости И фактор масштабирования,
        // чтобы движение соответствовало размеру экрана.
        val speedFactor = GameConstants.LOGIC_SPEED_FACTOR
        velocityX = -accelX * speedFactor * scaleX
        velocityY = accelY * speedFactor * scaleY
    }

    // --- Главная логика игры: обновление позиции и коллизии ---
    private fun updateGame() {
        if (isFinished || !isInitialized) return // Предотвращаем обновление до инициализации

        // --- 1. Обновление позиции ---

        // Используем DELTA_TIME, чтобы контролировать движение, независимое от FPS
        val dx = velocityX * GameConstants.DELTA_TIME
        val dy = velocityY * GameConstants.DELTA_TIME

        var newBallX = ballX + dx
        var newBallY = ballY + dy

        // --- 2. Ограничение движения (Учитываем масштабирование) ---

        // Радиус шарика в пикселях (масштабированный)
        val scaledRadius = GameConstants.LOGIC_BALL_RADIUS * scaleX

        // Минимальная/Максимальная граница лабиринта (в логических единицах)
        val logicMinX = GameConstants.LOGIC_WALL_LEFT
        val logicMaxX = GameConstants.GAME_WIDTH - GameConstants.LOGIC_WALL_LEFT

        // Переводим границы в пиксели
        val minBoundX = logicMinX * scaleX + scaledRadius
        val maxBoundX = logicMaxX * scaleX - scaledRadius
        val minBoundY = logicMinX * scaleY + scaledRadius
        val maxBoundY = logicMaxX * scaleY - scaledRadius


        // Применяем ограничения
        newBallX = max(minBoundX, min(newBallX, maxBoundX))
        newBallY = max(minBoundY, min(newBallY, maxBoundY))

        // Обновляем текущее положение
        ballX = newBallX
        ballY = newBallY

        // --- 3. Проверка финиша ---

        // Нужно масштабировать координаты финиша, чтобы проверить коллизию
        val (fx1, fy1, fx2, fy2) = maze.finishRect

        val scaledFx1 = fx1 * scaleX
        val scaledFy1 = fy1 * scaleY
        val scaledFx2 = fx2 * scaleX
        val scaledFy2 = fy2 * scaleY

        if (ballX > scaledFx1 && ballX < scaledFx2 && ballY > scaledFy1 && ballY < scaledFy2) {
            isFinished = true
        }
    }

    // --- Метод отрисовки ---
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isInitialized) return // Не рисовать, пока не инициализировано

        updateGame() // 1. Обновляем состояние игры

        // Радиус шарика в пикселях
        val scaledRadius = GameConstants.LOGIC_BALL_RADIUS * scaleX

        // --- Рисуем финишную зону (масштабируем координаты) ---
        val (fx1, fy1, fx2, fy2) = maze.finishRect
        canvas.drawRect(fx1 * scaleX, fy1 * scaleY, fx2 * scaleX, fy2 * scaleY, finishPaint)

        // --- Рисуем лабиринт (масштабируем координаты) ---
        for (wall in maze.walls) {
            canvas.drawLine(
                wall[0] * scaleX, wall[1] * scaleY,
                wall[2] * scaleX, wall[3] * scaleY, wallPaint)
        }

        // --- Рисуем шарик (используем уже масштабированные ballX/Y) ---
        canvas.drawCircle(ballX, ballY, scaledRadius, ballPaint)

        // ... (Сообщение о победе) ...

        invalidate()
    }
}