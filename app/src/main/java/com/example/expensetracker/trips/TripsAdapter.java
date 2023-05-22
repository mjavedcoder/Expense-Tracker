package com.example.expensetracker.trips;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.database.Trip;

public class TripsAdapter extends ListAdapter<Trip, TripsAdapter.TripViewHolder> {

    private OnTripItemClickListener listener;

    protected TripsAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_trip_item, parent, false);
        return new TripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = getItem(position);
        if (trip == null) return;
        holder.textViewName.setText(trip.getName());
        holder.textViewDestination.setText(trip.getDestination());
        holder.textViewDate.setText(trip.getDate());
        holder.rootLayout.setOnClickListener(v -> {
            listener.onTripItemClick(trip);
        });
    }

    private static final DiffUtil.ItemCallback<Trip> DIFF_CALLBACK = new DiffUtil.ItemCallback<Trip>() {
        @Override
        public boolean areItemsTheSame(Trip oldItem, Trip newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Trip oldItem, Trip newItem) {
            return oldItem.getId() == newItem.getId() && oldItem.getName().equals(newItem.getName()) && oldItem.getDestination().equals(newItem.getDestination());
        }
    };

    class TripViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rootLayout;
        private TextView textViewName;
        private TextView textViewDestination;
        private TextView textViewDate;


        public TripViewHolder(@NonNull View itemView) {
            super(itemView);

            rootLayout = itemView.findViewById(R.id.rootLayout);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDestination = itemView.findViewById(R.id.textViewDestination);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }

    public interface OnTripItemClickListener {
        void onTripItemClick(Trip trip);
    }

    public void setOnItemClickListener(OnTripItemClickListener listener) {
        this.listener = listener;
    }
}