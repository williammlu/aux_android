package org.mobiledevsberkeley.auxmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ParticipantsActivity extends AppCompatActivity {
    private User currUser;
    private User host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        currUser = AuxSingleton.getInstance().getCurrentUser();

    }
}
    