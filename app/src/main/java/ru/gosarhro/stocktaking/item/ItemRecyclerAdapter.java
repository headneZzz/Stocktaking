package ru.gosarhro.stocktaking.item;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ru.gosarhro.stocktaking.R;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder> implements Filterable {
    private List<Item> itemListFiltered;
    private List<Item> itemListFull;
    private OnItemListener onItemListener;
    private String filterQuery;

    public ItemRecyclerAdapter(List<Item> itemListFull, OnItemListener onItemListener) {
        this.itemListFull = itemListFull;
        this.itemListFiltered = new ArrayList<>(this.itemListFull);
        this.onItemListener = onItemListener;
        this.filterQuery = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(itemView, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item;
        if (filterQuery == null) {
            item = itemListFull.get(position);
        } else {
            item = itemListFiltered.get(position);
        }
        holder.idTextView.setText(item.getId());
        holder.nameTextView.setText(item.getName());
        item.setIconImage(holder.icon);
        holder.cardView.setCardBackgroundColor(item.isChecked() ? holder.itemView.getContext().getResources().getColor(R.color.colorAccent2) : Color.WHITE);
    }


    @Override
    public int getItemCount() {
        if (filterQuery == null)
            return itemListFull.size();
        else
            return itemListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();
            if (constraint != null && constraint.length() != 0) {
                filterQuery = constraint.toString().trim().toLowerCase();
                for (Item item : itemListFull) {
                    if (item.getId().toLowerCase().contains(filterQuery)) {
                        filteredList.add(item);
                    }
                }
            } else {
                filteredList.addAll(itemListFull);
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemListFiltered.clear();
            itemListFiltered.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView idTextView;
        TextView nameTextView;
        ImageView icon;
        OnItemListener onItemListener;

        ViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_row_card);
            idTextView = itemView.findViewById(R.id.text_list_item_id);
            nameTextView = itemView.findViewById(R.id.text_list_item_name);
            icon = itemView.findViewById(R.id.image_view);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
