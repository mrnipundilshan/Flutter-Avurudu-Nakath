# Avurudu Nakath: Flutter → Kotlin/Compose Migration Design

## Repository layout

- Branch: `kotlin-migration` (currently identical to `main`).
- Flutter files removed from repo root on this branch; existing Kotlin/Compose
  scaffold (`~/Desktop/kotlin/avurudunakath`) copied in as the new root.
- Result: `kotlin-migration` branch = pure Kotlin/Compose Android app, `main`
  stays pure Flutter. History preserved via git log on the branch.

## Architecture

Single-activity Compose app. `MainActivity` hosts a `NavHost`
(androidx.navigation:navigation-compose) with two routes: `getStart`, `home`.
Countdown state driven by `LaunchedEffect` + `delay(1000)` loop per screen,
mirroring Flutter's `Timer.periodic`. The event popup is a Compose `Dialog`,
not a separate route (matches Flutter's `showDialog`).

## Components

```
data/EventData.kt              data class Event, hardcoded eventList (10 items,
                                Sinhala/Tamil text preserved verbatim)
util/DateTimeUtils.kt          parse "yyyy-MM-dd" + "hh:mm a", nextUpcomingEvent(),
                                countdown diff -> {days, hours, minutes, seconds}
ui/theme/{Color,Theme,Type}.kt extended: FFBE45 / FFD485 / FFF1D6 / FFE3AE colors,
                                5 custom FontFamily entries (res/font)
ui/screens/GetStartScreen.kt   radial gradient bg, 3 layered bg PNGs, rotating +
                                breathing sun (rememberInfiniteTransition), fade-in,
                                2 language buttons (Tamil = no-op onClick, matches
                                Flutter placeholder behavior)
ui/screens/HomeScreen.kt       LazyColumn with scroll-linked header fade (container01
                                -> container02 transition via scroll offset),
                                countdown card, event list, triggers notification
                                scheduling on first launch
ui/components/CountdownBox.kt
ui/components/EventListItem.kt
ui/components/EventPopupDialog.kt
notification/NotificationScheduler.kt   AlarmManager.setExactAndAllowWhileIdle per
                                         future event
notification/AlarmReceiver.kt           BroadcastReceiver, builds + posts notification
notification/BootReceiver.kt            RECEIVE_BOOT_COMPLETED, re-schedules alarms
                                         (Android alarms don't survive reboot)
notification/NotificationHelper.kt      channel creation, POST_NOTIFICATIONS runtime
                                         request (Android 13+)
```

## Data flow

`eventList` is a static list, same content as Flutter's `dataList`.
`nextUpcomingEvent()` finds the soonest future event; both the Home header card
and the ticking countdown derive from it. Tapping the header or any event list
item opens `EventPopupDialog` for that specific event, with its own live
countdown.

## Notifications

On first `HomeScreen` composition: request `POST_NOTIFICATIONS` (API 33+) and
exact-alarm permission if needed, then `NotificationScheduler` cancels and
re-schedules one alarm per future event (alarm id = event.id, matching
Flutter's per-event notification id). `AlarmReceiver` fires at the exact
scheduled time and posts a notification with the event's name/description.
`BootReceiver` re-arms all alarms after device reboot.

## Assets

- PNGs → `res/drawable/` (renamed to lowercase snake_case — Android
  resource-name requirement).
- TTFs → `res/font/`.
- App icon: reuse existing Kotlin scaffold's launcher icons.

## Testing

Manual, in emulator:
- Countdown ticks correctly on both screens and in the popup.
- Popup opens with correct event data for every list item and the header.
- Scroll-linked header transition (container01 → container02) is smooth.
- Sun rotates/breathes continuously on GetStart screen.
- Schedule a near-future test event; confirm notification fires, including
  from a killed-app state and after a simulated reboot.

## Out of scope

- `notification_page.dart` — dead code in Flutter app (never navigated to),
  not ported.
- Tamil language content — button stays a no-op, matching current Flutter
  behavior (no Tamil translations exist yet).
