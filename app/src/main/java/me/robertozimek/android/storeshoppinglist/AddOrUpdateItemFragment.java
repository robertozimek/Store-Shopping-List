package me.robertozimek.android.storeshoppinglist;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Set;

/**
 * Fragment Class - AddOrUpdateItemFragment
 * Form fragment for adding or updating items in shopping list
 *
 * Created by robertozimek on 1/14/16.
 */
public class AddOrUpdateItemFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CAMERA = 10;
    static final int REQUEST_WRITE = 20;
    private Bitmap bitmap;
    private Uri fileUri;
    private int storeID;
    private int itemID;
    private boolean updateItem = false;
    private ShoppingItem mShoppingItem;
    private ImageView itemImageView;
    private String imagePath;
    private Context context;

    public void AddEditItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_or_update_item, container, false);
        setHasOptionsMenu(true);

        context = getContext();

        // Retrieve store id from arguments bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            Set<String> keys = arguments.keySet();
            storeID = arguments.getInt("store_id");
            if (keys.contains("item_id")) {
                updateItem = true;
                itemID = arguments.getInt("item_id");
                mShoppingItem = ShoppingItemUtil.getShoppingItemWithID(itemID, context);
            }  else {
                mShoppingItem = new ShoppingItem();
                mShoppingItem.setStoreID(storeID);
            }
        }

        // Retrieve View elements
        final EditText itemNameEditText = (EditText) view.findViewById(R.id.itemname_edittext);
        itemImageView = (ImageView) view.findViewById(R.id.item_imageview);
        Button itemButton = (Button) view.findViewById(R.id.item_button);

        // Adds back button and sets new actionbar title
        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Determines if this view is for updating an item or adding an item
        if(updateItem) {
            itemNameEditText.setText(mShoppingItem.getItemName());
            itemButton.setText(R.string.updateitem_button);
            activity.getSupportActionBar().setTitle(R.string.actionbar_updateitem);

            // Loads image from file into image view
            if(mShoppingItem.getImagePath() != null) {
                Uri imageUri = Uri.parse(mShoppingItem.getImagePath());
                File imageFile = new File(imageUri.getEncodedPath());

                if(imageFile.exists()) {
                    Matrix matrix = new Matrix();
                    Bitmap bitmap = PhotoHelper.getBitmapFromFile(imageUri, matrix);
                    itemImageView.setImageBitmap(bitmap);
                }
            }
        } else {
            activity.getSupportActionBar().setTitle(R.string.actionbar_additem);
        }

        itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = itemNameEditText.getText().toString();

                // Checks if item name is empty and if so alerts user otherwise conitnues
                if (!alertUserIfNameEmpty(itemName)) {
                    mShoppingItem.setItemName(itemName);
                    mShoppingItem.setImagePath(imagePath);

                    // Update item
                    if (updateItem) {
                        ShoppingItemUtil.updateItemInStore(mShoppingItem, context);
                        activity.onBackPressed();
                    }
                    // Add item
                    else {
                        if(!ShoppingItemUtil.addItemToStoreID(mShoppingItem, context)) {
                            // Alerting user because adding item failed
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Duplicate Item");
                            builder.setMessage("Item already exists, please enter a new name for item");
                            builder.setCancelable(false);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Wait until clicked
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            activity.onBackPressed();
                        }
                    }
                }
            }
        });

        itemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionsFor(REQUEST_CAMERA);
            }
        });

        return view;
    }


    // Request write and camera permissions at runtime
    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionsFor(int request) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionStatus = 0;
            Toast toast;
            String manifestPermission;

            // Setup for specific permissions request
            switch (request) {
                case REQUEST_CAMERA:
                    permissionStatus = context.checkSelfPermission(Manifest.permission.CAMERA);
                    toast = Toast.makeText(context, "Camera permission are needed to take photo", Toast.LENGTH_SHORT);
                    manifestPermission = Manifest.permission.CAMERA;
                    break;
                case REQUEST_WRITE:
                    permissionStatus = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    toast = Toast.makeText(context, "Write permission are needed to save photo", Toast.LENGTH_SHORT);
                    manifestPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                    break;
                default:
                    return;
            }

            // Check if permissions granted
            if(permissionStatus == PackageManager.PERMISSION_GRANTED) {
                if(request == REQUEST_CAMERA) {
                    // Once granted request write permissions
                    requestPermissionsFor(REQUEST_WRITE);
                } else if (request == REQUEST_WRITE) {
                    // Once granted begin capturing camera
                    captureImage();
                }
            } else {
                // Let user know why permission is needed
                if(shouldShowRequestPermissionRationale(manifestPermission)) {
                    toast.show();
                }

                // Request for permission
                requestPermissions(new String[]{manifestPermission}, request);
            }
        } else {
            // For devices that have an android version lower than Android M continue to capturing photo
            captureImage();
        }
    }

    // Determine results after permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CAMERA:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermissionsFor(REQUEST_WRITE);
                } else {
                    Toast.makeText(context, "Permission was not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_WRITE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    Toast.makeText(context, "Permission was not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }


    // Capturing Camera Image will launch camera app request image capture
    private void captureImage() {
        Context context = getActivity();

        // Create directories and get URI
        String fileSuffix = "ID_" + storeID + "_";
        String path = PhotoHelper.createPackageImageDirectory(context);
        fileUri = PhotoHelper.generateImageFileName(path, fileSuffix);

        // Retrieves paths for storing
        imagePath = fileUri.getEncodedPath();

        // 4. First check if we have a camera
        boolean deviceHasCamera = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

        // 5. Our device has a camera. Lets start the native camera
        if (deviceHasCamera) {

            // 6.Create intent to take a picture
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // 7. Tell the intent that we need the camera to store the photo in
            // our file defined earlier
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // 8. start the activity with the intent created above. When this
            // activity finishes, the method onActivityResult(...) is called
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

        } else {

            Log.i("CAMERA_APP", "No camera found");

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {

                // 11. Now we need to ensure our photo is not unnecessarily
                // rotated
                Matrix matrix = new Matrix();
                ExifInterface ei = new ExifInterface(fileUri.getPath());

                // 12. Get orientation of the photograph
                int orientation = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                // 13. In case image is rotated, we rotate it back
                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                }

                Bitmap bitmap = PhotoHelper.getBitmapFromFile(fileUri, matrix);
                itemImageView.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private boolean alertUserIfNameEmpty(String itemName) {
        boolean isEmpty = itemName.isEmpty();
        if (isEmpty) {
            // Alerts user why data couldn't be added
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Unable to add");
            builder.setMessage("Item needs a name");
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Wait until clicked
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return isEmpty;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu != null) {
            menu.removeItem(R.id.action_add_store);
            menu.removeItem(R.id.action_map);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        if(imagePath != null && imagePath != mShoppingItem.getImagePath()) {
            PhotoHelper.deleteImageFileFrom(new File(imagePath));
        }
        super.onDestroy();
    }
}
