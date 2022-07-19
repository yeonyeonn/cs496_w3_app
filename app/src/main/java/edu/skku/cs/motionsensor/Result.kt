package edu.skku.cs.motionsensor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_result.*

class Result : AppCompatActivity() {

    lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        mSocket = SocketHandler.getSocket()

        var intent = intent

        var hour = intent.getStringExtra("hr")
        var min = intent.getStringExtra("min")
        var sec = intent.getStringExtra("sec")
        var good = intent.getIntExtra("good", 0)
        var bad = intent.getIntExtra("bad", 0)
        var total = good + bad

        Log.d("sortList good", ""+good)
        Log.d("sortList bad", ""+bad)

        var time = "0"+ hour + " : 0" + min + " : " + sec

        //timeTextView.text = time
        scoreTextView1.text = good.toString()
        scoreTextView2.text = bad.toString()
        totalTextView.text = total.toString()

        mSocket.on("restart", Emitter.Listener { args ->
            Log.d("restart", "restart")
            val intent = Intent(this, PlayGame::class.java)
            startActivity(intent)
        })
    }
}