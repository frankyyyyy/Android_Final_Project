package com.example.frank.final_project.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.frank.final_project.Model.Message;
import com.example.frank.final_project.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.*;

/**
 *  Holding message views
 *  providing to chat room message list.
 */
public class MessageListViewHolder extends ViewHolder {

    @BindView(R.id.chat_page_message_container_sender_Tv)
    TextView messageSender;

    @BindView(R.id.chat_page_message_container_time_Tv)
    TextView messageTime;

    @BindView(R.id.chat_page_message_container_content_Tv)
    TextView messageContent;

    public MessageListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Bind attributes to views
     * @param message
     */
    public void bind(Message message){
        // Show sender id if sender has no name
        if(message.getSender() != null){
            messageSender.setText(message.getSender());
        }else{
            messageSender.setText(message.getSenderId());
        }
        messageTime.setText(message.getTime());
        messageContent.setText(message.getContent());
    }
}
