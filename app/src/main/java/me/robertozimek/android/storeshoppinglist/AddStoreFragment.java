package me.robertozimek.android.storeshoppinglist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Fragment Class - AddStoreFragment
 * Form fragment for adding stores
 *
 * Created by robertozimek on 1/14/16.
 */

public class AddStoreFragment extends Fragment {

    public AddStoreFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        final Context context = getContext();

        View view = inflater.inflate(R.layout.fragment_add_store, container, false);
        setHasOptionsMenu(true);

        // Adds back button and sets new actionbar title
        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.action_add_store);

        // Retrieve EditText views
        final EditText storeName = (EditText) view.findViewById(R.id.edittext_storename);
        final EditText street = (EditText) view.findViewById(R.id.edittext_street);
        final EditText city = (EditText) view.findViewById(R.id.edittext_city);
        final EditText state = (EditText) view.findViewById(R.id.state_edittext);

        // Add button on click listener
        Button addStoreButton = (Button) view.findViewById(R.id.button_addstore);
        addStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create store object with EditText values
                Store store = new Store(storeName.getText().toString(),
                        street.getText().toString(),
                        city.getText().toString(),
                        state.getText().toString());

                if (!store.getStoreName().isEmpty() && StoreUtil.addStore(store, v.getContext())) {
                    // If successfully added, return to stores list fragment
                    activity.onBackPressed();
                }

            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu != null) {
            menu.removeItem(R.id.action_add_store);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
}
