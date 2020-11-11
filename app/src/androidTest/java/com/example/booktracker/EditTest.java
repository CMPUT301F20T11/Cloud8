package com.example.booktracker;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.ui.AddBookActivity;
import com.example.booktracker.ui.EditBookActivity;
import com.example.booktracker.ui.HomeActivity;
import com.example.booktracker.ui.SignInActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EditTest {
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
     * Test if activity starts.
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
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
     * set the EditText for author,title, and description in EditBookActivity.
     */
    private void mockEdit(){
        solo.assertCurrentActivity("Wrong activity should be AddBookActivity", EditBookActivity.class);
        EditBookActivity activity = (EditBookActivity) solo.getCurrentActivity();
        EditText title = activity.findViewById(R.id.editbook_title);
        EditText author = activity.findViewById(R.id.editbook_author);
        EditText description = activity.findViewById(R.id.editbook_description);
        title.setText("");
        author.setText("");
        description.setText("");
        solo.enterText(title,"Edited The Communist Manifesto");
        solo.enterText(author,"Edited Karl Marx");
        solo.enterText(description,"Edited Test book");
    }

    /**
     * check if the edit was properly applied.
     */
    private void checkEdit(){
        assertTrue("title cant be found",solo.searchText("Edited The Communist Manifesto"));
        assertTrue("author cant be found",solo.searchText("Edited Karl Marx"));
        assertTrue("description was not edited",solo.searchText("Edited Test book"));
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
     * test the edit functionality of EditBookActivity
     */
    @Test
    public void editBook(){
        //=======================add a book to be deleted===========================================
        login();
        solo.clickOnButton("Add");
        solo.assertCurrentActivity("Wrong activity should be AddBookActivity", AddBookActivity.class);
        mockBook();
        solo.clickOnButton("Add");
        assertTrue(solo.waitForActivity(HomeActivity.class));
        assertTrue("book was not added",solo.searchText("The Communist Manifesto"));
        //=======================done===============================================================
        solo.clickOnText("The Communist Manifesto");
        solo.clickOnButton("Edit");
        mockEdit();
        solo.clickOnButton("Save");
        assertTrue(solo.waitForActivity(HomeActivity.class));
        solo.clickOnText("The Communist Manifesto");
        solo.clickOnButton("View");
        checkEdit();
        deleteBook();
    }
}
