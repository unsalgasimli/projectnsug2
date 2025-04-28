package com.unsalgasimliapplicationsnsug.projectnsug;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AddPartnerDialog {

    public interface Callback {
        void onSent();
        void onError(@NonNull String message);
    }

    /**
     * Shows a dialog to type a partner’s email, looks up their UID,
     * and writes a PartnerRequest into Firestore.
     *
     * @param ctx     any Context (e.g. your Activity)
     * @param myUid   the current user’s UID
     * @param cb      callback invoked on success or failure
     */
    public static void show(
            @NonNull Context ctx,
            @NonNull String myUid,
            @NonNull Callback cb
    ) {
        EditText input = new EditText(ctx);
        input.setHint("Partner’s email");

        new AlertDialog.Builder(ctx)
                .setTitle("Add Partner")
                .setView(input)
                .setPositiveButton("Send", (dialog, which) -> {
                    String email = input.getText().toString().trim();
                    if (email.isEmpty()) {
                        Toast.makeText(ctx, "Email can’t be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    // 1) Find that user by email
                    db.collection("users")
                            .whereEqualTo("email", email)
                            .limit(1)
                            .get()
                            .addOnSuccessListener((QuerySnapshot snap) -> {
                                if (snap.isEmpty()) {
                                    Toast.makeText(ctx, "User not found", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String otherUid = snap.getDocuments().get(0).getId();

                                // 2) Build & send the PartnerRequest
                                PartnerRequest pr = new PartnerRequest(myUid, otherUid);
                                db.collection("partnerRequests")
                                        .add(pr)
                                        .addOnSuccessListener(ref -> {
                                            Toast.makeText(ctx, "Request sent", Toast.LENGTH_SHORT).show();
                                            cb.onSent();
                                        })
                                        .addOnFailureListener(e -> {
                                            cb.onError(e.getMessage());
                                        });
                            })
                            .addOnFailureListener(e -> {
                                cb.onError(e.getMessage());
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
