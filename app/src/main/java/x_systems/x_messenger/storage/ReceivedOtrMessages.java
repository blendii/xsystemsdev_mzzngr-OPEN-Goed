package x_systems.x_messenger.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manasseh on 10/9/2016.
 */

public class ReceivedOtrMessages {
    private List<String> decryptedMessaged = new ArrayList<>();
    private List<String> participants = new ArrayList<>();
    private List<Long> unixTimes = new ArrayList<>();

    public void add(String participant, String decryptedMessage, long time) {
        participants.add(participant);
        decryptedMessaged.add(decryptedMessage);
        unixTimes.add(time);
    }

    public Integer size() {
        return decryptedMessaged.size();
    }

    public String getParticipant() {
        return participants.get(0);
    }

    public String getDecryptedMessage() {
        return decryptedMessaged.get(0);
    }

    public Long getUnixTime() {
        return unixTimes.get(0);
    }

    public void removeFirst() {
        participants.remove(0);
        decryptedMessaged.remove(0);
        unixTimes.remove(0);
    }
}
