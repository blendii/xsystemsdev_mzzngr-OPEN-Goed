package x_systems.x_messenger.xmpp;

import android.content.Context;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.iqlast.LastActivityManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.fragments.ChatFragment;
import x_systems.x_messenger.storage.ContactChats;
import x_systems.x_messenger.storage.ContactList;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;

/**
 * Created by Manasseh on 10/9/2016.
 */

public class XMPP {
    public static AbstractXMPPConnection connection;
    public static Presence.Type isAvailable = Presence.Type.available;
    public static RosterEntry user;
    public static boolean isOnline = false;

    public void UpdateUsers(final Context context) {
        ContactList contactList = new ContactList();
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            Roster roster = Roster.getInstanceFor(connection);
            if (!roster.isLoaded()) {
                try {
                    roster.reloadAndWait();
                } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            Collection<RosterEntry> entries = roster.getEntries();
            System.out.println("entries.size(): "+entries.size());
            for (RosterEntry rosterEntry : entries)
            {
                String JID = rosterEntry.getUser();
                try {
                    if (Objects.equals(JID, new Database(context).readProperty(Property.Type.USER_JID)))
                    {
                        user = rosterEntry;
                    }
                    else {
                        contactList.JIDs.add(JID);
                        contactList.names.add(JID.split("@")[0]);
                        contactList.status.add("empty");
                        contactList.iconIds.add(0);
                        contactList.encryptionTypes.add(R.drawable.otr);

                        if (!ContactChats.contains(rosterEntry.getUser()))
                            ContactChats.add(rosterEntry.getUser());
                    }
                } catch (Exception e) {
                    System.out.println("OTRBRO " + R.drawable.otr);
                    System.out.println("Error195 " + e.toString());
                }

                isOnline = true;
            }

            new Database(context).updateContacts(contactList);
        }
        else {
            isOnline = false;
        }
    }



    public static boolean CheckOnlineStatus(String jid) {


                try {
                    LastActivityManager lastActivityManager = LastActivityManager.getInstanceFor(connection);
                    if(lastActivityManager.isLastActivitySupported(jid)) {
                        Long lastActivity = lastActivityManager.getLastActivity(jid).lastActivity;
                        Date date = new Date(lastActivity);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                        if(lastActivity > System.currentTimeMillis() - 30)
                        return true;
                    }
                } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                return false;


    }

    public void deleteContact(String contact) {

        try {
            System.out.println("Login");
            Roster roster = Roster.getInstanceFor(connection);
            String removeJID = contact ;
            Collection<RosterEntry> entries = roster.getEntries();
            for (RosterEntry entry : entries) {
                // remove roster whith removeJID
                if (removeJID.equals(entry.getUser())) {
                     roster.removeEntry(entry);
                }

            }
        }
        catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        }
    }



    public void changePassword(String newPassword) throws Exception {
        AccountManager accountManager = AccountManager.getInstance(connection);
        try {
            accountManager.changePassword(newPassword);
        } catch (SmackException.NotConnectedException | SmackException.NoResponseException | XMPPException.XMPPErrorException e) {
            throw new Exception(e);
        }
    }

    public Integer getContactImageId(String JID) {
        try {
            VCardManager vCardManager = VCardManager.getInstanceFor(connection);
            VCard vCard = null;
            vCard = vCardManager.loadVCard();
            String iconId = vCard.getField("iconId");
            if (iconId == null)
                return R.drawable.ic_placeholder;
            else
                return Integer.valueOf(iconId);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return R.drawable.ic_placeholder;
    }

    private String getJID(final RosterEntry rosterEntry) {
        return rosterEntry.getUser();
    }

    public void addContact(String JID, Context context) throws SmackException.NotConnectedException, SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        Roster roster = Roster.getInstanceFor(connection);
        roster.createEntry(JID, JID.split("@")[0], null);
        Presence subscribe = new Presence(Presence.Type.subscribed);
        subscribe.setTo(JID);
        XMPP.connection.sendStanza(subscribe);
    }
}
