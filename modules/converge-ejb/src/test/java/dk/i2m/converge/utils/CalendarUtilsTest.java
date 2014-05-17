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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Allan Lykke Christensen
 */
public class CalendarUtilsTest {

    public CalendarUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of findStartOfWeek method, of class CalendarUtils.
     */
    @Test
    public void testFindStartOfWeek() {
        Calendar c = Calendar.getInstance();
        c.set(2010, Calendar.MAY, 31, 0, 0);

        Calendar expResult = Calendar.getInstance();
        expResult.set(2010, Calendar.MAY, 31, 0, 0);

        Calendar result = CalendarUtils.getFirstDayOfWeek(c);
        assertEquals(expResult.get(Calendar.YEAR), result.get(Calendar.YEAR));
        assertEquals(expResult.get(Calendar.MONTH), result.get(Calendar.MONTH));
        assertEquals(expResult.get(Calendar.DAY_OF_MONTH), result.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Test of getLastDayOfWeek method, of class CalendarUtils.
     */
    @Test
    public void testGetLastDayOfWeek() {
        Calendar c = Calendar.getInstance();
        c.set(2010, Calendar.MAY, 31, 0, 0);

        Calendar expResult = Calendar.getInstance();
        expResult.set(2010, Calendar.JUNE, 6, 0, 0);

        Calendar result = CalendarUtils.getLastDayOfWeek(c);
        assertEquals(expResult.get(Calendar.YEAR), result.get(Calendar.YEAR));
        assertEquals(expResult.get(Calendar.MONTH), result.get(Calendar.MONTH));
        assertEquals(expResult.get(Calendar.DAY_OF_MONTH), result.get(Calendar.DAY_OF_MONTH));
    }
}
