package com.example.frank.final_project.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.frank.final_project.Model.Message;
import com.example.frank.final_project.R;
import com.example.frank.final_project.ViewHolder.MessageListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

/**
 * Created by Frank on 2018/5/18.
 */

public class MessageListViewAdapter extends FirebaseRecyclerAdapter<Message, MessageListViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessageListViewAdapter(@NonNull FirebaseRecyclerOptions<Message> options, Context context) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageListViewHolder holder, int position, @NonNull Message message) {
        holder.bind(message);
    }

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_page_message_container, parent, false));
    }
}
