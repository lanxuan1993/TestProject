package com.example.tools.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;

public class AppManager {
    public static Stack<WeakReference<Activity>> getActivityStack() {
        return activityStack;
    }

    private static Stack<WeakReference<Activity>> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        activityStack.add(activityWeakReference);
    }

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    public Activity getTopActivity() {
        return activityStack.lastElement().get();
    }



    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    public void finishTopActivity() {
        Activity activity = activityStack.lastElement().get();
        finishActivity(activity);
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        Iterator iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            WeakReference<Activity> activity = (WeakReference<Activity>) iterator.next();
            if (activity != null && activity.get() != null && activity.get().getClass().equals(cls)) {
                iterator.remove();
                activity.get().finish();
            }
        }
    }

    public void finishOtherActivity(Class<?> cls) {
        Iterator iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            WeakReference<Activity> activity = (WeakReference<Activity>) iterator.next();
            if (activity != null && activity.get() != null && !activity.get().getClass().equals(cls)) {
                iterator.remove();
                activity.get().finish();
            }
        }
    }

    /**
     * 结束所有Activity
     */
    @SuppressWarnings("WeakerAccess")
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                if (activityStack.get(i).get() != null) {
                    activityStack.get(i).get().finish();
                }
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            finishAllActivity();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());

        } catch (Exception e) {
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            for (WeakReference<Activity> activityWeakReference : activityStack) {
                if (activity.equals(activityWeakReference.get())) {
                    activityStack.remove(activityWeakReference);
                    break;
                }
            }
            activity.finish();
            activity = null;
        }
    }

    /**
     * 得到指定类名的Activity
     */
    public Activity getActivity(Class<?> cls) {
        for (WeakReference<Activity> activity : activityStack) {
            if (activity != null && activity.get() != null && activity.get().getClass().equals(cls)) {
                return activity.get();
            }
        }
        return null;
    }


}
