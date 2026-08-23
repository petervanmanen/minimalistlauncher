package nl.petervanmanen.minimalauncher.data.util

private const val ALARM_LOOKAHEAD_MS = 25 * 60 * 60 * 1000L // 25 hours

/**
 * [alarmTriggerAtMillis] (from `AlarmManager.getNextAlarmClock()`) if it falls
 * within the next 25 hours of [nowMillis], otherwise null — a farther-out
 * alarm is treated the same as no alarm being set at all.
 */
internal fun nextAlarmToShow(alarmTriggerAtMillis: Long?, nowMillis: Long): Long? {
    if (alarmTriggerAtMillis == null) return null
    val delta = alarmTriggerAtMillis - nowMillis
    return if (delta in 0..ALARM_LOOKAHEAD_MS) alarmTriggerAtMillis else null
}
