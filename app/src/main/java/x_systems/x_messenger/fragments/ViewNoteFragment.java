package x_systems.x_messenger.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.ViewFile;
import x_systems.x_messenger.storage.Database;

import static x_systems.x_messenger.activities.ViewFile.fileId;
import static x_systems.x_messenger.activities.ViewFile.fileName;
import static x_systems.x_messenger.activities.ViewFile.filePath;

/**
 * Created by Manasseh on 11/3/2016.
 */

public class ViewNoteFragment extends Fragment {

    public ViewNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View main = inflater.inflate(R.layout.fragment_view_note, container, false);
        if (Objects.equals(fileName.substring(0, 4), "NOTE"))
        ((TextView)main.findViewById(R.id.myNote)).setText(new Database(getActivity()).getNote(fileId));
        else if (Objects.equals(fileName.substring(0, 4), "CHAT"))
            ((TextView)main.findViewById(R.id.myNote)).setText(new Database(getActivity()).readSavedMessages(ViewFile.filePath));
        return main;
    }
}
