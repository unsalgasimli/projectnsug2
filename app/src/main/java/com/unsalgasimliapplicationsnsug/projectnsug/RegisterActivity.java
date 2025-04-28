package com.unsalgasimliapplicationsnsug.projectnsug;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailEt, passEt;
    private Button registerBtn;
    private TextView goLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth & Firestore
        mAuth       = FirebaseAuth.getInstance();
        db          = FirebaseFirestore.getInstance();

        // UI references
        emailEt     = findViewById(R.id.emailEt);
        passEt      = findViewById(R.id.passEt);
        registerBtn = findViewById(R.id.registerBtn);
        goLogin     = findViewById(R.id.goLogin);

        registerBtn.setOnClickListener(v -> {
            // Read & normalize inputs
            String email = emailEt.getText().toString().trim().toLowerCase();
            String pass  = passEt.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1) Create Auth user
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            // Auth failed
                            Exception e = task.getException();
                            Log.e(TAG, "Auth failed", e);
                            Toast.makeText(this,
                                    "Registration failed: " + (e != null ? e.getMessage() : "unknown"),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // 2) On success, write profile into Firestore
                        String uid = mAuth.getCurrentUser().getUid();
                        Map<String,Object> user = new HashMap<>();
                        user.put("email", email);

                        db.collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.i(TAG, "User profile created for UID=" + uid);
                                    // Move on to MainActivity
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to save profile", e);
                                    Toast.makeText(this,
                                            "Failed to save profile: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // createUserWithEmail call itself failed
                        Log.e(TAG, "createUserWithEmail failed", e);
                        Toast.makeText(this,
                                "Registration error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        });

        goLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }
}
