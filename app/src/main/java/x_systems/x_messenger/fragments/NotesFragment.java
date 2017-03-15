package x_systems.x_messenger.fragments;

/**
 * Created by Manasseh on 10/8/2016.
 */

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.ViewFile;
import x_systems.x_messenger.canvas.Notes;
import x_systems.x_messenger.storage.Database;

import static x_systems.x_messenger.activities.ViewFile.fileId;
import static x_systems.x_messenger.activities.ViewFile.fileName;
import static x_systems.x_messenger.activities.ViewFile.filePath;

/**
 * TODO: Notify about:
 * Chat selected
 */
public class NotesFragment extends Fragment {
    public boolean safe = true;
    private boolean isNew = true;
    LinearLayout tv;

    public NotesFragment () {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View main = (View)inflater.inflate(R.layout.fragment_notes, container, false);
        tv = (LinearLayout)main.findViewById(R.id.Rlnotes);
        ((TextView)main.findViewById(R.id.TvNotetitle)).setText("Mynote");

        if (fileName != null && Objects.equals(fileName.substring(0, 4), "NOTE"))
            isNew = false;
        if (isNew) {
            for(int a=0;a < 5;a++) {
                addnote(tv);
            }
        }
        else {
            openSaveText(new Database(getActivity()).getNote(fileId));
        }
        ((Button)main.findViewById(R.id.BtSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });

        ((Button)main.findViewById(R.id.BtClose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Close();
            }
        });
        return main;
    }

    public Notes addnote(final LinearLayout tv) {
        final Notes l = new Notes(this, tv);
        l.setBackground(getActivity().getResources().getDrawable(R.drawable.noteline, null));
        l.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        l.setTextColor(Color.BLACK);
        l.setTextSize(15);
        l.setLines(1);
        l.setMaxLines(1);
        l.setSingleLine(true);
        l.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        l.setEllipsize(TextUtils.TruncateAt.END);
        l.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (40 * getResources().getDisplayMetrics().density)));
        tv.addView(l);
        return l;
    }

    private void reset(){

    }

    public void Save(){
        if(safe == false)
            safe = true;
        Toast.makeText(getActivity(), "Note successfully saved!", Toast.LENGTH_LONG).show();
        if (isNew) {
            new Database(getActivity()).writeNote(getSaveText(tv));
        }
        else
            new Database(getActivity()).updateNote(fileId, getSaveText(tv));
    }

    public void Close(){
        if(safe){

            if(getActivity() instanceof ViewFile){
                ((ViewFile)getActivity()).finish();

            } else {
                FilesFragment fileFragment = (FilesFragment)getFragmentManager().findFragmentByTag(FilesFragment.class.getSimpleName());
                fileFragment.CreateFilesListWithOnItemClickListener(fileFragment.getView());
                getFragmentManager().beginTransaction().remove(this).commit();
                fileName = null;
                filePath = null;
                fileId   = -1;
            }
            fileName = null;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to exit without saving?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getFragmentManager().popBackStackImmediate();
                    fileName = null;
                    filePath = null;
                    fileId   = -1;
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setCancelable(true);
            builder.create().show();
        }
    }

    public String getSaveText(LinearLayout noteholder){
        String output = "";
        for(int a = 0;a < noteholder.getChildCount();a++){
            output += ((Notes)noteholder.getChildAt(a)).getText().toString();
            if(noteholder.getChildCount() > 2 && a < noteholder.getChildCount()-2)
                output += "\n";
        }
        return output;
    }

    public void openSaveText(String text) {
        int lt = text.split("\n").length;

        for (int a=0;a < lt;a++) {
            Notes nt = addnote(tv);
            nt.setText(text.split("\n")[a]);
        }
    }
}