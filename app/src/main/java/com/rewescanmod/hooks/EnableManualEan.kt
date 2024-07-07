package com.rewescanmod.hooks

import android.annotation.SuppressLint
import com.rewescanmod.utils.Hook
import com.rewescanmod.utils.HookStage
import com.rewescanmod.utils.hook

class EnableManualEan : Hook(
    "Enable Manual Ean Input"
) {
    private val mscoSettings = "com.rewe.digital.msco.core.support.preference.MscoSettings"

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun init() {
        val mscoSettingsClass = findClass(mscoSettings)
        mscoSettingsClass.hook(
            "getAllowManualEanInput", HookStage.BEFORE
        ) { param ->
            param.result = true
        }
        mscoSettingsClass.hook(
            "setAllowManualEanInput", HookStage.BEFORE
        ) { param ->
            param.args[0] = true
        }
    }
}