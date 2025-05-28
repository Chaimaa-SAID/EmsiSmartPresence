package com.example.emsismartpresence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RattrapageAdapter extends RecyclerView.Adapter<RattrapageAdapter.ViewHolder> {

    private List<RattrapageItem> rattrapageList;

    public RattrapageAdapter(List<RattrapageItem> rattrapageList) {
        this.rattrapageList = rattrapageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rattrapage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RattrapageItem item = rattrapageList.get(position);
        holder.textModule.setText(item.getModule());
        holder.textDate.setText("Date : " + item.getDate());
        holder.textHeure.setText("Heure : " + item.getHeure());
    }

    @Override
    public int getItemCount() {
        return rattrapageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textModule, textDate, textHeure;

        ViewHolder(View itemView) {
            super(itemView);
            textModule = itemView.findViewById(R.id.textModule);
            textDate = itemView.findViewById(R.id.textDate);
            textHeure = itemView.findViewById(R.id.textHeure);
        }
    }
}
