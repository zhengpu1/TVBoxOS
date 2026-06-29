package com.github.tvbox.osc.util;

import static android.content.Context.MODE_PRIVATE;
import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.github.tvbox.osc.base.baseActivity;
import com.github.tvbox.osc.ui.dialog.UpdateDialog;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.orhanobut.hawk.Hawk;

import okhttp3.Response;


/*
 * 远程文件配置
 */
public class RemoteConfig {
    private static String remoteUrl = "";
    private static JsonObject remoteJsonObject;
    public static boolean isRemoteConfigOk;
    private static Context mContext;
    private static String CurrVersions="1.0.0000.00.00";
    private static String NewVersions="1.0.0000.00.00";
    public static void Init(Context mContext) {
        RemoteConfig.mContext = mContext;
        String remoteUrl = "http://192.168.80.10:38/tv/version.php";
        LOG.i("RemoteConfig",
                ToolUtils.isApkInDebug(mContext) ? "当前处于【调试】模式" : "当前处于【正式】模式",
                "远程配置地址", remoteUrl);
        isRemoteConfigOk = false;
        try {
            Response response = OkGo.<String>get(remoteUrl).execute();
            String result = response.body().string();
            // 先标记配置拉取成功
            isRemoteConfigOk = true;
            //remoteJsonObject = JsonParser.parseString(result).getAsJsonObject();
            InitRemoteConfig(result);
        } catch (Exception e) {
            LOG.e("RemoteConfig", "拉取配置异常：" + e.getMessage(), e);
            isRemoteConfigOk = false;
            remoteJsonObject = null;
        }
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
    public static void jumpActivity(Class<? extends baseActivity> clazz) {
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
        try {
            remoteJsonObject = JsonParser.parseString(config).getAsJsonObject();
            Hawk.put(HawkConfig.VIDEO_PURIFY, remoteJsonObject.has("Removeads") && Boolean.parseBoolean(checkUpdateA("Removeads")));
            Hawk.put(HawkConfig.EPISODE_GROUPING, remoteJsonObject.has("Grouping") && Boolean.parseBoolean(checkUpdateA("Grouping")));
            if (remoteJsonObject.has("Grouping")) {
                boolean Grouping = Boolean.parseBoolean(checkUpdateA("Grouping"));
                Hawk.put(HawkConfig.EPISODE_GROUPING, Grouping);
            }
            // 只调用一次，存到变量里，避免多次重复调用
            JsonElement updateDataElement = GetValue("UpdateData");
            if (!updateDataElement.isJsonNull() && updateDataElement.isJsonObject()) {
                JsonObject updateData = updateDataElement.getAsJsonObject();

                JsonElement forceUpdateElement = GetValue("IsForceUpdate");
                boolean forceUpdate = false;
                if (!forceUpdateElement.isJsonNull() && forceUpdateElement.isJsonPrimitive()) {
                    forceUpdate = forceUpdateElement.getAsBoolean();
                }

                if (forceUpdate){
                    UpdateDialog.checkUpdate(AppManager.getInstance().currentActivity(), false);
                }else {
                    LOG.i("RemoteConfig", "★远端设置", "启动非强制显示更新");
                }
            }
        } catch (Exception e) {
            LOG.e("RemoteConfig", "InitRemoteConfig异常", e);
        }
    }
}
