package com.unsalgasimliapplicationsnsug.projectnsug;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddMediaActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO = 42;

    private Button btnPickImage, btnPickAudio, btnRecordVoice, btnSendTextNote, btnSendNudge;
    private FirebaseFirestore db;
    private StorageReference storage;
    private String currentUid;

    private MediaRecorder recorder;
    private String voiceFilePath;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    private final ActivityResultLauncher<String> pickAudioLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onAudioPicked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);

        // find views
        btnPickImage    = findViewById(R.id.btnPickImage);
        btnPickAudio    = findViewById(R.id.btnPickAudio);
        btnRecordVoice  = findViewById(R.id.btnRecordVoice);
        btnSendTextNote = findViewById(R.id.btnSendTextNote);
        btnSendNudge    = findViewById(R.id.btnSendNudge);

        // init Firebase
        db         = FirebaseFirestore.getInstance();
        storage    = FirebaseStorage.getInstance().getReference();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // pick image from gallery
        btnPickImage.setOnClickListener(v ->
                pickImageLauncher.launch("image/*")
        );

        // pick audio from device
        btnPickAudio.setOnClickListener(v ->
                pickAudioLauncher.launch("audio/*")
        );

        // record voice (with permission)
        btnRecordVoice.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{ Manifest.permission.RECORD_AUDIO },
                        REQUEST_RECORD_AUDIO
                );
            } else {
                toggleRecording();
            }
        });

        // send a text note
        btnSendTextNote.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Enter note")
                    .setView(input)
                    .setPositiveButton("Send", (dlg, which) -> {
                        String note = input.getText().toString().trim();
                        if (note.isEmpty()) {
                            Toast.makeText(this, "Cannot send empty note", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Map<String,Object> m = new HashMap<>();
                        m.put("userId",    currentUid);
                        m.put("type",      "text");
                        m.put("url",       null);
                        m.put("text",      note);
                        m.put("timestamp", System.currentTimeMillis());
                        db.collection("media")
                                .add(m)
                                .addOnSuccessListener(d -> {
                                    Toast.makeText(this, "Note sent", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show()
                                );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // send a nudge
        btnSendNudge.setOnClickListener(v -> {
            Map<String,Object> m = new HashMap<>();
            m.put("userId",    currentUid);
            m.put("type",      "nudge");
            m.put("url",       null);
            m.put("text",      null);
            m.put("timestamp", System.currentTimeMillis());
            db.collection("media")
                    .add(m)
                    .addOnSuccessListener(d -> {
                        Toast.makeText(this, "Nudge sent!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            toggleRecording();
        } else {
            Toast.makeText(this, "Audio permission is required to record voice", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleRecording() {
        if (recorder == null) {
            // start recording
            voiceFilePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                    + "/voice_" + System.currentTimeMillis() + ".3gp";
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(voiceFilePath);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                recorder.prepare();
                recorder.start();
                Toast.makeText(this, "Recording… tap again to stop", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Record error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                recorder = null;
            }
        } else {
            // stop recording
            try {
                recorder.stop();
            } catch (RuntimeException ignore) { }
            recorder.release();
            recorder = null;
            Uri recordedVoiceUri = Uri.fromFile(new File(voiceFilePath));
            uploadMedia("voice", recordedVoiceUri);
        }
    }

    private void onImagePicked(Uri uri) {
        if (uri != null) uploadMedia("image", uri);
    }

    private void onAudioPicked(Uri uri) {
        if (uri != null) uploadMedia("audio", uri);
    }

    private void uploadMedia(String type, Uri uri) {
        String path = "media/" + type + "/" + currentUid + "/" + System.currentTimeMillis();
        storage.child(path)
                .putFile(uri)
                .continueWithTask(task -> storage.child(path).getDownloadUrl())
                .addOnSuccessListener(downloadUri -> {
                    Map<String,Object> m = new HashMap<>();
                    m.put("userId",    currentUid);
                    m.put("type",      type);
                    m.put("url",       downloadUri.toString());
                    m.put("text",      null);
                    m.put("timestamp", System.currentTimeMillis());
                    db.collection("media")
                            .add(m)
                            .addOnSuccessListener(d -> {
                                Toast.makeText(this, type + " uploaded", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Meta write error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Upload failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}
