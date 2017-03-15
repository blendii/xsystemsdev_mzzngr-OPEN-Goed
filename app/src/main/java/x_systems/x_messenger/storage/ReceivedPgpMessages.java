package x_systems.x_messenger.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manasseh on 11/15/2016.
 */

public class ReceivedPgpMessages {
    private List<String> encryptedMessages = new ArrayList<>();
    private List<String> participants = new ArrayList<>();
    private List<Long> unixTimes = new ArrayList<>();

    public void add(String participant, String encryptedMessage, long time) {
        participants.add(participant);
        encryptedMessages.add(encryptedMessage);
        unixTimes.add(time);
    }

    public Integer size() {
        return encryptedMessages.size();
    }

    public String getParticipant() {
        return participants.get(0);
    }

    public String getEncryptedMessage() {
        return encryptedMessages.get(0);
    }

    public Long getUnixTime() {
        return unixTimes.get(0);
    }

    public void removeFirst() {
        participants.remove(0);
        encryptedMessages.remove(0);
        unixTimes.remove(0);
    }
}
