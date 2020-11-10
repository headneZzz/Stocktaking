package ru.gosarhro.stocktaking.model.item;

public enum ItemType {
    PC("Системный блок"),
    MONITOR("Монитор"),
    PRINTER("Принтер"),
    UPS("ИБП"),
    SCANNER("Сканнер"),
    ANOTHER("Другое");

    private final String type;

    ItemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
