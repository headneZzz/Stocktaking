package ru.gosarhro.stocktaking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.gosarhro.stocktaking.model.Location;

public class LocationsRecyclerAdapter extends RecyclerView.Adapter<LocationsRecyclerAdapter.ViewHolder> {
    private List<Location> locations;
    private OnLocationListener onLocationListener;

    public LocationsRecyclerAdapter(List<Location> locations, OnLocationListener onLocationListener) {
        this.locations = locations;
        this.onLocationListener = onLocationListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_row, parent, false);
        return new ViewHolder(itemView, onLocationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.locationText.setText(R.string.location);
        holder.locationText.append(String.valueOf(locations.get(position).getId()));
        locations.get(position).setIconImage(holder.checkIcon);
    }


    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView locationText;
        ImageView checkIcon;
        OnLocationListener onLocationListener;

        ViewHolder(View itemView, OnLocationListener onLocationListener) {
            super(itemView);
            locationText = itemView.findViewById(R.id.text_location);
            checkIcon = itemView.findViewById(R.id.check_icon);
            this.onLocationListener = onLocationListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onLocationListener.onLocationClick(getAdapterPosition());
        }
    }

    public interface OnLocationListener {
        void onLocationClick(int position);
    }
}
