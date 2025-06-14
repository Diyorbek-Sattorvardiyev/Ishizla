package com.example.ishizla.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ishizla.R;
import com.example.ishizla.models.Message;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private Context context;
    private List<Message> conversationList;
    private int currentUserId;
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(int contactId, String contactName);
    }

    public ConversationAdapter(Context context, List<Message> conversationList, int currentUserId, OnConversationClickListener listener) {
        this.context = context;
        this.conversationList = conversationList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Message lastMessage = conversationList.get(position);

        // Determine contact ID and name
        int contactId;
        String contactName;

        if (lastMessage.getSenderId() == currentUserId) {
            contactId = lastMessage.getReceiverId();
            contactName = "To: " + getContactNameFromMessage(lastMessage);
        } else {
            contactId = lastMessage.getSenderId();
            contactName = lastMessage.getSenderName();
        }

        holder.textContactName.setText(contactName);
        holder.textLastMessage.setText(lastMessage.getContent());
        holder.textTime.setText(formatDateTime(lastMessage.getCreatedAt()));

        // Highlight unread messages
        if (lastMessage.getSenderId() != currentUserId && lastMessage.getIsRead() == 0) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorUnreadBackground));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorCardBackground));
        }

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            listener.onConversationClick(contactId, contactName);
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    // Helper method to get contact name from message
    private String getContactNameFromMessage(Message message) {
        // Normally, you would query the database for the user name
        // But for simplicity, we'll just display "Recipient"
        return "Recipient";
    }

    // Helper method to format date time
    private String formatDateTime(String dateTime) {
        // You can implement a proper date formatting here
        // For simplicity, we're just returning the datetime string
        return dateTime;
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textContactName, textLastMessage, textTime;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_conversation);
            textContactName = itemView.findViewById(R.id.text_contact_name);
            textLastMessage = itemView.findViewById(R.id.text_last_message);
            textTime = itemView.findViewById(R.id.text_message_time);
        }
    }
}
