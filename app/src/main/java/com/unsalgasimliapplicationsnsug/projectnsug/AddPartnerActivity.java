package com.unsalgasimliapplicationsnsug.projectnsug;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPartnerActivity extends AppCompatActivity {
    private TextInputEditText inputEmail;
    private Button             sendBtn;
    private FirebaseFirestore  db;
    private String             myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partner);

        inputEmail = findViewById(R.id.inputEmail);
        sendBtn    = findViewById(R.id.btnSendRequest);
        db         = FirebaseFirestore.getInstance();
        myUid      = FirebaseAuth.getInstance().getCurrentUser().getUid();

        sendBtn.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                inputEmail.setError("Enter an email");
                return;
            }

            // 1) Look up the target user by email
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.isEmpty()) {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // assume the first matching doc
                        String toUid = snapshot.getDocuments().get(0).getId();

                        // 2) Build a partner‐request object
                        Map<String,Object> req = new HashMap<>();
                        req.put("fromUserId", myUid);
                        req.put("toUserId",   toUid);
                        req.put("timestamp",  System.currentTimeMillis());
                        req.put("status",     "pending");

                        // 3) Write it to Firestore
                        db.collection("partnerRequests")
                                .add(req)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(this,
                                            "Request sent!",
                                            Toast.LENGTH_SHORT).show();
                                    finish(); // back to main screen
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this,
                                                "Error: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show()
                                );
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    "Error looking up user: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );
        });
    }
}
