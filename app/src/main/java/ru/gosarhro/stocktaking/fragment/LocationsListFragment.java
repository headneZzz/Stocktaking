package ru.gosarhro.stocktaking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.activity.ItemsListActivity;
import ru.gosarhro.stocktaking.location.Location;
import ru.gosarhro.stocktaking.location.LocationRecyclerAdapter;

public class LocationsListFragment extends Fragment implements LocationRecyclerAdapter.OnLocationListener, SwipeRefreshLayout.OnRefreshListener{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<Location> locations = new ArrayList<>();
    private LocationRecyclerAdapter adapter = new LocationRecyclerAdapter(locations, this);


    public LocationsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations_list, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        swipeRefreshLayout = view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = view.findViewById(R.id.locations_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getLocationsFromDb();
        swipeRefreshLayout.setRefreshing(true);
        return view;
    }

    public void getLocationsFromDb() {
        locations.clear();
        db.collection("locations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        locations.addAll(task.getResult().toObjects(Location.class));
                        Collections.sort(locations, (o1, o2) -> o1.getId() - o2.getId());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast toast = Toast.makeText(getContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    @Override
    public void onLocationClick(int position) {
        startActivity(new Intent(getContext(), ItemsListActivity.class).putExtra("location", locations.get(position).getId()));
    }

    @Override
    public void onRefresh() {
        getLocationsFromDb();
    }
}