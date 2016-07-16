package me.robertozimek.android.storeshoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * ShoppingItemUtil
 * Utility class for retrieving and storing ShoppingItem objects from content provider
 *
 * Created by robertozimek on 1/17/16.
 */
public class ShoppingItemUtil {
    // Retrieves items from database and returns list of items
    public static List<ShoppingItem> getShoppingListForStoreID(int storeID, Context context) {
        List<ShoppingItem> items = new ArrayList<ShoppingItem>();

        // Selects columns to get
        String[] columns = {ShoppingListContract.ItemEntry.ID_ITEM,
                ShoppingListContract.ItemEntry.ITEM_NAME,
                ShoppingListContract.ItemEntry.IMAGE_PATH};

        // Where condition for query
        String where = ShoppingListContract.ItemEntry.ITEM_STORE_ID + "=?";

        // Arguments for where condition
        String arg[] = {String.valueOf(storeID)};

        // Get CONTENT URI with STORE ID
        Uri CONTENT_URI = ShoppingListContract.ItemEntry.CONTENT_URI;

        // Retrieve reference to records
        Cursor itemsCursor = context.getContentResolver().query(CONTENT_URI,columns, where, arg, null);

        // Create store objects and add to list
        if (itemsCursor != null) {
            while (itemsCursor.moveToNext()) {
                // Initialize item and fill data
                ShoppingItem mItem = new ShoppingItem(itemsCursor.getString(1), itemsCursor.getString(2));
                mItem.setStoreID(storeID);
                mItem.setItemID(itemsCursor.getInt(0));

                // Add to list
                items.add(mItem);
            }
            itemsCursor.close();
        }

        return items;
    }

    public static boolean removeItem(ShoppingItem item, Context context) {
        // Builds CONTENT URI with storeID
        Uri CONTENT_URI = ShoppingListContract.ItemEntry.getItemListUriWithID(item.getItemID());

        int numOfDeleted = context.getContentResolver().delete(CONTENT_URI, null, null);

        // Returns true only when something was deleted
        if(numOfDeleted > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static ShoppingItem getShoppingItemWithID(int itemID, Context context) {
        ShoppingItem shoppingItem = new ShoppingItem();

        // Retrieves CONTENT URI for shopping list items
        Uri CONTENT_URI = ShoppingListContract.ItemEntry.getItemListUriWithID(itemID);

        // Selects columns in query
        String[] columns = {ShoppingListContract.ItemEntry.ITEM_NAME,
                ShoppingListContract.ItemEntry.IMAGE_PATH,
                ShoppingListContract.ItemEntry.ITEM_STORE_ID};

        // Retrieve reference to records
        Cursor itemCursor = context.getContentResolver().query(CONTENT_URI,columns, null, null, null);

        // Checks if cursor exists and retrieve record
        if(itemCursor != null && itemCursor.moveToFirst()) {
            shoppingItem = new ShoppingItem(itemCursor.getString(0));
            shoppingItem.setImagePath(itemCursor.getString(1));
            shoppingItem.setStoreID(itemCursor.getInt(2));
            shoppingItem.setItemID(itemID);

            itemCursor.close();
        }

        return shoppingItem;
    }


    public static boolean checkForItemNameDuplicates(ShoppingItem item, Context context) {
        boolean exists = false;
        // Retrieves CONTENT URI for shopping list items
        Uri CONTENT_URI = ShoppingListContract.ItemEntry.CONTENT_URI;

        // Selects columns in query
        String[] columns = {ShoppingListContract.ItemEntry.ID_ITEM};

        // Where condition of query
        String where = ShoppingListContract.ItemEntry.ITEM_NAME + "=? AND " +
                ShoppingListContract.ItemEntry.ITEM_STORE_ID + "=?";

        // Arguments for where condition
        String[] args = {item.getItemName(), String.valueOf(item.getStoreID())};

        // Retrieve reference to records
        Cursor itemCursor = context.getContentResolver().query(CONTENT_URI,columns, where, args, null);

        // Checks if cursor exists and retrieves record
        if(itemCursor != null && itemCursor.moveToFirst()) {
            exists = true;
            itemCursor.close();
        }

        return exists;
    }

    public static boolean addItemToStoreID(ShoppingItem item, Context context) {
        boolean successfulAdd = true;

        // Make sure there isn't one that already exists
        if(checkForItemNameDuplicates(item, context)) {
            successfulAdd = false;
            return successfulAdd;
        }

        // Sets content values for each column
        ContentValues values = new ContentValues();
        values.put(ShoppingListContract.ItemEntry.ITEM_NAME, item.getItemName());
        values.put(ShoppingListContract.ItemEntry.IMAGE_PATH, item.getImagePath());
        values.put(ShoppingListContract.ItemEntry.ITEM_STORE_ID, item.getStoreID());

        // Gets CONTENT URI from contract class
        Uri CONTENT_URI = ShoppingListContract.ItemEntry.CONTENT_URI;

        // Inserts record
        context.getContentResolver().insert(CONTENT_URI, values);

        return successfulAdd;
    }

    public static void updateItemInStore(ShoppingItem item, Context context) {
        // Sets content values for each column
        ContentValues values = new ContentValues();
        values.put(ShoppingListContract.ItemEntry.ITEM_NAME, item.getItemName());
        values.put(ShoppingListContract.ItemEntry.IMAGE_PATH, item.getImagePath());
        values.put(ShoppingListContract.ItemEntry.ITEM_STORE_ID, item.getStoreID());

        // Gets CONTENT URI from contract class
        Uri CONTENT_URI = ShoppingListContract.ItemEntry.CONTENT_URI;

        // Where condition of query
        String where = ShoppingListContract.ItemEntry.ID_ITEM + "=? AND " +
                ShoppingListContract.ItemEntry.ITEM_STORE_ID + "=?";

        // Arguments for where condition
        String[] args = {String.valueOf(item.getItemID()), String.valueOf(item.getStoreID())};

        // Updates record
        context.getContentResolver().update(CONTENT_URI, values, where, args);
    }

}
