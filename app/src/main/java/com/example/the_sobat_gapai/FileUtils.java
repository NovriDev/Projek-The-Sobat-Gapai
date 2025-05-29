package com.example.the_sobat_gapai;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static String getPathFromURI(Context context, Uri uri) throws IOException {
        if (uri == null) {
            return null;
        }

        // Cek apakah URI adalah file
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        // Gunakan ContentResolver untuk mengakses file
        ContentResolver contentResolver = context.getContentResolver();
        String fileName = getFileName(contentResolver, uri);
        if (fileName == null) {
            throw new IOException("File name not found for URI: " + uri);
        }

        File file = new File(context.getCacheDir(), fileName);
        try (InputStream inputStream = contentResolver.openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {

            if (inputStream == null) {
                throw new IOException("Failed to open InputStream for URI: " + uri);
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Gunakan metode write dengan offset dan panjang
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return file.getAbsolutePath();
    }


    private static String getFileName(ContentResolver contentResolver, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        try (Cursor cursor = contentResolver.query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            }
        }
        return null;
    }

    public static String getPath(Context context, Uri uri) {
        // Handle versi Android yang lebih baru
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return context.getExternalFilesDir(null) + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = Uri.parse("content://downloads/public_downloads").buildUpon().appendPath(id).build();
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public static String convertToBase64(File file) throws IOException {
        FileInputStream fileInputStream = null;
        byte[] fileBytes;
        String base64String;

        try {
            // Membaca file menjadi byte array
            fileInputStream = new FileInputStream(file);
            fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);

            // Mengonversi byte array menjadi string base64
            base64String = Base64.encodeToString(fileBytes, Base64.NO_WRAP);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        return base64String;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
