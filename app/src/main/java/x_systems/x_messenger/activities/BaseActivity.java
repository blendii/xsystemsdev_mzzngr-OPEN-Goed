package x_systems.x_messenger.activities;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.renderscript.Sampler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Properties;
import java.util.concurrent.Executor;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import x_systems.x_messenger.R;
import x_systems.x_messenger.application.Values;
import x_systems.x_messenger.consistent_connection.ConnectionCheck;
import x_systems.x_messenger.fragments.ChatFragment;
import x_systems.x_messenger.fragments.ChatsFragment;
import x_systems.x_messenger.fragments.ContactsFragment;
import x_systems.x_messenger.fragments.FilesFragment;
import x_systems.x_messenger.fragments.FragmentNavigation;
import x_systems.x_messenger.fragments.NotesFragment;
import x_systems.x_messenger.fragments.PgpMail;
import x_systems.x_messenger.fragments.PhotoCameraFragment;
import x_systems.x_messenger.fragments.SettingsFragment;
import x_systems.x_messenger.fragments.VideoCameraFragment;
import x_systems.x_messenger.fragments.support.v4.SupportFragment;
import x_systems.x_messenger.listeners.NavigateListener;
import x_systems.x_messenger.messages.MessageController;
import x_systems.x_messenger.pgp.OpenPgpHttp;
import x_systems.x_messenger.pgp.PGPMailMessage;
import x_systems.x_messenger.pgp.PgpStarter;
import x_systems.x_messenger.preferences.PreferenceLoader;
import x_systems.x_messenger.services.ConnectionService;
import x_systems.x_messenger.storage.DataStore;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;
import x_systems.x_messenger.threading.AwaitMethod;
import x_systems.x_messenger.threading.MethodWrapper;
import x_systems.x_messenger.xmpp.XMPP;

import static android.R.attr.host;
import static android.R.attr.soundEffectsEnabled;

/**
 * Created by Manasseh on 10/8/2016.
 */

public class BaseActivity extends ExtendedActivity implements NavigateListener {
    public static BaseActivity ca = null;
    public String contactJID;
    public static boolean canClose = true;
    public String contactName;
    public static String imgcontent = "";
    public int contactResourceId;
    public Bundle savedInstanceState;

    public Database database = new Database(this);

    private FragmentNavigation fragmentNavigation;

    public PgpStarter pgpStarter;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        canClose = true;
        if (scanResult != null && scanResult.getContents() != null) {
            System.out.println("bcresult = " + scanResult.getContents());
            ContactsFragment.SetBcValue(scanResult.getContents().substring(scanResult.getContents().length() - 16, scanResult.getContents().length()));
        }
        // else continue with any other code you need in the method

    }


    private void CreatePgpMessageListener() {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    int currentcount = 0;
                    try {

                        Properties properties = new Properties();
                        properties.put("mail.pop3.host", Values.Mail_Server);
                        properties.put("mail.pop3.port", "995");
                        properties.put("mail.pop3.starttls.enable", "true");
                        properties.setProperty("mail.store.protocol", "pop3s");
                        Session EmailSession = Session.getInstance(properties, null);
                        Store storeMain = EmailSession.getStore();
                        storeMain.connect(Values.Mail_Server, Values.User + "@" + Values.Mail_Domain, Values.Mail_Password);
                        final Folder inboxMain = storeMain.getFolder("INBOX");
                        currentcount = inboxMain.getMessageCount();
                        System.out.println("MESSAGE COUNT: " + currentcount);
                        while(true){
                            Store store = EmailSession.getStore();
                            store.connect(Values.Mail_Domain, Values.User + "@" + Values.Mail_Domain, Values.Mail_Password);
                            final Folder inbox = store.getFolder("INBOX");
                            inbox.open(Folder.READ_WRITE);
                            if(inbox.getMessageCount() > 0) {
                                Flags deleted = new Flags(Flags.Flag.DELETED);
                                PGPMailMessage msg = new PGPMailMessage(inbox.getMessage(inbox.getMessageCount()));
                                inbox.setFlags(new Message[]{ inbox.getMessage(inbox.getMessageCount())}, deleted, true);
                                currentcount = inbox.getMessageCount();
                                System.out.println("NEW PGP MESSAGE " + currentcount + msg.GetSender() + " " + msg.IsSecure() + "\nMSG: " + msg.GetMessage());
                                String msginsert = "Insecure message removed";


                                    msginsert = BaseActivity.ca.pgpStarter.decryptText(msg.GetMessage(), new Database(BaseActivity.ca).readProperty(Property.Type.USER_JID));
                                    if (msginsert.equals(null))
                                        msginsert = "Error pgp, contact administrators";

                                    if (ChatFragment.isRunning && msg.GetSender().equals(contactJID)) {
                                        ConnectionService.observable.notifyObservers(new String[]{msginsert, String.valueOf(System.currentTimeMillis())});
                                    }
                                    new Database(BaseActivity.this).insertDecryptedMessage(new MessageController(msg.GetSender() + "/" + msg.GetSender().split("@")[0] + " - PGP ", msginsert, System.currentTimeMillis()), msg.GetSender());

                            }
                            inbox.close(true);
                            store.close();
                            Thread.sleep(1000);
                        }

                }

                catch(NoSuchProviderException e) {
                    System.out.println("ERROR265 " + e.toString());
                }  catch(MessagingException e) {
                        System.out.println("ERROR265 " + e.toString());
                }  catch (InterruptedException e) {
                        System.out.println("ERROR265 " + e.toString());
                    }

                }
        }).start();
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        database.writeProperty(Property.Type.AUTO_LOCK_TIME, String.valueOf(SystemClock.elapsedRealtime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentNavigation = new FragmentNavigation(savedInstanceState, this);

        SplashActivity.isApplicationRunning = true;
        System.out.println("BaseActivity.onCreate(): SplashActivity.isApplicationRunning = true;");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ca = this;
        DataStore.Init(this);
        DataStore.SetItem("chatback", new ContactsFragment(),BaseActivity.this);
        this.savedInstanceState = savedInstanceState;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout Logout = (RelativeLayout) toolbar.findViewById(R.id.Logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adbuilder = new AlertDialog.Builder(BaseActivity.this);
                LayoutInflater ifs = getLayoutInflater();
                View main = ifs.inflate(R.layout.dialog_yesorno, null);
                ((TextView)main.findViewById(R.id.TvMessage)).setText("Do You want to logout?");
                adbuilder.setView(main);
                final AlertDialog ad = adbuilder.create();
                ((Button)main.findViewById(R.id.BtYes)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Exit();
                        ad.dismiss();
                    }
                });
                ((Button)main.findViewById(R.id.BtNo)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.dismiss();
                    }
                });
                ad.show();

            }
        });
        setSupportActionBar(toolbar);
//        setUserValues();

        fragmentNavigation.navigateDefaultFragment();

        pgpStarter = new PgpStarter(this) {
            @Override
            public void onStartCreateKeyPair() {
                System.out.println("onStartCreateKeyPair()");
                new Thread() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("running: StartAnimation");
                                FragmentNavigation.isEnabled = false;

                                View container = BaseActivity.this.findViewById(R.id.encryptionLoader);
                                container.setBackgroundResource(R.drawable.pgp_otr_loader);
                                ((AnimationDrawable) container.getBackground()).start();
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onEndCreateKeyPair() {
                System.out.println("onEndCreateKeyPair()");
                new Thread() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("running: StopAnimation");

                                View container = BaseActivity.this.findViewById(R.id.encryptionLoader);
                                container.setVisibility(View.GONE);

                                if (container.getBackground() instanceof AnimationDrawable)
                                    ((AnimationDrawable) container.getBackground()).stop();

                                FragmentNavigation.isEnabled = true;
                            }
                        });
                    }
                }.start();
            }
        };
       CreatePgpMessageListener();
    }

    @Override
    protected void onResume() {
        SplashActivity.isApplicationRunning = true;
        System.out.println("BaseActivity.onResume(): SplashActivity.isApplicationRunning = true;");
        super.onResume();
        ConnectionCheck.startRepeatedChecks(this);
        canClose = true;
    }

    @Override
    public void OnRequestNavigate(final String key, final Object[] args) {
        switch(key)
        {
            case "chat":
                contactJID = (String)args[0];
                contactName = (String)args[1];
                contactResourceId = (int)args[2];
                fragmentNavigation.navigateAsMainFragment(new ChatFragment());
                break;
            case "note":
                fragmentNavigation.navigateAsMainFragment(new NotesFragment());
                break;
        }
    }

//    private void setUserValues() {
//        PreferenceLoader preferenceLoader = new PreferenceLoader(this);
//
//        ((ImageView)findViewById(R.id.profileIcon)).setImageResource((int)preferenceLoader.getPreferences(PreferenceLoader.Type.AVATAR_ID));
//        try {
//            ((TextView) findViewById(R.id.profileName)).setText(database.readProperty(Property.Type.USERNAME));
//        } catch (Exception e) {
//            ((TextView) findViewById(R.id.profileName)).setText("Could not decrypt");
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onStart() {
        SplashActivity.isApplicationRunning = true;
        System.out.println("BaseActivity.onStart(): SplashActivity.isApplicationRunning = true;");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SplashActivity.isApplicationRunning = false;
        System.out.println("BaseActivity.onPause(): SplashActivity.isApplicationRunning = false;");
        if(canClose)
        Exit();
    }

    public void onClick_Contacts(View view) {
        fragmentNavigation.navigateAsMainFragment(new ContactsFragment());
        DataStore.SetItem("chatback", new ContactsFragment(),BaseActivity.this);
    }
    public void onClick_Chats(View view) {
        fragmentNavigation.navigateAsMainFragment(new ChatsFragment());
        DataStore.SetItem("chatback", new ChatsFragment(),BaseActivity.this);
    }
    public void onClick_Photo_Camera(View view) {
        fragmentNavigation.navigateAsMainFragment(new PhotoCameraFragment());
    }
    public void onClick_Video_Camera(View view) {
        fragmentNavigation.navigateAsMainFragment(new VideoCameraFragment());
    }
    public void onClick_Files(View view) {
        fragmentNavigation.navigateAsMainFragment(new FilesFragment());
    }
    public void onClick_Settings(View view) {
        fragmentNavigation.navigateAsMainFragment(new SettingsFragment());
    }
    public void SetFragment(Fragment fragment){
        fragmentNavigation.navigateAsSubFragment(fragment);
    }

    public void onClick_AddContact(View view) {
        //if ( FragmentNavigation.isEnabled)
            //askContactInfo();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        SettingsFragment.auto_lock_time_opened = SystemClock.elapsedRealtime();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        SettingsFragment.auto_lock_time_opened = SystemClock.elapsedRealtime();
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        pgpStarter.unbind();
    }

    @Override
    public void onBackPressed() {

            Fragment chatFragment = getFragmentManager().findFragmentByTag(ChatFragment.class.getSimpleName());
            Fragment noteFragment = getFragmentManager().findFragmentByTag(NotesFragment.class.getSimpleName());
            FilesFragment fileFragment = (FilesFragment)getFragmentManager().findFragmentByTag(FilesFragment.class.getSimpleName());
            if (chatFragment != null && chatFragment.isAdded() && chatFragment.isVisible()) {
                System.out.println("CHATBACK");
                getFragmentManager().beginTransaction().hide(chatFragment).commit();
                if(DataStore.GetItem("chatback", BaseActivity.this) instanceof SupportFragment)
                    fragmentNavigation.navigateAsMainFragment((SupportFragment) DataStore.GetItem("chatback", BaseActivity.this));
                else
                    fragmentNavigation.navigateAsMainFragment((Fragment) DataStore.GetItem("chatback", BaseActivity.this));
            } else if(noteFragment != null && noteFragment.isAdded() && noteFragment.isVisible()){
                getFragmentManager().beginTransaction().remove(noteFragment).commit();
                fileFragment.CreateFilesListWithOnItemClickListener(fileFragment.getView());
            } else {
                super.onBackPressed();
                if(canClose)
                Exit();
            }

    }



    private void Exit(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
        finish();
    }
}



