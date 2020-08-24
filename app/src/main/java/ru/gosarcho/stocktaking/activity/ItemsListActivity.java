package ru.gosarcho.stocktaking.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import ru.gosarcho.stocktaking.ItemsRecyclerAdapter;
import ru.gosarcho.stocktaking.R;
import ru.gosarcho.stocktaking.model.Item;

public class ItemsListActivity extends AppCompatActivity implements ItemsRecyclerAdapter.OnItemListener, SwipeRefreshLayout.OnRefreshListener {
    FirebaseFirestore db;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private ItemsRecyclerAdapter adapter;
    private SearchView searchView;
    private MenuItem searchItem;
    private int location;
    List<Item> items;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        if (item.getItemId() == R.id.nav_camera) {
            startActivity(new Intent(getApplicationContext(), QRCameraActivity.class));
        }
        return true;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView bottomNav = findViewById(R.id.bot_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        recyclerView = findViewById(R.id.items_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        location = getIntent().getIntExtra("location", 0);
        getSupportActionBar().setTitle("Кабинет №" + location);
        items = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        getDateFromFireBase();
        swipeRefreshLayout.setRefreshing(true);

    }

    public void getDateFromFireBase() {
        items.clear();
        db.collection("items")
                .whereEqualTo("location", location)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Item item = document.toObject(Item.class);
                            items.add(item);
                        }
                    } else {
                        Log.w("myLogs", "Error getting documents.", task.getException());
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    adapter = new ItemsRecyclerAdapter(items, this);
                    recyclerView.setAdapter(adapter);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
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

    @Override
    public void onRefresh() {
        getDateFromFireBase();
    }
}