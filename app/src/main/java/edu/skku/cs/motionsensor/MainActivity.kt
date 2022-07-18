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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_play_game.*
import render.animations.*
import java.net.URISyntaxException
import kotlin.math.sqrt

var pw = ""
class MainActivity : AppCompatActivity() {

    val TAG: String = "로그"
    companion object{
        var requestQueue: RequestQueue? = null
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()
        fun Int.pxToDp(displayMetrics: DisplayMetrics): Int = (this / displayMetrics.density).toInt()

        val button = findViewById<Button>(R.id.button)
        val editText = findViewById<EditText>(R.id.edit_text)

        val fastButton = findViewById<Button>(R.id.fastButton)
        fastButton.setOnClickListener {
            SocketHandler.setSocket()
            SocketHandler.establishConnection()

            val intent = Intent(this, Loading::class.java)
            startActivity(intent)
        }

//        requestQueue = Volley.newRequestQueue(applicationContext)
//        var text_from_server = ""

        button.setOnClickListener {
            Log.d("clicked", editText.text.toString())

            SocketHandler.setSocket()
            SocketHandler.establishConnection()

            var mSocket = SocketHandler.getSocket()

            mSocket.emit("passwordReq", editText.text.toString())
            mSocket.on("appConnected", Emitter.Listener { args ->
            runOnUiThread {
                Log.d("appConnected", "" + args.get(0))

                if(args.get(0) == 1) {
                    val intent = Intent(this, Loading::class.java)
                    startActivity(intent)
                } else {
                    val render = Render(this)
                    render.setAnimation(Attention().Shake(edit_text))
                    render.start()
                }
            }
        })

//            mSocket.emit("password", Emitter.Listener { args ->
//                runOnUiThread {
//                    Log.d("password", ""+ args)
//                    if (args.get(0) == editText.text.toString()) {
//                        Toast.makeText(this@MainActivity, "연결에 성공했습니다!", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, Loading::class.java)
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(this@MainActivity, "코드를 확인해주세요!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            })

//            val url = "http://192.249.18.153:80/login"
//            val request = object : StringRequest(
//                Request.Method.GET,
//                url, {
//                    text_from_server = it
//                    Log.d("text_from_server", text_from_server)
//
//                    if (text_from_server == editText.text.toString()) {
//                        Toast.makeText(this@MainActivity, "연결에 성공했습니다!", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, Loading::class.java)
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(this@MainActivity, "코드를 확인해주세요!", Toast.LENGTH_SHORT).show()
//                    }
//                }, {
//                    Log.d("error", "" + it)
//                }
//            ) {
//
//            }
//            request.setShouldCache(false)
//            requestQueue?.add(request)
        }
    }
}

object SocketHandler {
    lateinit var mainSocket: Socket

    fun setSocket() {
        try {
            mainSocket = IO.socket("http://192.249.18.153:443")
        } catch (e: URISyntaxException) {
        }
    }

        @Synchronized
        fun getSocket(): Socket {
            return mainSocket
        }

        @Synchronized
        fun establishConnection() {
            mainSocket.connect()
        }

        @Synchronized
        fun closeConnection() {
            mainSocket.disconnect()
        }
}