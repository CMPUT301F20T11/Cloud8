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

public class FilterBookTest {
    private Solo solo;
    private String email = "test@gmail.com";
    private String pass = "password";
    Book borrowed;
    Book accepted;
    Book requested;
    Book available;
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
        addToDb();//this will add books to db with the filters to be tested
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                rule.getActivity());
    }

    /**
     * Sign in and set the current activity to HomeActivity.
     */
    private void login() {
        solo.assertCurrentActivity("Wrong activity should be SignInAcitiviy",
                SignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field), email);
        solo.enterText((EditText) solo.getView(R.id.password_field), pass);
        solo.clickOnButton("SIGN IN");
        solo.waitForActivity(HomeActivity.class);
        solo.assertCurrentActivity("Wrong activity should be HomeActivity",
                HomeActivity.class);
    }

    /**
     * set 4 different books with the 4 distinct statuses.
     */
    private void setBooks() {
        List<String> author = new ArrayList<>();
        author.add("test");
        HashMap<String, String> owner = new HashMap<>();
        owner.put(email, "");
        borrowed = new Book(owner, author, "borrowed Book", "1000000000000",
                "test");
        accepted = new Book(owner, author, "accepted Book", "2000000000000",
                "test");
        requested = new Book(owner, author, "requested Book", "3000000000000"
                , "test");
        available = new Book(owner, author, "available Book", "4000000000000"
                , "test");
        borrowed.setStatus("borrowed");
        accepted.setStatus("accepted");
        requested.setStatus("requested");
        available.setStatus("available");
    }

    /**
     * add the book to the database.
     */
    private void addToDb() {
        AddBookQuery query = new AddBookQuery(email);
        setBooks();

        query.loadUsername(borrowed);
        query.addBook(borrowed);

        query.loadUsername(accepted);
        query.addBook(accepted);

        query.loadUsername(requested);
        query.addBook(requested);

        query.loadUsername(available);
        query.addBook(available);
    }

    /**
     * Test if books with borrowed status displays.
     */
    private void testBorrowed() {
        solo.clickOnButton("filter");
        assertTrue("fragment is not displayed", solo.searchText("Filters"));
        solo.clickOnButton("Borrowed");
        assertTrue("book was not filtered", solo.searchText("borrowed Book"));
    }

    /**
     * Test if books with accepted status displays.
     */
    private void testAccepted() {
        solo.clickOnButton("filter");
        assertTrue("fragment is not displayed", solo.searchText("Filters"));
        solo.clickOnButton("Accepted");
        assertTrue("book was not filtered", solo.searchText("accepted Book"));
    }

    /**
     * Test if books with requested status displays.
     */
    private void testRequested() {
        solo.clickOnButton("filter");
        assertTrue("fragment is not displayed", solo.searchText("Filters"));
        solo.clickOnButton("Requested");
        assertTrue("book was not filtered", solo.searchText("requested Book"));
    }

    /**
     * Test if books with available status displays.
     */
    private void testAvailable() {
        solo.clickOnButton("filter");
        assertTrue("fragment is not displayed", solo.searchText("Filters"));
        solo.clickOnButton("Available");
        assertTrue("book was not filtered", solo.searchText("available Book"));
    }

    /**
     * Test the 4 distinct filters.
     */
    private void testFilters() {
        solo.assertCurrentActivity("Wrong activity should be AddBookActivity"
                , HomeActivity.class);
        testAccepted();
        testAvailable();
        testBorrowed();
        testRequested();
    }

    /**
     * Delete all the books that were created for the sake of testing from
     * the database.
     */
    private void deleteBook() {
        DeleteBookQuery del = new DeleteBookQuery(email);
        del.deleteBook(borrowed);
        del.deleteBook(accepted);
        del.deleteBook(available);
        del.deleteBook(requested);
    }

    /**
     * Test if the correct books are displayed when a filter is selected.
     */
    @Test
    public void testBookFilters() {
        login();
        testFilters();
    }

    @After
    public final void tearDown() {
        deleteBook();
    }
}