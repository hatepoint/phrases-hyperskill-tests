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
}
