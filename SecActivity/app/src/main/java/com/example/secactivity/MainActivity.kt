package com.example.secactivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.secactivity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val secondActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val replyText = result.data?.getStringExtra("EXTRA_REPLY_TEXT")
            binding.tvResultText.text = replyText ?: ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSwitchPage.setOnClickListener {
            val sendText = binding.etSend.text.toString()
            val intent = Intent(this, SecondActivity::class.java).apply {
                putExtra("EXTRA_SEND_TEXT", sendText)
            }
            secondActivityLauncher.launch(intent)
        }
    }
}
