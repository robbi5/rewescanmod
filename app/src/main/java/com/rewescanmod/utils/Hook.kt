package com.rewescanmod.utils

import android.app.Activity
import com.rewescanmod.ReweScanMod


abstract class Hook(val hookName: String) {

    open fun init() {}

    open fun cleanup() {}

    open fun activityResumed(activity: Activity) {}

    open fun activityPaused(activity: Activity) {}

    protected fun findClass(name: String): Class<*> {
        return ReweScanMod.loadClass(name)
    }
}