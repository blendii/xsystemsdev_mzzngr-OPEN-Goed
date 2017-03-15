package x_systems.x_messenger.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.Objects;

import x_systems.x_messenger.R;
import x_systems.x_messenger.fragments.FilesFragment;
import x_systems.x_messenger.fragments.NotesFragment;
import x_systems.x_messenger.fragments.ViewChatFragment;
import x_systems.x_messenger.fragments.ViewImageFragment;
import x_systems.x_messenger.fragments.ViewNoteFragment;
import x_systems.x_messenger.fragments.ViewVideoFragment;
import x_systems.x_messenger.storage.DataStore;

public class ViewFile extends ExtendedActivity {
    public static String fileName;
    public static String filePath;
    public static int    fileId;

    @Override
    public void onBackPressed(){
        this.finish();
        System.out.println("Im finished");
        fileName = null;
        filePath = null;
        fileId   = -1;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);
        System.out.println(fileName.substring(0, 4));
        if (Objects.equals(fileName.substring(0, 4),  "NOTE")) {
            FragmentTransaction ts = getFragmentManager().beginTransaction();
            ts.addToBackStack(null).replace(R.id.fragment_container, new NotesFragment());
            ts.commit();
        }
        else if (Objects.equals(fileName.substring(0, 4), "CHAT")) {
            FragmentTransaction ts = getFragmentManager().beginTransaction();
            ts.replace(R.id.fragment_container, new ViewNoteFragment());
            ts.commit();
        }
        else if (filePath != null) {
            Fragment l;
            String file_ext = fileName.split("\\.")[fileName.split("\\.").length - 1];
            if (file_ext.equals("jpg")) {
                l = new ViewImageFragment();
                System.out.println("Show image");
            } else {
                l = new ViewVideoFragment();
                System.out.println("Show video");
            }
            FragmentTransaction ts = getFragmentManager().beginTransaction();
            ts.replace(R.id.fragment_container, l);
            ts.commit();
        }
        else
        {
            System.out.println("Null file");
            System.out.println(fileName.substring(0, 4));
            if (Objects.equals(fileName.substring(0, 4), "CHAT"))
            {
                System.out.println("Is a CHAT");
            }
            else if (Objects.equals(fileName.substring(0, 4), "NOTE"))
            {
                System.out.println("Is a NOTE");
            }
        }
    }


}
