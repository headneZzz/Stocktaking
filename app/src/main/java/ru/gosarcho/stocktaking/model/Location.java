package ru.gosarcho.stocktaking.model;

import android.widget.ImageView;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import ru.gosarcho.stocktaking.R;

@IgnoreExtraProperties
public class Location {
    @DocumentId
    private String id;
    private LocationStatus status;

    public Location(String id, String status) {
        this.id = id;
        this.status = LocationStatus.valueOf(status);
    }

    public Location() {
    }

    public void setIconImage(ImageView icon) {
        switch (status) {
            case NOT_CHECKED:
                break;
            case OK:
                icon.setImageResource(R.drawable.ic_baseline_check);
                break;
            case LACK:
                icon.setImageResource(R.drawable.ic_baseline_close);
                break;
        }
    }

    public int getId() {
        return Integer.parseInt(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocationStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = LocationStatus.valueOf(status);
    }
}
