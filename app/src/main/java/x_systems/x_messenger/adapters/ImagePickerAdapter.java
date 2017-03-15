package x_systems.x_messenger.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import x_systems.x_messenger.R;
import x_systems.x_messenger.preferences.PreferenceLoader;

/**
 * Created by jeremiah8100 on 20-10-2016.
 */

public class ImagePickerAdapter extends BaseAdapter {
    private List<int[]> Items = new ArrayList<>();
    private Context Ct;
    private ImageView MainIV;
    private AlertDialog dg;
    private ImageView.OnClickListener onclick = new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            int resourceId = (int)((ImageView)v).getTag();
            new PreferenceLoader(Ct).setAvatarId(resourceId);
            MainIV.setImageResource(resourceId);
            dg.cancel();
        }
    };

    public ImagePickerAdapter(List<int[]> Items, Context Ct, AlertDialog dg, ImageView MainIV){
        this.Ct = Ct;
        this.Items = Items;
        this.dg = dg;
        this.MainIV = MainIV;
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View main = ((LayoutInflater)Ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_imagepicker_layout, null);
        ((ImageView)main.findViewById(R.id.Iv_Rowimg1)).setImageResource(Items.get(position)[0]);
        ((ImageView)main.findViewById(R.id.Iv_Rowimg2)).setImageResource(Items.get(position)[1]);
        ((ImageView)main.findViewById(R.id.Iv_Rowimg3)).setImageResource(Items.get(position)[2]);
        ((ImageView)main.findViewById(R.id.Iv_Rowimg1)).setTag(Items.get(position)[0]);
        ((ImageView)main.findViewById(R.id.Iv_Rowimg2)).setTag(Items.get(position)[1]);
        ((ImageView)main.findViewById(R.id.Iv_Rowimg3)).setTag(Items.get(position)[2]);
        ((ImageView)main.findViewById(R.id.Iv_Rowimg1)).setOnClickListener(onclick);
        ((ImageView)main.findViewById(R.id.Iv_Rowimg2)).setOnClickListener(onclick);
        ((ImageView)main.findViewById(R.id.Iv_Rowimg3)).setOnClickListener(onclick);

        return main;
    }
}
