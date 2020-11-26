package com.example.booktracker;

import android.widget.EditText;
import android.widget.SearchView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booktracker.boundary.AddBookQuery;
import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;
import com.example.booktracker.ui.HomeActivity;
import com.example.booktracker.ui.SetGeoActivity;
import com.example.booktracker.ui.SignInActivity;
import com.example.booktracker.ui.ViewGeoActivity;
import com.google.android.gms.maps.model.LatLng;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Book Exchange Test
 * Tests Requesting, Accepting, setGeo, viewGeo, borrowing, returning
 */
public class BookExchangeTest {
    private Solo solo;
    private String email1 = "testrequest1@gmail.com";
    private String pass1 = "password";
    private String email2 = "testrequest2@gmail.com";
    private String pass2 = "password";


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
        addBookToDb(email1);
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                rule.getActivity());
    }

    /**
     * Sign in and set the current activity to HomeActivity.
     */
    private void login(String email, String password) {
        solo.assertCurrentActivity("Wrong activity should be SignInAcitiviy",
                SignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_field), email);
        solo.enterText((EditText) solo.getView(R.id.password_field), password);
        solo.clickOnButton("SIGN IN");
        solo.waitForActivity(HomeActivity.class);
        solo.assertCurrentActivity("Wrong activity should be HomeActivity",
                HomeActivity.class);
    }

    /**
     * Log out of current account
     */
    private void logout(){
        // nav drawer
        navDrawer("Profile");
        solo.clickOnText("Logout");
        solo.waitForActivity(SignInActivity.class);
        solo.assertCurrentActivity("Wrong activity should be SignInActivity",
                SignInActivity.class);
    }


    /**
     * Delete the book that was used from firestore.
     */
    private void deleteBook(String email) {
        DeleteBookQuery del = new DeleteBookQuery(email);
        Book book1 = new Book();
        book1.setIsbn("1234123412349");
        book1.setStatus("available");
        del.deleteBook(book1);
    }

    /**
     * Add test book to db
     */
    private void addBookToDb(String email) {
        AddBookQuery addBook = new AddBookQuery(email);
        ArrayList<String> author = new ArrayList<>();
        author.add("author");
        HashMap<String, String> owner = new HashMap<>();
        owner.put(email, "");
        book = new Book(owner, author, "request69 Test title",
                "1234123412349", "descr69");
        addBook.loadUsername(book);
        addBook.addBook(book);
    }

    /**
     * test2@gmail.com requests book from test1@gmail.com
     */
    private void requestBook() {
        navDrawer("Find Books");
        SearchView searchview = (SearchView) solo.getView(R.id.book_search);
        searchview.setQuery("descr69",true);
        //click on book
        solo.clickOnText("request69");
        //request book
        solo.clickOnView(solo.getView(R.id.request_book_button));
    }

    /**
     * Once user has requested a book it should show up in RequestedBooks
     */
    private void checkRequestedBooks(){
        navDrawer("Requested Books");
        assertTrue("Book not appearing in borrowed", solo.searchText("request69"));
    }

    /**
     * test1@gmail.com accepts book request from test2@gmail.com
     * and specifies a pickupLocation
     */
    private void acceptRequest() {
        //accept request
        solo.clickOnView(solo.getView(R.id.nav_find));
        solo.clickOnImageButton(0);
        solo.clickOnText("Incoming Requests");
        solo.clickOnText("request69");
        solo.clickOnText("Accept");
        solo.clickOnText("Yes");
        solo.waitForActivity(SetGeoActivity.class);
        solo.assertCurrentActivity("Wrong activity should be SetGeoActivity",
                SetGeoActivity.class);
        setGeo();
    }

    /**
     * DOESNT WORK -- NEED TO SET LOCATION PIN MANUALLY
     */
    private void setGeo(){
        //set location
        solo.clickLongOnScreen(240,400,1000);
        solo.clickOnText("Confirm");
    }

    /**
     * test1@gmail.com accepts book request from test2@gmail.com
     * and specifies a pickupLocation
     */
    private void viewLocation() {
        navDrawer("Accepted Requests");
        solo.clickOnText("request69");
        solo.clickOnButton("View Pickup Location");
        solo.waitForActivity(ViewGeoActivity.class);
        solo.sleep(1000);
        solo.clickOnButton("Done");
    }

    /**
     * denote book as borrowed from borrower side
     */
    private void borrowBook() {
        navDrawer("Accepted Requests");
        solo.clickOnText("request69");
        solo.clickOnButton("View");
        solo.clickOnButton("Borrow book");
    }

    /**
     * denote book as borrowed from owner side
     */
    private void giveBook() {
        navDrawer("My Books");
        solo.clickOnText("request69");
        solo.clickOnButton("View");
        solo.clickOnButton("Give Book");
    }


    /**
     * Book should appear with status borrowed in MyBooks
     */
    private void checkBorrowedStatus(){
        navDrawer("My Books");
        solo.clickOnText("request69");
        solo.clickOnButton("View");
        assertTrue("Book status is not borrowed", solo.searchText("Borrowed"));
    }

    /**
     * Book should appear in borrowed books
     */
    private void checkBorrowedBook(){
        navDrawer("Borrowed Books");
        assertTrue("Book not appearing in borrowed books", solo.searchText("request69"));
    }


    /**
     * user2 returns book to user1
     */
    private void returnBook(){
        navDrawer("Borrowed Books");
        assertTrue("Book not appearing in borrowed", solo.searchText("request69"));
        solo.clickOnText("request69");
        solo.clickOnButton("View");
        solo.clickOnButton("Return book");
    }


    /**
     * user1 confirms book is received
     */
    private void receiveBook() {
        navDrawer("My Books");
        solo.clickOnText("request69");
        solo.clickOnButton("Return book");
    }

    /**
     *
     * @param fragment - nav drawer item to click
     */
    private void navDrawer(String fragment){
        solo.clickOnView(solo.getView(R.id.nav_find));
        solo.clickOnImageButton(0);
        solo.clickOnText(fragment);
    }


    /**
     * Delete book form db
     */
    @After
    public final void tearDown() {
        deleteBook(email1);
    }

    /**
     *
     */
    @Test
    public void TestrequestesT() {
        //login to user2 account - this account will request user1 book
        login(email2, pass2);
        //create request for user1 book from user2
        requestBook();
        //see if new request shows
        checkRequestedBooks();
        //logout user2 account
        logout();
        //login to user1 account
        login(email1, pass1);
        //accept request for my book and set location
        acceptRequest();
        //logout user1 account
        logout();
        //login user2 account
        login(email2, pass2);
        //view accepted request location
        viewLocation();
        //user2 denote borrowed
        borrowBook();
        //logout user2 account
        logout();
        //login user1 account
        login(email1, pass1);
        //user1 denote borrowed
        giveBook();
        //book should have status "borrowed" in My Books
        checkBorrowedStatus();
        //logout user1 account
        logout();
        //login user2 account
        login(email2, pass2);
        //book should appear in Borrowed Books
        checkBorrowedBook();
        //return book back to user1
        returnBook();
        //logout user2 account
        logout();
        //login user1 account
        login(email1, pass1);
        //receive book from user2
        receiveBook();
        //book should appear available in My Books

        //DONE
    }

}
