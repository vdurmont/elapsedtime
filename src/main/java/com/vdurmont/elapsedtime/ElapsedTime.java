package com.vdurmont.elapsedtime;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ElapsedTime is an utility to generate strings that describe an elapsed time.
 * Examples:
 * - Moments ago
 * - 2 days ago
 * - 3 hours, 32 minutes and 8 seconds ago
 * - 2 months ago
 */
public class ElapsedTime {
    ////////////////////////
    // CONFIG
    ////////////////

    /**
     * The locale to use if no "method specific" locale is given
     */
    public static Locale defaultLocale = Locale.ENGLISH;

    /**
     * The smallest time division that can be printed. Everything below will be considered as "moments ago".
     */
    public static TimeDivision smallestTimeDivision = TimeDivision.SECOND;

    ////////////////////////
    // METHODS
    ////////////////

    /**
     * Private constructor. No instance needed.
     */
    private ElapsedTime() {}

    /**
     * Returns the string representing the duration between the provided date and the present instant.
     *
     * @param date the date (in the past)
     *
     * @return the string representing the provided duration
     * @throws java.lang.IllegalArgumentException if the date is in the future
     */
    public static String getFromDate(Date date) {
        Date now = new Date();
        long durationMillis = now.getTime() - date.getTime();
        if (durationMillis < 0) {
            throw new IllegalArgumentException("The provided date cannot be in the future.");
        }
        return getFromDurationMillis(durationMillis);
    }

    /**
     * Returns the string representing the provided duration.
     *
     * @param durationSeconds the duration to represent in seconds
     *
     * @return the string representing the provided duration
     */
    public static String getFromDurationSeconds(long durationSeconds) {
        return getFromDurationMillis(durationSeconds * 1000);
    }

    /**
     * Returns the string representing the provided duration.
     *
     * @param durationSeconds the duration to represent in seconds
     *
     * @return the string representing the provided duration
     */
    public static String getFromDurationSeconds(long durationSeconds, Locale locale) {
        return getFromDurationMillis(durationSeconds * 1000, locale);
    }

    /**
     * Returns the string representing the provided duration.
     *
     * @param durationMillis the duration to represent in milliseconds
     *
     * @return the string representing the provided duration
     */
    public static String getFromDurationMillis(long durationMillis) {
        return getFromDurationMillis(durationMillis, defaultLocale);
    }

    /**
     * Returns the string representing the provided duration.
     *
     * @param durationMillis the duration to represent in milliseconds
     * @param locale         the locale to use for this string
     *
     * @return the string representing the provided duration
     */
    public static String getFromDurationMillis(long durationMillis, Locale locale) {
        Map<TimeDivision, Long> dividedTime = divideTime(durationMillis);
        TimeDivision division = TimeDivision.YEAR;
        TimeDivision superDivision = null;
        long value = 0;
        while (division != null && value == 0) {
            value = dividedTime.get(division);
            if (value == 0) {
                superDivision = division;
                division = division.getSubDivision();
            }
        }

        if (division == null) {
            throw new RuntimeException("Unable to divide the time properly.");
        }

        // Check if we crossed the current division threshold
        if (superDivision != null && value >= division.getThreshold()) {
            division = superDivision;
            value = 1;
        }


        // Check if we crossed a threshold in the subdivision
        // We don't do it if we already increased the division, it can't happen
        TimeDivision subDivision = division.getSubDivision();
        if (division != superDivision && subDivision != null) {
            long remaining = durationMillis % division.getMillis();
            if (remaining >= subDivision.getThresholdMillis()) {
                value++;
            }
        }

        // If our time division cannot be printed, return the "epsilon" text.
        if (isBelow(smallestTimeDivision, division)) {
            return locale.getString(StringKey.EPSILON);
        }

        // Else return the singular or plural text
        if (value > 1) {
            return locale.getString(division.getPluralStringKey()).replaceAll("\\{num\\}", String.valueOf(value));
        }
        return locale.getString(division.getSingularStringKey());
    }

    // TODO move and comment
    private static boolean isBelow(TimeDivision minimumTimeDivision, TimeDivision division) {
        TimeDivision current = minimumTimeDivision.getSubDivision();
        while (current != null) {
            if (current == division) {
                return true;
            }
            current = current.getSubDivision();
        }
        return false;
    }

    /**
     * Creates a hashmap with the amount of each division that can be fit into the duration.
     * We start at the biggest division and everytime a division can fit, we decrease the remaining duration.
     *
     * Example:
     * - 275723300 millis can fit: 3 days + 4 hours + 35 minutes + 23 seconds + 300 millis
     *
     * @param duration the duration to divide
     *
     * @return the amount for each {@link TimeDivision}
     */
    private static Map<TimeDivision, Long> divideTime(long duration) {
        TimeDivision division = TimeDivision.YEAR;
        Map<TimeDivision, Long> results = new HashMap<TimeDivision, Long>();

        while (division != null) {
            results.put(division, duration / division.getMillis());
            duration %= division.getMillis();
            division = division.getSubDivision();
        }

        return results;
    }

    ////////////////////////
    // TIME DIVISIONS
    ////////////////

    /**
     * Represents the different time divisions supported by this library.
     * Also provides useful information such as:
     * - the duration of this division in millis,
     * - the string keys for plural and singular forms
     * - the subdivision
     * - a threshold that, once reach, will increase the super division amount by 1
     */
    private static enum TimeDivision {
        MILLIS(1, StringKey.MILLISECOND_AGO, StringKey.MILLISECONDS_AGO, null, 750),
        SECOND(1000, StringKey.SECOND_AGO, StringKey.SECONDS_AGO, TimeDivision.MILLIS, 45),
        MINUTE(60 * TimeDivision.SECOND.getMillis(), StringKey.MINUTE_AGO, StringKey.MINUTES_AGO, TimeDivision.SECOND, 45),
        HOUR(60 * TimeDivision.MINUTE.getMillis(), StringKey.HOUR_AGO, StringKey.HOURS_AGO, TimeDivision.MINUTE, 22),
        DAY(24 * TimeDivision.HOUR.getMillis(), StringKey.DAY_AGO, StringKey.DAYS_AGO, TimeDivision.HOUR, 26),
        MONTH(30 * TimeDivision.DAY.getMillis(), StringKey.MONTH_AGO, StringKey.MONTHS_AGO, TimeDivision.DAY, 11), // Duration is an approximation
        YEAR(12 * TimeDivision.MONTH.getMillis(), StringKey.YEAR_AGO, StringKey.YEARS_AGO, TimeDivision.MONTH, 0); // Duration is an approximation

        private final long millis;
        private final StringKey singularStringKey;
        private final StringKey pluralStringKey;
        private final TimeDivision subDivision;
        private final long threshold;

        private TimeDivision(long millis, StringKey singularStringKey, StringKey pluralStringKey, TimeDivision subDivision, long threshold) {
            this.millis = millis;
            this.singularStringKey = singularStringKey;
            this.pluralStringKey = pluralStringKey;
            this.subDivision = subDivision;
            this.threshold = threshold;
        }

        public long getMillis() {
            return millis;
        }

        public StringKey getSingularStringKey() {
            return singularStringKey;
        }

        public StringKey getPluralStringKey() {
            return pluralStringKey;
        }

        public TimeDivision getSubDivision() {
            return subDivision;
        }

        public long getThreshold() {
            return threshold;
        }

        public long getThresholdMillis() {
            return threshold * millis;
        }
    }

    ////////////////////////
    // STRINGS
    ////////////////

    /**
     * The supported languages and their associated strings.
     */
    public static enum Locale {
        ENGLISH(StringsMap.newInstance()
                .with(StringKey.EPSILON, "Moments ago")
                .with(StringKey.MILLISECOND_AGO, "1 millisecond ago")
                .with(StringKey.MILLISECONDS_AGO, "{num} milliseconds ago")
                .with(StringKey.SECOND_AGO, "1 second ago")
                .with(StringKey.SECONDS_AGO, "{num} seconds ago")
                .with(StringKey.MINUTE_AGO, "1 minute ago")
                .with(StringKey.MINUTES_AGO, "{num} minutes ago")
                .with(StringKey.HOUR_AGO, "1 hour ago")
                .with(StringKey.HOURS_AGO, "{num} hours ago")
                .with(StringKey.DAY_AGO, "1 day ago")
                .with(StringKey.DAYS_AGO, "{num} days ago")
                .with(StringKey.MONTH_AGO, "1 month ago")
                .with(StringKey.MONTHS_AGO, "{num} months ago")
                .with(StringKey.YEAR_AGO, "1 year ago")
                .with(StringKey.YEARS_AGO, "{num} years ago")),
        FRENCH(StringsMap.newInstance()
                .with(StringKey.EPSILON, "Il y a quelques instants")
                .with(StringKey.MILLISECOND_AGO, "Il y a 1 milliseconde")
                .with(StringKey.MILLISECONDS_AGO, "Il y a {num} millisecondes")
                .with(StringKey.SECOND_AGO, "Il y a 1 seconde")
                .with(StringKey.SECONDS_AGO, "Il y a {num} secondes")
                .with(StringKey.MINUTE_AGO, "Il y a 1 minute")
                .with(StringKey.MINUTES_AGO, "Il y a {num} minutes")
                .with(StringKey.HOUR_AGO, "Il y a 1 heure")
                .with(StringKey.HOURS_AGO, "Il y a {num} heures")
                .with(StringKey.DAY_AGO, "Il y a 1 jour")
                .with(StringKey.DAYS_AGO, "Il y a {num} jours")
                .with(StringKey.MONTH_AGO, "Il y a 1 mois")
                .with(StringKey.MONTHS_AGO, "Il y a {num} mois")
                .with(StringKey.YEAR_AGO, "Il y a 1 an")
                .with(StringKey.YEARS_AGO, "Il y a {num} ans"));

        private Map<StringKey, String> strings;

        private Locale(Map<StringKey, String> strings) {
            this.strings = strings;
        }

        public String getString(StringKey key) {
            return this.strings.get(key);
        }
    }

    /**
     * The keys for the strings used in the generation
     */
    protected static enum StringKey {
        EPSILON,
        MILLISECOND_AGO, MILLISECONDS_AGO,
        SECOND_AGO, SECONDS_AGO,
        MINUTE_AGO, MINUTES_AGO,
        HOUR_AGO, HOURS_AGO,
        DAY_AGO, DAYS_AGO,
        MONTH_AGO, MONTHS_AGO,
        YEAR_AGO, YEARS_AGO
    }

    /**
     * A utility class to create a HashMap of <StringKey, String> with a fluent syntax.
     */
    private static class StringsMap extends HashMap<StringKey, String> {
        public static StringsMap newInstance() {
            return new StringsMap();
        }

        public StringsMap with(StringKey key, String value) {
            this.put(key, value);
            return this;
        }
    }
}
