package com.example.ishizla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


import com.example.ishizla.database.JobDao;
import com.example.ishizla.database.UserDao;
import com.example.ishizla.models.Job;
import com.example.ishizla.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class JobDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textTitle, textCompany, textLocation, textSalary, textDescription, textRequirements;
    private TextView textDatePosted, textEmployerEmail, textEmployerPhone;
    private Button btnApply, btnEdit, btnDelete;

    private JobDao jobDao;
    private UserDao userDao;

    private Job job;
    private User employer;
    private int userId;
    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        // Initialize DAOs
        jobDao = new JobDao(this);
        userDao = new UserDao(this);

        // Get user info from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        userType = sharedPreferences.getInt("user_type", 0);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        textTitle = findViewById(R.id.text_job_title);
        textCompany = findViewById(R.id.text_company_name);
        textLocation = findViewById(R.id.text_job_location);
        textSalary = findViewById(R.id.text_job_salary);
        textDescription = findViewById(R.id.text_job_description);
        textRequirements = findViewById(R.id.text_job_requirements);
        textDatePosted = findViewById(R.id.text_date_posted);
        textEmployerEmail = findViewById(R.id.text_employer_email);
        textEmployerPhone = findViewById(R.id.text_employer_phone);

        btnApply = findViewById(R.id.btn_apply);
        btnEdit = findViewById(R.id.btn_edit_job);
        btnDelete = findViewById(R.id.btn_delete_job);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get job ID from intent
        int jobId = getIntent().getIntExtra("job_id", -1);

        if (jobId == -1) {
            Toast.makeText(this, R.string.error_invalid_job, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load job details in background
        loadJobDetails(jobId);
    }

    private void loadJobDetails(int jobId) {
        new Thread(() -> {
            // Get job details from database
            job = jobDao.getJobById(jobId);

            // Update UI on main thread
            runOnUiThread(() -> {
                if (job == null) {
                    Toast.makeText(this, R.string.error_job_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Set job details to views
                setupJobDetails();

                // Load employer details in background
                loadEmployerDetails(job.getEmployerId());
            });
        }).start();
    }

    private void loadEmployerDetails(int employerId) {
        new Thread(() -> {
            // Get employer details from database
            employer = userDao.getUserById(employerId);

            // Update UI on main thread
            runOnUiThread(() -> {
                if (employer != null) {
                    // Set employer details to views
                    textCompany.setText(employer.getName());
                    textEmployerEmail.setText(employer.getEmail());
                    textEmployerPhone.setText(employer.getPhone());
                }

                // Set visibility of action buttons based on user type
                setupActionButtons();
            });
        }).start();
    }

    private void setupJobDetails() {
        getSupportActionBar().setTitle(job.getTitle());

        textTitle.setText(job.getTitle());
        textLocation.setText(job.getLocation());

        // Set salary if available
        if (job.getSalary() != null && !job.getSalary().isEmpty()) {
            textSalary.setText(job.getSalary());
        } else {
            textSalary.setText(R.string.salary_not_specified);
        }

        // Set description
        textDescription.setText(job.getDescription());

        // Set requirements
        textRequirements.setText(job.getRequirements());

        // Format and set date posted
        textDatePosted.setText(getPostedDateText(job.getCreatedAt()));
    }

    private void setupActionButtons() {
        // Show/hide buttons based on user type
        if (userType == 0) {  // Job seeker
            btnApply.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        } else {  // Employer
            btnApply.setVisibility(View.GONE);

            // Only show edit/delete buttons if this job belongs to current user
            if (job.getEmployerId() == userId) {
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }

        // Set click listeners
        btnApply.setOnClickListener(v -> applyForJob());
        btnEdit.setOnClickListener(v -> editJob());
        btnDelete.setOnClickListener(v -> confirmDeleteJob());
    }

    private void applyForJob() {
        // Start chat with employer
        Intent intent = new Intent(JobDetailActivity.this, ChatActivity.class);
        intent.putExtra("contact_id", job.getEmployerId());
        intent.putExtra("contact_name", employer.getName());
        intent.putExtra("job_title", job.getTitle());
        startActivity(intent);
    }

    private void editJob() {
        Intent intent = new Intent(JobDetailActivity.this, EditJobActivity.class);
        intent.putExtra("job_id", job.getId());
        startActivity(intent);
    }

    private void confirmDeleteJob() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_job)
                .setMessage(R.string.confirm_delete_job)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    deleteJob();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteJob() {
        new Thread(() -> {
            int result = jobDao.deleteJob(job.getId());

            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(this, R.string.job_deleted_successfully, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.error_deleting_job, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    // Helper method to format the posted date text
    private String getPostedDateText(String createdAt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        try {
            Date postedDate = dateFormat.parse(createdAt);
            Date currentDate = new Date();

            long diffInMillis = currentDate.getTime() - postedDate.getTime();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

            String dateFormatted = displayFormat.format(postedDate);

            if (diffInDays == 0) {
                return getString(R.string.posted_today_format, dateFormatted);
            } else if (diffInDays == 1) {
                return getString(R.string.posted_yesterday_format, dateFormatted);
            } else {
                return getString(R.string.posted_days_ago_format, diffInDays, dateFormatted);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return getString(R.string.recently_posted);
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

    @Override
    protected void onResume() {
        super.onResume();

        // Reload job details when returning to this activity (in case it was edited)
        int jobId = getIntent().getIntExtra("job_id", -1);
        if (jobId != -1) {
            loadJobDetails(jobId);
        }
    }
}