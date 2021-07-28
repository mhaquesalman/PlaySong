package com.salman.playsong

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        val serviceIntent = Intent(context, MusicService::class.java)
        if (actionName != null) {
            when (actionName) {
                Constants.ACTION_PLAY -> {
                    Toast.makeText(context, "play", Toast.LENGTH_SHORT).show()
                    serviceIntent.putExtra("actionName", Constants.S_ACTION_PLAY)
                    context?.startService(serviceIntent)
                }
                Constants.ACTION_NEXT -> {
                    Toast.makeText(context, "next", Toast.LENGTH_SHORT).show()
                    serviceIntent.putExtra("actionName", Constants.S_ACTION_NEXT)
                    context?.startService(serviceIntent)
                }
                Constants.ACTION_PREV -> {
                    Toast.makeText(context, "prev", Toast.LENGTH_SHORT).show()
                    serviceIntent.putExtra("actionName", Constants.S_ACTION_PREV)
                    context?.startService(serviceIntent)
                }
                "action_stop" -> {
                    Toast.makeText(context, "stop", Toast.LENGTH_SHORT).show()
                    serviceIntent.putExtra("actionName", "actionStop")
                    context?.stopService(serviceIntent)
                }
            }
        }
    }
}