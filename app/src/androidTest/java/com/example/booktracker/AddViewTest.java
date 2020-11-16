package com.example.booktracker;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.ui.AddBookActivity;
import com.example.booktracker.ui.HomeActivity;
import com.example.booktracker.ui.SignInActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * This will test if a book is added and it will also test if the list of books can be seen
 * by the user
 */
public class AddViewTest {
    private Solo solo;
    private String email = "test@gmail.com";
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
     * Initialize entries in the AddBookActivity edit text.
     */
    private void mockBook(){
        solo.enterText((EditText) solo.getView(R.id.addbook_title),"The Communist Manifesto");
        solo.enterText((EditText) solo.getView(R.id.addbook_author),"Karl Marx");
        solo.enterText((EditText) solo.getView(R.id.addbook_isbn),"9780671678814");
        solo.enterText((EditText) solo.getView(R.id.addbook_description),"Test book");
    }

    /**
     * Sign in and set the current activity to HomeActivity.
     */
    private void login(){
        solo.assertCurrentActivity("Wrong activity should be SignInAcitiviy",SignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field),email);
        solo.enterText((EditText) solo.getView(R.id.password_field),pass);
        solo.clickOnButton("SIGN IN");
        solo.waitForActivity(HomeActivity.class);
        solo.assertCurrentActivity("Wrong activity should be HomeActivity",HomeActivity.class);
    }
    /**
     * Delete the book that was used from firestore.
     */
    private void deleteBook(){
        DeleteBookQuery del = new DeleteBookQuery(email);
        Book book1 = new Book();
        book1.setIsbn("9780671678814");
        book1.setStatus("available");
        del.deleteBook(book1);
    }

    /**
     * Test the add book functionality of AddBookActivity.
     */
    @Test
    public void addBook(){
        login();
        solo.clickOnButton("Add");
        solo.assertCurrentActivity("Wrong activity should be AddBookActivity",AddBookActivity.class);
        mockBook();
        solo.clickOnButton("Add");
        assertTrue(solo.waitForActivity(HomeActivity.class));
        assertTrue("book was not added",solo.searchText("The Communist Manifesto"));
        deleteBook();
    }

}
