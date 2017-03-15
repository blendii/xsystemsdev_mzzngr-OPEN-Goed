package x_systems.x_messenger.storage;

import java.util.ArrayList;
import java.util.List;

import x_systems.x_messenger.fragments.ChatFragment;

/**
 * Created by Manasseh on 10/14/2016.
 */

public class ChatsList {
    public List<Integer> photos = new ArrayList<>();
    public List<String> contactNames = new ArrayList<>();
    public List<String> JIDs = new ArrayList<>();
    public List<ChatFragment.Type> encryptionTypes = new ArrayList<>();
    public List<String> dateTimes = new ArrayList<>();

    public void add(Integer photo, String JID, String contactName, ChatFragment.Type encryptionType, String dateTime) {
        photos.add(photo);
        contactNames.add(contactName);
        JIDs.add(JID);
        encryptionTypes.add(encryptionType);
        dateTimes.add(dateTime);
    }
}
