package com.example.tools.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static final String TAG = "FileUtils";

    /**
     * 保存文件到app私有目录
     *
     * @param context
     * @param type    {@link android.os.Environment#DIRECTORY_MUSIC},
     *                {@link android.os.Environment#DIRECTORY_PODCASTS},
     *                {@link android.os.Environment#DIRECTORY_RINGTONES},
     *                {@link android.os.Environment#DIRECTORY_ALARMS},
     *                {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link android.os.Environment#DIRECTORY_PICTURES},
     *                {@link android.os.Environment#DIRECTORY_MOVIES}
     */
    public void createSpecificFile(Context context, String type, String child) {
        File newFile = new File(getFilePath(context, type), child);
        OutputStream fileOS = null;
        try {
            fileOS = new FileOutputStream(newFile);
            if (fileOS != null) {
                fileOS.write("file is created".getBytes(StandardCharsets.UTF_8));
                fileOS.flush();
            }
        } catch (IOException e) {
            LogUtils.i(TAG, "create file fail");
        } finally {
            try {
                if (fileOS != null) {
                    fileOS.close();
                }
            } catch (IOException e1) {
                LogUtils.i(TAG, "close stream fail");
            }
        }
    }


    /**
     * 获取应用专用文件路径
     * 用来存储一些长时间保留的数据,应用卸载会被删除
     *
     * @param context
     * @param type
     * @return
     */
    public static String getFilePath(Context context, String type) {
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            filePath = context.getExternalFilesDir(type).getAbsolutePath();
        } else {
            //外部存储不可用
            filePath = context.getFilesDir().getAbsolutePath();
        }
        return filePath;
    }

    /**
     * 获取应用专用缓存文件路径
     * 一般存放临时缓存数据,应用卸载会被删除
     *
     * @param context
     * @return
     */
    public static String getCachePath(Context context) {
        String cachePath = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                    !Environment.isExternalStorageRemovable()) {
                cachePath = context.getExternalCacheDir().getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cachePath;
    }


    /**
     * 使用MediaStore新建文件
     *
     * @param context
     */
    public void createMediaFile(Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "Image.png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.TITLE, "Image.png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/test");

        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        Uri insertUri = resolver.insert(external, values);
        LogUtils.d(TAG, "insertUri: " + insertUri);

        OutputStream os = null;
        try {
            if (insertUri != null) {
                os = resolver.openOutputStream(insertUri);
            }
            if (os != null) {
                final Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
                // write what you want
            }
        } catch (IOException e) {
            LogUtils.d(TAG, "fail: " + e.getCause());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                LogUtils.d(TAG, "fail in close: " + e.getCause());
            }
        }
    }


    /**
     * 使用MediaStore查询文件
     *
     * @param context
     * @param queryUri 例如：MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     * @return
     */
    public Uri queryMediaFile(Context context,Uri queryUri) {
        ContentResolver resolver = context.getContentResolver();
        String selection = MediaStore.Images.Media.TITLE + "=?";
        String[] args = new String[]{"Image"};
        String[] projection = new String[]{MediaStore.Images.Media._ID};
        Cursor cursor = resolver.query(queryUri, projection, selection, args, null);
        Uri imageUri = null;
        if (cursor != null && cursor.moveToFirst()) {
            imageUri = ContentUris.withAppendedId(queryUri, cursor.getLong(0));
            cursor.close();
        }
        return imageUri;
    }

    /**
     * 使用MediaStore删除文件
     *
     * @param context
     * @param uri
     */
    public void deleteMediaFile(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(uri, null, null);
    }


}
