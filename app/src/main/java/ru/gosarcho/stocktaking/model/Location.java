package ru.gosarcho.stocktaking.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Location {
    @DocumentId
    private String id;
    private boolean isChecked;

    public Location(String id, boolean isChecked) {
        this.id = id;
        this.isChecked = isChecked;
    }

    public Location() {
    }

    public int getId() {
        return Integer.parseInt(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
