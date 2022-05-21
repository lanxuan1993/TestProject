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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 文件操作工具类
 */
public class FileUtils {
    public static final String TAG = "FileUtils";

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值
    private static File file;

    /**
     * 创建目录
     *
     * @param path 路径
     * @return 目录创建成功返回true，否则返回false
     */
    public static boolean createDirectory(String path) {
        File file = new File(path);
        return file.mkdirs();
    }

    /**
     * 创建文件
     *
     * @param directoryPath 文件夹路径
     * @param fileName  文件名+文件后缀（比如：text.doc）
     * @return
     */
    public static File createFile(String directoryPath, String fileName) {
        String filePath = directoryPath + "/" + fileName;
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }


    /**
     * 删除目录及目录下的文件
     *
     * @param path
     * @return 目录删除成功返回true，否则返回false
     */
    public static void delDirectory(String path) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符(斜线)
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    delDirectory(file.getAbsolutePath());
                }
            }
            // 删除当前目录
            dirFile.delete();
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 将文件转换成Base64编码
     * 将文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param path 待处理图片地址(例如:/storage/emulated/0/相机/IMG_20200429_123409.jpg)
     * @return
     */
    public static String FileToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 获取某个目录路径
     *
     * @param context
     * @param folderName
     * @return
     */
    public static String getFolderPath(Context context, String folderName) {
        return context.getDir(folderName, Context.MODE_PRIVATE) + "/";
    }


    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getAllFileSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getDirectorySize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取文件大小失败!");
        }
        return formatFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAllFileSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getDirectorySize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取文件大小失败!");
        }
        return formatFileSize(blockSize);
    }


    /**
     * 获取指定文件夹的大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getDirectorySize(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getDirectorySize(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e(TAG, "获取文件大小不存在!");
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileSize
     * @return
     */
    private static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileSize == 0) {
            return wrongSize;
        }
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double formatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }


    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param path
     * @return 删除成功返回true，否则返回false
     */
    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                delDirectory(path);
            }
        }
    }

    /**
     * 获取应用文件缓存路径
     * 用来存储一些长时间保留的数据,应用卸载会被删除
     *
     * @param context
     * @param type
     * @return
     */
    public static String getFilePath(Context context, String type) {
        String filePath;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
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
     * 拷贝文件
     *
     * @param sourceFile
     * @param dstFile
     */
    public static void copyFile(File sourceFile, File dstFile) {
        try {
            InputStream inputStream = new FileInputStream(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(dstFile);

            int BUFFER_SIZE = 1024 * 2;
            byte[] buffer = new byte[BUFFER_SIZE];
            BufferedInputStream in = new BufferedInputStream(inputStream, BUFFER_SIZE);
            BufferedOutputStream out = new BufferedOutputStream(outputStream, BUFFER_SIZE);
            int n;

            try {
                while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                    out.write(buffer, 0, n);
                }
                out.flush();
            } finally {
                try {
                    out.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 从assets目录拷贝文件到指定目录
     *
     * @param context
     * @param strOutFileName
     * @param zipName
     */
    public static void copyBigDataToSD(Context context, String strOutFileName, String zipName) {
        try {
            InputStream myInput;
            OutputStream myOutput = null;
            myOutput = new FileOutputStream(strOutFileName);
            myInput = context.getAssets().open(zipName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     */
    public static boolean upZipFile(String zipFilePath, String folderPath) {
        File zipFile = new File(zipFilePath);
        return upZipFile(zipFile, folderPath);
    }

    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     */
    public static boolean upZipFile(File zipFile, String folderPath) {
        boolean result = false;
        try {
            ZipFile zfile = new ZipFile(zipFile);
            Enumeration zList = zfile.entries();
            ZipEntry ze = null;
            byte[] buf = new byte[1024];
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                if (ze.isDirectory()) {
                    //Logcat.d("upZipFile", "ze.getName() = " + ze.getName());
                    String dirstr = folderPath + ze.getName();
                    //dirstr.trim();
                    dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                    //Logcat.d("upZipFile", "str = " + dirstr);
                    File f = new File(dirstr);
                    f.mkdir();
                    continue;
                }
                //Logcat.d("upZipFile", "ze.getName() = " + ze.getName());
                OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
                InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
                int readLen = 0;
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
                is.close();
                os.close();
            }
            zfile.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 根据保存zip的文件路径和zip文件相对路径名，返回一个实际的文件
     * 因为zip文件解压后，里边可能是多重文件结构
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            if (!ret.exists()) {
                ret.mkdirs();
            }
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            return ret;
        }
        return ret;
    }


    /**
     * 从assets目录解压缩文件到指定目录
     *
     * @param assetName
     * @param folderPath
     */
    public static void unZip(Context context, String assetName, String folderPath) {
        try {
            InputStream inputStream = context.getAssets().open(assetName);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            // 读取一个进入点
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            byte[] buffer = new byte[1024 * 1024];
            int count = 0;
            // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
            while (nextEntry != null) {
                // 如果是一个文件夹
                if (nextEntry.isDirectory()) {
                    file = new File(folderPath + File.separator + nextEntry.getName());
                    if (!file.exists()) {
                        file.mkdir();
                    }
                } else {
                    // 如果是文件那就保存
                    file = new File(folderPath + File.separator + nextEntry.getName());
                    // 则解压文件
                    if (!file.exists()) {
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        while ((count = zipInputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, count);
                        }

                        fos.close();
                    }
                }

                //这里很关键循环解读下一个文件
                nextEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
     * 获取应用专用缓存文件路径
     * 一般存放临时缓存数据,应用卸载会被删除
     *
     * @param context
     * @return
     */
    public static String getCachePath(Context context) {
        String cachePath = null;
        try {
            if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
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
    public Uri queryMediaFile(Context context, Uri queryUri) {
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
