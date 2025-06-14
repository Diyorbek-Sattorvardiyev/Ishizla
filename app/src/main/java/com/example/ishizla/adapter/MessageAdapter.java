package com.example.ishizla.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ishizla.R;
import com.example.ishizla.models.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<Message> messageList;
    private int currentUserId;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat outputFormat;

    public MessageAdapter(Context context, List<Message> messageList, int currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        }

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.textMessage.setText(message.getContent());

        // Format and set time
        String formattedTime = formatTime(message.getCreatedAt());
        holder.textTime.setText(formattedTime);

        // Set sender name for received messages
        if (getItemViewType(position) == VIEW_TYPE_RECEIVED && holder.textSender != null) {
            holder.textSender.setText(message.getSenderName());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId() == currentUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public void updateMessageList(List<Message> newMessageList) {
        this.messageList = newMessageList;
        notifyDataSetChanged();
    }

    // Helper method to format message time
    private String formatTime(String createdAt) {
        try {
            Date date = inputFormat.parse(createdAt);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return createdAt; // Return original string if parsing fails
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textMessage, textTime, textSender;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_message);
            textMessage = itemView.findViewById(R.id.text_message_content);
            textTime = itemView.findViewById(R.id.text_message_time);

            // textSender may not be present in sent message layout
            textSender = itemView.findViewById(R.id.text_message_sender);
        }
    }
}
