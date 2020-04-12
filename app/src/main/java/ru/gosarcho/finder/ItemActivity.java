package ru.gosarcho.finder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ItemActivity extends Activity implements AsyncResponse {
    private TextView idTextView;
    private TextView itemNameTextView;
    private TextView locationTextView;
    private TextView previousLocationTextView;
    private TextView executorTextView;
    private TextView receiptDateTextView;
    private TextView isDecommissionedTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        idTextView = findViewById(R.id.id);
        itemNameTextView = findViewById(R.id.item_name);
        locationTextView = findViewById(R.id.location);
        previousLocationTextView = findViewById(R.id.previous_location);
        executorTextView = findViewById(R.id.executor);
        receiptDateTextView = findViewById(R.id.receiptDate);
        isDecommissionedTextView = findViewById(R.id.is_decommissioned);

        String id = Objects.requireNonNull(getIntent().getExtras()).getString("Id");
        ConnectTask task = new ConnectTask(this);
        task.delegate = this;
        task.execute("https://find-inventory-api-test.herokuapp.com/" + id);
    }

    @Override
    public void processFinish(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            idTextView.setText(jsonObject.getString("id"));
            itemNameTextView.setText(jsonObject.getString("name"));
            locationTextView.append(jsonObject.getString("location"));
            previousLocationTextView.append(jsonObject.getString("previousLocation"));
            executorTextView.append(" " + jsonObject.getString("executor"));
            boolean isDecommissioned = jsonObject.getBoolean("decommissioned");
            isDecommissionedTextView.append(" " + (isDecommissioned ? "Да" : "Нет"));
            receiptDateTextView.append(" " + jsonObject.getString("receiptDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
