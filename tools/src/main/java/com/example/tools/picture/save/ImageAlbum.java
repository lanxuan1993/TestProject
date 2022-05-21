package com.example.tools.picture.save;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: created by ZhaoBeibei on 2020-08-31 10:24
 * @describe: 保存图片到相册
 */
public class ImageAlbum {
    private static final String TAG = ImageAlbum.class.getName();
    private static ImageAlbum instance = null;
    private Context mContext;

    public static ImageAlbum getInstance(Context context) {
        if (instance == null) {
            instance = new ImageAlbum(context);
        }
        return instance;
    }

    private ImageAlbum(Context context) {
        mContext = context;
    }

    /**
     * 根据IO流保存到相册
     *
     * @param inputStream
     * @param fileName
     * @return
     */
    public boolean savaImageInputStream(InputStream inputStream, String fileName) {
        try {
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return saveBitmapToAlbum(bitmap, fileName, getAppName(mContext));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 根据本地图片文件地址保存到相册
     *
     * @param filePath : file.getAbsolutePath()
     */
    public boolean savaImageFile(String filePath, String fileName) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return saveBitmapToAlbum(bitmap, fileName, getAppName(mContext));
    }

    /**
     * 将图片Bitmap保存到相册
     *
     * @param bitmap
     * @param fileName
     * @return
     */
    public boolean saveBitmapToAlbum(Bitmap bitmap, String fileName, String folderName) {
        boolean isSaveSuc = false;
        if (bitmap != null) {
            if (Build.VERSION.SDK_INT >= 29) {
                isSaveSuc = saveImageAboveAndroidQ(bitmap, fileName, folderName);
            } else {
                isSaveSuc = saveImageBelowAndroidQ(bitmap, fileName, folderName);
            }
        }
        return isSaveSuc;
    }

    /**
     * 将图片Bitmap保存到相册(适配Android10,公共媒体文件的操作需要用到ContentResolver和Cursor)
     *
     * @param bitmap
     * @param fileName
     */
    private boolean saveImageAboveAndroidQ(Bitmap bitmap, String fileName, String folderName) {
        try {
            ContentValues contentValues = new ContentValues();
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/" + folderName);

            ContentResolver contentResolver = mContext.getContentResolver();
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    return true;
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "saveImageAboveAndroidQ: 测试云真机:" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将图片Bitmap保存到相册(适配Android10以下)
     *
     * @param bitmap
     * @param fileName
     */
    private boolean saveImageBelowAndroidQ(Bitmap bitmap, String fileName, String folderName) {
        String albumPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM;
        creatDir(albumPath);
        if (Build.BRAND.equalsIgnoreCase("xiaomi") || Build.BRAND.equalsIgnoreCase("Huawei")) {
            albumPath = albumPath + "/Camera";
            creatDir(albumPath);
        }
        String folderPath = albumPath + "/" + folderName;
        creatDir(folderPath);
        File file = new File(folderPath, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
//            insertGallery(null,file);
            refreshGallery(mContext, file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送广播，通知刷新图库的显示
     *
     * @param mContext
     * @param file
     */
    private void refreshGallery(Context mContext, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] paths = new String[]{file.getAbsolutePath()};
            MediaScannerConnection.scanFile(mContext, paths, null, null);
        } else {
            Intent intent;
            if (file.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
            }
            mContext.sendBroadcast(intent);
        }
    }


    /**
     * 插入图库
     *
     * @param bitmap
     * @param file
     */
    private void insertGallery(Bitmap bitmap, File file) {
        try {
            if (file != null) {
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
            } else if (bitmap != null) {
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, null, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取应用程序名称
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建文件夹目录
     */
    public void creatDir(String path) {
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

}
