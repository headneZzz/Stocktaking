package ru.gosarcho.finder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import ru.gosarcho.finder.R;
import ru.gosarcho.finder.model.Item;

public class ItemActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Item item = (Item) getIntent().getSerializableExtra("item");

        TextView idTextView = findViewById(R.id.id);
        idTextView.setText(item.getId());

        TextView itemNameTextView = findViewById(R.id.item_name);
        itemNameTextView.setText(item.getName());

        TextView locationTextView = findViewById(R.id.location);
        locationTextView.append(String.valueOf(item.getLocation()));

        TextView typeTextView = findViewById(R.id.type);
        typeTextView.append(item.getType());

        ImageView icon = findViewById(R.id.image_view);
        item.setIconImage(icon);

        TextView isWorkingTextView = findViewById(R.id.is_working);
        isWorkingTextView.append(item.isWorking() ? "Работает" : "Списан");

        TextView purchaseDateTextView = findViewById(R.id.purchase_date);
        purchaseDateTextView.append(item.getPurchaseDate());

        TextView historyTextView = findViewById(R.id.history);
        historyTextView.append(item.getHistory().toString());
    }

    @Override
    public void finish() {
        super.finish();
    }
}
