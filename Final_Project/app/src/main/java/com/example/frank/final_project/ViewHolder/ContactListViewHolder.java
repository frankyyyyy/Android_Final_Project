package com.example.frank.final_project.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frank.final_project.Model.Contact;
import com.example.frank.final_project.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *  Holding contact views
 *  providing to contact list.
 */
public class ContactListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.contact_name_Tv)
    TextView contactName;

    @BindView(R.id.contact_item)
    LinearLayout contactItem;

    public ContactListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Bind attributes to views
     * @param contact
     */
    public void bind(Contact contact){
        if(contact.getOppositeName() != null){
            contactName.setText(contact.getOppositeName());
        }else{
            contactName.setText(contact.getOppositeId());
        }
    }

    /**
     *  Return contact view to adapter
     * @return contactItem
     */
    public LinearLayout getContactItem(){
        return contactItem;
    }
}
