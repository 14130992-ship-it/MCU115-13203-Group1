package com.example.secactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.secactivity.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedText = intent.getStringExtra("EXTRA_SEND_TEXT") ?: ""
        binding.tvReceivedText.text = receivedText

        binding.btnReplyAndReturn.setOnClickListener {
            val replyText = binding.etReply.text.toString()
            val resultIntent = Intent().apply {
                putExtra("EXTRA_REPLY_TEXT", replyText)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
