package x_systems.x_messenger.otr;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrException;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.crypto.OtrCryptoEngineImpl;
import net.java.otr4j.crypto.OtrCryptoException;
import net.java.otr4j.session.SessionID;

import org.jivesoftware.smack.SmackException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import test.net.java.otr4j.OtrEngineImplTest;

/**
 * Created by Manasseh on 10/9/2016.
 */

public class OtrHost implements OtrEngineHost {
    private OtrPolicy policy;
    public String lastInjectedMessage;
    public org.jivesoftware.smack.chat.Chat cchat = null;
    public OtrSession crypt_ses = null;
    private static Logger logger = Logger.getLogger(OtrEngineImplTest.class
            .getName());

    public OtrHost(OtrPolicy policy, org.jivesoftware.smack.chat.Chat ch, OtrSession cs) {
        this.cchat = ch;
        this.crypt_ses = cs;
        this.policy = policy;
    }



    public OtrPolicy getSessionPolicy(SessionID ctx) {
        return this.policy;
    }

    /*public void injectMessage(SessionID sessionID, String msg) {
        this.lastInjectedMessage = msg;
        System.out.println("IM injects message: " + msg);
        try {
            ActivityChat.sendmsg(msg, crypt_ses, cchat);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }*/

    public void injectMessage(SessionID sessionID, String msg) {
        this.lastInjectedMessage = msg;
        System.out.println("IM injects message: " + msg);
        try {
            if (cchat != null)
                cchat.sendMessage(msg);
            System.out.println("SEND : "  + msg);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void smpError(SessionID sessionID, int tlvType, boolean cheated)
            throws OtrException{
        System.out.println("SM verification error with user: " + sessionID);
    }

    public void smpAborted(SessionID sessionID) throws OtrException {
        System.out.println("SM verification has been aborted by user: "
                + sessionID);
    }

    public void finishedSessionMessage(SessionID sessionID) throws OtrException {
        System.out.println("SM session was finished. You shouldn't send messages to: "
                + sessionID);

    }

    public void requireEncryptedMessage(SessionID sessionID, String msgText)
            throws OtrException {
        System.out.println("Message can't be sent while encrypted session is not established: "
                + sessionID);

    }

    public void unreadableMessageReceived(SessionID sessionID)
            throws OtrException {
        System.out.println("Unreadable message received from: " + sessionID);
    }

    public void unencryptedMessageReceived(SessionID sessionID, String msg)
            throws OtrException {
        System.out.println("Unencrypted message received: " + msg + " from "
                + sessionID);
    }

    public void showError(SessionID sessionID, String error)
            throws OtrException {
        System.out.println("IM shows error to user: " + error);
    }

    public String getReplyForUnreadableMessage() {
        return "You sent me an unreadable encrypted message.";
    }

    public void sessionStatusChanged(SessionID sessionID) {


    }

    public KeyPair getLocalKeyPair(SessionID paramSessionID) {
        KeyPairGenerator kg;
        try {
            kg = KeyPairGenerator.getInstance("DSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return kg.genKeyPair();
    }

    @Override
    public void askForSecret(SessionID sessionID, String question) {
        System.out.println("Ask for secret from: " + sessionID + ", question: "
                + question);
    }

    @Override
    public void verify(SessionID sessionID, boolean approved) {
        System.out.println("Session was verified: " + sessionID);
        if (!approved)
            System.out.println("Your answer for the question was verified."
                    + "You should ask your opponent too or check shared secret.");
    }

    @Override
    public void unverify(SessionID sessionID) {
        System.out.println("Session was not verified: " + sessionID);
    }

    @Override
    public byte[] getLocalFingerprintRaw(SessionID sessionID) {
        try {
            return new OtrCryptoEngineImpl()
                    .getFingerprintRaw(getLocalKeyPair(sessionID)
                            .getPublic());
        } catch (OtrCryptoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getFallbackMessage() {
        return "Off-the-Record private conversation has been requested. However, you do not have a plugin to support that.";

    }

}