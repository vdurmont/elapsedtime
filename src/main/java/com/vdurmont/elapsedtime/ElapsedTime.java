package com.vdurmont.elapsedtime;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ElapsedTime is an utility to generate strings that describe an elapsed time.
 * Examples:
 * - Moments ago
 * - 2 days ago
 * - 3 hours, 32 minutes and 8 seconds ago
 * - 2 months ago
 */
public class ElapsedTime {
    // Init to default config
    private static Config CONFIG = new Config();

    /**
     * Private constructor. No instance needed.
     */
    private ElapsedTime() {}

    /**
     * Returns the string representing the provided duration.
     *
     * @param durationMillis the duration to represent in milliseconds
     *
     * @return the string representing the provided duration
     */
    public static String getFromDuration(long durationMillis) {
        return getFromDuration(durationMillis, CONFIG);
    }

    /**
     * Returns the string representing the provided duration.
     *
     * @param durationMillis the duration to represent in milliseconds
     * @param config         the config to use when generating the elapsed time
     *
     * @return the string representing the provided duration
     */
    public static String getFromDuration(long durationMillis, Config config) {
        Map<TimeDivision, Long> dividedTime = divideTime(config, durationMillis);
        TimeDivision division = config.getMaximumTimeDivision();
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

        if (isBelow(config.getMinimumTimeDivision(), division)) {
            return formatString(config, "epsilon", null);
        }

        String key = value > 1 ? division.getMultipleStringKey() : division.getSingleStringKey();
        return formatString(config, key, value);
    }

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

    private static Map<TimeDivision, Long> divideTime(Config config, long duration) {
        TimeDivision division = config.getMaximumTimeDivision();
        Map<TimeDivision, Long> results = new HashMap<TimeDivision, Long>();

        while (division != null) {
            results.put(division, duration / division.getMillis());
            duration %= division.getMillis();
            division = division.getSubDivision();
        }

        return results;
    }

    private static String formatString(Config config, String key, Long value) {
        if (value == null) {
            return config.getString(key);
        }
        return config.getString(key).replaceAll("\\{num\\}", value.toString());
    }

    ////////////////////////
    // CONFIG
    ////////////////

    public static class Config {
        private String locale;
        private TimeDivision minDivision;
        private TimeDivision maxDivision;
        private Map<String, Properties> texts;

        public Config() {
            // Init the default values
            this.locale = "en";
            this.minDivision = TimeDivision.SECOND;
            this.maxDivision = TimeDivision.YEAR;

            this.texts = new HashMap<String, Properties>();
            this.loadStrings();
        }

        private void loadStrings() {
            File[] files = getStringsFiles();
            for (File file : files) {
                this.loadFile(file);
            }
        }

        private static File[] getStringsFiles() {
            String stringsDirectory = "src/main/resources/strings/";
            return new File(stringsDirectory).listFiles();
        }

        private void loadFile(File file) {
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(file));
                this.texts.put(file.getName().replace(".properties", ""), props);
            } catch (IOException e) {
                throw new RuntimeException("Unable to load strings file " + file.getAbsolutePath());
            }
        }

        public TimeDivision getMinimumTimeDivision() {
            return minDivision;
        }

        public TimeDivision getMaximumTimeDivision() {
            return maxDivision;
        }

        public String getString(String key) {
            return this.texts.get(this.locale).getProperty(key);
        }
    }

    public static enum TimeDivision {
        MILLIS(1, "millisecond", null, 750),
        SECOND(1000, "second", TimeDivision.MILLIS, 45),
        MINUTE(60 * TimeDivision.SECOND.getMillis(), "minute", TimeDivision.SECOND, 45),
        HOUR(60 * TimeDivision.MINUTE.getMillis(), "hour", TimeDivision.MINUTE, 22),
        DAY(24 * TimeDivision.HOUR.getMillis(), "day", TimeDivision.HOUR, 26),
        MONTH(30 * TimeDivision.DAY.getMillis(), "month", TimeDivision.DAY, 11), // Duration is an approximation
        YEAR(12 * TimeDivision.MONTH.getMillis(), "year", TimeDivision.MONTH, 0); // Duration is an approximation

        private static final String SUFFIX = "_ago";
        private final long millis;
        private final String stringKey;
        private final TimeDivision subDivision;
        private final long threshold;

        private TimeDivision(long millis, String stringKey, TimeDivision subDivision, long threshold) {
            this.millis = millis;
            this.stringKey = stringKey;
            this.subDivision = subDivision;
            this.threshold = threshold;
        }

        public long getMillis() {
            return millis;
        }

        public String getSingleStringKey() {
            return stringKey + SUFFIX;
        }

        public String getMultipleStringKey() {
            return stringKey + "s" + SUFFIX;
        }

        public TimeDivision getSubDivision() {
            return subDivision;
        }

        public long getThreshold() {
            return this.threshold;
        }

        public long getThresholdMillis() {
            return this.threshold * this.millis;
        }
    }
}
