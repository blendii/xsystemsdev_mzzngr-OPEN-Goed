package x_systems.x_messenger.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.LoginActivity;

/**
 * Created by Manasseh on 10/8/2016.
 */

public class NotificationBuilder {
    private Context context;
    private final NotificationCompat.Builder builder;
    private final Type notificationType;
    private Integer iconId = R.drawable.ic_placeholder;

    public NotificationBuilder(Type notificationType, Context context) {
        this.notificationType = notificationType;
        this.context = context;
        builder = new NotificationCompat.Builder(context);
    }

    public enum Type {
        MESSAGE(0),
        MESSAGE_DESTRUCTION_TIME(1),
        FRIEND_REQUEST(2);

        private int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public void setTitle(String title) {
        builder.setContentTitle(title);
    }

    public void setText(String text) {
        switch(notificationType)
        {
            case MESSAGE:
                builder.setContentText(text);
                break;
            case MESSAGE_DESTRUCTION_TIME:
                String destructiontime = "All messages will be cleared in "+text+" minutes.";
                builder.setContentText(destructiontime);
                break;
            case FRIEND_REQUEST:
                break;
        }
    }

    public void setRemoteInput() {
        Intent destination = new Intent(context, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(destination);
        PendingIntent destinationPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.addAction(new NotificationCompat.Action.Builder(
                R.drawable.ic_reply,
                "REPLY",
                destinationPendingIntent)
                .addRemoteInput(
                        new RemoteInput.Builder("key_text_reply")
                                .setLabel("Reply")
                                .build())
                .build());
    }

    public void setDestination(Class c) {
        Intent destination = new Intent(context, c);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(c);
        stackBuilder.addNextIntent(destination);
        PendingIntent destinationPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(destinationPendingIntent);
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
    }

    public void showNotification() {
        switch(notificationType)
        {
            case MESSAGE:
                builder.setSmallIcon(R.drawable.logo);
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), iconId));
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                builder.setDefaults(Notification.DEFAULT_LIGHTS);
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                break;
            case MESSAGE_DESTRUCTION_TIME:
                builder.setSmallIcon(R.drawable.logo);
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_alarm));
                builder.setColor(0xFFFF0000);
                builder.setDefaults(Notification.DEFAULT_LIGHTS);
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                break;
            case FRIEND_REQUEST:
                break;
        }
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationType.getValue(), builder.build());
    }

    public static void cancel(Context context, Type... notificationTypes) {
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        for (Type notificationType : notificationTypes)
        {
            if (notificationType == Type.MESSAGE_DESTRUCTION_TIME) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.drawable.logo);
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_alarm));
                builder.setColor(0xFFFF0000);
                builder.setContentText("All messages have been cleared.");
                builder.setDefaults(Notification.DEFAULT_LIGHTS);
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                notificationManager.notify(notificationType.getValue(), builder.build());
            }
            else {
                notificationManager.cancel(notificationType.getValue());
            }
        }
    }
}
