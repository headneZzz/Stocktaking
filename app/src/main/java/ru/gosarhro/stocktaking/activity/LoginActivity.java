package ru.gosarhro.stocktaking.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import ru.gosarhro.stocktaking.R;

import static ru.gosarhro.stocktaking.activity.MainActivity.PASSWORD;
import static ru.gosarhro.stocktaking.activity.MainActivity.PREF;
import static ru.gosarhro.stocktaking.activity.MainActivity.USERNAME;

public class LoginActivity extends AppCompatActivity {
    static SharedPreferences sPref;
    private EditText usernameEditText;
    private EditText passwordEditText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.activity_item_tv_location);
        Button loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener(v -> login(usernameEditText.getText().toString(), passwordEditText.getText().toString()));
    }

    private void login(String username, String password) {
        if (!username.equals("") && !password.equals("")) {
            db.collection("users")
                    .document(username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && password.equals(task.getResult().get(PASSWORD))) {
                                sPref = getSharedPreferences(PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sPref.edit();
                                editor.putString(USERNAME, username);
                                editor.putString(PASSWORD, password);
                                editor.apply();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_empty_input, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
