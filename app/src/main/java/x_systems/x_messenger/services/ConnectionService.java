package x_systems.x_messenger.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.Observable;

import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.application.ULTRA;
import x_systems.x_messenger.application.Values;
import x_systems.x_messenger.fragments.ChatFragment;
import x_systems.x_messenger.messages.MessageController;
import x_systems.x_messenger.notifications.NotificationBuilder;
import x_systems.x_messenger.otr.OtrSession;
import x_systems.x_messenger.preferences.PreferenceLoader;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;
import x_systems.x_messenger.storage.ReceivedOtrMessages;
import x_systems.x_messenger.storage.ReceivedPgpMessages;
import x_systems.x_messenger.xmpp.XMPP;

/**
 * Created by Manasseh on 10/7/2016.
 */

public class ConnectionService extends Service {
    private PowerManager.WakeLock wakeLock;
    private ConnectionService connectionService = this;
    private XMPP xmpp = new XMPP();
    public static MyObservable observable = new MyObservable();

    private ReceivedOtrMessages receivedOtrMessages = new ReceivedOtrMessages();
    private ReceivedPgpMessages receivedPgpMessages = new ReceivedPgpMessages();

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (XMPP.connection == null || !XMPP.connection.isConnected()) {

                    XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder()
                            .setResource("X-Messenger Client")
                            .setServiceName(Values.Domain)
                            .setHost(Values.Server)
                            .setPort(5222)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    XMPP.connection = new XMPPTCPConnection(config.build());
                    try {
                        XMPP.connection.connect();
                        XMPP.connection.addAsyncStanzaListener(new StanzaListener() {
                            @Override
                            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                                System.out.println("Processing packet: "+packet);
                            }
                        }, new StanzaFilter() {
                            public boolean accept(Stanza stanza) {
                                if (stanza instanceof Presence)
                                    if (((Presence) stanza).getType().equals(Presence.Type.subscribe)) {
                                        System.out.println("Subscribe received");
                                        return true;
                                    }
                                return false;
                            }
                        });
                    } catch (SmackException | IOException | XMPPException e) {
                        e.printStackTrace();
                    }
                }

                if (XMPP.connection.isConnected()) {
                    XMPP.isAvailable = Presence.Type.available;
                }
                else
                    XMPP.isAvailable = Presence.Type.unavailable;
            }
        }).start();
    }

    public class ChatMessageReader implements ChatMessageListener {
        @Override
        public void processMessage(Chat chat, Message message) {

            if (message.getBody() != null) {
                String decryptedMessage = OtrSession.handleOtr(chat, message);
                if (decryptedMessage != null) {
                    System.out.println("Received new message: '" + decryptedMessage + "'. Message is placed in performance queue.");
                    long time = System.currentTimeMillis();
                    String jid = chat.getParticipant().split("@")[0];
                    System.out.println("myjid: " + ChatFragment.isRunning);
                    new Database(BaseActivity.ca).insertDecryptedMessage(new MessageController( chat.getParticipant().split("/")[0] + "/" + jid + " - OTR ", decryptedMessage, System.currentTimeMillis()), chat.getParticipant().split("/")[0]);
                    if (ChatFragment.isRunning) {
                        System.out.println("ActivityChat.isRunning");
                        System.out.println("Sending message '" + decryptedMessage + "' to observer.");
                        System.out.println("ActivityChats: " + observable.countObservers());
                        observable.notifyObservers(new String[]{decryptedMessage, String.valueOf(time)});
                    } else {
                        receivedOtrMessages.add(chat.getParticipant(), decryptedMessage, time);
                    }
                } else
                    System.out.println("Removed a null message");

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    private void handleIntent(Intent intent) {
        obtainWakeLock();
        new RepeatedTask().execute();

        if (XMPP.connection != null && XMPP.connection.isConnected() && XMPP.connection.isAuthenticated()) {
            if (receivedOtrMessages.size() > 0)
                new OtrMessageTask().execute(receivedOtrMessages);
            if (receivedPgpMessages.size() > 0)
                new PgpMessageTask().execute(receivedPgpMessages);
        }
    }

    private class RepeatedTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Database db = new Database(connectionService);
            PreferenceLoader preferenceLoader = new PreferenceLoader(connectionService);
            long currentTime = SystemClock.elapsedRealtime();

            long destructionTime = (long)preferenceLoader.getPreferences(PreferenceLoader.Type.MESSAGE_DESTRUCTION_TIME);
            db.removeAllMessagesOlderThan(currentTime + destructionTime*60*60*1000);

            /*long destructionTime = (long)preferenceLoader.getPreferences(PreferenceLoader.Type.MESSAGE_DESTRUCTION_TIME);
            long timeSet = Long.parseLong(db.readProperty(Property.Type.MESSAGE_DESTRUCTION_TIME));
            //Check every message for it's time received and if it's older than destruction time
            if (destructionTime != 0) {
                if (timeSet + (destructionTime * 60 * 60 * 1000 - 5 * 60 * 1000) <= SystemClock.elapsedRealtime()) {
                    NotificationBuilder notificationBuilder = new NotificationBuilder(NotificationBuilder.Type.MESSAGE_DESTRUCTION_TIME, connectionService);
                    notificationBuilder.setTitle("Message destruction time");
                    String minutes = String.valueOf(
                            Math.round(5 - ((SystemClock.elapsedRealtime() - (timeSet + (destructionTime * 60 * 60 * 1000 - 5 * 60 * 1000))) / 60 / 1000))
                    );
                    notificationBuilder.setText(minutes);
                    notificationBuilder.setDestination(BaseActivity.class);
                    notificationBuilder.showNotification();
                }
                if (timeSet + destructionTime * 60 * 60 * 1000 <= SystemClock.elapsedRealtime()) {
                    System.out.println("Clearing messages");
                    NotificationBuilder.cancel(connectionService, NotificationBuilder.Type.MESSAGE_DESTRUCTION_TIME, NotificationBuilder.Type.MESSAGE);
                    db.clearMessages();
                    db.writeProperty(Property.Type.MESSAGE_DESTRUCTION_TIME, String.valueOf(SystemClock.elapsedRealtime()));
                }
            }*/

            long autoLockTime = (long)preferenceLoader.getPreferences(PreferenceLoader.Type.AUTO_LOCK_TIME);
            long timeSet = 0;
            try {
                timeSet = Long.parseLong(db.readProperty(Property.Type.AUTO_LOCK_TIME));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (timeSet != 0 && autoLockTime != 0) {
                if (timeSet + autoLockTime * 60 * 1000 <= SystemClock.elapsedRealtime()) {
                    System.out.println("Auto-lock time: Start");
                    if (XMPP.connection != null)
                        XMPP.connection.disconnect();
                    Activity currentActivity = ((ULTRA)connectionService.getApplicationContext()).getCurrentActivity();
                    if (currentActivity != null && currentActivity instanceof BaseActivity) {
                        System.out.println("Auto-lock time: BaseActivity");
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        db.writeProperty(Property.Type.AUTO_LOCK_TIME, String.valueOf(SystemClock.elapsedRealtime()));
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        /**
         * What you HAVE to do is call stopSelf() after you've pushed your
         * notification(s). This will:
         * 1) Kill the service so it doesn't waste precious resources
         * 2) Call onDestroy() which will release the wake lock, so the device
         *    can go to sleep again and save precious battery.
         */
        @Override
        protected void onPostExecute(Void result) {
            stopSelf();
        }
    }

    private class OtrMessageTask extends AsyncTask<ReceivedOtrMessages, Void, Void> {
        @Override
        protected Void doInBackground(ReceivedOtrMessages... params) {
            for (ReceivedOtrMessages receivedOtrMessages : params)
                while (receivedOtrMessages.size() != 0) {
                    NotificationBuilder notificationBuilder = new NotificationBuilder(NotificationBuilder.Type.MESSAGE, connectionService);
                    notificationBuilder.setTitle("New Message (X-Messenger)");
                    notificationBuilder.setText("Received new OTR message.");
                    notificationBuilder.setDestination(BaseActivity.class);
                    notificationBuilder.setRemoteInput();
                    notificationBuilder.setIconId(xmpp.getContactImageId(receivedOtrMessages.getParticipant()));
                    notificationBuilder.showNotification();
                    // TODO: destination to chat;

                    String fullContactName = receivedOtrMessages.getParticipant();
                    fullContactName += "/"+ fullContactName.split("@")[0];
                    fullContactName += " - OTR";
                    new Database(connectionService).insertDecryptedMessage(
                            new MessageController(
                                    fullContactName,
                                    receivedOtrMessages.getDecryptedMessage(),
                                    receivedOtrMessages.getUnixTime()
                            ),
                            receivedOtrMessages.getParticipant()
                    );

                    receivedOtrMessages.removeFirst();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            stopSelf();
        }
    }

    private class PgpMessageTask extends AsyncTask<ReceivedPgpMessages, Void, Void> {

        @Override
        protected Void doInBackground(ReceivedPgpMessages... params) {
            for (ReceivedPgpMessages receivedPgpMessages : params)
                while (receivedPgpMessages.size() != 0) {
                    NotificationBuilder notificationBuilder = new NotificationBuilder(NotificationBuilder.Type.MESSAGE, connectionService);
                    notificationBuilder.setTitle("New Message (X-Messenger)");
                    notificationBuilder.setText("Received new PGP message.");
                    notificationBuilder.setDestination(BaseActivity.class);
                    notificationBuilder.setRemoteInput();
                    notificationBuilder.setIconId(xmpp.getContactImageId(receivedPgpMessages.getParticipant()));
                    notificationBuilder.showNotification();
                    // TODO: destination to chat;

                    String fullContactName = receivedPgpMessages.getParticipant();
                    fullContactName += "/"+ fullContactName.split("@")[0];
                    fullContactName += " - PGP";
                    new Database(connectionService).insertPgpMessage(
                            new MessageController(
                                    fullContactName,
                                    receivedPgpMessages.getEncryptedMessage(),
                                    receivedPgpMessages.getUnixTime()
                            ),
                            receivedPgpMessages.getParticipant()
                    );

                    receivedPgpMessages.removeFirst();
                }
            return null;
        }
    }

    public static class MyObservable extends Observable {
        @Override
        public void notifyObservers(Object arg) {
            setChanged();
            super.notifyObservers(arg);
        }
    }

    private void obtainWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ConnectionService.wakeLock");
        wakeLock.acquire();
    }

    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
        stopSelf();
    }
}
