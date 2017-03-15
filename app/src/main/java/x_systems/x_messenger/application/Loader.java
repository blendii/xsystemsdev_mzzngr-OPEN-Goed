package x_systems.x_messenger.application;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import x_systems.x_messenger.R;

/**
 * Created by jeremiah8100 on 5-12-2016.
 */

public class Loader {
    static AlertDialog dg;
    public static void Dismiss(){
        if(Loader.dg != null){
            System.out.println("Loader dissmissed");
            Loader.dg.dismiss();
            Loader.dg = null;
        }
        System.out.println("Loader is null!");
    }

    public static void Show(Context ct){
        if(dg == null) {
            WindowManager wm = (WindowManager) ct.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            AlertDialog.Builder bg = new AlertDialog.Builder(ct);
            View sample = ((LayoutInflater)ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.basic_loader, null);
            bg.setView(sample);
            Loader.dg = bg.create();
            Loader.dg.getWindow().setBackgroundDrawableResource(R.drawable.white);
            Loader.dg.show();
        }
    }
}
