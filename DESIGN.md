# Visual guidelines

This documents the app's actual, current visual language — colors, type,
spacing, and interaction patterns — as a reference for any new screen or
feature. It replaces the original `inspiration-design/` mood-board images
(removed once the app's own established look became the reference).
[`FEATURES.md`](FEATURES.md) covers *behavior*; this covers *how it should
look and feel* while doing it. Both are living documents — update this one
whenever a change establishes a new visual pattern, the same way FEATURES.md
gets updated for behavior.

## Color

Three colors, defined once in
[`ui/theme/Theme.kt`](app/src/main/java/nl/petervanmanen/minimalauncher/ui/theme/Theme.kt) —
nothing else should introduce a new color without a real reason:

| Token | Value | Use |
| --- | --- | --- |
| `PureBlack` | `#000000` | The only background, everywhere. True black, not a dark grey — this matters on OLED screens (also why the black-wallpaper setting exists). |
| `PureWhite` | `#FFFFFF` | Primary text and the "selected" / "on" state (toggles, palette swatches, active tab). |
| `DimWhite` | `#9A9A9A` | Secondary text: subtitles, unselected options, placeholder/empty states, "Off" toggle labels. |

No other colors appear in the UI chrome. The two configurable exceptions —
the notification bubble color and the map's optional color-tile mode — are
user-chosen accents layered on top of this palette, not part of it.

## Typography

One text style, `LauncherTypography.bodyLarge` in the same `Theme.kt`: 28sp,
`FontWeight.Normal`, `PureWhite`. Every piece of text in the app uses this
style (color overridden to `DimWhite` for secondary text where needed) —
**no ad-hoc font sizes**. This was a deliberate fix (#9): the dashboard's
weather line used to override to 32sp and looked out of place next to
everything else at the standard size.

## Layout

- **32dp horizontal margin** for text content on every screen (dock, all
  apps, dashboard sections, settings).
- **24dp vertical spacing** between stacked dashboard sections (date/time,
  weather, map, notifications).
- **12dp vertical padding** on list rows (dock apps, all-apps rows,
  notification rows).
- **20dp reserved leading gutter** wherever a row can carry a small status
  dot (notification bubble, in the Dock and All Apps lists): the gutter is
  always reserved, dot or not, so text lines up identically either way. The
  dot itself sits inside that gutter, closer to the true screen edge than
  the 32dp text margin — i.e. it's allowed to sit in the margin space, by
  design (#11), rather than pushed flush with the text.
- The map is inset by the same 32dp margin as surrounding text (not edge to
  edge — an earlier "full width" choice was explicitly reverted once it
  started reading as inconsistent, see #9).

## No icons — text and hand-drawn glyphs only

- **No app icons**, anywhere — dock, all-apps, context menus. Apps are
  their text label, full stop.
- **No image assets and no emoji/Unicode symbols** for UI glyphs (weather
  condition, alarm bell). Emoji risk forced color rendering that breaks the
  monochrome look regardless of theme. Instead, small glyphs are hand-drawn
  with Compose `Canvas`/`DrawScope` — see
  [`WeatherIcon.kt`](app/src/main/java/nl/petervanmanen/minimalauncher/ui/dashboard/WeatherIcon.kt)
  and
  [`AlarmIcon.kt`](app/src/main/java/nl/petervanmanen/minimalauncher/ui/dashboard/AlarmIcon.kt)
  as the reference pattern for adding another one: simple filled shapes,
  `PureWhite`, sized to match the text line it sits next to (20–24dp), never
  larger than the text as a way to draw attention.

## Interaction patterns

- **Tap** to act (launch an app, expand a notification, toggle a setting).
- **Long-press** to configure/edit — the dock (enter edit mode), a
  dashboard section (change its linked app), an all-apps row (context
  menu). This is consistent everywhere: long-press never performs the
  primary action, only ever opens configuration for it.
- **Toggle rows** (`ToggleRow` in
  [`SettingsScreen.kt`](app/src/main/java/nl/petervanmanen/minimalauncher/ui/settings/SettingsScreen.kt)):
  label on the left, "On"/"Off" on the right in `PureWhite`/`DimWhite`.
- **Value rows** (`LinkRow`): label on the left, current value in
  `DimWhite` on the right, tap to open a picker.
- **Preset pickers** (color palette, map layer): a row of tappable swatches
  or text options; the selected one is marked with a `PureWhite` ring
  (colors) or `PureWhite` text (labels), unselected ones dimmed.
- Everything reachable from **Settings** lives inside the dock's long-press
  edit overlay, behind a single "Settings" link — there's no separate
  settings entry point anywhere else.

## Screens

Three screens in one `HorizontalPager`, dock in the center. See
[`README.md`](README.md#screens) for the current screenshots and
[`FEATURES.md`](FEATURES.md) for the full behavioral checklist per screen.
