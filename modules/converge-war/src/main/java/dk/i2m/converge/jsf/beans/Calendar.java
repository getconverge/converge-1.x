/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.calendar.Event;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.ejb.facades.*;
import dk.i2m.converge.jsf.components.tags.DialogAssignment;
import dk.i2m.converge.utils.CalendarUtils;
import dk.i2m.jsf.JsfUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code Calendar.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Calendar {

    enum CalendarView {

        YEAR, MONTH, WEEK, DAY, UPCOMING
    }

    @EJB private CalendarFacadeLocal calendarFacade;

    @EJB private UserFacadeLocal userFacade;

    @EJB private OutletFacadeLocal outletFacade;

    @EJB private WorkflowFacadeLocal workflowFacade;

    @EJB private NewsItemFacadeLocal newsItemFacade;

    private DialogAssignment dialogAssignment;

    private DataModel schedule = new ListDataModel(new ArrayList());

    private Event selectedEvent = null;

    private java.util.Calendar selectedDate = java.util.Calendar.getInstance();

    private String title = "";

    private CalendarView view = CalendarView.DAY;

    @PostConstruct
    public void onInit() {
        onRefreshSchedule(null);
    }

    /**
     * Action handler for refreshing the schedule.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onRefreshSchedule(ActionEvent event) {
        switch (view) {
            case YEAR:
                onShowYear(event);
                break;
            case MONTH:
                onShowMonth(event);
                break;
            case WEEK:
                onShowWeek(event);
                break;
            case DAY:
                onShowDay(event);
                break;
            case UPCOMING:
                onShowUpcoming(event);
                break;
        }
    }

    /**
     * Action handler for preparing the creation of a new event.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onNewEvent(ActionEvent event) {
        this.selectedEvent = new Event();
        this.selectedEvent.setOriginator(getUserAccount());

        if (selectedDate != null) {
            this.selectedEvent.setStartDate((java.util.Calendar) selectedDate.clone());
            this.selectedEvent.setEndDate((java.util.Calendar) selectedDate.clone());
        } else {
            this.selectedEvent.setStartDate(java.util.Calendar.getInstance());
            this.selectedEvent.setEndDate(java.util.Calendar.getInstance());
        }
    }

    public void onSelectDate(ActionEvent event) {
        onRefreshSchedule(event);
    }

    /**
     * Action handler for adding the new event to the shared calendar.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onSaveEvent(ActionEvent event) {
        String msg;
        if (isEditMode()) {
            this.selectedEvent = calendarFacade.update(selectedEvent);
            msg = "Calendar_EVENT_MSG_UPDATED";
        } else {
            this.selectedEvent = calendarFacade.create(this.selectedEvent);
            msg = "Calendar_EVENT_MSG_CREATED";
        }

        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(), msg);

        onRefreshSchedule(event);
    }

    /**
     * Action handler for deleting a selected event.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onDeleteEvent(ActionEvent event) {
            calendarFacade.delete(selectedEvent.getId());
            onRefreshSchedule(event);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(), "Calendar_EVENT_MSG_DELETED");
    }

    /**
     * Event handler for showing events in the selected year.
     * 
     * @param event
     *          Event that invoked the handler
     */
    public void onShowYear(ActionEvent event) {
        this.view = CalendarView.YEAR;
        this.title = JsfUtils.getMessage(Bundle.i18n.name(), "Calendar_BY_YEAR", new Object[]{selectedDate.getTime()});

        java.util.Calendar startDate = CalendarUtils.getFirstDayOfYear(selectedDate);
        startDate.setTimeZone(getUserAccount().getTimeZone());
        java.util.Calendar endDate = CalendarUtils.getLastDayOfYear(selectedDate);
        endDate.setTimeZone(getUserAccount().getTimeZone());

        List<Event> events = calendarFacade.findByDate(startDate, endDate);

        this.schedule.setWrappedData(events);
    }

    public void onShowMonth(ActionEvent event) {
        this.view = CalendarView.MONTH;
        this.title = JsfUtils.getResourceBundle(Bundle.i18n.name()).getString("Calendar_BY_MONTH");
        this.title = MessageFormat.format(this.title, selectedDate.getTime());

        java.util.Calendar startDate = CalendarUtils.getFirstDayOfMonth(selectedDate);
        startDate.setTimeZone(getUserAccount().getTimeZone());
        java.util.Calendar endDate = CalendarUtils.getLastDayOfMonth(selectedDate);
        endDate.setTimeZone(getUserAccount().getTimeZone());

        List<Event> events = calendarFacade.findByDate(startDate, endDate);
        this.schedule.setWrappedData(events);
    }

    public void onShowWeek(ActionEvent event) {
        this.view = CalendarView.WEEK;
        this.title = JsfUtils.getResourceBundle(Bundle.i18n.name()).getString("Calendar_BY_WEEK");

        java.util.Calendar startDate = CalendarUtils.getFirstDayOfWeek(selectedDate);
        startDate.setTimeZone(getUserAccount().getTimeZone());

        java.util.Calendar endDate = CalendarUtils.getLastDayOfWeek(selectedDate);
        endDate.setTimeZone(getUserAccount().getTimeZone());

        this.title = MessageFormat.format(this.title, startDate.getTime(), endDate.getTime());

        List<Event> events = calendarFacade.findByDate(startDate, endDate);
        this.schedule.setWrappedData(events);
    }

    public void onShowDay(ActionEvent event) {
        this.view = CalendarView.DAY;
        this.title = JsfUtils.getResourceBundle(Bundle.i18n.name()).getString("Calendar_BY_DAY");

        java.util.Calendar startDate = CalendarUtils.getStartOfDay(selectedDate);
        startDate.setTimeZone(getUserAccount().getTimeZone());
        java.util.Calendar endDate = CalendarUtils.getEndOfDay(selectedDate);
        endDate.setTimeZone(getUserAccount().getTimeZone());
        this.title = MessageFormat.format(this.title, startDate.getTime(), endDate.getTime());

        List<Event> events = calendarFacade.findByDate(startDate, endDate);
        this.schedule.setWrappedData(events);
    }

    public void onShowUpcoming(ActionEvent event) {
        this.view = CalendarView.UPCOMING;
        this.title = JsfUtils.getResourceBundle(Bundle.i18n.name()).getString("Calendar_BY_UPCOMING");
        List<Event> events = calendarFacade.findUpcoming();
        this.schedule.setWrappedData(events);
    }

    public void onNewAssignment(ActionEvent event) {
        dialogAssignment = new DialogAssignment(outletFacade, workflowFacade, userFacade, newsItemFacade, calendarFacade, getOutlets());
        dialogAssignment.showStoryTab();
        dialogAssignment.getAssignment().setAssigned(true);
        dialogAssignment.getAssignment().setAssignedBy(getUserAccount());
        dialogAssignment.getAssignment().setOutlet(getUserAccount().getDefaultOutlet());

        if (dialogAssignment.getAssignment().getOutlet() == null) {
            if (!getOutlets().isEmpty()) {
                dialogAssignment.getAssignment().setOutlet(getOutlets().iterator().next());
            }
        }

        dialogAssignment.onChangeOutlet(null);
        dialogAssignment.getAssignment().setEvent(selectedEvent);

        String msg = JsfUtils.getResourceBundle(Bundle.i18n.name()).getString("Calendar_COVER_X_EVENT");
        dialogAssignment.getAssignment().setTitle(MessageFormat.format(msg, selectedEvent.getSummary()));

        if (selectedDate != null) {
            dialogAssignment.getAssignment().setDeadline(selectedDate);
            dialogAssignment.getAssignment().getDeadline().setTimeZone(getUserAccount().getTimeZone());
            dialogAssignment.getAssignment().getDeadline().set(java.util.Calendar.HOUR_OF_DAY, 15);
            dialogAssignment.getAssignment().getDeadline().set(java.util.Calendar.MINUTE, 0);
            dialogAssignment.getAssignment().getDeadline().set(java.util.Calendar.SECOND, 0);
        } else {
            dialogAssignment.getAssignment().setDeadline(java.util.Calendar.getInstance());
            dialogAssignment.getAssignment().getDeadline().setTimeZone(getUserAccount().getTimeZone());
            dialogAssignment.getAssignment().getDeadline().set(java.util.Calendar.HOUR_OF_DAY, 15);
            dialogAssignment.getAssignment().getDeadline().set(java.util.Calendar.MINUTE, 0);
            dialogAssignment.getAssignment().getDeadline().set(java.util.Calendar.SECOND, 0);
        }
    }

    /**
     * Gets the schedule of upcoming events.
     *
     * @return {@link DataModel} containing the registered events
     */
    public DataModel getSchedule() {
        return this.schedule;
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event event) {
        this.selectedEvent = event;
    }

    public java.util.Calendar getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(java.util.Calendar selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getTitle() {
        return JsfUtils.getMessage(Bundle.i18n.name(), "Calendar_EVENTS", new Object[]{title});
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAddMode() {
        if (selectedEvent == null || selectedEvent.getId() == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEditMode() {
        return !isAddMode();
    }

    /**
     * Gets a {@link List} of the {@link Outlet}s where the current user has
     * outlet planning privileges.
     *
     * @return {@link List} of the {@link Outlet}s where the current user has
     *         outlet planning privileges
     */
    public List<Outlet> getOutlets() {
        UserAccount currentUser = (UserAccount) JsfUtils.getValueOfValueExpression("#{userSession.user}");
        return currentUser.getPrivilegedOutlets(SystemPrivilege.OUTLET_PLANNING);
    }

    public DialogAssignment getDialogAssignment() {
        return dialogAssignment;
    }

    public void setDialogAssignment(DialogAssignment dialogAssignment) {
        this.dialogAssignment = dialogAssignment;
    }

    private UserAccount getUserAccount() {
        final String valueExpression = "#{userSession.user}";
        return (UserAccount) JsfUtils.getValueOfValueExpression(valueExpression);
    }
}
