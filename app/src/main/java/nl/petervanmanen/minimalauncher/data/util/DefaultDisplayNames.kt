package nl.petervanmanen.minimalauncher.data.util

/**
 * Friendlier default names for well-known apps, applied when an app is first
 * added to the dock. The user can still override these afterwards.
 */
private val DEFAULT_NAMES = mapOf(
    "com.spotify.music" to "Music",
    "com.google.android.apps.maps" to "Navigation",
)

fun defaultDisplayNameFor(packageName: String, fallbackLabel: String): String =
    DEFAULT_NAMES[packageName] ?: fallbackLabel
