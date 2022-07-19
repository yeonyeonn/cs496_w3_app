package edu.skku.cs.motionsensor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import edu.skku.cs.motionsensor.databinding.ActivityLoadingBinding
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_loading.*
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

class Loading : AppCompatActivity(), SensorEventListener {

    // 각 액티비티 이름을 딴 binding이 있다
    private lateinit var binding: ActivityLoadingBinding
    private lateinit var mSocket: Socket

    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null

    private val fallingModels: MutableList<TomatoModel> = mutableListOf()
    private val randomImageRes = listOf(
        R.drawable.tomatoyellow,
        R.drawable.tomatored,
        R.drawable.tomatodead
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSensorManager()
        initTouchListener()

        var loadingTextView = findViewById<TextView>(R.id.loadingTextView)
        YoYo.with(Techniques.BounceInUp).duration(3000).repeat(50).playOn(loadingTextView)

//        addTomatoView(890.15625f, 252.60938f)
//        addTomatoView(188.26172f, 514.23047f)
//        addTomatoView(926.80664f, 1146.5742f)
//        addTomatoView(159.25781f, 1565.168f)
//        addTomatoView(868.53516f, 1889.1328f)

        //소켓 접속
        try {
            mSocket = SocketHandler.getSocket()
        } catch (e: Exception) {
            SocketHandler.setSocket()
            SocketHandler.establishConnection()
            mSocket = SocketHandler.getSocket()
        }

        //mSocket.connect()

        // 서버가 앱에게 시작 사인 주는 걸 받는 코드, 사인 받으면 게임 진행 액티비티로 전환
        mSocket.on("startGame", Emitter.Listener { args ->
            runOnUiThread {
                Log.d("start", "start")
                val intent = Intent(this, PlayGame::class.java)
                startActivity(intent)
            }
        })

   var clickButton = findViewById<Button>(R.id.clickButton)
        clickButton.setOnClickListener {
            val intent = Intent(this, PlayGame::class.java)
            startActivity(intent)
      }

        mSocket.emit("message", "hello")
        Log.d("here", "here")

        mSocket.on("message", Emitter.Listener { args ->
            runOnUiThread {
                Log.d("data", ""+ args[0])
                val data = args[0]
                Log.d("data", "data")
            }
        })
    }

    private fun initSensorManager() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initTouchListener() {

        binding.root.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    val touchedX = event.x
                    val touchedY = event.y
                    addTomatoView(touchedX, touchedY)
                    true
                }
                else -> true
            }
            fallingModels.add(TomatoModel(toma1))
            fallingModels.add(TomatoModel(toma2))
            fallingModels.add(TomatoModel(toma3))
            fallingModels.add(TomatoModel(toma4))
            fallingModels.add(TomatoModel(toma5))

        }
    }

    private fun addTomatoView(touchedX: Float, touchedY: Float) {
        val tomato = ImageView(this).apply {
            Log.d("add", "add")
            setBackgroundResource(randomImageRes.random())
            layoutParams = LinearLayout.LayoutParams(TOMATO_SIZE, TOMATO_SIZE)
            /**
             * 좌표는 뷰의 왼쪽 상단이 기준점
             */
            x = touchedX - TOMATO_SIZE / 2
            y = touchedY - TOMATO_SIZE / 2
        }
        binding.root.addView(tomato)
        fallingModels.add(TomatoModel(tomato))
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == accelerometerSensor) {
            fallingModels.map {
                val exX = event!!.values[0] * it.speed
                val exY = event.values[1] * it.speed

                /**
                 * 오른쪽은 -, 왼쪽은 + 좌표
                 * 위는 -, 아래는 + 좌표
                 */
                with(it.tomato) {
                    x -= exX
                    y += exY

                    if (y > getRealRootViewHeight()) y = getRealRootViewHeight().toFloat()
                    if (y < 0) y = 0f

                    if (x > getRealRootViewWidth()) x = getRealRootViewWidth().toFloat()
                    if (x < 0) x = 0f
                }
            }
        }
    }

    private fun getRealRootViewWidth(): Int {
        return window.decorView.width - TOMATO_SIZE
    }

    private fun getRealRootViewHeight(): Int {
        return if (Build.VERSION.SDK_INT < 30) {
            window.decorView.height - TOMATO_SIZE - window.decorView.rootWindowInsets.run {
                systemWindowInsetTop + systemWindowInsetBottom
            }
        } else {
            val insets = window.decorView.rootWindowInsets.displayCutout?.run {
                safeInsetBottom + safeInsetTop
            } ?: 0
            window.decorView.height - TOMATO_SIZE - insets
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    companion object {
        private const val TOMATO_SIZE = 140
    }

}

data class TomatoModel(
    val tomato: ImageView,
    val speed: Float = Random.nextInt(2, 10).toFloat(),
)