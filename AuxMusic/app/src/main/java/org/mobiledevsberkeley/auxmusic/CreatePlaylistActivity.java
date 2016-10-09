package org.mobiledevsberkeley.auxmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreatePlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        final CheckBox publicPrivate = (CheckBox) findViewById(R.id.publicPrivate);
        final TextView passwordText = (TextView) findViewById(R.id.passwordTextView);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final EditText nameText = (EditText) findViewById(R.id.nameTextView);


        publicPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordText.setVisibility(View.VISIBLE);
                    passwordEditText.setVisibility(View.VISIBLE);
                }
                else {
                    passwordText.setVisibility(View.GONE);
                    passwordEditText.setVisibility(View.GONE);
                }
            }
        });



        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference nameRef = firebase.child(getString(R.string.playlistName));
                DatabaseReference password = firebase.child(getString(R.string.playlistPassword));
                DatabaseReference passwordProtect = firebase.child(getString(R.string.playlistPasswordProtect));
                String name = nameText.getText().toString();
                nameRef.setValue(name);
                passwordProtect.setValue(publicPrivate.isChecked());
                if (publicPrivate.isChecked()) {
                    password.setValue(passwordEditText.getText().toString());
                }

            }
        });
    }
}
