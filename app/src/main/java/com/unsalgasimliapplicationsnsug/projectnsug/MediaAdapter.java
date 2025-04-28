package com.unsalgasimliapplicationsnsug.projectnsug;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.VH> {

    public interface OnMediaClickListener {
        void onClick(Media m);
    }

    private final List<Media> items;
    private final OnMediaClickListener listener;

    public MediaAdapter(List<Media> items, OnMediaClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH vh, int pos) {
        Media m = items.get(pos);
        vh.name.setText(m.getName());
        // load thumbnail if image
        if ("image".equals(m.getType())) {
            Glide.with(vh.thumb.getContext())
                    .load(m.getUrl())
                    .into(vh.thumb);
        } else {
            vh.thumb.setImageResource(R.drawable.pngegg);
        }
        vh.itemView.setOnClickListener(v -> listener.onClick(m));
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public void update(List<Media> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    public Media removeAt(int pos) {
        Media m = items.remove(pos);
        notifyItemRemoved(pos);
        return m;
    }

    static class VH extends RecyclerView.ViewHolder {
        final CircleImageView thumb;
        final TextView name;
        VH(@NonNull View v) {
            super(v);
            thumb = v.findViewById(R.id.media_thumb);
            name  = v.findViewById(R.id.media_name);
        }
    }
}
