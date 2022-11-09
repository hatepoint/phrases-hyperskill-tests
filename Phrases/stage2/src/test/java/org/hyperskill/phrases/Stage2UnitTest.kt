package org.hyperskill.phrases

import org.hyperskill.phrases.internals.PhrasesUnitTest
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
class Stage2UnitTest : PhrasesUnitTest<MainActivity>(MainActivity::class.java){

    @Test
    fun test00_checkReminderTextViewExists() {
        testActivity {
            reminderTv
        }
    }

    @Test
    fun test01_checkFloatingButtonExists() {
        testActivity {
            floatingButton
        }
    }

    @Test
    fun test02_checkRecyclerViewExists() {
        testActivity {
            recyclerView
        }
    }
    @Test
    fun test03_checkFirstListItemContainExpectedViews() {
        testActivity {
            recyclerView.assertItemViewsExistOnItemWithIndex(0)
        }
    }

    @Test
    fun test04_checkRecyclerViewHasAtLeastThreeItems() {
        testActivity {
            recyclerView.assertAmountItems(3)
        }
    }

    @Test
    fun test05_checkClickingDeleteDecreasesNumberOfItems() {
        testActivity {
            recyclerView.deleteLastItemAndAssertSizeDecreased()
        }
    }
}