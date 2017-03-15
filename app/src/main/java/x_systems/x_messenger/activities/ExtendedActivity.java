package x_systems.x_messenger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import x_systems.x_messenger.application.ULTRA;

/**
 * Created by Manasseh on 11/4/2016.
 */

public class ExtendedActivity extends AppCompatActivity {
    protected ULTRA ultra;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ultra = (ULTRA)this.getApplicationContext();
    }
    protected void onResume() {
        super.onResume();
        ultra.setCurrentActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currentActivity = ultra.getCurrentActivity();
        if (this.equals(currentActivity))
            ultra.setCurrentActivity(null);
    }
}
