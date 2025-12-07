package com.example.gameproject

object GameConstants {
    private const val TARGET_FPS = 60f

    // Размер абстрактного игрового поля (логические единицы)
    const val GAME_WIDTH = 1000f
    const val GAME_HEIGHT = 1000f

    // Все физические размеры задаем в этих абстрактных единицах
    const val LOGIC_BALL_RADIUS = 20f
    const val LOGIC_SPEED_FACTOR = 30f // Относительная скорость

    // Начальные координаты лабиринта (те, что были 50f, 1000f, 1000f)
    const val LOGIC_WALL_LEFT = 50f
    // ... и т.д.

    const val DELTA_TIME = 1f / TARGET_FPS
}