package util

import org.junit.jupiter.api.Test

class ScheduleUtilsTest {
    @Test
    fun scheduleTimestamp() {
        val generated = getScheduleTime(2021, 23)
        val hardcoded = 1622419200000L
        println("Generated: $generated")
        println("Hardcoded: $hardcoded")
        assert(generated == hardcoded)
    }
}