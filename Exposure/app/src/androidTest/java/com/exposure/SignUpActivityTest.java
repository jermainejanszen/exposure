package com.exposure;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.exposure.activities.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {

    @BeforeClass
    public static void setup() {
        /* Log user out to avoid login persistence issues */
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void activityInViewTest() {
        ActivityScenario<SignUpActivity> activity = ActivityScenario.launch(SignUpActivity.class);
        onView(withId(R.id.activity_signup)).check(matches(isDisplayed()));
    }

    @Test
    public void visibilityUsernameTextFieldTest() {
        ActivityScenario<SignUpActivity> activity = ActivityScenario.launch(SignUpActivity.class);
        onView(withId(R.id.full_name_edit_text)).check(matches(withHint(R.string.name)));
    }

    @Test
    public void visibilityEmailTextFieldTest() {
        ActivityScenario<SignUpActivity> activity = ActivityScenario.launch(SignUpActivity.class);
        onView(withId(R.id.email_edit_text)).check(matches(withHint(R.string.email)));
    }

    @Test
    public void visibilityPasswordTextFieldTest() {
        ActivityScenario<SignUpActivity> activity = ActivityScenario.launch(SignUpActivity.class);
        onView(withId(R.id.password_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.password_edit_text)).check(matches(withHint(R.string.password)));
    }

    @Test
    public void visibilitySignUpButtonTest() {
        ActivityScenario<SignUpActivity> activity = ActivityScenario.launch(SignUpActivity.class);
        onView(withId(R.id.sign_up_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_button)).check(matches(withText(R.string.sign_up)));
    }

    @Test
    public void visibilityTermsAndConditionsTextTest() {
        ActivityScenario<SignUpActivity> activity = ActivityScenario.launch(SignUpActivity.class);

        onView(withId(R.id.terms_of_service_checkbox)).check(matches(isDisplayed()));
        onView(withId(R.id.terms_of_service_checkbox)).check(matches(withText(R.string.terms_and_service_agreement)));
    }

    @Test
    public void signupWithoutFullNameTest() {
        ActivityScenario<SignUpActivity> activity = ActivityScenario.launch(SignUpActivity.class);
        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withId(R.id.full_name_edit_text)).check(matches(hasErrorText("Name required")));
    }

    @Test
    public void signupWithoutEmailTest() {
        ActivityScenario<SignUpActivity> activity = ActivityScenario.launch(SignUpActivity.class);

        onView(withId(R.id.full_name_edit_text)).perform(typeText("Benji"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withId(R.id.email_edit_text)).check(matches(hasErrorText("Email required")));
    }

    @Test
    public void signupWithoutPasswordTest() {
        ActivityScenario<SignUpActivity> activity = ActivityScenario.launch(SignUpActivity.class);

        onView(withId(R.id.full_name_edit_text)).perform(typeText("Benji"), closeSoftKeyboard());
        onView(withId(R.id.email_edit_text)).perform(typeText("benji@live.com.au"), closeSoftKeyboard());
        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withId(R.id.password_edit_text)).check(matches(hasErrorText("Password required")));
    }
}