package com.rewescanmod.utils

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

enum class HookStage {
    BEFORE,
    AFTER
}

object Hooker {
    inline fun <T> newMethodHook(
        stage: HookStage,
        crossinline consumer: (HookAdapter<T>) -> Unit,
    ): XC_MethodHook {
        return object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (stage == HookStage.BEFORE) {
                    HookAdapter<T>(param).also(consumer)
                }
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                if (stage == HookStage.AFTER) {
                    HookAdapter<T>(param).also(consumer)
                }
            }
        }
    }

    fun <T> hook(
        clazz: Class<T>,
        methodName: String,
        stage: HookStage,
        consumer: (HookAdapter<T>) -> Unit
    ): Set<XC_MethodHook.Unhook> =
        XposedBridge.hookAllMethods(clazz, methodName, newMethodHook(stage, consumer))

    fun <T> hookConstructor(
        clazz: Class<T>,
        stage: HookStage,
        consumer: (HookAdapter<T>) -> Unit
    ): Set<XC_MethodHook.Unhook> =
        XposedBridge.hookAllConstructors(clazz, newMethodHook(stage, consumer))
}

fun <T> Class<T>.hookConstructor(
    stage: HookStage,
    consumer: (HookAdapter<T>) -> Unit
) = Hooker.hookConstructor(this, stage, consumer)

fun <T> Class<T>.hook(
    methodName: String,
    stage: HookStage,
    consumer: (HookAdapter<T>) -> Unit
): Set<XC_MethodHook.Unhook> = Hooker.hook(this, methodName, stage, consumer)
