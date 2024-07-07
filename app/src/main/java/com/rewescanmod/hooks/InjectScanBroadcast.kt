package com.rewescanmod.hooks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.rewescanmod.ReweScanMod
import com.rewescanmod.utils.Hook
import com.rewescanmod.utils.HookStage
import com.rewescanmod.utils.hookConstructor
import de.robv.android.xposed.XposedHelpers


class InjectScanBroadcast : Hook(
    "Inject Scan Broadcast"
) {
    private val scannedCode = "com.rewe.digital.msco.util.scanner.ScannedCode"
    private val scannedCodeType = "com.rewe.digital.msco.util.scanner.ScannedCodeType"
    private val scanController = "com.rewe.digital.msco.core.scanning.ScanController"
    private val scanAndGoController = "com.rewe.digital.msco.core.main.ScanAndGoController"
    private val scanView = "com.rewe.digital.msco.util.scanner.ScanView"
    private val scannedCodeCorners = "com.rewe.digital.msco.util.scanner.ScannedCodeCorners"
    private val decoderScanInfo = "com.rewe.digital.msco.util.scanner.DecoderScanInfo"

    var receiver: BroadcastReceiver? = null

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun init() {
        val scannedCodeTypeClass = findClass(scannedCodeType)
        val TYPE_EAN_13 = XposedHelpers.getStaticObjectField(scannedCodeTypeClass,"EAN_13")
        val TYPE_EAN_8 = XposedHelpers.getStaticObjectField(scannedCodeTypeClass,"EAN_8")

        // First try: using ScanController.onDetect
        /*
        val scannedCodeClass = findClass(scannedCode)
        val scannedCodeCornersClass = findClass(scannedCodeCorners)
        val decoderScanInfoClass = findClass(decoderScanInfo)


        val scanControllerClass = findClass(scanController)
        scanControllerClass.hookConstructor(HookStage.AFTER) { param ->
            val activity : Activity = param.arg(0)
            val thisScanController = param.thisObject

            val filter = IntentFilter()
            filter.addAction("com.rewescanmod.SCAN")

            // am broadcast -a com.rewescanmod.SCAN --es com.symbol.datawedge.data_string "4388844147485"
            val receiver = object: BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    ReweScanMod.logger.log("got intent!")
                    val scanData : String? = intent!!.getStringExtra("com.symbol.datawedge.data_string")
                    ReweScanMod.logger.log(scanData!!)

                    val myScannedCode = XposedHelpers.newInstance(scannedCodeClass, scanData, TYPE_EAN_13)
                    ReweScanMod.logger.log("built scannedCode data object")
                    XposedHelpers.callMethod(thisScanController, "onDetect", myScannedCode)
                    ReweScanMod.logger.log("called onDetect")
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activity.applicationContext.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                activity.applicationContext.registerReceiver(receiver, filter)
            }
            ReweScanMod.logger.log("registered broadcastreceiver")
        }
        */

        // second try: using ScanView.onScan
        /*
        var receiver: BroadcastReceiver? = null

        val scanViewClass = findClass(scanView)
        scanViewClass.hook("initScanProvider", HookStage.AFTER) { param ->
            val thisScanView : View = param.thisObject as View

            if (receiver != null) {
                // deregister prev
                try {
                    thisScanView.context.applicationContext.unregisterReceiver(receiver)
                } catch (_: IllegalArgumentException) {
                    // thats okay, its already gone
                }
            }

            val filter = IntentFilter()
            filter.addAction("com.rewescanmod.SCAN")

            // am broadcast -a com.rewescanmod.SCAN --es com.symbol.datawedge.data_string "4388844147485"
            receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    ReweScanMod.logger.log("got intent!")
                    val scanData : String? = intent!!.getStringExtra("com.symbol.datawedge.data_string")
                    ReweScanMod.logger.log(scanData!!)

                    var myCorners : Any?
                    try {
                        val binding = XposedHelpers.getObjectField(thisScanView, "binding")
                        val lsvBarcodeFrame = XposedHelpers.getObjectField(binding, "lsvBarcodeFrame")
                        val left: Int = XposedHelpers.callMethod(lsvBarcodeFrame, "getLeft") as Int
                        val top: Int = XposedHelpers.callMethod(lsvBarcodeFrame, "getTop") as Int
                        val right: Int = XposedHelpers.callMethod(lsvBarcodeFrame, "getRight") as Int
                        val bottom: Int = XposedHelpers.callMethod(lsvBarcodeFrame, "getBottom") as Int

                        //tl_x, tl_y, tr_x, tr_y, br_x, br_y, bl_x, bl_y
                        myCorners = XposedHelpers.newInstance(scannedCodeCornersClass,
                            left.toFloat() + 1.0F, top.toFloat() - 1.0F,
                            right.toFloat() - 1.0F, top.toFloat() - 1.0F,
                            right.toFloat() - 1.0F, bottom.toFloat() - 1.0F,
                            left.toFloat() + 1.0F, bottom.toFloat() - 1.0F)
                        ReweScanMod.logger.log("got corners!!")
                    } catch (_ : NoSuchMethodError) {
                        myCorners = XposedHelpers.newInstance(scannedCodeCornersClass, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                        ReweScanMod.logger.log("corners failed, NSME")
                    } catch (_ : Exception) {
                        myCorners = XposedHelpers.newInstance(scannedCodeCornersClass, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                        ReweScanMod.logger.log("corners failed, Ex")
                    }

                    // String value, ScannedCodeCorners corners, ScannedCodeType type
                    XposedHelpers.callMethod(thisScanView, "onScan", scanData, myCorners!!, TYPE_EAN_13)
                    ReweScanMod.logger.log("called onScan")

                    //val myScannedCode = XposedHelpers.newInstance(scannedCodeClass, scanData, TYPE_EAN_13)
                    //val myDecoderScanInfo = XposedHelpers.newInstance(decoderScanInfoClass, myScannedCode, myCorners, System.currentTimeMillis())

                    //val stateMachine = XposedHelpers.callMethod(thisScanView, "getStateMachine")
                    //XposedHelpers.callMethod(stateMachine, "enterPulsingState", myDecoderScanInfo)
                    //ReweScanMod.logger.log("called enterPulsingState")
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                thisScanView.context.applicationContext.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                thisScanView.context.applicationContext.registerReceiver(receiver, filter)
            }
            ReweScanMod.logger.log("registered broadcastreceiver")
        }

        scanViewClass.hook("stopScanning", HookStage.BEFORE) { param ->
            val thisScanView : View = param.thisObject as View
            try {
                if (receiver != null) {
                    thisScanView.context.applicationContext.unregisterReceiver(receiver)
                    ReweScanMod.logger.log("unregistered broadcastreceiver")
                }
            } catch (_: Exception) {}
        }
        */

        // third try: using ScanAndGoController.showManualEanInput / ManualEanInputDialog.submit
        val scanAndGoControllerClass = findClass(scanAndGoController)
        scanAndGoControllerClass.hookConstructor(HookStage.AFTER) { param ->
            val activity : Activity = param.arg(0)
            val thisScanAndGoController = param.thisObject

            val filter = IntentFilter()
            filter.addAction("com.rewescanmod.SCAN")

            // am broadcast -a com.rewescanmod.SCAN --es com.symbol.datawedge.data_string "4388844147485"
            receiver = object: BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    ReweScanMod.logger.log("got intent!")
                    val scanData : String? = intent!!.getStringExtra("com.symbol.datawedge.data_string")
                    ReweScanMod.logger.log(scanData!!)

                    XposedHelpers.callMethod(thisScanAndGoController, "showManualEanInput")
                    ReweScanMod.logger.log("called showManualEanInput")

                    val manualEanInputDialog = XposedHelpers.getObjectField(thisScanAndGoController, "manualEanInputDialog")
                    XposedHelpers.callMethod(manualEanInputDialog, "submit", scanData)
                    ReweScanMod.logger.log("called submit")
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activity.applicationContext.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                activity.applicationContext.registerReceiver(receiver, filter)
            }
            ReweScanMod.logger.log("registered broadcast receiver")
        }
    }

    override fun activityPaused(activity: Activity) {
        try {
            if (receiver != null) {
                activity.applicationContext.unregisterReceiver(receiver)
                ReweScanMod.logger.log("unregistered broadcast receiver")
            }
        } catch (_: Exception) {}
    }
}