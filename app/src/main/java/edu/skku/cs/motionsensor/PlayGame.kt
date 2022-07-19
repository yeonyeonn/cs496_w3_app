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
import kotlinx.android.synthetic.main.activity_play_game.totalTextView
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.lang.Exception
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
    var tomatoVisibleList = ArrayList<ImageView>()
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
        getNumTextView.text = Integer.valueOf(0).toString()

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
            findViewById<ImageView>(R.id.ball20))

        tomatoVisibleList = arrayListOf<ImageView>()
        sortList = ArrayList<Int>()

        fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()
        fun Int.pxToDp(displayMetrics: DisplayMetrics): Int = (this / displayMetrics.density).toInt()

        mSocket = SocketHandler.getSocket()

        mSocket.on("endGame", Emitter.Listener { args ->
            Log.d("endGame", "응답")

            Log.d("endGame", "" + args)
        })

        mSocket.on("timeBroadcast", Emitter.Listener { args ->
            Log.d("endGame", "timeBroadcast")

            val data = args[0] as JSONObject
            val intent = Intent(this, Result::class.java)
            intent.putExtra("hr", data.getString("hr"))
            intent.putExtra("min", data.getString("min"))
            intent.putExtra("sec", data.getString("sec"))

            var good: Int = 0
            var bad: Int = 0

            for (i in sortList) {
                Log.d("sortList", ""+i)
                if (i == 0) {
                    good += 1
                } else {
                    bad += 1
                }
            }

            intent.putExtra("good", good)
            intent.putExtra("bad", bad)
            startActivity(intent)
        })

            // tomato 종류 구분 코드
        mSocket.on("getTomato", Emitter.Listener { args ->
            Log.d("endGame", "getTomato")

            runOnUiThread {
                Log.d("getTomato", "" + args)

                lateinit var new : ImageView
                try {
                    new = tomatoList.get(tomatoVisibleList.size)
                    tomatoVisibleList.add(new)
                } catch (e: Exception) {

                }
                //sortList.add(0)

                if (args.get(0) == 1) {
                    //new.setImageResource()
                    sortList.add(1)
                    getOldTextView.text = (getOldTextView.text.toString().toInt() + 1).toString();
                } else {
                    sortList.add(0)
                }

                new?.visibility = View.VISIBLE
                getNumTextView.text = "" + (tomatoVisibleList.size - getOldTextView.text.toString().toInt())
                totalTextView.text = "" + tomatoVisibleList.size
                progressBar.incrementProgressBy(5)

                // 스무개 모으면 게임 끝
                if (sortList.size == 20) {
                    Log.d("endGame", "endGame")
                    Log.d("endGame", "20개")

                    mSocket.emit("endGame", "endGame")
                    // 종료 액티비티로 전환
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
        Log.d("endGame", "onAccuracyChanged")

        Log.d(TAG, "MainActivity - onAccuracyChanged() called")

    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("endGame", "onSensorChanged")

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
                sortList.removeAt(tomatoVisibleList.size-1)
                tomatoVisibleList.removeAt(tomatoVisibleList.size-1)
                YoYo.with(Techniques.ZoomOutUp).duration(800).playOn(ball1)
                removed.visibility = View.GONE

                Log.d("endGame", tomatoVisibleList.size.toString())
                if (sort == 0) {
                    getNumTextView.text = (getNumTextView.text.toString().toInt() - 1).toString()
                } else {
                    getOldTextView.text = (getOldTextView.text.toString().toInt() - 1).toString()
                }

                totalTextView.text = "" + tomatoVisibleList.size
                progressBar.incrementProgressBy(-5)
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
                                Log.d("endGame", "timer")

                                if (tomatoVisibleList.size != 0) {
                                    var sort = sortList.get(tomatoVisibleList.size-1)
                                    var removed = tomatoVisibleList.get(tomatoVisibleList.size-1)
                                    tomatoVisibleList.removeAt(tomatoVisibleList.size-1)
                                    sortList.removeAt(tomatoVisibleList.size-1)
                                    YoYo.with(Techniques.ZoomOutUp).duration(800).playOn(ball1) // 안 됨
                                    removed.visibility = View.GONE

                                    if (sort == 0) {
                                        getNumTextView.text = (getNumTextView.text.toString().toInt() - 1).toString()
                                    } else {
                                        getOldTextView.text = (getOldTextView.text.toString().toInt() - 1).toString()
                                    }

                                    totalTextView.text = "" + tomatoVisibleList.size
                                    progressBar.incrementProgressBy(-5)
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

            Log.d("endGame", "checkSensorChanged")


            if (tomatoVisibleList.size != 0) {
                var sort = sortList.get(tomatoVisibleList.size - 1)
                var removed = tomatoVisibleList.get(tomatoVisibleList.size - 1)
                sortList.removeAt(tomatoVisibleList.size-1)
                tomatoVisibleList.removeAt(tomatoVisibleList.size - 1)
                YoYo.with(Techniques.ZoomOutUp).duration(800).playOn(ball1)
                removed.visibility = View.GONE

                if (sort == 0) {
                    getNumTextView.text = (getNumTextView.text.toString().toInt() - 1).toString()
                } else {
                    getOldTextView.text = (getOldTextView.text.toString().toInt() - 1).toString()
                }

                totalTextView.text = "" + tomatoVisibleList.size
                progressBar.incrementProgressBy(-5)
            }
        }

        val x_g = event.values[0]
        val z_g = event.values[2]

        moved = sqrt((x * x + z * z).toDouble()).toFloat()
    }

    override fun onResume() {
        Log.d("endGame", "onResume")

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }

    override fun onPause() {
        Log.d("endGame", "onPause")

        sensorManager.unregisterListener(this)
        super.onPause()
    }
}