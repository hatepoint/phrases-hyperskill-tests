package org.hyperskill.phrases

import android.app.Notification.EXTRA_TEXT
import android.app.Notification.EXTRA_TITLE
import android.app.NotificationManager
import org.hyperskill.phrases.internals.PhrasesUnitTest
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.*
import java.time.Duration
import java.util.*

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
class Stage3UnitTest : PhrasesUnitTest<MainActivity>(MainActivity::class.java){

    init {
        notificationManager.setNotificationsEnabled(true)
        notificationManager.setNotificationPolicyAccessGranted(true)
    }

    @Test
    fun test00_checkNotificationChannelExists() {
        testActivity {
            notificationChannel
        }
    }

    @Test
    fun test01_checkTimePickerDialog() {
        testActivity {
            reminderTv.clickAndRun()
            val timePickerDialog = getLatestTimePickerDialog()
            timePickerDialog.pickTime(10, 15)

            val expectedTimeText = "Reminder set for 10:15"
            val actualTimeText = reminderTv.text.toString()

            assertEquals("The reminderTextView has a wrong text", expectedTimeText, actualTimeText)
        }
    }
    //TODO Fix this test for working with Broadcast Receiver
    @Test
    fun test02_checkNotificationIsSent() {
        testActivity {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            reminderTv.clickAndRun()
            val timePickerDialog = getLatestTimePickerDialog()
            timePickerDialog.pickTime(currentHour, currentMinute - 1)

            supportForAlarmManager()

            val notification: android.app.Notification? = notificationManager.getNotification(NOTIFICATION_ID)

            val messageNotificationId =
                "Could not find notification with id 393939. Did you set the proper id?"
            assertNotNull(messageNotificationId, notification)
            notification!!

            val messageChannelId = "The notification channel id does not equals \"$CHANNEL_ID\""
            val actualChannelId = notification.channelId
            assertEquals(messageChannelId, CHANNEL_ID, actualChannelId)

            val messageTitle = "Have you set correct notification title?"
            val expectedTitle = "Phrases"
            val actualTitle = notification.extras.getCharSequence(EXTRA_TITLE)?.toString()
            assertEquals(messageTitle, expectedTitle, actualTitle)

            val messageContent = "Have you set the notification content?"
            val actualContent = notification.extras.getCharSequence(EXTRA_TEXT)?.toString()
            assertNotNull(messageContent, actualContent)

            notification.contentIntent
        }
    }



}