package ru.gosarcho.stocktaking.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ru.gosarcho.stocktaking.R;

public class LoginActivity extends AppCompatActivity {
    static SharedPreferences sPref;
    private EditText usernameEditText;
    private EditText locationEditText;
    public static final String LOGIN_PREF = "pref";
    static final String SAVED_USERNAME = "username";
    static final String SAVED_LOCATION = "location";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.username);
        locationEditText = findViewById(R.id.location);
        Button loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener(v -> login(usernameEditText.getText().toString(), Integer.parseInt(locationEditText.getText().toString())));
    }

    private void login(String username, int location) {
        sPref = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(SAVED_USERNAME, usernameEditText.getText().toString());
        editor.putInt(SAVED_LOCATION, Integer.parseInt(locationEditText.getText().toString()));
        editor.apply();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
