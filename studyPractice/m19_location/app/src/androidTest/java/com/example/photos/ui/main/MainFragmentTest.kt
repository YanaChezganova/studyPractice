package com.example.photos.ui.main

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.photos.MainActivity
import com.example.photos.R
import com.example.photos.ui.main.support.FakeDataList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeFragmentTestUI {
    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    val LIST_ITEM_IN_TEST = 5
    val MOVIE_IN_TEST = FakeDataList.photosObject[LIST_ITEM_IN_TEST]

    @Test
    fun is_start_fragment_visible(){
        onView(withId(R.id.layout)).check(matches(isDisplayed()))
    }
    @Test
    fun is_home_fragment_visible(){
        onView(withId(R.id.button_start)).perform(click())
        onView(withId(R.id.main_fragment)).check(matches(isDisplayed()))
    }
    @Test
    fun is_recycler_view_visible(){
        onView(withId(R.id.button_start)).perform(click())
        onView(withId(R.id.photo_recycler_view)).check(matches(isDisplayed()))
    }


    @Test
    fun scroll_to_item_below_fold_check_its_text() {
        onView(withId(R.id.button_start)).perform(click())
        // First scroll to the position that needs to be matched and click on it.
        onView(withId(R.id.photo_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(LIST_ITEM_IN_TEST, click()))
        //check big-photo-fragment is visible
        onView(withId(R.id.date_see_fragment)).check(matches(isDisplayed()))
        //check matches date in listOfElements[n] with date n-element of recycler view
        onView(withId(R.id.date_see_fragment)).check(matches( withText(MOVIE_IN_TEST.date)))
    }

    @Test
    fun click_on_take_photo_button_fragment_takePhoto_visible(){
        onView(withId(R.id.button_start)).perform(click())
        onView(withId(R.id.take_photo))
            .perform(click())
        onView(withId(R.id.fragment_second_take_photo)).check(matches(isDisplayed()))
    }
}


/*   class RecyclerViewSampleTest {
       /**
        * Use [ActivityScenario] to create and launch the activity under test. This is a
        * replacement for [androidx.test.rule.ActivityTestRule].
        */
       @Rule
       var activityScenarioRule = ActivityScenarioRule(
           MainActivity::class.java
       )





       @Test
       fun itemInMiddleOfList_hasSpecialText() {
           // First, scroll to the view holder using the isInTheMiddle matcher.
           onView(withId(R.id.recyclerView))
               .perform(RecyclerViewActions.scrollToHolder(isInTheMiddle))

           // Check that the item has the special text.
           val middleElementText: String =
               getApplicationContext().getResources().getString(R.string.middle)
           onView(withText(middleElementText)).check(matches(isDisplayed()))
       }

       companion object {
           private const val ITEM_BELOW_THE_FOLD = 40

           /**
            * Matches the [CustomAdapter.ViewHolder]s in the middle of the list.
            */
           private val isInTheMiddle: Matcher<CustomAdapter.ViewHolder>
               private get() = object : TypeSafeMatcher<CustomAdapter.ViewHolder?>() {
                   protected fun matchesSafely(customHolder: CustomAdapter.ViewHolder): Boolean {
                       return customHolder.getIsInTheMiddle()
                   }

                   fun describeTo(description: Description) {
                       description.appendText("item in the middle")
                   }
               }
       }
   }
}*/
