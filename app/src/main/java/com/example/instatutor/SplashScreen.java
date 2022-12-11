package com.example.instatutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    private final int DELAY_TIME = 1500;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // If no user is logged on
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // If the user is logged in, send them to the MainActivity, else they need
            // to log in.
            if (isUserLoggedIn()) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class)
                        .putExtra("firebaseUser", mAuth.getCurrentUser()));

            }

            finish();
        }, DELAY_TIME); // The DELAY_TIME allows time for the user to look at the splash screen.
    }

    private boolean isUserLoggedIn() {
        // If getCurrentUser() returns null, the user is not signed in.
        return mAuth.getCurrentUser() != null;
    }
}