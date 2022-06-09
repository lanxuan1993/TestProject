package com.example.tools.wechat;

import android.content.Context;
import android.util.Log;

import com.example.tools.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Util {
    public static final String TAG = Util.class.getName();
    /**
     * Read bytes from InputStream
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }


    public static File downloadAndCacheFile(Context context, String url) {
        Log.d(TAG, String.format("Start downloading file at %s.", url));
        File cacheFile = FileUtils.createFile(FileUtils.getCachePath(context),
                url.substring(url.lastIndexOf("/") + 1));
        Log.d(TAG, String.format("File was downloaded and saved at %s.", cacheFile.getAbsolutePath()));

        try {
            URL fileURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) fileURL.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, String.format("Failed to download file from %s, response code: %d.", url, connection.getResponseCode()));
                return null;
            }

            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(cacheFile);
            byte buffer[] = new byte[4096];
            int dataSize;
            while ((dataSize = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, dataSize);
            }
            outputStream.close();
            return cacheFile;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
