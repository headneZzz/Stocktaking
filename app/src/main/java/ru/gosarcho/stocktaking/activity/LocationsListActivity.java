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

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.gosarcho.stocktaking.LocationsRecyclerAdapter;
import ru.gosarcho.stocktaking.R;
import ru.gosarcho.stocktaking.model.Location;

public class LocationsListActivity extends AppCompatActivity implements LocationsRecyclerAdapter.OnLocationListener, SwipeRefreshLayout.OnRefreshListener {
    FirebaseFirestore db;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private LocationsRecyclerAdapter adapter;
    private SearchView searchView;
    private MenuItem searchItem;
    List<Location> locations;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);
        SharedPreferences sPref = getSharedPreferences(LoginActivity.LOGIN_PREF, MODE_PRIVATE);
        if (!sPref.contains(LoginActivity.SAVED_USERNAME)) {
            startActivity(new Intent(LocationsListActivity.this, LoginActivity.class));
            finish();
        } else {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            swipeRefreshLayout = findViewById(R.id.swipe_container);
            swipeRefreshLayout.setOnRefreshListener(this);

            recyclerView = findViewById(R.id.locations_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            getSupportActionBar().setTitle("Инвентаризация");
            locations = new ArrayList<>();
            db = FirebaseFirestore.getInstance();
            getDateFromFireBase();
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    public void getDateFromFireBase() {
        locations.clear();
        String collectionName = new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis()));
        db.collection("locations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        locations.addAll(task.getResult().toObjects(Location.class));
                    } else {
                        Log.w("myLogs", "Error getting documents.", task.getException());
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    adapter = new LocationsRecyclerAdapter(locations, this);
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
    public void onLocationClick(int position) {
        startActivity(new Intent(LocationsListActivity.this, ItemsListActivity.class).putExtra("location", locations.get(position).getId()));
    }

    @Override
    public void onRefresh() {
        getDateFromFireBase();
    }
}