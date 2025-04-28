package com.unsalgasimliapplicationsnsug.projectnsug;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ImageView ivProfile;
    private TextView tvProfileNote;
    private Button btnMedia, btnNotes, btnNudge, btnPartners, btnLogout;

    private FirebaseFirestore db;
    private StorageReference storage;
    private String myUid;
    private List<String> partnerIds = new ArrayList<>();

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        // Bind views
        ivProfile     = findViewById(R.id.ivProfile);
        tvProfileNote = findViewById(R.id.tvProfileNote);
        btnMedia      = findViewById(R.id.btnMedia);
        btnNotes      = findViewById(R.id.btnNotes);
        btnNudge      = findViewById(R.id.btnNudge);
        btnPartners   = findViewById(R.id.btnPartners);
        btnLogout     = findViewById(R.id.btnLogout);

        // Firebase setup
        db      = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference("profilePics");
        myUid   = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Current UID: " + myUid);

        // 1) Listen for your own Firestore doc changes:
        DocumentReference meRef = db.collection("users").document(myUid);
        meRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snap, @Nullable FirebaseFirestoreException err) {
                if (err != null) {
                    Log.e(TAG, "Profile listener error", err);
                    Toast.makeText(MainActivity.this,
                            "Load error: " + err.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (snap != null && snap.exists()) {
                    Log.d(TAG, "Profile update: " + snap.getData());
                    String photoUrl = snap.getString("photoUrl");
                    String note     = snap.getString("note");

                    if (photoUrl != null) {
                        Glide.with(MainActivity.this)
                                .load(photoUrl)
                                .placeholder(R.drawable.pngegg)
                                .into(ivProfile);
                    }
                    tvProfileNote.setText(
                            note != null ? note
                                    : getString(R.string.tap_to_edit_note)
                    );
                } else {
                    Log.w(TAG, "Profile document missing");
                }
            }
        });

        // 2) Track your partners so we can mirror updates:
        db.collection("users")
                .document(myUid)
                .collection("partners")
                .addSnapshotListener((snap, err) -> {
                    if (err != null) {
                        Log.e(TAG, "Partners listener error", err);
                        return;
                    }
                    partnerIds.clear();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        partnerIds.add(doc.getId());
                    }
                    Log.d(TAG, "Partners now: " + partnerIds);
                });

        // 3) Set up image picker
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri == null) {
                            Log.w(TAG, "No image selected");
                            return;
                        }
                        Log.d(TAG, "Picked image: " + uri);
                        String filename = myUid + ".jpg";
                        storage.child(filename)
                                .putFile(uri)
                                .continueWithTask(task -> {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return storage.child(filename).getDownloadUrl();
                                })
                                .addOnSuccessListener(url -> {
                                    Log.d(TAG, "Uploaded, URL=" + url);
                                    updateField("photoUrl", url.toString());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Upload failed", e);
                                    Toast.makeText(MainActivity.this,
                                            "Upload failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    }
                }
        );
        ivProfile.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // 4) Edit note dialog
        tvProfileNote.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            input.setText(tvProfileNote.getText());
            new AlertDialog.Builder(this)
                    .setTitle("Edit your note")
                    .setView(input)
                    .setPositiveButton("Save", (d, w) -> {
                        String newNote = input.getText().toString();
                        Log.d(TAG, "Saving note: " + newNote);
                        updateField("note", newNote);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 5) Menu buttons
        btnMedia.setOnClickListener(v ->
                startActivity(new Intent(this, MediaActivity.class)));
        btnNotes.setOnClickListener(v ->
                startActivity(new Intent(this, NotesListActivity.class)));
        btnNudge.setOnClickListener(v ->
                startActivity(new Intent(this, NudgeActivity.class)));
        btnPartners.setOnClickListener(v ->
                startActivity(new Intent(this, PartnersActivity.class)));
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Updates `field` in your doc and in each partner’s doc, with logging.
     */
    private void updateField(@NonNull String field, @NonNull Object value) {
        Map<String,Object> m = new HashMap<>();
        m.put(field, value);

        // Update your doc
        db.collection("users")
                .document(myUid)
                .update(m)
                .addOnSuccessListener(a -> Log.d(TAG, "Updated " + field + " on my doc"))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed update on my doc", e);
                    Toast.makeText(this,
                            "Update failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });

        // Mirror to partners
        for (String pid : partnerIds) {
            db.collection("users")
                    .document(pid)
                    .update(m)
                    .addOnSuccessListener(a ->
                            Log.d(TAG, "Mirrored " + field + " to " + pid))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Mirror failed for " + pid, e));
        }
    }
}
