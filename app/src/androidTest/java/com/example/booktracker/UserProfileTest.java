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

import static org.junit.Assert.assertTrue;

public class UserProfileTest {
    private Solo solo;
    private String email = "test@gmail.com";
    private String pass = "password";
    private String username = "test";
    private String phone = "12345678";
    @Rule
    public ActivityTestRule<SignInActivity> rule =
            new ActivityTestRule<>(SignInActivity.class,true,true);

    /**
     * Initialize solo to be used by tests.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Sign in and set the current activity to HomeActivity.
     */
    private void login() {
        solo.assertCurrentActivity("Wrong activity, should be SignInActivity", SignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field),email);
        solo.enterText((EditText) solo.getView(R.id.password_field),pass);
        solo.clickOnButton("Sign In");
        solo.waitForActivity(HomeActivity.class);
        solo.assertCurrentActivity("Wrong activity, should be HomeActivity", HomeActivity.class);
    }

    @Test
    public void testProfile() {
        login();
        solo.clickOnView(solo.getView(R.id.nav_find));
        solo.clickOnImageButton(0);
        solo.clickOnText("Profile");
        assertTrue("Email not found", solo.searchText(email));
        assertTrue("Username not found", solo.searchText(username));
        assertTrue("Phone number not found", solo.searchText(phone));
    }
}
