package com.example.passengerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passengerapp.R;
import com.example.passengerapp.models.Trip;
import com.example.passengerapp.tools.Const;
import com.example.passengerapp.tools.Tool;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    List<Trip> listTrips;
    Context context;

    public OrderAdapter(List<Trip> listTrips, Context context) {
        this.listTrips = listTrips;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = listTrips.get(position);
        holder.textDestination.setText(trip.getDropOffName());

        holder.textTime.setText(Tool.getShortTime(trip.getCreateTime()));

        if (trip.getStatus().equals(Const.cancelByDriver)
            || trip.getStatus().equals(Const.cancelByPassenger)) {
            holder.textStatus.setText("Canceled");
            holder.textStatus.setTextColor(context.getResources().getColor(R.color.secondary_text));
            holder.textStatus.setBackground(context.getResources().getDrawable(R.drawable.button_background_grey_line));
        } else if (trip.getStatus().equals(Const.success)) {
            holder.textStatus.setText("Ended");
            holder.textStatus.setTextColor(context.getResources().getColor(R.color.secondary_text));
            holder.textStatus.setBackground(context.getResources().getDrawable(R.drawable.button_background_grey_line));
        } else {
            holder.textStatus.setText("In progress");
            holder.textStatus.setTextColor(context.getResources().getColor(R.color.white));
            holder.textStatus.setBackground(context.getResources().getDrawable(R.drawable.button_background_green));
        }

        if (trip.getVehicleType().equals(Const.car)) {
            holder.imgVehicle.setImageResource(R.drawable.car);
        } else {
            holder.imgVehicle.setImageResource(R.drawable.shipper);
        }
    }

    @Override
    public int getItemCount() {
        return listTrips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgVehicle;
        TextView textDestination, textTime, textStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVehicle = itemView.findViewById(R.id.img_vehicle);
            textDestination = itemView.findViewById(R.id.text_destination);
            textTime = itemView.findViewById(R.id.text_time);
            textStatus = itemView.findViewById(R.id.text_status);

        }
    }
}
