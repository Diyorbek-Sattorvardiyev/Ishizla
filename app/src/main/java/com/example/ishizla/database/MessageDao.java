package com.example.ishizla.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.ishizla.models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    private DatabaseHelper dbHelper;

    public MessageDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Insert a new message into the database
     * @param message The message to insert
     * @return The row ID of the newly inserted message, or -1 if an error occurred
     */
    public long insertMessage(Message message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MESSAGE_SENDER_ID, message.getSenderId());
        values.put(DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID, message.getReceiverId());
        values.put(DatabaseHelper.COLUMN_MESSAGE_CONTENT, message.getContent());
        values.put(DatabaseHelper.COLUMN_MESSAGE_READ, message.getIsRead());

        long id = db.insert(DatabaseHelper.TABLE_MESSAGES, null, values);
        db.close();
        return id;
    }

    /**
     * Get a conversation between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return List of messages between the two users, ordered by creation time
     */
    @SuppressLint("Range")
    public List<Message> getConversation(int userId1, int userId2) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT m.*, u.name as sender_name FROM " + DatabaseHelper.TABLE_MESSAGES + " m " +
                "LEFT JOIN " + DatabaseHelper.TABLE_USERS + " u ON m." +
                DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + " = u." + DatabaseHelper.COLUMN_ID +
                " WHERE (m." + DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + " = ? AND m." +
                DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID + " = ?) OR " +
                "(m." + DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + " = ? AND m." +
                DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID + " = ?) " +
                "ORDER BY m." + DatabaseHelper.COLUMN_CREATED_AT + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId1), String.valueOf(userId2),
                String.valueOf(userId2), String.valueOf(userId1)
        });

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                message.setSenderId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_SENDER_ID)));
                message.setReceiverId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID)));
                message.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_CONTENT)));
                message.setIsRead(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_READ)));
                message.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
                message.setSenderName(cursor.getString(cursor.getColumnIndex("sender_name")));
                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    /**
     * Get all conversations for a user (latest message from each contact)
     * @param userId The user ID
     * @return List of latest messages from each conversation
     */
    @SuppressLint("Range")
    public List<Message> getUserConversations(int userId) {
        List<Message> conversations = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the latest message from each conversation
        String query = "SELECT m1.*, u.name as sender_name FROM " + DatabaseHelper.TABLE_MESSAGES + " m1 " +
                "LEFT JOIN " + DatabaseHelper.TABLE_USERS + " u ON " +
                "CASE WHEN m1." + DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + " = ? " +
                "THEN m1." + DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID + " " +
                "ELSE m1." + DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + " END = u." + DatabaseHelper.COLUMN_ID + " " +
                "INNER JOIN ( " +
                "    SELECT MAX(" + DatabaseHelper.COLUMN_ID + ") as max_id, " +
                "    CASE WHEN " + DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + " = ? " +
                "    THEN " + DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID + " " +
                "    ELSE " + DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + " END as contact_id " +
                "    FROM " + DatabaseHelper.TABLE_MESSAGES + " " +
                "    WHERE " + DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + " = ? " +
                "    OR " + DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID + " = ? " +
                "    GROUP BY contact_id " +
                ") m2 ON m1." + DatabaseHelper.COLUMN_ID + " = m2.max_id " +
                "ORDER BY m1." + DatabaseHelper.COLUMN_CREATED_AT + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId),
                String.valueOf(userId),
                String.valueOf(userId),
                String.valueOf(userId)
        });

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                message.setSenderId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_SENDER_ID)));
                message.setReceiverId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID)));
                message.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_CONTENT)));
                message.setIsRead(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_READ)));
                message.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
                message.setSenderName(cursor.getString(cursor.getColumnIndex("sender_name")));
                conversations.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return conversations;
    }

    /**
     * Mark a message as read
     * @param messageId The message ID
     * @return Number of rows affected
     */
    public int markAsRead(int messageId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MESSAGE_READ, 1);

        int rowsAffected = db.update(
                DatabaseHelper.TABLE_MESSAGES,
                values,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(messageId)}
        );
        db.close();
        return rowsAffected;
    }

    /**
     * Mark all messages in a conversation as read
     * @param userId The user ID (receiver)
     * @param contactId The contact ID (sender)
     * @return Number of rows affected
     */
    public int markConversationAsRead(int userId, int contactId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MESSAGE_READ, 1);

        int rowsAffected = db.update(
                DatabaseHelper.TABLE_MESSAGES,
                values,
                DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + "=? AND " +
                        DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID + "=? AND " +
                        DatabaseHelper.COLUMN_MESSAGE_READ + "=?",
                new String[]{String.valueOf(contactId), String.valueOf(userId), String.valueOf(0)}
        );
        db.close();
        return rowsAffected;
    }

    /**
     * Count unread messages for a user
     * @param userId The user ID
     * @return Number of unread messages
     */
    @SuppressLint("Range")
    public int getUnreadCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_MESSAGES,
                new String[]{"COUNT(*) as count"},
                DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID + "=? AND " +
                        DatabaseHelper.COLUMN_MESSAGE_READ + "=?",
                new String[]{String.valueOf(userId), String.valueOf(0)},
                null,
                null,
                null
        );

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
            cursor.close();
        }
        db.close();
        return count;
    }

    /**
     * Delete a message
     * @param messageId The message ID
     * @return Number of rows affected
     */
    public int deleteMessage(int messageId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(
                DatabaseHelper.TABLE_MESSAGES,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(messageId)}
        );
        db.close();
        return rowsAffected;
    }

    /**
     * Delete an entire conversation between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Number of rows affected
     */
    public int deleteConversation(int userId1, int userId2) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(
                DatabaseHelper.TABLE_MESSAGES,
                "(" + DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + "=? AND " +
                        DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID + "=?) OR (" +
                        DatabaseHelper.COLUMN_MESSAGE_SENDER_ID + "=? AND " +
                        DatabaseHelper.COLUMN_MESSAGE_RECEIVER_ID + "=?)",
                new String[]{
                        String.valueOf(userId1), String.valueOf(userId2),
                        String.valueOf(userId2), String.valueOf(userId1)
                }
        );
        db.close();
        return rowsAffected;
    }
}