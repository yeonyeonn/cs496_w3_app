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
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
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

        val fastButton = findViewById<Button>(R.id.fastButton)
        fastButton.setOnClickListener {
            SocketHandler.setSocket()
            SocketHandler.establishConnection()

            val intent = Intent(this, Loading::class.java)
            startActivity(intent)
        }

        edit1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 1) {
                    edit2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        edit2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 1) {
                    edit3.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        edit3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 1) {
                    edit4.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        edit4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 1) {
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken,0)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })


        button.setOnClickListener {
            var pin = edit1.text.toString() + edit2.text.toString() + edit3.text.toString() + edit4.text.toString()
            Log.d("clicked", pin)

            SocketHandler.setSocket()
            SocketHandler.establishConnection()

            var mSocket = SocketHandler.getSocket()

            mSocket.emit("passwordReq", pin)
            mSocket.on("appConnected", Emitter.Listener { args ->
            runOnUiThread {
                Log.d("appConnected", "" + args.get(0))

                if(args.get(0).toString().toInt() == 1) {
                    Log.d("1", "1")
                    val intent = Intent(this, Loading::class.java)
                    startActivity(intent)
                } else {
                    Log.d("2", "2")
                    edit1.setText("")
                    edit2.setText("")
                    edit3.setText("")
                    edit4.setText("")

                    YoYo.with(Techniques.Shake).duration(1000).playOn(edit1)
                    YoYo.with(Techniques.Shake).duration(1000).playOn(edit2)
                    YoYo.with(Techniques.Shake).duration(1000).playOn(edit3)
                    YoYo.with(Techniques.Shake).duration(1000).playOn(edit4)

                    //YoYo.with(Techniques.Shake).duration(1000).playOn(edit_text)
                }
            }
        })
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