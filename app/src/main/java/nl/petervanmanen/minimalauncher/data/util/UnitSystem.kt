package nl.petervanmanen.minimalauncher.data.util

import android.content.Context
import android.icu.util.LocaleData
import android.icu.util.ULocale
import java.util.Locale
import kotlin.math.roundToInt

enum class UnitSystem { METRIC, IMPERIAL }

/** Whether the device's own locale prefers miles/feet (US, UK) or meters/km (everywhere else). */
fun currentUnitSystem(context: Context): UnitSystem {
    val locale = context.resources.configuration.locales[0] ?: Locale.getDefault()
    return runCatching {
        when (LocaleData.getMeasurementSystem(ULocale.forLocale(locale))) {
            LocaleData.MeasurementSystem.US, LocaleData.MeasurementSystem.UK -> UnitSystem.IMPERIAL
            else -> UnitSystem.METRIC
        }
    }.getOrDefault(UnitSystem.METRIC)
}

/** A short human-readable label for a distance, in whichever [unitSystem] applies. */
fun formatDistance(meters: Float, unitSystem: UnitSystem): String = when (unitSystem) {
    UnitSystem.METRIC -> if (meters >= 1000f) {
        "${formatOneDecimal(meters / 1000f)} km"
    } else {
        "${meters.roundToInt()} m"
    }
    UnitSystem.IMPERIAL -> {
        val feet = meters * 3.28084f
        if (feet >= 1000f) "${formatOneDecimal(feet / 5280f)} mi" else "${feet.roundToInt()} ft"
    }
}

private fun formatOneDecimal(value: Float): String {
    val rounded = (value * 10).roundToInt()
    return "${rounded / 10}.${rounded % 10}"
}
