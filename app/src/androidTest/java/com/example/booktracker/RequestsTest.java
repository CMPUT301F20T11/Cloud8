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
import com.example.booktracker.ui.SignInActivity;
import com.google.android.gms.maps.model.LatLng;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class RequestsTest {
    private Solo solo;
    private String email1 = "test1@gmail.com";
    private String pass1 = "password";
    private String email2 = "test2@gmail.com";
    private String pass2 = "password";
    private Double lat = 53.498500;
    private Double lon = -113.496640;
    private LatLng pickupLocation = new LatLng(lat,lon);

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

//    /**
//     * Initialize entries in the AddBookActivity edit text.
//     */
//    private void mockBook() {
//        solo.enterText((EditText) solo.getView(R.id.addbook_title), "The " +
//                "Communist Manifesto");
//        solo.enterText((EditText) solo.getView(R.id.addbook_author), "Karl " +
//                "Marx");
//        solo.enterText((EditText) solo.getView(R.id.addbook_isbn),
//                "9780671678814");
//        solo.enterText((EditText) solo.getView(R.id.addbook_description),
//                "Test book");
//    }

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
        solo.clickOnView(solo.getView(R.id.nav_find));
        solo.clickOnImageButton(0);
        solo.clickOnText("Profile");
        solo.clickOnText("Logout");
    }



//    /**
//     * Test if correct information is displayed in ViewBookActivity.
//     */
//    private void testView() {
//        assertTrue("title cant be found", solo.searchText("The Communist " +
//                "Manifesto"));
//        assertTrue("author cant be found", solo.searchText("Karl Marx"));
//        assertTrue("isbn cant be found", solo.searchText("9780671678814"));
//        assertTrue("description cant be found", solo.searchText("Test book"));
//        assertTrue("owner cant be found", solo.searchText("test@gmail.com"));
//        assertTrue("status cant be found", solo.searchText("Test book"));
//    }

    /**
     * Delete the book that was used from firestore.
     */
    private void deleteBook(String email) {
        DeleteBookQuery del = new DeleteBookQuery(email);
        Book book1 = new Book();
        book1.setIsbn("9780671678814");
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
        owner.put(email1, "");
        book = new Book(owner, author, "request69 Test title",
                "1234123412349", "descr");
        addBook.loadUsername(book);
        addBook.addBook(book);
    }

    /**
     * test2@gmail.com requests book from test1@gmail.com
     */
    private void requestBook() {
        solo.clickOnView(solo.getView(R.id.nav_find));
        solo.clickOnImageButton(0);
        solo.clickOnText("Find Books");
        SearchView searchview = (SearchView) solo.getView(R.id.book_search);
        solo.clickOnView(solo.getView(searchview));
        // search for "request69"

        //click on book
        solo.clickOnText("request69");
        //request book
        solo.clickOnView(solo.getView(R.id.request_book_button));

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
        solo.clickOnButton(R.id.accept_req_button);
        solo.clickOnText("Yes");
        setGeo();

        //set geo location
    }

    private void setGeo(){
        //set location

        solo.clickOnButton(R.id.geo_confirm_button);
    }

    /**
     * test1@gmail.com accepts book request from test2@gmail.com
     * and specifies a pickupLocation
     */
    private void viewLocation() {
        solo.clickOnView(solo.getView(R.id.nav_find));
        solo.clickOnImageButton(0);
        solo.clickOnText("Accepted Requests");
        solo.clickOnButton(R.id.view_geo_button);
        solo.sleep(1000);
    }

    /**
     * Delete book form db
     */
    @After
    public final void tearDown() {
        deleteBook(email1);
    }

    /**
     * Use helper functions mockBook,login,testView,deleteBook to test the
     * ViewBook functionality
     */
    @Test
    public void TestrequestesT() {
        //login to user2 account - this account will request user1 book
        login(email2, pass2);
        //create request for user1 book from user2
        requestBook();
        //logout user2 account
        logout();
        //login to user1 account
        login(email2, pass2);
        //accept request for my book and set location
        acceptRequest();
        //logout user1 account
        logout();
        //login user2 account
        login(email2, pass2);
        //view accepted request
        viewLocation();
        //view accepted request location
        //DONE

        
//        testView();
    }


}
