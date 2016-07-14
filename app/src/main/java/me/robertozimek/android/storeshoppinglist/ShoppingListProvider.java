package me.robertozimek.android.storeshoppinglist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ShoppingListProvider extends ContentProvider {
    // DECLARING URIs
    public static final String PROVIDER_NAME = "robertozimek.android.storeshoppinglist.ShoppingListProvider";
    public static final String PATH_STORE = "stores";
    public static final String PATH_ITEMLIST = "itemlist";
    public static final Uri CONTENT_URI_STORE = Uri.parse("content://" + PROVIDER_NAME + "/" + PATH_STORE);
    public static final Uri CONTENT_URI_ITEMLIST = Uri.parse("content://" + PROVIDER_NAME + "/" + PATH_ITEMLIST);

    // COLUMNS FOR STORE TABLE
    private static final String ID_STORE = "store_id";
    private static final String NAME = "name";
    private static final String STREET = "street";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";

    // COLUMNS FOR SHOPPING LIST ITEM
    private static final String ID_ITEM = "item_id";
    private static final String ITEM_NAME = "item_name";
    private static final String IMAGE_PATH = "image_path";
    private static final String ITEM_STORE_ID = "item_store_id";

    // URI for all records or specific id
    private static final int STORES = 1;
    private static final int STORES_ID = 2;
    private static final int ITEMLIST = 3;
    private static final int ITEMLIST_ID = 4;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "stores", STORES);
        uriMatcher.addURI(PROVIDER_NAME, "stores/#", STORES_ID);
        uriMatcher.addURI(PROVIDER_NAME, "itemlist", ITEMLIST);
        uriMatcher.addURI(PROVIDER_NAME, "itemlist/#", ITEMLIST_ID);
    }

    // DECLARES DATABASE AND TABLE CREATION PARAMETERS
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "Shoppinglist";
    private static final String STORES_TABLE_NAME = "stores";
    private static final String ITEMLIST_TABLE_NAME = "itemlist";
    private static final int DATABASE_VERSION = 1;

    // STORES TABLE CREATION PARAMETERS
    private static final String CREATE_DB_STORETABLE =
            "CREATE TABLE IF NOT EXISTS " + STORES_TABLE_NAME +
                    " (" + ID_STORE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT NOT NULL, " +
                    STREET + " TEXT NOT NULL, " +
                    CITY + " TEXT NOT NULL, " +
                    STATE + " TEXT NOT NULL, " +
                    LONGITUDE + " REAL NOT NULL, " +
                    LATITUDE + " REAL NOT NULL);";

    // SHOPPING LIST ITEMS TABLE CREATION PARAMETERS
    private static final String CREATE_DB_ITEMTABLE =
            "CREATE TABLE IF NOT EXISTS " + ITEMLIST_TABLE_NAME +
                    " (" + ID_ITEM + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ITEM_NAME + " TEXT NOT NULL, " +
                    IMAGE_PATH + " TEXT, " +
                    ITEM_STORE_ID + " INTEGER REFERENCES " + STORES_TABLE_NAME + "(" + ID_STORE + ") ON DELETE CASCADE);";


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Create databases
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_STORETABLE); // first create store table because of foreign key in item table
            db.execSQL(CREATE_DB_ITEMTABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  STORES_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " +  ITEMLIST_TABLE_NAME);

            onCreate(db);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }
    }

    public ShoppingListProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteCount = 0;
        switch (uriMatcher.match(uri)){
            case STORES:
                // deletes based on selection & selectionArgs parameter
                deleteCount = db.delete(STORES_TABLE_NAME, selection, selectionArgs);
                break;
            case STORES_ID:
                // deletes based on id and selection & selectionArgs
                deleteCount = db.delete( STORES_TABLE_NAME, ID_STORE +  " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            case ITEMLIST:
                // deletes based on selection & selectionArgs parameter
                deleteCount = db.delete(ITEMLIST_TABLE_NAME, selection, selectionArgs);
                break;
            case ITEMLIST_ID:
                // deletes based on id and selection & selectionArgs
                deleteCount = db.delete( ITEMLIST_TABLE_NAME, ID_ITEM +  " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case STORES:
            case ITEMLIST:
                return "vnd.android.cursor.dir/vnd.me.robertozimek.android.geo_shoppinglist.ShoppingListProvider";
            case STORES_ID:
            case ITEMLIST_ID:
                return "vnd.android.cursor.item/vnd.me.robertozimek.android.geo_shoppinglist.ShoppingListProvider";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri _uri = null;
        long _ID;
        switch (uriMatcher.match(uri)) {
            case STORES:
                _ID = db.insert(STORES_TABLE_NAME, "", values);
                if (_ID > 0) {
                    // Appends insert with id
                    _uri = ContentUris.withAppendedId(CONTENT_URI_STORE, _ID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case ITEMLIST:
                _ID = db.insert(ITEMLIST_TABLE_NAME, "", values);
                if (_ID > 0) {
                    // Appends insert with id
                    _uri = ContentUris.withAppendedId(CONTENT_URI_ITEMLIST, _ID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            default:
                throw new SQLException("Failed to insert into " + uri);
        }
        return _uri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // retrieves database
        db = dbHelper.getWritableDatabase();
        return (db != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int matcher = uriMatcher.match(uri);

        // If no sort order was specified set sort order by id
        boolean setDefaultSortOrder = (sortOrder == null || sortOrder == "");

        switch (matcher) {
            case STORES:
                // builder query with all stores
                queryBuilder.setTables(STORES_TABLE_NAME);
                if (setDefaultSortOrder) { sortOrder = ID_STORE; }
                break;
            case STORES_ID:
                // build query with provided id
                queryBuilder.setTables(STORES_TABLE_NAME);
                queryBuilder.appendWhere(ID_STORE + "=" + uri.getPathSegments().get(1));
                if (setDefaultSortOrder) { sortOrder = ID_STORE; }
                break;
            case ITEMLIST:
                // builder query with all items
                queryBuilder.setTables(ITEMLIST_TABLE_NAME);
                if (setDefaultSortOrder) { sortOrder = ID_ITEM; }
                break;
            case ITEMLIST_ID:
                // build query with provided id
                queryBuilder.setTables(ITEMLIST_TABLE_NAME);
                queryBuilder.appendWhere(ID_ITEM + "=" + uri.getPathSegments().get(1));
                if (setDefaultSortOrder) { sortOrder = ID_ITEM; }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get a cursor of query
        Cursor cursor = queryBuilder.query(db,	projection,	selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updateCount = 0;

        switch (uriMatcher.match(uri)){
            case STORES:
                // Update records with provided condition
                updateCount = db.update(STORES_TABLE_NAME, values, selection, selectionArgs);
                break;
            case STORES_ID:
                // Update records with provided id and provided condition
                updateCount = db.update(STORES_TABLE_NAME, values, ID_STORE + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            case ITEMLIST:
                // Update records with provided condition
                updateCount = db.update(ITEMLIST_TABLE_NAME, values, selection, selectionArgs);
                break;
            case ITEMLIST_ID:
                // Update records with provided id and provided condition
                updateCount = db.update(ITEMLIST_TABLE_NAME, values, ID_ITEM + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}
