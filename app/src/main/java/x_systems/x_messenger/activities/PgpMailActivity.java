package x_systems.x_messenger.activities;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import x_systems.x_messenger.R;
import x_systems.x_messenger.application.Loader;
import x_systems.x_messenger.fragments.PgpMail;
import x_systems.x_messenger.fragments.PgpMailViewer;
import x_systems.x_messenger.fragments.pgpmail_send;
import x_systems.x_messenger.storage.DataStore;

public class PgpMailActivity extends AppCompatActivity {

    FragmentTransaction ts;
    PgpMail mailer;
    PgpMailViewer viewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pgp_mail);
        DataStore.Init(this);
        ((TextView)findViewById(R.id.TvContactName)).setText((String)DataStore.GlobalStore.GetItem("contactname"));
        mailer = new PgpMail();
        viewer = new PgpMailViewer();
        ShowMails();
    }
    boolean canback = true;
    public void ShowMails() {
        ts = getFragmentManager().beginTransaction();
        ts.addToBackStack(null).replace(R.id.MailFragment, mailer, "Mailer");
        ts.commit();
        canback = true;
    }

    public void ShowViewer(){
        ts = getFragmentManager().beginTransaction();
        ts.addToBackStack("Mailer").replace(R.id.MailFragment, viewer, "Viewer");
        ts.commit();
        canback = false;
    }

    public void ShowSender(){
        ts = getFragmentManager().beginTransaction();
        ts.addToBackStack("Mailer").replace(R.id.MailFragment, new pgpmail_send());
        ts.commit();
        canback = false;
    }

    @Override
    public void onBackPressed() {
        if(canback)
            this.finish();
        else {
            Loader.Show(this);
            ShowMails();
        }


    }
}
