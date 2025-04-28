package com.unsalgasimliapplicationsnsug.projectnsug;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MediaActivity extends AppCompatActivity {
    private RecyclerView mediaRecycler;
    private MediaAdapter adapter;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        mediaRecycler = findViewById(R.id.mediaRecycler);
        mediaRecycler.setLayoutManager(new LinearLayoutManager(this));

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db  = FirebaseFirestore.getInstance();

        adapter = new MediaAdapter(new ArrayList<>(), this::previewMedia);
        mediaRecycler.setAdapter(adapter);

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(
                    @NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder vh,
                    @NonNull RecyclerView.ViewHolder t) {
                return false;
            }
            @Override public void onSwiped(
                    @NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                Media m = adapter.removeAt(pos);
                db.collection("media")
                        .document(m.getId())
                        .delete()
                        .addOnFailureListener(e ->
                                Toast.makeText(MediaActivity.this,
                                        "Delete failed: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show());
            }
        }).attachToRecyclerView(mediaRecycler);

        FloatingActionButton fab = findViewById(R.id.fab_add_media);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddMediaActivity.class))
        );

        // Load “my” media once
        db.collection("media")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(this::onMediaLoaded)
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Load failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void onMediaLoaded(QuerySnapshot qs) {
        List<Media> list = new ArrayList<>();
        for (QueryDocumentSnapshot doc : qs) {
            list.add(doc.toObject(Media.class));
        }
        adapter.update(list);
    }

    private void previewMedia(Media m) {
        // TODO: image fullscreen / play audio
        Toast.makeText(this, "Preview: " + m.getName(), Toast.LENGTH_SHORT).show();
    }
}
