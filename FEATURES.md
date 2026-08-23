# Requirements & regression checklist

This is the living, current specification of Minimal Launcher's behavior —
every requirement below is already implemented. Unlike [`requirements.MD`](requirements.MD)
(the original one-page brief) and [`README.md`](README.md) (a user-facing intro),
this document exists to be **tested against**: after any change, walk the
relevant sections on-device (emulator or real phone) and confirm nothing
here regressed before shipping.

Each item traces back to its origin in brackets — either the original brief
or a GitHub issue number — so history isn't lost.

**Keeping this current:** whenever a new feature request is implemented and
closed, add its acceptance criteria here in the matching section (or a new
one) before considering the work done.

## Global / visual

- [ ] Pure black background, plain white sans-serif text, throughout the
  entire app — no app icons anywhere (dock, all-apps, dashboard, context
  menus). [original]
- [ ] No colored UI chrome — the only color variation is white vs. a dimmed
  white/grey for secondary text. [original]
- [ ] Text size is uniform across a screen — no ad-hoc larger text (e.g. the
  dashboard's weather line matches the date/time, location, and
  notification text sizes, not an oversized one-off). Section icons (bell,
  weather) are sized to match their adjacent text line, not the other way
  around. [#9]

## Navigation

The app is one `HorizontalPager` of three screens, dock in the center
(`initialPage`):

- [ ] Center screen = **Dock**, the home screen.
- [ ] Swipe **right** from the dock → **Dashboard** (weather, map,
  notifications). [original, swapped per user feedback]
- [ ] Swipe **left** from the dock → **All Apps** (alphabetical list).
  [original, swapped per user feedback]
- [ ] From either Dashboard or All Apps, swiping **up** from the bottom of
  the screen returns to the Dock. The gesture zone sits above Android's
  non-excludable bottom system-gesture strip, so it doesn't fight the
  system "go home" gesture. [feedback]
- [ ] On the All Apps screen specifically, the swipe-up-to-dock zone doesn't
  interfere with normal list scrolling (it's a fixed strip below the list,
  not an overlay).

## Dock

- [ ] The dock can have multiple **vertical** pages, swiped up/down.
  [original]
- [ ] A column of dots on the right edge shows which dock page is current.
  [original — design reference]
- [ ] Apps are shown as plain text, left-aligned, no icons. [original]
- [ ] Tapping an app in the dock launches it.
- [ ] An empty dock page shows "Long-press to add an app" instead of being
  blank.
- [ ] Long-pressing empty space on a dock page opens the **Edit dock**
  overlay. [original]

### Edit dock overlay

- [ ] **Rename**: tapping an app's name in the editor makes it editable
  in-place; committing (Done / focus-loss) with a non-blank, changed value
  renames it. Renaming is dock-only — the All Apps list always shows the
  real system label. [original]
- [ ] Default display names are applied the first time an app is added to
  the dock: Spotify → "Music", Google Maps → "Navigation". Any other app
  keeps its normal label. Still user-editable afterwards. [original]
- [ ] **Remove**: tapping "−" on a row removes that app from the dock page.
  [original]
- [ ] **Reorder**: dragging the "≡" handle on a row reorders apps within the
  current page. [original]
- [ ] **Add app**: "+ Add app" opens the same alphabetical app picker used
  elsewhere, filtered to exclude apps already in the dock; selecting one
  adds it to the current page.
- [ ] **New page**: "+ New page" appends a new empty dock page.
- [ ] **Remove this page** only appears when the current page is empty AND
  there is more than one page (can't delete the last remaining page, and
  won't silently drop apps).
- [ ] The page-dot indicator is present in the editor too, matching the
  read-only dock.
- [ ] "×" or tapping outside the dialog exits edit mode (and closes the app
  picker if it was open).
- [ ] A "Settings" link sits next to "×" in the editor header and opens the
  Settings screen (see below). [#5]

### Settings

- [ ] **Rotate with device** toggle, default **Off**. [#5]
- [ ] With it Off, the app is locked to portrait regardless of physical
  device rotation (both at cold start via the manifest, and while the app
  is running via `requestedOrientation`). [#5]
- [ ] Turning it On immediately allows the app to follow the device's
  sensor orientation, live, without restarting the app. [#5]
- [ ] Turning it back Off immediately re-locks to portrait. [#5]
- [ ] The choice persists across app restarts (DataStore).
- [ ] **Set black wallpaper** action sets a pure black wallpaper (home and
  lock screen) in one tap, via `WallpaperManager` — no extra permission
  prompt, since `SET_WALLPAPER` is a normal manifest permission. Shows
  transient "…" / "Done" / "Failed" feedback next to the label, then clears
  after a couple seconds.
- [ ] **Show weather** / **Show map** / **Show date** toggles, each default
  **On**, independently show/hide their dashboard section regardless of
  location permission state. [#7]
- [ ] **Maps app** / **Weather app** / **Date app** rows show the currently
  configured app's label (or "Not set"), and tapping opens the same
  alphabetical app picker used elsewhere to change it — a second way to
  reach the same configuration as tapping/long-pressing the section
  directly on the dashboard (#1). [#7]
- [ ] All four toggles and all three app-link choices persist across app
  restarts (DataStore). [#7]

## All Apps screen

- [ ] Every launchable installed app is listed alphabetically by label, text
  only, no icons. [original]
- [ ] Tapping an app launches it.
- [ ] A persistent **A–Z index strip** sits along the trailing edge of the
  screen. [original ask for "an indicator"; restyled and made interactive
  per #6]
  - [ ] Letters with at least one matching app are shown brighter than
    letters with none.
  - [ ] Tapping or dragging on the strip scrolls the list to the nearest
    letter that actually has an app (e.g. dragging to "Q" with no
    Q-apps lands on the closest available letter instead of doing
    nothing). [#6]
  - [ ] While touching the strip, a large letter bubble appears centered
    on screen showing the exact letter under the finger, fading out on
    release. [#6]
- [ ] **Long-pressing** an app opens a context menu with up to three
  actions: Uninstall, Hide, App info. [feedback]
  - [ ] Uninstall and Hide are **omitted** for apps that cannot actually be
    removed (system apps that aren't updated — e.g. Settings must never
    offer Uninstall or Hide). App info is always available. [feedback,
    explicit Settings-app example]
  - [ ] Uninstall triggers Android's real uninstall confirmation
    (`ACTION_DELETE`); confirming there actually uninstalls the app.
  - [ ] Hide removes the app from the main alphabetical list without
    uninstalling it.
  - [ ] App info opens the system app-details screen for that package.
- [ ] Hidden apps are **not** shown inline in the main list, and do **not**
  float as a persistent header/footer. Instead, a single "Hidden apps (N)"
  row appears as the **last item**, visible only once the user scrolls all
  the way to the bottom — and only when there's at least one hidden app.
  [#3]
- [ ] Tapping "Hidden apps (N)" opens a dedicated screen listing hidden
  apps, each with an action to unhide it (returning it to the main list).
- [ ] Newly installed/uninstalled apps update the list live (package-change
  broadcast receiver), without needing to reopen the screen.

## Dashboard

Location and notification access are each optional and independently
gated — the screen degrades gracefully without either.

### Date & time

- [ ] Shows the current date as day + full month name (e.g. "23 August" or
  "August 23"), in whichever order the device's language/locale setting
  prescribes — not a short numeric date, and no year. [#9]
- [ ] Shows the current time in the device's own system format (respects
  the 12h/24h setting), independent of location permission. [#7]
- [ ] Updates live, at least once a minute. [#7]
- [ ] Hidden when the **Show date** setting is off. [#7]
- [ ] If the device's next alarm (`AlarmManager.getNextAlarmClock()`) is due
  within the next 25 hours, its time is shown on its own line below the
  date/time, next to a small hand-drawn bell icon. A farther-out alarm (or
  none set) shows nothing extra. [#8]
- [ ] Tapping launches the user's configured date app; if none is
  configured yet, it opens the app picker instead. [#7]
- [ ] Long-pressing always opens the app picker, even if one is already
  configured. [#7]

### Weather

- [ ] Hidden when the **Show weather** setting is off, regardless of
  location permission state. [#7]
- [ ] If location permission isn't granted, shows "Enable location";
  tapping requests the permission. [original]
- [ ] Once granted, fetches and shows current temperature + short
  description (e.g. "18° · Clear sky") for the device's current location.
  [original]
- [ ] A hand-drawn monochrome icon (no image assets) representing current
  conditions is shown next to the temperature: sun, moon, cloud,
  fog/mist, rain, snow, or thunderstorm, matching the actual weather
  code. [#4]
- [ ] The icon uses a day/night-appropriate variant (sun vs. moon for clear
  skies, etc.), driven by the API's day/night flag rather than device
  clock. [#4]
- [ ] The location's place name is shown beneath the temperature when
  reverse-geocoding succeeds.
- [ ] Tapping the weather section launches the user's configured weather
  app; if none is configured yet, it opens the app picker instead. [#1]
- [ ] Long-pressing the weather section always opens the app picker, even
  if one is already configured, so the choice can be changed. [#1]

### Map

- [ ] Hidden when the **Show map** setting is off, regardless of location
  permission state. [#7]
- [ ] Otherwise shown only when location permission is granted (no separate
  opt-in — same permission as weather). [original]
- [ ] A small map centered on the device's current location, sourced from
  raw OpenStreetMap tiles (no API key). [original, tile source per
  feedback]
- [ ] Inset by the same horizontal margin as the surrounding dashboard text
  (32dp), square aspect ratio — supersedes an earlier "full width" choice
  once the two turned out to conflict. [feedback, then #9]
- [ ] Rendered in **greyscale**. [feedback]
- [ ] A marker dot shows the exact current-location point on the map.
- [ ] Tapping the map launches the user's configured maps app; if none is
  configured yet, it opens the app picker instead. [#1]
- [ ] Long-pressing the map always opens the app picker, even if one is
  already configured. [#1]
- [ ] Weather and map share a single location fetch per refresh (not two
  independent GPS reads).

### App link picker (Maps / Weather / Date)

- [ ] Reachable via tap-when-unconfigured or long-press on the relevant
  dashboard section [#1], and also via the matching row in Settings [#7].
- [ ] Presents the same alphabetical, no-icon app list used elsewhere;
  selecting an app saves it as that link's target. [#1]
- [ ] The choice persists across app restarts (DataStore) and is
  independent per link (maps, weather, and date apps are configured
  separately). [#1, #7]

### Notifications

- [ ] If notification access isn't granted, shows "Enable notifications";
  tapping opens Android's notification-listener settings (can't be
  granted in-app). [original]
- [ ] Shows "No notifications" when access is granted but none are active.
- [ ] Active notifications are listed, each shown **minimized** by default:
  one line of title, up to two lines of body text, truncated with an
  ellipsis. [feedback]
- [ ] System-noise notifications (e.g. Android/System UI's own ongoing
  media-session placeholder entries) are filtered out entirely and never
  shown. [feedback — Samsung "MediaOngoingActivity" case]
- [ ] Tapping a minimized notification **expands** it: full title, full
  body text, and — if the notification carries a picture (e.g.
  BigPictureStyle) — the image, all without leaving the dashboard.
  [feedback]
- [ ] Tapping an **already-expanded** notification opens the app/content
  the notification points to (its own `contentIntent`). [feedback]
- [ ] Swiping a notification (either direction) dismisses it from the list
  and marks it as read/cleared on the system side — same effect as
  swiping it away in the system shade. [feedback]
- [ ] The notification list scrolls independently when it overflows the
  available space. [feedback]

## Persistence

- [ ] Dock configuration (pages, apps, custom display names) survives app
  restarts. [original]
- [ ] Hidden-app set survives app restarts. [feedback]
- [ ] Configured maps/weather link apps survive app restarts. [#1]
- [ ] Rotation-lock setting survives app restarts. [#5]
- [ ] All of the above is stored locally only (Jetpack DataStore) — nothing
  leaves the device except the weather/map/geocoding network calls
  themselves.

## Build & distribution

- [ ] Every push builds a debug-signed APK via GitHub Actions, no signing
  secrets required. [original]
- [ ] The APK is available as a downloadable build without requiring users
  to build from source (rolling "latest" GitHub Release, direct
  tag-based download link since it's a prerelease). [#2]
