package com.example.ishizla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ishizla.database.DatabaseHelper;


public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_TIME = 2500; // 2.5 seconds

    private ImageView imageView;
    private TextView textView;
    private ProgressBar progressBar;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Apply animations
        imageView.startAnimation(fadeIn);
        textView.startAnimation(fadeIn);

        // Initialize the database in background
        new Thread(() -> {
            // This will create the database if it doesn't exist
            databaseHelper.getWritableDatabase();

            // Check if user is already logged in
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

            // Delayed navigation
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;

                if (isLoggedIn) {
                    intent = new Intent(MainActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, SPLASH_DISPLAY_TIME);
        }).start();
    }
}