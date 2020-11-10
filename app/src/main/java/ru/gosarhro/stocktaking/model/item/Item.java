package ru.gosarhro.stocktaking.model.item;

import android.widget.ImageView;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.gosarhro.stocktaking.R;

@IgnoreExtraProperties
public class Item implements Serializable {
    @DocumentId
    private String id;
    private ItemType type;
    private int location;
    private String name;
    private Date purchaseDate;
    private boolean isWorking;
    private List<ItemAction> history;
    private boolean isFound;

    public Item() {
    }

    public Item(String id, String type, int location, String name, Date purchaseDate, boolean isWorking, List<ItemAction> history) {
        this.id = id;
        this.type = ItemType.valueOf(type);
        this.location = location;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.isWorking = isWorking;
        this.history = history;
        isFound = false;
    }

    public Item(String id, String type, int location, String name, Date purchaseDate, boolean isWorking, List<ItemAction> history, boolean isFound) {
        this.id = id;
        this.type = ItemType.valueOf(type);
        this.location = location;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.isWorking = isWorking;
        this.history = history;
        this.isFound = isFound;
    }

    public void setIconImage(ImageView icon) {
        switch (type) {
            case PC:
                icon.setImageResource(R.drawable.ic_item_pc_tower_color);
                break;
            case MONITOR:
                icon.setImageResource(R.drawable.ic_item_monitor_color);
                break;
            case UPS:
                icon.setImageResource(R.drawable.ic_item_ups_color);
                break;
            case SCANNER:
                icon.setImageResource(R.drawable.ic_item_scanner_color);
                break;
            case PRINTER:
                icon.setImageResource(R.drawable.ic_item_printer_color);
                break;
            case ANOTHER:
                icon.setImageResource(R.drawable.ic_item_another_color);
                break;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type.getType();
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurchaseDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(purchaseDate);
    }

    public void setPurchaseDate(String purchaseDate) throws ParseException {
        this.purchaseDate = new SimpleDateFormat("dd.MM.yyyy").parse(purchaseDate);
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public List<ItemAction> getHistory() {
        return history;
    }

    public void setHistory(List<ItemAction> history) {
        this.history = history;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
    }
}

