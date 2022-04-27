package com.example.tools.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionUtils {

    /**
     * 判断是否有某个权限
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(Context context,String permission){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(context.checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    /**
     * 请求权限
     * @param activity
     * @param permissions
     * @param requestCode
     */
    public static void requestPermissions(Activity activity, String[] permissions,int requestCode) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    /**
     * 获取被拒绝的权限
     * @param context
     * @param permissions
     * @return
     */
    public static String[] getDeniedPermissions(Context context,String[] permissions){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            ArrayList<String> deniedPermissionList = new ArrayList<>();
            for (String permission:permissions){
                if(context.checkSelfPermission(permission)!=PackageManager.PERMISSION_GRANTED){
                    deniedPermissionList.add(permission);
                }
            }
            int size = deniedPermissionList.size();
            if(size>0){
                return deniedPermissionList.toArray(new String[size]);
            }
        }
        return null;
    }


}
