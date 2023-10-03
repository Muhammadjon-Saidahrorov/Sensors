package uz.gita.sensors

import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.gita.sensors.databinding.ActivitySecondBinding
import kotlin.math.abs

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    private val sensorManager by lazy { getSystemService(SENSOR_SERVICE) as SensorManager }
    private val sensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    private val pointEnd by lazy { Point(binding.container.width, binding.container.height) }
    private var lastEvent: SensorEvent? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val scopeMain = CoroutineScope(Dispatchers.Main.immediate)
    private var logic = true

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            lastEvent = event
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        movementBoll()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(listener)
    }

    private fun periodTimer(): Flow<Unit> = flow {
        while (true) {
            delay(80)
            emit(Unit)
        }
    }.flowOn(Dispatchers.IO)

    private fun movementBoll() {
        periodTimer().onEach {
            lastEvent?.let {
                if (logic) {
                    logger("${it.values[0]} , ${it.values[1]}")

                    logger("0 -> ${binding.ball.x}")
                    binding.ball.x -= binding.ball.width * (it.values[0] / 5)

                    logger("1 -> ${binding.ball.x}")
                    logger("2 -> ${binding.ball.width * (it.values[0] / 5)}")
                    logger("-----------------------")
                    if (binding.ball.x < 0)
                        binding.ball.x = 0f
                    if (binding.ball.x > pointEnd.x - binding.ball.width)
                        binding.ball.x = (pointEnd.x - binding.ball.width).toFloat()

                    binding.ball.y += binding.ball.height * (it.values[1] / 5)

                    if (binding.ball.y < 0f)
                        binding.ball.y = 0f
                    if (binding.ball.y > pointEnd.y - binding.ball.height)
                        binding.ball.y = (pointEnd.y - binding.ball.height).toFloat()
                }

                finishBall()
            }
        }.launchIn(scope)
    }

    private suspend fun finishBall() {
        val finishX = binding.finish.x
        val finishY = binding.finish.y
        val ballX = binding.ball.x
        val ballY = binding.ball.y

        if (finishX != 0f) {
            if (abs(finishX - ballX) <= 20f && abs(finishY - ballY) <= 20f && logic) {
                scopeMain.launch {
                    binding.ball.animate()
                        .setDuration(500)
                        .scaleX(0f)
                        .scaleY(0f)
                        .start()

                    logic = false

                    delay(500)

                    binding.ball.visibility = View.INVISIBLE

                    Toast.makeText(this@SecondActivity, "FINISH", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}