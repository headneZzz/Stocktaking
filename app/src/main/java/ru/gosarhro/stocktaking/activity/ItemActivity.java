package ru.gosarhro.stocktaking.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.model.Item;

public class ItemActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Item item = (Item) getIntent().getSerializableExtra("item");

        ImageView icon = findViewById(R.id.image_view);
        TextView idTextView = findViewById(R.id.id);
        TextView itemNameTextView = findViewById(R.id.item_name);
        TextView locationTextView = findViewById(R.id.password);
        TextView typeTextView = findViewById(R.id.type);
        TextView isWorkingTextView = findViewById(R.id.is_working);
        TextView purchaseDateTextView = findViewById(R.id.purchase_date);
        TextView historyTextView = findViewById(R.id.history);

        item.setIconImage(icon);
        idTextView.setText(item.getId());
        itemNameTextView.setText(item.getName());
        locationTextView.append(String.valueOf(item.getLocation()));
        typeTextView.append(item.getType());
        isWorkingTextView.append(item.isWorking() ? "Работает" : "Списан");
        purchaseDateTextView.append(item.getPurchaseDate());
        historyTextView.append(item.getHistory().toString());
    }

    @Override
    public void finish() {
        super.finish();
    }
}
