package com.unsalgasimliapplicationsnsug.projectnsug;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.VH> {
    private final List<Note> list;

    public NoteAdapter(List<Note> list) {
        this.list = list;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int i) {
        Note n = list.get(i);
        h.text.setText(n.text);
        h.time.setText(DateFormat.getDateTimeInstance()
                .format(new Date(n.timestamp)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView text, time;
        VH(View v) {
            super(v);
            text = v.findViewById(R.id.noteText);
            time = v.findViewById(R.id.noteTime);
        }
    }
}
