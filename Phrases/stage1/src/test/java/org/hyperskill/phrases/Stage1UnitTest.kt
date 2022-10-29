package org.hyperskill.phrases

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.hyperskill.phrases.internals.AbstractUnitTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest : AbstractUnitTest<MainActivity>(MainActivity::class.java){

    private val reminderTv: TextView by lazy {
        val view = activity.findViewByString<TextView>("reminderTextView")
        val messageInitialText = "The reminderTextView has a wrong initial text"
        val expectedInitialText = "No reminder set"
        val actualInitialText = view.text.toString()
        assertEquals(messageInitialText, expectedInitialText, actualInitialText)

        view
    }

    private val recyclerView : RecyclerView by lazy {
        val view = activity.findViewByString<RecyclerView>("recyclerView")

        view
    }

    private val floatingButton: FloatingActionButton by lazy {
        val view = activity.findViewByString<FloatingActionButton>("addButton")

        view
    }

    @Test
    fun checkReminderTextView() {
        testActivity {
            reminderTv
        }
    }

    @Test
    fun checkRecyclerView() {
        testActivity {
            recyclerView
        }
    }

    @Test
    fun checkFloatingButton() {
        testActivity {
            floatingButton
        }
    }
}