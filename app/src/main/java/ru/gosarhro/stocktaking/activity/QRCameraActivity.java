package ru.gosarhro.stocktaking.activity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import ru.gosarhro.stocktaking.R;
import ru.gosarhro.stocktaking.model.item.Item;

import static android.Manifest.permission.CAMERA;
import static ru.gosarhro.stocktaking.activity.LocationActivity.getItemByIdInList;
import static ru.gosarhro.stocktaking.activity.LocationActivity.items;

public class QRCameraActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private int location;
    String currentCollectionName = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        location = getIntent().getIntExtra("location", 0);
        currentCollectionName = getIntent().getStringExtra("currentCollectionName");
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String itemId = result.getText();
        Item itemInList = getItemByIdInList(itemId);
        if (itemInList != null) {
            db.collection("current")
                    .document("stocktaking")
                    .collection(currentCollectionName)
                    .document(itemId)
                    .update("found", true);
            items.get(items.indexOf(itemInList)).setFound(true);
            Toast.makeText(getApplicationContext(), itemId + " отмечен в списке", Toast.LENGTH_LONG).show();
            scannerView.resumeCameraPreview(QRCameraActivity.this);
        } else {
            db.collection("items")
                    .document(itemId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Item item = task.getResult().toObject(Item.class);
                            if (item != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle("Новый предмет");
                                builder.setMessage(itemId + " нет в кабинете " + location + ", но он найден в базе. Добавить его в список кабинета?");
                                builder.setPositiveButton("Да", (dialog, which) -> {
                                    db.collection("current")
                                            .document("stocktaking")
                                            .collection(currentCollectionName)
                                            .document(itemId)
                                            .update(
                                                    "location", location,
                                                    "found", true
                                            );
                                    item.setFound(true);
                                    items.add(item);
                                    Collections.sort(items, (o1, o2) -> o1.getId().compareTo(o2.getId()));
                                    Toast.makeText(getApplicationContext(), itemId + " добавлен в кабинет " + location, Toast.LENGTH_LONG).show();
                                    scannerView.resumeCameraPreview(QRCameraActivity.this);
                                });
                                builder.setNegativeButton("Отмена", (dialog, which) -> {
                                    dialog.cancel();
                                    scannerView.resumeCameraPreview(QRCameraActivity.this);
                                });
                                AlertDialog alert1 = builder.create();
                                alert1.show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.error_no_item_in_db, Toast.LENGTH_SHORT).show();
                                scannerView.resumeCameraPreview(QRCameraActivity.this);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_connect_to_db, Toast.LENGTH_SHORT).show();
                            scannerView.resumeCameraPreview(QRCameraActivity.this);
                        }
                    });
        }
    }
}