package com.github.tvbox.osc.startup

import android.content.Context
import com.github.tvbox.osc.util.RemoteConfig
import com.rousetime.android_startup.AndroidStartup
import com.rousetime.android_startup.executor.ExecutorManager
import java.util.concurrent.Executor

class RemoteConfigTask : AndroidStartup<Boolean>() {
    override fun callCreateOnMainThread(): Boolean = false
    override fun create(context: Context): Boolean {
        RemoteConfig.Init(context)
        return RemoteConfig.isRemoteConfigOk
    }
    override fun waitOnMainThread() = false
    override fun createExecutor(): Executor {
        return ExecutorManager.instance.ioExecutor
    }
}