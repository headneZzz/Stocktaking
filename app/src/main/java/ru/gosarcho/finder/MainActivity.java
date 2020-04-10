package ru.gosarcho.finder;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static ru.gosarcho.finder.LoginActivity.LOGIN_PREF;
import static ru.gosarcho.finder.LoginActivity.SAVED_LOCATION;
import static ru.gosarcho.finder.LoginActivity.SAVED_USERNAME;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private ImageButton speakButton;
    private AutoCompleteTextView textView;
    private Button searchButton;
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private int location;
    private String username;
    private List<String> ids;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sPref = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
        if (!sPref.contains(SAVED_USERNAME)) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            username = sPref.getString(SAVED_USERNAME, "");
            location = sPref.getInt(SAVED_LOCATION, 0);
            ConnectTask task = new ConnectTask(this);
            task.delegate = this;
            task.execute("https://find-inventory-api-test.herokuapp.com/get_all_items_by_location/" + location);
            ids = new ArrayList<>();
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Кабинет №" + location);
            recyclerView = findViewById(R.id.list_items);
            speakButton = findViewById(R.id.btn_speak);
            speakButton.setOnClickListener(v -> speak());
            searchButton = findViewById(R.id.btn_search);
            searchButton.setOnClickListener(v -> search());
            textView = findViewById(R.id.auto_text_view);
            textView.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchButton.performClick();
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void processFinish(String output) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(output);
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.optString("id");
                ids.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ids);
        textView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter(jsonArray));
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