package ru.gosarhro.stocktaking.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.fragment.LocationsListFragment;
import ru.gosarhro.stocktaking.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sPref = getSharedPreferences(LoginActivity.LOGIN_PREF, MODE_PRIVATE);
        if (!sPref.contains(LoginActivity.USERNAME)) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_bar);
            bottomNav.setOnNavigationItemSelectedListener(navListener);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LocationsListFragment()).commit();
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        Fragment selectedFragment = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_locations:
                selectedFragment = new LocationsListFragment();
                break;
            case R.id.nav_search:
                selectedFragment = new SearchFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    };
}