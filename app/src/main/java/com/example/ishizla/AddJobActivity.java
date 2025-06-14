package com.example.ishizla;

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


public class AddJobActivity extends AppCompatActivity {
    private EditText editTitle, editDescription, editRequirements, editLocation, editSalary;
    private Button btnPost;
    private Toolbar toolbar;

    private JobDao jobDao;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        // Initialize DAO
        jobDao = new JobDao(this);

        // Get user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post a Job");

        editTitle = findViewById(R.id.edit_job_title);
        editDescription = findViewById(R.id.edit_job_description);
        editRequirements = findViewById(R.id.edit_job_requirements);
        editLocation = findViewById(R.id.edit_job_location);
        editSalary = findViewById(R.id.edit_job_salary);
        btnPost = findViewById(R.id.btn_post_job);

        // Set click listener for post button
        btnPost.setOnClickListener(v -> postJob());
    }

    private void postJob() {
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

        // Create and save new job
        Job job = new Job(title, description, requirements, location, salary, userId);
        long jobId = jobDao.insertJob(job);

        if (jobId > 0) {
            Toast.makeText(this, "Job posted successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to post job", Toast.LENGTH_SHORT).show();
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