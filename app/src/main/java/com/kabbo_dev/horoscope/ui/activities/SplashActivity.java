package com.kabbo_dev.horoscope.ui.activities;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kabbo_dev.horoscope.R;
import com.kabbo_dev.horoscope.utils.Functions;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(() -> {
            if (currentUser == null) {
                Functions.startIntent(SplashActivity.this, RegisterActivity.class, true);
            } else {
                Functions.startIntent(SplashActivity.this, MainActivity.class, true);
            }
        }, 3000);
    }

}