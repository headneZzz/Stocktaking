package ru.gosarhro.stocktaking.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ru.gosarhro.stocktaking.LocationsRecyclerAdapter;
import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.model.Location;

public class LocationsListActivity extends AppCompatActivity implements LocationsRecyclerAdapter.OnLocationListener, SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private MenuItem searchItem;
    List<Location> locations = new ArrayList<>();
    private LocationsRecyclerAdapter adapter = new LocationsRecyclerAdapter(locations, this);
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);
        SharedPreferences sPref = getSharedPreferences(LoginActivity.LOGIN_PREF, MODE_PRIVATE);
        if (!sPref.contains(LoginActivity.USERNAME)) {
            startActivity(new Intent(LocationsListActivity.this, LoginActivity.class));
            finish();
        } else {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);
            swipeRefreshLayout = findViewById(R.id.swipe_container);
            swipeRefreshLayout.setOnRefreshListener(this);

            recyclerView = findViewById(R.id.locations_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            getDateFromFireBase();
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    public void getDateFromFireBase() {
        locations.clear();
        db.collection("locations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        locations.addAll(task.getResult().toObjects(Location.class));
                        Collections.sort(locations, (o1, o2) -> o1.getId() - o2.getId());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    swipeRefreshLayout.setRefreshing(false);
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.voice_hint);
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