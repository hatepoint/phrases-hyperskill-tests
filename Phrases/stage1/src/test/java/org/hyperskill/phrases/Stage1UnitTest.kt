package org.hyperskill.phrases

import org.hyperskill.phrases.internals.PhrasesUnitTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest : PhrasesUnitTest<MainActivity>(MainActivity::class.java){

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