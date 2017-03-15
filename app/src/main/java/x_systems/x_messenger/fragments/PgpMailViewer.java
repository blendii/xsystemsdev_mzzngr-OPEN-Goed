package x_systems.x_messenger.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.application.Loader;
import x_systems.x_messenger.pgp.PGPMailMessage;
import x_systems.x_messenger.storage.DataStore;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;

/**
 * A simple {@link Fragment} subclass.
 */
public class PgpMailViewer extends Fragment {

    private View main;
    public PgpMailViewer() {
        // Required empty public constructor
    }

    PGPMailMessage currentpgpmsg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View main = inflater.inflate(R.layout.activity_chat, container, false);
        this.currentpgpmsg = (PGPMailMessage)DataStore.GetItem("messagecontent", getActivity());
        if(this.currentpgpmsg.IsSecure()){

        }
        ((TextView)main.findViewById(R.id.MsgFtw)).setText(currentpgpmsg.GetMessage());
        this.main = main;
        return main;
    }

    View.OnClickListener clicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String decrypted = BaseActivity.ca.pgpStarter.decryptText(PgpMailViewer.this.currentpgpmsg.GetMessage(), new Database(getActivity()).readProperty(Property.Type.USER_JID));
            ((TextView)PgpMailViewer.this.main.findViewById(R.id.MsgFtw)).setText(decrypted);
        }
    };

}
