package com.example.ishizla;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ishizla.database.UserDao;
import com.example.ishizla.models.User;


public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editName, editEmail, editPassword, editConfirmPassword, editPhone, editAddress;
    private RadioGroup radioUserType;
    private Button btnRegister;
    private TextView textLogin;
    private ProgressBar progressBar;

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize DAO
        userDao = new UserDao(this);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.register);

        // Initialize views
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        editPhone = findViewById(R.id.edit_phone);
        editAddress = findViewById(R.id.edit_address);
        radioUserType = findViewById(R.id.radio_user_type);
        btnRegister = findViewById(R.id.btn_register);
        textLogin = findViewById(R.id.text_login);
        progressBar = findViewById(R.id.progressBar);

        // Set click listeners
        btnRegister.setOnClickListener(v -> attemptRegister());

        textLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptRegister() {
        // Reset errors
        editName.setError(null);
        editEmail.setError(null);
        editPassword.setError(null);
        editConfirmPassword.setError(null);

        // Get values
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        // Get selected user type
        int selectedId = radioUserType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, R.string.error_select_user_type, Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton radioButton = findViewById(selectedId);
        int userType = radioButton.getText().toString().equals(getString(R.string.job_seeker)) ? 0 : 1;

        // Flag to track validation
        boolean cancel = false;
        View focusView = null;

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            editConfirmPassword.setError(getString(R.string.error_field_required));
            focusView = editConfirmPassword;
            cancel = true;
        } else if (!confirmPassword.equals(password)) {
            editConfirmPassword.setError(getString(R.string.error_passwords_dont_match));
            focusView = editConfirmPassword;
            cancel = true;
        }

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

        // Validate name
        if (TextUtils.isEmpty(name)) {
            editName.setError(getString(R.string.error_field_required));
            focusView = editName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and perform the registration attempt
            showProgress(true);

            // Execute registration in background thread
            new Thread(() -> {
                // Check if email already exists
                final User existingUser = userDao.getUserByEmail(email);

                if (existingUser != null) {
                    // Email already exists
                    runOnUiThread(() -> {
                        showProgress(false);
                        editEmail.setError(getString(R.string.error_email_exists));
                        editEmail.requestFocus();
                    });
                    return;
                }

                // Create new user
                User newUser = new User(name, email, password, phone, address, userType);
                final long userId = userDao.insertUser(newUser);

                // Update UI on main thread
                runOnUiThread(() -> {
                    showProgress(false);

                    if (userId > 0) {
                        Toast.makeText(RegisterActivity.this, R.string.registration_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }
    }

    private boolean isEmailValid(String email) {
        // Basic email validation
        return email.contains("@") && email.contains(".");
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}