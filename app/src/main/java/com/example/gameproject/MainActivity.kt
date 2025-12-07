package com.example.gameproject
// Файл: MainActivity.kt

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    // Переменные для работы с сенсорами
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var gameView: GameView // Ссылка на наш GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получаем ссылку на наш GameView из макета
        gameView = findViewById(R.id.game_view)

        // Инициализация SensorManager и акселерометра
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    // --- Методы Жизненного цикла Activity для регистрации/отмены регистрации сенсора ---

    // Регистрируем слушателя сенсора при возобновлении работы Activity
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            // Регистрируем слушателя. SENSOR_DELAY_GAME - это хороший баланс
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    // Отменяем регистрацию слушателя при паузе, чтобы экономить батарею
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // --- Реализация интерфейса SensorEventListener ---

    // Вызывается при изменении показаний сенсора
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Данные акселерометра:
            val accelX = event.values[0] // Ускорение вдоль оси X
            val accelY = event.values[1] // Ускорение вдоль оси Y

            // Передаем данные в наш GameView для обновления скорости
            gameView.updateAcceleration(accelX, accelY)
        }
    }

    // Вызывается при изменении точности сенсора (обычно можно игнорировать)
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Не используется в нашем случае
    }
}