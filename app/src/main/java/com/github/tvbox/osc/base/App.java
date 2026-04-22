package com.github.tvbox.osc.base;

import androidx.multidex.MultiDexApplication;

import com.codelang.window.FloatingWindowManager;
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.startup.DatabaseTask;
import com.github.tvbox.osc.startup.HawkTask;
import com.github.tvbox.osc.startup.PlayerTask;
import com.github.tvbox.osc.startup.ServerTask;
import com.github.tvbox.osc.startup.UITask;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.RemoteConfig;
import com.github.tvbox.osc.util.js.JSEngine;
import com.p2p.P2PClass;
import com.rousetime.android_startup.StartupManager;

public class App extends MultiDexApplication {
    private P2PClass p;
    private String dashData;
    private boolean pyLoadSuccess = false;
    private VodInfo vodInfo;

    public static String burl;
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        RemoteConfig.Init(this);
        instance = this;
        FloatingWindowManager.init(this);
        ControlManager.init(this);
        FileUtils.cleanPlayerCache();
        JSEngine.getInstance().create();
        RemoteConfig.isFirstdlde(this);

        new StartupManager.Builder()
                .addStartup(new UITask())
                .addStartup(new HawkTask())
                .addStartup(new DatabaseTask())
                .addStartup(new ServerTask())
                .addStartup(new PlayerTask())
                .build(this)
                .start()
                .await();
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JSEngine.getInstance().destroy();
    }

    public void setPyLoadSuccess(boolean success) {
        this.pyLoadSuccess = success;
    }

    public boolean getPyLoadSuccess() {
        return pyLoadSuccess;
    }

    public void setVodInfo(VodInfo vodinfo) {
        this.vodInfo = vodinfo;
    }

    public VodInfo getVodInfo() {
        return vodInfo;
    }

    public P2PClass getp2p() {
        try {
            if (p == null) {
                p = new P2PClass(instance.getExternalCacheDir().getAbsolutePath());
            }
            return p;
        } catch (Exception e) {
            LOG.e(e.toString());
            return null;
        }
    }

    public void setDashData(String data) {
        this.dashData = data;
    }

    public String getDashData() {
        return dashData;
    }
}