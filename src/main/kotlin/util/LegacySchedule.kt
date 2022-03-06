package util

import model.LegacySchedule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.*

private val weekFields = WeekFields.of(Locale.US)

private data class TimePeriod(val start: Time, val end: Time)
private data class Time(val hour: Int, val minute: Int)

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

fun LegacySchedule.getStartDate(year: Int, week: Int): LocalDateTime {
    return LocalDateTime
        .now()
        .withYear(year)
        .with(weekFields.weekOfYear(), week.toLong())
        // Нужно указать начало недели - понедельник, но для захардкоженой локали US
        // начало недели это воскресенье, поэтому нужно прибавить 1
        .with(weekFields.dayOfWeek(), dayWeek.toLong() + 1)
        .withHour(bellTime[numberLesson].start.hour)
        .withMinute(bellTime[numberLesson].start.minute)
        .withSecond(0)
        .withNano(0)
}

fun LegacySchedule.getEndDate(year: Int, week: Int): LocalDateTime {
    return LocalDateTime
        .now()
        .withYear(year)
        .with(weekFields.weekOfYear(), week.toLong())
        // Нужно указать начало недели - понедельник, но для захардкоженой локали US
        // начало недели это воскресенье, поэтому нужно прибавить 1
        .with(weekFields.dayOfWeek(), dayWeek.toLong() + 1)
        .withHour(bellTime[numberLesson].end.hour)
        .withMinute(bellTime[numberLesson].end.minute)
        .withSecond(0)
        .withNano(0)
}