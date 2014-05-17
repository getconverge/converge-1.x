/*
 *  Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.jsf.components.tags;

import dk.i2m.converge.core.calendar.Event;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.ejb.facades.CalendarFacadeLocal;
import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.converge.utils.CalendarUtils;
import dk.i2m.jsf.JsfUtils;
import java.util.Calendar;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for the {@link dialogEventSelection} tag.
 *
 * @author Allan Lykke Christensen
 */
public class DialogEventSelection {

    private CalendarFacadeLocal calendarFacade;

    private Calendar calendarDate = Calendar.getInstance();

    private Event event;

    private NewsItem assignment;

    public DialogEventSelection(CalendarFacadeLocal calendarFacade) {
        this.calendarFacade = calendarFacade;
    }

    public void onCalendarThisMonth(ActionEvent event) {
        calendarDate = Calendar.getInstance();
    }

    public void onCalendarPrevMonth(ActionEvent event) {
        calendarDate.add(Calendar.MONTH, -1);
    }

    public void onCalendarNextMonth(ActionEvent event) {
        calendarDate.add(Calendar.MONTH, 1);
    }

    public DataModel getSchedule() {
        Calendar start = CalendarUtils.getFirstDayOfMonth(calendarDate);
        Calendar end = CalendarUtils.getLastDayOfMonth(calendarDate);
        return new ListDataModel(calendarFacade.findByDate(start, end));
    }

    public void onSelectEvent(ActionEvent evet) {
        if (event == null) {
            return;
        }

        if (assignment.getTitle() == null || assignment.getTitle().isEmpty()) {
            
            String title = JsfUtils.getMessage(Bundle.i18n.name(), 
                    "DialogEventSelection_COVER_X_EVENT", 
                    new Object[]{event.getSummary()});

            assignment.setTitle(title);
        }
        assignment.setEvent(event);
    }

    public NewsItem getAssignment() {
        return assignment;
    }

    public void setAssignment(NewsItem assignment) {
        this.assignment = assignment;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Calendar getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(Calendar calendarDate) {
        this.calendarDate = calendarDate;
    }
}
