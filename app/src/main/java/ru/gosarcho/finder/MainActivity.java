package ru.gosarcho.finder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements AsyncResponse {
    public List<String> ids;
    public ImageButton speakButton;
    public AutoCompleteTextView textView;
    public Button searchButton;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectTask task = new ConnectTask(this);
        task.delegate = this;
        task.execute("https://find-inventory-api-test.herokuapp.com/get_all_items_ids");

        ids = new ArrayList<>();
        textView = findViewById(R.id.auto_text_view);
        speakButton = findViewById(R.id.btn_speak);
        searchButton = findViewById(R.id.btn_search);

        speakButton.setOnClickListener(v -> speak());
        searchButton.setOnClickListener(v -> search());
        textView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchButton.performClick();
                return true;
            }
            return false;
        });

    }

    @Override
    public void processFinish(String output) {
        ids.addAll(Arrays.asList(output.split(",")));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ids);
        textView.setAdapter(adapter);
    }

    public void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Назовите номер дела");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            textView.setText(result.get(0));
        }
    }

    public void search() {
        String value = textView.getText().toString();
        if (ids.contains(value)) {
            startActivity(new Intent(getApplicationContext(), ItemActivity.class).putExtra("Id", value));
        }
    }
}