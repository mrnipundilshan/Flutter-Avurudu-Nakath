package com.aurudu.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        if (eventId == -1) return
        val name = intent.getStringExtra(EXTRA_EVENT_NAME) ?: return
        val description = intent.getStringExtra(EXTRA_EVENT_DESCRIPTION).orEmpty()
        NotificationHelper.createChannel(context)
        NotificationHelper.showEventNotification(context, eventId, name, description)
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
        const val EXTRA_EVENT_NAME = "extra_event_name"
        const val EXTRA_EVENT_DESCRIPTION = "extra_event_description"
    }
}
