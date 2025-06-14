package com.example.ishizla.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.ishizla.models.Resume;
import com.example.ishizla.models.User;

import java.util.ArrayList;
import java.util.List;

public class ResumeDao {
    private DatabaseHelper dbHelper;

    public ResumeDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Insert a new resume into the database
     * @param resume The resume to insert
     * @return The row ID of the newly inserted resume, or -1 if an error occurred
     */
    public long insertResume(Resume resume) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RESUME_USER_ID, resume.getUserId());
        values.put(DatabaseHelper.COLUMN_RESUME_EDUCATION, resume.getEducation());
        values.put(DatabaseHelper.COLUMN_RESUME_EXPERIENCE, resume.getExperience());
        values.put(DatabaseHelper.COLUMN_RESUME_SKILLS, resume.getSkills());
        values.put(DatabaseHelper.COLUMN_RESUME_ABOUT, resume.getAbout());

        long id = db.insert(DatabaseHelper.TABLE_RESUMES, null, values);
        db.close();
        return id;
    }

    /**
     * Get a resume by its ID
     * @param id The resume ID
     * @return The resume, or null if not found
     */
    public Resume getResumeById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RESUMES,
                null,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Resume resume = null;
        if (cursor != null && cursor.moveToFirst()) {
            resume = cursorToResume(cursor);
            cursor.close();
        }
        db.close();
        return resume;
    }

    /**
     * Get the latest resume for a specific user
     * @param userId The user ID
     * @return The resume, or null if not found
     */
    public Resume getResumeByUserId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RESUMES,
                null,
                DatabaseHelper.COLUMN_RESUME_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC",
                "1" // Limit to latest resume
        );

        Resume resume = null;
        if (cursor != null && cursor.moveToFirst()) {
            resume = cursorToResume(cursor);
            cursor.close();
        }
        db.close();
        return resume;
    }

    /**
     * Get all resumes for a specific user, ordered by creation time (newest first)
     * @param userId The user ID
     * @return List of resumes
     */
    public List<Resume> getAllResumesByUserId(int userId) {
        List<Resume> resumes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RESUMES,
                null,
                DatabaseHelper.COLUMN_RESUME_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                Resume resume = cursorToResume(cursor);
                resumes.add(resume);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resumes;
    }

    /**
     * Update an existing resume
     * @param resume The resume to update
     * @return Number of rows affected
     */
    public int updateResume(Resume resume) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RESUME_EDUCATION, resume.getEducation());
        values.put(DatabaseHelper.COLUMN_RESUME_EXPERIENCE, resume.getExperience());
        values.put(DatabaseHelper.COLUMN_RESUME_SKILLS, resume.getSkills());
        values.put(DatabaseHelper.COLUMN_RESUME_ABOUT, resume.getAbout());

        int rowsAffected = db.update(
                DatabaseHelper.TABLE_RESUMES,
                values,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(resume.getId())}
        );
        db.close();
        return rowsAffected;
    }

    /**
     * Delete a resume by its ID
     * @param resumeId The resume ID
     * @return Number of rows affected
     */
    public int deleteResume(int resumeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(
                DatabaseHelper.TABLE_RESUMES,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(resumeId)}
        );
        db.close();
        return rowsAffected;
    }

    /**
     * Delete all resumes for a specific user
     * @param userId The user ID
     * @return Number of rows affected
     */
    public int deleteAllResumesByUserId(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(
                DatabaseHelper.TABLE_RESUMES,
                DatabaseHelper.COLUMN_RESUME_USER_ID + "=?",
                new String[]{String.valueOf(userId)}
        );
        db.close();
        return rowsAffected;
    }

    /**
     * Get a list of all resumes with user information
     * Useful for employers to browse through job seekers' resumes
     * @return List of resumes with user information
     */
    @SuppressLint("Range")
    public List<Resume> getAllResumesWithUserInfo() {
        List<Resume> resumes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT r.*, u.name as user_name, u.email as user_email, u.phone as user_phone " +
                "FROM " + DatabaseHelper.TABLE_RESUMES + " r " +
                "JOIN " + DatabaseHelper.TABLE_USERS + " u ON r." +
                DatabaseHelper.COLUMN_RESUME_USER_ID + " = u." + DatabaseHelper.COLUMN_ID + " " +
                "WHERE u." + DatabaseHelper.COLUMN_USER_TYPE + " = 0 " + // 0 for job seekers
                "GROUP BY r." + DatabaseHelper.COLUMN_RESUME_USER_ID + " " + // Get latest resume for each user
                "ORDER BY r." + DatabaseHelper.COLUMN_CREATED_AT + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Resume resume = cursorToResume(cursor);

                // Add user information
                User user = new User();
                user.setId(resume.getUserId());
                user.setName(cursor.getString(cursor.getColumnIndex("user_name")));
                user.setEmail(cursor.getString(cursor.getColumnIndex("user_email")));
                user.setPhone(cursor.getString(cursor.getColumnIndex("user_phone")));

                // You could set the user object to the resume if you add a user field to the Resume class
                // resume.setUser(user);

                resumes.add(resume);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resumes;
    }

    /**
     * Search resumes by keyword
     * @param keyword The keyword to search for in education, experience, skills, or about
     * @return List of matching resumes
     */
    @SuppressLint("Range")
    public List<Resume> searchResumes(String keyword) {
        List<Resume> resumes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT r.*, u.name as user_name FROM " + DatabaseHelper.TABLE_RESUMES + " r " +
                "JOIN " + DatabaseHelper.TABLE_USERS + " u ON r." +
                DatabaseHelper.COLUMN_RESUME_USER_ID + " = u." + DatabaseHelper.COLUMN_ID + " " +
                "WHERE u." + DatabaseHelper.COLUMN_USER_TYPE + " = 0 " + // 0 for job seekers
                "AND (r." + DatabaseHelper.COLUMN_RESUME_EDUCATION + " LIKE ? OR " +
                "r." + DatabaseHelper.COLUMN_RESUME_EXPERIENCE + " LIKE ? OR " +
                "r." + DatabaseHelper.COLUMN_RESUME_SKILLS + " LIKE ? OR " +
                "r." + DatabaseHelper.COLUMN_RESUME_ABOUT + " LIKE ?) " +
                "GROUP BY r." + DatabaseHelper.COLUMN_RESUME_USER_ID + " " + // Get latest resume for each user
                "ORDER BY r." + DatabaseHelper.COLUMN_CREATED_AT + " DESC";

        String searchParam = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{
                searchParam, searchParam, searchParam, searchParam
        });

        if (cursor.moveToFirst()) {
            do {
                Resume resume = cursorToResume(cursor);
                resume.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                resumes.add(resume);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resumes;
    }

    /**
     * Convert cursor to Resume object
     * @param cursor The cursor to convert
     * @return The Resume object
     */
    @SuppressLint("Range")
    private Resume cursorToResume(Cursor cursor) {
        Resume resume = new Resume();
        resume.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        resume.setUserId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESUME_USER_ID)));
        resume.setEducation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESUME_EDUCATION)));
        resume.setExperience(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESUME_EXPERIENCE)));
        resume.setSkills(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESUME_SKILLS)));
        resume.setAbout(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESUME_ABOUT)));
        resume.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));

        // Get user name if available in the cursor
        if (cursor.getColumnIndex("user_name") != -1) {
            resume.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
        }

        return resume;
    }
}
