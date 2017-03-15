package x_systems.x_messenger.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import x_systems.x_messenger.R;

import static x_systems.x_messenger.activities.BaseActivity.canClose;

public class contact_info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        final EditText aaaaa = (EditText) findViewById(R.id.contact_name_edittext);
        String a = getIntent().getStringExtra("EXTRA_SESSION_ID");
        aaaaa.setText(a);

    }
}
