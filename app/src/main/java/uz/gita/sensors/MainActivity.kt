package uz.gita.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import uz.gita.sensors.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sensorService by lazy { getSystemService(SENSOR_SERVICE) as SensorManager }
    private val sensor by lazy { sensorService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    private var lastAmount = -1000f
    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
//                logger("${it.values[0]} , ${it.values[1]} , ${it.values[2]}")
                if (lastAmount > -1000f) {
                    if (abs(lastAmount - it.values[0]) > 0.3) {
                        binding.img.rotation = it.values[0] * 9
                    }
                    lastAmount = it.values[0]
                } else lastAmount = it.values[0]
            }

        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        sensorService.getSensorList(Sensor.TYPE_ALL).forEach {
//            logger(it.name)
//        }
    }

    override fun onResume() {
        super.onResume()
        sensorService.registerListener(listener, sensor,  SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorService.unregisterListener(listener)
    }
}


fun logger(message: String, tag: String = "TTT") {
    Log.d(tag, message)
}

