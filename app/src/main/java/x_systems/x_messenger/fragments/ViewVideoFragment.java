package x_systems.x_messenger.fragments;


import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.ViewFile;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewVideoFragment extends Fragment {


    public ViewVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View main = inflater.inflate(R.layout.fragment_view_video, container, false);
        VideoView vv = (VideoView) main.findViewById(R.id.VvViewVideo);
        MediaController mc = new MediaController(getActivity());
        mc.removeAllViews();
        vv.setMediaController(mc);
        vv.start();

        return main;
    }

}
