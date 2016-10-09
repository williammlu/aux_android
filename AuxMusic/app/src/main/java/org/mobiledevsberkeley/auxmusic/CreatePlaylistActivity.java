package org.mobiledevsberkeley.auxmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class CreatePlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        CheckBox publicPrivate = (CheckBox) findViewById(R.id.publicPrivate);
        final TextView passwordText = (TextView) findViewById(R.id.passwordTextView);
        final EditText editText = (EditText) findViewById(R.id.passwordEditText);


        publicPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordText.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.VISIBLE);
                }
                else {
                    passwordText.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                }
            }
        });
    }
}
