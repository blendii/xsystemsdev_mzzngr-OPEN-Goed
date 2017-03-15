package x_systems.x_messenger.storage;

/**
 * Created by Manasseh on 10/8/2016.
 */

public class Property {
    public enum Type {
        USER_JID(0),
        USERNAME(1),
        ENCRYPTED_PASSWORD(2),
        WHIPE_OUT_PASSWORD(3),
        SESSION_ID(4),
        MESSAGE_DESTRUCTION_TIME(5),
        AUTO_LOCK_TIME(6),
        SALT(7);
        private int value;
        Type(int value) {
            this.value = value;
        }

        int getValue() {
            return this.value;
        }
    }
}
