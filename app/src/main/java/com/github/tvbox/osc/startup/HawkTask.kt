package com.github.tvbox.osc.startup

import android.content.Context
import android.text.TextUtils
import com.github.tvbox.osc.util.HawkConfig
import com.github.tvbox.osc.util.RemoteConfig
import com.orhanobut.hawk.Hawk
import com.rousetime.android_startup.AndroidStartup
import com.rousetime.android_startup.executor.ExecutorManager
import java.util.concurrent.Executor

class HawkTask : AndroidStartup<String>() {
    override fun callCreateOnMainThread(): Boolean = false

    override fun create(context: Context): String? {
        initParams(context)
        return HawkTask::class.simpleName

    }

    override fun waitOnMainThread() = true
    override fun createExecutor(): Executor {
        return ExecutorManager.instance.cpuExecutor
    }

    private fun initParams(context: Context) {
        // 设定默认参数
        Hawk.init(context).build()
        //0 系统 1 ijk 2 exo 10 MXPlayer
        if (!Hawk.contains(HawkConfig.PLAY_TYPE)) {
            Hawk.put(HawkConfig.PLAY_TYPE, 1)
        }
        //沉浸式开关
        if (!Hawk.contains(HawkConfig.IMMERSIVE_SWITCH)) {
            Hawk.put(HawkConfig.IMMERSIVE_SWITCH, true)
        }
        //去广告开关
        if (!Hawk.contains(HawkConfig.VIDEO_PURIFY)) {
            val remoteStr = RemoteConfig.checkUpdateA("Removeads")
            val removeAds = "true".equals(remoteStr, ignoreCase = true)
            Hawk.put(HawkConfig.VIDEO_PURIFY, removeAds)
        }
        //调试模式开关
        if (!Hawk.contains(HawkConfig.DEBUG_OPEN)) {
            Hawk.put(HawkConfig.DEBUG_OPEN, true)
        }
        //数据源显示
        if (!Hawk.contains(HawkConfig.HOME_SHOW_SOURCE)) {
            Hawk.put(HawkConfig.HOME_SHOW_SOURCE, true)
        }
        // 0 豆瓣热播 1 数据源推荐 2 历史
        if (!Hawk.contains(HawkConfig.HOME_REC)) {
            Hawk.put(HawkConfig.HOME_REC, 1)
        }
        // 0 列表 1 缩略图
        if (!Hawk.contains(HawkConfig.SEARCH_VIEW)) {
            Hawk.put(HawkConfig.SEARCH_VIEW, 0)
        }
        //略缩图大小
        if (!Hawk.contains(HawkConfig.HOME_LSTIMG)) {
            Hawk.put(HawkConfig.HOME_LSTIMG, 2)
        }
        //自动跳过片头片尾开关
        if (!Hawk.contains(HawkConfig.HOME_PTST_TUMM)) {
            Hawk.put(HawkConfig.HOME_PTST_TUMM, true)
        }
        //剧集分组
        if (!Hawk.contains(HawkConfig.EPISODE_GROUPING)) {
            val remoteStr = RemoteConfig.checkUpdateA("Grouping")
            val removeAds = "true".equals(remoteStr, ignoreCase = true)
            Hawk.put(HawkConfig.EPISODE_GROUPING, removeAds)
        }
        //自动换源
        if (!Hawk.contains(HawkConfig.FORBID_JAR_DIALOG)) {
            val remoteStr = RemoteConfig.checkUpdateA("SwitchSource")
            val removeAds = "true".equals(remoteStr, ignoreCase = true)
            Hawk.put(HawkConfig.FORBID_JAR_DIALOG, removeAds)
        }
        //搜索线程
        if (!Hawk.contains(HawkConfig.XIANC_NUM)) {
            Hawk.put(HawkConfig.XIANC_NUM, 3)
        }

            val homeUrl = Hawk.get(HawkConfig.API_URL, "")
            if (TextUtils.isEmpty(homeUrl)) {
                Hawk.put(HawkConfig.API_URL, RemoteConfig.checkUpdateA("APIUrl"))
            }
        if (Hawk.get<String>(HawkConfig.IJK_CODEC).isNullOrEmpty()) {
            Hawk.put(HawkConfig.IJK_CODEC, "硬解码")
        }
    }
}