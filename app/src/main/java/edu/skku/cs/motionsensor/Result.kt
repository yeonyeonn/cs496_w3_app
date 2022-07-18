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

        hourTextView.text = intent.getStringExtra("hr")
        minTextView.text = intent.getStringExtra("min")
        secTextView.text = intent.getStringExtra("sec")

        mSocket.on("restart", Emitter.Listener { args ->
            Log.d("restart", "restart")
            val intent = Intent(this, PlayGame::class.java)
            startActivity(intent)
        })
    }
}