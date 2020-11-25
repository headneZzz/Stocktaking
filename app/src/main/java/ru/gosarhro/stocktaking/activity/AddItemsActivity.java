package ru.gosarhro.stocktaking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.model.item.Item;
import ru.gosarhro.stocktaking.model.item.ItemRecyclerAdapter;

import static ru.gosarhro.stocktaking.activity.LocationActivity.items;

public class AddItemsActivity extends AppCompatActivity implements ItemRecyclerAdapter.OnItemListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int currentLocation;
    String currentCollectionName = "";
    private int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private SearchView searchView;
    private MenuItem searchItem;
    RecyclerView recyclerView;
    List<Item> itemsFromDb = new ArrayList<>();
    ItemRecyclerAdapter adapter = new ItemRecyclerAdapter(itemsFromDb, this);
    Button saveButton;
    int counter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);
        currentLocation = getIntent().getIntExtra("location", 0);
        currentCollectionName = getIntent().getStringExtra("currentCollectionName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Добавить");

        recyclerView = findViewById(R.id.items_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        saveButton = findViewById(R.id.activity_item_btn_save);
        saveButton.setOnClickListener(v -> addItems());
        getItemsFromDb();
    }

    public void getItemsFromDb() {
        db.collection("current")
                .document("stocktaking")
                .collection(currentCollectionName)
                .whereNotEqualTo("location", currentLocation)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Item> temp = task.getResult().toObjects(Item.class);
                        for (Item item : temp)
                            if (!item.isFound())
                                itemsFromDb.add(item);
                        Collections.sort(itemsFromDb, (o1, o2) -> o1.getId().compareTo(o2.getId()));
                        adapter.getFilter().filter(null);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {
        counter = itemsFromDb.get(position).isFound() ? --counter : ++counter;
        itemsFromDb.get(position).setFound(!itemsFromDb.get(position).isFound());
        adapter.notifyDataSetChanged();
        saveButton.setText("Добавить (" + counter + ")");
    }

    public void addItems() {
        for (Item item : itemsFromDb) {
            if (item.isFound()) {
                item.setLocation(currentLocation);
                items.add(item);
                Collections.sort(items, (o1, o2) -> o1.getId().compareTo(o2.getId()));
            }
        }
        Toast.makeText(getApplicationContext(), counter + " предметов добавлены в кабинет " + currentLocation, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                recyclerView.scrollToPosition(0);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.voice) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.voice_hint);
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchItem.expandActionView();
            searchView.setQuery(result.get(0).replace(" ", ""), false);
        }
    }
}
