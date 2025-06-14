package com.example.ishizla;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ishizla.adapter.MessageAdapter;
import com.example.ishizla.database.MessageDao;
import com.example.ishizla.database.UserDao;
import com.example.ishizla.models.Message;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerMessages;
    private EditText editMessage;
    private ImageButton btnSend;
    private TextView textContactName;
    private Toolbar toolbar;

    private MessageDao messageDao;
    private UserDao userDao;
    private MessageAdapter messageAdapter;

    private int userId;
    private int contactId;
    private String contactName;
    private String jobTitle;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize DAOs
        messageDao = new MessageDao(this);
        userDao = new UserDao(this);

        // Get user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Get contact ID and name from intent
        contactId = getIntent().getIntExtra("contact_id", -1);
        contactName = getIntent().getStringExtra("contact_name");
        jobTitle = getIntent().getStringExtra("job_title");

        if (contactId == -1 || contactName == null) {
            Toast.makeText(this, "Invalid contact", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(contactName);

        recyclerMessages = findViewById(R.id.recycler_messages);
        editMessage = findViewById(R.id.edit_message);
        btnSend = findViewById(R.id.btn_send);
        textContactName = findViewById(R.id.text_contact_name);

        textContactName.setText(contactName);

        findViewById(R.id.image_contact).setOnClickListener(v -> openUserProfile());
        textContactName.setOnClickListener(v -> openUserProfile());
        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);  // Messages appear from bottom
        recyclerMessages.setLayoutManager(layoutManager);

        // Load messages
        loadMessages();

        // Mark conversation as read
        messageDao.markConversationAsRead(userId, contactId);

        // Set click listener for send button
        btnSend.setOnClickListener(v -> sendMessage());

        // Auto-populate message if job title is provided (application message)
        if (jobTitle != null && !jobTitle.isEmpty()) {
            editMessage.setText("Salom,  " + jobTitle + " Men bu ishga ariza bermoqchiman.");
        }
    }
    private void openUserProfile() {
        // Agar kontakt ish qidiruvchi bo'lsa (userType = 0)
        com.example.ishizla.models.User user = userDao.getUserById(contactId);
        if (user != null) {
            if (user.getUserType() == 0) { // 0 = ish qidiruvchi
                // Rezyume ochish
                Intent intent = new Intent(ChatActivity.this, ResumeDetailActivity.class);
                intent.putExtra("user_id", contactId);
                startActivity(intent);
            } else {
                // Ish beruvchi bo'lsa oddiy profil yoki uning e'lonlarini ko'rsatish
                Toast.makeText(this, "Bu foydalanuvchi ish beruvchi", Toast.LENGTH_SHORT).show();
                // Yoki ish beruvchi profilini ochish
                // Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                // intent.putExtra("user_id", contactId);
                // startActivity(intent);
            }
        }
    }

    private void loadMessages() {
        List<Message> messageList = messageDao.getConversation(userId, contactId);
        messageAdapter = new MessageAdapter(this, messageList, userId);
        recyclerMessages.setAdapter(messageAdapter);

        if (messageList.size() > 0) {
            recyclerMessages.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    private void sendMessage() {
        String content = editMessage.getText().toString().trim();

        if (content.isEmpty()) {
            return;
        }

        // Create and save new message
        Message message = new Message(userId, contactId, content);
        long messageId = messageDao.insertMessage(message);

        if (messageId > 0) {
            // Clear input field
            editMessage.setText("");

            // Reload messages
            loadMessages();
        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload messages when returning to this activity
        loadMessages();

        // Mark conversation as read
        messageDao.markConversationAsRead(userId, contactId);
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