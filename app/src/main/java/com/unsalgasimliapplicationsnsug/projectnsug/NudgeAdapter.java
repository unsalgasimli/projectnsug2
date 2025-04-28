package com.unsalgasimliapplicationsnsug.projectnsug;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NudgeAdapter extends RecyclerView.Adapter<NudgeAdapter.VH> {
    public interface OnNudgeClickListener { void onClick(Nudge n); }

    private final List<Nudge> list;
    private final OnNudgeClickListener clicker;

    public NudgeAdapter(List<Nudge> list, OnNudgeClickListener clicker) {
        this.list    = list;
        this.clicker = clicker;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_nudge, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Nudge n = list.get(i);
        h.title.setText(n.getTitle());
        h.body .setText(n.getBody());
        h.itemView.setOnClickListener(v -> clicker.onClick(n));
    }

    @Override public int getItemCount() { return list.size(); }

    public void update(List<Nudge> fresh) {
        list.clear();
        list.addAll(fresh);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView title, body;
        VH(@NonNull View iv) {
            super(iv);
            title = iv.findViewById(R.id.text_nudge_title);
            body  = iv.findViewById(R.id.text_nudge_body);
        }
    }
}
