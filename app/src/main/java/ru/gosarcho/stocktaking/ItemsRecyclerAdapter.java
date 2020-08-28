package ru.gosarcho.stocktaking;

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
import ru.gosarcho.stocktaking.model.Item;

public class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.ViewHolder> implements Filterable {
    private List<Item> itemListTemp;
    private List<Item> itemListFull;
    List<CardView> cardViewList;  //TODO: Не используется
    private OnItemListener onItemListener;

    public ItemsRecyclerAdapter(List<Item> itemListFull, OnItemListener onItemListener) {
        this.itemListFull = itemListFull;
        this.itemListTemp = new ArrayList<>(this.itemListFull);
        this.cardViewList = new ArrayList<>();
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(itemView, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemListTemp.get(position);
        holder.idTextView.setText(item.getId());
        holder.nameTextView.setText(item.getName());
        item.setIconImage(holder.icon);

        if (!cardViewList.contains(holder.cardView)) {
            cardViewList.add(holder.cardView);
        }
    }


    @Override
    public int getItemCount() {
        return itemListTemp.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                String filteredPattern = constraint.toString().trim().toLowerCase();
                for (Item item : itemListFull) {
                    if (item.getId().toLowerCase().contains(filteredPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemListTemp.clear();
            itemListTemp.addAll((List) results.values);
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
            cardView.setCardBackgroundColor(Color.GREEN);
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
