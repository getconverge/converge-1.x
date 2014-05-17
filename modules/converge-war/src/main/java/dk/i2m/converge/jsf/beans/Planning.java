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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.calendar.Event;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.dto.EditionAssignmentView;
import dk.i2m.converge.core.dto.EditionView;
import dk.i2m.converge.core.dto.OutletActionView;
import dk.i2m.converge.core.logging.LogEntry;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.ejb.facades.*;
import dk.i2m.converge.jsf.components.tags.DialogAssignment;
import dk.i2m.converge.jsf.components.tags.DialogEventSelection;
import dk.i2m.converge.jsf.components.tags.UIEvent;
import dk.i2m.converge.jsf.components.tags.UIEventListener;
import dk.i2m.converge.utils.CalendarUtils;
import dk.i2m.jsf.JsfUtils;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for the planning view.
 *
 * @author Allan Lykke Christensen
 */
public class Planning implements UIEventListener {

    private static final Logger LOG = Logger.getLogger(Planning.class.getName());

    @EJB private OutletFacadeLocal outletFacade;

    @EJB private UserFacadeLocal userFacade;

    @EJB private CalendarFacadeLocal calendarFacade;

    @EJB private NewsItemFacadeLocal newsItemFacade;

    @EJB private WorkflowFacadeLocal workflowFacade;

    @EJB private SystemFacadeLocal systemFacade;

    private DialogAssignment dialogAssignment;

    private DialogEventSelection dialogEventSelection = null;

    private Outlet selectedOutlet;

    private Department selectedDepartment;

    private WorkflowState selectedState;

    private NewsItemPlacement selectedNewsItemPlacement;

    private Date selectedDate;

    private List<EditionView> selectedEditions;

    private List<OutletActionView> selectedOutletActions;

    private List<OutletActionView> selectedPlacementActions;

    private Edition selectedEdition;

    private EditionView selectedEditionView;

    private Date selectedCalendarDate;

    private Calendar useExistingEventDate;

    private Event selectedEventFromCalendar;

    private Outlet moveOutlet;

    private NewsItem selectedAssignment;

    private DataModel pipeline = new ListDataModel();

    private List<UserAccount> selectedUsers;

    private DataModel openAssignments = null;

    private DataModel logEntries = new ListDataModel();
    
    private ResourceBundle bundle = JsfUtils.getResourceBundle(Bundle.i18n.name());

    /**
     * Creates a new instance of {@link Planning}.
     */
    public Planning() {
    }

    @PostConstruct
    public void onInit() {
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeZone(getUser().getTimeZone());
        
        int workDay = getUser().getDefaultWorkDay();
        
        startDate.add(Calendar.DAY_OF_MONTH, workDay);
        selectedDate = startDate.getTime();

        useExistingEventDate = Calendar.getInstance();

        selectedOutlet = getUser().getDefaultOutlet();

        if (selectedOutlet == null) {
            if (!getOutlets().isEmpty()) {
                selectedOutlet = getOutlets().iterator().next();
            }
        }

        fetchEditions();
    }

    /**
     * Event handler for when an {@link Outlet} is selected.
     *
     * @param event
     * Event that invoked the handler
     */
    public void onSelectOutlet(ActionEvent event) {
        fetchEditions();
    }

    /**
     * Event handler for when a date is selected from the calendar.
     *
     * @param event
     * Event that invoked the handler
     */
    public void onSelectDate(ActionEvent event) {
        fetchEditions();
    }

    public void onNewEdition(ActionEvent event) {
        if (isOutletSelected() && isDateSelected()) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTimeZone(getUser().getTimeZone());
            startDate.setTime(selectedDate);
            startDate.set(Calendar.HOUR_OF_DAY, 0);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);

            Calendar endDate = Calendar.getInstance();
            endDate.setTimeZone(getUser().getTimeZone());
            endDate.setTime(selectedDate);
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);

//            selectedEdition = new Edition();
//            selectedEdition.setOutlet(selectedOutlet);
//            selectedEdition.setPublicationDate(startDate);
//            selectedEdition.setExpirationDate(endDate);
//            selectedEdition.setOpen(true);

            selectedEditionView = new EditionView(null, selectedOutlet.getId(),
                    selectedOutlet.getTitle(), true, startDate, endDate, null);
        }
    }

    public void setCreateEdition(EditionView edition) {
        if (edition.getId() == null) {
            selectedEdition = outletFacade.createEdition(edition.getOutletId(),
                    edition.isOpen(), edition.getPublicationDate(), edition.
                    getExpirationDate(), edition.getCloseDate());
            fetchEditions();
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Planning_EDITION_SAVED");
        } else {
            LOG.warning("Selected edition already exist in the database");
        }
    }

    public void setDeletePlacement(Long id) {
        if (id != null) {
            newsItemFacade.deletePlacementById(id);
            fetchEditions();
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Planning_PLACEMENT_REMOVED");
        }
    }

    public void onDeleteEdition(ActionEvent event) {
        outletFacade.deleteEdition(selectedEditionView.getId());
        fetchEditions();
    }

    public void onSaveEdition(ActionEvent event) {
        if (isEditionAddMode()) {
            outletFacade.createEdition(selectedEditionView.getOutletId(),
                    selectedEditionView.isOpen(), selectedEditionView.
                    getPublicationDate(),
                    selectedEditionView.getExpirationDate(),
                    selectedEditionView.getCloseDate());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Planning_EDITION_SAVED");
        } else {
            try {
                selectedEdition =
                        outletFacade.updateEdition(selectedEditionView.getId(),
                        selectedEditionView.isOpen(), selectedEditionView.
                        getPublicationDate(), selectedEditionView.
                        getExpirationDate(), selectedEditionView.getCloseDate());
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                        "Planning_EDITION_UPDATED");
            } catch (DataNotFoundException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                        "Planning_EDITION_NO_LONGER_EXIST");
            }
        }

        fetchEditions();
    }

    /**
     * Event handler for scheduling the execution of
     * edition actions.
     *
     * @param event Event that invoked the handler
     */
    public void onExecuteAllActions(ActionEvent event) {
        if (selectedEditionView != null) {
            outletFacade.scheduleActions(selectedEditionView.getId());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Planning_EDITION_ACTIONS_SCHEDULED");
        }
    }

    /**
     * Event handler for scheduling the execution of all
     * edition actions for the {@link #getSelectedNewsItemPlacement() }.
     *
     * @param event Event that invoked the handler
     */
    public void onExecuteAllPlacementActions(ActionEvent event) {
        if (selectedNewsItemPlacement != null) {
            outletFacade.scheduleNewsItemPlacementActions(selectedNewsItemPlacement.
                    getEdition().getId(), selectedNewsItemPlacement.getId());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Editions_ACTIONS_SCHEDULED_FOR_NEWS_ITEM", null);
        }
    }

    /**
     * Schedules the execution of the given edition action on the
     * {@link #getSelectedNewsItemPlacement()}.
     *
     * @param action {@link OutletEditionAction} to execute
     */
    public void setExecutePlacementAction(Long id) {
        if (selectedNewsItemPlacement != null && id != null) {
            outletFacade.scheduleNewsItemPlacementAction(
                    selectedNewsItemPlacement.getEdition().
                    getId(), id, selectedNewsItemPlacement.getId());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Editions_ACTION_SCHEDULED_FOR_NEWS_ITEM", null);
        }
    }

    /**
     * Schedules the execution of the given edition action.
     *
     * @param id
     *          Unique identifier of the {@link OutletEditionAction} to execute
     */
    public void setExecuteAction(Long id) {
        if (selectedEditionView != null && id != null) {
            outletFacade.scheduleAction(selectedEditionView.getId(), id);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Planning_EDITION_ACTION_SCHEDULED");
        }
    }

    public void onNewEditionPlacement(ActionEvent event) {
        onNewAssignment(event);
        //TODO (2010/10/08: dialogAssignment.getAssignment().setEdition(selectedEdition);
        dialogAssignment.onChangeEdition(null);
//        dialogAssignment.getAssignment().setSection(selectedEditionSection);
    }

    public void onSaveEditionPlacement(ActionEvent event) {
        if (isAssignmentAddMode()) {
            try {
                selectedAssignment = newsItemFacade.start(selectedAssignment);
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                        "Planning_EDITION_PLACEMENT_CREATED");
            } catch (WorkflowStateTransitionException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                selectedAssignment = newsItemFacade.checkin(selectedAssignment);
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                        "Planning_EDITION_PLACEMENT_UPDATED");
            } catch (LockingException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.getMessage()});
            }
        }
        fetchEditions();

    }

    public void onDeleteEditionPlacement(ActionEvent event) {
        if (selectedAssignment.getActors().isEmpty()) {
            newsItemFacade.deleteNewsItem(selectedAssignment.getId());
        } else {
            // TODO (2010/10/08)
//            selectedAssignment.setEdition(null);
//            selectedAssignment.setSection(null);
//            selectedAssignment.setPosition(0);
//            selectedAssignment.setStart(0);
            try {
                selectedAssignment = newsItemFacade.checkin(selectedAssignment);
            } catch (LockingException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.getMessage()});
            }
        }

        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                "Planning_EDITION_PLACEMENT_DELETED");
        fetchEditions();
    }

    /**
     * Gets a {@link List} of the {@link Outlet}s where the current user has
     * outlet planning privileges.
     *
     * @return {@link List} of the {@link Outlet}s where the current user has
     * outlet planning privileges
     */
    public List<Outlet> getOutlets() {
        UserAccount currentUser = (UserAccount) JsfUtils.
                getValueOfValueExpression("#{userSession.user}");
        return currentUser.getPrivilegedOutlets(SystemPrivilege.OUTLET_PLANNING);
    }

    /**
     * Gets a {@link Map} of the {@link Outlet}s where the current user has
     * outlet planning privileges.
     *
     * @return {@link Map} of the {@link Outlet}s where the current user has
     * outlet planning privileges
     */
    public Map<String, Outlet> getOutletsMap() {
        Map<String, Outlet> outlets = new LinkedHashMap<String, Outlet>();
        UserAccount currentUser = (UserAccount) JsfUtils.
                getValueOfValueExpression("#{userSession.user}");
        for (Outlet outlet : currentUser.getPrivilegedOutlets(
                SystemPrivilege.OUTLET_PLANNING)) {
            outlets.put(outlet.getTitle(), outlet);
        }
        return outlets;
    }

    /**
     * Gets a {@link Map} of potential authors for a {@link NewsItem} of the
     * selected outlet.
     *
     * @return {@link Map} of {@link UserAccount}s that are potential authors
     * of a new {@link NewsItem} for the selected outlet
     */
    public Map<String, UserAccount> getAuthors() {
        Map<String, UserAccount> authors =
                new LinkedHashMap<String, UserAccount>();

        WorkflowState start = selectedOutlet.getWorkflow().getStartState();

        List<UserAccount> potentialAuthors = userFacade.getMembers(start.
                getActorRole());

        for (UserAccount acc : potentialAuthors) {
            authors.put(acc.getFullName(), acc);
        }

        return authors;
    }

    public Map<String, UserAccount> getOutletDepartmentEditors() {
        Map<String, UserAccount> editors =
                new LinkedHashMap<String, UserAccount>();

        if (selectedAssignment.getDepartment() != null) {
            List<UserAccount> members =
                    userFacade.getMembers(selectedAssignment.getDepartment().
                    getId());

            for (UserAccount acc : members) {
                editors.put(acc.getFullName(), acc);
            }
        }

        return editors;
    }

    public Outlet getSelectedOutlet() {
        return selectedOutlet;
    }

    public void setSelectedOutlet(Outlet selectedOutlet) {
        this.selectedOutlet = selectedOutlet;
        this.openAssignments = null;
        onSelectOutlet(null);
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public List<EditionView> getSelectedEditions() {
        return selectedEditions;
    }

    public void setSelectedEditions(List<EditionView> selectedEditions) {
        this.selectedEditions = selectedEditions;
    }

    public Edition getSelectedEdition() {
        return selectedEdition;
    }

    public void setSelectedEdition(Edition selectedEdition) {
        this.selectedEdition = selectedEdition;
    }

    public boolean isEditionEditMode() {
        if (selectedEditionView == null || selectedEditionView.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isEditionAddMode() {
        return !isEditionEditMode();
    }

    /**
     * Determines if there is an {@link Edition} available for the selected
     * {@link Outlet} and date.
     *
     * @return <
     * code>true</code> if there is an {@link Edition} available for
     * the selected {@link Outlet} and date, otherwise
     * <code>false</code>
     */
    public boolean isEditionAvailable() {
        if (selectedEditions == null || selectedEditions.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if a date has been selected.
     *
     * @return <
     * code>true</code> if a date has been selected, otherwise
     * <code>false</code>
     */
    public boolean isDateSelected() {
        if (selectedDate == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if an {@link Outlet} has been selected.
     *
     * @return <
     * code>true</code> if an {@link Outlet} has been selected,
     * otherwise
     * <code>false</code>
     */
    public boolean isOutletSelected() {
        if (selectedOutlet == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets a formatted title for the Edition bar.
     *
     * @return formatted title for the Edition bar
     */
    public String getEditionTitle() {
        if (selectedDate != null) {
            return JsfUtils.getMessage(Bundle.i18n.name(), "Planning_EDITION_TITLE", new Object[]{selectedOutlet.getTitle(), selectedDate});
        } else {
            return JsfUtils.getMessage(Bundle.i18n.name(), "Planning_EDITION_TITLE_NO_DATE", new Object[]{selectedOutlet.getTitle()});
        }
    }

    public Date getSelectedCalendarDate() {
        return selectedCalendarDate;
    }

    public void setSelectedCalendarDate(Date selectedCalendarDate) {
        this.selectedCalendarDate = selectedCalendarDate;
    }

    private UserAccount getUser() {
        return (UserAccount) JsfUtils.getValueOfValueExpression(
                "#{userSession.user}");
    }

    /**
     * Updates the changes of a {@link NewsItemPlacement}.
     *
     * @param assignment Placement to update
     */
    public void setUpdatePlacement(EditionAssignmentView assignment) {
        newsItemFacade.updatePlacement(assignment.getPlacementId(), assignment.
                getStart(), assignment.getPosition());
    }

    private void fetchEditions() {
        if (!(isOutletSelected() && isDateSelected())) {
            LOG.log(Level.FINEST, "Outlet [{0}] or date [{1}] is not selected",
                    new Object[]{selectedOutlet, selectedDate});
            return;
        }

        selectedEditions =
                outletFacade.findEditionViewsByDate(selectedOutlet.getId(),
                selectedDate, true, true);
        try {
            this.selectedOutletActions =
                    outletFacade.findOutletActions(selectedOutlet.getId());
            this.selectedPlacementActions = outletFacade.
                    findOutletPlacementActions(selectedOutlet.getId());
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown Outlet");
            this.selectedOutletActions = new ArrayList<OutletActionView>();
            this.selectedPlacementActions = new ArrayList<OutletActionView>();
        }
    }

    public DataModel getSchedule() {
        Calendar start = CalendarUtils.getFirstDayOfMonth(useExistingEventDate);
        Calendar end = CalendarUtils.getLastDayOfMonth(useExistingEventDate);
        return new ListDataModel(calendarFacade.findByDate(start, end));
    }

    public Calendar getUseExistingEventDate() {
        return useExistingEventDate;
    }

    public void setUseExistingEventDate(Calendar useExistingEventDate) {
        this.useExistingEventDate = useExistingEventDate;
    }

    public Event getSelectedEventFromCalendar() {
        return selectedEventFromCalendar;
    }

    public void setSelectedEventFromCalendar(Event selectedEventFromCalendar) {
        this.selectedEventFromCalendar = selectedEventFromCalendar;
    }

    public void onSelectEventFromCalendar(ActionEvent evet) {
        if (selectedEventFromCalendar == null) {
            return;
        }

        String title = JsfUtils.getMessage(Bundle.i18n.name(), "Planning_COVER_X_EVENT", new Object[]{ selectedEventFromCalendar.getSummary()});

        this.selectedAssignment.setTitle(title);
        this.selectedAssignment.setEvent(selectedEventFromCalendar);
    }

    public Map<String, Edition> getOutletEditionsMap() {
        Map<String, Edition> editionMap = new LinkedHashMap<String, Edition>();
        if (moveOutlet != null) {
            List<Edition> editions = outletFacade.findEditionsByStatus(true,
                    moveOutlet);

            for (Edition edition : editions) {
                editionMap.put(edition.getFriendlyName(), edition);
            }
        }
        return editionMap;
    }

    public Map<String, Section> getOutletSectionsMap() {
        Map<String, Section> activeSections =
                new LinkedHashMap<String, Section>();

        for (Section section : selectedOutlet.getSections()) {
            if (section.isActive()) {
                activeSections.put(section.getFullName(), section);
            }
        }

        return activeSections;
    }

    // ------------------------------------------------------------------------
    public void onNewAssignment(ActionEvent event) {
        onRefreshOpenAssignments(event);

        dialogAssignment = new DialogAssignment(outletFacade, workflowFacade,
                userFacade, newsItemFacade, calendarFacade, getOutlets());
        dialogAssignment.addListener(this);
        dialogAssignment.showStoryTab();
        dialogAssignment.getAssignment().setAssigned(true);
        dialogAssignment.getAssignment().setAssignedBy(getUser());
        dialogAssignment.getAssignment().setOutlet(selectedOutlet);
        dialogAssignment.onChangeOutlet(null);

        if (selectedDate != null) {
            Calendar deadline = Calendar.getInstance();
            deadline.setTime(selectedDate);

            dialogAssignment.getAssignment().setDeadline(deadline);
            dialogAssignment.getAssignment().getDeadline().setTimeZone(getUser().
                    getTimeZone());
            dialogAssignment.getAssignment().getDeadline().set(
                    Calendar.HOUR_OF_DAY, 15);
            dialogAssignment.getAssignment().getDeadline().set(Calendar.MINUTE,
                    0);
            dialogAssignment.getAssignment().getDeadline().set(Calendar.SECOND,
                    0);
        } else {
            dialogAssignment.getAssignment().setDeadline(Calendar.getInstance());
            dialogAssignment.getAssignment().getDeadline().setTimeZone(getUser().
                    getTimeZone());
            dialogAssignment.getAssignment().getDeadline().set(
                    Calendar.HOUR_OF_DAY, 15);
            dialogAssignment.getAssignment().getDeadline().set(Calendar.MINUTE,
                    0);
            dialogAssignment.getAssignment().getDeadline().set(Calendar.SECOND,
                    0);
        }
        dialogEventSelection = new DialogEventSelection(calendarFacade);
        dialogEventSelection.setAssignment(dialogAssignment.getAssignment());
    }

    public void onShowBriefing(ActionEvent event) {
        dialogAssignment = new DialogAssignment(outletFacade, workflowFacade,
                userFacade, newsItemFacade, calendarFacade, getOutlets());
        dialogAssignment.showBriefingTab();
        dialogAssignment.setAssignment(selectedAssignment);
        dialogAssignment.onChangeOutlet(null);
        dialogEventSelection = new DialogEventSelection(calendarFacade);
        dialogEventSelection.setAssignment(dialogEventSelection.getAssignment());
    }

    public void onShowAssignment(ActionEvent event) {
        dialogEventSelection = new DialogEventSelection(calendarFacade);
        dialogAssignment = new DialogAssignment(outletFacade, workflowFacade,
                userFacade, newsItemFacade, calendarFacade, getOutlets());
        dialogAssignment.showStoryTab();
        dialogAssignment.setAssignment(selectedAssignment);
        dialogAssignment.onChangeOutlet(null);
        dialogEventSelection = new DialogEventSelection(calendarFacade);
        dialogEventSelection.setAssignment(dialogAssignment.getAssignment());
    }

    public void onDeleteAssignment(ActionEvent event) {
        if (newsItemFacade.deleteNewsItem(selectedAssignment.getId())) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(), "Planning_ASSIGNMENT_DELETED");
        } else {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(), "Planning_ASSIGNMENT_COULD_NOT_BE_DELETED");
        }
        onRefreshOpenAssignments(event);
        onRefreshPipeline(event);
    }

    public NewsItem getSelectedAssignment() {
        return selectedAssignment;
    }

    public void setSelectedAssignment(NewsItem selectedAssignment) {
        this.selectedAssignment = selectedAssignment;

        try {
            NewsItemHolder nih =
                    newsItemFacade.checkout(this.selectedAssignment.getId());

            dialogEventSelection = new DialogEventSelection(calendarFacade);
            dialogAssignment = new DialogAssignment(outletFacade, workflowFacade,
                    userFacade, newsItemFacade, calendarFacade, getOutlets());
            dialogAssignment.showStoryTab();
            dialogAssignment.setNewsItemHolder(nih);
            dialogAssignment.onChangeOutlet(null);
            dialogEventSelection = new DialogEventSelection(calendarFacade);
            dialogEventSelection.setAssignment(dialogAssignment.getAssignment());
        } catch (DataNotFoundException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(), "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.getMessage()});
        }
    }

    public boolean isAssignmentEditMode() {
        if (selectedAssignment == null || selectedAssignment.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isAssignmentAddMode() {
        return !isAssignmentEditMode();
    }

    public void onRefreshOpenAssignments(ActionEvent event) {
        this.openAssignments = null;
        getOpenAssignments();
    }

    public DataModel getOpenAssignments() {
        if (openAssignments == null) {
            openAssignments = new ListDataModel(newsItemFacade.
                    findAssignmentsByOutlet(selectedOutlet));
        }
        return openAssignments;
    }

    public Map<String, UserAccount> getAssignmentOutletDepartmentEditors() {
        Map<String, UserAccount> editors =
                new LinkedHashMap<String, UserAccount>();

        if (selectedAssignment.getDepartment() != null) {
            List<UserAccount> members =
                    userFacade.getMembers(selectedAssignment.getDepartment().
                    getId());

            for (UserAccount acc : members) {
                editors.put(acc.getFullName(), acc);
            }
        }

        return editors;
    }

    public Department getSelectedDepartment() {
        return selectedDepartment;
    }

    public void setSelectedDepartment(Department selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
    }

    public WorkflowState getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(WorkflowState selectedState) {
        this.selectedState = selectedState;
    }

    public Map<String, WorkflowState> getOutletWorkflowStates() {
        Map<String, WorkflowState> workflowStates =
                new LinkedHashMap<String, WorkflowState>();

        if (selectedOutlet != null) {
            for (WorkflowState state : selectedOutlet.getWorkflow().getStates()) {
                workflowStates.put(state.getName(), state);
            }
        }

        return workflowStates;
    }

    public void onRefreshPipeline(ActionEvent event) {
        if (selectedOutlet == null) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                    "Planning_SELECT_OUTLET_ERROR");
        } else if (selectedState == null) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                    "Planning_SELECT_STATE_ERROR");
        } else {
            List<NewsItem> items = newsItemFacade.findByStateAndOutlet(
                    selectedState, selectedOutlet);

            pipeline = new ListDataModel(items);
        }
    }

    public DataModel getPipeline() {
        return pipeline;
    }

    public List<UserAccount> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<UserAccount> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public DialogAssignment getDialogAssignment() {
        return dialogAssignment;
    }

    public void setDialogAssignment(DialogAssignment dialogAssignment) {
        this.dialogAssignment = dialogAssignment;
    }

    public DialogEventSelection getDialogEventSelection() {
        if (dialogEventSelection == null) {
            dialogEventSelection = new DialogEventSelection(calendarFacade);
        }
        return dialogEventSelection;
    }

    public void setDialogEventSelection(
            DialogEventSelection dialogEventSelection) {
        this.dialogEventSelection = dialogEventSelection;
    }

    @Override
    public void onUIEvent(UIEvent event) {
        if (event.getType().equalsIgnoreCase("onsaveassignment")) {
            onRefreshOpenAssignments(null);
        }
    }

    public EditionView getSelectedEditionView() {
        return selectedEditionView;
    }

    public void setSelectedEditionView(EditionView selectedEditionView) {
        this.selectedEditionView = selectedEditionView;
    }

    public List<OutletActionView> getSelectedOutletActions() {
        return selectedOutletActions;
    }

    public void setSelectedOutletActions(
            List<OutletActionView> selectedOutletActions) {
        this.selectedOutletActions = selectedOutletActions;
    }

    /**
     * Gets a {@link List} of {@link OutletAction}s that can be executed
     * on {@link NewsItemPlacement}s.
     *
     * @return {@link List} of {@link OutletAction}s that can be executed
     * on {@link NewsItemPlacement}s.
     */
    public List<OutletActionView> getSelectedPlacementActions() {
        return selectedPlacementActions;
    }

    public NewsItemPlacement getSelectedNewsItemPlacement() {
        return selectedNewsItemPlacement;
    }

    public void setSelectedNewsItemPlacement(
            NewsItemPlacement selectedNewsItemPlacement) {
        this.selectedNewsItemPlacement = selectedNewsItemPlacement;
    }

    public void setSelectedNewsItemPlacementId(Long id) {
        try {
            this.selectedNewsItemPlacement = newsItemFacade.
                    findNewsItemPlacementById(id);
            onRefreshNewsItemLogEntries(null);
        } catch (DataNotFoundException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "Editions_NEWS_ITEM_COULD_NOT_BE_FOUND");
        }
    }

    public DataModel getLogEntries() {
        return logEntries;
    }

    public void onRefreshNewsItemLogEntries(ActionEvent event) {
        NewsItem logItem = getSelectedNewsItemPlacement().getNewsItem();
        List<LogEntry> entries = systemFacade.findLogEntries(logItem, String.
                valueOf(logItem.getId()), 0, 100);
        logEntries = new ListDataModel(entries);
    }
}
