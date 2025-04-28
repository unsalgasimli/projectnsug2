package com.unsalgasimliapplicationsnsug.projectnsug;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class NudgeActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nudge);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NudgeListFragment())
                    .commit();
        }
    }
}
