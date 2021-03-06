package com.example.signatureexample.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Works {

    public static String ImageToBase64(Context context) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/Signature.jpg");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] imageBytes = stream.toByteArray();
        Log.w("IMAGENES", Arrays.toString(imageBytes));
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

}
