package ru.gosarcho.finder.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.gosarcho.finder.AsyncResponse;
import ru.gosarcho.finder.ConnectTask;
import ru.gosarcho.finder.ItemsRecyclerAdapter;
import ru.gosarcho.finder.DateAdapter;
import ru.gosarcho.finder.R;
import ru.gosarcho.finder.model.Item;

import static ru.gosarcho.finder.activity.LoginActivity.LOGIN_PREF;
import static ru.gosarcho.finder.activity.LoginActivity.SAVED_LOCATION;
import static ru.gosarcho.finder.activity.LoginActivity.SAVED_USERNAME;

public class MainActivity extends AppCompatActivity implements AsyncResponse, ItemsRecyclerAdapter.OnItemListener {
    private ItemsRecyclerAdapter adapter;
    private SearchView searchView;
    private MenuItem searchItem;
    private int location;
    private String username;
    private List<String> ids;
    List<Item> items;
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
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            username = sPref.getString(SAVED_USERNAME, "");
            location = sPref.getInt(SAVED_LOCATION, 0);
            ConnectTask task = new ConnectTask(this);
            task.delegate = this;
            if (username.equals("admin")) {
                getSupportActionBar().setTitle("Все кабинеты");
                task.execute("https://find-inventory-api-test.herokuapp.com/get_all_items");
            } else {
                getSupportActionBar().setTitle("Кабинет №" + location);
                task.execute("https://find-inventory-api-test.herokuapp.com/get_all_items_by?location=" + location);
            }
            ids = new ArrayList<>();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (ids.contains(searchView.getQuery().toString())) {
                    searchView.clearFocus();
                    searchItem.collapseActionView();
                    //FIXME: getQuery return null
                    startActivity(new Intent(getApplicationContext(), ItemActivity.class).putExtra("Id", searchView.getQuery().toString()));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.voice) {
            speak();
            return true;
        }
        return false;
    }

    @Override
    public void processFinish(String output) {
        Type type = new TypeToken<List<Item>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Date.class, new DateAdapter().nullSafe())
                .create();
        items = gson.fromJson(output, type);
        for (Item item : items) {
            ids.add(item.getId());
        }
        adapter = new ItemsRecyclerAdapter(items, this);
        RecyclerView recyclerView = findViewById(R.id.items_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Назовите номер");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchItem.expandActionView();
            searchView.setQuery(result.get(0).replace(" ", ""), false);
        }
    }

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(getApplicationContext(), ItemActivity.class).putExtra("item", items.get(position)));
    }
}