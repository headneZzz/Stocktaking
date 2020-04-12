package ru.gosarcho.finder;

public class Item {
    private String id;
    private Type type;
    private String name;
    private int location;
    private int previousLocation;
    private boolean isDecommissioned;
    private String executor;
    private String receiptDate;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

enum Type {
    PRINTER,
    MONITOR,
    COMPUTER,
    ITEM
}

