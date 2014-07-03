/*
 * Copyright 2010 Interactive Media Management
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
package dk.i2m.converge.core.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for working with dates and times.
 *
 * @author Allan Lykke Christensen
 */
public class CalendarUtils {

    /**
     * Non-instantiable class.
     */
    private CalendarUtils() {
        throw new UnsupportedOperationException();
    }

/**
     * Determines the first date given a week number and year.
     *
     * @param weekNumber Week number
     * @param year Year
     * @return {@link Calendar} containing the first day of the week
     */
    public static Calendar findStartOfWeek(int weekNumber, int year) {
        Calendar start = Calendar.getInstance();
        start.setFirstDayOfWeek(Calendar.MONDAY);
        start.set(Calendar.YEAR, year);
        start.set(Calendar.WEEK_OF_YEAR, weekNumber);

        while (start.get(Calendar.WEEK_OF_YEAR) == weekNumber) {
            start.add(Calendar.DAY_OF_MONTH, -1);
        }
        start.add(Calendar.DAY_OF_MONTH, 1);
        return start;
    }

    /**
     * Determines the end date given a week number and year.
     *
     * @param weekNumber Week number
     * @param year Year
     * @return {@link Calendar} containing the last day of the week
     */
    public static Calendar findEndOfWeek(int weekNumber, int year) {
        Calendar end = Calendar.getInstance();
        end.setFirstDayOfWeek(Calendar.MONDAY);
        end.set(Calendar.YEAR, year);
        end.set(Calendar.WEEK_OF_YEAR, weekNumber);

        while (end.get(Calendar.WEEK_OF_YEAR) == weekNumber) {
            end.add(Calendar.DAY_OF_MONTH, 1);
        }
        end.add(Calendar.DAY_OF_MONTH, -1);
        return end;
    }

    /**
     * Creates a {@link Calendar} from a {@link Date}.
     *
     * @param date {@link Date} to convert
     * @return {@link Calendar} created from the <code>date</code>
     */
    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Gets the first day of the year based on the given {@link Calendar}.
     *
     * @param c Calendar for which to get the first day of the year
     * @return The first day of the year in the given {@link Calendar}
     */
    public static Calendar getFirstDayOfYear(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.DAY_OF_MONTH, 1);
        result.set(Calendar.MONTH, Calendar.JANUARY);
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    /**
     * Gets the last day of the year based on the given {@link Calendar}.
     *
     * @param c Calendar for which to get the last day of the year
     * @return The last day of the year in the given {@link Calendar}
     */
    public static Calendar getLastDayOfYear(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.DAY_OF_MONTH, 31);
        result.set(Calendar.MONTH, Calendar.DECEMBER);
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    /**
     * Gets the first day of the month based on the given {@link Calendar}.
     *
     * @param c Calendar for which to get the first day of the month
     * @return The first day of the month in the given {@link Calendar}
     */
    public static Calendar getFirstDayOfMonth(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.DAY_OF_MONTH, 1);
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    /**
     * Gets the last day of the month based on the given {@link Calendar}.
     *
     * @param c Calendar for which to get the last day of the month
     * @return The last day of the month in the given {@link Calendar}
     */
    public static Calendar getLastDayOfMonth(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.DAY_OF_MONTH, 31);
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    /**
     * Gets the first day of the current month
     *
     * @return The first day of the current month
     */
    public static Calendar getFirstDayOfMonth() {
        Calendar today = Calendar.getInstance();
        return getFirstDayOfMonth(today);
    }

    /**
     * Gets the last day of the current month
     *
     * @return The last day of the current month
     */
    public static Calendar getLastDayOfMonth() {
        Calendar today = Calendar.getInstance();
        return getLastDayOfMonth(today);
    }

    /**
     * Gets the first day of the week based on the given {@link Calendar}.
     *
     * @param c Calendar for which to get the first day of the week
     * @return The first day of the week in the given {@link Calendar}
     */
    public static Calendar getFirstDayOfWeek(Calendar c) {
        int week = c.get(java.util.Calendar.WEEK_OF_YEAR);
        int year = c.get(java.util.Calendar.YEAR);
        return CalendarUtils.findStartOfWeek(week, year);
    }

    /**
     * Gets the last day of the week based on the given {@link Calendar}.
     *
     * @param c Calendar for which to get the last day of the week
     * @return The last day of the week in the given {@link Calendar}
     */
    public static Calendar getLastDayOfWeek(Calendar c) {
        int week = c.get(java.util.Calendar.WEEK_OF_YEAR);
        int year = c.get(java.util.Calendar.YEAR);
        return CalendarUtils.findEndOfWeek(week, year);
    }

    /**
     * Gets the start of the day based on the given {@link Calendar}.
     *
     * @param c Calendar for which to get the start of the day
     * @return The start of the day for the given {@link Calendar}
     */
    public static Calendar getStartOfDay(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    /**
     * Gets the start of the day based on the given {@link Date}.
     *
     * @param d Date for which to get the start of the day
     * @return The start of the day for the given {@link Date}
     */
    public static Calendar getStartOfDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return getStartOfDay(c);
    }

    /**
     * Gets the end of the day based on the given {@link Calendar}.
     *
     * @param c Calendar for which to get the end of the day
     * @return The end of the day for the given {@link Calendar}
     */
    public static Calendar getEndOfDay(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.HOUR_OF_DAY, 23);
        result.set(Calendar.MINUTE, 59);
        result.set(Calendar.SECOND, 59);
        result.set(Calendar.MILLISECOND, 999);
        return result;
    }

    /**
     * Gets the end of the day based on the given {@link Date}.
     *
     * @param d Date for which to get the end of the day
     * @return The end of the day for the given {@link Date}
     */
    public static Calendar getEndOfDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return getEndOfDay(c);
    }
}
