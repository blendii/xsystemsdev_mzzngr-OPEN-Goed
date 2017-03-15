package x_systems.x_messenger.otr;

import net.java.otr4j.OtrEngineImpl;
import net.java.otr4j.OtrException;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.OtrPolicyImpl;
import net.java.otr4j.session.SessionID;
import net.java.otr4j.session.SessionImpl;
import net.java.otr4j.session.SessionStatus;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import x_systems.x_messenger.xmpp.XMPP;

/**
 * Created by Manasseh on 10/9/2016.
 */

public class OtrSession {
    public static List<OtrSession> Chat_Sessions = new ArrayList<OtrSession>();
    public String chatuser = null;
    public SessionID sdUserid1 = null;
    public OtrEngineImpl uUserid1 = null;
    public boolean crypted = false;
    public Semaphore cryptlock = new Semaphore(0);
    private SessionImpl session;
    public org.jivesoftware.smack.chat.Chat xmpp_chat;
    OtrHost host = null;
    public OtrSession(String userid1, String userid2, String keyphrase, org.jivesoftware.smack.chat.Chat chat){
        sdUserid1 = new SessionID(userid1, userid2, keyphrase);
        cryptlock.release();
        host = new OtrHost(new OtrPolicyImpl(OtrPolicy.ALLOW_V2 | OtrPolicy.ERROR_START_AKE), chat, OtrSession.this);
        uUserid1 = new OtrEngineImpl(host);
        chatuser = userid2;
        xmpp_chat = chat;
        Chat_Sessions.add(this);

        try {
            uUserid1.startSession(sdUserid1);
        } catch (OtrException e) {
            e.printStackTrace();
        }

        if(uUserid1.getSessionStatus(sdUserid1) == SessionStatus.ENCRYPTED )
        {
            System.out.println("encrypted session started");
        }else {
            System.out.println("Unsafe session");
        }


    }

    public static OtrSession find_session(String UserId){
        OtrSession output = null;
        for(OtrSession s : Chat_Sessions){
            if(UserId .equalsIgnoreCase(s.chatuser)) {
                output = s;
                break;
            }
        }
        return output;
    }

    public static String handleOtr(Chat chat, Message message) {
        System.out.println("Handle otr");
        OtrSession otrSession = find_session(chat.getParticipant());
        if(otrSession != null) {
            try {
                String decryptedMessage = otrSession.uUserid1.transformReceiving(otrSession.sdUserid1, message.getBody());
                if (decryptedMessage != null) {
                    return decryptedMessage;
                }
            } catch (OtrException e) {
                e.printStackTrace();
            }
        } else {
            try {
                otrSession = new OtrSession(XMPP.connection.getUser(), chat.getParticipant(), null, chat);
                String decryptedMessage = otrSession.uUserid1.transformReceiving(otrSession.sdUserid1, message.getBody());
                if (decryptedMessage != null) {
                    return decryptedMessage;
                }
            } catch (OtrException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
