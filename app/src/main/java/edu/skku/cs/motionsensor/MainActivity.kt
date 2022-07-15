package edu.skku.cs.motionsensor

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timer
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    val TAG: String = "로그"

    // 센서매니저
    private lateinit var sensorManager: SensorManager

    // 흔들림 센서
    private var accel: Float = 0.0f
    private var accelCurrent: Float = 0.0f
    private var accelLast: Float = 0.0f
    var num : Int = 0
    var movestatus = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {

        // 길이를 세로 모드로 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "MainActivity - onCreate() called")

        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d(TAG, "MainActivity - onAccuracyChanged() called")

    }

    override fun onSensorChanged(event: SensorEvent?) {
        //     Log.d(TAG, "MainActivity - onSensorChanged() called")

        val x: Float = event?.values?.get(0) as Float
        val y: Float = event?.values?.get(1) as Float
        val z: Float = event?.values?.get(2) as Float

        //
        x_text.text = "X: " + x.toInt().toString()
        y_text.text = "Y: " + y.toInt().toString()
        z_text.text = "Z: " + z.toInt().toString()

        accelLast = accelCurrent

        accelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val delta: Float = accelCurrent - accelLast

        accel = accel * 0.9f + delta

        if (accel > 8){
            Log.d(TAG, "MainActivity - 흔들었음")
            Log.d(TAG, "MainActivity - accel : ${accel}")
        }

        val x_g = event.values[0]
        val z_g = event.values[2]


        val moved = sqrt((x * x + z * z).toDouble()).toFloat()

//        runBlocking {
//            CoroutineScope(Dispatchers.IO).launch {
//                if (moved > 8) {
//                    Log.d(TAG, "x_g ${x_g}, z_g ${z_g}")
//                    Log.d(TAG, "기울었음 ${moved}")
//                    delay(3000)
//                    Log.d(TAG, "3초 지남 ${moved}")
//                    Log.d(TAG, "A 실행 중 ")
//                }
//            }
//        }
    }

    override fun onResume() {

//        Log.d(TAG, "MainActivity - onResume() called")
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
       // sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }

    override fun onPause() {

//        Log.d(TAG, "MainActivity - onPause() called")
        sensorManager.unregisterListener(this)

        super.onPause()
    }
}