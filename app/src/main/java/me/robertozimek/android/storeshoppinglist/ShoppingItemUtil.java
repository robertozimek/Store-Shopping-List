package me.robertozimek.android.storeshoppinglist;

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


}
