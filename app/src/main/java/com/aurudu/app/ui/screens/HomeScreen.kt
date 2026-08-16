package com.aurudu.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurudu.app.R
import com.aurudu.app.data.Event
import com.aurudu.app.data.eventList
import com.aurudu.app.notification.NotificationScheduler
import com.aurudu.app.ui.components.CountdownBox
import com.aurudu.app.ui.components.EventListItem
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts
import com.aurudu.app.util.DateTimeUtils
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        NotificationScheduler.scheduleAll(context)
    }

    var nextEvent by remember {
        mutableStateOf(DateTimeUtils.nextUpcomingEvent(eventList) ?: eventList.first())
    }
    var countdown by remember {
        mutableStateOf(
            DateTimeUtils.countdownParts(DateTimeUtils.parseDateTime(nextEvent.date, nextEvent.time))
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            val target = DateTimeUtils.parseDateTime(nextEvent.date, nextEvent.time)
            val now = LocalDateTime.now()
            countdown = DateTimeUtils.countdownParts(target, now)
            if (!target.isAfter(now)) {
                nextEvent = DateTimeUtils.nextUpcomingEvent(eventList) ?: nextEvent
            }
            delay(1000)
        }
    }

    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(colors = listOf(AppColors.GradientTop, AppColors.Primary))
            ),
    ) {
        val listState = rememberLazyListState()

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            item {
                Text(
                    text = "සුභ අළුත් අවුරුද්දක් වේවා",
                    fontFamily = AppFonts.Disapamok,
                    fontWeight = FontWeight.Medium,
                    fontSize = 36.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 70.dp, bottom = 20.dp, start = 10.dp, end = 10.dp),
                )
            }

            item {
                HomeCountdownCard(
                    event = nextEvent,
                    countdown = countdown,
                    onClick = { selectedEvent = nextEvent },
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }

            item {
                Image(
                    painter = painterResource(R.drawable.line_art),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp, horizontal = 24.dp),
                    contentScale = FillWidth,
                )
            }

            items(eventList) { event ->
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    EventListItem(event = event, onClick = { selectedEvent = event })
                }
            }
        }
    }

    // Task 8 replaces this with the real EventPopupDialog.
    selectedEvent?.let { event ->
        HomePopupPlaceholder(event = event, onDismiss = { selectedEvent = null })
    }
}

@Composable
private fun HomeCountdownCard(
    event: Event,
    countdown: DateTimeUtils.CountdownParts,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 16.dp),
    ) {
        Text(
            text = "මීළඟ නැකත: ${event.name}",
            fontFamily = AppFonts.Indeewaree,
            fontWeight = FontWeight.Medium,
            fontSize = 26.sp,
            color = Color.Black,
        )
        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CountdownLabel("දින:")
            CountdownBox(countdown.days)
            CountdownLabel("පැය:")
            CountdownBox(countdown.hours)
            CountdownLabel("මිනි:")
            CountdownBox(countdown.minutes)
            CountdownLabel("තත්:")
            CountdownBox(countdown.seconds)
        }
        Text(
            text = event.description,
            fontFamily = AppFonts.Gurulugomi,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.Black,
            maxLines = 3,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun CountdownLabel(text: String) {
    Text(
        text = text,
        fontFamily = AppFonts.Indeewaree,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        color = Color.Black,
        modifier = Modifier.padding(end = 4.dp),
    )
}

@Composable
private fun HomePopupPlaceholder(event: Event, onDismiss: () -> Unit) {
    // Replaced by EventPopupDialog in Task 8.
    LaunchedEffect(event) { }
}
