package com.github.tvbox.osc.util;

import static android.content.Context.MODE_PRIVATE;
import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.ui.activity.LivePlayActivity;
import com.github.tvbox.osc.ui.dialog.UpdateDialog;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;


/*
 * 远程文件配置
 */
public class RemoteConfig {
    private static String remoteUrl = "";
    private static JsonObject remoteJsonObject;
    private static boolean isRemoteConfigOk;
    private static Context mContext;
    private static String CurrVersions="1.0.0000.00.00";
    private static String NewVersions="1.0.0000.00.00";
    public static void Init(Context mContext){
        RemoteConfig.mContext = mContext;
        remoteUrl = "http://www.hrystv.top:38/tv/version.php";
        LOG.i("RemoteConfig",
                ToolUtils.isApkInDebug(mContext) ? "当前处于【调试】模式":"当前处于【正式】模式",
                "远程配置地址", remoteUrl);
        isRemoteConfigOk = false;
        OkGo.<String>get(remoteUrl).execute(new AbsCallback<String>() {
            @Override
            public String convertResponse(okhttp3.Response response) throws Throwable {
                return response.body().string();
            }
            @Override
            public void onSuccess(Response<String> response) {
                isRemoteConfigOk = true;
                InitRemoteConfig(response.body());
            }
            @Override
            public void onError(Response<String> response){
                LOG.e(this.getClass().getName(), "初始化地址拉取失败：",remoteUrl, "错误详情：",  response.getException() != null ? response.getException().getMessage() : "");
            }
        });

    }


    public static boolean IsOk(){
        return isRemoteConfigOk;
    }
    public static JsonElement GetValue(String... keys){
        if (IsOk() && remoteJsonObject != null) {
            JsonObject jsonObject = remoteJsonObject;
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                if (i == keys.length - 1)
                    return jsonObject.get(key);
                else
                    jsonObject = jsonObject.get(key).getAsJsonObject();
            }
        }
        return JsonNull.INSTANCE;
    }

    public static String checkUpdateA(String jsonjx) {
        String updateJosn;
        try {
            updateJosn = GetValue(jsonjx).getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return updateJosn;
    }
    public static boolean checkUpdatess(Context context) {
        JsonObject updateJosn = null;
        try {
            updateJosn = RemoteConfig.GetValue("UpdateData").getAsJsonObject();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        try {
        NewVersions = updateJosn.get("NewVersion").getAsString();
        CurrVersions = DefaultConfig.getAppVersionName(context);
        int NewVersionNum = Integer.parseInt(NewVersions.substring(7, 14));
        int CurrVersionNum = Integer.parseInt(CurrVersions.substring(7, 14));
        if (NewVersionNum > CurrVersionNum) {
            return true;
        }
        }catch (Throwable th){
            th.printStackTrace();
        };
        return false;
    }
    public static void jumpActivity(Class<? extends BaseActivity> clazz) {
        Intent intent = new Intent(mContext, clazz);
        startActivity(intent);
    }
    public static boolean isFirstStart(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {// 第一次
            editor.putBoolean("isFirstRun", false);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }
    public static void isFirstdlde(Context context) {
        SharedPreferences sp = context.getSharedPreferences("share", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    private static void InitRemoteConfig(String config){
        LOG.i("RemoteConfig", "获取到远端内容", config);
        remoteJsonObject = JsonParser.parseString(config).getAsJsonObject();
        if (Hawk.get(HawkConfig.HOME_LIVE, false)) {
            new Handler().postDelayed(() -> {
                jumpActivity(LivePlayActivity.class);
            }, 100);
        }
        // region 默认更新地址
        if (!GetValue("UpdateData").isJsonNull() && GetValue("UpdateData").getAsJsonObject() != null) {
            JsonObject updateData = GetValue("UpdateData").getAsJsonObject();
            LOG.i("RemoteConfig", "★远端设置", "默认更新数据", updateData.toString());
            if (!GetValue("IsForceUpdate").isJsonNull() && GetValue("IsForceUpdate").getAsBoolean()){
                LOG.i("RemoteConfig", "★远端设置", "启动强制显示更新");
                UpdateDialog.checkUpdate(AppManager.getInstance().currentActivity(), false);
            }else {
                LOG.i("RemoteConfig", "★远端设置", "启动非强制显示更新");
            }
        }
    }

}
