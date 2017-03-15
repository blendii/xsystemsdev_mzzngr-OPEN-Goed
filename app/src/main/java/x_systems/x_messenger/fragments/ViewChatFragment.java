package x_systems.x_messenger.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.ViewFile;
import x_systems.x_messenger.storage.Database;

/**
 * Created by Manasseh on 10/17/2016.
 */

public class ViewChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_view_chat, container, false);
        ((TextView)content.findViewById(R.id.conversation)).setText(new Database(getActivity()).readSavedMessages(ViewFile.fileName));
        return content;
    }
}
