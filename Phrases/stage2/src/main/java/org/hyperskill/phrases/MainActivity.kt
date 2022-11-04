package org.hyperskill.phrases

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperskill.phrases.databinding.ActivityMainBinding


const val CHANNEL_ID = "org.hyperskill.phrases"
const val NOTIFICATION_ID = 393939

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}