package x_systems.x_messenger.local;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.fragments.FilesFragment;
import x_systems.x_messenger.storage.Database;

/**
 * Created by Manasseh on 10/9/2016.
 */

public class Local {

    public static int dpToPixels(final Context context, final int dp)
    {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static void wipeAllData(final Context context) {
        new Database(context).wipeDatabase();
        //new PgpService().cleanDatabase();
        FilesFragment.clearMedia(context.getFilesDir());
    }
}
