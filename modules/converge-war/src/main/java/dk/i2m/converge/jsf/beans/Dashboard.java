/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.calendar.Event;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.views.CurrentAssignment;
import dk.i2m.converge.ejb.facades.CalendarFacadeLocal;
import dk.i2m.converge.ejb.facades.NewsItemFacadeLocal;
import dk.i2m.jsf.JsfUtils;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /Dashboard.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Dashboard {

    @EJB private CalendarFacadeLocal calendarFacade;

    @EJB private NewsItemFacadeLocal newsItemFacade;

    private DataModel events = null;

    private DataModel assignments = null;

    private DataModel news = null;

    /**
     * Gets the {@link DataModel} containing the user assignments.
     *
     * @return {@link DataModel} containing the user assignments
     */
    public DataModel getUserAssignments() {
        if (this.assignments == null) {
            List<CurrentAssignment> ni = newsItemFacade.findCurrentAssignments(getUserAccount().getUsername());
            this.assignments = new ListDataModel(ni);
        }

        return this.assignments;
    }

    /**
     * Gets the {@link DataModel} containing upcoming events.
     *
     * @return {@link DataModel} containing upcoming events
     */
    public DataModel getEvents() {
        if (this.events == null) {
            List<Event> upcomingEvent = calendarFacade.findUpcomingEvents(5);
            this.events = new ListDataModel(upcomingEvent);
        }
        return this.events;
    }

    private UserAccount getUserAccount() {
        final String valueExpression = "#{userSession.user}";
        return (UserAccount) JsfUtils.getValueOfValueExpression(valueExpression);
    }
}
