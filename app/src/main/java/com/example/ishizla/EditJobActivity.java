package com.example.ishizla;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ishizla.database.JobDao;
import com.example.ishizla.models.Job;


public class EditJobActivity extends AppCompatActivity {
    private EditText editTitle, editDescription, editRequirements, editLocation, editSalary;
    private Button btnUpdate;
    private Toolbar toolbar;

    private JobDao jobDao;

    private int userId;
    private int jobId;
    private Job job;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        // Initialize DAO
        jobDao = new JobDao(this);

        // Get user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Get job ID from intent
        jobId = getIntent().getIntExtra("job_id", -1);

        if (jobId == -1) {
            Toast.makeText(this, "Invalid job", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get job details from database
        job = jobDao.getJobById(jobId);

        if (job == null) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Make sure this job belongs to current user
        if (job.getEmployerId() != userId) {
            Toast.makeText(this, "You can only edit your own jobs", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Job");

        editTitle = findViewById(R.id.edit_job_title);
        editDescription = findViewById(R.id.edit_job_description);
        editRequirements = findViewById(R.id.edit_job_requirements);
        editLocation = findViewById(R.id.edit_job_location);
        editSalary = findViewById(R.id.edit_job_salary);
        btnUpdate = findViewById(R.id.btn_update_job);

        // Set job details to views
        editTitle.setText(job.getTitle());
        editDescription.setText(job.getDescription());
        editRequirements.setText(job.getRequirements());
        editLocation.setText(job.getLocation());
        editSalary.setText(job.getSalary());

        // Set click listener for update button
        btnUpdate.setOnClickListener(v -> updateJob());
    }

    private void updateJob() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String requirements = editRequirements.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String salary = editSalary.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty()) {
            editTitle.setError("Title is required");
            editTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            editDescription.setError("Description is required");
            editDescription.requestFocus();
            return;
        }

        if (requirements.isEmpty()) {
            editRequirements.setError("Requirements are required");
            editRequirements.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            editLocation.setError("Location is required");
            editLocation.requestFocus();
            return;
        }

        // Update job
        job.setTitle(title);
        job.setDescription(description);
        job.setRequirements(requirements);
        job.setLocation(location);
        job.setSalary(salary);

        int result = jobDao.updateJob(job);

        if (result > 0) {
            Toast.makeText(this, "Job updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update job", Toast.LENGTH_SHORT).show();
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