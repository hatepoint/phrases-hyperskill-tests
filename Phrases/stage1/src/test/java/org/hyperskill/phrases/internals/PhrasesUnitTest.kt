package org.hyperskill.phrases.internals

import android.app.*
import android.app.AlarmManager.OnAlarmListener
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.os.Handler
import android.os.SystemClock
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import androidx.room.RoomDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.junit.Assert.*
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlarmManager
import org.robolectric.shadows.ShadowAlarmManager.ScheduledAlarm
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowNotificationManager
import java.util.concurrent.TimeUnit


open class PhrasesUnitTest<T : Activity>(clazz: Class<T>): AbstractUnitTest<T>(clazz) {

    companion object {
        const val CHANNEL_ID = "org.hyperskill.phrases"
        const val NOTIFICATION_ID = 393939
        val fakePhrases = listOf("This is a test phrase", "This is another test phrase", "Yet another test phrase")
    }

    protected val reminderTv: TextView by lazy {
        val view = activity.findViewByString<TextView>("reminderTextView")
        val messageInitialText = "The reminderTextView has a wrong initial text"
        val expectedInitialText = "No reminder set"
        val actualInitialText = view.text.toString()
        assertEquals(messageInitialText, expectedInitialText, actualInitialText)

        view
    }

    protected val recyclerView : RecyclerView by lazy {
        activity.findViewByString("recyclerView")
    }

    protected val floatingButton: FloatingActionButton by lazy {
        activity.findViewByString("addButton")
    }

    protected val notificationManager: ShadowNotificationManager by lazy {
        Shadows.shadowOf(
            activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        )
    }

    protected fun RecyclerView.assertItemViewsExistOnItemWithIndex(index: Int = 0) {
        this.assertSingleListItem(index) { itemViewSupplier ->
            val itemView = itemViewSupplier()
            itemView.findViewByString<TextView>("phraseTextView")
            itemView.findViewByString<TextView>("phraseTextView")
        }
    }

    protected fun RecyclerView.assertAmountItems(expectedAmount: Int) {
        val actualInitialItems = this.adapter?.itemCount
            ?: throw AssertionError("Could not find any RecyclerView.Adapter on recyclerView")
        val messageInitialText = "The recyclerView doesn't have 3 or more items. Found $actualInitialItems items."
        assertTrue(messageInitialText, (actualInitialItems >= expectedAmount))
    }

    protected fun RecyclerView.deleteLastItemAndAssertSizeDecreased() {
        val adapter = this.adapter ?: throw AssertionError("Could not find any RecyclerView.Adapter on recyclerView")
        val beforeDeleteSize = adapter.itemCount
        val lastIndex = beforeDeleteSize - 1

        assertSingleListItem(lastIndex) { itemViewSupplier ->
            val itemView = itemViewSupplier()
            itemView.findViewByString<TextView>("deleteTextView").clickAndRun()
        }

        val expectedSizeAfterDelete = beforeDeleteSize - 1
        val actualSizeAfterDelete = adapter.itemCount

        assertEquals(
            "The recyclerView didn't remove item after clicking 'Delete'.",
            expectedSizeAfterDelete,
            actualSizeAfterDelete
        )
    }

    protected val notificationChannel: NotificationChannel by lazy {
        val notificationChannel =
            notificationManager.notificationChannels.mapNotNull {
                it as NotificationChannel?
            }.firstOrNull {
                it.id == CHANNEL_ID
            }

        assertNotNull("Couldn't find notification channel with ID \"$CHANNEL_ID\"", notificationChannel)
        notificationChannel!!
    }

    protected fun getLatestTimePickerDialog(notFoundMessage: String = "No TimePickerDialog was found"): android.app.TimePickerDialog {
        return ShadowDialog.getShownDialogs().mapNotNull {
            if(it is android.app.TimePickerDialog) it else null
        }.lastOrNull() ?: throw AssertionError("No TimePickerDialog found")
    }

    protected fun android.app.TimePickerDialog.pickTime(hourOfDay: Int, minuteOfHour: Int, advanceClockMillis: Long = 500) {
        val shadowTimePickerDialog = shadowOf(this)

        this.updateTime(hourOfDay, minuteOfHour)
        shadowTimePickerDialog.clickOn(android.R.id.button1) // ok button
        shadowLooper.idleFor(advanceClockMillis, TimeUnit.MILLISECONDS)
    }

    fun supportForAlarmManager() {
        val alarmManager = activity.getSystemService<AlarmManager>()
        val shadowAlarmManager: ShadowAlarmManager = shadowOf(alarmManager)
        val toTrigger = shadowAlarmManager.scheduledAlarms.filter {
            it.triggerAtTime < SystemClock.currentGnssTimeClock().millis()
        }
        toTrigger.forEach { alarm ->
            // trigger alarm
            if(alarm.operation != null) {
                val pendingIntent = shadowOf(alarm.operation)
                if(alarm.triggerAtTime < SystemClock.currentGnssTimeClock().millis()) {
                    alarm.operation.intentSender.sendIntent(
                        pendingIntent.savedContext,
                        pendingIntent.requestCode,
                        pendingIntent.savedIntent,
                        null,
                        Handler(activity.mainLooper)
                    )
                    shadowLooper.idleFor(500, TimeUnit.MILLISECONDS)
                }
            } else if(alarm.onAlarmListener != null) {
                if(alarm.triggerAtTime < SystemClock.currentGnssTimeClock().millis()) {
                    alarm.onAlarmListener.onAlarm()
                }
            }

            shadowAlarmManager.scheduledAlarms.remove(alarm) // remove triggered
            if(alarm.interval > 0) {
                // if repeating schedule next
                val nextAlarm = alarm.copy(triggerAtTime = alarm.triggerAtTime + alarm.interval)
                shadowAlarmManager.scheduledAlarms.add(nextAlarm)
            }
        }
    }

    private fun ScheduledAlarm.copy(
        type: Int = this.type,
        triggerAtTime: Long = this.triggerAtTime,
        interval: Long = this.interval,
        operation: PendingIntent? = this.operation,
        showIntent: PendingIntent? = this.showIntent,
        onAlarmListener: OnAlarmListener? = this.onAlarmListener,
        handler: Handler? = this.handler
    ): ScheduledAlarm {
        val alarmConstructor = ScheduledAlarm::class.java.getDeclaredConstructor(
            Int::class.java,
            Long::class.java,
            Long::class.java,
            PendingIntent::class.java,
            PendingIntent::class.java,
            OnAlarmListener::class.java,
            Handler::class.java
        )
        alarmConstructor.isAccessible = true
        return alarmConstructor.newInstance(
            type,
            triggerAtTime,
            interval,
            operation,
            showIntent,
            onAlarmListener,
            handler
        )
    }

    protected fun addToDatabase(phrases: List<String>) {

        TestDatabaseFactory().writableDatabase.use { database ->
            database.execSQL("CREATE TABLE IF NOT EXISTS phrases (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, phrase TEXT NOT NULL)")
            database.beginTransaction()
            try {
                phrases.forEach {
                    ContentValues().apply {
                        put("phrase", it)
                        database.insert("phrases", null, this)
                    }
                }
                database.setTransactionSuccessful()
            } catch (ex: SQLiteException) {
                ex.printStackTrace()
                fail(ex.stackTraceToString())
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
                fail(ex.stackTraceToString())
            } finally {
                database.endTransaction()
            }
        }
    }

    protected fun readAllFromDatabase(): List<String> {

        val phrasesFromDb = mutableListOf<String>()

        TestDatabaseFactory().readableDatabase.use { database ->
            database.query("phrases", null,
                null, null, null, null, null).use { cursor ->

                val phraseColumnIndex = cursor.getColumnIndex("phrase")
                assertTrue("phrase column was not found", phraseColumnIndex >= 0)

                while(cursor.moveToNext()) {
                    val phrase = cursor.getString(phraseColumnIndex)
                    phrasesFromDb.add(phrase)
                }
            }
        }

        return phrasesFromDb
    }

    fun closeRoom() {
        val clazzName = "org.hyperskill.phrases.data.room.AppDatabase"
        val clazz: Class<*> = try {
            Class.forName(clazzName)
        } catch (e: ClassNotFoundException ) {
            throw AssertionError("Could not find on solution a class $clazzName")
        }

        val instanceField = try {
            clazz.getDeclaredField("INSTANCE")
        } catch (e: NoSuchFieldException) {
            throw AssertionError("Could not find on $clazzName a field named INSTANCE")
        }

        instanceField.isAccessible = true
        val instanceAsAny: Any = instanceField.get(clazz) ?: return

        assertTrue("AppDatabase.INSTANCE should be a RoomDatabase", instanceAsAny is RoomDatabase)
        val roomInstance = instanceAsAny as RoomDatabase
        roomInstance.close()
        instanceField.set(null, null)
    }
}
