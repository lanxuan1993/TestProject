package com.example.tools.picture.photo.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;

/**
 * @author: created by ZhaoBeibei on 2020-06-08 16:39
 * @describe: 图片处理工具
 */
public class ImageUtils {
    private static final String TAG = ImageUtils.class.getName();

    /**
     * 根据图片Uri压缩图片并保存到新的文件(Uri)
     *
     * @param context
     * @param oldUri
     * @param newUri
     * @param quality
     * @return
     * @throws IOException
     */
    public static Uri compressImage(Context context, Uri oldUri, Uri newUri, int quality, int maxLength,
                                    int width, int height, boolean correctOrientation) {
        int degree = getImageDegree(context, oldUri);
        Bitmap bitmap = uriToBitmap(context, oldUri);
        bitmap = scaleAndRotateImage(bitmap, width, height, correctOrientation, degree);
        byte[] bytes = qualityCompress(bitmap, quality, maxLength);
        Uri uri = saveCompressed(context, newUri, bytes);
        return uri;
    }


    /**
     * 根据图片路径(Path)压缩图片并保存到新的文件(Path)
     *
     * @param oldPath
     * @param newPath
     * @param quality
     */
    public static String compressImage(String oldPath, String newPath, int quality, int maxLength,
                                       int width, int height, boolean correctOrientation) {
        int degree = getImageDegree(oldPath);
        Bitmap bitmap = pathToBitmap(oldPath);
        bitmap = scaleAndRotateImage(bitmap, width, height, correctOrientation, degree);
        byte[] bytes = qualityCompress(bitmap, quality, maxLength);
        String path = saveCompressed(newPath, bytes);
        return path;
    }

    /************************************************旋转缩放图片***********************************************/

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getImageDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ORIENTATION_NORMAL);
            switch (orientation) {
                case ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param context
     * @param uri
     * @return
     */
    private static int getImageDegree(Context context, Uri uri) {
        int degree = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.ORIENTATION},
                    null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                degree = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return degree;
    }

    /**
     * 缩放旋转图片
     *
     * @param bitmap
     * @param reqWidth
     * @param reqHeight
     * @param correctOrientation
     * @param degree
     * @return
     */
    public static Bitmap scaleAndRotateImage(Bitmap bitmap, int reqWidth, int reqHeight,
                                             boolean correctOrientation, int degree) {
        boolean isScale = reqWidth > 0 && reqHeight > 0;
        boolean isRotate = correctOrientation && (degree != 0);
        if (!isScale && !isRotate) {
            return bitmap;
        }

        Bitmap resultBm = null;
        try {
            Matrix matrix = new Matrix();
            if (isScale) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float sx = (float) reqWidth / width;
                float sy = (float) reqHeight / height;
                float scale;
                if (sx > sy) {
                    scale = sy;
                } else {
                    scale = sx;
                }
                matrix.postScale(scale, scale);
            }

            if (isRotate) {
                matrix.postRotate(degree);
            }
            resultBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resultBm == null) {
            resultBm = bitmap;
        }
        if (bitmap != resultBm) {
            bitmap.recycle();
        }
        return resultBm;
    }

    /************************************************质量压缩图片********************************************/

    /**
     * 质量压缩图片
     *
     * @param bitmap
     * @param quality
     * @param maxLength
     */
    public static byte[] qualityCompress(Bitmap bitmap, int quality, int maxLength) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (maxLength > 0) {
                int targetQuality = 100;
                int maxQuality = 100;
                int minQuality = 0;
                bitmap.compress(Bitmap.CompressFormat.JPEG, targetQuality, baos);
                if (baos.toByteArray().length / 1024 > maxLength) {
                    // 最多压缩6次,二分法
                    for (int i = 0; i < 6; i++) {
                        targetQuality = (maxQuality + minQuality) / 2;
                        baos.reset();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, targetQuality, baos);
                        int length = baos.toByteArray().length / 1024;
                        if (length < maxLength * 0.9) {
                            minQuality = targetQuality;
                        } else if (length > maxLength) {
                            maxQuality = targetQuality;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            }

            byte[] bytes = baos.toByteArray();
            baos.flush();
            baos.close();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /************************************************尺寸压缩图片***********************************************/
    /**
     * 尺寸压缩图片(采用BitmapFactory.decodeStream)
     *
     * @param uri
     * @param width
     * @param height
     * @return
     */
    public static Bitmap sizeCompress(Context context, Uri uri, int width, int height) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(options, width, height);
            inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 尺寸压缩图片(采用BitmapFactory.decodeFile)
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap sizeCompress(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置仅读取Bitmap的宽高而不读取内容
        BitmapFactory.decodeFile(path, options);// 获取到图片的宽高，放在option里边
        options.inJustDecodeBounds = false;
        // inSampleSize的默认值和最小值为1（当小于1时，解码器将该值当做1来处理）
        options.inSampleSize = calculateInSampleSize(options, width, height);
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 计算出所需要压缩的大小
     *
     * @param options
     * @param reqWidth  我们期望的图片的宽，单位px
     * @param reqHeight 我们期望的图片的高，单位px
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round(height) / reqHeight;
            int widthRatio = Math.round(width) / reqWidth;
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /************************************************保存压缩图片***********************************************/

    /**
     * 压缩后保存到文件路径(path)
     *
     * @param newPath
     * @param bytes
     * @return
     */
    public static String saveCompressed(String newPath, byte[] bytes) {
        try {
            FileOutputStream fos = new FileOutputStream(newPath);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return newPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 压缩后保存到Uri
     *
     * @param context
     * @param newUri
     * @param bytes
     * @return
     */
    public static Uri saveCompressed(Context context, Uri newUri, byte[] bytes) {
        try {
            newUri = UriUtils.checkUri(context, newUri, null);
            OutputStream outputStream = context.getContentResolver().openOutputStream(newUri);
            if (outputStream != null) {
                outputStream.write(bytes);
                outputStream.close();
            }
            return newUri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /************************************************图片转换成Base64编码***********************************************/
    /**
     * 将图片转换成Base64编码
     *
     * @param uri 待处理图片uri
     * @return
     */
    public static String imgToBase64(Context context, Uri uri) {
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
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
     * 将图片转换成Base64编码
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param path 待处理图片地址(例如:/storage/emulated/0/相机/IMG_20200429_123409.jpg)
     * @return
     */
    public static String imgToBase64(String path) {
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
     * 将图片转换成Base64编码
     *
     * @param bitmap
     * @param format (Bitmap.CompressFormat.JPEG 或者 Bitmap.CompressFormat.PNG)
     * @return
     */
    public static String imgToBase64(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        byte[] pngByte = baos.toByteArray(); // 转为byte数组
        return Base64.encodeToString(pngByte, Base64.DEFAULT);
    }


    /************************************************图片转换成Bitmap***********************************************/

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return Bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        if (!TextUtils.isEmpty(base64Data)) {
            byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    /**
     * uri转为bitmap
     *
     * @param uri
     * @return
     */
    public static Bitmap uriToBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 根据图片路径转为bitmap
     *
     * @param path
     * @return
     */
    public static Bitmap pathToBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    /**
     * 获取服务器上的图片尺寸
     */
    public static int[] getImgWH(String urls) {
        int[] imgSize = new int[2];
        try {
            URL url = new URL(urls);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(is);
            int srcWidth = image.getWidth();      // 源图宽度
            int srcHeight = image.getHeight();    // 源图高度
            imgSize[0] = srcWidth;
            imgSize[1] = srcHeight;

            image.recycle();
            is.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgSize;
    }

    /**
     * Url转换为Drawable
     *
     * @param imageUrl
     * @return
     */
    public static Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return drawable;
    }
}
