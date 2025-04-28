package com.unsalgasimliapplicationsnsug.projectnsug;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class PartnersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PartnerRequestAdapter adapter;
    private final List<PartnerRequest> requestList = new ArrayList<>();
    private FirebaseFirestore db;
    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partners);

        // 1) RecyclerView + Adapter
        recyclerView = findViewById(R.id.rvRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new PartnerRequestAdapter(
                requestList,
                new PartnerRequestAdapter.Listener() {
                    @Override
                    public void onAccept(@NonNull PartnerRequest pr) {
                        db.collection("partnerRequests")
                                .document(pr.getId())
                                .update("status", "accepted")
                                .addOnSuccessListener(v -> {
                                    // link both users as partners
                                    db.collection("users")
                                            .document(pr.getFromUserId())
                                            .collection("partners")
                                            .document(myUid)
                                            .set(new Partner(myUid));
                                    db.collection("users")
                                            .document(myUid)
                                            .collection("partners")
                                            .document(pr.getFromUserId())
                                            .set(new Partner(pr.getFromUserId()));
                                    Toast.makeText(
                                            PartnersActivity.this,
                                            "Partner added!",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                PartnersActivity.this,
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
                                .addOnSuccessListener(v ->
                                        Toast.makeText(
                                                PartnersActivity.this,
                                                "Request declined",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                PartnersActivity.this,
                                                "Decline failed: " + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show()
                                );
                    }
                }
        );
        recyclerView.setAdapter(adapter);

        // 2) Firestore real-time listener
        db = FirebaseFirestore.getInstance();
        db.collection("partnerRequests")
                .whereEqualTo("toUserId", myUid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(
                            @NonNull QuerySnapshot qs,
                            @NonNull FirebaseFirestoreException e
                    ) {
                        if (e != null) {
                            Toast.makeText(
                                    PartnersActivity.this,
                                    "Load error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }
                        List<PartnerRequest> fresh = new ArrayList<>();
                        for (DocumentSnapshot doc : qs.getDocuments()) {
                            PartnerRequest pr = doc.toObject(PartnerRequest.class);
                            if (pr != null) {
                                pr.setId(doc.getId());
                                fresh.add(pr);
                            }
                        }
                        adapter.updateList(fresh);
                    }
                });

        // 3) FAB to add new partner
        FloatingActionButton fab = findViewById(R.id.fabAddPartner);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, AddPartnerActivity.class));
        });
    }
}
