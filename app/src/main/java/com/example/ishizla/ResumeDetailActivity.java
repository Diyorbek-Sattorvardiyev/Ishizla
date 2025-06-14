package com.example.ishizla;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ishizla.database.ResumeDao;
import com.example.ishizla.database.UserDao;
import com.example.ishizla.models.Resume;
import com.example.ishizla.models.User;

public class ResumeDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textName, textEmail, textPhone;
    private TextView textEducation, textExperience, textSkills, textAbout;
    private Button btnContact;

    private ResumeDao resumeDao;
    private UserDao userDao;

    private Resume resume;
    private User user;
    private int resumeId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_detail);

        // Initialize DAOs
        resumeDao = new ResumeDao(this);
        userDao = new UserDao(this);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        textName = findViewById(R.id.text_user_name);
        textEmail = findViewById(R.id.text_user_email);
        textPhone = findViewById(R.id.text_user_phone);
        textEducation = findViewById(R.id.text_education);
        textExperience = findViewById(R.id.text_experience);
        textSkills = findViewById(R.id.text_skills);
        textAbout = findViewById(R.id.text_about);
        btnContact = findViewById(R.id.btn_contact);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.resume_details);

        // Get resume ID or user ID from intent
        resumeId = getIntent().getIntExtra("resume_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        if (resumeId == -1 && userId == -1) {
            Toast.makeText(this, R.string.error_invalid_resume, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load resume details
        loadResumeDetails();
    }

    private void loadResumeDetails() {
        // Get resume details from database
        if (resumeId != -1) {
            resume = resumeDao.getResumeById(resumeId);
        } else if (userId != -1) {
            resume = resumeDao.getResumeByUserId(userId);
        }

        if (resume == null) {
            Toast.makeText(this, R.string.error_resume_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get user details
        user = userDao.getUserById(resume.getUserId());

        if (user == null) {
            Toast.makeText(this, R.string.error_invalid_contact, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set resume details to views
        setupResumeDetails();
    }

    private void setupResumeDetails() {
        // Set user info
        textName.setText(user.getName());
        textEmail.setText(user.getEmail());
        textPhone.setText(user.getPhone());

        // Set resume details
        textEducation.setText(resume.getEducation());
        textExperience.setText(resume.getExperience());
        textSkills.setText(resume.getSkills());
        textAbout.setText(resume.getAbout());

        // Hide contact button if viewing your own resume
        int currentUserId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);
        if (user.getId() == currentUserId) {
            btnContact.setVisibility(View.GONE);
        } else {
            btnContact.setVisibility(View.VISIBLE);
            // Set contact button
            btnContact.setOnClickListener(v -> contactUser());
        }
    }

    private void contactUser() {
        Intent intent = new Intent(ResumeDetailActivity.this, ChatActivity.class);
        intent.putExtra("contact_id", user.getId());
        intent.putExtra("contact_name", user.getName());
        startActivity(intent);
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