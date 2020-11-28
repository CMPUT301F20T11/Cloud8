package com.example.booktracker.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.booktracker.R;
import com.example.booktracker.control.Email;
import com.example.booktracker.entities.NotificationCircle;
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
        // ==============save the email if the activity gets killed=============
        if (savedInstanceState != null) {
            userEmail = savedInstanceState.getString("email");
        } else {
            userEmail = getIntent().getStringExtra(EXTRA_MESSAGE);
        }
        // =====================================================================

        getUsername();
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
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController,
                mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        // ======================== nav buttons ========================

        notif = new NotificationCircle(userEmail, findViewById(R.id.hamburger_count), (TextView) navigationView.getMenu().findItem(R.id.nav_incoming).getActionView(), (TextView) navigationView.getMenu().findItem(R.id.nav_accepted).getActionView());
    }

    /**
     * Set up the navigation header
     */
    public void getUsername() {
        FirebaseFirestore.getInstance().collection("users")
                .document(userEmail)
                .get().addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String username = (String) documentSnapshot.get("username");
                        View headerView = navigationView.getHeaderView(0);
                        TextView usernameText = headerView.findViewById(R.id.nav_header_username);
                        usernameText.setText(username);
                        TextView emailText = headerView.findViewById(R.id.nav_header_email);
                        emailText.setText(userEmail);
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
}
