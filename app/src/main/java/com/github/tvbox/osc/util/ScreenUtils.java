package com.github.tvbox.osc.util;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtils {

    // 获取屏幕英寸（你原来的代码没问题，可以保留）
    public static double getSqrt(Activity activity) {
        WindowManager wm = activity.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches;
    }

    /**
     * 核心修复：真正准确的 TV 判断
     */
    public static boolean isTv(Context context) {
        // 方法1：Android TV 官方判断（最准）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (context.getPackageManager().hasSystemFeature("android.software.leanback")) {
                return true;
            }
        }

        // 方法2：系统 UI 模式为电视
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true;
        }

        // 方法3：旧设备 / 盒子 / 非手机 + 大屏幕 = 电视
        boolean isNotPhone = !isPhone(context);
        boolean isBigScreen = isLargeScreen(context);
        return isNotPhone && isBigScreen;
    }

    /**
     * 判断是不是手机（有通话功能）
     */
    private static boolean isPhone(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否大屏幕（电视/平板）
     */
    private static boolean isLargeScreen(Context context) {
        int size = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return size == Configuration.SCREENLAYOUT_SIZE_XLARGE || size == Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}