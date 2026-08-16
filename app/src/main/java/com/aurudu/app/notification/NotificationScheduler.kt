package com.aurudu.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.aurudu.app.data.Event
import com.aurudu.app.data.Language
import com.aurudu.app.data.LanguagePreference
import com.aurudu.app.data.eventList
import com.aurudu.app.data.localizedDescription
import com.aurudu.app.data.localizedName
import com.aurudu.app.util.DateTimeUtils
import java.time.LocalDateTime
import java.time.ZoneId

object NotificationScheduler {

    fun scheduleAll(
        context: Context,
        events: List<Event> = eventList,
        language: Language = LanguagePreference.get(context),
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = LocalDateTime.now()

        events.forEach { event ->
            alarmManager.cancel(pendingIntentFor(context, event, language))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return
        }

        events.forEach { event ->
            val target = DateTimeUtils.parseDateTime(event.date, event.time)
            if (target.isAfter(now)) {
                val triggerAtMillis = target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntentFor(context, event, language)
                )
            }
        }
    }

    private fun pendingIntentFor(context: Context, event: Event, language: Language): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_EVENT_ID, event.id)
            putExtra(AlarmReceiver.EXTRA_EVENT_NAME, event.localizedName(language))
            putExtra(AlarmReceiver.EXTRA_EVENT_DESCRIPTION, event.localizedDescription(language))
        }
        return PendingIntent.getBroadcast(
            context,
            event.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
