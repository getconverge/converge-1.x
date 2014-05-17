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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.workflow.Edition;
import java.util.List;
import dk.i2m.converge.core.workflow.EditionPattern;
import java.util.Calendar;
import java.util.GregorianCalendar;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.EjbTestCase;
import dk.i2m.converge.core.workflow.OutletType;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 * Unit tests for the outlet edition functionality.
 *
 * @author Allan Lykke Christensen
 */
public class OutletEditionTest extends EjbTestCase {

    public static final String BEAN_INTERFACE = "OutletFacadeBeanLocal";

    @Test
    @Ignore
    public void testObtainBean() throws Exception {
        Object object = getInitialContext().lookup(BEAN_INTERFACE);
        assertNotNull(object);
        assertTrue(object instanceof OutletFacadeLocal);
    }

    @Test
    @Ignore
    public void testSelectDateForDaily() throws Exception {
        Object object = getInitialContext().lookup(BEAN_INTERFACE);
        OutletFacadeLocal facade = (OutletFacadeLocal) object;
        Outlet outlet = facade.createOutlet(new Outlet());
        outlet.setTitle("My Times");
        outlet.setType(OutletType.PRINT);

        outlet.getEditionPatterns().add(new EditionPattern(Calendar.MONDAY, 0, 0, 23, 59, 23, 59, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.TUESDAY, 0, 0, 23, 59, 23, 59, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.WEDNESDAY, 0, 0, 23, 59, 23, 59, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.THURSDAY, 0, 0, 23, 59, 23, 59, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.FRIDAY, 0, 0, 23, 59, 23, 59, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.SATURDAY, 0, 0, 23, 59, 23, 59, outlet));

        outlet = facade.updateOutlet(outlet);

        List<Edition> fridayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 2));
        List<Edition> saturdayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 2));
        List<Edition> sundayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 3));
        List<Edition> mondayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 4));
        List<Edition> tuesdayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 5));
        List<Edition> wednesdayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 6));
        List<Edition> thursdayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 7));

        assertEquals(1, fridayEditions.size());
        assertEquals(1, saturdayEditions.size());
        assertEquals(0, sundayEditions.size());
        assertEquals(1, mondayEditions.size());
        assertEquals(1, tuesdayEditions.size());
        assertEquals(1, wednesdayEditions.size());
        assertEquals(1, thursdayEditions.size());
    }

    @Test
    @Ignore
    public void testSelectDateForRadioMultipleDaily() throws Exception {
        Object object = getInitialContext().lookup(BEAN_INTERFACE);
        OutletFacadeLocal facade = (OutletFacadeLocal) object;
        Outlet outlet = facade.createOutlet(new Outlet());
        outlet.setTitle("My Tunes");
        outlet.setType(OutletType.AUDIO);

        outlet.getEditionPatterns().add(new EditionPattern(Calendar.MONDAY, 6, 0, 6, 15, 6, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.MONDAY, 7, 0, 7, 15, 7, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.MONDAY, 8, 0, 8, 15, 8, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.MONDAY, 9, 0, 9, 15, 9, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.MONDAY, 13, 0, 13, 15, 13, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.MONDAY, 17, 0, 17, 15, 17, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.MONDAY, 18, 0, 18, 15, 18, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.MONDAY, 19, 0, 19, 15, 19, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.TUESDAY, 6, 0, 6, 15, 6, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.TUESDAY, 7, 0, 7, 15, 7, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.TUESDAY, 8, 0, 8, 15, 8, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.TUESDAY, 9, 0, 9, 15, 9, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.TUESDAY, 13, 0, 13, 15, 13, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.TUESDAY, 17, 0, 17, 15, 17, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.TUESDAY, 18, 0, 18, 15, 18, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.TUESDAY, 19, 0, 19, 15, 19, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.SATURDAY, 6, 0, 6, 15, 6, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.SATURDAY, 7, 0, 7, 15, 7, 15, outlet));
        outlet.getEditionPatterns().add(new EditionPattern(Calendar.SATURDAY, 8, 0, 8, 15, 8, 15, outlet));

        outlet = facade.updateOutlet(outlet);

        List<Edition> mondayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 4));
        List<Edition> tuesdayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 5));
        List<Edition> saturdayEditions = facade.findEditionsByDate(outlet, new GregorianCalendar(2010, Calendar.JANUARY, 9));

        assertEquals(8, mondayEditions.size());
        assertEquals(8, tuesdayEditions.size());
        assertEquals(3, saturdayEditions.size());

    }

    @Test
    @Ignore
    public void testClose() throws Exception {
        OutletFacadeLocal facade = (OutletFacadeLocal) getInitialContext().lookup(BEAN_INTERFACE);

        Outlet outlet = new Outlet();
        outlet.setTitle("Test Outlet");

        outlet = facade.createOutlet(outlet);

        Calendar tMinusOne = Calendar.getInstance();
        tMinusOne.add(Calendar.DAY_OF_MONTH, -1);

        Calendar tMinusTwo = Calendar.getInstance();
        tMinusTwo.add(Calendar.DAY_OF_MONTH, -2);

        Calendar tPlusOne = Calendar.getInstance();
        tPlusOne.add(Calendar.DAY_OF_MONTH, 1);

        Calendar tPlusTwo = Calendar.getInstance();
        tPlusTwo.add(Calendar.DAY_OF_MONTH, 2);

        Calendar tPlusThree = Calendar.getInstance();
        tPlusThree.add(Calendar.DAY_OF_MONTH, 3);

        facade.createEdition(new Edition(outlet, tMinusOne.getTime()));
        facade.createEdition(new Edition(outlet, tMinusTwo.getTime()));
        facade.createEdition(new Edition(outlet, tPlusOne.getTime()));
        facade.createEdition(new Edition(outlet, tPlusTwo.getTime()));
        facade.createEdition(new Edition(outlet, tPlusThree.getTime()));

        List<Edition> editions = facade.findEditionsByStatus(true, outlet);

        assertEquals(5, editions.size());

        int closed = facade.closeOverdueEditions();

        assertEquals(2, closed);

        editions = facade.findEditionsByStatus(true, outlet);

        assertEquals(3, editions.size());
    }
}
