package me.robertozimek.android.storeshoppinglist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by robertozimek on 7/15/16.
 */
public class DisplayItemFragment extends Fragment {
    private int storeID;
    private int itemID;
    private ShoppingItem shoppingItem;

    public DisplayItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_item, container, false);
        Context context = getContext();
        setHasOptionsMenu(true);

        // Adds back button and sets new actionbar title
        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Gets arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            storeID = arguments.getInt("store_id");
            itemID = arguments.getInt("item_id");

            // Gets item object
            shoppingItem = ShoppingItemUtil.getShoppingItemWithID(itemID, context);

            // Set action bar title
            activity.getSupportActionBar().setTitle("Item - " + shoppingItem.getItemName());

            // Retrieves view for displaying data
            TextView itemName = (TextView) view.findViewById(R.id.itemName_display);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_display);

            itemName.setText(shoppingItem.getItemName());

            if(shoppingItem.getImagePath() != null) {
                Uri imageUri = Uri.parse(shoppingItem.getImagePath());
                File imageFile = new File(imageUri.getEncodedPath());

                if (imageFile.exists()) {
                    Matrix matrix = new Matrix();
                    Bitmap bitmap = PhotoHelper.getBitmapFromFile(imageUri, matrix);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }


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
