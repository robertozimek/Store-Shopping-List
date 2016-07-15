package me.robertozimek.android.storeshoppinglist;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class StoreListFragment extends Fragment {
    private RecyclerView recyclerView;
    private StoreAdapter storeAdapter;
    private List<Store> stores;
    private OnStoreFragmentInteractionListener mListener; // listener for handling clicks on RecyclerView
    private static final String ARG_COLUMN_COUNT = "column-count"; // key string for bundle argument retrieval
    private int mColumnCount = 1; // default number of columns

    public StoreListFragment() {
    }

    public static StoreListFragment newInstance(int columnCount) {
        StoreListFragment fragment = new StoreListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Retrieves passed in bundle argrument for number of columns
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_list, container, false);

        // Retrieve stores list
        Context context = getContext();
        stores = StoreUtil.getStoresList(context);

        // Set ActionBar Title and Up button
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setTitle(R.string.actionbar_stores);

        // Set the adapter
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;

            // Adds a divider line under each RecyclerView - SOURCE for class code provided in the header comment of SimpleDividerItemDecoration class
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context.getApplicationContext()));

            // Sets recyclerView layout to either a linearlayout or gridlayout
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // Sets adapter and send list of stores and listener for handling onClick of RecyclerView
            storeAdapter = new StoreAdapter(stores, mListener, context);
            recyclerView.setAdapter(storeAdapter);
        }
        return view;
    }


    // Establish communication with the Main Activty
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Sets context of listener
        if (context instanceof OnStoreFragmentInteractionListener) {
            mListener = (OnStoreFragmentInteractionListener) context;

        }
        // Ensures that class that implements listener has the listener method implemented
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStoreFragmentInteractionListener");
        }
    }

    // Clean up listener on detach
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Interface for listener
    public interface OnStoreFragmentInteractionListener {
        void onStoreFragmentInteraction(Store item);
    }
}
