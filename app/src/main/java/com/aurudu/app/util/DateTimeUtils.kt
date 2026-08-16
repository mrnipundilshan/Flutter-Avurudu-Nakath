package com.aurudu.app.util

import com.aurudu.app.data.Event
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {

    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    /** Parses "yyyy-MM-dd" + "hh:mm a" (e.g. "2026-04-14" + "09:12 AM") into a LocalDateTime. */
    fun parseDateTime(dateStr: String, timeStr: String): LocalDateTime {
        val date = LocalDate.parse(dateStr)
        val time = LocalTime.parse(timeStr.trim(), timeFormatter)
        return LocalDateTime.of(date, time)
    }

    data class CountdownParts(
        val days: String,
        val hours: String,
        val minutes: String,
        val seconds: String,
    )

    /** Zero-padded remaining time until [target]; all zeros if [target] is not after [now]. */
    fun countdownParts(target: LocalDateTime, now: LocalDateTime = LocalDateTime.now()): CountdownParts {
        val duration = Duration.between(now, target)
        if (duration.isNegative || duration.isZero) {
            return CountdownParts("00", "00", "00", "00")
        }
        val totalSeconds = duration.seconds
        val days = totalSeconds / 86400
        val hours = (totalSeconds % 86400) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return CountdownParts(
            days = days.toString().padStart(2, '0'),
            hours = hours.toString().padStart(2, '0'),
            minutes = minutes.toString().padStart(2, '0'),
            seconds = seconds.toString().padStart(2, '0'),
        )
    }

    /** Returns the event with the soonest future [Event.date]/[Event.time], or null if none are upcoming. */
    fun nextUpcomingEvent(events: List<Event>, now: LocalDateTime = LocalDateTime.now()): Event? {
        return events
            .map { it to parseDateTime(it.date, it.time) }
            .filter { (_, dateTime) -> dateTime.isAfter(now) }
            .minByOrNull { (_, dateTime) -> Duration.between(now, dateTime) }
            ?.first
    }
}
