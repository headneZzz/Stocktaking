package ru.gosarcho.stocktaking.model;

import android.widget.ImageView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.gosarcho.stocktaking.R;

public class Item implements Serializable {
    private String id;
    private ItemType type;
    private int location;
    private String name;
    private Date purchaseDate;
    private boolean isWorking;
    private List<Action> history;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type.getType();
    }

    public int getLocation() {
        return location;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public List<Action> getHistory() {
        return history;
    }

    public String getPurchaseDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(purchaseDate);
    }

    public void setIconImage(ImageView icon) {
        switch (type) {
            case PC:
                icon.setImageResource(R.drawable.ic_pc_color);
                break;
            case MONITOR:
                icon.setImageResource(R.drawable.ic_monitor_color);
                break;
            case UPS:
                icon.setImageResource(R.drawable.ic_ups_color);
                break;
            case SCANNER:
                icon.setImageResource(R.drawable.ic_scanner_color);
                break;
            case PRINTER:
                icon.setImageResource(R.drawable.ic_printer_color);
                break;
            case ANOTHER:
                icon.setImageResource(R.drawable.ic_another_color);
                break;
        }
    }
}

