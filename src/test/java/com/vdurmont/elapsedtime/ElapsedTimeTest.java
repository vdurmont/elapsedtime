package com.vdurmont.elapsedtime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class ElapsedTimeTest {
    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;
    private static final long HOUR = MINUTE * 60;
    private static final long DAY = HOUR * 24;
    private static final long MONTH = DAY * 30;
    private static final long YEAR = MONTH * 12;

    @Test public void get_with_a_duration_lower_than_1_second_returns_EPSILON_TEXT() {
        long duration = 3;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("Moments ago", result);
    }

    @Test public void get_with_a_duration_equal_to_1_second() {
        long duration = SECOND;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 second ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_second_but_lower_than_2_seconds() {
        long duration = SECOND + 500;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 second ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_second_but_lower_than_1_minute() {
        long duration = 10 * SECOND;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("10 seconds ago", result);
    }

    @Test public void get_with_a_duration_equal_to_1_minute() {
        long duration = MINUTE;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 minute ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_minute_but_lower_than_2_minutes() {
        long duration = MINUTE + 2000;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 minute ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_minute_but_lower_than_1_hour() {
        long duration = 10 * MINUTE;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("10 minutes ago", result);
    }

    @Test public void get_with_a_duration_equal_to_1_hour() {
        long duration = HOUR;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 hour ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_hour_but_lower_than_2_hours() {
        long duration = HOUR + 2000;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 hour ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_hour_but_lower_than_1_day() {
        long duration = 10 * HOUR;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("10 hours ago", result);
    }

    @Test public void get_with_a_duration_equal_to_1_day() {
        long duration = DAY;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 day ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_day_but_lower_than_2_days() {
        long duration = DAY + 2000;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 day ago", result);
    }

    @Test public void get_with_a_duration_equal_to_1_month() {
        long duration = MONTH;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 month ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_month_but_lower_than_2_months() {
        long duration = MONTH + 2000;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 month ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_month_but_lower_than_1_year() {
        long duration = 10 * MONTH;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("10 months ago", result);
    }

    @Test public void get_with_a_duration_equal_to_1_year() {
        long duration = YEAR;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 year ago", result);
    }

    @Test public void get_with_a_duration_greater_than_1_year_but_lower_than_2_years() {
        long duration = YEAR + 2000;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 year ago", result);
    }

    @Test public void get_with_a_duration_greater_than_2_years() {
        long duration = 10 * YEAR;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("10 years ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_millis_threshold() {
        long duration = 900;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 second ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_millis_threshold_2() {
        long duration = SECOND + 900;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("2 seconds ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_seconds_threshold() {
        long duration = SECOND * 46;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 minute ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_seconds_threshold_2() {
        long duration = MINUTE + SECOND * 46;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("2 minutes ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_minutes_threshold() {
        long duration = MINUTE * 46;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 hour ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_minutes_threshold_2() {
        long duration = HOUR + MINUTE * 46;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("2 hours ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_hours_threshold() {
        long duration = HOUR * 23;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 day ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_hours_threshold_2() {
        long duration = DAY + HOUR * 23;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("2 days ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_days_threshold() {
        long duration = DAY * 27;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 month ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_days_threshold_2() {
        long duration = MONTH + DAY * 27;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("2 months ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_month_threshold() {
        long duration = MONTH * 11;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("1 year ago", result);
    }

    @Test public void get_with_a_duration_greater_than_the_month_threshold_2() {
        long duration = YEAR + MONTH * 11;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("2 years ago", result);
    }

    @Test public void get_with_a_lot_of_years() {
        long duration = 900 * YEAR;
        String result = ElapsedTime.getFromDuration(duration);
        assertEquals("900 years ago", result);
    }

    @Test public void simplify_config() {
        fail();
    }

    @Test public void test_config() {
        fail();
    }
}
