/*
 *  Copyright (C) 2010 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Utilities for working with {@link Calendar}s.
 *
 * @author Allan Lykke Christensen
 */
public class CalendarUtils {

    /**
     * Non-instantiable class.
     */
    private CalendarUtils() {
    }

    /**
     * Determines the first date given a week number and year.
     *
     * @param weekNumber
     *          Week number
     * @param year
     *          Year
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
     * @param weekNumber
     *          Week number
     * @param year
     *          Year
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
     * @param date
     *          {@link Date} to convert
     * @return {@link Calendar} created from the <code>date</code>
     */
    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

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

    public static Calendar getFirstDayOfMonth(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.DAY_OF_MONTH, 1);
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    public static Calendar getLastDayOfMonth(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.DAY_OF_MONTH, 31);
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }
    
    public static Calendar getFirstDayOfMonth() {
        Calendar today = Calendar.getInstance();
        return getFirstDayOfMonth(today);
    }

    public static Calendar getLastDayOfMonth() {
        Calendar today = Calendar.getInstance();
        return getLastDayOfMonth(today);
    }

    public static Calendar getFirstDayOfWeek(Calendar c) {
        int week = c.get(java.util.Calendar.WEEK_OF_YEAR);
        int year = c.get(java.util.Calendar.YEAR);
        return CalendarUtils.findStartOfWeek(week, year);
    }

    public static Calendar getLastDayOfWeek(Calendar c) {
//        c.setFirstDayOfWeek(java.util.Calendar.MONDAY);
        int week = c.get(java.util.Calendar.WEEK_OF_YEAR);
        int year = c.get(java.util.Calendar.YEAR);
        return CalendarUtils.findEndOfWeek(week, year);
    }

    public static Calendar getStartOfDay(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }
    
    public static Calendar getStartOfDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return getStartOfDay(c);
    }

    public static Calendar getEndOfDay(Calendar c) {
        Calendar result = (Calendar) c.clone();
        result.set(Calendar.HOUR_OF_DAY, 23);
        result.set(Calendar.MINUTE, 59);
        result.set(Calendar.SECOND, 59);
        result.set(Calendar.MILLISECOND, 99);
        return result;
    }
    
    public static Calendar getEndOfDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return getEndOfDay(c);
    }
}
