package x_systems.x_messenger.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.PgpMailActivity;
import x_systems.x_messenger.storage.DataStore;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;

/**
 * A simple {@link Fragment} subclass.
 */
public class pgpmail_send extends Fragment {

    private String contact;
    public pgpmail_send() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View main = inflater.inflate(R.layout.fragment_pgpmail_send, container, false);
        // Inflate the layout for this fragment
        contact = (String)DataStore.GlobalStore.GetItem("contactjid");
        ((Button) main.findViewById(R.id.BtSendPgp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMail(((TextView) main.findViewById(R.id.TvPgpMessage)).getText().toString());
                ((PgpMailActivity)getActivity()).ShowMails();
            }
        });
        return main;
    }

    public void SendMail(final String Text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String username = "john@otr-safe.com";
                final String password = "12345678";
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "otr-safe.com");
                props.put("mail.smtp.port", "25");
                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try {

                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(pgpmail_send.this.contact));
                    message.setSubject("Ultra Message");
                    String using = BaseActivity.ca.pgpStarter.encryptText(Text, pgpmail_send.this.contact, new Database(BaseActivity.ca).readProperty(Property.Type.USER_JID));
                    if (using == null)
                        using = "error";
                    message.setText(using);


                    Transport.send(message);

                    System.out.println("Done");

                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }

}
