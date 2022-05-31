package com.outsystems.firebase.cloudmessaging;

import com.outsystems.plugins.firebasemessaging.controller.FirebaseMessagingController
import com.outsystems.plugins.firebasemessaging.controller.FirebaseMessagingInterface
import com.outsystems.plugins.firebasemessaging.controller.FirebaseMessagingManager
import com.outsystems.plugins.firebasemessaging.controller.FirebaseNotificationManager
import com.outsystems.plugins.firebasemessaging.model.FirebaseMessagingError
import com.outsystems.plugins.oscordova.CordovaImplementation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.cordova.CallbackContext
import org.json.JSONArray

class OSFirebaseCloudMessaging : CordovaImplementation() {

    override var callbackContext: CallbackContext? = null

    private val controllerDelegate = object: FirebaseMessagingInterface {
        override fun callback(token: String) {
            sendPluginResult(token)
        }
        override fun callbackSuccess() {
            sendPluginResult(true)
        }
        override fun callbackBadgeNumber(number: Int) {
            TODO("Not yet implemented")
        }
        override fun callbackError(error: FirebaseMessagingError) {
            sendPluginResult(null, Pair(error.code, error.description))
        }
    }
    private val messagingManager = FirebaseMessagingManager()
    private val notificationManager = FirebaseNotificationManager(cordova.activity)
    private val controller = FirebaseMessagingController(controllerDelegate, messagingManager, notificationManager)

    override fun execute(action: String, args: JSONArray, callbackContext: CallbackContext): Boolean {
        this.callbackContext = callbackContext
        CoroutineScope(Default).launch {
            when (action) {
                "getToken" -> {
                    controller.getToken()
                }
                "subscribe" -> {
                    args.getString(0)?.let { topic ->
                        controller.subscribe(topic)
                    }
                }
                "unsubscribe" -> {
                    args.getString(0)?.let { topic ->
                        controller.unsubscribe(topic)
                    }
                }
                "registerDevice" -> {
                    controller.registerDevice()
                }
                "unregisterDevice" -> {
                    controller.unregisterDevice()
                }
                "clearNotifications" -> {
                    clearNotifications()
                }
                "sendLocalNotification" -> {
                    sendLocalNotification(args)
                }
                "setBadge" -> {
                    setBadgeNumber()
                }
                "getBadge" -> {
                    getBadgeNumber()
                }
                "getPendingNotifications" -> {
                    args.getBoolean(0).let { clearFromDatabase ->
                        controller.getPendingNotifications(clearFromDatabase)
                    }
                }
                else -> {}
            }
        }
        return true
    }

    override fun onRequestPermissionResult(requestCode: Int,
                                           permissions: Array<String>,
                                           grantResults: IntArray) {
        TODO("Not yet implemented")
    }

    override fun areGooglePlayServicesAvailable(): Boolean {
        TODO("Not yet implemented")
    }

    private fun getBadgeNumber() {
        controller.getBadgeNumber()
    }

    private fun sendLocalNotification(args : JSONArray) {
        val badge = args.get(0).toString().toInt()
        val title = args.get(1).toString()
        val text = args.get(2).toString()
        val channelName = args.get(3).toString()
        val channelDescription = args.get(4).toString()
        controller.sendLocalNotification(badge, title, text, channelName, channelDescription)
    }

    private fun clearNotifications() {
        controller.clearNotifications()
    }

    private fun setBadgeNumber() {
        controller.setBadgeNumber()
    }
}