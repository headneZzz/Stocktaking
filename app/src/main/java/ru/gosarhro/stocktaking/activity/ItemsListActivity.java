package ru.gosarhro.stocktaking.activity;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.gosarhro.stocktaking.item.ItemRecyclerAdapter;
import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.fragment.NewItemDialogFragment;
import ru.gosarhro.stocktaking.item.Item;
import ru.gosarhro.stocktaking.location.LocationStatus;

public class ItemsListActivity extends AppCompatActivity
        implements ItemRecyclerAdapter.OnItemListener, SwipeRefreshLayout.OnRefreshListener, NewItemDialogFragment.NewItemDialogListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentCollectionName = new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis()));
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private SearchView searchView;
    private MenuItem searchItem;
    private int location;
    static List<Item> items = new ArrayList<>();
    private ItemRecyclerAdapter adapter = new ItemRecyclerAdapter(items, this);
    private final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);
        location = getIntent().getIntExtra("location", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.location) + location);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_app_bar);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = findViewById(R.id.items_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        getItemsFromDb();
        swipeRefreshLayout.setRefreshing(true);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        switch (menuItem.getItemId()) {
            case R.id.app_bar_add:
                DialogFragment dialogFragment = new NewItemDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "NewItem");
                break;
            case R.id.app_bar_camera:
                startActivity(new Intent(getApplicationContext(), QRCameraActivity.class).putExtra("location", location));
                break;
            case R.id.app_bar_send:
                saveItemsInDb();
                break;
        }
        return true;
    };

    @Override
    public void onResume() {
        super.onResume();
        adapter.getFilter().filter(null);
    }

    @Override
    public void onItemClick(int position) {
        items.get(position).setChecked(!items.get(position).isChecked());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getItemsFromDb();
    }

    public void getItemsFromDb() {
        items.clear();
        Map<String, Boolean> foundedItemIdsMap = new HashMap<>();
        db.collection(currentCollectionName)
                .document(String.valueOf(location))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().get("items") != null) {
                        foundedItemIdsMap.putAll((Map<String, Boolean>) task.getResult().get("items"));
                        for (Map.Entry itemEntry : foundedItemIdsMap.entrySet()) {
                            db.collection("items")
                                    .document((String) itemEntry.getKey())
                                    .get()
                                    .addOnSuccessListener(result -> {
                                        Item itemFromDb = result.toObject(Item.class);
                                        itemFromDb.setChecked((Boolean) itemEntry.getValue());
                                        items.add(itemFromDb);
                                        Collections.sort(items, (o1, o2) -> o1.getId().compareTo(o2.getId()));
                                    });
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void saveItemsInDb() {
        boolean isLocationFullChecked = true;
        Map<String, Boolean> foundedItemIdsMap = new HashMap<>();
        for (Item item : items) {
            if (isLocationFullChecked && !item.isChecked()) {
                isLocationFullChecked = false;
            }
            foundedItemIdsMap.put(item.getId(), item.isChecked());
        }
        boolean finalIsLocationFullChecked = isLocationFullChecked;
        db.collection(currentCollectionName)
                .document(String.valueOf(location))
                .update("items", foundedItemIdsMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (finalIsLocationFullChecked) {
                            db.collection("locations")
                                    .document(String.valueOf(location))
                                    .update("status", LocationStatus.OK);
                        } else {
                            db.collection("locations")
                                    .document(String.valueOf(location))
                                    .update("status", LocationStatus.NOT_ENOUGH);
                        }
                        Toast.makeText(getApplicationContext(), "Данные отправлены", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    static Item getItemByIdInList(String itemId) {
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText itemIdText = dialog.getDialog().findViewById(R.id.item_id_text);
        String itemId = itemIdText.getText().toString();
        Item itemInList = getItemByIdInList(itemId);
        if (itemInList != null) {
            itemInList.setChecked(true);
            recyclerView.smoothScrollToPosition(items.indexOf(itemInList));
            Toast.makeText(getApplicationContext(), R.string.hint_item_already_in_list, Toast.LENGTH_SHORT).show();
        } else if (itemId.equals("")) {
            Toast.makeText(getApplicationContext(), R.string.error_empty_input, Toast.LENGTH_SHORT).show();
        } else {
            db.collection("items")
                    .document(itemId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Item item = task.getResult().toObject(Item.class);
                            if (item != null) {
                                item.setChecked(true);
                                items.add(item);
                                Collections.sort(items, (o1, o2) -> o1.getId().compareTo(o2.getId()));
                                adapter.getFilter().filter(null);
                                dialog.dismiss();
                                recyclerView.smoothScrollToPosition(items.indexOf(item));
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.error_no_item_in_db, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchItem.expandActionView();
            searchView.setQuery(result.get(0).replace(" ", ""), false);
        }
    }
}