package com.example.ishizla.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ishizla.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final String TAG = "UserDao";
    private DatabaseHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
            values.put(DatabaseHelper.COLUMN_USER_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_USER_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.COLUMN_USER_PHONE, user.getPhone());
            values.put(DatabaseHelper.COLUMN_USER_ADDRESS, user.getAddress());
            values.put(DatabaseHelper.COLUMN_USER_TYPE, user.getUserType());

            return db.insert(DatabaseHelper.TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user: " + e.getMessage());
            return -1;
        }
    }

    @SuppressLint("Range")
    public User getUserById(int id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        User user = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PHONE)));
                user.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ADDRESS)));
                user.setUserType(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TYPE)));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    @SuppressLint("Range")
    public User getUserByEmail(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        User user = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_USER_EMAIL + "=?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PHONE)));
                user.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ADDRESS)));
                user.setUserType(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TYPE)));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by email: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    @SuppressLint("Range")
    public User login(String email, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        User user = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_USER_EMAIL + "=? AND " + DatabaseHelper.COLUMN_USER_PASSWORD + "=?",
                    new String[]{email, password},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PHONE)));
                user.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ADDRESS)));
                user.setUserType(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TYPE)));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during login: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
            values.put(DatabaseHelper.COLUMN_USER_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_USER_PHONE, user.getPhone());
            values.put(DatabaseHelper.COLUMN_USER_ADDRESS, user.getAddress());

            return db.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    DatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(user.getId())}
            );
        } catch (Exception e) {
            Log.e(TAG, "Error updating user: " + e.getMessage());
            return 0;
        }
    }

    @SuppressLint("Range")
    public List<User> getAllEmployers() {
        List<User> employers = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_USER_TYPE + "=?",
                    new String[]{String.valueOf(1)},  // 1 for employer
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                    user.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME)));
                    user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMAIL)));
                    user.setPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PHONE)));
                    user.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ADDRESS)));
                    user.setUserType(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_TYPE)));
                    user.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
                    employers.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all employers: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return employers;
    }
}