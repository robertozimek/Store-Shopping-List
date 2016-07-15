package me.robertozimek.android.storeshoppinglist;

/**
 * ShoppingItem Class
 *  contains item id, store id, item name, image path
 *
 * Created by robertozimek on 1/15/16.
 */
public class ShoppingItem {
    private int itemID;
    private int storeID;
    private String itemName; // shopping list item name
    private String imagePath; // shopping list full size image

    // Empty Constructor
    public ShoppingItem() {}

    // Constructor that takes item name and path to the image
    public ShoppingItem(String itemName) {
        this.itemName = itemName;
    }

    public ShoppingItem(String itemName, String imagePath) {
        this.itemName = itemName;
        this.imagePath = imagePath;
    }

    // Setters and Getters

    // Item ID
    public int getItemID() { return itemID; }
    public void setItemID(int itemID) { this.itemID = itemID; }

    // Item's Store ID
    public int getStoreID() { return storeID; }
    public void setStoreID(int storeID) { this.storeID = storeID; }

    // Item Name
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    // Image Path
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imageBitmap) { this.imagePath = imageBitmap; }



}
