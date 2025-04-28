package com.unsalgasimliapplicationsnsug.projectnsug;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    private EditText noteEt;
    private Button saveBtn;
    private FirebaseFirestore db;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        noteEt    = findViewById(R.id.noteEt);
        saveBtn   = findViewById(R.id.saveBtn);
        db        = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        saveBtn.setOnClickListener(v -> {
            String text = noteEt.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter a note", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String,Object> note = new HashMap<>();
            note.put("text", text);
            note.put("timestamp", System.currentTimeMillis());
            note.put("userId", currentUid);

            db.collection("notes")
                    .add(note)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving note: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );
        });
    }
}
