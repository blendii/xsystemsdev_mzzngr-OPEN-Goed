package x_systems.x_messenger.exceptions;

/**
 * Created by Manasseh on 10/26/2016.
 */

public class InvalidDatabaseOrServerCredentials extends Exception {
    private String errorMessage;

    public InvalidDatabaseOrServerCredentials() {
        super();
        errorMessage = "x_systems.x_messenger.exceptions.InvalidDatabaseOrServerCredentials: Offline and online authorization should match.";
    }

    public InvalidDatabaseOrServerCredentials(Throwable cause) {
        super(cause);
        errorMessage = "x_systems.x_messenger.exceptions.InvalidDatabaseOrServerCredentials: Offline and online authorization should match.";
    }

    public InvalidDatabaseOrServerCredentials(String message) {
        super(message);
        errorMessage = "x_systems.x_messenger.exceptions.InvalidDatabaseOrServerCredentials: Offline and online authorization should match.";
    }

    public InvalidDatabaseOrServerCredentials(String message, Throwable cause) {
        super(message, cause);
        errorMessage = "x_systems.x_messenger.exceptions.InvalidDatabaseOrServerCredentials: Offline and online authorization should match.";
    }

    protected InvalidDatabaseOrServerCredentials(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        errorMessage = "x_systems.x_messenger.exceptions.InvalidDatabaseOrServerCredentials: Offline and online authorization should match.";
    }

    @Override
    public String toString() {
        return errorMessage;
    }
}
