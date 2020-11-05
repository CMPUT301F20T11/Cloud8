package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.control.Email;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.booktracker.R;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String userEmail;
    private BookCollection bookList;
    private String email;
    private GetBookQuery getQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //======================save the email if the activity gets killed
        if (savedInstanceState != null){
            userEmail = savedInstanceState.getString("email");
        }else{
            userEmail = getIntent().getStringExtra(EXTRA_MESSAGE);
        }
        //====================================================================

        ((Email) this.getApplication()).setEmail(userEmail);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_borrowed, R.id.nav_find, R.id.nav_scan, R.id.nav_incoming,
                R.id.nav_accepted, R.id.nav_requested, R.id.nav_profile, R.id.nav_notifications)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        email = ((Email) this.getApplication()).getEmail();

        //========================nav buttons============================================


    }

    public String getUserEmail() {
        return userEmail;
    }

//    @Override
    /**
     * @author Ivan Penales
     * Check if updates where made to the list of books
     */
//    protected void onResume() {
//        super.onResume();
//        //=============execute async operation===============
//        //books will be displayed after async operation is done
//        getQuery = (new GetBookQuery(email));
//        getQuery.getMyBooks((ListView) findViewById(R.id.my_book_list),getApplicationContext());
//        findViewById(R.id.my_book_list).bringToFront();
//        //===================================================
//    }

    /**
     * This method will be used to save user email in case this activity gets killed
     * @param outState
     * @param outPersistentState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("email",userEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
