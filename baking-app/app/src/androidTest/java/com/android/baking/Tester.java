package com.android.baking;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;

import android.support.test.espresso.Espresso;

import android.support.test.espresso.contrib.RecyclerViewActions;

import android.support.test.espresso.idling.CountingIdlingResource;

import android.support.test.espresso.matcher.ViewMatchers;

import android.support.test.rule.ActivityTestRule;

import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import com.android.baking.ui.activity.MainActivity;

import com.android.baking.recylerview.RecyclerMatcher;

@RunWith(AndroidJUnit4.class)
public class Tester {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule(MainActivity.class);

    private CountingIdlingResource idlingResource;

    @Before
    public void registerIdlingResource() {
        idlingResource = activityRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(idlingResource);
    }

    @Test
    public void testingRecyclerView() {

        //Checking that first item retrieved correctly
        onView(withRecyclerView(R.id.recipe_rv).atPositionOnView(0, R.id.recipe_name)).check(matches(withText("Nutella Pie")));

        //Make click on item , at example at position 1 "Brownies"
        onView(ViewMatchers.withId(R.id.recipe_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.ingredient_rv)).check(matches(isDisplayed()));
        onView(withId(R.id.steps_rv)).check(matches(isDisplayed()));


        onView(ViewMatchers.withId(R.id.steps_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.description)).check(matches(isDisplayed()));

    }

    public static RecyclerMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerMatcher(recyclerViewId);
    }

    @After
    public void unregisterIdlingResource() {

        if (idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }

    }

}