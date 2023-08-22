package com.github.tvbox.osc.data;

import android.os.Bundle;

import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.ui.activity.HomeActivity;
import com.github.tvbox.osc.util.AppManager;
import com.github.tvbox.osc.util.RemoteConfigName;
import com.orhanobut.hawk.Hawk;

public class CustomData {
    private static CustomData instance;
    public static CustomData getInstance(){
        if (instance==null)
            instance = new CustomData();
        return instance;
    }

    // region APP模式类型
    public static final String APP_MODEL_TYPE = "APP_MODEL_TYPE"; // APP模式
    public enum AppModelType {
        YOUND, // 默认/青年版
        AGED // 老年版
    }
    public void SetAppModelType(AppModelType appModelType){
        Hawk.put(APP_MODEL_TYPE, appModelType);
        if (AppManager.getInstance().isActivity()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("useCache", true);
            BaseActivity currActivity = (BaseActivity) AppManager.getInstance().currentActivity();
            currActivity.jumpActivity(HomeActivity.class, bundle);
        }
    }
    public AppModelType GetCurrAppModelType(){
        return Hawk.get(APP_MODEL_TYPE, AppModelType.YOUND);
    }
    public String GetCurrAppModelTypeJsonName(){
        if (GetCurrAppModelType()== AppModelType.YOUND){
            return RemoteConfigName.CustomData_YOUND;
        }else if (GetCurrAppModelType()== AppModelType.AGED) {
            return RemoteConfigName.CustomData_AGED;
        }
        return RemoteConfigName.CustomData_YOUND;
    }
    // endregion
}