package com.example.booktracker;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booktracker.boundary.AddBookQuery;
import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.ui.HomeActivity;
import com.example.booktracker.ui.SignInActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ViewBookTest {
    private Solo solo;
    private String email = "test@gmail.com";
    private String pass = "password";
    private Book book;
    @Rule
    public ActivityTestRule<SignInActivity> rule =
            new ActivityTestRule<>(SignInActivity.class, true, true);

    /**
     * Initialize solo to be used by tests. And add book to be tested in db.
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
     * Initialize entries in the AddBookActivity edit text.
     */
    private void mockBook() {
        solo.enterText((EditText) solo.getView(R.id.addbook_title), "/pol/ Manifesto");
        solo.enterText((EditText) solo.getView(R.id.addbook_author), "Karl " +
                "Pogs");
        solo.enterText((EditText) solo.getView(R.id.addbook_isbn),
                "6980671678814");
        solo.enterText((EditText) solo.getView(R.id.addbook_description),
                "Test book");
        solo.enterText((EditText) solo.getView(R.id.addbook_keywords),
                "Dank");
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
     * Test if correct information is displayed in ViewBookActivity.
     */
    private void testView() {
        assertTrue("Title cant be found", solo.searchText("/pol/ Manifesto"));
        assertTrue("Author cannot be found", solo.searchText("Karl Pogs"));
        assertTrue("Isbn cannot be found", solo.searchText("6980671678814"));
        assertTrue("Description cannot be found", solo.searchText("Test book"));
        assertTrue("Owner cannot be found", solo.searchText("test@gmail.com"));
        assertTrue("Keyword cannot be found", solo.searchText("Dank"));
    }

    /**
     * Delete the book that was used from firestore.
     */
    private void deleteBook() {
        DeleteBookQuery del = new DeleteBookQuery(email);
        Book book1 = new Book();
        book1.setIsbn("6980671678814");
        book1.setStatus("available");
        del.deleteBook(book1);
    }

    /**
     * Add test book to db
     */
    private void addToDb() {
        AddBookQuery addBook = new AddBookQuery(email);
        List<String> author = new ArrayList<>();
        List<String> keywords = new ArrayList<>();
        keywords.add("Dank");
        author.add("Karl Pogs");
        HashMap<String, String> owner = new HashMap<>();
        owner.put(email, "");
        book = new Book(owner, author, "/pol/ Manifesto",
                "6980671678814", "Test book", keywords);
        addBook.loadUsername(book);
        addBook.addBook(book);
    }

    /**
     * Delete book form db
     */
    @After
    public final void tearDown() {
        deleteBook();
    }

    /**
     * Use helper functions mockBook,login,testView,deleteBook to test the
     * ViewBook functionality
     */
    @Test
    public void addBook() {
        login();
        solo.clickOnText("/pol/ Manifesto");
        solo.clickOnView(solo.getView(R.id.view_book_button));
        testView();
    }
}
