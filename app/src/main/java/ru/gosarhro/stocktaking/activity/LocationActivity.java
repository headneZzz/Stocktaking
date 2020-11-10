package ru.gosarhro.stocktaking.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.fragment.NewItemDialogFragment;
import ru.gosarhro.stocktaking.model.item.Item;
import ru.gosarhro.stocktaking.model.item.ItemRecyclerAdapter;
import ru.gosarhro.stocktaking.model.location.LocationStatus;

import static ru.gosarhro.stocktaking.activity.MainActivity.IS_FIRST_LAUNCH;
import static ru.gosarhro.stocktaking.activity.MainActivity.PREF;

public class LocationActivity extends AppCompatActivity
        implements ItemRecyclerAdapter.OnItemListener, SwipeRefreshLayout.OnRefreshListener, NewItemDialogFragment.NewItemDialogListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentCollectionName = "";
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private SearchView searchView;
    private MenuItem searchItem;
    private int location;
    static List<Item> items = new ArrayList<>();
    private ItemRecyclerAdapter adapter = new ItemRecyclerAdapter(items, this);
    private final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    ShowcaseView temp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabinet);
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

        SharedPreferences settings = getSharedPreferences(PREF, 0);
        boolean isFirst = settings.getBoolean(IS_FIRST_LAUNCH, true);
        if (isFirst) {
            settings.edit().putBoolean(IS_FIRST_LAUNCH, false).apply();
            showTips();
        }
    }

    private void showTips() {
        temp = new ShowcaseView.Builder(this)
                .setContentTitle("Подсказки")
                .setContentText("\n1. Зажмите предмет, чтобы отметить его в списке\n\n" +
                        "2. Если предмета нет в списке, то его можно добавить, сканировав QR код или нажав левую кнопку внизу. Добавить можно только предмет, который уже есть в базе.")
                .setStyle(R.style.CustomShowcaseTheme).setOnClickListener(v -> {
                    temp.hide();
                    new ShowcaseView.Builder(this)
                            .setContentTitle("Подсказки")
                            .setContentText("\n3. Если в кабинете нет предмета, который есть в этом списке, то просто не отмечайте его\n\n" +
                                    "4. Нажмите \"Сохранить\", чтобы сохранить список в общей базе")
                            .setStyle(R.style.CustomShowcaseTheme)
                            .build();
                })
                .build();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        switch (menuItem.getItemId()) {
            case R.id.app_bar_add:
                DialogFragment dialogFragment = new NewItemDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "NewItem");
                break;
            case R.id.app_bar_camera:
                Bundle bundle = new Bundle();
                bundle.putString("currentCollectionName", currentCollectionName);
                bundle.putInt("location", location);
                startActivity(new Intent(getApplicationContext(), QRCameraActivity.class).putExtras(bundle));
                break;
            case R.id.app_bar_send:
                saveItemsInDb();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
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
    }

    @Override
    public void onItemLongClick(int position) {
        items.get(position).setFound(!items.get(position).isFound());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getItemsFromDb();
    }

    public void getItemsFromDb() {
        items.clear();
        db.collection("current")
                .document("stocktaking")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentCollectionName = (String) documentSnapshot.get("date");
                    Map<String, Boolean> foundedItemIdsMap = new HashMap<>();
                    db.collection("current")
                            .document("stocktaking")
                            .collection(currentCollectionName)
                            .whereEqualTo("location", location)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    items.addAll(task.getResult().toObjects(Item.class));
                                    Collections.sort(items, (o1, o2) -> o1.getId().compareTo(o2.getId()));
                                    adapter.getFilter().filter(null);
                                    swipeRefreshLayout.setRefreshing(false);
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                });
    }

    private void saveItemsInDb() {
        boolean isLocationFullChecked = true;
        for (Item item : items) {
            if (!item.isFound()) isLocationFullChecked = false;
            db.collection("current")
                    .document("stocktaking")
                    .collection(currentCollectionName)
                    .document(item.getId())
                    .update(
                            "found", item.isFound(),
                            "location", item.getLocation()
                    )
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT).show());
        }
        if (isLocationFullChecked) {
            db.collection("locations")
                    .document(String.valueOf(location))
                    .update("status", LocationStatus.OK);
        } else {
            db.collection("locations")
                    .document(String.valueOf(location))
                    .update("status", LocationStatus.NOT_ENOUGH);
        }
        Toast.makeText(getApplicationContext(), "Данные отправлены", Toast.LENGTH_LONG).show();
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
            itemInList.setFound(true);
            recyclerView.smoothScrollToPosition(items.indexOf(itemInList));
            Toast.makeText(getApplicationContext(), R.string.hint_item_already_in_list, Toast.LENGTH_SHORT).show();
        } else if (itemId.equals("")) {
            Toast.makeText(getApplicationContext(), R.string.error_empty_input, Toast.LENGTH_SHORT).show();
        } else {
            db.collection("current")
                    .document("stocktaking")
                    .collection(currentCollectionName)
                    .document(itemId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Item item = task.getResult().toObject(Item.class);
                            if (item != null) {
                                db.collection("current")
                                        .document("stocktaking")
                                        .collection(currentCollectionName)
                                        .document(itemId)
                                        .update("location", location);
                                item.setFound(true);
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