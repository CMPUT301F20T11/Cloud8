package com.example.booktracker;

import android.widget.EditText;
import android.widget.SearchView;

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

import static org.junit.Assert.assertTrue;

/**
 * This will test if a book is added and it will also test if the list of books can be seen
 * by the user
 */
public class FindBookTest {
    private Solo solo;
    private String email = "zm1@ualberta.ca";
    private String pass = "password";
    private Book book;
    @Rule
    public ActivityTestRule<SignInActivity> rule =
            new ActivityTestRule<>(SignInActivity.class,true,true);
    /**
     * Initialize solo to be used by tests.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        addToDb();
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    @After
    public void tearDown(){
        deleteBook();
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
        ArrayList<String> author = new ArrayList<>();
        author.add("Karl Pogs");
        HashMap<String, String> owner = new HashMap<>();
        owner.put(email, "");
        book = new Book(owner, author, "/pol/ Manifesto",
                "6980671678814", "Test book");
        addBook.loadUsername(book);
        addBook.addBook(book);
    }
    /**
     * Initialize entries in the AddBookActivity edit text.
     */
    private void mockBook(){
        solo.enterText((EditText) solo.getView(R.id.addbook_title),"/pol/ Manifesto");
        solo.enterText((EditText) solo.getView(R.id.addbook_author),"Karl Pogs");
        solo.enterText((EditText) solo.getView(R.id.addbook_isbn),"6980671678814");
        solo.enterText((EditText) solo.getView(R.id.addbook_description),"Test book");
    }

    /**
     * Sign in and set the current activity to HomeActivity.
     */
    private void login(){
        solo.assertCurrentActivity("Wrong activity should be SignInAcitiviy",SignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field),email);
        solo.enterText((EditText) solo.getView(R.id.password_field),pass);
        solo.clickOnButton("Sign In");
        solo.waitForActivity(HomeActivity.class);
        solo.assertCurrentActivity("Wrong activity should be HomeActivity",HomeActivity.class);
    }
    /**
     * Test the add book functionality of AddBookActivity.
     */
    @Test
    public void findBook() throws InterruptedException{
        login();
        solo.clickOnView(solo.getView(R.id.nav_find));
        solo.clickOnImageButton(0);
        solo.clickOnText("Find Books");
        SearchView view = (SearchView) solo.getView(R.id.book_search);
        view.setQuery("Test book",true);
        assertTrue("Book not found", solo.searchText("/pol/ Manifesto"));
    }

}
