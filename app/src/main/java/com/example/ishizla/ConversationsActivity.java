package com.example.ishizla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ishizla.adapter.ConversationAdapter;
import com.example.ishizla.database.MessageDao;
import com.example.ishizla.database.UserDao;
import com.example.ishizla.models.Message;

import java.util.List;

public class ConversationsActivity extends AppCompatActivity implements ConversationAdapter.OnConversationClickListener {
    private RecyclerView recyclerConversations;
    private TextView textNoConversations;
    private Toolbar toolbar;

    private MessageDao messageDao;
    private UserDao userDao;
    private ConversationAdapter conversationAdapter;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        // Initialize DAOs
        messageDao = new MessageDao(this);
        userDao = new UserDao(this);

        // Get user ID from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Messages");

        recyclerConversations = findViewById(R.id.recycler_conversations);
        textNoConversations = findViewById(R.id.text_no_conversations);

        // Setup RecyclerView
        recyclerConversations.setLayoutManager(new LinearLayoutManager(this));

        // Load conversations
        loadConversations();
    }

    private void loadConversations() {
        List<Message> conversationList = messageDao.getUserConversations(userId);

        if (conversationList.isEmpty()) {
            recyclerConversations.setVisibility(View.GONE);
            textNoConversations.setVisibility(View.VISIBLE);
        } else {
            recyclerConversations.setVisibility(View.VISIBLE);
            textNoConversations.setVisibility(View.GONE);

            conversationAdapter = new ConversationAdapter(this, conversationList, userId, this);
            recyclerConversations.setAdapter(conversationAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload conversations when returning to this activity
        loadConversations();
    }

    @Override
    public void onConversationClick(int contactId, String contactName) {
        Intent intent = new Intent(ConversationsActivity.this, ChatActivity.class);
        intent.putExtra("contact_id", contactId);
        intent.putExtra("contact_name", contactName);
        startActivity(intent);
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