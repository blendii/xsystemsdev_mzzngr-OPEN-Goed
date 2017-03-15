package x_systems.x_messenger.adapters;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Objects;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.ViewFile;
import x_systems.x_messenger.fragments.FilesFragment;
import x_systems.x_messenger.storage.ChatsList;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.FilesList;
import x_systems.x_messenger.storage.Property;

/**
 * Created by Manasseh on 10/14/2016.
 */

public class FilesListAdapter extends ArrayAdapter<String> {
    private FilesList filesList;
    private final static int resourceId = R.layout.adapter_files_list;
    private Activity activity;
    private FilesFragment headFragment;

    public FilesListAdapter(Activity activity, FilesList filesList, FilesFragment headFragment) {
        super(activity, resourceId, filesList.fileTitles);
        this.filesList = filesList;
        this.activity = activity;
        this.headFragment = headFragment;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView = inflater.inflate(resourceId, null, true);
        System.out.println(filesList.fileTitles.get(position).substring(0, 5));
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordCorrect(getContext());

                    ViewFile.fileName = filesList.fileTitles.get(position);
                    ViewFile.filePath = filesList.filePaths.get(position);
                    if(!filesList.fileTitles.get(position).substring(0, 5).equals("IMAGE")&& !filesList.fileTitles.get(position).substring(0, 5).equals("VIDEO")) {
                        System.out.println("EQUAL: " + filesList.fileTitles.get(position).substring(0, 5).equals("IMAGE") + "SECONDEQUAL: " + filesList.fileTitles.get(position).substring(0, 5).equals("VIDEO"));
                        ViewFile.fileId = filesList.fileIds.get(position);

                    }

            }
        });
        ((Button)rowView.findViewById(R.id.BtDelete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!filesList.fileTitles.get(position).substring(0, 5).equals("IMAGE")&& !filesList.fileTitles.get(position).substring(0, 5).equals("VIDEO")) {

                    Dialog("Delete \"" + filesList.fileTitles.get(position) + "\"?", filesList.fileIds.get(position), filesList.fileTitles.get(position));
                } else {
                    Dialog("Delete \"" + filesList.fileTitles.get(position) + "\"?", filesList.filePaths.get(position));
                }
                FilesListAdapter.this.headFragment.CreateFilesListWithOnItemClickListener(FilesListAdapter.this.headFragment.getView());
            }
        });
        ImageView fileType = (ImageView) rowView.findViewById(R.id.fileType);
        fileType.setImageResource(filesList.fileTypes.get(position).getResourceId());
        TextView fileName = (TextView) rowView.findViewById(R.id.fileName);
        fileName.setText(filesList.fileTitles.get(position));
        return rowView;
    }



    private void Dialog(String message, final int id, final String filename){
        AlertDialog.Builder adbuilder = new AlertDialog.Builder(this.getContext());
        LayoutInflater ifs = this.headFragment.getActivity().getLayoutInflater();
        View main = ifs.inflate(R.layout.dialog_yesorno, null);
        ((TextView)main.findViewById(R.id.TvMessage)).setText(message);
        adbuilder.setView(main);
        final AlertDialog ad = adbuilder.create();
        ((Button)main.findViewById(R.id.BtYes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (filename.substring(0,4)) {
                    case "NOTE":
                        new Database(getContext()).deleteNote(id);
                        break;
                    case "CHAT":
                        new Database(getContext()).deleteFile(id);
                        break;
                }
                ad.dismiss();
                FilesListAdapter.this.headFragment.CreateFilesListWithOnItemClickListener(FilesListAdapter.this.headFragment.getView());
            }
        });
        ((Button)main.findViewById(R.id.BtNo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
                FilesListAdapter.this.headFragment.CreateFilesListWithOnItemClickListener(FilesListAdapter.this.headFragment.getView());
            }
        });
        ad.show();

    }

    private void Dialog(String message, final String filepath){
        AlertDialog.Builder adbuilder = new AlertDialog.Builder(this.getContext());
        LayoutInflater ifs = this.headFragment.getActivity().getLayoutInflater();
        View main = ifs.inflate(R.layout.dialog_yesorno, null);
        ((TextView)main.findViewById(R.id.TvMessage)).setText(message);
        adbuilder.setView(main);
        final AlertDialog ad = adbuilder.create();
        ((Button)main.findViewById(R.id.BtYes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(filepath);
                file.delete();
                ad.dismiss();
                FilesListAdapter.this.headFragment.CreateFilesListWithOnItemClickListener(FilesListAdapter.this.headFragment.getView());
            }
        });
        ((Button)main.findViewById(R.id.BtNo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
                FilesListAdapter.this.headFragment.CreateFilesListWithOnItemClickListener(FilesListAdapter.this.headFragment.getView());
            }
        });
        ad.show();

    }

    private void passwordCorrect(final Context context) {
        LayoutInflater inflater = ((BaseActivity)context).getLayoutInflater();
        final View content = inflater.inflate(R.layout.dialog_add_contact, null);
        ((EditText)content.findViewById(R.id.contactName)).setHint("******");
        ((EditText)content.findViewById(R.id.contactName)).setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Password required");
        builder.setMessage("Enter password:");
        builder.setView(content);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText contact = (EditText) content.findViewById(R.id.contactName);
                        if (Objects.equals(contact.getText().toString(), new Database(context).readProperty(Property.Type.ENCRYPTED_PASSWORD)))
                            BaseActivity.canClose = false;
                            context.startActivity(new Intent(context, ViewFile.class));
                        dialog.dismiss();
                    }
                }
        );
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.create().show();
    }
}
