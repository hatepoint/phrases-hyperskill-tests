package org.hyperskill.phrases

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.hyperskill.phrases.databinding.ActivityMainBinding


const val CHANNEL_ID = "org.hyperskill.phrases"
const val NOTIFICATION_ID = 393939

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phrases = mutableListOf<Phrase>(Phrase(0, "Test #1"), Phrase(1, "Test #2"), Phrase(2, "Test #3"))
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = PhrasesAdapter(phrases)

    }
}