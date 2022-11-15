package org.hyperskill.phrases

import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Build
import android.os.Bundle
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.hyperskill.phrases.data.PhrasesRepository
import org.hyperskill.phrases.data.room.entity.Phrase
import org.hyperskill.phrases.databinding.ActivityMainBinding
import org.hyperskill.phrases.ui.AddPhraseDialog
import org.hyperskill.phrases.ui.MainViewModel
import java.util.*


const val CHANNEL_ID = "org.hyperskill.phrases"
const val NOTIFICATION_ID = 393939
class MainActivity : AppCompatActivity(), OnTimeSetListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    private lateinit var repository: PhrasesRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = PhrasesRepository(application)
        viewModel = ViewModelProvider(this, MainViewHolderFactory(application, repository)).get(MainViewModel::class.java)
        viewModel.createNotificationChannel()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        //TODO: Check if alarm is already up and set the textView accordingly
        viewModel.phrasesList.observe(this) { phrases ->
            binding.recyclerView.adapter = PhrasesAdapter(phrases, viewModel)
        }

        binding.reminderTextView.setOnClickListener {
            val timePicker = TimePickerDialog()
            timePicker.show(supportFragmentManager, "timePicker")
        }

        binding.addButton.setOnClickListener {
            AddPhraseDialog().show(supportFragmentManager, "addPhraseDialog")
            supportFragmentManager.setFragmentResultListener(
                AddPhraseDialog.REQUEST_KEY, this
            ) { _, result ->
                val phrase = result.getString("phrase")
                if (phrase != null) {
                    viewModel.insert(Phrase(0, phrase))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        viewModel.scheduleNotification(hour, minute)
        binding.reminderTextView.text = "Reminder set for %02d:%02d".format(hour, minute)
    }
}