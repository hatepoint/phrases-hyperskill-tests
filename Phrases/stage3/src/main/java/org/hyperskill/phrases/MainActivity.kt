package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.hyperskill.phrases.databinding.ActivityMainBinding
import java.util.*


const val CHANNEL_ID = "org.hyperskill.phrases"
const val NOTIFICATION_ID = 393939
class MainActivity : AppCompatActivity(), OnTimeSetListener {
    lateinit var binding: ActivityMainBinding
    lateinit var phrases: MutableList<Phrase>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phrases = mutableListOf(Phrase(0, "Test #1"), Phrase(1, "Test #2"), Phrase(2, "Test #3"))
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = PhrasesAdapter(phrases)
        createNotificationChannel()

        binding.reminderTextView.setOnClickListener {
            val timePicker = TimePickerDialog()
            timePicker.show(supportFragmentManager, "timePicker")
        }
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Phrases"
            val descriptionText = "Phrases to boost your mood"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = android.app.NotificationChannel(CHANNEL_ID, name, importance).apply { description = descriptionText }

            notificationManager.createNotificationChannel(channel)
        }
    }

    /*@RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
            Calendar.DAY_OF_MONTH), hour, minute, 0)
        val intent = Intent(application, Notification::class.java)
        val title = "Your phrase of the day"
        val message = phrases.random().toString()
        intent.putExtra("titleExtra", title)
        intent.putExtra("messageExtra", message)

        val pendingIntent = PendingIntent.getBroadcast(application.applicationContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = application.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNotification(context: Context, hour: Int, minute: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val builder = android.app.Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Phrases")
            .setContentText(phrases.random().toString())
            .setStyle(android.app.Notification.BigTextStyle())
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        scheduleNotification(applicationContext, hour, minute)
        binding.reminderTextView.text = "Reminder set for $hour:$minute"
    }
}