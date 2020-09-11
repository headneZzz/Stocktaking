package ru.gosarhro.stocktaking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.activity.ItemActivity;
import ru.gosarhro.stocktaking.item.Item;
import ru.gosarhro.stocktaking.item.ItemRecyclerAdapter;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment implements ItemRecyclerAdapter.OnItemListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Item> items = new ArrayList<>();
    private List<String> itemIds = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemRecyclerAdapter adapter = new ItemRecyclerAdapter(items, this);
    private MenuItem searchItem;
    private SearchView searchView;
    private int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.search);
        recyclerView = view.findViewById(R.id.items_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getItemIdsFromDb();
        return view;
    }

    public void getItemIdsFromDb() {
        itemIds.clear();
        db.collection("search")
                .document("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemIds.addAll((List<String>) task.getResult().get("ids"));
                    } else {
                        Toast toast = Toast.makeText(getContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    public void getItemsByIds(List<String> newItemIds) {
        items.clear();
        for (String itemId : newItemIds) {
            db.collection("items")
                    .document(itemId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            items.add(task.getResult().toObject(Item.class));
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(getContext(), ItemActivity.class).putExtra("item", items.get(position)));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<String> newItemIds = new ArrayList<>();
                for (String itemId : itemIds) {
                    if (itemId.contains(query)) {
                        newItemIds.add(itemId);
                    }
                }
                getItemsByIds(newItemIds);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
        searchItem.expandActionView();
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