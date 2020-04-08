package ru.gosarcho.finder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Objects;

public class ItemActivity extends Activity implements AsyncResponse {
    public TextView number;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        number = findViewById(R.id.number);
        id = Objects.requireNonNull(getIntent().getExtras()).getString("Id");
        ConnectTask task = new ConnectTask(this);
        task.delegate = this;
        task.execute("https://find-inventory-api-test.herokuapp.com/" + id);
    }

    @Override
    public void processFinish(String output) {
        number.setText(output);
    }
}
