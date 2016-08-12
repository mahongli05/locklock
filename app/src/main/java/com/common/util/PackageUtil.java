package com.common.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by MHL on 2016/7/9.
 */
public class PackageUtil {

    private static final String TAG = "PackageUtil";

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "1.0";
    }

    public static String getSchemePart(Intent intent) {
        Uri uri = intent.getData();
        if(uri != null) {
            return uri.getSchemeSpecificPart();
        }
        return null;
    }

    /**
     * 判断是否有用权限
     *
     * @param context 上下文参数
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkUsageStatsPermission(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    public static void startActivity(Context context, Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            LogHelper.d("TAG", "startActivity", e);
        }
    }


    public static boolean isReadPhoneStateAllowed(Context context) {
        return PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean isFloatWindowAllowed(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int ops = getAppOps(context, "OP_SYSTEM_ALERT_WINDOW");
            if (ops == AppOpsManager.MODE_ALLOWED) {
                return true;
            } else if (ops == AppOpsManager.MODE_DEFAULT) {
                int perm = ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
                return perm == PackageManager.PERMISSION_GRANTED;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 判断 悬浮窗口权限是否打开
     *
     * @param context
     * @return true 允许  false禁止
     */
    public static int getAppOps(Context context, String filed) {
        try {
            Object object = context.getSystemService("appops");
            if (object == null) {
                return AppOpsManager.MODE_ERRORED;
            }

            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return AppOpsManager.MODE_ERRORED;
            }

            Class manager = Class.forName("android.app.AppOpsManager");
            Field field = manager.getDeclaredField(filed);
            field.setAccessible(true);
            int OP_SYSTEM_ALERT_WINDOW = field.getInt(null);

            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(OP_SYSTEM_ALERT_WINDOW);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m;
        } catch (Exception ex) {

        }
        return AppOpsManager.MODE_ERRORED;
    }
}
