package com.example.booktracker.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookCollection;
import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.boundary.UserQuery;
import com.example.booktracker.control.Callback;
import com.example.booktracker.control.Email;
import com.example.booktracker.entities.NotifCount;
import com.example.booktracker.entities.Notification;
import com.example.booktracker.entities.NotificationCircle;
import com.example.booktracker.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String userEmail;
    private NotificationCircle notif;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //======================save the email if the activity gets killed
        if (savedInstanceState != null) {
            userEmail = savedInstanceState.getString("email");
        } else {
            userEmail = getIntent().getStringExtra(EXTRA_MESSAGE);
        }
        //=====================================================================
        notif = new NotificationCircle(userEmail,findViewById(R.id.hamburger_count));
        getUsername();
        //=============test===============
//        DeleteBookQuery del = new DeleteBookQuery();
//        del.deleteBookList("myBooks",userEmail);
        //==============test==============-
        ((Email) this.getApplication()).setEmail(userEmail);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_borrowed, R.id.nav_find,
                R.id.nav_scan, R.id.nav_incoming,
                R.id.nav_accepted, R.id.nav_requested, R.id.nav_profile,
                R.id.nav_notifications)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController,
                mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        // ======================== nav buttons ========================
    }

    /**
     * Set up the navigation header
     */
    public void getUsername() {
        FirebaseFirestore.getInstance().collection("users")
                .document(userEmail)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    String username = (String) documentSnapshot.get("username");
                    View headerView = navigationView.getHeaderView(0);
                    TextView usernameText = (TextView) headerView.findViewById(R.id.nav_header_username);
                    usernameText.setText(username);
                    TextView emailText = (TextView) headerView.findViewById(R.id.nav_header_email);
                    emailText.setText(userEmail);
                }
            }
        });
    }

    public void notifRefresh(){
        notif.checkNotification();
    }
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * This method will be used to save user email in case this activity gets
     * killed
     *
     * @param outState
     * @param outPersistentState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState,
                                    @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("email", userEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /*@Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }*/

}
