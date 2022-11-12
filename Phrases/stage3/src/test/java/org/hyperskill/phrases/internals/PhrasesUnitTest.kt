package org.hyperskill.phrases.internals

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.hyperskill.phrases.TimePickerDialog
import org.junit.Assert.*
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlarmManager
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowNotificationManager
import java.util.concurrent.TimeUnit


open class PhrasesUnitTest<T : Activity>(clazz: Class<T>): AbstractUnitTest<T>(clazz) {

    companion object {
        const val CHANNEL_ID = "org.hyperskill.phrases"
        const val NOTIFICATION_ID = 393939
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
        shadowAlarmManager.scheduledAlarms.lastOrNull()?.also {
            if(it.operation != null) {
                val pendingIntent = Shadows.shadowOf(it.operation)
                if(it.triggerAtTime < SystemClock.currentGnssTimeClock().millis()) {
                    it.operation.intentSender.sendIntent(
                        pendingIntent.savedContext,
                        pendingIntent.requestCode,
                        pendingIntent.savedIntent,
                        null,
                        Handler(activity.mainLooper)
                    )
                    shadowLooper.idleFor(500, TimeUnit.MILLISECONDS)
                }
            } else if(it.onAlarmListener != null) {
                if(it.triggerAtTime < SystemClock.currentGnssTimeClock().millis()) {
                    it.onAlarmListener.onAlarm()
                }
            }
        }

    }
}
