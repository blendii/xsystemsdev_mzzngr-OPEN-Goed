package x_systems.x_messenger.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manasseh on 10/14/2016.
 */

public class MessagesList {
    public List<Long> unixTimes = new ArrayList<>();
    public List<String> names = new ArrayList<>();
    public List<Boolean> isSenders = new ArrayList<>();
    public List<String> messages = new ArrayList<>();

    public void add(Long unixTime, String name, Boolean isSender, String message) {
        unixTimes.add(unixTime);
        names.add(name);
        isSenders.add(isSender);
        messages.add(message);
    }

    public void clear() {
        unixTimes.clear();
        names.clear();
        isSenders.clear();
        messages.clear();
    }
}
