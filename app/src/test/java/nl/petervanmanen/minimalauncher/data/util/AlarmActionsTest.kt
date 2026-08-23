package nl.petervanmanen.minimalauncher.data.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlarmActionsTest {

    private val now = 1_000_000_000_000L
    private val hour = 60 * 60 * 1000L

    @Test
    fun `no alarm set returns null`() {
        assertNull(nextAlarmToShow(null, now))
    }

    @Test
    fun `alarm one hour away is shown`() {
        assertEquals(now + hour, nextAlarmToShow(now + hour, now))
    }

    @Test
    fun `alarm exactly 25 hours away is shown`() {
        assertEquals(now + 25 * hour, nextAlarmToShow(now + 25 * hour, now))
    }

    @Test
    fun `alarm more than 25 hours away is treated as not set`() {
        assertNull(nextAlarmToShow(now + 25 * hour + 1, now))
    }

    @Test
    fun `alarm due right now is shown`() {
        assertEquals(now, nextAlarmToShow(now, now))
    }

    @Test
    fun `alarm in the past is treated as not set`() {
        assertNull(nextAlarmToShow(now - hour, now))
    }
}
