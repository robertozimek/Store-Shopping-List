package me.robertozimek.android.storeshoppinglist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends Fragment {
    private int storeID;
    private List<ShoppingItem> items = new ArrayList<ShoppingItem>();
    private OnShoppingListFragmentInteractionListener mListener;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public ShoppingListFragment() {
    }

    public static ShoppingListFragment newInstance(int columnCount) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item, menu);
        if(menu != null) {
            menu.removeItem(R.id.action_add_store);
            menu.removeItem(R.id.action_map);
        }

        // Retrieves the plus sign icon from the menu and applies a tint of white
        Drawable drawable = menu.findItem(R.id.action_add_item).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        menu.findItem(R.id.action_add_item).setIcon(drawable);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add_item){
            mListener.openAddItemFragment(false, storeID);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        Context context = getContext();

        // Retrieve store id from arguments bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            storeID = arguments.getInt("store_id");
            // Get shopping items list
            items = ShoppingItemUtil.getShoppingListForStoreID(storeID, context);
        }

        // Sets ActionBar title and adds up button
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.actionbar_itemlist);

        // Set the adapter
        if (view instanceof RecyclerView) {
            context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            // Adds a divider line under each RecyclerView - SOURCE for class code provided in the header comment of SimpleDividerItemDecoration class
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context.getApplicationContext()));

            // Sets recyclerView layout to either a linearlayout or gridlayout
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // Sets adapter sending items and a listener for handling onClick of a RecyclerView
            recyclerView.setAdapter(new ShoppingItemAdapter(items, mListener, context));
        }
        return view;
    }


    // Establish communication with the Main Activty
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShoppingListFragmentInteractionListener) {
            mListener = (OnShoppingListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnShoppingListFragmentInteractionListener {
        void onShoppingItemFragmentInteraction(ShoppingItem item);
        void openAddItemFragment(boolean updateItem, int storeID);
        void openUpdateItemFragment(ShoppingItem item);
    }
}

