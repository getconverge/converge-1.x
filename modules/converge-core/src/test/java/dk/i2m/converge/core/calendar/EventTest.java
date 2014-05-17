/*
 * Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.core.calendar;

import dk.i2m.converge.core.calendar.Event;
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
public class EventTest {

    public EventTest() {
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

    @Test
    public void testSameDayDifferentDays() {
        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.roll(Calendar.DAY_OF_MONTH, true);

        Event event = new Event();
        event.setStartDate(today);
        event.setEndDate(tomorrow);

        assertFalse("Same day should be false", event.isStartAndEndSameDay());

    }

    @Test
    public void testSameDaySameDay() {
        Calendar today1 = Calendar.getInstance();
        Calendar today2 = Calendar.getInstance();

        Event event = new Event();
        event.setStartDate(today1);
        event.setEndDate(today2);

        assertTrue("Same day should be true", event.isStartAndEndSameDay());
    }
}