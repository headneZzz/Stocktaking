package ru.gosarhro.stocktaking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;

import androidx.appcompat.app.AppCompatActivity;
import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.model.item.Item;
import ru.gosarhro.stocktaking.model.item.ItemType;

public class ItemActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Item item = (Item) getIntent().getSerializableExtra("item");

        LinkedList<String> list = new LinkedList<>();
        for (ItemType itemType : ItemType.values()) {
            list.add(itemType.getType());
        }
        String[] TYPES = list.toArray(new String[list.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_menu_popup_item, TYPES);
        AutoCompleteTextView typeDropdown = findViewById(R.id.activity_item_dropdown_type);
        typeDropdown.setAdapter(adapter);

        ImageView icon = findViewById(R.id.image_view);
        TextView idTextView = findViewById(R.id.id);
        TextView itemNameTextView = findViewById(R.id.activity_item_et_name);
        TextView locationTextView = findViewById(R.id.activity_item_et_location);
        CheckBox isWorkingCheckBox = findViewById(R.id.activity_item_cb_working);
        TextView purchaseDateTextView = findViewById(R.id.purchase_date);
        TextView historyTextView = findViewById(R.id.history);
        Button saveButton = findViewById(R.id.activity_item_btn_save);

        item.setIconImage(icon);
        idTextView.setText(item.getId());
        itemNameTextView.setText(item.getName());
        locationTextView.append(String.valueOf(item.getLocation()));
        typeDropdown.setText(item.getType());
        isWorkingCheckBox.setChecked(item.isWorking());
        purchaseDateTextView.append(item.getPurchaseDate());
        historyTextView.append(item.getHistory().toString());
        saveButton.setOnClickListener(v -> {
            try {
                if (ItemType.findByType(typeDropdown.getText().toString()) == null)
                    throw new Exception();
                db.collection("items")
                        .document(item.getId())
                        .update(
                                "name", itemNameTextView.getText().toString(),
                                "location", Integer.parseInt(locationTextView.getText().toString()),
                                "type", ItemType.findByType(typeDropdown.getText().toString()),
                                "working", isWorkingCheckBox.isChecked()
                        )
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getApplicationContext(), "Данные отправлены", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
            }
        });
    }
}
