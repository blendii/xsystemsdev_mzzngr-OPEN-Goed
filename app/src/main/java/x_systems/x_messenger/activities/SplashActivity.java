package x_systems.x_messenger.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import x_systems.x_messenger.xmpp.XMPP;

/**
 * Created by Manasseh on 10/13/2016.
 */

public class SplashActivity extends ExtendedActivity {
    public static boolean isApplicationRunning = false;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isApplicationRunning = true;

        if (XMPP.connection != null && XMPP.connection.isConnected() && XMPP.connection.isAuthenticated())
            goToActivity(BaseActivity.class);
        else
            goToActivity(LoginActivity.class);
    }

    private void goToActivity(final Class activityClass)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO: Don't add login to back
                Intent goToActivity = new Intent(context, activityClass);
                startActivity(goToActivity);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        isApplicationRunning = true;
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isApplicationRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isApplicationRunning = true;
    }
}