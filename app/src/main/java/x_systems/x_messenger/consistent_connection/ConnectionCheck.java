package x_systems.x_messenger.consistent_connection;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import x_systems.x_messenger.services.ConnectionService;

/**
 * Created by Manasseh on 10/7/2016.
 */

public class ConnectionCheck {
    private static int minutes = 1;
    public static void startRepeatedChecks(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ConnectionService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        alarmManager.cancel(pendingIntent);
        // minutes <= 0 means notifications are disabled
        if (minutes > 0) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    minutes * 60000, pendingIntent);
        }
    }

    public static void forcedCheck(Context context) {
        Intent intent = new Intent(context, ConnectionService.class);
        context.startService(intent);
    }
}
