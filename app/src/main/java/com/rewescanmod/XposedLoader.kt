package com.rewescanmod;

import android.app.Application
import android.widget.Toast
import com.rewescanmod.utils.HookStage
import com.rewescanmod.utils.hook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedLoader : IXposedHookZygoteInit, IXposedHookLoadPackage {
    companion object {
        const val REWE_PACKAGE_NAME = "de.rewe.app.mobile"
    }

    private lateinit var modulePath: String

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != REWE_PACKAGE_NAME) return;

        Application::class.java.hook("attach", HookStage.AFTER) {
            val application = it.thisObject
            val pkgInfo = application.packageManager.getPackageInfo(application.packageName, 0)

            if (pkgInfo.versionName != BuildConfig.TARGET_REWE_VERSION) {
                Toast.makeText(
                    application,
                    "reweScanMod: Rewe version mismatch (installed: ${pkgInfo.versionName}, expected: ${BuildConfig.TARGET_REWE_VERSION}). Mod disabled.",
                    Toast.LENGTH_LONG
                ).show()
                return@hook
            }

            ReweScanMod.init(modulePath, application)
        }
    }
}
