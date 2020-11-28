package com.example.booktracker;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booktracker.ui.HomeActivity;
import com.example.booktracker.ui.SignInActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LogInTest {
    private Solo solo;
    private String email = "zm1@ualberta.ca";
    private String pass = "password";
    @Rule
    public ActivityTestRule<SignInActivity> rule =
            new ActivityTestRule<>(SignInActivity.class,true,true);
    /**
     * Initialize solo to be used by tests.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Test if registered user is able to log in
     */
    @Test
    public void login(){
        solo.assertCurrentActivity("Wrong activity should be SignInAcitiviy",SignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field),email);
        solo.enterText((EditText) solo.getView(R.id.password_field),pass);
        solo.clickOnButton("Sign In");
        solo.waitForActivity(HomeActivity.class);
        solo.assertCurrentActivity("Wrong activity should be HomeActivity",HomeActivity.class);
    }
}
