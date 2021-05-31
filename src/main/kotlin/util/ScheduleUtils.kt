package util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.*

object ScheduleUtils {
    private val weekFields = WeekFields.of(Locale.US)
    private val bellTime = listOf(
        TimePeriod(
            start = Time(0, 0),
            end = Time(0, 0)
        ),
        TimePeriod(
            start = Time(8, 30),
            end = Time(10, 0)
        ),
        TimePeriod(
            start = Time(10, 10),
            end = Time(11, 40)
        ),
        TimePeriod(
            start = Time(12, 0),
            end = Time(13, 30)
        ),
        TimePeriod(
            start = Time(13, 40),
            end = Time(15, 10)
        ),
        TimePeriod(
            start = Time(15, 20),
            end = Time(16, 50)
        ),
        TimePeriod(
            start = Time(17, 0),
            end = Time(18, 30)
        ),
        TimePeriod(
            start = Time(18, 40),
            end = Time(20, 10)
        ),
        TimePeriod(
            start = Time(20, 15),
            end = Time(21, 45)
        )
    )

    fun getScheduleTime(year: Int, week: Int): Long {
        val localDate = LocalDate.now()
            .withYear(year)
            .with(weekFields.weekOfYear(), week.toLong())
            // Нужно указать начало недели - понедельник, но для захардкоженой локали US
            // начало недели это воскресенье, поэтому не 1, а 2
            .with(weekFields.dayOfWeek(), 2)
        return localDate.toEpochDay() * 24 * 60 * 60 * 1000L
    }

    fun getStartDate(dayOfWeek: Int, number: Int, year: Int, week: Int): LocalDateTime {
        return LocalDateTime.now()
            .withYear(year)
            .with(weekFields.weekOfYear(), week.toLong())
            // Нужно указать начало недели - понедельник, но для захардкоженой локали US
            // начало недели это воскресенье, поэтому нужно прибавить 1
            .with(weekFields.dayOfWeek(), dayOfWeek.toLong() + 1)
            .withHour(bellTime[number].start.hour)
            .withMinute(bellTime[number].start.minute)
            .withSecond(0)
            .withNano(0)
    }

    fun getEndDate(dayOfWeek: Int, number: Int, year: Int, week: Int): LocalDateTime {
        return LocalDateTime.now()
            .withYear(year)
            .with(weekFields.weekOfYear(), week.toLong())
            // Нужно указать начало недели - понедельник, но для захардкоженой локали US
            // начало недели это воскресенье, поэтому нужно прибавить 1
            .with(weekFields.dayOfWeek(), dayOfWeek.toLong() + 1)
            .withHour(bellTime[number].end.hour)
            .withMinute(bellTime[number].end.minute)
            .withSecond(0)
            .withNano(0)
    }

    data class TimePeriod(val start: Time, val end: Time)
    data class Time(val hour: Int, val minute: Int)
}