package x_systems.x_messenger.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.Objects;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.ViewFile;
import x_systems.x_messenger.adapters.FilesListAdapter;
import x_systems.x_messenger.listeners.NavigateListener;
import x_systems.x_messenger.storage.DataStore;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.FilesList;
import x_systems.x_messenger.storage.Property;

/**
 * Created by Manasseh on 10/14/2016.
 */

public class FilesFragment  extends Fragment {
    private final int API_VERSION = android.os.Build.VERSION.SDK_INT;
    private Context context;
    private FilesListAdapter listAdapter;
    private NavigateListener navigateListener;

    public static void clearMedia(File directory)
    {
        for(File file : directory.listFiles())
        {
            if (file.isDirectory())
                clearMedia(file);

            String file_ext = "";
            if(file.getName().split("\\.").length > 1)
                file_ext = file.getName().split("\\.")[file.getName().split("\\.").length - 1];

            if(file_ext.equals("jpg"))
                file.delete();
            else if(file_ext.equals("mp4"))
                file.delete();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            context = getActivity();
            navigateListener = (NavigateListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NavigateListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            navigateListener = (NavigateListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NavigateListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.fragment_files, container, false);
        context = getActivity();
        if (ChatFragment.isRunning)
        {
            content.findViewById(R.id.write).setVisibility(View.INVISIBLE);
            content.findViewById(R.id.delete).setVisibility(View.INVISIBLE);
            content.findViewById(R.id.back).setVisibility(View.VISIBLE);
            content.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().popBackStackImmediate();
                }
            });
        }
        else {
            content.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(content.getContext());
                    builder.setTitle("DELETE FILES");
                    builder.setMessage("YOU ARE TO DELETE ALL CURRENT CHATS.\n\nARE YOU SURE?");
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Database(content.getContext()).clearFiles();
                            clearMedia(content.getContext().getFilesDir());
                            CreateFilesListWithOnItemClickListener(getView());
                            //((BaseActivity) content.getContext()).recreate();
                        }
                    });
                    builder.create().show();
                }
            });
            content.findViewById(R.id.write).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateListener.OnRequestNavigate("note", null);
                }
            });
        }

        CreateFilesListWithOnItemClickListener(content);
        return content;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);






    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        CreateFilesListWithOnItemClickListener(getView());
    }

    public void CreateFilesListWithOnItemClickListener(View mainer)
    {
        File media = new File(BaseActivity.ca.getFilesDir(), "/Media");
        System.out.println("FILES CMON");
        if(!media.exists())
            media.mkdirs();
        final FilesList filesList = new Database(BaseActivity.ca).getAllFiles();
        for(File f : BaseActivity.ca.getFilesDir().listFiles()){

            String file_ext = "";
            if(f.getName().split("\\.").length > 1)
                file_ext = f.getName().split("\\.")[f.getName().split("\\.").length - 1];
            System.out.println(file_ext);
            if(file_ext.equals("jpg"))
                filesList.add(FilesList.Type.PHOTO, f.getName(), f.getAbsolutePath());
            else if(file_ext.equals("mp4"))
                filesList.add(FilesList.Type.VIDEO, f.getName(), f.getAbsolutePath());
        }

        ListView listFiles = (ListView)mainer.findViewById(R.id.listFiles);
        listAdapter = new FilesListAdapter(getActivity(), filesList, this);

        listFiles.setAdapter(listAdapter);
    }

    private void showDialog(String text)
    {
        AlertDialog.Builder adbuilder = new AlertDialog.Builder(getActivity());
        adbuilder.setMessage(text);
        adbuilder.create().show();
    }



    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
            System.out.println("FilesFragment.listAdapter.notifyDataSetChanged()");
        }
        else
            System.out.println("FilesFragment.listAdapter.notifyDataSetChanged() == NULL");
    }
}