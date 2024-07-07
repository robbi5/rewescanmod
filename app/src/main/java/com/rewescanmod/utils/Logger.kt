package com.rewescanmod.utils

import android.util.Log
import de.robv.android.xposed.XposedBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class Logger(logFile: String) {
    private val LOG_FILE = File(logFile)
    private val LOG_TAG = "reweScanMod"
    private val MAX_LOG_SIZE = 1024 * 1024 * 5 // 5 MB

    private val logFlow = MutableSharedFlow<String>(
        extraBufferCapacity = Int.MAX_VALUE
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                LOG_FILE.createNewFile()
                logFlow.collect { msg ->
                    try {
                        if (checkAndManageSize()) {
                            LOG_FILE.appendText("$msg\n")
                        }
                    } catch (e: Exception) {
                        Log.wtf(LOG_TAG, "Failed to log message: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.wtf(LOG_TAG, "Failed to create log file ($logFile): ${e.message}")
            }
        }
    }

    fun log(msg: String) {
        XposedBridge.log("$LOG_TAG: $msg")
        logFlow.tryEmit(msg)
    }

    private fun checkAndManageSize(): Boolean {
        if (LOG_FILE.length() > MAX_LOG_SIZE) {
            manageLogOverflow()
            return false
        }
        return true
    }

    private fun manageLogOverflow() {
        LOG_FILE.delete()
        LOG_FILE.createNewFile()
        XposedBridge.log("$LOG_TAG: Log file overflowed")
    }
}