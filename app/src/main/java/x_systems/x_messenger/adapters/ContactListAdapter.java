package x_systems.x_messenger.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.contact_info;
import x_systems.x_messenger.fragments.ContactsFragment;
import x_systems.x_messenger.pgp.PgpStarter;
import x_systems.x_messenger.storage.ContactList;
import x_systems.x_messenger.xmpp.XMPP;

import static x_systems.x_messenger.activities.BaseActivity.canClose;


/**
 * Created by Manasseh on 10/8/2016.
 */

public class ContactListAdapter extends ArrayAdapter<String> {
    private ContactList contactList;
    private XMPP xmpp;
    private ContactsFragment Cf;
    int resourceId;



    public ContactListAdapter(Context context, ContactList contactList, ContactsFragment Cf) {
        super(context, R.layout.adapter_contact_list, contactList.names);
        this.contactList = contactList;
        this.Cf = Cf;
    }

    @NonNull
    public View getView(final int position, final View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((BaseActivity)getContext()).getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_contact_list, null, true);

        TextView tvContactName = (TextView) rowView.findViewById(R.id.contactName);
        tvContactName.setText(contactList.names.get(position));
        TextView tvonline = (TextView) rowView.findViewById(R.id.TvJid);
        tvonline.setText(contactList.JIDs.get(position));
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String JID = contactList.JIDs.get(position);
                String contactName = contactList.names.get(position);
                resourceId = contactList.encryptionTypes.get(position);
                // TODO: Change to pgp
                if(resourceId == R.drawable.pgp) {
                    Cf.GoToChatFragment(JID, contactName, resourceId);


                } else
                    Cf.GoToChatFragment(JID, contactName, resourceId);
            }
        });
        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                };
                AlertDialog.Builder dg = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = ((BaseActivity) getContext()).getLayoutInflater();
                View vt = (View) inflater.inflate(R.layout.dialog_yesorno, null);
                dg.setView(vt);
                final AlertDialog dialog = dg.create();
                final String deleteContact = contactList.JIDs.get(position);
                ((TextView) vt.findViewById(R.id.TvMessage)).setText("are you sure you want to remove " + contactList.names.get(position) + "?");
                ((Button) vt.findViewById(R.id.BtNo)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }


                }  );
                ((Button) vt.findViewById(R.id.BtYes)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        new XMPP().deleteContact(deleteContact);
                    }


                }  );
                dialog.show();

                //ImageView ivIcon = (ImageView) rowView.findViewById(R.id.contactIcon);
                //TextView tvContactStatus = (TextView) rowView.findViewById(R.id.contactStatus);
            return true;}
        });
                rowView.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        canClose = false;
                        Intent myIntent = new Intent((BaseActivity)getContext(), contact_info.class);
                        myIntent.putExtra("EXTRA_SESSION_ID", contactList.names.get(position));
                        Cf.startActivity(myIntent);

                    }

                });
                return rowView;

    }
}
