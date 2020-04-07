package ru.gosarcho.finder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ItemActivity extends Activity {
    public TextView number;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        number = findViewById(R.id.number);
    }

}
