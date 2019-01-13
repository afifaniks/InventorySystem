package main.java.others;

public class ItemType {
    Integer itemTypeID;
    String itemTypeName;
    Integer totalItems; //Total items of this type

    public Integer getItemTypeID() {
        return itemTypeID;
    }

    public void setItemTypeID(Integer itemTypeID) {
        this.itemTypeID = itemTypeID;
    }

    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public ItemType(Integer itemTypeID, String itemTypeName, Integer totalItems) {
        this.itemTypeID = itemTypeID;
        this.itemTypeName = itemTypeName;
        this.totalItems = totalItems;
    }
}
