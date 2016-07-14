package me.robertozimek.android.storeshoppinglist;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by robertozimek on 7/13/16.
 */
@RunWith(AndroidJUnit4.class)
public class StoreProviderUnitTest extends ProviderTestCase2<ShoppingListProvider> {
    private MockContentResolver mockContentResolver;
    private static Uri STORE_CONTENT_URI;

    public StoreProviderUnitTest() {
        super(ShoppingListProvider.class, ShoppingListContract.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();

        STORE_CONTENT_URI = ShoppingListContract.StoreEntry.CONTENT_URI;
        mockContentResolver = getMockContentResolver();
        assertNotNull(mockContentResolver);
    }
    @Test
    public void testInsertion() {
        // Set content values for each column
        ContentValues values = new ContentValues();
        values.put(ShoppingListContract.StoreEntry.NAME, "Costco");
        values.put(ShoppingListContract.StoreEntry.STREET, "9153 Junction Blvd");
        values.put(ShoppingListContract.StoreEntry.CITY, "Rego Park");
        values.put(ShoppingListContract.StoreEntry.STATE, "NY");
        values.put(ShoppingListContract.StoreEntry.LATITUDE, 0);
        values.put(ShoppingListContract.StoreEntry.LONGITUDE, 0);

        assertNotNull(STORE_CONTENT_URI);
        assertNotNull(values);

        Uri insertion = mockContentResolver.insert(STORE_CONTENT_URI, values);
        assertNotNull(insertion);
        testQuery();
    }

    public void testQuery() {
        String[] columns = {ShoppingListContract.StoreEntry.ID_STORE,
                ShoppingListContract.StoreEntry.NAME,
                ShoppingListContract.StoreEntry.STREET,
                ShoppingListContract.StoreEntry.CITY,
                ShoppingListContract.StoreEntry.STATE,
                ShoppingListContract.StoreEntry.LATITUDE,
                ShoppingListContract.StoreEntry.LONGITUDE};

        Cursor cursor = mockContentResolver.query(STORE_CONTENT_URI,columns,null,null,null);
        assertNotNull(cursor);
        cursor.close();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

}
