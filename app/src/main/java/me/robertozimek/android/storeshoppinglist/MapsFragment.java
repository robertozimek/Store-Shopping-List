package me.robertozimek.android.storeshoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by robertozimek on 1/17/16.
 */
public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    GoogleMap mMap;
    private OnMapFragmentInteractionListener mListener;
    private static View rootView;
    private static SupportMapFragment mapFragment;
    private List<Store> mStoreList = new ArrayList<Store>();
    private HashMap<Marker, Integer> mHashMarkers = new HashMap<Marker, Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
        }

        // Adds back button and sets new actionbar title
        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.actionbar_map);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener((GoogleMap.OnInfoWindowClickListener) this);

        // Sets map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // Adds stores to map
        for(int i = 0; i < mStoreList.size(); i++) {
            Store store = mStoreList.get(i);
            String title = store.getStoreName();
            String snippet = store.getShoppingItemsCount() + " items";
            LatLng storeLocation = store.getCoordinates();
            float colorIconDescriptor = BitmapDescriptorFactory.HUE_GREEN;
            if(store.getShoppingItemsCount() >= 10) {
                colorIconDescriptor = BitmapDescriptorFactory.HUE_RED;
            } else if (store.getShoppingItemsCount() >= 5){
                colorIconDescriptor = BitmapDescriptorFactory.HUE_ORANGE;
            }

            // Add Marker
            Marker mark = mMap.addMarker(new MarkerOptions().position(storeLocation).title(title).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(colorIconDescriptor)));
            mHashMarkers.put(mark, i);

            float zoomLevel = 11.5f;

            if(i == (mStoreList.size() - 1)) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, zoomLevel));
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int position = mHashMarkers.get(marker);
        mListener.onMapFragmentInteraction(mStoreList.get(position));
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadPins();
    }

    private void loadPins() {
        Context context = getContext();

        // Retrieves store list
        mStoreList = StoreUtil.getStoresList(context);

        if(mapFragment == null) {
            mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.googlemap);
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu != null) {
            menu.removeItem(R.id.action_add_store);
            menu.removeItem(R.id.action_map);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Establish communication with the Main Activty
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Sets context of listener
        if (context instanceof OnMapFragmentInteractionListener) {
            mListener = (OnMapFragmentInteractionListener) context;

        }
        // Ensures that class that implements listener has the listener method implemented
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    // Clean up listener on detach
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        if(mMap != null) {
            mMap.clear();
            loadPins();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            SupportMapFragment fragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.googlemap);
            if (fragment != null) getFragmentManager().beginTransaction().remove(fragment).commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rootView = null;
        mapFragment = null;
    }

    public interface OnMapFragmentInteractionListener {
        void onMapFragmentInteraction(Store item);
    }
}

