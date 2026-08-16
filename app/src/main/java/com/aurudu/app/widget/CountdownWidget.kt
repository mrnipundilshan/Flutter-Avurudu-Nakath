package com.aurudu.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.unit.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.aurudu.app.MainActivity
import com.aurudu.app.data.Event
import com.aurudu.app.data.eventList
import com.aurudu.app.util.DateTimeUtils
import java.time.LocalDateTime

private val CardBackground = Color.White
private val TextColor = Color.Black
private val DigitBoxBackground = Color(0xFFFFE3AE)

class CountdownWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val now = LocalDateTime.now()
        val nextEvent = DateTimeUtils.nextUpcomingEvent(eventList, now) ?: eventList.first()
        val countdown = DateTimeUtils.countdownParts(
            DateTimeUtils.parseDateTime(nextEvent.date, nextEvent.time),
            now,
        )

        provideContent {
            CountdownWidgetContent(nextEvent, countdown)
        }
    }
}

@GlanceComposable
@Composable
private fun CountdownWidgetContent(event: Event, countdown: DateTimeUtils.CountdownParts) {
    // Seconds are intentionally omitted: the widget refreshes once a minute
    // (see WidgetUpdateScheduler), so a seconds digit would just sit stale
    // between ticks instead of reflecting reality.
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(CardBackground)
            .cornerRadius(14.dp)
            .clickable(actionStartActivity<MainActivity>())
            .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
    ) {
        Text(
            text = event.name,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = ColorProvider(TextColor),
                textAlign = TextAlign.Center,
            ),
            modifier = GlanceModifier.fillMaxWidth(),
        )
        Spacer(modifier = GlanceModifier.size(10.dp))
        Row(horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {
            CountdownItem("දින", countdown.days)
            Spacer(modifier = GlanceModifier.width(10.dp))
            CountdownItem("පැය", countdown.hours)
            Spacer(modifier = GlanceModifier.width(10.dp))
            CountdownItem("මිනි", countdown.minutes)
        }
        Spacer(modifier = GlanceModifier.size(10.dp))
        Text(
            text = event.description.take(220),
            style = TextStyle(fontSize = 12.sp, color = ColorProvider(TextColor)),
        )
    }
}

@GlanceComposable
@Composable
private fun CountdownItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {
        Text(
            text = value,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(TextColor),
            ),
            modifier = GlanceModifier
                .background(DigitBoxBackground)
                .cornerRadius(10.dp)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        )
        Spacer(modifier = GlanceModifier.size(4.dp))
        Text(
            text = label,
            style = TextStyle(fontSize = 10.sp, color = ColorProvider(TextColor)),
        )
    }
}
