package com.unsalgasimliapplicationsnsug.projectnsug;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class NudgeListFragment extends Fragment {
    private RecyclerView     rv;
    private NudgeAdapter     adapter;
    private final List<Nudge> nudges = new ArrayList<>();
    private FirebaseFirestore db;
    private String            myUid;

    public NudgeListFragment() {
        super(R.layout.fragment_nudge_list);
    }

    @Override
    public void onCreate(@Nullable Bundle saved) {
        super.onCreate(saved);
        db    = FirebaseFirestore.getInstance();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        super.onViewCreated(view, saved);

        rv = view.findViewById(R.id.rv_nudges);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new NudgeAdapter(nudges, n -> {
            Toast.makeText(requireContext(),
                    "Clicked “" + n.getTitle() + "”", Toast.LENGTH_SHORT).show();
        });
        rv.setAdapter(adapter);

        // listen to *all* nudges, then filter locally:
        db.collection("nudges")
                .addSnapshotListener((snap, err) -> {
                    if (err != null) {
                        Toast.makeText(requireContext(),
                                "Load failed: " + err.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    nudges.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        Nudge n = d.toObject(Nudge.class);
                        if (n == null) continue;
                        n.setId(d.getId());
                        // only show those you sent or received
                        if (myUid.equals(n.getFromUserId()) ||
                                myUid.equals(n.getToUserId())) {
                            nudges.add(n);
                        }
                    }
                    adapter.update(nudges);
                });

        // TODO: wire up fab_add_nudge → your Create/Send dialog or menu
    }
}
