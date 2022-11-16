package org.hyperskill.phrases.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.hyperskill.phrases.CHANNEL_ID
import org.hyperskill.phrases.NOTIFICATION_ID
import org.hyperskill.phrases.Notification
import org.hyperskill.phrases.R
import org.hyperskill.phrases.data.PhrasesRepository
import org.hyperskill.phrases.data.room.entity.Phrase
import java.util.*

class MainViewModel(val application: Application, val repository: PhrasesRepository) : ViewModel() {

    private lateinit var alarmManager: AlarmManager
    var phrasesList: MutableLiveData<List<Phrase>> = MutableLiveData()
    private var notificationManager = application.getSystemService<NotificationManager>()

    init {
        phrasesList.postValue(repository.getAll())
    }

    fun insert(phrase: Phrase) {
        repository.insert(phrase)
        phrasesList.postValue(repository.getAll())
    }

    fun delete(phrase: Phrase) {
        repository.delete(phrase)
        phrasesList.postValue(repository.getAll())
    }

    //this exists solely for stage 2 or whatever it's gonna be
    @RequiresApi(Build.VERSION_CODES.O)
    private fun postNotification(text: String) {
        val builder = android.app.Notification.Builder(application, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Phrases")
            .setContentText(text)
            .setStyle(android.app.Notification.BigTextStyle())
            .setAutoCancel(true)

        notificationManager?.notify(1, builder.build())
    }

    fun createNotificationChannel() {
        val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Phrases"
            val descriptionText = "Phrases to boost your mood"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply { description = descriptionText }

            notificationManager.createNotificationChannel(channel)
            Log.d("Channel", "Channel created")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun scheduleNotification(hour: Int, minute: Int) : Boolean {
        val calendar = Calendar.getInstance()
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, minute, 0)
        val intent = Intent(application, Notification::class.java)
        val title = "Your phrase of the day"
        if (phrasesList.value?.isNotEmpty() == true) {
            val message = phrasesList.value?.random()?.phrase
            intent.putExtra("titleExtra", title)
            intent.putExtra("messageExtra", message)


            val pendingIntent = PendingIntent.getBroadcast(application.applicationContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager = application.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 86400 * 1000, pendingIntent)
            Log.d("Notification", "Notification scheduled at $hour:$minute")
            return true
        } else {
            Toast.makeText(application, "You have no phrases to schedule", Toast.LENGTH_SHORT).show()
            return false
        }
    }

}