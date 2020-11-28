package com.example.booktracker;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booktracker.boundary.AddBookQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.ui.HomeActivity;
import com.example.booktracker.ui.SignInActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;

public class DeleteTest {
    private Solo solo;
    private String email = "test@gmail.com";
    private String pass = "password";
    private Book book;
    @Rule
    public ActivityTestRule<SignInActivity> rule =
            new ActivityTestRule<>(SignInActivity.class, true, true);

    /**
     * Initialize solo to be used by tests and add book to be tested in db
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        addToDb();
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                rule.getActivity());
    }

    /**
     * Add test book to db
     */
    private void addToDb() {
        AddBookQuery addBook = new AddBookQuery(email);
        ArrayList<String> author = new ArrayList<>();
        HashMap<String, String> owner = new HashMap<>();
        owner.put(email, "");
        author.add("Karl Pogs");
        book = new Book(owner, author, "/pol/ Manifesto",
                "6980671678814", "Test book");
        addBook.loadUsername(book);
        addBook.addBook(book);
    }

    /**
     * Sign in and set the current activity to HomeActivity.
     */
    private void login() {
        solo.assertCurrentActivity("Wrong activity, should be SignInActivity",
                SignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field), email);
        solo.enterText((EditText) solo.getView(R.id.password_field), pass);
        solo.clickOnButton("Sign In");
        solo.waitForActivity(HomeActivity.class);
        solo.assertCurrentActivity("Wrong activity, should be HomeActivity",
                HomeActivity.class);
    }

    /**
     * Test the book deletion functionality
     */
    @Test
    public void deleteBook() {
        login();
        solo.clickOnText("/pol/ Manifesto");
        solo.clickOnView(solo.getView(R.id.delete_book_button));
        assertFalse("Book was not deleted", solo.searchText("/pol/ " +
                "Manifesto"));
    }
}
