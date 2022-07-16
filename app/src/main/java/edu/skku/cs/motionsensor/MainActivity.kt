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
import kotlinx.android.synthetic.main.activity_main.*
import render.animations.*
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

        requestQueue = Volley.newRequestQueue(applicationContext)
        var text_from_server = ""

        button.setOnClickListener {
            Log.d("clicked", editText.text.toString())
            val url = "http://192.249.18.153:80/login"
            val request = object : StringRequest(
                Request.Method.GET,
                url, {
                    text_from_server = it
                    Log.d("text_from_server", text_from_server)

                    if (text_from_server == editText.text.toString()) {
                        Toast.makeText(this@MainActivity, "연결에 성공했습니다!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Loading::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity, "코드를 확인해주세요!", Toast.LENGTH_SHORT).show()
                    }
                }, {
                    Log.d("error", "" + it)
                }
            ) {

            }
            request.setShouldCache(false)
            requestQueue?.add(request)
        }
    }
}