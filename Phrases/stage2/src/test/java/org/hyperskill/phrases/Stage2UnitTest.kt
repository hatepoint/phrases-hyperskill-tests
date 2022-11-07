package org.hyperskill.phrases

import org.hyperskill.phrases.internals.PhrasesUnitTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Stage2UnitTest : PhrasesUnitTest<MainActivity>(MainActivity::class.java){

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
            recyclerViewItems()
            recyclerViewCheckAmount()
            recyclerViewClick()
        }
    }

    @Test
    fun checkFloatingButton() {
        testActivity {
            floatingButton
        }
    }
}