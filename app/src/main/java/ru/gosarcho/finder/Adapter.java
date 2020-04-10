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

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private JSONArray items;

    Adapter(JSONArray items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView;
        TextView nameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.text_list_item1);
            nameTextView = itemView.findViewById(R.id.text_list_item2);
        }
    }
}
