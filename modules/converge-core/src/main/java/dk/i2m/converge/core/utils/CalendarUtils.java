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
            start.roll(Calendar.DAY_OF_MONTH, false);
        }
        start.roll(Calendar.DAY_OF_MONTH, true);
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
            end.roll(Calendar.DAY_OF_MONTH, true);
        }
        end.roll(Calendar.DAY_OF_MONTH, false);
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
}
