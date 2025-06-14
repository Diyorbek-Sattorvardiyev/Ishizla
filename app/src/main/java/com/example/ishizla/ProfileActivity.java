package com.example.ishizla;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ishizla.database.ResumeDao;
import com.example.ishizla.database.UserDao;
import com.example.ishizla.models.Resume;
import com.example.ishizla.models.User;


public class ProfileActivity extends AppCompatActivity {
    private EditText editName, editEmail, editPhone, editAddress;
    private TextView textUserType, textEducation, textExperience, textSkills, textAbout;
    private Button btnSave;
    private Toolbar toolbar;

    private UserDao userDao;
    private ResumeDao resumeDao;

    private int userId;
    private User user;
    private Resume resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize DAOs
        userDao = new UserDao(this);
        resumeDao = new ResumeDao(this);

        // Get user info from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Get user details from database
        user = userDao.getUserById(userId);

        if (user == null) {
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        editAddress = findViewById(R.id.edit_address);
        textUserType = findViewById(R.id.text_user_type);
        textEducation = findViewById(R.id.text_education);
        textExperience = findViewById(R.id.text_experience);
        textSkills = findViewById(R.id.text_skills);
        textAbout = findViewById(R.id.text_about);
        btnSave = findViewById(R.id.btn_save);

        // Set user details to views
        editName.setText(user.getName());
        editEmail.setText(user.getEmail());
        editPhone.setText(user.getPhone());
        editAddress.setText(user.getAddress());
        textUserType.setText(user.getUserType() == 0 ? "Job Seeker" : "Employer");

        // If user is a job seeker, load resume
        if (user.getUserType() == 0) {
            resume = resumeDao.getResumeByUserId(userId);

            if (resume != null) {
                textEducation.setText(resume.getEducation());
                textExperience.setText(resume.getExperience());
                textSkills.setText(resume.getSkills());
                textAbout.setText(resume.getAbout());
            }
        }

        // Set click listener for save button
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            editName.setError("Name is required");
            editName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            editEmail.requestFocus();
            return;
        }

        // Update user
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        int result = userDao.updateUser(user);

        if (result > 0) {
            // Update shared preferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_name", user.getName());
            editor.putString("user_email", user.getEmail());
            editor.apply();

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
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