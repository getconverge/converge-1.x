/*
 * Copyright (C) 2012 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.functions;

import org.apache.commons.lang.time.DurationFormatUtils;

/**
 * JSF utility functions for formatting durations.
 * <p/>
 * @author Allan Lykke Christensen
 */
public class DurationFunctions {

    /**
     * Pretty formating of a duration in milliseconds.
     * <p/>
     * @param ms Milliseconds
     * @return Pretty format of the duration
     */
    public static String formatDuration(Long ms) {
        return DurationFormatUtils.formatDurationWords(ms, true, true);
    }

    
    /**
     * Gets the number of days from milliseconds.
     * <p/>
     * @param ms Milliseconds
     * @return Number of days
     */
    public static Integer formatDays(Long ms) {
        return Integer.valueOf(DurationFormatUtils.formatDuration(ms, "d"));
    }

    /**
     * Gets the number of hours from milliseconds.
     * <p/>
     * @param ms Milliseconds
     * @return Number of hours
     */
    public static Integer formatHours(Long ms) {
        return Integer.valueOf(DurationFormatUtils.formatDuration(ms, "H"));
    }

    /**
     * Gets the number of minutes from milliseconds.
     * <p/>
     * @param ms Milliseconds
     * @return Number of minutes
     */
    public static Integer formatMinutes(Long ms) {
        return Integer.valueOf(DurationFormatUtils.formatDuration(ms, "m"));
    }

    /**
     * Gets the number of seconds from milliseconds.
     * <p/>
     * @param ms Milliseconds
     * @return Number of seconds
     */
    public static Integer formatSeconds(Long ms) {
        return Integer.valueOf(DurationFormatUtils.formatDuration(ms, "s"));
    }
}
