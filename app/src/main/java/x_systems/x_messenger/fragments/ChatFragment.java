package x_systems.x_messenger.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.java.otr4j.OtrException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.SplashActivity;
import x_systems.x_messenger.adapters.MessagesListAdapter;
import x_systems.x_messenger.application.Values;
import x_systems.x_messenger.consistent_connection.ConnectionCheck;
import x_systems.x_messenger.messages.MessageController;
import x_systems.x_messenger.otr.OtrSession;
import x_systems.x_messenger.pgp.PgpExtension;
import x_systems.x_messenger.services.ConnectionService;
import x_systems.x_messenger.storage.ContactChats;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.MessagesList;
import x_systems.x_messenger.storage.Property;
import x_systems.x_messenger.threading.AwaitMethod;
import x_systems.x_messenger.threading.MethodWrapper;
import x_systems.x_messenger.xmpp.XMPP;

import static x_systems.x_messenger.activities.BaseActivity.canClose;
import static x_systems.x_messenger.fragments.ChatFragment.Type.CRYPTO;
import static x_systems.x_messenger.fragments.ChatFragment.Type.OTR;
import static x_systems.x_messenger.fragments.ChatFragment.Type.PGP;

/**
 * Created by Manasseh on 10/14/2016.
 */

public class ChatFragment extends Fragment implements Observer {
    public static boolean isRunning = false;
    private Chat chat;

    public enum Type {
        OTR(R.drawable.otr),
        PGP(R.drawable.pgp),
        CRYPTO(R.drawable.crypto);
        private int resourceId;

        Type(int resourceId) {
            this.resourceId = resourceId;
        }

        public int getResourceId() {
            return this.resourceId;
        }
    }

    public static Type resourceIdToType(int resourceId) {
        if (OTR.getResourceId() == resourceId)
            return OTR;
        else if (PGP.getResourceId() == resourceId)
            return PGP;
        else if (CRYPTO.getResourceId() == resourceId)
            return CRYPTO;
        else
            return null;
    }

    private String contactJID;
    private String contactName;
    private Type chatType;
    private MessagesList messagesList = new MessagesList();
    private Database database;
    private MessagesListAdapter messagesListAdapter;
    private Context context;

    private String fullUserName;
    private String fullContactName;

    private OtrSession otrSession;

    private TextView contactNameView;

    private void onAttachInitilization() {
        this.contactJID = ((BaseActivity)getActivity()).contactJID;
        this.contactName = ((BaseActivity)getActivity()).contactName;
        this.chatType = resourceIdToType(((BaseActivity) getActivity()).contactResourceId);
        System.out.println("JID = " + this.contactJID);
        if(this.chatType == null)
            this.chatType = Type.OTR;
        System.out.println("Encryption type: "+ chatType.name());
        if (XMPP.isOnline && chatType == Type.OTR) {
            chat = ContactChats.get(contactJID);
            if (chat != null) {
                SplashActivity.isApplicationRunning = true;
                isRunning = true;
            }
        }

        this.fullUserName = "";
        try {
            this.fullUserName = database.readProperty(Property.Type.USER_JID);
            this.fullUserName += "/"+database.readProperty(Property.Type.USERNAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.fullContactName = contactJID;
        this.fullContactName += "/"+contactName;

        if (chatType == OTR)
        {
            otrSession = getOrCreateOtrSession();

            this.fullUserName += " - OTR";

            this.fullContactName += " - OTR";
        } else if (chatType == PGP)
        {
            this.fullUserName += " - PGP";

            this.fullContactName += " - PGP";
        }
    }
    @Override
    public void onAttach(Context context) {
        System.out.println("ChatFragment.onAttach()-getContext(): SplashActivity.isApplicationRunning = true;");
        ConnectionService.observable.addObserver(this);
        database = new Database(context);
        onAttachInitilization();
        this.context = context;
        super.onAttach(context);
    }


    @Override
    public void onAttach(Activity activity) {
        System.out.println("ChatFragment.onAttach()-getContext(): SplashActivity.isApplicationRunning = true;");
        ConnectionService.observable.addObserver(this);
        database = new Database(activity);
        onAttachInitilization();
        this.context = activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isRunning = true;
            onAttachInitilization();
//            contactNameView.setText(contactName);
            messagesList.clear();
            fillMessagesList();
            messagesListAdapter.notifyDataSetChanged();
            System.out.println("Chat is Active " + ChatFragment.isRunning);
        }
        else {
            isRunning = false;
            System.out.println("Chat is InActive" + ChatFragment.isRunning);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("onCreateView()");
        final View content = inflater.inflate(R.layout.activity_chat, container, false);
        SplashActivity.isApplicationRunning = true;
        isRunning = true;
        ConnectionCheck.startRepeatedChecks(context);


        //contactNameView = (TextView) content.findViewById(R.id.contact_name);
//        contactNameView.setText(contactName);

        fillMessagesList();
        ListView listMessages = (ListView)content.findViewById(R.id.listMessages);
        messagesListAdapter = new MessagesListAdapter(context, messagesList);
        listMessages.setAdapter(messagesListAdapter);

        EditText editText = (EditText) content.findViewById(R.id.input);
        editText.setHorizontallyScrolling(false);
        editText.setMaxLines(20);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                database.writeProperty(Property.Type.AUTO_LOCK_TIME, String.valueOf(SystemClock.elapsedRealtime()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                            if(chatType.equals(Type.PGP)){
                                sendMessage(v.getText().toString());
                                v.setText("");
                            }
                            else if (XMPP.isOnline) {
                                sendMessage(v.getText().toString());
                                v.setText("");
                            }
                            else {
                                Toast.makeText(v.getContext(), "You currently offline and cannot receive or send any messages.", Toast.LENGTH_LONG).show();
                            }
                            return true;
                        }
                        return false;
                    }
                }
        );

//        content.findViewById(R.id.save_conversation).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        database.saveConversation(contactJID);
//                        Toast.makeText(v.getContext(), "Conversation saved.", Toast.LENGTH_LONG).show();
//                    }
//                }
//        );

//        content.findViewById(R.id.delete_conversation).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        database.clearMessages(contactJID);
//                        Toast.makeText(v.getContext(), "Conversation deleted.", Toast.LENGTH_LONG).show();
//                        getFragmentManager()
//                                .beginTransaction()
//                                .detach(ChatFragment.this)
//                                .attach(ChatFragment.this)
//                                .commit();
//                    }
//                });

//        content.findViewById(R.id.back).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getFragmentManager().beginTransaction().hide(ChatFragment.this).commit();
//                    }
//                }
//        );

//        content.findViewById(R.id.take_and_send_photo).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        goToFragment(new PhotoCameraFragment());
//                        if (chatType == OTR)
//                        {
//                            Toast.makeText(context, "OTR only clients cannot receive any attachments.", Toast.LENGTH_SHORT).show();
//                        }
//                        else if (chatType == PGP)
//                        {
//                            Toast.makeText(context, "Messenger doesn't support attachments yet!", Toast.LENGTH_SHORT).show();
//                        }
//                        else if (chatType == CRYPTO)
//                        {
//                            Toast.makeText(context, "Messenger doesn't support attachments yet!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );
//        content.findViewById(R.id.take_and_send_video).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        goToFragment(new VideoCameraFragment());
//                        if (chatType == OTR)
//                        {
//                            Toast.makeText(context, "OTR only clients cannot receive any attachments.", Toast.LENGTH_SHORT).show();
//                        }
//                        else if (chatType == PGP)
//                        {
//                            Toast.makeText(context, "Messenger doesn't support attachments yet!", Toast.LENGTH_SHORT).show();
//                        }
//                        else if (chatType == CRYPTO)
//                        {
//                            Toast.makeText(context, "Messenger doesn't support attachments yet!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );

//        content.findViewById(R.id.send_attachments).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        goToFragment(new FilesFragment());
//                    }
//                }
//        );

//        content.findViewById(R.id.options).getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                HorizontalScrollView options = (HorizontalScrollView) content.findViewById(R.id.options);
//                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(content.findViewById(R.id.back).getLayoutParams());
//                RelativeLayout.MarginLayoutParams params1 = new RelativeLayout.MarginLayoutParams(options.getLayoutParams());
//                int childWidth = (content.findViewById(R.id.back).getMeasuredWidth() + params.getMarginEnd())*4;
//                int scrollViewX = options.getWidth() + options.getScrollX();
//                int scrollViewMargin = params1.leftMargin + params1.rightMargin + params1.getMarginEnd() + params1.getMarginStart();
//                boolean isScrollableRight = scrollViewX < childWidth + scrollViewMargin;
//                boolean isScrollableLeft = options.getScrollX() != 0;
//
//                if (isScrollableLeft && isScrollableRight)
//                {
//                    content.findViewById(R.id.navigate_next).setVisibility(View.VISIBLE);
//                    content.findViewById(R.id.navigate_before).setVisibility(View.VISIBLE);
//                } else if (isScrollableRight)
//                {
//                    content.findViewById(R.id.navigate_next).setVisibility(View.VISIBLE);
//                    content.findViewById(R.id.navigate_before).setVisibility(View.INVISIBLE);
//                }
//                else if (isScrollableLeft)
//                {
//                    content.findViewById(R.id.navigate_next).setVisibility(View.INVISIBLE);
//                    content.findViewById(R.id.navigate_before).setVisibility(View.VISIBLE);
//                }
//                else {
//                    content.findViewById(R.id.navigate_next).setVisibility(View.INVISIBLE);
//                    content.findViewById(R.id.navigate_before).setVisibility(View.INVISIBLE);
//                }
//            }
//        });
        if(this.chatType == null)
            this.chatType = Type.OTR;
        return content;
    }


    @Override
    public void update(Observable o, Object arg) {
        System.out.println("ActivityChat.update(Observable o, Object arg)");
        String[] data = (String[])arg;
        String message = data[0];
        long time = Long.valueOf(data[1]);
        if (isRunning)
        {
            System.out.println("Adding message");
            addSenderMessage(message, time);
        }
    }


    private void fillMessagesList() {
        final String[][] ss = database.readEarlierMessages(contactJID);
        for (int i = ss.length - 1; i >= 0; i--) {
            if (Boolean.valueOf(ss[i][3]))
            {
                final int Index = i;
                /*MethodWrapper<String> methodWrapper = new MethodWrapper<String>() {
                    @Override
                    public String method() {
                        return decryptPgpMessage(ss[Index][1]);
                    }
                };*/

                messagesList.add(
                        Long.parseLong(ss[i][2]),
                        ss[i][0].split("/")[1],
                        !Objects.equals(ss[i][0].split("/")[0], fullUserName.split("/")[0]),
                        ss[Index][1]
                );
                // TODO: decrypt and update in database
            }
            else {
                if(ss[i][0].split("/").length > 1) {
                    messagesList.add(
                            Long.parseLong(ss[i][2]),
                            ss[i][0].split("/")[1],
                            !Objects.equals(ss[i][0].split("/")[0], fullUserName.split("/")[0]),
                            ss[i][1]
                    );
                }
            }
            System.out.println(ss[i][0].split("/")[0]+" - "+fullUserName.split("/")[0]+" - "+!Objects.equals(ss[i][0].split("/")[0], fullUserName.split("/")[0]));
        }
    }

    private void sendMessage(String message) {


        switch (chatType) {
            case OTR:
                addUserMessage(message, System.currentTimeMillis());
                trySendOtrEncrypted(message);
                break;
            case PGP:
                trySendPgpEncrypted(message);
                break;
            case CRYPTO:
                break;
            default:
                trySendOtrEncrypted(message);
                break;
        }
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void addUserMessage(String message, long time) {
        addToLayoutAsUser(message, time);
        addToDatabase(message, time);
    }

    private void addSenderMessage(String message, long time) {
        addToLayoutAsSender(message, time);
        database.insertDecryptedMessage(
                new MessageController(
                        contactName,
                        message,
                        time),
                contactJID);
    }




    private void trySendOtrEncrypted(String message) {
        /*String encryptedMessage = "";
        try {
            encryptedMessage = OtrSession.find_session(contactName).uUserid1.transformSending(
                    otrSession.sdUserid1,
                    message
            );
            ContactChats.get(contactName).sendMessage(encryptedMessage);
        } catch (OtrException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("ActivityChat.encryptedMessage= "+OtrSession.find_session(contactName).uUserid1.transformSending(
                    otrSession.sdUserid1,
                    message
            ));
        } catch (OtrException e) {
            e.printStackTrace();
        }*/
        try {
            sendmsg(message, otrSession, chat);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public static void sendmsg(String msg, OtrSession otr_session, Chat chatnow) throws SmackException.NotConnectedException {
        try {
            if (otr_session != null) {
                // if (otr_session.uUserid1.getSessionStatus(otr_session.sdUserid1) == SessionStatus.ENCRYPTED) {
                System.out.println("Sending CRYPTED message: " + otr_session.uUserid1.transformSending(otr_session.sdUserid1, msg));
                chatnow.sendMessage(otr_session.uUserid1.transformSending(otr_session.sdUserid1, msg));

                //  } else {
                System.out.println("SSTATUS " + msg + ", " + otr_session.chatuser + ", " + otr_session.uUserid1.getSessionStatus(otr_session.sdUserid1) + " " + OtrSession.Chat_Sessions.size());
                //  chatnow.sendMessage(msg);
                //  }
            }
        } catch(OtrException e){
            e.printStackTrace();
        }
    }

    public void SendMail(final String Text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String username = Values.User + "@" + Values.Mail_Domain;
                final String password = Values.Mail_Password;
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", Values.Mail_Server);
                props.put("mail.smtp.port", "25");
                Session session = Session.getInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try {

                    javax.mail.Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(javax.mail.Message.RecipientType.TO,
                            InternetAddress.parse(contactJID));
                    message.setSubject("Ultra Message");
                    String using = BaseActivity.ca.pgpStarter.encryptText(Text, contactJID, new Database(BaseActivity.ca).readProperty(Property.Type.USER_JID));
                    if (using == null)
                        using = "error";
                    message.setText(using);


                    Transport.send(message);

                    System.out.println("Done");

                } catch (MessagingException e) {
                    System.out.println("ERROR265 " + e.toString());
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }

    private void trySendPgpEncrypted(String message) {

            String encryptedMessage = ((BaseActivity)getActivity()).pgpStarter.encryptText(message, contactJID, fullUserName.split("/")[0]);


            /*System.out.println("Send encrypted message: " + msg);

            chat.sendMessage(msg);*/

        /*final boolean isSigned = true, isAsciiArmored = true;
        Set<String> recipients = new HashSet<>();
        recipients.add(contactJID);
        OperationResult operationResult = pgpService.encryptText(
                recipients,
                new Database(this).readProperty(Property.Type.USER_JID),
                message,
                isSigned,
                isAsciiArmored
        );
        try {
            ContactChats.get(contactJID).sendMessage(operationResult.getResultString());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }*/
        if(encryptedMessage != null) {
            SendMail(encryptedMessage);
            addUserMessage(message, System.currentTimeMillis());
        }
    }

    private String decryptPgpMessage(String encryptedMessage) {
        return ((BaseActivity)getActivity()).pgpStarter.decryptText(encryptedMessage, fullUserName.split("/")[0]);
        /*final AtomicBoolean isSigned = new AtomicBoolean(true);
        OperationResult operationResult = pgpService.decryptText(
                encryptedMessage,
                contactJID,
                isSigned
        );
        return operationResult.getResultString();*/
    }

    private void addToLayoutAsUser(String message, long time) {
        messagesListAdapter.add(
                time,
                fullUserName.split("/")[1],
                false,
                message
        );
        messagesListAdapter.notifyDataSetChanged();
    }

    private void addToLayoutAsSender(String message, long time) {
        messagesListAdapter.add(
                time,
                fullContactName.split("/")[1],
                true,
                message
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ((BaseActivity)getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messagesListAdapter.notifyDataSetChanged();
                }
            });
        else
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messagesListAdapter.notifyDataSetChanged();
                }
            });
    }

    private void addToDatabase(String message, long time) {
        database.insertDecryptedMessage(
                new MessageController(
                        fullUserName,
                        message,
                        time
                ),
                contactJID
        );
    }

    private OtrSession getOrCreateOtrSession() {
        OtrSession foundOtrSession = OtrSession.find_session(contactJID);
        if (foundOtrSession != null) {
            return foundOtrSession;
        }
        return new OtrSession(XMPP.connection.getUser(), contactJID, null, chat);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        System.out.println("onStart()");
        SplashActivity.isApplicationRunning = true;
        isRunning = true;
        super.onStart();
    }

    @Override
    public void onPause() {
        SplashActivity.isApplicationRunning = false;
        super.onPause();
    }

    @Override
    public void onResume() {
        System.out.println("onResume()");
        SplashActivity.isApplicationRunning = true;
        isRunning = true;
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ConnectionService.observable.deleteObserver(this);
    }


}
