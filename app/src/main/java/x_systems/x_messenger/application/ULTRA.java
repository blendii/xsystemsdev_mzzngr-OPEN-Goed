package x_systems.x_messenger.application;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Manasseh on 11/4/2016.
 */

public class ULTRA extends MultiDexApplication {
    public void onCreate() {
        super.onCreate();
    }

    private Activity currentActivity = null;
    public Activity getCurrentActivity(){
        return currentActivity;
    }
    public void setCurrentActivity(Activity currentActivity){
        this.currentActivity = currentActivity;
    }
}
