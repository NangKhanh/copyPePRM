package com.example.pe;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pe.entity.Contact;
import com.example.pe.helpers.FirebaseDatabaseHelper;

import java.util.List;

public class ContactCardAdapter extends RecyclerView.Adapter<ContactCardAdapter.ContactCardHolder> {

    List<Contact> contacts;
    private IclickItem ilickItem;

    public interface IclickItem{
        void getContact(int id);
    }
    public ContactCardAdapter(List<Contact> contacts , IclickItem iclickItem){
        this.contacts = contacts;
        this.ilickItem = iclickItem;
    }
    @NonNull
    @Override
    public ContactCardAdapter.ContactCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_card,parent,false);
        return new ContactCardHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactCardAdapter.ContactCardHolder holder, int position) {
        int positionTemp = position;
        Contact contact = contacts.get(positionTemp);
        holder.txt_name.setText(contact.firstName + " " + contact.lastName);
        holder.txt_email.setText(contact.email);
        holder.txt_phone.setText(contact.phone);
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ilickItem.getContact(contacts.get(positionTemp).id);
            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int contactId = contacts.get(positionTemp).id;
                Intent intent = new Intent(view.getContext(), ContactDetailActivity.class);
                intent.putExtra("CONTACT_ID", contactId);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactCardHolder extends RecyclerView.ViewHolder {

        TextView txt_name;
        TextView txt_email;
        TextView txt_phone;

        Button btn_edit;
        Button btn_delete;
        ImageView img;
        public ContactCardHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_email = itemView.findViewById(R.id.txt_email);
            txt_phone = itemView.findViewById(R.id.txt_phone);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
