package com.github.tvbox.osc.base

import androidx.multidex.MultiDexApplication
import com.codelang.window.FloatingWindowManager.init
import com.github.tvbox.osc.bean.VodInfo
import com.github.tvbox.osc.server.ControlManager
import com.github.tvbox.osc.startup.DatabaseTask
import com.github.tvbox.osc.startup.HawkTask
import com.github.tvbox.osc.startup.PlayerTask
import com.github.tvbox.osc.startup.ServerTask
import com.github.tvbox.osc.startup.UITask
import com.github.tvbox.osc.util.FileUtils
import com.github.tvbox.osc.util.LOG
import com.github.tvbox.osc.util.RemoteConfig
import com.github.tvbox.osc.util.RemoteConfig.isFirstdlde
import com.github.tvbox.osc.util.js.JSEngine
import com.p2p.P2PClass
import com.rousetime.android_startup.StartupManager

/**
 * @author pj567
 * @date :2020/12/17
 * @description:
 */
class Appa : MultiDexApplication() {
    private var p: P2PClass? = null
    private var dashData: String? = null
    override fun onCreate() {
        super.onCreate()
        RemoteConfig.Init(this)
        instance = this
        init(this)
        ControlManager.init(this)
        FileUtils.cleanPlayerCache()
        JSEngine.getInstance().create()
        isFirstdlde(this)
        StartupManager.Builder()
            .addStartup(UITask())
            .addStartup(HawkTask())
            .addStartup(DatabaseTask())
            .addStartup(ServerTask())
            .addStartup(PlayerTask())
            .build(this)
            .start().await()
    }

    companion object {
        lateinit var burl: String
        @JvmStatic
        var instance: Appa? = null
            private set
    }

    override fun onTerminate() {
        super.onTerminate()
        JSEngine.getInstance().destroy()
    }

    private var pyLoadSuccess = false

    fun setPyLoadSuccess(success: Boolean) {
        pyLoadSuccess = success
    }

    fun getPyLoadSuccess(): Boolean {
        return pyLoadSuccess
    }

    private var vodInfo: VodInfo? = null
    fun setVodInfo(vodinfo: VodInfo?) {
        vodInfo = vodinfo
    }
    fun getp2p(): P2PClass? {
        return try {
            if (p == null) {
                p = P2PClass(instance!!.externalCacheDir!!.absolutePath)
            }
            p
        } catch (e: Exception) {
            LOG.e(e.toString())
            null
        }
    }

    fun getVodInfo(): VodInfo? {
        return vodInfo
    }
    fun setDashData(data: String?) {
        dashData = data
    }

    fun getDashData(): String? {
        return dashData
    }
}
