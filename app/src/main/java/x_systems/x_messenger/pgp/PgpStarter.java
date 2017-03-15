package x_systems.x_messenger.pgp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.pawelgorny.pgpotr.PgpService;
import com.pawelgorny.pgpotr.tech.OperationResult;
import com.pawelgorny.pgpotr.tech.PgpOtrConstant;
import com.pawelgorny.pgpotr.tech.ResultCode;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;

/**
 * Created by Manasseh on 10/17/2016.
 */

public abstract class PgpStarter {
    public PgpService pgpService;
    private boolean isServiceBound = false;
    private Context context;
    public static String password;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PgpService.PgpServiceBinder binder = (PgpService.PgpServiceBinder) service;
            pgpService = binder.getService();
            System.out.println("Pgp is service bound.");
            isServiceBound = true;
            startPgp();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    protected PgpStarter(Context context){
        this.context = context;
        Intent pgpIntent = new Intent(context, PgpService.class);
        context.bindService(pgpIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void startPgp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("CreateKeyPairIfNotExists()");
                //pgpService.importRecipientKey(OpenPgpHttp.getkey("A86C65B1F597CF69").getBytes(), "pgp@otr-safe.com");
                Set<String> repc = new HashSet<String>();
                repc.add("pgp@otr-safe.com");
                String crypted = pgpService.encryptText(repc, "pgp@otr-safe.com", "Hello world From afghanistan", true, true).getResultString();
                AtomicBoolean verf = new AtomicBoolean();
                String decrypted = pgpService.decryptText(crypted, "pgp@otr-safe.com",verf).getResultString();
                System.out.println("Pgp message(CRYPTED): " + crypted );
                System.out.println("Pgp message(DECRYPTED): " + decrypted );

                if (hasKeyPair())
                    createKeyPair(new Database(pgpService).readProperty(Property.Type.USER_JID).split("/")[0], password);
                else
                    endCreateKeyPair();
            }
        }).start();

    }
    //Decrypt text for the current user
    public String decryptText(String encryptedMessage, String Current_user_JID){
        String output = "";
        try {
            AtomicBoolean at = new AtomicBoolean();
            OperationResult or = pgpService.decryptText(encryptedMessage, Current_user_JID, at);
            if(or.getResultCode() == ResultCode.SUCCESS){
                output = or.getResultString();
                return output;
            } else {
                System.out.println("Couldn't decrypt message for " + Current_user_JID + " error code: " + or.getResultCode());
                return "Can't decrypt message, check your key";
            }
        } catch (Exception e){
            System.out.println("Pgp decrypting stopped with an unexpected error: " + e.toString());
        }
        return null;
    }

    public String encryptText(String unencryptedmessage, String Contact_user_JID, String Current_user_JID){
        String output = "";
        try {
            Set<String> receivers = new HashSet<String>();
            receivers.add(Contact_user_JID);
            OperationResult or = pgpService.encryptText(receivers, Current_user_JID, unencryptedmessage, true, true);
            if(or.getResultCode() == ResultCode.SUCCESS){
                output = or.getResultString();
                return output;
            }
            else {
                System.out.println("Couldn't encrypt message for receiver " + Contact_user_JID + " error code: " + or.getResultCode());
            }
        } catch (Exception e){
            System.out.println("Pgp encrypting stopped with an unexpected error: " + e.toString());
        }

        return null;
    }

    private boolean hasKeyPair() {
        return pgpService.getMyPublicKey().getResultCode() != ResultCode.SUCCESS;
    }

    private void createKeyPair(String jid, String pwd){
        startCreateKeyPair();
        try {
            OperationResult or = pgpService.createMyKey(jid, PgpOtrConstant.ALLOWED_STRENGTH.KEY_4096, pwd.toCharArray(), null);

            System.out.println("Pgp key status: " + or.getResultString());
            System.out.print("my pub key: " + OpenPgpHttp.addkey(pgpService.getMyPublicKey().getResultString()));
        } catch (Exception e) {
            System.out.println("Error creating PGP Keypair: " + e.toString());
        }
        endCreateKeyPair();
    }

    private void startCreateKeyPair() {
        onStartCreateKeyPair();
    }

    public abstract void onStartCreateKeyPair();

    private void endCreateKeyPair() {
        onEndCreateKeyPair();
    }

    public abstract void onEndCreateKeyPair();

    public boolean ImportContactKey(String PublicKey, String ContactJid){
        if (PublicKey != null) {
            byte[] key = PublicKey.getBytes();
            try {
                OperationResult or = pgpService.importRecipientKey(key, ContactJid, true);
                if (or.getResultCode() == ResultCode.SUCCESS) {
                    System.out.println("Pgp PublicKey imported! " + "(For " + ContactJid + ")");
                    return true;
                } else {
                    System.out.println("Pgp PublicKey import Failed error: " + or.getResultCode());
                }
            } catch (Exception e) {
                System.out.println("Pgp PublicKey import stopped with an unexpected error: " + e.toString());
            }
        }

        return false;
    }

    public void unbind() {
        if (isServiceBound) {
            context.unbindService(serviceConnection);
            isServiceBound = false;
        }
    }
}
