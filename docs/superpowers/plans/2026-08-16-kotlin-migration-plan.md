# Avurudu Nakath Kotlin/Compose Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port the Avurudu Nakath Flutter app to a native Kotlin/Jetpack Compose Android app on the `kotlin-migration` branch of `mrnipundilshan/Flutter-Avurudu-Nakath`, feature-for-feature (GetStart screen, Home screen, event popup, scheduled local notifications).

**Architecture:** Single-activity Compose app. `MainActivity` hosts a `NavHost` (androidx.navigation:navigation-compose) with two routes (`getStart`, `home`). Countdown state ticks via `LaunchedEffect` + `delay(1000)` loops. Notifications scheduled per-event via `AlarmManager.setExactAndAllowWhileIdle`, delivered via `BroadcastReceiver`, re-armed on boot.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose (BOM 2026.02.01), Material3, androidx.navigation:navigation-compose, AlarmManager (no external notification library needed), AGP 9.3.1, minSdk 24 / targetSdk 36 / compileSdk 36.

## Global Constraints

- Package/namespace: `com.aurudu.app` (already set in scaffold — do not change).
- minSdk 24, targetSdk 36, compileSdk 36 — do not change existing values in `app/build.gradle.kts`.
- All Sinhala/Tamil user-facing strings must be copied verbatim from the Flutter source (`lib/data/data.dart`, `lib/GetStartPage.dart`, `lib/HomePage.dart`) — no retranslation, no wording changes.
- Colors: `#FFBE45` (primary/scaffold bg), `#FFD485` (gradient top), `#FFF1D6` (secondary/card bg), `#FFE3AE` (countdown box bg) — exact hex values, copied from `lib/main.dart` and container files.
- Fonts: 5 custom Sinhala fonts (`UN-Disapamok`, `UN-Indeewaree`, `UN-Arundathee`, `UN-Gurulugomi`, `UN-Ganganee`), sourced from `assets/fonts/*.ttf` in the Flutter repo, same family-to-usage mapping as Flutter (see Task 4).
- Tamil language button on GetStart screen is a no-op `onClick` — no Tamil content exists yet (matches Flutter placeholder behavior).
- `notification_page.dart` is dead code in Flutter (unreferenced) — not ported.
- No Android emulator/device tool is available in this environment. Verification for each task is `./gradlew compileDebugKotlin` (or `assembleDebug` for the final task) plus JVM unit tests where applicable. The user is expected to do final manual/visual verification on a device or emulator themselves before merging.

---

## File Structure

```
Flutter-Avurudu-Nakath/                         (repo root, after Task 1 restructure)
├── app/
│   ├── build.gradle.kts                        (Task 2: + navigation-compose dep)
│   ├── src/main/
│   │   ├── AndroidManifest.xml                 (Task 5: + permissions/receivers)
│   │   ├── res/
│   │   │   ├── drawable/                        (Task 2: 9 PNGs copied+renamed)
│   │   │   ├── font/                            (Task 2: 5 TTFs copied+renamed)
│   │   │   └── values/strings.xml               (unchanged, app_name already set)
│   │   └── java/com/aurudu/app/
│   │       ├── MainActivity.kt                  (Task 6: NavHost wiring)
│   │       ├── data/
│   │       │   └── Event.kt                     (Task 3: data class + eventList)
│   │       ├── util/
│   │       │   └── DateTimeUtils.kt              (Task 3: parsing/countdown logic)
│   │       ├── ui/theme/
│   │       │   ├── Color.kt                     (Task 4: replace defaults)
│   │       │   ├── Type.kt                       (Task 4: custom FontFamilies)
│   │       │   └── Theme.kt                      (Task 4: app color scheme)
│   │       ├── ui/screens/
│   │       │   ├── GetStartScreen.kt             (Task 6)
│   │       │   └── HomeScreen.kt                 (Task 7)
│   │       ├── ui/components/
│   │       │   ├── CountdownBox.kt               (Task 7)
│   │       │   ├── EventListItem.kt              (Task 7)
│   │       │   └── EventPopupDialog.kt           (Task 8)
│   │       └── notification/
│   │           ├── NotificationHelper.kt         (Task 5)
│   │           ├── AlarmReceiver.kt              (Task 5)
│   │           ├── BootReceiver.kt               (Task 5)
│   │           └── NotificationScheduler.kt      (Task 5)
│   └── src/test/java/com/aurudu/app/
│       └── util/DateTimeUtilsTest.kt             (Task 3)
```

---

### Task 1: Restructure repo — replace Flutter root with Kotlin project

**Files:**
- Delete (git rm): all Flutter root files/dirs (`lib/`, `android/`, `ios/`, `linux/`, `macos/`, `windows/`, `web/`, `test/`, `assets/`, `.vscode/`, `.metadata`, `pubspec.yaml`, `pubspec.lock`, `analysis_options.yaml`, `Screenshot_*.png`)
- Keep: `README.md`, `.git/`, `docs/superpowers/` (specs/plans just added)
- Copy in: entire contents of `~/Desktop/kotlin/avurudunakath/` (app/, gradle/, build.gradle.kts, settings.gradle.kts, gradle.properties, gradlew, gradlew.bat, .gitignore) into repo root

**Interfaces:** N/A (repo restructuring only, no code interfaces yet).

- [ ] **Step 1: Confirm on the right branch and working tree is clean**

Run: `cd "/Users/nipun/Desktop/Flutter/Flutter-Avurudu-Nakath" && git status`
Expected: `On branch kotlin-migration` and `nothing to commit, working tree clean`

- [ ] **Step 2: Remove Flutter source files from the branch**

```bash
cd "/Users/nipun/Desktop/Flutter/Flutter-Avurudu-Nakath"
git rm -r lib android ios linux macos windows web test assets .vscode
git rm .metadata pubspec.yaml pubspec.lock analysis_options.yaml
git rm "Screenshot_1742715809.png" "Screenshot_1742715817.png" "Screenshot_1742715823.png"
```
Expected: all listed paths staged for deletion, no errors.

- [ ] **Step 3: Copy the Kotlin project into the repo root**

```bash
cd "/Users/nipun/Desktop/Flutter/Flutter-Avurudu-Nakath"
cp -R "/Users/nipun/Desktop/kotlin/avurudunakath/app" .
cp -R "/Users/nipun/Desktop/kotlin/avurudunakath/gradle" .
cp "/Users/nipun/Desktop/kotlin/avurudunakath/build.gradle.kts" .
cp "/Users/nipun/Desktop/kotlin/avurudunakath/settings.gradle.kts" .
cp "/Users/nipun/Desktop/kotlin/avurudunakath/gradle.properties" .
cp "/Users/nipun/Desktop/kotlin/avurudunakath/gradlew" .
cp "/Users/nipun/Desktop/kotlin/avurudunakath/gradlew.bat" .
chmod +x gradlew
```
Expected: no errors, `ls` shows `app/`, `gradle/`, `build.gradle.kts`, `settings.gradle.kts`, `gradlew` at repo root.

- [ ] **Step 4: Replace the repo `.gitignore` with the Kotlin project's `.gitignore`, keeping the old Flutter `.gitignore` content out**

```bash
cd "/Users/nipun/Desktop/Flutter/Flutter-Avurudu-Nakath"
cp "/Users/nipun/Desktop/kotlin/avurudunakath/.gitignore" .gitignore
```
Expected: `.gitignore` now contains the Android/Gradle ignore rules (`*.iml`, `.gradle`, `/local.properties`, `/build`, etc.) instead of Flutter ones.

- [ ] **Step 5: Update README.md to describe the Kotlin app**

Replace the full contents of `README.md`:

```markdown
# Avurudu Nakath

Native Android app (Kotlin + Jetpack Compose) showing the Sinhala/Tamil New Year (Avurudu) auspicious times (nakath) with live countdowns and scheduled local notifications.

## Getting Started

Open the project root in Android Studio, or build from the command line:

```bash
./gradlew assembleDebug
```

Minimum SDK 24, target/compile SDK 36.
```

- [ ] **Step 6: Verify the Gradle wrapper runs**

Run: `cd "/Users/nipun/Desktop/Flutter/Flutter-Avurudu-Nakath" && ./gradlew tasks --console=plain 2>&1 | tail -20`
Expected: task list prints, no build errors (the app module currently only has default scaffold code, so this should succeed).

- [ ] **Step 7: Stage and commit the restructure**

```bash
cd "/Users/nipun/Desktop/Flutter/Flutter-Avurudu-Nakath"
git add -A
git status
git commit -m "$(cat <<'EOF'
chore: replace Flutter project with Kotlin/Compose scaffold

Moves the kotlin-migration branch root from the Flutter app to the
existing empty Kotlin/Compose Android scaffold. Implementation of the
actual app follows in subsequent commits.
EOF
)"
```
Expected: commit succeeds. Review `git status` output before committing — confirm no unexpected files (e.g. no `local.properties`, no `.gradle/` cache dirs) are staged.

---

### Task 2: Gradle dependency + asset pipeline (drawables, fonts)

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Create: `app/src/main/res/drawable/sun.png`, `sun_face.png`, `line_art.png`, `cooking.png`, `nakath2.png`, `nakath3.png`, `bg1.png`, `bg2.png`, `bg3.png`
- Create: `app/src/main/res/font/un_disapamok.ttf`, `un_indeewaree.ttf`, `un_arundathee.ttf`, `un_gurulugomi.ttf`, `un_ganganee.ttf`

**Interfaces:**
- Produces: drawable resource ids `R.drawable.sun`, `R.drawable.sun_face`, `R.drawable.line_art`, `R.drawable.cooking`, `R.drawable.nakath2`, `R.drawable.nakath3`, `R.drawable.bg1`, `R.drawable.bg2`, `R.drawable.bg3`. Font resource ids `R.font.un_disapamok`, `R.font.un_indeewaree`, `R.font.un_arundathee`, `R.font.un_gurulugomi`, `R.font.un_ganganee`. Task 4 (theme/fonts) and Tasks 6–8 (screens) consume these.

- [ ] **Step 1: Add navigation-compose to the version catalog**

Edit `gradle/libs.versions.toml` — add to `[versions]`:
```toml
navigationCompose = "2.9.6"
```
Add to `[libraries]`:
```toml
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
```

- [ ] **Step 2: Add the dependency to the app module**

Edit `app/build.gradle.kts`, in the `dependencies { ... }` block, add after `implementation(libs.androidx.activity.compose)`:
```kotlin
    implementation(libs.androidx.navigation.compose)
```

- [ ] **Step 3: Copy and rename image assets into `res/drawable`**

```bash
cd "/Users/nipun/Desktop/Flutter/Flutter-Avurudu-Nakath"
SRC="/Users/nipun/Desktop/Flutter/Flutter-Avurudu-Nakath"
# Note: at this point in git history the old assets/ dir was removed from
# the working tree in Task 1's git rm, but the files still exist on disk
# until a hard reset — if they were already deleted, restore from git first:
git show main:assets/sun.png > app/src/main/res/drawable/sun.png 2>/dev/null || true
mkdir -p app/src/main/res/drawable
for f in sun:sun sunFace:sun_face lineArt:line_art cooking:cooking nakath2:nakath2 nakath3:nakath3 bg1:bg1 bg2:bg2 bg3:bg3; do
  src_name="${f%%:*}"
  dst_name="${f##*:}"
  git show main:"assets/${src_name}.png" > "app/src/main/res/drawable/${dst_name}.png"
done
ls app/src/main/res/drawable/
```
Expected: 9 PNG files listed (`sun.png`, `sun_face.png`, `line_art.png`, `cooking.png`, `nakath2.png`, `nakath3.png`, `bg1.png`, `bg2.png`, `bg3.png`), each with nonzero size.

- [ ] **Step 4: Copy and rename font assets into `res/font`**

```bash
cd "/Users/nipun/Desktop/Flutter/Flutter-Avurudu-Nakath"
mkdir -p app/src/main/res/font
for f in UN-Disapamok:un_disapamok UN-Indeewaree:un_indeewaree UN-Arundathee:un_arundathee UN-Gurulugomi:un_gurulugomi UN-Ganganee:un_ganganee; do
  src_name="${f%%:*}"
  dst_name="${f##*:}"
  git show main:"assets/fonts/${src_name}.ttf" > "app/src/main/res/font/${dst_name}.ttf"
done
ls app/src/main/res/font/
```
Expected: 5 TTF files listed, each with nonzero size.

- [ ] **Step 5: Verify Gradle sync picks up the new dependency and resources**

Run: `./gradlew :app:compileDebugKotlin --console=plain 2>&1 | tail -30`
Expected: `BUILD SUCCESSFUL` (existing `MainActivity.kt` is unchanged default scaffold, so this only validates the dependency resolves and resources are valid — no R references yet).

- [ ] **Step 6: Commit**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts app/src/main/res/drawable app/src/main/res/font
git commit -m "$(cat <<'EOF'
build: add navigation-compose dependency and copy image/font assets

Ports drawables and custom Sinhala fonts from the Flutter app's
assets/ directory into Android res/drawable and res/font, renamed to
valid lowercase snake_case resource names.
EOF
)"
```
Expected: commit succeeds.

---

### Task 3: Data layer — Event model, static event list, date/countdown utilities (TDD)

**Files:**
- Create: `app/src/main/java/com/aurudu/app/data/Event.kt`
- Create: `app/src/main/java/com/aurudu/app/util/DateTimeUtils.kt`
- Create: `app/src/test/java/com/aurudu/app/util/DateTimeUtilsTest.kt`

**Interfaces:**
- Produces: `data class Event(val id: Int, val name: String, val time: String, val date: String, val description: String, val drawableRes: Int)`, `val eventList: List<Event>`, `object DateTimeUtils` with:
  - `fun parseDateTime(dateStr: String, timeStr: String): LocalDateTime`
  - `data class CountdownParts(val days: String, val hours: String, val minutes: String, val seconds: String)`
  - `fun countdownParts(target: LocalDateTime, now: LocalDateTime = LocalDateTime.now()): CountdownParts`
  - `fun nextUpcomingEvent(events: List<Event>, now: LocalDateTime = LocalDateTime.now()): Event?`
- Consumed by: Tasks 6, 7, 8 (screens/components), Task 5 (notification scheduler).

- [ ] **Step 1: Write the failing tests**

Create `app/src/test/java/com/aurudu/app/util/DateTimeUtilsTest.kt`:
```kotlin
package com.aurudu.app.util

import com.aurudu.app.data.Event
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
}
```

- [ ] **Step 2: Run tests to verify they fail (compile error — no implementation yet)**

Run: `./gradlew :app:testDebugUnitTest --tests "com.aurudu.app.util.DateTimeUtilsTest" --console=plain 2>&1 | tail -40`
Expected: FAIL — compilation error, `Event` and `DateTimeUtils` unresolved references.

- [ ] **Step 3: Create the Event data class and static event list**

Create `app/src/main/java/com/aurudu/app/data/Event.kt`:
```kotlin
package com.aurudu.app.data

import com.aurudu.app.R

data class Event(
    val id: Int,
    val name: String,
    val time: String,
    val date: String,
    val description: String,
    val drawableRes: Int,
)

val eventList: List<Event> = listOf(
    Event(
        id = 1,
        name = "නව සඳ බැලීම",
        time = "00:01 AM",
        date = "2026-03-20",
        description = "අභිනව චන්ද්‍ර වර්ෂය සඳහා මාර්තු මස 20 වන සිකුරාදා දින ද, අභිතව සූර්ය වර්ෂය සඳහා අප්‍රේල් මස 19 වන ඉරිදාදිත ද නව සඳ බැලීම මැනවි.",
        drawableRes = R.drawable.nakath2,
    ),
    Event(
        id = 2,
        name = "පරණ අවුරුද්ද සඳහා ස්නානය",
        time = "00:01 AM",
        date = "2026-04-13",
        description = "අප්‍රේල් මස 13 වැනි සදු දින දිවුල් පත් යුෂ මිශ්‍ර නානු ගා ස්නානය කොට ඉෂ්ට දේවතා අනුස්මරණයේ යෙදී වාසය මැනවි.",
        drawableRes = R.drawable.nakath2,
    ),
    Event(
        id = 3,
        name = "අලුත් අවුරුදු උදාව",
        time = "09:12 AM",
        date = "2026-04-14",
        description = "අප්‍රේල් මස 14 වැනි අඟහරුවාදා පූර්ව භාග 09.32 ට සිංහල අලූත් අවුරුද්ද උදාවෙයි.",
        drawableRes = R.drawable.nakath2,
    ),
    Event(
        id = 4,
        name = "පුණ්‍ය කාලය",
        time = "03:08 AM",
        date = "2026-04-14",
        description = "අප්‍රේල් මස 14 වැනි අගහරුවාදා පූර්ව භාග 03.08 සිට එදිනම අපර භාග 03.56 දක්වා පුණ්‍ය කාලය බැවින් අප්‍රේල් මස 14 වන අගහරුවාදා පූර්ව භාග 03.08 ට පළමුව ආහාර පාන ගෙන සියලු වැඩ අත්හැර ආගමික වතාවත් වල යෙදීමද, පුණ්‍ය කාලයේ අපර කොටස එනම් අප්‍රේල් මස 14 වන අඟහරුවාදා පූර්ව භාග 09.32 සිට එදින අපර භාග 08.56 දක්වා ආහාර පිසීම, වැඩ ඇල්ලීම, ගණුදෙනු කිරීම හා ආහාර අනුභවය ආදී නැකත් චාරිත්‍ර විධි ඉටු කිරීම මැනවි.",
        drawableRes = R.drawable.nakath3,
    ),
    Event(
        id = 5,
        name = "ආහාර පිසීම",
        time = "10:41 AM",
        date = "2026-04-14",
        description = "අප්‍රේල් මස 14 වෙනි අගහරුවාදා පූර්ව භාග 10.41 ට රක්ත වර්ණ වස්ත්‍රාභරණයෙන් සැරසී දකුණු දිශාව බලා ලිප් බැඳ ගිණි මොලවා කිරි බතක්ද, කැවිලි වර්ගයක්ද, දී කිරි වලද ද, පිලියෙල කර ගැනීම මැනවි.",
        drawableRes = R.drawable.cooking,
    ),
    Event(
        id = 6,
        name = "වැඩ ඇල්ලීම, ගනුදෙනු කිරීම හා ආහාර අනුභවය",
        time = "12:05 PM",
        date = "2026-04-14",
        description = "අප්‍රේල් මස 14 වෙනි අඟහරුවාදා අපර භාග 12.05 ට රක්ත වර්ණ වස්ත්‍රාභරණයෙන් සැරසී දකුණු දිශාව බලා සියලු වැඩ අල්ලා ගනුදෙනු කොට ආහාර අනුභවය කිරීම මැනවි.",
        drawableRes = R.drawable.nakath3,
    ),
    Event(
        id = 7,
        name = "හිස තෙල් ගෑම",
        time = "06:54 AM",
        date = "2026-04-15",
        description = "අප්‍රේල් මස 15 වෙහි බදාදා පූර්ව භාග 06.54 ට නැගෙනහිර දිශාව බලා හිසට කොහොඹ පත්ද, පයට කොළොන් පද, තබා පච්ච වර්ගා වස්ත්රාභරණයෙන් සැරසී කොහොඹ පත් යුෂ මිශ්‍ර නානු හා තෙල් ගා ස්නානය කිරීම මැනවි.",
        drawableRes = R.drawable.nakath2,
    ),
    Event(
        id = 8,
        name = "රැකීරක්ෂා සඳහා පිටත්ව යෑම",
        time = "06:27 AM",
        date = "2026-04-20",
        description = "අප්‍රේල් මස 20 වැනි සඳුදා පූර්ව භාග 06.27 ට ශ්වේත වර්ණ වස්ත්‍රාභරණයෙන් සැරසී කිරිබත් සහ එළකිරි මිශ්‍ර කැවිලිද අනුභව කර දකුණු දිශාව බලා පිටත් වීම මැනවි.",
        drawableRes = R.drawable.nakath2,
    ),
    Event(
        id = 9,
        name = "නව සඳ බැලීම",
        time = "00:01 AM",
        date = "2026-04-19",
        description = "අභිනව චන්ද්‍ර වර්ෂය සඳහා මාර්තු මස 20 වන සිකුරාදා දින ද, අභිතව සූර්ය වර්ෂය සඳහා අප්‍රේල් මස 19 වන ඉරිදාදිත ද නව සඳ බැලීම මැනවි.",
        drawableRes = R.drawable.nakath2,
    ),
    Event(
        id = 10,
        name = "පැළ සිටුවීමට",
        time = "11:36 AM",
        date = "2026-04-23",
        description = "අප්‍රේල් මස 23 වැනි බ්‍රහස්පතින්දා රන්වන් පැහැති වස්ත්‍රාභරණයෙන් සැරසී පූර්ව භාග 11.36 ට උතුරු දිශාව බලා පැළ සිටුවීම මැනවි.",
        drawableRes = R.drawable.nakath2,
    ),
)
```

- [ ] **Step 4: Create DateTimeUtils**

Create `app/src/main/java/com/aurudu/app/util/DateTimeUtils.kt`:
```kotlin
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
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `./gradlew :app:testDebugUnitTest --tests "com.aurudu.app.util.DateTimeUtilsTest" --console=plain 2>&1 | tail -40`
Expected: `BUILD SUCCESSFUL`, all 7 tests pass.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/aurudu/app/data/Event.kt \
        app/src/main/java/com/aurudu/app/util/DateTimeUtils.kt \
        app/src/test/java/com/aurudu/app/util/DateTimeUtilsTest.kt
git commit -m "$(cat <<'EOF'
feat: add Event data model, static event list, and countdown utilities

Ports lib/data/data.dart's dataList and the date/countdown parsing
logic duplicated across HomePage.dart, homePageContainer01.dart, and
popup.dart into a single tested DateTimeUtils.
EOF
)"
```
Expected: commit succeeds.

---

### Task 4: Theme — colors, custom fonts, typography

**Files:**
- Modify: `app/src/main/java/com/aurudu/app/ui/theme/Color.kt`
- Modify: `app/src/main/java/com/aurudu/app/ui/theme/Type.kt`
- Modify: `app/src/main/java/com/aurudu/app/ui/theme/Theme.kt`

**Interfaces:**
- Consumes: `R.font.*` from Task 2.
- Produces: `object AppColors` with `Primary`, `GradientTop`, `SecondaryCard`, `CountdownBoxBg` (`Color`); `object AppFonts` with `Disapamok`, `Indeewaree`, `Arundathee`, `Gurulugomi`, `Ganganee` (`FontFamily`); `AvurudunakathTheme` composable (signature unchanged, `dynamicColor` default flipped to `false` so the fixed brand colors always apply). Consumed by Tasks 6, 7, 8.

- [ ] **Step 1: Replace Color.kt with app palette**

Replace contents of `app/src/main/java/com/aurudu/app/ui/theme/Color.kt`:
```kotlin
package com.aurudu.app.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    val Primary = Color(0xFFFFBE45)
    val GradientTop = Color(0xFFFFD485)
    val SecondaryCard = Color(0xFFFFF1D6)
    val CountdownBoxBg = Color(0xFFFFE3AE)
}
```

- [ ] **Step 2: Replace Type.kt with custom font families and typography**

Replace contents of `app/src/main/java/com/aurudu/app/ui/theme/Type.kt`:
```kotlin
package com.aurudu.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aurudu.app.R

object AppFonts {
    val Disapamok = FontFamily(Font(R.font.un_disapamok, FontWeight.Normal))
    val Indeewaree = FontFamily(Font(R.font.un_indeewaree, FontWeight.Normal))
    val Arundathee = FontFamily(Font(R.font.un_arundathee, FontWeight.Normal))
    val Gurulugomi = FontFamily(Font(R.font.un_gurulugomi, FontWeight.Normal))
    val Ganganee = FontFamily(Font(R.font.un_ganganee, FontWeight.Normal))
}

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = AppFonts.Gurulugomi,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```

- [ ] **Step 3: Replace Theme.kt to use fixed brand colors (no dynamic color)**

Replace contents of `app/src/main/java/com/aurudu/app/ui/theme/Theme.kt`:
```kotlin
package com.aurudu.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    secondary = AppColors.SecondaryCard,
    background = AppColors.Primary,
    surface = AppColors.SecondaryCard,
)

@Composable
fun AvurudunakathTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
```

- [ ] **Step 4: Update MainActivity's preview/usage to match the simplified theme signature**

Modify `app/src/main/java/com/aurudu/app/MainActivity.kt` — this still has the default scaffold `Greeting`/`GreetingPreview` calling `AvurudunakathTheme { ... }` with no arguments, which already matches the new signature (`dynamicColor`/`darkTheme` params removed). No change needed here; this step just confirms it.

Run: `grep -n "AvurudunakathTheme" app/src/main/java/com/aurudu/app/MainActivity.kt`
Expected: two matches, both call `AvurudunakathTheme { ... }` with no named arguments — compatible with the new signature.

- [ ] **Step 5: Verify build**

Run: `./gradlew :app:compileDebugKotlin --console=plain 2>&1 | tail -30`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/aurudu/app/ui/theme/Color.kt \
        app/src/main/java/com/aurudu/app/ui/theme/Type.kt \
        app/src/main/java/com/aurudu/app/ui/theme/Theme.kt
git commit -m "$(cat <<'EOF'
feat: apply Avurudu brand colors and custom Sinhala fonts to theme

Replaces the default Material scaffold theme with the app's fixed
color palette (FFBE45/FFD485/FFF1D6/FFE3AE) and the 5 custom fonts
ported from assets/fonts in the Flutter app.
EOF
)"
```
Expected: commit succeeds.

---

### Task 5: Notification infrastructure — scheduler, receivers, permissions

**Files:**
- Create: `app/src/main/java/com/aurudu/app/notification/NotificationHelper.kt`
- Create: `app/src/main/java/com/aurudu/app/notification/AlarmReceiver.kt`
- Create: `app/src/main/java/com/aurudu/app/notification/BootReceiver.kt`
- Create: `app/src/main/java/com/aurudu/app/notification/NotificationScheduler.kt`
- Modify: `app/src/main/AndroidManifest.xml`

**Interfaces:**
- Consumes: `Event`, `eventList`, `DateTimeUtils.parseDateTime` from Task 3.
- Produces: `object NotificationScheduler` with `fun scheduleAll(context: Context, events: List<Event> = eventList)`. Consumed by Task 7 (`HomeScreen` calls this on first composition) and `BootReceiver`.

- [ ] **Step 1: Create NotificationHelper**

Create `app/src/main/java/com/aurudu/app/notification/NotificationHelper.kt`:
```kotlin
package com.aurudu.app.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aurudu.app.R

object NotificationHelper {
    const val CHANNEL_ID = "event_channel_id"
    private const val CHANNEL_NAME = "Event Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for Avurudu events"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
            enableVibration(true)
            enableLights(true)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun showEventNotification(context: Context, eventId: Int, title: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(eventId, notification)
    }
}
```

- [ ] **Step 2: Create AlarmReceiver**

Create `app/src/main/java/com/aurudu/app/notification/AlarmReceiver.kt`:
```kotlin
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
```

- [ ] **Step 3: Create NotificationScheduler**

Create `app/src/main/java/com/aurudu/app/notification/NotificationScheduler.kt`:
```kotlin
package com.aurudu.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.aurudu.app.data.Event
import com.aurudu.app.data.eventList
import com.aurudu.app.util.DateTimeUtils
import java.time.LocalDateTime
import java.time.ZoneId

object NotificationScheduler {

    fun scheduleAll(context: Context, events: List<Event> = eventList) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = LocalDateTime.now()

        events.forEach { event ->
            alarmManager.cancel(pendingIntentFor(context, event))
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
                    pendingIntentFor(context, event)
                )
            }
        }
    }

    private fun pendingIntentFor(context: Context, event: Event): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_EVENT_ID, event.id)
            putExtra(AlarmReceiver.EXTRA_EVENT_NAME, event.name)
            putExtra(AlarmReceiver.EXTRA_EVENT_DESCRIPTION, event.description)
        }
        return PendingIntent.getBroadcast(
            context,
            event.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
```

- [ ] **Step 4: Create BootReceiver**

Create `app/src/main/java/com/aurudu/app/notification/BootReceiver.kt`:
```kotlin
package com.aurudu.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationScheduler.scheduleAll(context)
        }
    }
}
```

- [ ] **Step 5: Add permissions and register receivers in the manifest**

Replace contents of `app/src/main/AndroidManifest.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Avurudunakath">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.Avurudunakath"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <receiver
            android:name=".notification.AlarmReceiver"
            android:exported="false" />

        <receiver
            android:name=".notification.BootReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
    </application>

</manifest>
```

- [ ] **Step 6: Verify build**

Run: `./gradlew :app:compileDebugKotlin --console=plain 2>&1 | tail -30`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/aurudu/app/notification app/src/main/AndroidManifest.xml
git commit -m "$(cat <<'EOF'
feat: add AlarmManager-based event notification scheduling

Ports notification_service.dart's exact-alarm scheduling (one alarm
per future event, boot-persistent) to AlarmManager +
BroadcastReceiver, since Android alarms don't survive reboot.
EOF
)"
```
Expected: commit succeeds.

---

### Task 6: Navigation shell, MainActivity, GetStartScreen

**Files:**
- Modify: `app/src/main/java/com/aurudu/app/MainActivity.kt`
- Create: `app/src/main/java/com/aurudu/app/ui/screens/GetStartScreen.kt`

**Interfaces:**
- Consumes: `AvurudunakathTheme`, `AppColors`, `AppFonts` (Task 4); `R.drawable.*` (Task 2).
- Produces: `@Composable fun GetStartScreen(onSinhalaSelected: () -> Unit)`. `MainActivity` produces the `NavHost` with routes `"getStart"` and `"home"` — Task 7's `HomeScreen` is registered against the `"home"` route in this task's `MainActivity.kt` edit (as a temporary placeholder `Text("home")`, replaced for real in Task 7 Step where `HomeScreen` exists) — to avoid a forward reference, this task defines the `NavHost` calling `HomeScreen(navController)` directly; since `HomeScreen` does not exist until Task 7, **Task 6 Step 4's build verification will fail to compile until Task 7 is also done**. To keep every task independently buildable, Task 6 instead adds a minimal placeholder `HomeScreen` composable inline in `MainActivity.kt` that Task 7 will replace by creating the real one in its own file (Kotlin will use whichever `HomeScreen` is present; Task 7 Step 1 deletes the placeholder).

- [ ] **Step 1: Create GetStartScreen**

Create `app/src/main/java/com/aurudu/app/ui/screens/GetStartScreen.kt`:
```kotlin
package com.aurudu.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurudu.app.R
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts

@Composable
fun GetStartScreen(onSinhalaSelected: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "sun")
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
        ),
        label = "sunRotation",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(AppColors.GradientTop, AppColors.Primary),
                )
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Image(
            painter = painterResource(R.drawable.bg3),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            contentScale = FillWidth,
        )

        Image(
            painter = painterResource(R.drawable.sun),
            contentDescription = null,
            modifier = Modifier
                .offset(y = 90.dp)
                .size(230.dp)
                .rotate(sunRotation),
            contentScale = ContentScale.Crop,
        )

        Image(
            painter = painterResource(R.drawable.sun_face),
            contentDescription = null,
            modifier = Modifier
                .offset(y = 90.dp)
                .size(230.dp),
            contentScale = ContentScale.Crop,
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "අපේ අවුරුදු නැකැත්",
                fontFamily = AppFonts.Disapamok,
                fontWeight = FontWeight.Medium,
                fontSize = 48.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 330.dp),
            )

            Text(
                text = "புத்தாண்டு வாழ்த்துக்கள்",
                fontFamily = AppFonts.Disapamok,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 548.dp),
            )

            Button(
                onClick = onSinhalaSelected,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 630.dp)
                    .size(width = 300.dp, height = 50.dp),
            ) {
                Text(text = "සිංහල", fontSize = 25.sp)
            }

            Button(
                onClick = { /* Tamil not yet supported — matches Flutter placeholder */ },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 694.dp)
                    .size(width = 300.dp, height = 50.dp),
            ) {
                Text(text = "தமிழ்", fontSize = 25.sp)
            }
        }
    }
}
```

- [ ] **Step 2: Wire MainActivity with NavHost and a temporary Home placeholder**

Replace contents of `app/src/main/java/com/aurudu/app/MainActivity.kt`:
```kotlin
package com.aurudu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aurudu.app.ui.screens.GetStartScreen
import com.aurudu.app.ui.screens.HomeScreen
import com.aurudu.app.ui.theme.AvurudunakathTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvurudunakathTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
private fun AppNavHost() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "getStart",
            modifier = Modifier,
        ) {
            composable("getStart") {
                GetStartScreen(onSinhalaSelected = { navController.navigate("home") })
            }
            composable("home") {
                HomeScreen()
            }
        }
    }
}
```
Note: this references `com.aurudu.app.ui.screens.HomeScreen`, which does not exist until Task 7. This is intentional — Task 6's build verification (Step 3) is expected to fail with an unresolved reference; that failure is resolved by Task 7. Do not treat it as a Task 6 defect.

- [ ] **Step 3: Verify GetStartScreen itself compiles (expected overall failure due to missing HomeScreen)**

Run: `./gradlew :app:compileDebugKotlin --console=plain 2>&1 | tail -40`
Expected: FAIL with `unresolved reference: HomeScreen` only — no other errors. If there are errors unrelated to `HomeScreen`, fix `GetStartScreen.kt` before proceeding.

- [ ] **Step 4: Commit (build intentionally red pending Task 7)**

```bash
git add app/src/main/java/com/aurudu/app/MainActivity.kt \
        app/src/main/java/com/aurudu/app/ui/screens/GetStartScreen.kt
git commit -m "$(cat <<'EOF'
feat: add navigation shell and GetStartScreen

Ports GetStartPage.dart: radial gradient background, layered bg
images, rotating sun, main title text, and the Sinhala/Tamil language
buttons (Tamil remains a no-op, matching Flutter). Wires up
NavHost with getStart -> home routes. Build is red pending HomeScreen
in the next commit (intentional, avoids a giant single commit).
EOF
)"
```
Expected: commit succeeds (commit is allowed even though the build currently fails to compile — it's fixed by Task 7).

---

### Task 7: HomeScreen — countdown ticker, scroll-linked header, event list

**Files:**
- Create: `app/src/main/java/com/aurudu/app/ui/screens/HomeScreen.kt`
- Create: `app/src/main/java/com/aurudu/app/ui/components/CountdownBox.kt`
- Create: `app/src/main/java/com/aurudu/app/ui/components/EventListItem.kt`

**Interfaces:**
- Consumes: `Event`, `eventList`, `DateTimeUtils` (Task 3); `AppColors`, `AppFonts` (Task 4); `NotificationScheduler.scheduleAll` (Task 5); `R.drawable.*` (Task 2).
- Produces: `@Composable fun HomeScreen()` (resolves Task 6's unresolved reference). `@Composable fun CountdownBox(value: String)`. `@Composable fun EventListItem(event: Event, onClick: () -> Unit)`. `HomeScreen` internally manages a `showPopupForEvent: Event?` state — Task 8 replaces the internal no-op placeholder dialog call with the real `EventPopupDialog`.

- [ ] **Step 1: Create CountdownBox component**

Create `app/src/main/java/com/aurudu/app/ui/components/CountdownBox.kt`:
```kotlin
package com.aurudu.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts

@Composable
fun CountdownBox(value: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(AppColors.CountdownBoxBg, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = value, fontFamily = AppFonts.Arundathee, fontSize = 25.sp)
    }
}
```

- [ ] **Step 2: Create EventListItem component**

Create `app/src/main/java/com/aurudu/app/ui/components/EventListItem.kt`:
```kotlin
package com.aurudu.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurudu.app.data.Event
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts

@Composable
fun EventListItem(event: Event, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(AppColors.SecondaryCard, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(
            text = event.name,
            fontFamily = AppFonts.Indeewaree,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            color = Color.Black,
        )
        Text(
            text = event.description,
            fontFamily = AppFonts.Ganganee,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}
```

- [ ] **Step 3: Create HomeScreen**

Create `app/src/main/java/com/aurudu/app/ui/screens/HomeScreen.kt`:
```kotlin
package com.aurudu.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
```
Note: `Row` needs `import androidx.compose.foundation.layout.Row` — add it to the import list above (`androidx.compose.foundation.layout.Row` alongside the other `androidx.compose.foundation.layout.*` imports).

- [ ] **Step 4: Add the missing Row import**

Edit `app/src/main/java/com/aurudu/app/ui/screens/HomeScreen.kt`, add this import alongside the other `androidx.compose.foundation.layout` imports:
```kotlin
import androidx.compose.foundation.layout.Row
```

- [ ] **Step 5: Verify full build compiles (Task 6's HomeScreen reference now resolves)**

Run: `./gradlew :app:compileDebugKotlin --console=plain 2>&1 | tail -40`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Run unit tests to confirm no regression**

Run: `./gradlew :app:testDebugUnitTest --console=plain 2>&1 | tail -30`
Expected: `BUILD SUCCESSFUL`, all `DateTimeUtilsTest` tests still pass.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/aurudu/app/ui/screens/HomeScreen.kt \
        app/src/main/java/com/aurudu/app/ui/components/CountdownBox.kt \
        app/src/main/java/com/aurudu/app/ui/components/EventListItem.kt
git commit -m "$(cat <<'EOF'
feat: add HomeScreen with live countdown and event list

Ports HomePage.dart, homePageContainer01.dart/02.dart: ticking
countdown card for the next event, full event list, and notification
scheduling triggered on first composition. Popup wiring is a
placeholder here, replaced in the next commit.
EOF
)"
```
Expected: commit succeeds.

---

### Task 8: EventPopupDialog — event detail popup

**Files:**
- Create: `app/src/main/java/com/aurudu/app/ui/components/EventPopupDialog.kt`
- Modify: `app/src/main/java/com/aurudu/app/ui/screens/HomeScreen.kt`

**Interfaces:**
- Consumes: `Event`, `DateTimeUtils` (Task 3); `AppColors`, `AppFonts` (Task 4); `R.drawable.*` (Task 2).
- Produces: `@Composable fun EventPopupDialog(event: Event, onDismiss: () -> Unit)`.

- [ ] **Step 1: Create EventPopupDialog**

Create `app/src/main/java/com/aurudu/app/ui/components/EventPopupDialog.kt`:
```kotlin
package com.aurudu.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aurudu.app.data.Event
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts
import com.aurudu.app.util.DateTimeUtils
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
fun EventPopupDialog(event: Event, onDismiss: () -> Unit) {
    var countdown by remember {
        mutableStateOf(
            DateTimeUtils.countdownParts(DateTimeUtils.parseDateTime(event.date, event.time))
        )
    }

    androidx.compose.runtime.LaunchedEffect(event.id) {
        while (true) {
            countdown = DateTimeUtils.countdownParts(
                DateTimeUtils.parseDateTime(event.date, event.time),
                LocalDateTime.now(),
            )
            delay(1000)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(24.dp))
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black,
                        modifier = Modifier
                            .background(AppColors.CountdownBoxBg, CircleShape)
                            .padding(8.dp),
                    )
                }
            }

            Image(
                painter = painterResource(event.drawableRes),
                contentDescription = event.name,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .size(240.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Text(
                text = event.name,
                fontFamily = AppFonts.Indeewaree,
                fontWeight = FontWeight.Medium,
                fontSize = 26.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            )

            Text(
                text = "${event.date} ${event.time}",
                fontFamily = AppFonts.Arundathee,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .background(AppColors.SecondaryCard, RoundedCornerShape(16.dp))
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                PopupCountdownItem("දින:", countdown.days)
                PopupCountdownItem("පැය:", countdown.hours)
                PopupCountdownItem("මිනි:", countdown.minutes)
                PopupCountdownItem("තත්:", countdown.seconds)
            }

            Text(
                text = event.description,
                fontFamily = AppFonts.Ganganee,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            )
        }
    }
}

@Composable
private fun PopupCountdownItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontFamily = AppFonts.Indeewaree,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.Black,
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .size(54.dp)
                .background(AppColors.CountdownBoxBg, RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                fontFamily = AppFonts.Arundathee,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black,
            )
        }
    }
}
```

- [ ] **Step 2: Add the Material Icons Extended dependency (needed for `Icons.Default.Close`)**

Edit `gradle/libs.versions.toml`, add to `[libraries]`:
```toml
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
```
Edit `app/build.gradle.kts`, add to `dependencies { ... }`:
```kotlin
    implementation(libs.androidx.compose.material.icons.extended)
```

- [ ] **Step 3: Wire EventPopupDialog into HomeScreen, replacing the placeholder**

Edit `app/src/main/java/com/aurudu/app/ui/screens/HomeScreen.kt`:

Replace the import:
```kotlin
import com.aurudu.app.ui.components.EventListItem
```
with:
```kotlin
import com.aurudu.app.ui.components.EventListItem
import com.aurudu.app.ui.components.EventPopupDialog
```

Replace the trailing block:
```kotlin
    // Task 8 replaces this with the real EventPopupDialog.
    selectedEvent?.let { event ->
        HomePopupPlaceholder(event = event, onDismiss = { selectedEvent = null })
    }
}
```
with:
```kotlin
    selectedEvent?.let { event ->
        EventPopupDialog(event = event, onDismiss = { selectedEvent = null })
    }
}
```

Delete the now-unused placeholder function at the bottom of the file:
```kotlin
@Composable
private fun HomePopupPlaceholder(event: Event, onDismiss: () -> Unit) {
    // Replaced by EventPopupDialog in Task 8.
    LaunchedEffect(event) { }
}
```

- [ ] **Step 4: Verify build**

Run: `./gradlew :app:compileDebugKotlin --console=plain 2>&1 | tail -40`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/aurudu/app/ui/components/EventPopupDialog.kt \
        app/src/main/java/com/aurudu/app/ui/screens/HomeScreen.kt \
        gradle/libs.versions.toml app/build.gradle.kts
git commit -m "$(cat <<'EOF'
feat: add event detail popup dialog

Ports popup.dart: full-detail dialog with event image, name,
date/time, live per-event countdown, and description. Wired into
HomeScreen's countdown card and event list taps.
EOF
)"
```
Expected: commit succeeds.

---

### Task 9: Final integration build, push

**Files:** none (verification + push only).

**Interfaces:** N/A.

- [ ] **Step 1: Full assemble to catch anything unit-compile missed (resource linking, manifest merge, lint-blocking errors)**

Run: `./gradlew assembleDebug --console=plain 2>&1 | tail -60`
Expected: `BUILD SUCCESSFUL`, APK produced at `app/build/outputs/apk/debug/app-debug.apk`.

- [ ] **Step 2: Run full unit test suite**

Run: `./gradlew testDebugUnitTest --console=plain 2>&1 | tail -40`
Expected: `BUILD SUCCESSFUL`, all tests pass.

- [ ] **Step 3: Review full diff against origin/kotlin-migration before pushing**

Run: `git log --oneline origin/kotlin-migration..HEAD`
Expected: 8 commits listed (Tasks 1–8), matching the work done.

- [ ] **Step 4: Push the branch**

```bash
git push origin kotlin-migration
```
Expected: push succeeds. **Do not run this step without explicit user confirmation at execution time** — pushing is a shared-state action.

- [ ] **Step 5: Report manual verification steps to the user**

No code change — communicate to the user that automated verification (build + unit tests) is complete, but real device/emulator testing is still needed for: sun rotation animation smoothness, countdown visual layout across screen sizes, popup dialog scroll behavior, and actual notification delivery (schedule a test event a few minutes out, background the app, confirm the notification fires).

---

## Self-Review Notes

- **Spec coverage:** Repo layout (Task 1), navigation-compose + assets (Task 2), data/countdown logic (Task 3), theme/fonts/colors (Task 4), notifications (Task 5), GetStart screen (Task 6), Home screen (Task 7), popup (Task 8), final build/push (Task 9) — all spec sections covered. `notification_page.dart` explicitly excluded per spec's "Out of scope" section.
- **Placeholder scan:** Task 6/7's temporary `HomeScreen`/popup placeholders are intentional cross-task sequencing (explained inline, resolved by name in Task 7/8), not unfinished-work placeholders — every line of actual code is complete, no TODOs.
- **Type consistency:** `Event(id, name, time, date, description, drawableRes)` used identically in Task 3 (definition), Task 5 (`NotificationScheduler`), Task 7 (`HomeScreen`/`EventListItem`), Task 8 (`EventPopupDialog`). `DateTimeUtils.CountdownParts(days, hours, minutes, seconds)` used identically in Task 3, 7, 8. `AppColors`/`AppFonts` member names consistent across Task 4 definition and Tasks 6–8 usage.
