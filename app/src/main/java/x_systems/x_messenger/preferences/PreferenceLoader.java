package x_systems.x_messenger.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import x_systems.x_messenger.R;

/**
 * Created by Manasseh on 10/8/2016.
 */

public class PreferenceLoader {
    private final Activity activity;
    private final Context context;
    private final SharedPreferences sharedPreferences;

    public PreferenceLoader(Activity activity) {
        this.activity = activity;
        this.context = activity;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public PreferenceLoader(Context context) {
        this.context = context;
        this.activity = null;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public enum Type {
        MESSAGE_DESTRUCTION_TIME,
        AUTO_LOCK_TIME,
        STATUS,
        AVATAR_ID
    }

    public Object getPreferences(Type preference) {
        switch (preference) {
            case AUTO_LOCK_TIME:
                return getAutoLockTime();
            case MESSAGE_DESTRUCTION_TIME:
                return getMessageDestructionTime();
            case STATUS:
                return getStatus();
            case AVATAR_ID:
                return getAvatarId();
        }
        return null;
    }

    public void loadPreferences(Type preference) {
        switch (preference) {
            case MESSAGE_DESTRUCTION_TIME:
                break;
        }
    }

    public void loadPreferences(Type... preference) {
        for (Type pref : preference) {
            switch (pref) {
                case MESSAGE_DESTRUCTION_TIME:
                    break;
            }
        }
    }

    public void preferencesOnResume(Type preference) {
        switch (preference) {
            case MESSAGE_DESTRUCTION_TIME:
                break;
        }
    }

    public void preferencesOnResume(Type... preference) {
        for (Type pref : preference) {
            switch (pref) {
                case MESSAGE_DESTRUCTION_TIME:
                    break;
            }
        }
    }

    private long getMessageDestructionTime() {
        return sharedPreferences.getLong("msgtime", 10);
    }

    private String getStatus() {
        return sharedPreferences.getString("status", "Available");
    }

    private long getAutoLockTime() {
        return sharedPreferences.getLong("auto_lock", 10);
    }

    private int getAvatarId() {
        return sharedPreferences.getInt("avatar_id", R.drawable.ic_placeholder);
    }

    public void setMessageDestructionTime(Long messageDestructionTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("msgtime", messageDestructionTime);
        editor.apply();
    }

    public void setStatus(String status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("status", status);
        editor.apply();
    }

    public void setAutoLockTime(Long autoLockTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("auto_lock", autoLockTime);
        editor.apply();
    }

    public void setAvatarId(int avatarId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("avatar_id", avatarId);
        editor.apply();
    }
}