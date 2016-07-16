package me.robertozimek.android.storeshoppinglist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by robertozimek on 1/17/16.
 */
public class PhotoHelper {
    public static String createPackageImageDirectory(Context context) {
        String directoryPath = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/files/SavedImages";

        File directory = new File(directoryPath);
        if(!directory.exists()) {
            directory.mkdirs();
        }

        return directoryPath;
    }

    public static Uri generateImageFileName(String directoryPath, String suffix) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy-hhmmss.SSS", Locale.US);

        // Generates file with unique identifier
        File imageFile = new File(directoryPath, suffix + simpleDateFormat.format(new Date()) + ".png");

        // Create Uri
        Uri fileUri = Uri.fromFile(imageFile);

        return fileUri;
    }

    // Deletes image file at file path
    public static void deleteImageFileFrom(File file) {
        if(file.exists()) {
            file.delete();
        }
    }

    public static Bitmap getBitmapFromFile(Uri imageUri, Matrix matrix) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // down-sizing image as it can throw OutOfMemory Exception for
        // larger images
        options.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(),
                options);

        int width = (int) (bitmap.getWidth());
        int height = (int) (bitmap.getHeight());

        Bitmap createdBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                width, height, matrix, false);

        return createdBitmap;
    }


    // Deletes image file at uri
    public static void deleteImageFileFrom(Uri fileUri) {
        File file = new File(fileUri.getEncodedPath());
        deleteImageFileFrom(file);
    }

}
