package org.hyperskill.phrases

import android.app.Notification
import android.os.SystemClock
import android.widget.EditText
import android.widget.TextView
import org.hyperskill.phrases.internals.PhrasesUnitTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowToast
import java.util.*
import java.util.concurrent.TimeUnit

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
class Stage4UnitTest : PhrasesUnitTest<MainActivity>(MainActivity::class.java) {


    @Before
    fun setUp() {
        SystemClock.setCurrentTimeMillis(System.currentTimeMillis())
    }

    @After
    fun tearDown() {
        closeRoom()
    }

    @Test
    fun test00_checkRecyclerViewIsUsingDatabase() {

        addToDatabase(fakePhrases)

        testActivity {
            recyclerView.assertListItems(fakePhrases) { itemViewSupplier, index, phrase ->
                val itemView = itemViewSupplier()
                val phraseTextView = itemView.findViewByString<TextView>("phraseTextView")
                val actualPhrase = phraseTextView.text.toString()

                assertEquals(
                    "The recyclerView is not matching database content",
                    phrase,
                    actualPhrase
                )
            }
        }
    }

    @Test
    fun test01_checkRecyclerViewIsUsingDatabase2() {

        val phrases = fakePhrases + "one more test phrase"
        addToDatabase(phrases)

        testActivity {
            recyclerView.assertListItems(phrases) { itemViewSupplier, index, phrase ->
                val itemView = itemViewSupplier()
                val phraseTextView = itemView.findViewByString<TextView>("phraseTextView")
                val actualPhrase = phraseTextView.text.toString()

                assertEquals(
                    "The recyclerView is not matching database content",
                    phrase,
                    actualPhrase
                )
            }
        }
    }

    @Test
    fun test02_checkAddDialog() {
        val phrases = listOf("A text for test")

        testActivity {
            floatingButton.clickAndRun()
            val dialog = ShadowDialog.getLatestDialog()
            val shadowDialog = shadowOf(dialog)
            assertNotNull("Are you sure you are showing a dialog when the floating button is clicked?", dialog)

            val editText = dialog.findViewByString<EditText>("editText")

            editText.setText(phrases[0])
            shadowDialog.clickOn(android.R.id.button1) // ok button
            shadowLooper.idleFor(500, TimeUnit.MILLISECONDS)

            val phrasesOnDatabase = readAllFromDatabase()

            assertEquals("Database content should contain added phrase", phrases, phrasesOnDatabase)

            recyclerView.assertListItems(phrases) { itemViewSupplier, index, phrase ->
                val itemView = itemViewSupplier()
                val phraseTextView = itemView.findViewByString<TextView>("phraseTextView")
                val actualPhrase = phraseTextView.text.toString()

                assertEquals(
                    "The recyclerView is not matching database content",
                    phrase,
                    actualPhrase
                )
            }
        }
    }



    @Test
    fun test03_checkPhrasesAreDeleted() {

        addToDatabase(fakePhrases)

        testActivity {
            recyclerView.assertSingleListItem(0) { itemViewSupplier ->
                val itemView = itemViewSupplier()
                val deleteTextView = itemView.findViewByString<TextView>("deleteTextView")
                deleteTextView.clickAndRun()
            }

            val phrasesOnDb = readAllFromDatabase()
            val expectedSizeAfterDelete = fakePhrases.size - 1
            val actualSizeAfterDelete = phrasesOnDb.size

            assertEquals(
                "The number of phrases on database should decrease after deleting phrase",
                expectedSizeAfterDelete,
                actualSizeAfterDelete
            )

            recyclerView.assertListItems(fakePhrases.slice(1..fakePhrases.lastIndex)) { itemViewSupplier, index, phrase ->
                val itemView = itemViewSupplier()
                val phraseTextView = itemView.findViewByString<TextView>("phraseTextView")
                val actualPhrase = phraseTextView.text.toString()

                assertEquals(
                    "The recyclerView is not matching database content",
                    phrase,
                    actualPhrase
                )
            }
        }
    }

    @Test
    fun test04_checkNotificationContainsPhraseFromDb() {

        addToDatabase(fakePhrases)

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

            val messageContent = "The phrase in the notification doesn't any one in the database."
            val actualContent = notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            assertTrue(messageContent, actualContent in fakePhrases)
        }
    }

    @Test
    fun test05_checkTimeFormatting() {
        val phrases = listOf("database should not be empty to set reminder")
        addToDatabase(phrases)

        testActivity {
            val testCases = listOf(
                0 to 0,
                2 to 22,
                12 to 6,
                23 to 59
            )

            val expectedTime = listOf(
                "00:00",
                "02:22",
                "12:06",
                "23:59"
            )

            testCases.forEachIndexed { i, (pickHour, pickMinute)  ->
                reminderTv.clickAndRun()
                val timePickerDialog = getLatestTimePickerDialog()

                timePickerDialog.pickTime(pickHour, pickMinute)
                val timeText = expectedTime[i]

                val expectedText = "Reminder set for $timeText"
                val actualText = reminderTv.text.toString()
                assertEquals("Time is not formatted correctly", expectedText, actualText)
            }
        }
    }

    @Test
    fun test06_checkRemindersNotAllowedWithEmptyDatabase() {


        testActivity {

            val minutesToAdd = 10
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, minutesToAdd)
            val pickHour = 9
            val pickMinute = 5

            reminderTv.clickAndRun()
            val timePickerDialog = getLatestTimePickerDialog()

            timePickerDialog.pickTime(pickHour, pickMinute)

            val toast = ShadowToast.getLatestToast()
            assertNotNull("Toast is not shown after trying to set reminder with empty database", toast)

            val expectedText = "No reminder set"
            val actualText = reminderTv.text.toString()
            assertEquals("Seems like reminder is still set with empty database", expectedText, actualText)

            shadowLooper.idleFor(minutesToAdd + 2L, TimeUnit.MINUTES) // trigger alarm
            supportForAlarmManager()

            val notification: android.app.Notification? = notificationManager.getNotification(NOTIFICATION_ID)
            val messageNotificationId =
                "No notification should be sent with empty database"
            assertNull(messageNotificationId, notification)
        }
    }

    @Test
    fun test07_checkNotificationSentOnNextDay() {
        addToDatabase(fakePhrases)

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

            val messageContent = "The phrase in the notification doesn't any one in the database."
            val actualContent = notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            assertTrue(messageContent, actualContent in fakePhrases)

            shadowLooper.idleFor(1 , TimeUnit.DAYS)
            shadowLooper.idleFor(10, TimeUnit.MINUTES)  // trigger alarm on next day
            supportForAlarmManager()

            val notification2: android.app.Notification? = notificationManager.getNotification(NOTIFICATION_ID)

            val messageNotificationId2 =
                "Could not find notification with id 393939. Did you set the proper id?"
            assertNotNull(messageNotificationId2, notification2)
            notification2!!

            val messageSameNotificationError =
                "A new notification should be triggered on the next day"
            assertFalse(messageSameNotificationError, notification === notification2)

            val messageContent2 = "The phrase in the notification doesn't match any one in the database."
            val actualContent2 = notification2.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            assertTrue(messageContent2, actualContent2 in fakePhrases)
        }
    }
}