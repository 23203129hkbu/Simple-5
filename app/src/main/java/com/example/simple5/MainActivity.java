package com.example.simple5;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            sendToLogin();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(onNav);

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home_bottom); // Set a default selected item
        }
    }

    private final BottomNavigationView.OnItemSelectedListener onNav = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null;
            if (item.getItemId() == R.id.profile_bottom) {
                selected = new Fragment1();
            } else if (item.getItemId() == R.id.ask_bottom) {
                selected = new Fragment2();
            } else if (item.getItemId() == R.id.queue_bottom) {
                selected = new Fragment3();
            } else if (item.getItemId() == R.id.home_bottom) {
                selected = new Fragment4();
            } else {
                return false;
            }
            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, selected)
                        .commit();
            }
            return true;
        }
    };

    public void logout(View view) {
        auth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}