package com.maaksoft.smartshop.fragments;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import java.util.HashSet;

import android.content.Context;

import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.maaksoft.smartshop.model.Contact;

import com.maaksoft.smartshop.messaging.FirebaseMessagingService;

import com.maaksoft.smartshop.service.SmartShopService;

import com.maaksoft.smartshop.R;

public class ContactListsFragment extends Fragment {

    private String phoneNumber = "";

    private Set<String> contactsSet = new HashSet<String>();

    private RelativeLayout contactLoadingRelativeLayout;

    private TableLayout contactListsTbl;
    private TableLayout myContactListsTbl;

    private TextView contactNumberEt;

    private SmartShopService smartShopService;

    public static ContactListsFragment newInstance() {
        return new ContactListsFragment();
    }

    private void deleteContact(View view) {

        Contact contact = (Contact)view.getTag();
        System.out.println("Contact to be deleted: " + contact);
        if (contact != null) {

            long contactId = contact.getContactId();
            if (contactId > 0) {

                smartShopService.deleteContact(getContext(), contactId);

                this.myContactListsTbl.removeView(((TableRow) view.getParent()));
                this.myContactListsTbl.requestLayout();

                if (this.myContactListsTbl.getChildCount() <= 1) {
                    this.myContactListsTbl.setVisibility(View.GONE);
                }

                Toast.makeText(getContext(), (getString(R.string.contact) + " \"" + contact.getName() + "\" " + getString(R.string.my_contact_removed_message)), Toast.LENGTH_LONG).show();

            }

        }

    }

    private void cleanTable() {

        int childCount = this.contactListsTbl.getChildCount();

        //Remove all rows except the first one which is a header row
        if (childCount > 1) {
            this.contactListsTbl.removeViews(1, (childCount - 1));
        }
        this.contactListsTbl.setVisibility(View.GONE);

    }

    private void addContactToTable(Contact contact, TableLayout table) {

        final Context context = getContext();
        final TableRow tableRow = (TableRow) LayoutInflater.from(context).inflate(R.layout.table_row_for_contact_lists, null);
        tableRow.setBackgroundResource(R.drawable.cell_shape);

        int count = table.getChildCount();
        String spaces = "    ";
        String contactInfo = (spaces + (count++) + ". " + contact.getName());
        ((TextView)tableRow.findViewById(R.id.contact_name_attr)).setText(contactInfo);
        ((TextView)tableRow.findViewById(R.id.contact_number_attr)).setText((spaces + spaces + contact.getPhoneNumber()));

        table.addView(tableRow);
        table.requestLayout();

        ImageButton contactActionBtn = tableRow.findViewById(R.id.contactActionBtn);
        contactActionBtn.setTag(contact);

        if (table.getId() == R.id.contact_lists_tbl) {

            contactActionBtn.setContentDescription(getString(R.string.add_my_contact));
            contactActionBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Contact selectedContact = (Contact)view.getTag();
                    if (contactsSet.contains(selectedContact.getKey())) {
                        Toast.makeText(context, ("\"" + selectedContact.getName() + "\" " + getString(R.string.already_added_as_my_contact_message)), Toast.LENGTH_LONG).show();
                    } else {

                        smartShopService.addContact(context, selectedContact);
                        selectedContact = smartShopService.findContactByKey(context, selectedContact.getKey());
                        view.setTag(selectedContact);

                        addContactToTable(selectedContact, myContactListsTbl);
                        contactsSet.add(selectedContact.getKey());

                        myContactListsTbl.setVisibility(View.VISIBLE);

                        Toast.makeText(getContext(), (getString(R.string.contact) + " \"" + selectedContact.getName() + "\" " + getString(R.string.my_contact_added_message)),
                                Toast.LENGTH_LONG).show();

                    }

                }

            });

        } else {

            contactActionBtn.setContentDescription(getString(R.string.remove_my_contact));
            contactsSet.add(contact.getKey());

            contactActionBtn.setImageDrawable(getResources().getDrawable(R.drawable.delete));
            contactActionBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Contact selectedContact = (Contact) view.getTag();
                    deleteContact(view);
                    contactsSet.remove(selectedContact.getKey());

                }

            });

        }

    }

    private void addContactsToTable(List<Contact> contacts, TableLayout table) {

        System.out.println("Contacts size: " + contacts.size());
        if (contacts != null && contacts.size() > 0) {

            for (Contact contact : contacts) {

                if (!contact.getName().equals(Contact.SELF) && !contact.getPhoneNumber().equals(this.phoneNumber)) {
                    addContactToTable(contact, table);
                }

            }

        }

        if (table.getChildCount() <= 1) {
            table.setVisibility(View.GONE);
        } else {
            table.setVisibility(View.VISIBLE);
        }

    }

    private void searchContacts(final String contactNumber) {

        contactLoadingRelativeLayout.setVisibility(View.VISIBLE);
        this.cleanTable();

        Firebase.setAndroidContext(getContext());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(FirebaseMessagingService.CONTACTS_FIREBASE_URL);
        Query query = databaseReference.orderByChild(FirebaseMessagingService.PHONE_NUMBER).startAt(contactNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean noContactsFound = true;
                if (dataSnapshot.getChildrenCount() > 0) {

                    List<Contact> contacts = new LinkedList<Contact>();
                    Iterator<DataSnapshot> dataSnapshotItr = dataSnapshot.getChildren().iterator();
                    while (dataSnapshotItr.hasNext()) {

                        DataSnapshot contactDataSnapshot = dataSnapshotItr.next();

                        System.out.println("Contact Data Snapshot: " + contactDataSnapshot);
                        String name = contactDataSnapshot.child(FirebaseMessagingService.NAME).getValue().toString();
                        String key = contactDataSnapshot.child(FirebaseMessagingService.KEY).getValue().toString();
                        String phoneNumber = contactDataSnapshot.child(FirebaseMessagingService.PHONE_NUMBER).getValue().toString();
                        System.out.println("Via " + ContactListsFragment.class.getSimpleName() + " - phoneNumber: " + phoneNumber + "; Key: " + key);

                        if (!contactsSet.contains(key) && phoneNumber.startsWith(contactNumber)) {
                            contacts.add(new Contact(name, phoneNumber, key));
                        }

                    }

                    if (contacts.size() > 0) {
                        addContactsToTable(contacts, contactListsTbl);
                        noContactsFound = false;
                    }

                }

                contactLoadingRelativeLayout.setVisibility(View.GONE);

                if (noContactsFound) {
                    contactListsTbl.setVisibility(View.GONE);
                    Toast.makeText(getContext(), (getString(R.string.no_contacts_found_against_contact_number_message) + " \"" + contactNumber + "\". " +
                            getString(R.string.enter_number_message)), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contact_lists, container, false);

        this.phoneNumber = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(FirebaseMessagingService.PHONE_NUMBER, this.phoneNumber);

        this.contactNumberEt = view.findViewById(R.id.contact_number_et);
        this.contactLoadingRelativeLayout = view.findViewById(R.id.loading_contacts_info_rl);
        this.contactListsTbl = view.findViewById(R.id.contact_lists_tbl);
        this.myContactListsTbl = view.findViewById(R.id.my_contact_lists_tbl);

        Button searchContactsBtn = view.findViewById(R.id.search_contacts_btn);
        searchContactsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String contactNumber = contactNumberEt.getText().toString().trim();
                if (contactNumber.isEmpty()) {
                    Toast.makeText(getContext(), (getString(R.string.valid_contact_number_message)), Toast.LENGTH_LONG).show();
                } else {
                    searchContacts(contactNumber);
                }

            }

        });

        this.smartShopService = new SmartShopService();
        List<Contact> contacts = this.smartShopService.getContacts(getContext());
        this.addContactsToTable(contacts, this.myContactListsTbl);

        return view;

    }

}