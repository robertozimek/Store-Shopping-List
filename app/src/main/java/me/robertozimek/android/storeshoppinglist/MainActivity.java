package me.robertozimek.android.storeshoppinglist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Make sure the fragment container exists
        if (findViewById(R.id.fragment_container) != null) {
            // Avoid adding a fragment when one already exists
            if (savedInstanceState != null) {
                return;
            }

            // Open initial fragment to store list fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Set actionbar title for fragement
            getSupportActionBar().setTitle(R.string.actionbar_stores);

            StoreListFragment storeListFragment = new StoreListFragment();
            fragmentTransaction.add(R.id.fragment_container, storeListFragment);
            fragmentTransaction.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store, menu);

        // Retrieves the plus sign icon from the menu and applies a white tint
        Drawable drawable = menu.findItem(R.id.action_add_store).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        menu.findItem(R.id.action_add_store).setIcon(drawable);

        return true;
    }

    // Handles back button presses
    @Override
    public void onBackPressed() {
        // Hide any keyboard in view
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        // Ensure back stack isn't empty before popping of the stack
        int stackCount = getFragmentManager().getBackStackEntryCount();
        if (stackCount > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    // Replaces fragment with the fragment passed in
    private void transitionToFragment(Fragment nextFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.fragment_container, nextFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle clicks for specific menu item
        switch(id) {
            case android.R.id.home:
                // call onBackPressed when actionbar 'up' button pressed
                onBackPressed();
                break;
            case R.id.action_add_store:
                // replaces current fragment with add store fragment
                Fragment addToStoreFragment = new AddStoreFragment();
                transitionToFragment(addToStoreFragment);
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
