package com.example.passengerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passengerapp.R;
import com.example.passengerapp.interfaces.OnItemClickListener;
import com.example.passengerapp.models.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<Place> listPlaces;
    private Context context;
    private OnItemClickListener listener;

    public PlaceAdapter(List<Place> listPlaces, Context context, OnItemClickListener listener) {
        this.listPlaces = listPlaces;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_place_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = listPlaces.get(position);
        holder.textMain.setText(place.getMainText());
        holder.textSecondary.setText(place.getSecondaryText());

        holder.bind(listPlaces.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return listPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textMain, textSecondary;
        public void bind(final Place item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMain = itemView.findViewById(R.id.text_main);
            textSecondary = itemView.findViewById(R.id.text_secondary);

        }
    }

}
