package x_systems.x_messenger.adapters;

import android.content.Context;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.zip.Inflater;

import x_systems.x_messenger.R;
import x_systems.x_messenger.pgp.PGPMailMessage;

/**
 * Created by jeremiah8100 on 1-12-2016.
 */

public class MailAdapter extends BaseAdapter
{
    int Main;
    List<PGPMailMessage> Items;
    Context Index;
    public MailAdapter(Context Index, int Main, List<PGPMailMessage> Items){
        this.Items = Items;
        this.Main = Main;
        this.Index = Index;
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
        View mainview = ((LayoutInflater)Index.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.Main, null);
        if(this.Items.get(position).IsSecure()){
            ((ImageView)mainview.findViewById(R.id.security)).setImageResource(R.drawable.ic_lock);
        } else {
            ((ImageView)mainview.findViewById(R.id.security)).setImageResource(R.drawable.ic_unlocked);
        }
        ((TextView)mainview.findViewById(R.id.MailFrom)).setText("ULTRA Message");
        return mainview;
    }

}
