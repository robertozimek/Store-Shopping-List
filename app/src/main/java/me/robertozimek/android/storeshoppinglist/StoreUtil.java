package me.robertozimek.android.storeshoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

/**
 * StoreUtil
 * Utility class for retrieving and storing Store objects from content provider
 *
 * Created by robertozimek on 1/16/16.
 */


public class StoreUtil {
    // Adds store to database
    public static boolean addStore(Store store, Context context) {
        Uri returnedURI = null;

        // Set content values for each column
        ContentValues values = new ContentValues();
        values.put(ShoppingListContract.StoreEntry.NAME, store.getStoreName());
        values.put(ShoppingListContract.StoreEntry.STREET, store.getStreetAddress().toUpperCase().replaceAll("[.]", ""));
        values.put(ShoppingListContract.StoreEntry.CITY, store.getCityAddress().toUpperCase());
        values.put(ShoppingListContract.StoreEntry.STATE, store.getStateAddress().toUpperCase());

        /**************************************************
         * REMINDER!!!!!!!                                *
         * -----------------------------------------------*
         * MUST CHANGE NEXT 2 LINES AFTER ADDING GEOCODER *
         **************************************************/
        values.put(ShoppingListContract.StoreEntry.LATITUDE, 0);
        values.put(ShoppingListContract.StoreEntry.LONGITUDE, 0);


        // Gets CONTENT URI from contract class
        Uri CONTENT_URI = ShoppingListContract.StoreEntry.CONTENT_URI;

        // Inserts record
        returnedURI = context.getContentResolver().insert(CONTENT_URI, values);

        // Determine if store insertion was successful
        if (returnedURI != null)
            return true;
        else
            return false;
    }

}
