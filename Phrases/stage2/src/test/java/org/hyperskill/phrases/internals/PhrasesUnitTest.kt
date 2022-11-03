package org.hyperskill.phrases.internals

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.hyperskill.phrases.data.room.AppDatabase
import org.hyperskill.phrases.data.room.entity.Phrase
import org.junit.Assert.*
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowNotificationManager
import kotlin.reflect.KProperty


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

    protected val recyclerViewItems: RecyclerView by lazy {
        val view = activity.findViewByString<RecyclerView>("recyclerView")
        assertNotNull("The recyclerView adapter should not be null", view)
        val phraseTV = view.findViewByString<TextView>("phraseTextView")
        val deleteTV = view.findViewByString<TextView>("deleteTextView")
        view
    }

    protected val recyclerViewCheckAmount: RecyclerView by lazy {
        val view = activity.findViewByString<RecyclerView>("recyclerView")
        assertNotNull("The recyclerView adapter should not be null", view)
        val expectedInitialItems = 3
        val actualInitialItems = view.adapter!!.itemCount
        val messageInitialText = "The recyclerView doesn't have 3 or more items. Found $actualInitialItems items."
        assertTrue(messageInitialText, (actualInitialItems >= expectedInitialItems))
        view
    }

    protected val recyclerViewClick: RecyclerView by lazy {
        val view: RecyclerView = activity.findViewByString("recyclerView")
        val actualAmount = view.adapter!!.itemCount
        val expectedAmount = actualAmount - 1
        view.findViewByString<TextView>("deleteTextView").clickAndRun()
        assertEquals("The recyclerView didn't remove item after clicking 'Delete'.", expectedAmount, view.adapter!!.itemCount)
        view
    }

    fun addToRoomDatabase(amount: Int = 3) {
        val db = AppDatabase.getInstance(activity)
        val dao = db.phraseDao()
        for (i in 1..amount) {
            dao.insert(Phrase(0,"This is a test phrase #$i"))
        }
        killRoomInstance()
    }

    fun resetRoomDatabase() {
        val db = AppDatabase.getInstance(activity)
        db.clearAllTables()
    }

    fun killRoomInstance() {
        val field = AppDatabase::class.java.getDeclaredField("INSTANCE")
        field.isAccessible = true
        field.set(null, null)
    }

    //  .... common recurring assertions, util functions, other views
}
