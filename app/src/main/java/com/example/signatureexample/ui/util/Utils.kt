package com.example.signatureexample.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Base64
import java.lang.StringBuilder
import java.io.*


@Throws(IOException::class)
fun readXMLFileFromAsset(context: Context): StringBuilder {
    val reader = BufferedReader(InputStreamReader(context.assets.open("MyFirma.html")))
    val sb = StringBuilder()
    var mLine = reader.readLine()
    while (mLine != null) {
        sb.append(mLine)
        mLine = reader.readLine()
    }
    reader.close()
    return sb
}

fun getDirectoryPictures(): File =
    Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
    )

fun bitMapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
