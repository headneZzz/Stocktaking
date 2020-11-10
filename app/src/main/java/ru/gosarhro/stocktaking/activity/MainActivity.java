package ru.gosarhro.stocktaking.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.fragment.StocktakingFragment;
import ru.gosarhro.stocktaking.fragment.ItemsFragment;

public class MainActivity extends AppCompatActivity {
    static final String PREF = "pref";
    static final String USERNAME = "username";
    static final String PASSWORD = "password";
    static final String IS_FIRST_LAUNCH = "is_first";

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        switch (menuItem.getItemId()) {
            case R.id.nav_locations:
                if (getSupportFragmentManager().findFragmentByTag("one") != null) {
                    getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("one")).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new StocktakingFragment(), "one").commit();
                }
                if (getSupportFragmentManager().findFragmentByTag("two") != null) {
                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("two")).commit();
                }
                break;
            case R.id.nav_search:
                if (getSupportFragmentManager().findFragmentByTag("two") != null) {
                    getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("two")).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ItemsFragment(), "two").commit();
                }
                if (getSupportFragmentManager().findFragmentByTag("one") != null) {
                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("one")).commit();
                }
                break;
        }
        return true;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sPref = getSharedPreferences(PREF, MODE_PRIVATE);
        if (!sPref.contains(USERNAME)) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_bar);
            bottomNav.setOnNavigationItemSelectedListener(navListener);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new StocktakingFragment(), "one")
                        .commit();
            }
        }
    }
}