package com.example.storyapp.view.main

import android.content.Context
import android.support.test.espresso.matcher.ViewMatchers.withId
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.storyapp.utils.EspressoIdlingResource
import com.example.storyapp.view.login.LoginActivity
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.storyapp.R
import com.example.storyapp.view.intro.IntroActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
/**
 * Pengujian proses login
 */
class LoginActivityTest {

    private val dummyEmail = "marwan99999@gmail.com"
    private val dummyPassword = "12345678"

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginLogout_Sucess(){
        /**
         * Proses Login
         */
        onView(withId(R.id.edt_email_text)).perform(typeText(dummyEmail), closeSoftKeyboard())
        onView(withId(R.id.edt_password_text)).perform(typeText(dummyPassword), closeSoftKeyboard())

        Intents.init()
        onView(withId(R.id.btn_sign_in)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withText("Berhasil")).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText("Lanjut")).inRoot(isDialog()).perform(click())
        intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
        onView(withId(R.id.btn_upload)).check(matches(isDisplayed()))
        /**
         * Proses Logout
         */
        Espresso.openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext<Context>()
        )
        onView(withText(R.string.logout))
            .check(matches(isDisplayed()))
        onView(withText(R.string.logout)).perform(click())
        intended(IntentMatchers.hasComponent(IntroActivity::class.java.name))
        onView(withId(R.id.btn_sign_in_intro))
            .check(matches(isDisplayed()))
    }

}