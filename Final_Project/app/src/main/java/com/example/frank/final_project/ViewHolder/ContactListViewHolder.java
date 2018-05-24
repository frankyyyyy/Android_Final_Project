package com.example.frank.final_project.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frank.final_project.Model.Contact;
import com.example.frank.final_project.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.contact_name_Tv)
    TextView mContactName;

    @BindView(R.id.contact_item)
    LinearLayout mContactItem;

    public ContactListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Contact contact){
        if(contact.getOppositeName() != null){
            mContactName.setText(contact.getOppositeName());
        }else{
            mContactName.setText(contact.getOppositeId());
        }
    }

    public LinearLayout getContactItem(){
        return mContactItem;
    }
}
