package me.robertozimek.android.storeshoppinglist;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Store Class
 *  object contains store name, street address, city, state
 *
 * Created by robertozimek on 1/14/16.
 */
public class Store {
    private int storeID;
    private String storeName;

    private String streetAddress;
    private String cityAddress;
    private String stateAddress;

    private int shoppingItemsCount;

    private LatLng coordinates;

    // Constructor that takes store's name and street, city, state address
    public Store(String storeName, String streetAddress, String cityAddress, String stateAddress) {
        this.storeName = storeName;
        this.streetAddress = streetAddress;
        this.cityAddress = cityAddress;
        this.stateAddress = stateAddress;
    }

    // Return full address
    public String getFullAddress() {
        return this.streetAddress + " , " + this.cityAddress + " , " + this.stateAddress;
    }

    /***********************
     * Setters and Getters *
     ***********************/

    // storeID
    public int getStoreID() { return storeID; }
    public void setStoreID(int storeID) { this.storeID = storeID; }

    // storeName
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getStoreName() { return storeName; }

    // streetAddress
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getStreetAddress() { return streetAddress; }

    // cityAddress
    public String getCityAddress() { return cityAddress; }
    public void setCityAddress(String cityAddress) { this.cityAddress = cityAddress; }

    // stateAddress
    public String getStateAddress() { return stateAddress; }
    public void setStateAddress(String stateAddress) { this.stateAddress = stateAddress; }

    // shoppingItemsCount
    public int getShoppingItemsCount() { return shoppingItemsCount; }
    public void setShoppingItemsCount(int shoppingItemsCount) { this.shoppingItemsCount = shoppingItemsCount; }

    // get latitude and longitude
    public double getLatitude() { return coordinates.latitude; }
    public double getLongitude() { return coordinates.longitude; }

    // get coordinates
    public boolean retrieveCoordinates() {
        boolean success = true;
        try {
            coordinates = new GeocoderAsyncTask().execute(streetAddress, cityAddress, stateAddress).get();
        } catch(ExecutionException e) {
            e.printStackTrace();
            success = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            success = false;
        }
        return  success;
    }

}
