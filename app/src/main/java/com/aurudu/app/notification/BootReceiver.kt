package com.aurudu.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.aurudu.app.widget.CountdownWidget
import com.aurudu.app.widget.WidgetUpdateScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationScheduler.scheduleAll(context)

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val hasWidget = GlanceAppWidgetManager(context)
                        .getGlanceIds(CountdownWidget::class.java)
                        .isNotEmpty()
                    if (hasWidget) {
                        WidgetUpdateScheduler.startPeriodicUpdates(context)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
