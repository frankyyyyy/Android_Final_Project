package com.example.frank.final_project.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.frank.final_project.Model.Message;
import com.example.frank.final_project.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by Frank on 2018/5/18.
 */

public class MessageListViewHolder extends ViewHolder {

    @BindView(R.id.chat_page_message_container_sender_Tv)
    TextView mMessageSender;

    @BindView(R.id.chat_page_message_container_time_Tv)
    TextView mMessageTime;

    @BindView(R.id.chat_page_message_container_content_Tv)
    TextView mMessageContent;

    public MessageListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Message message){
        mMessageSender.setText(message.getSender());
        mMessageTime.setText(message.getTime());
        mMessageContent.setText(message.getContent());
    }
}
