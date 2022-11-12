package org.hyperskill.phrases

import android.app.Notification.EXTRA_TEXT
import android.app.Notification.EXTRA_TITLE
import android.os.SystemClock
import org.hyperskill.phrases.internals.PhrasesUnitTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import java.util.*
import java.util.concurrent.TimeUnit

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
class Stage3UnitTest : PhrasesUnitTest<MainActivity>(MainActivity::class.java){


    @Before
    fun setUp() {
        SystemClock.setCurrentTimeMillis(System.currentTimeMillis())
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

    @Test
    fun test02_checkNotificationIsSent() {
        testActivity {
            val minutesToAdd = 10
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, minutesToAdd)
            val pickHour = calendar.get(Calendar.HOUR_OF_DAY)
            val pickMinute = calendar.get(Calendar.MINUTE)

            reminderTv.clickAndRun()
            val timePickerDialog = getLatestTimePickerDialog()

            timePickerDialog.pickTime(pickHour, pickMinute)
            shadowLooper.idleFor(minutesToAdd + 2L, TimeUnit.MINUTES) // trigger alarm

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