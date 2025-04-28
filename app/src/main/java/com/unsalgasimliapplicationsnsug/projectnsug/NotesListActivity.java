package com.unsalgasimliapplicationsnsug.projectnsug;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class NotesListActivity extends AppCompatActivity {
    private RecyclerView recycler;
    private NoteAdapter adapter;
    private List<Note> notes = new ArrayList<>();
    private FirebaseFirestore db;
    private String currentUid;
    private List<String> partnerIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(notes);
        recycler.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 1) load your partners
        db.collection("users")
                .document(currentUid)
                .collection("partners")
                .get()
                .addOnSuccessListener(snap -> {
                    partnerIds.clear();
                    for (DocumentSnapshot doc : snap) {
                        partnerIds.add(doc.getId());
                    }
                    // always include your own ID
                    partnerIds.add(currentUid);

                    // 2) query notes whose userId is in partnerIds
                    db.collection("notes")
                            .whereIn("userId", partnerIds)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .addSnapshotListener((noteSnap, e) -> {
                                if (e != null) return;
                                notes.clear();
                                for (DocumentSnapshot nd : noteSnap.getDocuments()) {
                                    Note n = nd.toObject(Note.class);
                                    n.id = nd.getId();
                                    notes.add(n);
                                }
                                adapter.notifyDataSetChanged();
                            });
                });
    }
}
