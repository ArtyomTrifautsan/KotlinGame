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
    private var ballX = maze.startX
    private var ballY = maze.startY
    private var velocityX = 0f
    private var velocityY = 0f
    private var isFinished = false

    // --- Объекты для рисования (Paint) ---
    private val ballPaint = Paint().apply { color = Color.RED }
    private val wallPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = WALL_THICKNESS
    }
    private val finishPaint = Paint().apply { color = Color.GREEN }
    private val textPaint = Paint().apply {
        color = Color.BLUE
        textSize = 100f
        textAlign = Paint.Align.CENTER
    }

    // --- Публичный метод для обновления скорости (вызывается из MainActivity) ---
    fun updateAcceleration(accelX: Float, accelY: Float) {
        // Мы используем данные акселерометра напрямую для задания скорости
        // Умножаем на фактор для более заметного движения
        velocityX = -accelX * SPEED_FACTOR // -X для интуитивного управления
        velocityY = accelY * SPEED_FACTOR
    }

    // --- Главная логика игры: обновление позиции и коллизии ---
    private fun updateGame() {
        if (isFinished) return

        // 1. Обновление позиции
        var newBallX = ballX + velocityX * (16f / 1000f) // delta T ~ 16ms (60 FPS)
        var newBallY = ballY + velocityY * (16f / 1000f)

        // Ограничение движения границами экрана (или лабиринта, если он не на весь экран)
        val minX = 50f + BALL_RADIUS
        val maxX = width.toFloat() - 50f - BALL_RADIUS
        val minY = 50f + BALL_RADIUS
        val maxY = height.toFloat() - 50f - BALL_RADIUS

        newBallX = max(minX, min(newBallX, maxX))
        newBallY = max(minY, min(newBallY, maxY))

        // 2. Проверка коллизии со стенами (Упрощенная проверка!)
        var collisionOccurred = false
        // Более сложная логика коллизии тут опущена, но в минимальной версии
        // мы будем просто ограничивать движение.
        // Для демонстрации, пока просто приравняем:
        ballX = newBallX
        ballY = newBallY

        // 3. Проверка финиша
        val (fx1, fy1, fx2, fy2) = maze.finishRect
        if (ballX > fx1 && ballX < fx2 && ballY > fy1 && ballY < fy2) {
            isFinished = true
        }
    }

    // --- Метод отрисовки ---
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Обновляем состояние игры
        updateGame()

        // 2. Рисуем финишную зону
        val (fx1, fy1, fx2, fy2) = maze.finishRect
        canvas.drawRect(fx1, fy1, fx2, fy2, finishPaint)

        // 3. Рисуем лабиринт (стены)
        for (wall in maze.walls) {
            canvas.drawLine(wall[0], wall[1], wall[2], wall[3], wallPaint)
        }

        // 4. Рисуем шарик
        canvas.drawCircle(ballX, ballY, BALL_RADIUS, ballPaint)

        // 5. Сообщение о победе
        if (isFinished) {
            canvas.drawText("ПОБЕДА!", width / 2f, height / 2f, textPaint)
        }

        // 6. Запускаем перерисовку (игровой цикл)
        invalidate()
    }
}