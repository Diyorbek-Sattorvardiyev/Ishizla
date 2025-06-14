package com.example.ishizla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ishizla.database.UserDao;
import com.example.ishizla.models.User;


public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private Button btnLogin;
    private TextView textRegister, textForgotPassword;
    private ProgressBar progressBar;

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize DAO
        userDao = new UserDao(this);

        // Initialize views
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.btn_login);
        textRegister = findViewById(R.id.text_register);
        progressBar = findViewById(R.id.progressBar);

        // Optional: If you have a forgot password feature
        // textForgotPassword = findViewById(R.id.text_forgot_password);

        // Set click listeners
        btnLogin.setOnClickListener(v -> attemptLogin());

        textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Optional: Forgot password feature
        /*
        textForgotPassword.setOnClickListener(v -> {
            // Show password reset dialog or navigate to reset password activity
            showForgotPasswordDialog();
        });
        */
    }

    private void attemptLogin() {
        // Reset errors
        editEmail.setError(null);
        editPassword.setError(null);

        // Get values
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Flag to track validation
        boolean cancel = false;
        View focusView = null;

        // Validate password
        if (TextUtils.isEmpty(password)) {
            editPassword.setError(getString(R.string.error_field_required));
            focusView = editPassword;
            cancel = true;
        } else if (password.length() < 4) {
            editPassword.setError(getString(R.string.error_invalid_password));
            focusView = editPassword;
            cancel = true;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            editEmail.setError(getString(R.string.error_field_required));
            focusView = editEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            editEmail.setError(getString(R.string.error_invalid_email));
            focusView = editEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and perform the login attempt
            showProgress(true);

            // Execute login in background thread
            new Thread(() -> {
                // Attempt login
                final User user = userDao.login(email, password);

                // Update UI on main thread
                runOnUiThread(() -> {
                    showProgress(false);

                    if (user != null) {
                        // Save user session
                        saveUserSession(user);

                        // Navigate to home screen
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.error_invalid_login, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }
    }

    private boolean isEmailValid(String email) {
        // Basic email validation
        return email.contains("@") && email.contains(".");
    }

    private void saveUserSession(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putInt("user_id", user.getId());
        editor.putString("user_name", user.getName());
        editor.putString("user_email", user.getEmail());
        editor.putInt("user_type", user.getUserType());
        editor.apply();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }

    /*
    // Optional: Forgot password dialog
    private void showForgotPasswordDialog() {
        // Create dialog for password reset
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.forgot_password);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint(R.string.email);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.reset, (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!TextUtils.isEmpty(email) && isEmailValid(email)) {
                // Send password reset instructions
                Toast.makeText(LoginActivity.this, R.string.password_reset_sent, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }
    */
}