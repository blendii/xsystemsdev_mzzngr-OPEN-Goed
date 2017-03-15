package x_systems.x_messenger.messages;

import android.content.Context;
import x_systems.x_messenger.storage.Database;

/**
 * Created by Manasseh on 10/8/2016.
 */

public class MessageController {
    private String Username;
    public String getUsername() {return Username;}
    private String Message;
    public String getMessage() {return Message;}
    private long Time;
    public long getTime() {return Time;}

    public MessageController(String username, String message, long time)
    {
        this.Username = username;
        this.Message = message;
        this.Time = time;
    }
}