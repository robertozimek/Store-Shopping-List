package me.robertozimek.android.storeshoppinglist;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ShoppingListContract {
    public static final String PATH_STORE = ShoppingListProvider.PATH_STORE;
    public static final String PATH_ITEMLIST = ShoppingListProvider.PATH_ITEMLIST;

    public static final class StoreEntry implements BaseColumns {
        public static final Uri CONTENT_URI = ShoppingListProvider.CONTENT_URI_STORE;

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_STORE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_STORE;

        public static final int STORES = 1;
        public static final int STORES_ID = 2;

        public static final String TABLE_NAME = "stores";
        public static final String ID_STORE = "store_id";
        public static final String NAME = "name";
        public static final String STREET = "street";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";

        public static Uri getStoreUriWithID(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ItemEntry implements BaseColumns{
        public static final Uri CONTENT_URI = ShoppingListProvider.CONTENT_URI_ITEMLIST;

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_ITEMLIST;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_ITEMLIST;

        public static final int ITEMLIST = 3;
        public static final int ITEMLIST_ID = 4;

        public static final String TABLE_NAME = "itemlist";
        public static final String ID_ITEM = "item_id";
        public static final String ITEM_NAME = "item_name";
        public static final String IMAGE_PATH = "image_path";
        public static final String ITEM_STORE_ID = "item_store_id";

        public static Uri getItemListUriWithID(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
