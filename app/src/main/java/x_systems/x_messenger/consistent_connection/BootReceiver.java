package x_systems.x_messenger.consistent_connection;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.pawelgorny.pgpotr.PgpService;
import com.pawelgorny.pgpotr.tech.PgpOtrConstant;

import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;

/**
 * Created by Manasseh on 10/7/2016.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ConnectionCheck.forcedCheck(context);
            ConnectionCheck.startRepeatedChecks(context);
            Toast.makeText(context, "creating pgp private key pair", Toast.LENGTH_LONG).show();
            // TODO: post previous messages and start clients
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // TODO: logout
            System.out.println("TODO: Logout.");
        }
    }
}