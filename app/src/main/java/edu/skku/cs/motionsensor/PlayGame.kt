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
import android.view.View
import android.widget.ImageView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_play_game.*
import kotlinx.coroutines.Dispatchers.IO
import render.animations.*
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.timer
import kotlin.math.sqrt

class PlayGame : AppCompatActivity(), SensorEventListener {

    val TAG: String = "로그"

    // 센서매니저
    private lateinit var sensorManager: SensorManager

    // 흔들림 센서
    private var accel: Float = 0.0f
    private var accelCurrent: Float = 0.0f
    private var accelLast: Float = 0.0f
    var num : Int = 0
    var movestatus = false
    var x: Float = 0.0f
    var y: Float = 0.0f
    var z: Float = 0.0f
    var delta: Float = 0.0f
    var moved: Float = 0.0f
    lateinit var tomatoVisibleList: ArrayList<ImageView>
    var sortList = ArrayList<Int>()
    lateinit var mSocket: Socket

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "MainActivity - onCreate() called")

        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)
        sortList.add(0)

        val tomatoList = arrayListOf<ImageView>(
            findViewById<ImageView>(R.id.ball1),
            findViewById<ImageView>(R.id.ball2),
            findViewById<ImageView>(R.id.ball3),
            findViewById<ImageView>(R.id.ball4),
            findViewById<ImageView>(R.id.ball5),
            findViewById<ImageView>(R.id.ball6),
            findViewById<ImageView>(R.id.ball7),
            findViewById<ImageView>(R.id.ball8),
            findViewById<ImageView>(R.id.ball9),
            findViewById<ImageView>(R.id.ball10),
            findViewById<ImageView>(R.id.ball11),
            findViewById<ImageView>(R.id.ball12),
            findViewById<ImageView>(R.id.ball13),
            findViewById<ImageView>(R.id.ball14),
            findViewById<ImageView>(R.id.ball15),
            findViewById<ImageView>(R.id.ball16),
            findViewById<ImageView>(R.id.ball17),
            findViewById<ImageView>(R.id.ball18),
            findViewById<ImageView>(R.id.ball19),
            findViewById<ImageView>(R.id.ball20),
            )

        tomatoVisibleList = arrayListOf<ImageView>()

        fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()
        fun Int.pxToDp(displayMetrics: DisplayMetrics): Int = (this / displayMetrics.density).toInt()

        mSocket = SocketHandler.getSocket()

        var new = tomatoList.get(tomatoVisibleList.size)
        tomatoVisibleList.add(new)
        new.visibility = View.VISIBLE
        getNumTextView.text = "" + tomatoVisibleList.size

        var new2 = tomatoList.get(tomatoVisibleList.size)
        tomatoVisibleList.add(new2)
        new2.visibility = View.VISIBLE
        getNumTextView.text = "" + tomatoVisibleList.size

        var new3 = tomatoList.get(tomatoVisibleList.size)
        tomatoVisibleList.add(new3)
        new3.visibility = View.VISIBLE
        getNumTextView.text = "" + tomatoVisibleList.size



        // get tomato socket
        // tomato 종류 구분 코드
        mSocket.on("getTomato", Emitter.Listener { args ->
            runOnUiThread {
                Log.d("getTomato", "" + args)
                var new = tomatoList.get(tomatoVisibleList.size)
                tomatoVisibleList.add(new)
                sortList.add(0)

                if (args.get(0) == 1) {
                    //new.setImageResource()
                    sortList.add(1)
                    getOldNumTextView.text = (getOldNumTextView.text.toString().toInt() + 1).toString();
                }

                new.visibility = View.VISIBLE
                getNumTextView.text = "" + tomatoVisibleList.size

                // 스무개 모으면 게임 끝
                if (getNumTextView.text.toString().toInt() == 15) {
                    Log.d("endGame", "endGame")
                    mSocket.emit("endGame", "endGame")
                }
            }
        })

        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball1)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball2)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball3)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball4)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball5)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball6)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball7)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball8)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball9)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball10)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball11)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball12)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball13)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball14)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball15)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball16)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball17)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball18)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball19)
        YoYo.with(Techniques.ZoomInUp).duration(800).playOn(ball20)

//        val render = Render(this)
//        render.setAnimation(Zoom().InUp(ball1))
//        render.start()

        // 길이를 세로 모드로 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d(TAG, "MainActivity - onAccuracyChanged() called")

    }

    override fun onSensorChanged(event: SensorEvent?) {
        //     Log.d(TAG, "MainActivity - onSensorChanged() called")

        x = event?.values?.get(0) as Float
        y = event?.values?.get(1) as Float
        z = event?.values?.get(2) as Float

        accelLast = accelCurrent

        accelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        delta = accelCurrent - accelLast

        accel = accel * 0.9f + delta

        if (accel > 8){
            Log.d(TAG, "MainActivity - 흔들었음")
            Log.d(TAG, "MainActivity - accel : ${accel}")

            if (tomatoVisibleList.size != 0) {
                var sort = sortList.get(tomatoVisibleList.size-1)
                var removed = tomatoVisibleList.get(tomatoVisibleList.size-1)
                tomatoVisibleList.removeAt(tomatoVisibleList.size-1)
                removed.visibility = View.GONE

                if (sort == 0) {
                    getNumTextView.text = "" + tomatoVisibleList.size
                } else {
                    getNumTextView.text = "" + tomatoVisibleList.size
                    getOldNumTextView.text = (getOldNumTextView.text.toString().toInt() - 1).toString()
                }
            }
        }

        val x_g = event.values[0]
        val z_g = event.values[2]

       moved = sqrt((x * x + z * z).toDouble()).toFloat()

        var count = 0;
        if (moved > 8 && num == 0) {
            num = 1
            Log.d(TAG, "기울었음")
            runBlocking {
                launch {
                    timer(period = 1000, initialDelay = 1000) {
                        count += 1
                        checkSensorChanged(event)
                        Log.d(TAG, "count" + count + " moved" + moved)

                        if (moved <= 8) {
                            cancel()
                            Log.d(TAG, "타이머 종료" + "moved " + moved + "count " + count )
                            num = 0
                        }

                        if (count == 3) {
                            cancel()
                            Log.d(TAG, "토마토 차감")

                            runOnUiThread {
                                if (tomatoVisibleList.size != 0) {
                                    var removed = tomatoVisibleList.get(tomatoVisibleList.size-1)
                                    tomatoVisibleList.removeAt(tomatoVisibleList.size-1)
                                    YoYo.with(Techniques.ZoomOutUp).duration(800).playOn(ball1) // 안 됨
                                    removed.visibility = View.GONE
                                    getNumTextView.text = "" + tomatoVisibleList.size
                                }
                            }
                    }
                }
            }
        }
    }
    }

    fun checkSensorChanged(event: SensorEvent?) {
        x = event?.values?.get(0) as Float
        y = event?.values?.get(1) as Float
        z = event?.values?.get(2) as Float

        accelLast = accelCurrent

        accelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        delta = accelCurrent - accelLast

        accel = accel * 0.9f + delta

        if (accel > 8){
            Log.d(TAG, "MainActivity - 흔들었음")
            Log.d(TAG, "MainActivity - accel : ${accel}")

            if (tomatoVisibleList.size != 0) {
                var sort = sortList.get(tomatoVisibleList.size - 1)
                var removed = tomatoVisibleList.get(tomatoVisibleList.size - 1)
                tomatoVisibleList.removeAt(tomatoVisibleList.size - 1)
                removed.visibility = View.GONE

                if (sort == 0) {
                    getNumTextView.text = "" + tomatoVisibleList.size + " / 20"
                } else {
                    getNumTextView.text = "" + tomatoVisibleList.size + " / 20"
                    getOldNumTextView.text =
                        (getOldNumTextView.text.toString().toInt() - 1).toString()
                }
            }
        }

        val x_g = event.values[0]
        val z_g = event.values[2]

        moved = sqrt((x * x + z * z).toDouble()).toFloat()
    }

    override fun onResume() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }
}