package com.example.sandd_vmobile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    public static File compressImage(Context context, Uri imageUri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        if (input != null) {
            input.close();
        }

        File compressedFile = new File(context.getCacheDir(), "compressed_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream out = new FileOutputStream(compressedFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        out.flush();
        out.close();

        return compressedFile;
    }
}

