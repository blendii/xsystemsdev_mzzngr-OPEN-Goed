package x_systems.x_messenger.canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import x_systems.x_messenger.fragments.NotesFragment;

/**
 * Created by Manasseh on 11/3/2016.
 */

public class Notes extends EditText {
    Paint l = new Paint();
    public int textwidth = 0;
    public String oldText = "";
    NotesFragment Index;
    LinearLayout parent;
    public Notes(NotesFragment Index, LinearLayout parent) {
        super(Index.getActivity());
        l.setColor(Color.BLACK);
        this.Index = Index;
        this.parent = parent;
        this.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Notes.this.addTextChangedListener(tw);
                Notes.this.setOnFocusChangeListener(cl);
            }
        });

    }

    View.OnFocusChangeListener cl = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(parent.indexOfChild(v) == parent.getChildCount()-1){
                Index.addnote(parent);
            }
        }
    };

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            oldText = getText().toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (Index.safe == true)
                Index.safe = false;
            Rect bounds = new Rect();
            getPaint().getTextBounds(getText().toString(), 0, getText().length(), bounds);
            if (bounds.width() >= (getWidth() - getPaddingLeft() - getPaddingRight())) {
                Notes.this.clearFocus();
                setText(oldText);
                if (parent.indexOfChild(Notes.this) == parent.getChildCount() - 1) {
                    Notes next = Index.addnote(parent);
                    next.requestFocus();
                    Index.addnote(parent);
                } else if (parent.getChildCount() - 1 > parent.indexOfChild(Notes.this)) {
                    ((Notes) parent.getChildAt(parent.indexOfChild(Notes.this) + 1)).requestFocus();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



}
