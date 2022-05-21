package com.example.tools.picture.photo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.example.tools.utils.EncryptionUtils;
import com.example.tools.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author: created by ZhaoBeibei on 2020-06-08 17:45
 * @describe: Uri工具类
 */
public class UriUtils {
    private static final String TAG = "UriUtils";

    /**
     * uri检查
     * 将scheme为file的转换为FileProvider提供的uri，判断输入的SCHEME_FILE格式的在androidQ上是否能够使用
     *
     * @param context
     * @param uri
     * @param suffix  默认.jpg
     * @return
     */
    public static Uri checkUri(Context context, Uri uri, String suffix) {
        if (uri == null) {
            return getTempSchemeContentUri(context, suffix);
        }

        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                //检查App运行模式是否兼容Android10以下旧模式
                if (Environment.isExternalStorageLegacy()) {
                    Log.w(TAG, "当前是Legacy View视图，兼容File方式访问");
                    return getContentUriFromFileUri(context, uri);
                } else {
                    if (checkAppSpecific(uri, context)) {
                        return getContentUriFromFileUri(context, uri);
                    } else {
                        Log.w(TAG, "当前存储空间视图模式是Filtered View(Android10分区存储)，不能直接访问App-specific外的文件，" +
                                "Environment.getExternalStorageDirectory()不能使用，请使用MediaStore或者使用getExternalFilesDirs、getExternalCacheDirs等");
                    }
                }
            } else {
                return getContentUriFromFileUri(context, uri);
            }
        }
        return uri;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean checkAppSpecific(Uri uri, Context context) {
        if (uri == null || uri.getPath() == null) {
            return false;
        }
        String path = uri.getPath();
        if (context.getExternalMediaDirs().length != 0 && path.startsWith(context.getExternalMediaDirs()[0].getAbsolutePath())) {
            return true;
        } else if (path.startsWith(context.getObbDir().getAbsolutePath())) {
            return true;
        } else if (path.startsWith(context.getExternalCacheDir().getAbsolutePath())) {
            return true;
        } else if (path.startsWith(context.getExternalFilesDir("").getAbsolutePath())) {
            return true;
        }
        return false;
    }


    /**
     * 创建Uri
     * 创建Scheme为Content开头的临时uri
     *
     * @param context
     * @param suffix
     * @return
     */
    public static Uri getTempSchemeContentUri(Context context, String suffix) {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String childPath = "/img/" + fileName + (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);
        String filePath = FileUtils.getFilePath(context, Environment.DIRECTORY_PICTURES);
        File file = new File(filePath, childPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return getUriFromFile(context, file);
    }

    /**
     * 创建Uri
     * 创建压缩后存放的uri
     *
     * @param context
     * @param suffix
     * @return
     */
    public static Uri getCompressUri(Context context, String suffix) {
        String timeStamp = Long.toString(System.currentTimeMillis());
        String fileName = EncryptionUtils.md5Decode32(timeStamp + "bhfile");
        String childPath = "/img/" + fileName + (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);
        String filePath = FileUtils.getFilePath(context, Environment.DIRECTORY_PICTURES);
        File file = new File(filePath, childPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return getUriFromFile(context, file);
    }

    /**
     * 根据文件路径Path获取Uri
     *
     * @param context
     * @param uri(file://...)
     * @return
     */
    public static Uri getContentUriFromFileUri(Context context, Uri uri) {
        File file = new File(uri.getPath());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return getUriFromFile(context, file);
    }

    /**
     * 根据File获取Uri
     *
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //如果在Android7.0以上,使用FileProvider获取Uri
            return FileProvider.getUriForFile(context, getFileProviderName(context), file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".provider";
    }
}
