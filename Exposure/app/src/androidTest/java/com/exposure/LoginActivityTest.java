package com.exposure;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.exposure.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @BeforeClass
    public static void setup() {
        /* Log user out to avoid login persistence issues */
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void activityInViewTest() {
        ActivityScenario<LoginActivity> activity = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.activity_login)).check(matches(isDisplayed()));
    }

    @Test
    public void visibilityLogoTest() {
        ActivityScenario<LoginActivity> activity = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.logo)).check(matches(isDisplayed()));
    }

    @Test
    public void visibilityEmailTextFieldTest() {
        ActivityScenario<LoginActivity> activity = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.email_field)).check(matches(isDisplayed()));
    }

    @Test
    public void visibilityPasswordTextFieldTest() {
        ActivityScenario<LoginActivity> activity = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.password_field)).check(matches(isDisplayed()));
    }

    @Test
    public void loginWithoutEmailTest() {
        ActivityScenario<LoginActivity> activity = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.email_edit_text)).check(matches(hasErrorText("Email required.")));
    }

    @Test
    public void loginWithoutPasswordTest() {
        ActivityScenario<LoginActivity> activity = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.email_edit_text)).perform(typeText("ben@exposure.com"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.password_edit_text)).check(matches(hasErrorText("Password required.")));
    }

    @Test
    public void successfulLoginTest() {
        ActivityScenario<LoginActivity> activity = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.email_edit_text)).perform(typeText("ben@exposure.com"), closeSoftKeyboard());
        onView(withId(R.id.password_edit_text)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        /* Sleep current thread to allow login process to occur */
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.activity_login)).check(doesNotExist());
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));

        /* Log the user out */
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void unsuccessfulLoginTest() {
        ActivityScenario<LoginActivity> activity = ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.email_edit_text)).perform(typeText("hacker@hacks.com"), closeSoftKeyboard());
        onView(withId(R.id.password_edit_text)).perform(typeText("hacker"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        /* Sleep current thread to allow login process to occur */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.activity_login)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_main)).check(doesNotExist());
    }
}