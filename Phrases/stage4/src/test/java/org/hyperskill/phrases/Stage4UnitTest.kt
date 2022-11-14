package org.hyperskill.phrases

import android.app.Notification
import android.os.SystemClock
import android.widget.TextView
import org.hyperskill.phrases.data.room.AppDatabase
import org.hyperskill.phrases.internals.PhrasesUnitTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowDialog
import java.util.*
import java.util.concurrent.TimeUnit

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
class Stage4UnitTest : PhrasesUnitTest<MainActivity>(MainActivity::class.java) {


    @Before
    fun setUp() {
        SystemClock.setCurrentTimeMillis(System.currentTimeMillis())
    }

    @Test
    fun test00_checkAppDatabase() {
        testActivity {
            val db = AppDatabase.getInstance(activity)
            assertNotNull("Are you sure your AppDatabase has a getInstance() method?", db)
        }
    }

    @Test
    fun test01_checkDAO() {
        testActivity {
            val dao = AppDatabase.getInstance(activity).getPhraseDao()
            assertNotNull("Are you sure your AppDatabase has a getPhraseDao() method?", dao)
        }

    }

    @Before
    fun setDB(){
        addToRoomDatabase(1)
    }

    @Test
    fun test02_checkRecyclerViewIsUsingDatabase() {
        testActivity {
            val dao = AppDatabase.getInstance(activity).getPhraseDao()
            val phrases = dao.getAll()
            recyclerView.assertSingleListItem(0) { itemViewSupplier ->
                val itemView = itemViewSupplier()
                val phraseTextView = itemView.findViewByString<TextView>("phraseTextView")
                val phrase = phrases[0]
                assertEquals(
                    "The phrase[0] from database doesn't seem to match the one in the RecyclerView.",
                    phrase.phrase,
                    phraseTextView.text.toString()
                )
            }
        }
    }

    @Test
    fun test03_checkAddDialog() {
        testActivity {
            floatingButton.clickAndRun()
            val dialog = ShadowDialog.getLatestDialog()
            val shadowDialog = shadowOf(dialog)
            assertNotNull("Are you sure you are showing a dialog when the floating button is clicked?", dialog)
            // TODO click on editText and type something

        }
    }

    @Test
    fun test04_checkPhrasesAreDeleted() {
        testActivity {
            val dao = AppDatabase.getInstance(activity).getPhraseDao()
            val phrases = dao.getAll()
            recyclerView.assertSingleListItem(0) { itemViewSupplier ->
                val itemView = itemViewSupplier()
                val deleteTextView = itemView.findViewByString<TextView>("deleteTextView")
                deleteTextView.clickAndRun()
            }
            assertEquals("The phrase was not deleted from the database.", phrases.size - 1, dao.getAll().size)
        }
    }

    @Test
    fun test05_checkNotificationContainsPhraseFromDb() {
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

            val messageContent = "The phrase in the notification doesn't match the one in the database."
            val actualContent = notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            assertEquals(messageContent, actualContent, AppDatabase.getInstance(activity).getPhraseDao().getAll()[0].phrase)

            notification.contentIntent
        }
    }

    @After
    fun cleanUp() {
        killRoomInstance()
    }

}