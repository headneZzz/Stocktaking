package ru.gosarcho.stocktaking.activity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import ru.gosarcho.stocktaking.ItemsRecyclerAdapter;
import ru.gosarcho.stocktaking.R;
import ru.gosarcho.stocktaking.fragment.NewItemDialogFragment;
import ru.gosarcho.stocktaking.model.Item;

public class ItemsListActivity extends AppCompatActivity
        implements ItemsRecyclerAdapter.OnItemListener, SwipeRefreshLayout.OnRefreshListener, NewItemDialogFragment.NewItemDialogListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private SearchView searchView;
    private MenuItem searchItem;
    private int location;
    List<Item> items = new ArrayList<>();
    private ItemsRecyclerAdapter adapter = new ItemsRecyclerAdapter(items, this);
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

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
        db.collection("items")
                .whereEqualTo("location", location)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        items.addAll(task.getResult().toObjects(Item.class));
                        adapter.getFilter().filter(null);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        switch (item.getItemId()) {
            case R.id.nav_add:
                DialogFragment dialogFragment = new NewItemDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "NewItem");
                break;
            case R.id.nav_camera:
                startActivity(new Intent(getApplicationContext(), QRCameraActivity.class));
                break;
            case R.id.nav_send:

                break;
        }
        return true;
    };

    private boolean checkIdInList(String itemId) {
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText itemIdText = dialog.getDialog().findViewById(R.id.item_id_text);
        String itemId = itemIdText.getText().toString();

        if (checkIdInList(itemId)) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_item_already_in_list, Toast.LENGTH_SHORT);
            toast.show();
        } else if(itemId.equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_empty_input, Toast.LENGTH_SHORT);
            toast.show();
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
                                adapter.getFilter().filter("");
                                dialog.dismiss();
                                recyclerView.smoothScrollToPosition(items.size()-1);
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_item_in_db, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
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
}