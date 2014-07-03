/*
 *  Copyright (C) 2010 - 2014 Allan Lykke Christensen
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
package dk.i2m.converge.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

public class CalendarUtilsTest {

    @Test
    public void calendarUtils_uninstantiable() {
        try {
            // Arrange
            Constructor<CalendarUtils> constructor = CalendarUtils.class.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);

            // Act
            CalendarUtils l = constructor.newInstance(new Object[0]);

            // Assert
        } catch (NoSuchMethodException ex) {
            fail(ex.getMessage());
        } catch (InstantiationException ex) {
            fail(ex.getMessage());
        } catch (IllegalAccessException ex) {
            fail(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            fail(ex.getMessage());
        } catch (InvocationTargetException ex) {
            //Expected - as the class is uninstantiable
            return;
        }
        fail("CalendarUtils was instantiable");
    }

    @Test
    public void calendarUtils_findStartOfWeek_startOfWeekReturned() {
        // Arrange
        Calendar c = Calendar.getInstance();
        c.set(2010, Calendar.MAY, 31, 0, 0);
        Calendar expResult = Calendar.getInstance();
        expResult.set(2010, Calendar.MAY, 31, 0, 0);

        // Act
        Calendar result = CalendarUtils.getFirstDayOfWeek(c);

        // Assert
        assertEquals(expResult.get(Calendar.YEAR), result.get(Calendar.YEAR));
        assertEquals(expResult.get(Calendar.MONTH), result.get(Calendar.MONTH));
        assertEquals(expResult.get(Calendar.DAY_OF_MONTH), result.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void calendarUtils_findLastDayOfWeek_lastDayOfWeekReturned() {
        // Arrange
        Calendar c = Calendar.getInstance();
        c.set(2010, Calendar.MAY, 31, 0, 0);
        Calendar expResult = Calendar.getInstance();
        expResult.set(2010, Calendar.JUNE, 6, 0, 0);

        // Act
        Calendar result = CalendarUtils.getLastDayOfWeek(c);

        // Assert
        assertEquals(expResult.get(Calendar.YEAR), result.get(Calendar.YEAR));
        assertEquals(expResult.get(Calendar.MONTH), result.get(Calendar.MONTH));
        assertEquals(expResult.get(Calendar.DAY_OF_MONTH), result.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void calendarUtils_toCalendar_dateReturned() {
        // Arrange
        Calendar expResult = Calendar.getInstance();
        Date d = expResult.getTime();

        // Act
        Calendar result = CalendarUtils.toCalendar(d);

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getFirstDayOfYear_calendarAsFirstDayOfYearReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        testInput.set(2014, Calendar.MARCH, 10);
        Calendar expResult = Calendar.getInstance();
        expResult.set(2014, Calendar.JANUARY, 1, 0, 0, 0);
        expResult.set(Calendar.MILLISECOND, 0);

        // Act
        Calendar result = CalendarUtils.getFirstDayOfYear(testInput);

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getLastDayOfYear_calendarAsLastDayOfYearReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        testInput.set(2014, Calendar.MARCH, 10);
        Calendar expResult = Calendar.getInstance();
        expResult.set(2014, Calendar.DECEMBER, 31, 00, 00, 00);
        expResult.set(Calendar.MILLISECOND, 0);

        // Act
        Calendar result = CalendarUtils.getLastDayOfYear(testInput);

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getFirstDayOfMonth_calendarAsFirstDayOfMonthReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        testInput.set(2014, Calendar.MARCH, 10);
        Calendar expResult = Calendar.getInstance();
        expResult.set(2014, Calendar.MARCH, 1, 0, 0, 0);
        expResult.set(Calendar.MILLISECOND, 0);

        // Act
        Calendar result = CalendarUtils.getFirstDayOfMonth(testInput);

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getLastDayOfMonth_calendarAsLastDayOfMonthReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        testInput.set(2014, Calendar.MARCH, 10);
        Calendar expResult = Calendar.getInstance();
        expResult.set(2014, Calendar.MARCH, 31, 00, 00, 00);
        expResult.set(Calendar.MILLISECOND, 0);

        // Act
        Calendar result = CalendarUtils.getLastDayOfMonth(testInput);

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getStartOfDay_calendarAsStartOfDayReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        testInput.set(2014, Calendar.MARCH, 10);

        Calendar expResult = (Calendar) testInput.clone();
        expResult.set(Calendar.HOUR_OF_DAY, 0);
        expResult.set(Calendar.MINUTE, 0);
        expResult.set(Calendar.SECOND, 0);
        expResult.set(Calendar.MILLISECOND, 0);

        // Act
        Calendar result = CalendarUtils.getStartOfDay(testInput);

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getEndOfDay_calendarAsEndOfDayReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        testInput.set(2014, Calendar.MARCH, 10);
        Calendar expResult = (Calendar) testInput.clone();
        expResult.set(Calendar.HOUR_OF_DAY, 23);
        expResult.set(Calendar.MINUTE, 59);
        expResult.set(Calendar.SECOND, 59);
        expResult.set(Calendar.MILLISECOND, 999);

        // Act
        Calendar result = CalendarUtils.getEndOfDay(testInput);

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getStartOfDayFromDate_calendarAsStartOfDayReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        testInput.set(2014, Calendar.MARCH, 10);

        Calendar expResult = (Calendar) testInput.clone();
        expResult.set(Calendar.HOUR_OF_DAY, 0);
        expResult.set(Calendar.MINUTE, 0);
        expResult.set(Calendar.SECOND, 0);
        expResult.set(Calendar.MILLISECOND, 0);

        // Act
        Calendar result = CalendarUtils.getStartOfDay(testInput.getTime());

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getEndOfDayFromDate_calendarAsEndOfDayReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        testInput.set(2014, Calendar.MARCH, 10);
        Calendar expResult = (Calendar) testInput.clone();
        expResult.set(Calendar.HOUR_OF_DAY, 23);
        expResult.set(Calendar.MINUTE, 59);
        expResult.set(Calendar.SECOND, 59);
        expResult.set(Calendar.MILLISECOND, 999);

        // Act
        Calendar result = CalendarUtils.getEndOfDay(testInput.getTime());

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getFirstDayOfCurrentMonth_calendarAsFirstDayOfCurrentMonthReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        Calendar expResult = CalendarUtils.getFirstDayOfMonth(testInput);

        // Act
        Calendar result = CalendarUtils.getFirstDayOfMonth();

        // Assert
        assertEquals(expResult, result);
    }

    @Test
    public void calendarUtils_getLastDayOfCurrentMonth_calendarAsLastDayOfCurrentMonthReturned() {
        // Arrange
        Calendar testInput = Calendar.getInstance();
        Calendar expResult = CalendarUtils.getLastDayOfMonth(testInput);

        // Act
        Calendar result = CalendarUtils.getLastDayOfMonth();

        // Assert
        assertEquals(expResult, result);
    }

}
