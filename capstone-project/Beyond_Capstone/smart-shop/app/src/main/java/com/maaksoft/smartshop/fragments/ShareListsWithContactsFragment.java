package com.maaksoft.smartshop.fragments;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.maaksoft.smartshop.model.Contact;
import com.maaksoft.smartshop.model.ShopList;

import com.maaksoft.smartshop.service.SmartShopService;

import com.maaksoft.smartshop.R;

public class ShareListsWithContactsFragment extends Fragment {

    private RelativeLayout contactLoadingRelativeLayout;

    private TableLayout myContactListsTbl;

    private CheckBox allContactsCb;

    private Button shareListBtn;

    private ShopList shopList;

    private List<Long> contactIds;

    private SmartShopService smartShopService;

    public static ShareListsWithContactsFragment newInstance() {
        return new ShareListsWithContactsFragment();
    }

    private boolean hasContact(Long contactId) {
        return (this.contactIds != null && this.contactIds.contains(contactId));
    }

    private void addContactToTable(Contact contact) {

        final Context context = getContext();
        final TableRow tableRow = (TableRow)LayoutInflater.from(context).inflate(R.layout.table_row_for_sharing_with_contacts, null);
        tableRow.setBackgroundResource(R.drawable.cell_shape);

        String spaces = "    ";
        String contactInfo = (spaces + this.myContactListsTbl.getChildCount() + ". " + contact.getName());
        ((TextView)tableRow.findViewById(R.id.contact_name_attr)).setText(contactInfo);
        ((TextView)tableRow.findViewById(R.id.contact_number_attr)).setText((spaces + spaces + contact.getPhoneNumber()));

        CheckBox contactCb = tableRow.findViewById(R.id.contact_cb);
        contactCb.setTag(contact);
        contactCb.setChecked(this.hasContact(contact.getContactId()));

        this.myContactListsTbl.addView(tableRow);
        this.myContactListsTbl.requestLayout();

    }

    private void addContactsToTable(List<Contact> contacts) {

        System.out.println("Contacts size: " + contacts.size());
        if (contacts != null && contacts.size() > 0) {

            this.contactIds = this.smartShopService.getContactIdsByShopList(getContext(), this.shopList.getShopListId());
            for (Contact contact : contacts) {

                if (!contact.getName().equals(Contact.SELF)) {
                    this.addContactToTable(contact);
                }

            }

        }

        if (this.myContactListsTbl.getChildCount() <= 1) {
            this.myContactListsTbl.setVisibility(View.GONE);
        } else {
            this.myContactListsTbl.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.share_list_with_contacts, container, false);

        this.smartShopService = new SmartShopService();

        this.contactLoadingRelativeLayout = view.findViewById(R.id.loading_contacts_info_rl);
        this.myContactListsTbl = view.findViewById(R.id.my_contact_lists_tbl);

        this.allContactsCb = view.findViewById(R.id.all_contacts_cb);
        this.allContactsCb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int childCount = myContactListsTbl.getChildCount();
                if (childCount > 1) {

                    for (int index = 1; index < childCount; index++) {

                        View tableRow = myContactListsTbl.getChildAt(index);
                        CheckBox contactCb = tableRow.findViewById(R.id.contact_cb);
                        contactCb.setChecked(allContactsCb.isChecked());

                    }

                }

            }

        });

        this.shareListBtn = view.findViewById(R.id.share_list_btn);
        this.shareListBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                contactLoadingRelativeLayout.setVisibility(View.VISIBLE);

                if (shopList != null) {

                    int childCount = myContactListsTbl.getChildCount();
                    if (childCount > 1) {

                        List<Contact> contacts = new LinkedList<Contact>();
                        for (int index = 1; index < childCount; index++) {

                            View tableRow = myContactListsTbl.getChildAt(index);
                            CheckBox contactCb = tableRow.findViewById(R.id.contact_cb);

                            if (contactCb.isChecked()) {

                                Contact contact = (Contact) contactCb.getTag();
                                if (contact != null) {
                                    contacts.add(contact);
                                }

                            }

                        }

                        smartShopService.deleteContactsByShopList(getContext(), shopList.getShopListId());
                        String info = smartShopService.shareShopListWithContacts(getContext(), shopList, contacts);
                        if (!info.isEmpty()) {
                            Toast.makeText(getContext(), info, Toast.LENGTH_LONG).show();
                        }

                    }

                }

                contactLoadingRelativeLayout.setVisibility(View.GONE);

            }

        });

        Bundle arguments = this.getArguments();
        if (arguments != null) {

            long shopListId = arguments.getLong(ShopListDetailFragment.SHOP_LIST_ID);
            if (shopListId > 0) {

                this.shopList = this.smartShopService.getShopListById(getContext(), shopListId);

                List<Contact> contacts = this.smartShopService.getContacts(getContext());
                this.addContactsToTable(contacts);

            }

        }

        return view;

    }

}