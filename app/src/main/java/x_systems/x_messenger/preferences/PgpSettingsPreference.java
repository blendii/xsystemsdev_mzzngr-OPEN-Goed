package x_systems.x_messenger.preferences;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.pawelgorny.pgpotr.PgpService;

import x_systems.x_messenger.R;

/**
 * Created by Manasseh on 10/12/2016.
 */

public class PgpSettingsPreference extends PreferenceActivity {
    private PgpSettingsPreference pgpSettingsPreference = this;
    private static AlertDialog.Builder builderPrivatePGPKey;
    private static AlertDialog.Builder builderPublicPGPKey;
    private PgpService pgpService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PgpService.PgpServiceBinder binder = (PgpService.PgpServiceBinder) service;
            pgpService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        builderPrivatePGPKey.setTitle(R.string.dialog_title_private_pgp_key);
        builderPrivatePGPKey.setMessage(pgpService.getMyPublicKey().getResultString());
        builderPublicPGPKey.setTitle(R.string.dialog_title_public_pgp_key);
        builderPublicPGPKey.setMessage(pgpService.getMyPrivateKeyArmoredBlock().getResultString());

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        final static String KEY_SHOW_PRIVATE_KEY = "show private key";
        final static String KEY_SHOW_PUBLIC_KEY = "show public key";
        final static String KEY_IMPORT_PRIVATE_KEY = "import private key";

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_pgp_settings);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            switch (key)
            {
                case KEY_SHOW_PRIVATE_KEY:
                    builderPrivatePGPKey.show();
                    break;
                case KEY_SHOW_PUBLIC_KEY:
                    builderPublicPGPKey.show();
                    break;
                case KEY_IMPORT_PRIVATE_KEY:

                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }
}
