package com.example.ishizla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ishizla.adapter.JobAdapter;
import com.example.ishizla.database.JobDao;
import com.example.ishizla.database.MessageDao;
import com.example.ishizla.models.Job;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerJobs;
    private FloatingActionButton fabAddJob;
    private SearchView searchView;

    private JobDao jobDao;
    private MessageDao messageDao;
    private JobAdapter jobAdapter;

    private int userId;
    private int userType;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize DAOs
        jobDao = new JobDao(this);
        messageDao = new MessageDao(this);

        // Get user info from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        userName = sharedPreferences.getString("user_name", "");
        userType = sharedPreferences.getInt("user_type", 0);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerJobs = findViewById(R.id.recycler_jobs);
        fabAddJob = findViewById(R.id.fab_add_job);

        // Setup drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Update navigation header with user info
        View headerView = navigationView.getHeaderView(0);
        TextView textName = headerView.findViewById(R.id.text_user_name);
        TextView textEmail = headerView.findViewById(R.id.text_user_email);

        textName.setText(userName);
        textEmail.setText(sharedPreferences.getString("user_email", ""));

        // Show/hide menu items based on user type
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_my_jobs).setVisible(userType == 1);  // Show only for employers
        menu.findItem(R.id.nav_create_resume).setVisible(userType == 0);  // Show only for job seekers

        // Setup RecyclerView
        recyclerJobs.setLayoutManager(new LinearLayoutManager(this));

        // Show all jobs initially
        loadJobs();

        // Setup FloatingActionButton
        if (userType == 1) {  // Only employers can post jobs
            fabAddJob.setVisibility(View.VISIBLE);
            fabAddJob.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AddJobActivity.class);
                startActivity(intent);
            });
        } else {
            fabAddJob.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh job list when returning to this activity
        loadJobs();

        // Update notification badge
        updateNotificationBadge();
    }

    private void loadJobs() {
        List<Job> jobList = jobDao.getAllJobs();
        jobAdapter = new JobAdapter(this, jobList);
        recyclerJobs.setAdapter(jobAdapter);
    }

    private void searchJobs(String keyword) {
        List<Job> jobList = jobDao.searchJobs(keyword);
        if (jobAdapter == null) {
            jobAdapter = new JobAdapter(this, jobList);
            recyclerJobs.setAdapter(jobAdapter);
        } else {
            jobAdapter.updateJobList(jobList);
        }
    }

    private void loadMyJobs() {
        List<Job> jobList = jobDao.getJobsByEmployerId(userId);
        if (jobAdapter == null) {
            jobAdapter = new JobAdapter(this, jobList);
            recyclerJobs.setAdapter(jobAdapter);
        } else {
            jobAdapter.updateJobList(jobList);
        }
    }

    private void updateNotificationBadge() {
        int unreadCount = messageDao.getUnreadCount(userId);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_messages);

        if (unreadCount > 0) {
            // You would normally use a badge drawable or library here
            menuItem.setTitle("Messages (" + unreadCount + ")");
        } else {
            menuItem.setTitle("Messages");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchJobs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadJobs();
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Clear user session
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Navigate to login screen
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadJobs();
        } else if (id == R.id.nav_my_jobs) {
            loadMyJobs();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_create_resume) {
            Intent intent = new Intent(HomeActivity.this, CreateResumeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(HomeActivity.this, ConversationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}