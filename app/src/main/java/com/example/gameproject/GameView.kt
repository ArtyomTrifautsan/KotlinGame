package com.example.gameproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

private const val BALL_RADIUS = 10f
private const val WALL_THICKNESS = 10f
private const val SPEED_FACTOR = 150f

class Maze(val viewWidth: Int, val viewHeight: Int) {
    private val cellSize = 50f

    // Карта лабиринта (10x12)
    // 1 - стена, 0 - проход, S - старт, F - финиш
    private val map = listOf(
        "1111111111111111111111111",
        "1S00000001000000000100001",
        "1011111001011111100101101",
        "1010001001010000100100101",
        "1010101001010110111110101",
        "1000100000000100000000101",
        "1111111111100111111110101",
        "1000000000100000000010001",
        "1011111110111110111011111",
        "1010000010000010100010001",
        "1010111011111010101110101",
        "1000100000001000101000101",
        "1110101111101111101011101",
        "1000100000100000001000001",
        "1011111110111111111111101",
        "1010000000000000000000101",
        "1010111111111111111110101",
        "1010100000000010000010101",
        "1010101111111010111010101",
        "1000101000001010100010001",
        "1111110101101010101111101",
        "1000000101000010100000101",
        "1011111101111110111110101",
        "1000000000000010000000101", // +1 слой
        "1111111111110110111110101", // +2 слой
        "1000000000100010100000101", // +3 слой
        "1011111110101110101111101", // +4 слой
        "1000000000100000000000F01", // +5 слой (Финиш переехал сюда)
        "1111111111111111111111111"
    )

    private val mazeTotalWidth = map[0].length * cellSize
    private val mazeTotalHeight = map.size * cellSize

    val offsetX = (viewWidth - mazeTotalWidth) / 2f
    val offsetY = (viewHeight - mazeTotalHeight) / 2f

    val walls = mutableListOf<FloatArray>()
    val wallRects = mutableListOf<RectF>()
    var startX = 0f
    var startY = 0f
    var finishRect = floatArrayOf(0f, 0f, 0f, 0f)

    init {
        generateLevel()
    }

    private fun generateLevel() {
        walls.clear()
        for (row in map.indices) {
            for (col in map[row].indices) {
                val char = map[row][col]

                val x = col * cellSize + offsetX
                val y = row * cellSize + offsetY

                if (char == 'S') {
                    startX = x + cellSize / 2
                    startY = y + cellSize / 2
                }
                if (char == 'F') {
                    finishRect = floatArrayOf(x + 10f, y + 10f, x + cellSize - 10f, y + cellSize - 10f)
                }


                if (char == '1') {
                    wallRects.add(RectF(x, y, x + cellSize, y + cellSize))



                    if (row == 0 || map[row - 1][col] != '1') {
                        walls.add(floatArrayOf(x, y, x + cellSize, y))
                    }

                    if (row == map.size - 1 || map[row + 1][col] != '1') {
                        walls.add(floatArrayOf(x, y + cellSize, x + cellSize, y + cellSize))
                    }

                    if (col == 0 || map[row][col - 1] != '1') {
                        walls.add(floatArrayOf(x, y, x, y + cellSize))
                    }

                    if (col == map[row].length - 1 || map[row][col + 1] != '1') {
                        walls.add(floatArrayOf(x + cellSize, y, x + cellSize, y + cellSize))
                    }
                }
            }
        }
    }
}

class GameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {


    private var maze: Maze? = null
    private var ballX = 0f
    private var ballY = 0f
    private var velocityX = 0f
    private var velocityY = 0f
    private var isFinished = false


    private var startTime = 0L
    private var elapsedTime = 0L
    private var isTimerRunning = false


    fun startTimer() {
        startTime = System.currentTimeMillis()
        isTimerRunning = true
    }
    fun stopTimer(): Long {
        if (isTimerRunning) {
            elapsedTime = System.currentTimeMillis() - startTime
            isTimerRunning = false
        }
        return elapsedTime
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val newMaze = Maze(w, h)
        maze = newMaze
        ballX = newMaze.startX
        ballY = newMaze.startY
        startTimer()
    }


    private val wallFillPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }
    private val ballPaint = Paint().apply { color = Color.RED }
    private val wallPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = WALL_THICKNESS
        strokeCap = Paint.Cap.ROUND
    }
    private val finishPaint = Paint().apply { color = Color.GREEN }
    private val textPaint = Paint().apply {
        color = Color.BLUE
        textSize = 100f
        textAlign = Paint.Align.CENTER
    }


    fun updateAcceleration(accelX: Float, accelY: Float) {
        velocityX = -accelX * SPEED_FACTOR
        velocityY = accelY * SPEED_FACTOR
    }


    private fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        return String.format("%02d:%03d", seconds%1000, millis%1000)
    }
    fun getCurrentTime(): Long {
        return if (isTimerRunning) {
            System.currentTimeMillis() - startTime
        } else {
            elapsedTime
        }
    }

    override fun onDraw(canvas: Canvas) {
        val m = maze ?: return
        super.onDraw(canvas)

        updateGame()

        val currentTime = getCurrentTime()
        val timeText = "Time: ${formatTime(currentTime)}"

        val timerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
            textAlign = Paint.Align.RIGHT
        }

        canvas.drawText(timeText, width.toFloat()-20f, 100f, timerPaint)

        val (fx1, fy1, fx2, fy2) = m.finishRect
        canvas.drawRect(fx1, fy1, fx2, fy2, finishPaint)

        for (rect in m.wallRects) {
            canvas.drawRect(rect, wallFillPaint)
        }

        for (wall in m.walls) {
            canvas.drawLine(wall[0], wall[1], wall[2], wall[3], wallPaint)
        }

        canvas.drawCircle(ballX, ballY, BALL_RADIUS, ballPaint)

        if (isFinished) {
            val overlayPaint = Paint().apply { color = Color.argb(150, 255, 255, 255) }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

            canvas.drawText("ПОБЕДА!", width / 2f, height / 2f, textPaint)

            val hintPaint = Paint().apply {
                color = Color.GRAY
                textSize = 40f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Нажми, чтобы начать заново", width / 2f, height / 2f + 100f, hintPaint)
        }

        invalidate()
    }

    private fun isColliding(x: Float, y: Float, wall: FloatArray): Boolean {
        val x1 = wall[0]
        val y1 = wall[1]
        val x2 = wall[2]
        val y2 = wall[3]

        val l2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)
        if (l2 == 0f) return false

        var t = ((x - x1) * (x2 - x1) + (y - y1) * (y2 - y1)) / l2
        t = max(0f, min(1f, t))

        val closestX = x1 + t * (x2 - x1)
        val closestY = y1 + t * (y2 - y1)


        val distanceX = x - closestX
        val distanceY = y - closestY
        val distanceSquared = distanceX * distanceX + distanceY * distanceY

        val collisionThreshold = BALL_RADIUS + (WALL_THICKNESS / 2f)
        return distanceSquared < collisionThreshold * collisionThreshold
    }

    var onFinishCallback: ((Long) -> Unit)? = null

    private fun updateGame() {
        val m = maze ?: return
        if (isFinished) return

        val dt = 16f / 1000f
        val nextX = ballX + velocityX * dt
        val nextY = ballY + velocityY * dt

        var canMoveX = true
        for (wall in m.walls) {
            if (isColliding(nextX, ballY, wall)) {
                canMoveX = false
                break
            }
        }
        if (canMoveX) {
            ballX = nextX
        }

        var canMoveY = true
        for (wall in m.walls) {
            if (isColliding(ballX, nextY, wall)) {
                canMoveY = false
                break
            }
        }
        if (canMoveY) {
            ballY = nextY
        }

        ballX = max(BALL_RADIUS, min(ballX, width.toFloat() - BALL_RADIUS))
        ballY = max(BALL_RADIUS, min(ballY, height.toFloat() - BALL_RADIUS))

        val (fx1, fy1, fx2, fy2) = m.finishRect
        if (ballX > fx1 && ballX < fx2 && ballY > fy1 && ballY < fy2) {
            isFinished = true
            val time = stopTimer()
            onFinishCallback?.invoke(time)
        }
    }

    fun resetGame() {
        maze?.let {
            ballX = it.startX
            ballY = it.startY
            velocityX = 0f
            velocityY = 0f
            isFinished = false
            elapsedTime = 0L
            startTimer()
            invalidate()
        }
    }

    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        if (event.action == android.view.MotionEvent.ACTION_DOWN) {
            if (isFinished) {
                resetGame()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
