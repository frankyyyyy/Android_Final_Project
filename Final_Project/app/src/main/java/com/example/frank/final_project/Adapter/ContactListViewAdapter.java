package com.example.frank.final_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frank.final_project.Activity.ChatActivity;
import com.example.frank.final_project.Model.Contact;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.R;
import com.example.frank.final_project.ViewHolder.ContactListViewHolder;
import com.example.frank.final_project.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ContactListViewAdapter extends FirebaseRecyclerAdapter<Contact, ContactListViewHolder> {


    private Context context;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ContactListViewAdapter(@NonNull FirebaseRecyclerOptions<Contact> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ContactListViewHolder holder, int position, @NonNull final Contact model) {
        holder.bind(model);
        holder.getContactItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                CurrentUser.setOppositeId(model.getOppositeId());
                if(model.getOppositeName() != null){
                    CurrentUser.setOppositeName(model.getOppositeName());
                }
                context.startActivity(chatIntent);
            }
        });
    }

    @NonNull
    @Override
    public ContactListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_list, parent, false));
    }
}
