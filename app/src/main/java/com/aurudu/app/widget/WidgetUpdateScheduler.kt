package com.aurudu.app.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object WidgetUpdateScheduler {

    private const val UPDATE_INTERVAL_MILLIS = 60_000L
    private const val WIDGET_ALARM_REQUEST_CODE = -100

    fun startPeriodicUpdates(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            android.os.SystemClock.elapsedRealtime() + UPDATE_INTERVAL_MILLIS,
            pendingIntentFor(context),
        )
    }

    fun stopPeriodicUpdates(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntentFor(context))
    }

    private fun pendingIntentFor(context: Context): PendingIntent {
        val intent = Intent(context, WidgetUpdateReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            WIDGET_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
