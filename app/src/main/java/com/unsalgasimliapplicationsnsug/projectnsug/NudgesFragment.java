package com.unsalgasimliapplicationsnsug.projectnsug;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

public class NudgesFragment extends Fragment {
    public NudgesFragment() {
        super(R.layout.fragment_nudges);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        // TODO: load & show sent/received nudges, handle FAB menu (create vs send)
    }
}
