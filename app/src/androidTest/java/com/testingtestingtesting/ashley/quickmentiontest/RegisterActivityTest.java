package com.testingtestingtesting.ashley.quickmentiontest;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * This is a test that registers a new user. It checks that the profile activity
 * is then opened and that the current user is the test user that was just registered.
 * The user is then deleted and signed out.
 */
public class RegisterActivityTest {

    private RegisterActivity rActivity;

    private Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(ProfileActivity.class.getName(),null,false);

    private FirebaseAuth mAuth;

    @Rule
    public ActivityTestRule<RegisterActivity> rActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);

    @Before
    public void setUp() throws Exception {
        rActivity = rActivityTestRule.getActivity();
        mAuth = FirebaseAuth.getInstance();
    }

    @Test
    public void testLaunchOfProfileActivity()
    {
        String testEmail = "test@example.com";
        String testPassword = "123456";

        // input email
        Espresso.onView(withId(R.id.editTextEmail)).perform(typeText(testEmail));

        // close soft keyboard
        Espresso.closeSoftKeyboard();

        // input password
        Espresso.onView(withId(R.id.editTextPassword)).perform(typeText(testPassword));

        // close soft keyboard
        Espresso.closeSoftKeyboard();

        // perform button click
        Espresso.onView(withId(R.id.buttonRegister)).perform(click());

        // check if profile activity started
        Activity profileActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(profileActivity);

        // check if user logged in is test user
        assertEquals(mAuth.getCurrentUser().getEmail(), testEmail);



    }

    @After
    public void tearDown() throws Exception {
        // delete test user
        mAuth.getCurrentUser().delete();

        // sign out of profile activity
        Espresso.onView(withId(R.id.buttonSignOut)).perform(click());

    }

}