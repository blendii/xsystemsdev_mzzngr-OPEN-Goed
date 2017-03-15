package x_systems.x_messenger.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QDecoderStream;
import com.sun.mail.util.QPDecoderStream;

import org.apache.harmony.awt.internal.nls.Messages;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import javax.mail.util.SharedByteArrayInputStream;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.PgpMailActivity;
import x_systems.x_messenger.adapters.MailAdapter;
import x_systems.x_messenger.application.Loader;
import x_systems.x_messenger.pgp.PGPMailMessage;
import x_systems.x_messenger.storage.DataStore;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;
import x_systems.x_messenger.threading.MethodThread;


public class PgpMail extends Fragment {
    View MainView;
    ListView Mails;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    public PgpMail() {
        // Required empty public constructor
    }





    String contact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void ReadMails() {
        final PGPMailMessage[] Messages = ((PGPMailMessage[]) new MethodThread().Async(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return GetMails("otr-safe.com", PgpMail.this.contact, "john", "12345678");
            }
        }));
        this.Mails = (ListView) this.MainView.findViewById(R.id.LvMail);
        List<PGPMailMessage> MailFrom = new ArrayList<>();
        List<String> Content = new ArrayList<>();
        for (PGPMailMessage lm : Messages) {
            MailFrom.add(lm);
            Content.add(lm.GetMessage());
        }
        final List<String> Ct = Content;
        this.Mails.setAdapter(new MailAdapter(getActivity(), R.layout.pgpmail_mailitem, MailFrom));
        this.Mails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Loader.Show(getActivity());
                DataStore.SetItem("messagecontent", Messages[position], getActivity());
                ((PgpMailActivity) getActivity()).ShowViewer();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.MainView = inflater.inflate(R.layout.fragment_pgp_mail, container, false);
        this.contact = (String)DataStore.GlobalStore.GetItem("contactjid");
        ((Button)this.MainView.findViewById(R.id.BtNewMessage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PgpMailActivity)getActivity()).ShowSender();
            }
        });
        //ReadMails();
        Loader.Dismiss();
        return this.MainView;
    }







    public PGPMailMessage[] GetMails(final String host, final String contactname, final String username, final String password) {
        try {
            Properties properties = new Properties();
            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "110");
            //properties.put("mail.pop3.starttls.enable", "true");
            properties.setProperty("mail.store.protocol", "pop3s");
            Session EmailSession = Session.getInstance(properties, null);
            Store store = EmailSession.getStore();
            store.connect(host, username, password);
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            List<PGPMailMessage> msgs = new ArrayList<>();
            SearchTerm searchquery = new FromTerm(new InternetAddress(contactname));
            for (Message msg : emailFolder.search(searchquery)) {
                byte[] hello = new byte[9000];
                String mytype = "NONE";
                if (msg.getContent() instanceof SharedByteArrayInputStream) {
                    SharedByteArrayInputStream dt = (SharedByteArrayInputStream) msg.getContent();
                    hello = new byte[dt.available()];
                    dt.read(hello);
                    mytype = "Shared";

                } else if (msg.getContent() instanceof QPDecoderStream) {
                    QPDecoderStream dt = (QPDecoderStream) msg.getContent();
                    hello = new byte[dt.available()];
                    dt.read(hello);
                    mytype = "QPSTREAM";
                } else if (msg.getContent() instanceof QDecoderStream) {
                    QDecoderStream dt = (QDecoderStream) msg.getContent();
                    hello = new byte[dt.available()];
                    dt.read(hello);
                    mytype = "QSTREAM";
                } else if(msg.getContent() instanceof BASE64DecoderStream) {
                    BASE64DecoderStream dt = (BASE64DecoderStream) msg.getContent();
                    hello = new byte[dt.available()];
                    dt.read(hello);
                    mytype = "BASE64DecoderStream";

                } else {
                    hello = msg.getContent().toString().getBytes();
                }

                String fullmsg = new String(hello , "UTF-8");
                System.out.println("Mailer---------------------------------\n" + new String(hello) + "\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< -" + mytype  );
                //PGPMailMessage newmsg = new PGPMailMessage(null, null, fullmsg);
               // msgs.add(newmsg);
            }

            PGPMailMessage[] Messages = msgs.toArray(new PGPMailMessage[msgs.size()]);
            System.out.println("MSGLENGTH: " + Messages.length);
            emailFolder.close(true);
            store.close();

            return Messages;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }
}
