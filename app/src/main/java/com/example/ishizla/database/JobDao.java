package com.example.ishizla.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ishizla.models.Job;

import java.util.ArrayList;
import java.util.List;

public class JobDao {
    private static final String TAG = "JobDao";
    private DatabaseHelper dbHelper;

    public JobDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insertJob(Job job) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_JOB_TITLE, job.getTitle());
            values.put(DatabaseHelper.COLUMN_JOB_DESCRIPTION, job.getDescription());
            values.put(DatabaseHelper.COLUMN_JOB_REQUIREMENTS, job.getRequirements());
            values.put(DatabaseHelper.COLUMN_JOB_LOCATION, job.getLocation());
            values.put(DatabaseHelper.COLUMN_JOB_SALARY, job.getSalary());
            values.put(DatabaseHelper.COLUMN_JOB_EMPLOYER_ID, job.getEmployerId());

            return db.insert(DatabaseHelper.TABLE_JOBS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting job: " + e.getMessage());
            return -1;
        }
    }

    @SuppressLint("Range")
    public Job getJobById(int id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Job job = null;

        try {
            db = dbHelper.getReadableDatabase();

            String query = "SELECT j.*, u.name as employer_name FROM " + DatabaseHelper.TABLE_JOBS + " j " +
                    "LEFT JOIN " + DatabaseHelper.TABLE_USERS + " u ON j." +
                    DatabaseHelper.COLUMN_JOB_EMPLOYER_ID + " = u." + DatabaseHelper.COLUMN_ID +
                    " WHERE j." + DatabaseHelper.COLUMN_ID + " = ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor != null && cursor.moveToFirst()) {
                job = new Job();
                job.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                job.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_TITLE)));
                job.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_DESCRIPTION)));
                job.setRequirements(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_REQUIREMENTS)));
                job.setLocation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_LOCATION)));
                job.setSalary(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_SALARY)));
                job.setEmployerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_EMPLOYER_ID)));
                job.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
                job.setEmployerName(cursor.getString(cursor.getColumnIndex("employer_name")));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting job by ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return job;
    }

    @SuppressLint("Range")
    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String query = "SELECT j.*, u.name as employer_name FROM " + DatabaseHelper.TABLE_JOBS + " j " +
                    "LEFT JOIN " + DatabaseHelper.TABLE_USERS + " u ON j." +
                    DatabaseHelper.COLUMN_JOB_EMPLOYER_ID + " = u." + DatabaseHelper.COLUMN_ID +
                    " ORDER BY j." + DatabaseHelper.COLUMN_CREATED_AT + " DESC";

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Job job = new Job();
                    job.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                    job.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_TITLE)));
                    job.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_DESCRIPTION)));
                    job.setRequirements(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_REQUIREMENTS)));
                    job.setLocation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_LOCATION)));
                    job.setSalary(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_SALARY)));
                    job.setEmployerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_EMPLOYER_ID)));
                    job.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
                    job.setEmployerName(cursor.getString(cursor.getColumnIndex("employer_name")));
                    jobs.add(job);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all jobs: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return jobs;
    }

    @SuppressLint("Range")
    public List<Job> getJobsByEmployerId(int employerId) {
        List<Job> jobs = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String query = "SELECT j.*, u.name as employer_name FROM " + DatabaseHelper.TABLE_JOBS + " j " +
                    "LEFT JOIN " + DatabaseHelper.TABLE_USERS + " u ON j." +
                    DatabaseHelper.COLUMN_JOB_EMPLOYER_ID + " = u." + DatabaseHelper.COLUMN_ID +
                    " WHERE j." + DatabaseHelper.COLUMN_JOB_EMPLOYER_ID + " = ? " +
                    "ORDER BY j." + DatabaseHelper.COLUMN_CREATED_AT + " DESC";

            cursor = db.rawQuery(query, new String[]{String.valueOf(employerId)});

            if (cursor.moveToFirst()) {
                do {
                    Job job = new Job();
                    job.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                    job.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_TITLE)));
                    job.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_DESCRIPTION)));
                    job.setRequirements(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_REQUIREMENTS)));
                    job.setLocation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_LOCATION)));
                    job.setSalary(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_SALARY)));
                    job.setEmployerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_EMPLOYER_ID)));
                    job.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
                    job.setEmployerName(cursor.getString(cursor.getColumnIndex("employer_name")));
                    jobs.add(job);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting jobs by employer ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return jobs;
    }

    public int updateJob(Job job) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_JOB_TITLE, job.getTitle());
            values.put(DatabaseHelper.COLUMN_JOB_DESCRIPTION, job.getDescription());
            values.put(DatabaseHelper.COLUMN_JOB_REQUIREMENTS, job.getRequirements());
            values.put(DatabaseHelper.COLUMN_JOB_LOCATION, job.getLocation());
            values.put(DatabaseHelper.COLUMN_JOB_SALARY, job.getSalary());

            return db.update(
                    DatabaseHelper.TABLE_JOBS,
                    values,
                    DatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(job.getId())}
            );
        } catch (Exception e) {
            Log.e(TAG, "Error updating job: " + e.getMessage());
            return 0;
        }
    }

    public int deleteJob(int jobId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            return db.delete(
                    DatabaseHelper.TABLE_JOBS,
                    DatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(jobId)}
            );
        } catch (Exception e) {
            Log.e(TAG, "Error deleting job: " + e.getMessage());
            return 0;
        }
    }

    @SuppressLint("Range")
    public List<Job> searchJobs(String keyword) {
        List<Job> jobs = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String query = "SELECT j.*, u.name as employer_name FROM " + DatabaseHelper.TABLE_JOBS + " j " +
                    "LEFT JOIN " + DatabaseHelper.TABLE_USERS + " u ON j." +
                    DatabaseHelper.COLUMN_JOB_EMPLOYER_ID + " = u." + DatabaseHelper.COLUMN_ID +
                    " WHERE j." + DatabaseHelper.COLUMN_JOB_TITLE + " LIKE ? OR " +
                    "j." + DatabaseHelper.COLUMN_JOB_DESCRIPTION + " LIKE ? OR " +
                    "j." + DatabaseHelper.COLUMN_JOB_LOCATION + " LIKE ? " +
                    "ORDER BY j." + DatabaseHelper.COLUMN_CREATED_AT + " DESC";

            String searchParam = "%" + keyword + "%";
            cursor = db.rawQuery(query, new String[]{searchParam, searchParam, searchParam});

            if (cursor.moveToFirst()) {
                do {
                    Job job = new Job();
                    job.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                    job.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_TITLE)));
                    job.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_DESCRIPTION)));
                    job.setRequirements(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_REQUIREMENTS)));
                    job.setLocation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_LOCATION)));
                    job.setSalary(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_SALARY)));
                    job.setEmployerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB_EMPLOYER_ID)));
                    job.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
                    job.setEmployerName(cursor.getString(cursor.getColumnIndex("employer_name")));
                    jobs.add(job);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching jobs: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return jobs;
    }
}