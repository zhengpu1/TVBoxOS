package com.github.tvbox.osc.util;

import static android.content.Context.UI_MODE_SERVICE;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManager;

public class ScreenUtils {

    public static double getSqrt(Activity activity) {
        WindowManager wm = activity.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);// 屏幕尺寸
        return screenInches;
    }
    private static boolean checkScreenLayoutIsTv(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) > Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private static boolean checkIsPhone(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    public static boolean isTv(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION || (checkScreenLayoutIsTv(context) && !checkIsPhone(context));
    }
    public static boolean isTvOrBox(Context context) {
        // SDK > Android 11 直接认为不是 TV / 机顶盒
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            return false;
        }
        // SDK <= Android 9 直接认为是 TV / 机顶盒9999
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            return true;
        }
        PackageManager pm = context.getPackageManager();
        UiModeManager uiMode = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);

        // 1. UiMode 判断
        if (uiMode.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true;
        }

        // 2. Android TV / Leanback 特性判断
        if (pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
                || pm.hasSystemFeature("android.software.leanback_only")   // Strict leanback
                || pm.hasSystemFeature(PackageManager.FEATURE_TELEVISION)
                // Amazon Fire TV
                || pm.hasSystemFeature("amazon.hardware.fire_tv")
                // Google TV (part of Android TV 家族)
                || pm.hasSystemFeature("com.google.android.tv")) {
            return true;
        }

        // 3. 没有触摸屏：大多数机顶盒、电视不带触摸
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
            return true;
        }

        // 4. 物理遥控器 / D‑pad 键存在判断 兼容一些既有触摸也支持遥控的设备
        if (KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_DPAD_UP)
                && KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_DPAD_DOWN)
                && KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_DPAD_LEFT)
                && KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_DPAD_RIGHT)
                && KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_DPAD_CENTER)) {
            return true;
        }

        // 5. 输入设备源中有 DPAD
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int id : deviceIds) {
            InputDevice dev = InputDevice.getDevice(id);
            if (dev == null) continue;
            int sources = dev.getSources();
            if ((sources & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD) {
                return true;
            }
        }

        return false;
    }
}
