package me.robertozimek.android.storeshoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * StoreUtil
 * Utility class for retrieving and storing Store objects from content provider
 *
 * Created by robertozimek on 1/16/16.
 */


public class StoreUtil {
    // Retrieves stores from database and returns list of stores
    public static List<Store> getStoresList(Context context) {
        List<Store> stores = new ArrayList<Store>();

        // Selects columns to get
        String[] columns = {ShoppingListContract.StoreEntry.ID_STORE,
                ShoppingListContract.StoreEntry.NAME,
                ShoppingListContract.StoreEntry.STREET,
                ShoppingListContract.StoreEntry.CITY,
                ShoppingListContract.StoreEntry.STATE,
                ShoppingListContract.StoreEntry.LATITUDE,
                ShoppingListContract.StoreEntry.LONGITUDE};

        // Get CONTENT URI
        Uri CONTENT_URI = ShoppingListContract.StoreEntry.CONTENT_URI;

        // Retrieve reference to records
        Cursor storesCursor = context.getContentResolver().query(CONTENT_URI,columns, null, null, null);

        // Create store objects and add to list
        if (storesCursor != null) {
            while (storesCursor.moveToNext()) {
                // Initialize store with constructor
                Store mStore = new Store(storesCursor.getString(1), storesCursor.getString(2),
                        storesCursor.getString(3), storesCursor.getString(4));

                // Add ID
                mStore.setStoreID(storesCursor.getInt(0));

                // Add coordinate
                mStore.setCoordinates(storesCursor.getDouble(5), storesCursor.getDouble(6));

                // Retrieve number of items in shopping list
                mStore.setShoppingItemsCount(ShoppingItemUtil.getItemCount(mStore.getStoreID(), context));

                // Add to list
                stores.add(mStore);
            }
            storesCursor.close();
        }


        return stores;
    }

    // Adds store to database
    public static boolean addStore(Store store, Context context) {
        boolean foundLocationSuccessly = store.retrieveCoordinates();

        if(!foundLocationSuccessly) {
            return false;
        }

        // Set content values for each column
        ContentValues values = new ContentValues();
        values.put(ShoppingListContract.StoreEntry.NAME, store.getStoreName());
        values.put(ShoppingListContract.StoreEntry.STREET, store.getStreetAddress().toUpperCase().replaceAll("[.]", ""));
        values.put(ShoppingListContract.StoreEntry.CITY, store.getCityAddress().toUpperCase());
        values.put(ShoppingListContract.StoreEntry.STATE, store.getStateAddress().toUpperCase());
        values.put(ShoppingListContract.StoreEntry.LATITUDE, store.getLatitude());
        values.put(ShoppingListContract.StoreEntry.LONGITUDE, store.getLongitude());


        // Gets CONTENT URI from contract class
        Uri CONTENT_URI = ShoppingListContract.StoreEntry.CONTENT_URI;

        // Inserts record
        context.getContentResolver().insert(CONTENT_URI, values);

        return foundLocationSuccessly;
    }

    public static boolean removeStore(Store store, Context context) {
        // Builds CONTENT URI with storeID
        Uri CONTENT_URI = ShoppingListContract.StoreEntry.getStoreUriWithID(store.getStoreID());

        int numOfDeleted = context.getContentResolver().delete(CONTENT_URI, null, null);

        // Returns true only when something was deleted
        if(numOfDeleted > 0) {
            return true;
        } else {
            return false;
        }
    }

}
