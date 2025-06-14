package com.example.ishizla;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ishizla.database.ResumeDao;
import com.example.ishizla.models.Resume;


public class CreateResumeActivity extends AppCompatActivity {
    private EditText editEducation, editExperience, editSkills, editAbout;
    private Button btnSave;
    private Toolbar toolbar;

    private ResumeDao resumeDao;

    private int userId;
    private Resume resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_resume);

        // Initialize DAO
        resumeDao = new ResumeDao(this);

        // Get user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Resume");

        editEducation = findViewById(R.id.edit_education);
        editExperience = findViewById(R.id.edit_experience);
        editSkills = findViewById(R.id.edit_skills);
        editAbout = findViewById(R.id.edit_about);
        btnSave = findViewById(R.id.btn_save);

        // Load existing resume if available
        resume = resumeDao.getResumeByUserId(userId);

        if (resume != null) {
            editEducation.setText(resume.getEducation());
            editExperience.setText(resume.getExperience());
            editSkills.setText(resume.getSkills());
            editAbout.setText(resume.getAbout());
        }

        // Set click listener for save button
        btnSave.setOnClickListener(v -> saveResume());
    }

    private void saveResume() {
        String education = editEducation.getText().toString().trim();
        String experience = editExperience.getText().toString().trim();
        String skills = editSkills.getText().toString().trim();
        String about = editAbout.getText().toString().trim();

        // Validate inputs
        if (education.isEmpty()) {
            editEducation.setError("Education is required");
            editEducation.requestFocus();
            return;
        }

        if (experience.isEmpty()) {
            editExperience.setError("Experience is required");
            editExperience.requestFocus();
            return;
        }

        if (skills.isEmpty()) {
            editSkills.setError("Skills are required");
            editSkills.requestFocus();
            return;
        }

        if (about.isEmpty()) {
            editAbout.setError("About section is required");
            editAbout.requestFocus();
            return;
        }

        // Save or update resume
        if (resume == null) {
            // Create new resume
            resume = new Resume(userId, education, experience, skills, about);
            long resumeId = resumeDao.insertResume(resume);

            if (resumeId > 0) {
                Toast.makeText(this, "Resume saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save resume", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing resume
            resume.setEducation(education);
            resume.setExperience(experience);
            resume.setSkills(skills);
            resume.setAbout(about);

            int result = resumeDao.updateResume(resume);

            if (result > 0) {
                Toast.makeText(this, "Resume updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update resume", Toast.LENGTH_SHORT).show();
            }
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