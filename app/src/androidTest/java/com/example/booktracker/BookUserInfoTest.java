package com.example.booktracker;

import android.widget.ActionMenuView;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booktracker.ui.HomeActivity;
import com.example.booktracker.ui.SignInActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class BookUserInfoTest {
    private Solo solo;
    private String email = "test@gmail.com";
    private String pass = "password";
    private String username = "test";
    private String phone = "12345678";
    @Rule
    public ActivityTestRule<SignInActivity> rule =
            new ActivityTestRule<>(SignInActivity.class, true, true);

    /**
     * Initialize solo to be used by tests.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        login();
    }

    /**
     * Sign in and set the current activity to HomeActivity.
     */
    private void login() {
        solo.assertCurrentActivity("Wrong activity should be SignInAcitiviy", SignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field), email);
        solo.enterText((EditText) solo.getView(R.id.password_field), pass);
        solo.clickOnButton("SIGN IN");
        solo.waitForActivity(HomeActivity.class);
        solo.assertCurrentActivity("Wrong activity should be HomeActivity", HomeActivity.class);
    }


    private void checkProfEdit() {
        assertTrue("Email not found", solo.searchText("edited" + email));
        assertTrue("Phone number not found", solo.searchText("11" + phone));
    }


    @Test
    public void testUserInfo() {

    }
}
