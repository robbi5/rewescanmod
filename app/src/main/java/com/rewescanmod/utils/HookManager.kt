package com.rewescanmod.utils

import android.app.Activity
import com.rewescanmod.ReweScanMod
import com.rewescanmod.hooks.EnableManualEan
import com.rewescanmod.hooks.InjectScanBroadcast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class HookManager {
    private var hooks = mutableListOf<Hook>()

    private fun registerAndInitHooks() {
        runBlocking(Dispatchers.IO) {
            hooks = mutableListOf(
                EnableManualEan(),
                InjectScanBroadcast(),
            )

            hooks.forEach { hook ->
                hook.init()
                ReweScanMod.logger.log("Initialized hook: ${hook.hookName}")
            }
        }
    }

    fun onActivityResumed(activity: Activity) {
        hooks.forEach { hook -> hook.activityResumed(activity) }
    }

    fun onActivityPaused(activity: Activity) {
        hooks.forEach { hook -> hook.activityPaused(activity) }
    }

    fun init() {
        registerAndInitHooks()
    }
}