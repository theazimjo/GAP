package com.gap.app.gap_app_mobile

import android.Manifest
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.gap.app/sms"
    private val SMS_PERMISSION_CODE = 101

    private var pendingCall: io.flutter.plugin.common.MethodCall? = null
    private var pendingResult: MethodChannel.Result? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "sendSms") {
                val phone = call.argument<String>("phone")
                val message = call.argument<String>("message")

                if (phone == null || message == null) {
                    result.error("INVALID_ARGS", "Phone and message are required", null)
                    return@setMethodCallHandler
                }

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    pendingCall = call
                    pendingResult = result
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
                    return@setMethodCallHandler
                }

                sendSmsInternal(phone, message, result)
            } else {
                result.notImplemented()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val phone = pendingCall?.argument<String>("phone")
                val message = pendingCall?.argument<String>("message")
                if (phone != null && message != null && pendingResult != null) {
                    sendSmsInternal(phone, message, pendingResult!!)
                }
            } else {
                pendingResult?.error("PERMISSION_DENIED", "SMS permission denied.", null)
            }
            pendingCall = null
            pendingResult = null
        }
    }

    private fun sendSmsInternal(phone: String, message: String, result: MethodChannel.Result) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phone, null, message, null, null)
            result.success("sent")
        } catch (e: Exception) {
            result.error("SMS_ERROR", e.message, null)
        }
    }
}
