package edu.skku.cs.motionsensor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import render.animations.*
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
//    lateinit var ball1: ImageView
//    lateinit var ball2: ImageView
//    lateinit var ball3: ImageView
//    lateinit var ball4: ImageView
//    lateinit var ball5: ImageView
//    lateinit var ball6: ImageView
//    lateinit var ball7: ImageView
//    lateinit var ball8: ImageView


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "MainActivity - onCreate() called")

        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()
        fun Int.pxToDp(displayMetrics: DisplayMetrics): Int = (this / displayMetrics.density).toInt()

        ball1.setOnClickListener{
            val intent = Intent(this, Loading::class.java)
            startActivity(intent)
        }

//        ball1 = findViewById<ImageView>(R.id.ball1)
//        ball2 = findViewById(R.id.ball2)
//        ball3 = findViewById(R.id.ball3)
//        ball4 = findViewById(R.id.ball4)
//        ball5 = findViewById(R.id.ball5)
//        ball6 = findViewById(R.id.ball6)
//        ball7 = findViewById(R.id.ball7)
//        ball8 = findViewById(R.id.ball8)
//        val ball3 : ImageView = findViewById(R.id.ball3)
//        val ball4 : ImageView = findViewById(R.id.ball4)
//        val ball5 : ImageView = findViewById(R.id.ball5)
//        val ball6 : ImageView = findViewById(R.id.ball6)

        val render = Render(this)
        render.setAnimation(Attention().Shake(ball1))
        render.start()
        render.setAnimation(Attention().Shake(ball2))
        render.start()
        render.setAnimation(Attention().Shake(ball3))
        render.start()
        render.setAnimation(Attention().Shake(ball4))
        render.start()
        render.setAnimation(Attention().Shake(ball5))
        render.start()
        render.setAnimation(Attention().Shake(ball6))
        render.start()
        render.setAnimation(Attention().Shake(ball7))
        render.start()
        render.setAnimation(Bounce().InDown(ball8))
        render.start()

        render.start()

        // 길이를 세로 모드로 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d(TAG, "MainActivity - onAccuracyChanged() called")

    }

    override fun onSensorChanged(event: SensorEvent?) {
        //     Log.d(TAG, "MainActivity - onSensorChanged() called")

        val x: Float = event?.values?.get(0) as Float
        val y: Float = event?.values?.get(1) as Float
        val z: Float = event?.values?.get(2) as Float

//        //
//        x_text.text = "X: " + x.toInt().toString()
//        y_text.text = "Y: " + y.toInt().toString()
//        z_text.text = "Z: " + z.toInt().toString()

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