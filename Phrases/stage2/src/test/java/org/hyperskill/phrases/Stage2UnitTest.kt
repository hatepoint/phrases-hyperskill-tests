package org.hyperskill.phrases

import org.hyperskill.phrases.internals.PhrasesUnitTest
import org.hyperskill.phrases.ui.MainActivity
import org.junit.After
import org.junit.Before
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

    @Before
    fun setDB(){
        addToRoomDatabase()
        //resetRoomDatabase()
    }

    @Test
    fun checkRecyclerView() {
        testActivity {
            recyclerView
            recyclerViewItems
            recyclerViewCheckAmount
            recyclerViewClick
        }
    }

    @After
    fun cleanUp() {
        killRoomInstance()
    }

    @Test
    fun checkFloatingButton() {
        testActivity {
            floatingButton
        }
    }
}