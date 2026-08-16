package com.aurudu.app.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.aurudu.app.R
import com.aurudu.app.data.Event
import com.aurudu.app.data.Language
import com.aurudu.app.data.eventList
import com.aurudu.app.data.localizedDescription
import com.aurudu.app.data.localizedName
import com.aurudu.app.notification.NotificationScheduler
import com.aurudu.app.ui.components.CountdownBox
import com.aurudu.app.ui.components.EventListItem
import com.aurudu.app.ui.components.EventPopupDialog
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts
import com.aurudu.app.util.DateTimeUtils
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
fun HomeScreen(language: Language = Language.SINHALA) {
    val context = LocalContext.current

    // Tracks whether the POST_NOTIFICATIONS request has been answered (or
    // was never needed below API 33), so the exact-alarm prompt below can
    // wait for the actual result instead of guessing with a fixed delay.
    var notificationAskDone by remember {
        mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> notificationAskDone = true }

    // One-time POST_NOTIFICATIONS request on first composition.
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Exact-alarm Settings intent: waits for the POST_NOTIFICATIONS dialog to
    // actually be answered (rather than a fixed delay) so it doesn't launch
    // on top of it, and is gated to fire at most once per process lifetime
    // (surviving Activity recreation via rememberSaveable, since returning
    // from the exact-alarm Settings screen recreates the Activity).
    var exactAlarmPromptShown by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(notificationAskDone) {
        if (!notificationAskDone) return@LaunchedEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !exactAlarmPromptShown) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                exactAlarmPromptShown = true
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                try {
                    context.startActivity(intent)
                } catch (_: android.content.ActivityNotFoundException) {
                    // Settings screen unavailable on this OEM build — don't crash.
                }
            }
        }
    }

    // Re-attempt scheduling on every resume so returning from the exact-alarm
    // Settings screen (having granted the permission) actually retries the
    // scheduling that silently no-ops while the permission is still missing.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                NotificationScheduler.scheduleAll(context, language = language)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        ) {
            item {
                Text(
                    text = if (language == Language.TAMIL) "இனிய புத்தாண்டு வாழ்த்துக்கள்" else "සුභ අළුත් අවුරුද්දක් වේවා",
                    fontFamily = AppFonts.forLanguage(language, AppFonts.Disapamok),
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
                    language = language,
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
                    EventListItem(event = event, language = language, onClick = { selectedEvent = event })
                }
            }
        }
    }

    selectedEvent?.let { event ->
        EventPopupDialog(event = event, language = language, onDismiss = { selectedEvent = null })
    }
}

@Composable
private fun HomeCountdownCard(
    event: Event,
    countdown: DateTimeUtils.CountdownParts,
    language: Language,
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
        val nextEventLabel = if (language == Language.TAMIL) "அடுத்த நேரம்" else "මීළඟ නැකත"
        Text(
            text = "$nextEventLabel: ${event.localizedName(language)}",
            fontFamily = AppFonts.forLanguage(language, AppFonts.Indeewaree),
            fontWeight = FontWeight.Medium,
            fontSize = 26.sp,
            color = Color.Black,
        )
        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val daysLabel = if (language == Language.TAMIL) "நாட்கள்:" else "දින:"
            val hoursLabel = if (language == Language.TAMIL) "மணி:" else "පැය:"
            val minutesLabel = if (language == Language.TAMIL) "நிமி:" else "මිනි:"
            val secondsLabel = if (language == Language.TAMIL) "வினா:" else "තත්:"
            CountdownLabel(daysLabel, language)
            CountdownBox(countdown.days)
            CountdownLabel(hoursLabel, language)
            CountdownBox(countdown.hours)
            CountdownLabel(minutesLabel, language)
            CountdownBox(countdown.minutes)
            CountdownLabel(secondsLabel, language)
            CountdownBox(countdown.seconds)
        }
        Text(
            text = event.localizedDescription(language),
            fontFamily = AppFonts.forLanguage(language, AppFonts.Gurulugomi),
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.Black,
            maxLines = 3,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun CountdownLabel(text: String, language: Language) {
    Text(
        text = text,
        fontFamily = AppFonts.forLanguage(language, AppFonts.Indeewaree),
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        color = Color.Black,
        modifier = Modifier.padding(end = 4.dp),
    )
}

