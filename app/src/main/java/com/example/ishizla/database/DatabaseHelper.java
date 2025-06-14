package com.example.ishizla.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jobapp.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;

    // Singleton pattern
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Tables
    public static final String TABLE_USERS = "users";
    public static final String TABLE_JOBS = "jobs";
    public static final String TABLE_RESUMES = "resumes";
    public static final String TABLE_MESSAGES = "messages";

    // Common columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Users Table Columns
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_ADDRESS = "address";
    public static final String COLUMN_USER_TYPE = "user_type"; // 0 for job seeker, 1 for employer

    // Jobs Table Columns
    public static final String COLUMN_JOB_TITLE = "title";
    public static final String COLUMN_JOB_DESCRIPTION = "description";
    public static final String COLUMN_JOB_REQUIREMENTS = "requirements";
    public static final String COLUMN_JOB_LOCATION = "location";
    public static final String COLUMN_JOB_SALARY = "salary";
    public static final String COLUMN_JOB_EMPLOYER_ID = "employer_id";

    // Resumes Table Columns
    public static final String COLUMN_RESUME_USER_ID = "user_id";
    public static final String COLUMN_RESUME_EDUCATION = "education";
    public static final String COLUMN_RESUME_EXPERIENCE = "experience";
    public static final String COLUMN_RESUME_SKILLS = "skills";
    public static final String COLUMN_RESUME_ABOUT = "about";

    // Messages Table Columns
    public static final String COLUMN_MESSAGE_SENDER_ID = "sender_id";
    public static final String COLUMN_MESSAGE_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_MESSAGE_CONTENT = "content";
    public static final String COLUMN_MESSAGE_READ = "is_read";

    // Create table statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT NOT NULL,"
            + COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE,"
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_USER_PHONE + " TEXT,"
            + COLUMN_USER_ADDRESS + " TEXT,"
            + COLUMN_USER_TYPE + " INTEGER NOT NULL,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    private static final String CREATE_TABLE_JOBS = "CREATE TABLE " + TABLE_JOBS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_JOB_TITLE + " TEXT NOT NULL,"
            + COLUMN_JOB_DESCRIPTION + " TEXT NOT NULL,"
            + COLUMN_JOB_REQUIREMENTS + " TEXT NOT NULL,"
            + COLUMN_JOB_LOCATION + " TEXT NOT NULL,"
            + COLUMN_JOB_SALARY + " TEXT,"
            + COLUMN_JOB_EMPLOYER_ID + " INTEGER NOT NULL,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_JOB_EMPLOYER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TABLE_RESUMES = "CREATE TABLE " + TABLE_RESUMES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_RESUME_USER_ID + " INTEGER NOT NULL,"
            + COLUMN_RESUME_EDUCATION + " TEXT NOT NULL,"
            + COLUMN_RESUME_EXPERIENCE + " TEXT NOT NULL,"
            + COLUMN_RESUME_SKILLS + " TEXT NOT NULL,"
            + COLUMN_RESUME_ABOUT + " TEXT NOT NULL,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_RESUME_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MESSAGE_SENDER_ID + " INTEGER NOT NULL,"
            + COLUMN_MESSAGE_RECEIVER_ID + " INTEGER NOT NULL,"
            + COLUMN_MESSAGE_CONTENT + " TEXT NOT NULL,"
            + COLUMN_MESSAGE_READ + " INTEGER DEFAULT 0,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_MESSAGE_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_MESSAGE_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_JOBS);
        db.execSQL(CREATE_TABLE_RESUMES);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESUMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}