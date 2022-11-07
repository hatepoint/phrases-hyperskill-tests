package org.hyperskill.phrases.internals

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.junit.Assert.*
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowNotificationManager


open class PhrasesUnitTest<T : Activity>(clazz: Class<T>): AbstractUnitTest<T>(clazz) {

    companion object {
        const val CHANNEL_ID = "org.hyperskill.phrases"  // change as you like it
        const val NOTIFICATION_ID = 393939               // change as you like it
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

    protected fun recyclerViewItems(index: Int = 0) {
        recyclerView.assertSingleListItem(index) { itemViewSupplier ->
            val itemView = itemViewSupplier()
            var phraseTV = itemView.findViewByString<TextView>("phraseTextView")
            var deleteTV = itemView.findViewByString<TextView>("phraseTextView")
        }
    }

    protected fun recyclerViewCheckAmount() {
        val view = activity.findViewByString<RecyclerView>("recyclerView")
        val expectedInitialItems = 3
        val actualInitialItems = view.adapter!!.itemCount
        val messageInitialText = "The recyclerView doesn't have 3 or more items. Found $actualInitialItems items."
        assertTrue(messageInitialText, (actualInitialItems >= expectedInitialItems))
    }

    protected fun recyclerViewClick() {
        val view: RecyclerView = activity.findViewByString("recyclerView")
        val actualAmount = view.adapter!!.itemCount
        val expectedAmount = actualAmount - 1
        view.findViewByString<TextView>("deleteTextView").clickAndRun()
        assertEquals("The recyclerView didn't remove item after clicking 'Delete'.", expectedAmount, view.adapter!!.itemCount)
    }

    //  .... common recurring assertions, util functions, other views
}
