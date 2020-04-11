package ru.gosarcho.finder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.ViewHolder> {
    private JSONArray items;
    private OnItemListener onItemListener;

    ItemsRecyclerAdapter(JSONArray items, OnItemListener onItemListener) {
        this.items = items;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = items.getJSONObject(position);
            holder.idTextView.setText(jsonObject.optString("id"));
            holder.nameTextView.setText(jsonObject.optString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return items.length();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView idTextView;
        TextView nameTextView;
        OnItemListener onItemListener;

        ViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.text_list_item1);
            nameTextView = itemView.findViewById(R.id.text_list_item2);
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
