package com.unsalgasimliapplicationsnsug.projectnsug;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.List;

public class PartnerRequestAdapter
        extends RecyclerView.Adapter<PartnerRequestAdapter.VH> {

    /** Callback interface for accept/decline events */
    public interface Listener {
        void onAccept(@NonNull PartnerRequest pr);
        void onDecline(@NonNull PartnerRequest pr);
    }

    private final List<PartnerRequest> list;
    private final Listener callback;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * @param list     initial list of PartnerRequest objects
     * @param callback to handle accept/decline taps
     */
    public PartnerRequestAdapter(
            @NonNull List<PartnerRequest> list,
            @NonNull Listener callback
    ) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_partner_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        PartnerRequest pr = list.get(pos);

        // 1) Lookup the from‐user’s email in Firestore
        db.collection("users")
                .document(pr.getFromUserId())
                .get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    String email = doc.getString("email");
                    h.email.setText(email != null ? email : "Unknown");
                })
                .addOnFailureListener(e -> {
                    h.email.setText("Error");
                });

        // 2) Wire up the buttons to your callbacks
        h.acceptBtn.setOnClickListener(v -> {
            callback.onAccept(pr);
            // remove from list immediately
            list.remove(pos);
            notifyItemRemoved(pos);
        });
        h.declineBtn.setOnClickListener(v -> {
            callback.onDecline(pr);
            list.remove(pos);
            notifyItemRemoved(pos);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /** Replace the adapter’s data and refresh the RecyclerView */
    public void updateList(@NonNull List<PartnerRequest> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    /** ViewHolder class */
    static class VH extends RecyclerView.ViewHolder {
        final TextView email;
        final Button acceptBtn, declineBtn;

        VH(View v) {
            super(v);
            email      = v.findViewById(R.id.userEmail);
            acceptBtn  = v.findViewById(R.id.acceptBtn);
            declineBtn = v.findViewById(R.id.declineBtn);
        }
    }
}
