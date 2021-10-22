package com.example.mine.permission;

import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions3.Permission;
import com.tbruyelle.rxpermissions3.RxPermissions;

import io.reactivex.rxjava3.functions.Consumer;

public class RxPermissionUtils {
    private static int total = 0;

    private static RxPermissionUtils sInstance = null;


    public static RxPermissionUtils getInstance() {
        if (sInstance == null) {
            synchronized (RxPermissionUtils.class) {
                if (sInstance == null) {
                    sInstance = new RxPermissionUtils();
                }
            }
        }
        return sInstance;
    }


    private RxPermissionUtils() {
    }

    /**
     * 请求权限
     * @param activity
     * @param permissions
     */
    public static void requestPermissions(FragmentActivity activity, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(permissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Throwable {
                        if(aBoolean){//所有权限都通过了

                        }else {//非所有权限通过,下次只请求未通过的权限

                        }
                    }
                });
    }


    /**
     * 返回每个权限的请求详情
     * @param activity
     * @param permissions
     */
    public static void requestEachPermissions(FragmentActivity activity, String... permissions) {
        total = 0;
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Throwable {
                        if (permission.granted) {// 用户同意该权限

                        } else if (permission.shouldShowRequestPermissionRationale) {//禁止，但没有选择“以后不再询问”，以后申请权限，会继续弹出提示

                        } else {// 用户拒绝了该权限，而且选中『不再询问』

                        }
                        total++;
                        if (permissions.length == total) {

                        }
                    }
                });
    }
}
