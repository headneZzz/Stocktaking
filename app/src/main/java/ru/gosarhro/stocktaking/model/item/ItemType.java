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

    static public ItemType findByType(String type) {
        ItemType[] itemTypes = ItemType.values();
        for (ItemType itemType : itemTypes) {
            if (itemType.type.equals(type)) {
                return itemType;
            }
        }
        return null;
    }
}
