package x_systems.x_messenger.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.Objects;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.CaptureActivityExtended;
import x_systems.x_messenger.activities.PgpMailActivity;
import x_systems.x_messenger.adapters.ContactListAdapter;
import x_systems.x_messenger.application.Loader;
import x_systems.x_messenger.fragments.support.v4.SupportFragment;
import x_systems.x_messenger.listeners.NavigateListener;
import x_systems.x_messenger.pgp.OpenPgpHttp;
import x_systems.x_messenger.storage.ContactList;
import x_systems.x_messenger.storage.DataStore;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.threading.AwaitMethod;
import x_systems.x_messenger.threading.MethodWrapper;
import x_systems.x_messenger.xmpp.XMPP;

import static x_systems.x_messenger.activities.BaseActivity.canClose;

/**
 * Created by Manasseh on 10/8/2016.
 */

public class ContactsFragment extends SupportFragment {
    private Activity activity;
    private ContactList contactList = new ContactList();
    private EditText pgp_key_id;
    private static ContactsFragment currentct;
    private String[] ContactDetails;
    NavigateListener navigateListener;
    int newContactEncryptionType;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            navigateListener = (NavigateListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NavigateListener");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        if(!hidden){
            new XMPP().UpdateUsers(activity);
            if(contactList.JIDs.size() < new Database(getActivity()).getUsers().JIDs.size() ) {

                ca.notifyDataSetChanged();
                contactList = new Database(getActivity()).getUsers();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.currentct = this;
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        activity = getActivity();
        super.onActivityCreated(savedInstanceState);
        new XMPP().UpdateUsers(activity);
        contactList = new Database(getActivity()).getUsers();

        AddContact();
        CreateContactListWithOnItemClickListener();


    }
    ContactListAdapter ca ;
    private void CreateContactListWithOnItemClickListener()
    {
        final ListView contactsList = (ListView)getView().findViewById(R.id.listContacts);
        ca = new ContactListAdapter(getActivity(), contactList, this);
        contactsList.setAdapter(ca);

    }

    public void RefreshList(){
        new XMPP().UpdateUsers(activity);
        contactList = new Database(getActivity()).getUsers();
        CreateContactListWithOnItemClickListener();
        ca.notifyDataSetChanged();
    }

    public String[] GetContactDetails(){
        return this.ContactDetails;
    }

    private void SetContactDetails(String jid, String contact) {
        this.ContactDetails = new String[]{jid, contact};
    }

    public void GoToChatFragment(final String JID, final String contactName, final Integer resourceId)
    {
        if (navigateListener != null)
            navigateListener.OnRequestNavigate("chat", new Object[] {JID, contactName, resourceId});
    }

    private void AddContact() {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        ((Button)getView().findViewById(R.id.NewOtrContact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newContactEncryptionType = R.drawable.otr;
                if (newContactEncryptionType == R.drawable.otr) {
                    final View innerContent = inflater.inflate(R.layout.add_otr_contact, null);

                    AlertDialog.Builder innerBuilder = new AlertDialog.Builder(getActivity());
                    innerBuilder.setView(innerContent);
                    final AlertDialog dg = innerBuilder.create();
                    dg.show();
                    ((Button)innerContent.findViewById(R.id.BtOtrAdd)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText contact = (EditText) innerContent.findViewById(R.id.EtOtrJid);
                            try {
                                new XMPP().addContact(contact.getText().toString(), getActivity());
                                ((BaseActivity)getActivity()).database.addContact(
                                        contact.getText().toString().split("@")[0],
                                        "",
                                        contact.getText().toString(),
                                        R.drawable.ic_placeholder,
                                        newContactEncryptionType
                                );
                            } catch (SmackException.NotConnectedException | SmackException.NotLoggedInException | SmackException.NoResponseException | XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Contact cannot be added while offline.", Toast.LENGTH_LONG).show();
                            }
                            RefreshList();
                            dg.cancel();

                        }
                    });
                    ((Button)innerContent.findViewById(R.id.BtOtrCancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dg.cancel();
                        }
                    });





                    dg.show();
                }
            }
        });
        ((Button)getView().findViewById(R.id.NewPgpContact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newContactEncryptionType = R.drawable.pgp;
                if (newContactEncryptionType == R.drawable.pgp) {
                    final View innerContent = inflater.inflate(R.layout.add_pgp_contact, null);

                    pgp_key_id = (EditText) innerContent.findViewById(R.id.EtKeyId);

                    AlertDialog.Builder innerBuilder = new AlertDialog.Builder(getActivity());
                    innerBuilder.setView(innerContent);
                    innerBuilder.setCancelable(true);
                    final AlertDialog innerDialog = innerBuilder.create();
                    innerDialog.show();

                    innerContent.findViewById(R.id.BtContactAdd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            EditText contact_jid = (EditText) innerContent.findViewById(R.id.EtJID);
                            final String contactJid = contact_jid.getText().toString();

                            EditText pgp_key_id = (EditText) innerContent.findViewById(R.id.EtKeyId);
                            if (pgp_key_id.getText().length() == 16) {
                                final String pgpFingerprint = pgp_key_id.getText().toString();


                                AwaitMethod awaitMethod = new AwaitMethod(new MethodWrapper<String>() {
                                    @Override
                                    public String method() {
                                        if (((BaseActivity) getActivity()).pgpStarter.ImportContactKey(OpenPgpHttp.getkey(pgpFingerprint.substring(pgpFingerprint.length() - 16)), contactJid))
                                            return "true";
                                        else
                                            return "false";
                                    }
                                });

                                if (Objects.equals(awaitMethod.run(), "true")) {
                                    ((BaseActivity) getActivity()).database.addContact(
                                            contactJid.split("@")[0],
                                            "",
                                            contactJid,
                                            R.drawable.ic_placeholder,
                                            newContactEncryptionType
                                    );
                                } else {
                                    Toast.makeText(getActivity(), "Error adding PGP key.", Toast.LENGTH_LONG).show();
                                }

                                innerDialog.cancel();
                                RefreshList();
                            } else {
                                pgp_key_id.setText("CANT BE EMPTY");
                            }
                        }
                    });


                    innerContent.findViewById(R.id.BtCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            innerDialog.cancel();
                        }
                    });


                    innerContent.findViewById(R.id.scan_qr).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            canClose = false;
                            IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                            scanIntegrator.setPrompt("Scan");
                            scanIntegrator.setBeepEnabled(true);
                            scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                            scanIntegrator.setCaptureActivity(CaptureActivityExtended.class);
                            scanIntegrator.setOrientationLocked(true);
                            scanIntegrator.setBarcodeImageEnabled(true);
                            scanIntegrator.initiateScan();
                            //goToFullscreenFragment(new QrScanFragment());
                        }
                    });
                }

            }
        });


    }

    public static void SetBcValue(String Value){
        ContactsFragment.currentct.pgp_key_id.setText(Value);
    }

}
