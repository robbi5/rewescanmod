package com.rewescanmod

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.rewescanmod.utils.HookManager
import com.rewescanmod.utils.Logger
import dalvik.system.DexClassLoader
import kotlin.system.measureTimeMillis

const val TAG = "reweScanMod"

@SuppressLint("StaticFieldLeak")
object ReweScanMod {
    lateinit var context: Context
        private set
    lateinit var classLoader: ClassLoader
        private set
    lateinit var logger: Logger
        private set
    lateinit var hookManager: HookManager

    var currentActivity: Activity? = null
        private set


    @SuppressLint("StaticFieldLeak")
    fun init(modulePath: String, application: Application) {
        Log.d(
            TAG,
            "Initializing reweScanMod with module path: $modulePath, application: $application"
        )

        context = application // do not use .applicationContext as it's null at this point
        classLoader =
            DexClassLoader(modulePath, context.cacheDir.absolutePath, null, context.classLoader)
        logger = Logger(context.filesDir.absolutePath + "/rewescanmod.log")
        hookManager = HookManager()

        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                logger.log("Resuming activity: ${activity.javaClass.name}")
                hookManager.onActivityResumed(activity)
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                logger.log("Pausing activity: ${activity.javaClass.name}")
                hookManager.onActivityPaused(activity)
                if (currentActivity == activity) {
                    currentActivity = null
                }
            }

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })

        try {
            val initTime = measureTimeMillis { init() }
            logger.log("Initialization completed in $initTime ms.")
        } catch (e: Exception) {
            logger.log("Failed to initialize: ${e.message}")
            showToast(Toast.LENGTH_LONG, "Failed to initialize: ${e.message}")
        }
    }

    private fun init() {
        logger.log("Initializing reweScanMod...")
        hookManager.init()
    }

    fun runOnMainThread(block: Runnable) {
        Handler(context.mainLooper).post(block)
    }

    fun runOnMainThreadWithCurrentActivity(block: (Activity) -> Unit) {
        runOnMainThread {
            currentActivity?.let { activity ->
                block(activity)
            }
        }
    }

    fun showToast(duration: Int, message: String) {
        runOnMainThread {
            Toast.makeText(context, message, duration).show()
        }
    }

    fun loadClass(name: String): Class<*> {
        return classLoader.loadClass(name)
    }
}