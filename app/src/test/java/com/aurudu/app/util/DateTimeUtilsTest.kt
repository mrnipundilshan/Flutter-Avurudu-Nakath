package com.aurudu.app.util

import com.aurudu.app.data.Event
import com.aurudu.app.data.eventList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class DateTimeUtilsTest {

    @Test
    fun `parseDateTime parses 12-hour AM time`() {
        val result = DateTimeUtils.parseDateTime("2026-04-14", "09:12 AM")
        assertEquals(LocalDateTime.of(2026, 4, 14, 9, 12), result)
    }

    @Test
    fun `parseDateTime parses 12-hour PM time and converts to 24-hour`() {
        val result = DateTimeUtils.parseDateTime("2026-04-14", "12:05 PM")
        assertEquals(LocalDateTime.of(2026, 4, 14, 12, 5), result)
    }

    @Test
    fun `parseDateTime handles 12 AM as midnight`() {
        val result = DateTimeUtils.parseDateTime("2026-03-20", "00:01 AM")
        assertEquals(LocalDateTime.of(2026, 3, 20, 0, 1), result)
    }

    @Test
    fun `countdownParts computes remaining time and zero-pads`() {
        val now = LocalDateTime.of(2026, 4, 10, 0, 0, 0)
        val target = LocalDateTime.of(2026, 4, 11, 1, 2, 3)
        val parts = DateTimeUtils.countdownParts(target, now)
        assertEquals("01", parts.days)
        assertEquals("01", parts.hours)
        assertEquals("02", parts.minutes)
        assertEquals("03", parts.seconds)
    }

    @Test
    fun `countdownParts returns all zeros when target is in the past`() {
        val now = LocalDateTime.of(2026, 4, 15, 0, 0, 0)
        val target = LocalDateTime.of(2026, 4, 14, 0, 0, 0)
        val parts = DateTimeUtils.countdownParts(target, now)
        assertEquals("00", parts.days)
        assertEquals("00", parts.hours)
        assertEquals("00", parts.minutes)
        assertEquals("00", parts.seconds)
    }

    @Test
    fun `nextUpcomingEvent returns soonest future event`() {
        val now = LocalDateTime.of(2026, 4, 13, 12, 0, 0)
        val events = listOf(
            Event(1, "Later", "10:00 AM", "2026-04-20", "d", 0),
            Event(2, "Soonest", "09:12 AM", "2026-04-14", "d", 0),
            Event(3, "Past", "00:01 AM", "2026-03-20", "d", 0),
        )
        val result = DateTimeUtils.nextUpcomingEvent(events, now)
        assertEquals(2, result?.id)
    }

    @Test
    fun `nextUpcomingEvent returns null when all events are in the past`() {
        val now = LocalDateTime.of(2027, 1, 1, 0, 0, 0)
        val events = listOf(
            Event(1, "Past", "10:00 AM", "2026-04-20", "d", 0),
        )
        assertNull(DateTimeUtils.nextUpcomingEvent(events, now))
    }

    @Test
    fun `every event in eventList parses without throwing`() {
        eventList.forEach { event ->
            DateTimeUtils.parseDateTime(event.date, event.time)
        }
    }
}
