package x_systems.x_messenger.storage;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;

import java.util.ArrayList;
import java.util.List;

import x_systems.x_messenger.services.ConnectionService;
import x_systems.x_messenger.xmpp.XMPP;

/**
 * Created by Manasseh on 10/9/2016.
 */

public class ContactChats {
    private static List<String> JIDs = new ArrayList<>();
    private static List<Chat> contactChats = new ArrayList<>();
    public static boolean isSet = false;

    public static void add(String JID) {
        ChatManager chatManager = ChatManager.getInstanceFor(XMPP.connection);
        Chat chat = chatManager.createChat(JID);
        chat.addMessageListener(new ConnectionService().new ChatMessageReader());
        System.out.println("New ChatMessageReader For: " + JID);
        JIDs.add(JID);
        contactChats.add(chat);
        isSet = true;
    }

    public static boolean contains(String JID) {
        return JIDs.contains(JID);
    }

    public static Chat get(String JID) {
        if (JID == null)
            System.out.println("ContactChats.get(String JID):  JID == null");
        for (String _JID : JIDs)
        {
            System.out.println("JID: "+_JID);
        }
        return contactChats.get(JIDs.indexOf(JID));
    }
}
