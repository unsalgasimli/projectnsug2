package com.unsalgasimliapplicationsnsug.projectnsug;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PartnerRequestsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PartnerRequestAdapter adapter;
    private final List<PartnerRequest> requestList = new ArrayList<>();
    private FirebaseFirestore db;
    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_requests);

        // 1) hook up RecyclerView + adapter
        recyclerView = findViewById(R.id.recycler_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PartnerRequestAdapter(
                requestList,
                new PartnerRequestAdapter.Listener() {
                    @Override
                    public void onAccept(@NonNull PartnerRequest pr) {
                        // update the request’s status in Firestore
                        db.collection("partnerRequests")
                                .document(pr.getId())
                                .update("status", "accepted")
                                .addOnSuccessListener(__ -> {
                                    // link each other as partners
                                    db.collection("users")
                                            .document(pr.getFromUserId())
                                            .collection("partners")
                                            .document(pr.getToUserId())
                                            .set(new Partner(pr.getToUserId()));
                                    db.collection("users")
                                            .document(pr.getToUserId())
                                            .collection("partners")
                                            .document(pr.getFromUserId())
                                            .set(new Partner(pr.getFromUserId()));

                                    Toast.makeText(
                                            PartnerRequestsActivity.this,
                                            "Accepted: " + pr.getFromUserId(),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                PartnerRequestsActivity.this,
                                                "Accept failed: " + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show()
                                );
                    }

                    @Override
                    public void onDecline(@NonNull PartnerRequest pr) {
                        db.collection("partnerRequests")
                                .document(pr.getId())
                                .update("status", "declined")
                                .addOnSuccessListener(__ ->
                                        Toast.makeText(
                                                PartnerRequestsActivity.this,
                                                "Declined: " + pr.getFromUserId(),
                                                Toast.LENGTH_SHORT
                                        ).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                PartnerRequestsActivity.this,
                                                "Decline failed: " + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show()
                                );
                    }
                }
        );
        recyclerView.setAdapter(adapter);

        // 2) init Firestore + current user
        db    = FirebaseFirestore.getInstance();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 3) listen for incoming requests *to* me
        db.collection("partnerRequests")
                .whereEqualTo("toUserId", myUid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(
                            @Nullable QuerySnapshot qs,
                            @Nullable FirebaseFirestoreException e
                    ) {
                        if (e != null) {
                            Toast.makeText(
                                    PartnerRequestsActivity.this,
                                    "Load error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }
                        List<PartnerRequest> fresh = new ArrayList<>();
                        for (DocumentSnapshot doc : qs.getDocuments()) {
                            PartnerRequest pr = doc.toObject(PartnerRequest.class);
                            if (pr != null) {
                                pr.setId(doc.getId());       // carry over the Firestore doc ID
                                fresh.add(pr);
                            }
                        }
                        // update RecyclerView
                        adapter.updateList(fresh);
                    }
                });
    }
}
