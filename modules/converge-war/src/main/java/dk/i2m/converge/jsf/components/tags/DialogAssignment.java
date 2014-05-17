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

import dk.i2m.commons.BeanComparator;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.calendar.Event;
import dk.i2m.converge.core.content.ContentItemPermission;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemActor;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.ejb.facades.*;
import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.jsf.JsfUtils;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

/**
 * Backing bean for the {@code dialogAssignment} Facelets tag.
 *
 * @author Allan Lykke Christensen
 */
public class DialogAssignment {

    private static final Logger log = Logger.getLogger(DialogAssignment.class.
            getName());

    private OutletFacadeLocal outletFacade;

    private WorkflowFacadeLocal workflowFacade;

    private UserFacadeLocal userFacade;

    private NewsItemFacadeLocal newsItemFacade;

    private CalendarFacadeLocal calendarFacade;

    private String selectedTab = "tabStory";

    private UserRole selectedOutletRole;

    private UserAccount selectedUser;

    private NewsItemActor selectedActor;

    private NewsItem assignment;

    private NewsItemHolder newsItemHolder;

    private Map<String, Outlet> outlets = new LinkedHashMap<String, Outlet>();

    private Map<String, UserRole> outletRoles =
            new LinkedHashMap<String, UserRole>();

    private Map<String, UserAccount> usersInRole =
            new LinkedHashMap<String, UserAccount>();

    private List<UIEventListener> listeners = new ArrayList<UIEventListener>();

    private boolean readOnly = false;

    private ContentItemPermission permission = ContentItemPermission.USER;

    private NewsItemPlacement selectedNewsItemPlacement;

    private Map<String, EditionCandidate> editionCandidates =
            new LinkedHashMap<String, EditionCandidate>();

    private EditionCandidate editionCandidate;

    private Date editionDate;

    /**
     * Creates a new instance of {@link DialogAssignment}.
     */
    public DialogAssignment(OutletFacadeLocal outletFacade,
            WorkflowFacadeLocal workflowFacade, UserFacadeLocal userFacade,
            NewsItemFacadeLocal newsItemFacade,
            CalendarFacadeLocal calendarFacade, List<Outlet> outlets) {
        this.outletFacade = outletFacade;
        this.workflowFacade = workflowFacade;
        this.userFacade = userFacade;
        this.newsItemFacade = newsItemFacade;
        this.calendarFacade = calendarFacade;
        this.assignment = new NewsItem();

        for (Outlet outlet : outlets) {
            this.outlets.put(outlet.getTitle(), outlet);
        }
    }

    /**
     * Event handler invoked when the outlet is changed.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onChangeOutlet(ActionEvent event) {
        selectedOutletRole = null;
        selectedUser = null;
        outletRoles.clear();
        usersInRole.clear();

        Outlet assignmentOutlet = assignment.getOutlet();
        Workflow outletWorkflow = assignmentOutlet.getWorkflow();

        for (UserRole role : outletWorkflow.getUserRolesInWorkflowStates()) {
            outletRoles.put(role.getName(), role);
        }
    }

    /**
     * Event handler invoked when the outlet is changed.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onChangeEdition(ActionEvent event) {
    }

    /**
     * Event handler invoked when a new actor is added to the assignment.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onAddActor(ActionEvent event) {
        // Ensure that role and user has been selected.
        if (selectedOutletRole == null || selectedUser == null) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "DialogAssignment_SELECT_ROLE_AND_USER");
            return;
        }

        // Check that the actor is not already there
        boolean duplicate = false;
        for (NewsItemActor nia : assignment.getActors()) {
            if (nia.getRole().equals(selectedOutletRole) && nia.getUser().equals(
                    selectedUser)) {
                duplicate = true;
                break;
            }
        }

        if (!duplicate) {
            NewsItemActor actor = new NewsItemActor();
            actor.setNewsItem(assignment);
            actor.setRole(selectedOutletRole);
            actor.setUser(selectedUser);
            assignment.getActors().add(actor);
            if (isEditMode()) {
                newsItemFacade.addActorToNewsItem(actor);
            }
        }
    }

    /**
     * Event handler invoked when an existing actor is removed from an 
     * assignment.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onRemoveActor(ActionEvent event) {
        assignment.getActors().remove(selectedActor);
    }

    public void onSaveAssignment(ActionEvent event) {
        if (isAddMode()) {
            try {
                assignment = newsItemFacade.start(assignment);
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                        Bundle.i18n.name(),
                        "DialogAssignment_ASSIGNMENT_CREATED_WITH_ID_X",
                        new Object[]{assignment.getId()});
            } catch (MissingActorException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(),
                        "DialogAssignment_ASSIGNMENT_ACTOR_X_MISSING",
                        new Object[]{ex.getRole().getName()});
            } catch (WorkflowStateTransitionException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(),
                        "DialogAssignment_ASSIGNMENT_CREATION_ERROR");
            }
        } else {
            try {
                // TODO: OptimisticLockException seems to occur when updating
                assignment = newsItemFacade.checkin(assignment);
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                        Bundle.i18n.name(),
                        "DialogAssignment_ASSIGNMENT_UPDATED");
            } catch (LockingException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.
                            getMessage()});
            }
        }

        if (assignment.getEvent() != null) {
            Event assignmentEvent = assignment.getEvent();

            if (!assignmentEvent.getNewsItem().contains(assignment)) {
                assignmentEvent.getNewsItem().add(assignment);
                assignmentEvent = calendarFacade.update(assignmentEvent);
            }
        }
        notifyListeners("onSaveAssignment");
    }

    public void onApplyAssignment(ActionEvent event) {
        if (isAddMode()) {
            try {
                assignment = newsItemFacade.start(assignment);

                NewsItemHolder nih = newsItemFacade.checkout(assignment.getId());
                setNewsItemHolder(nih);

                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                        Bundle.i18n.name(),
                        "DialogAssignment_ASSIGNMENT_CREATED_WITH_ID_X",
                        new Object[]{assignment.getId()});
            } catch (DataNotFoundException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(),
                        "DialogAssignment_ASSIGNMENT_CREATION_ERROR");
            } catch (MissingActorException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(),
                        "The assignment must have an actor with the role {0}",
                        new Object[]{ex.getRole().getName()});
            } catch (WorkflowStateTransitionException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(),
                        "DialogAssignment_ASSIGNMENT_CREATION_ERROR");
            }
        } else {
            try {
                assignment = newsItemFacade.save(assignment);
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                        Bundle.i18n.name(),
                        "DialogAssignment_ASSIGNMENT_UPDATED");
            } catch (LockingException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.
                            getMessage()});
            }
        }

        if (assignment.getEvent() != null) {
            Event assignmentEvent = assignment.getEvent();

            if (!assignmentEvent.getNewsItem().contains(assignment)) {
                assignmentEvent.getNewsItem().add(assignment);
                assignmentEvent = calendarFacade.update(assignmentEvent);
                try {
                    NewsItemHolder nih = newsItemFacade.checkout(assignment.
                            getId());
                    setNewsItemHolder(nih);
                } catch (DataNotFoundException ex) {
                    JsfUtils.createMessage("frmPage",
                            FacesMessage.SEVERITY_ERROR,
                            Bundle.i18n.name(),
                            "DialogAssignment_ASSIGNMENT_CREATION_ERROR");
                }
            }
        }

        notifyListeners("onSaveAssignment");
    }

    public void onCancelAssignment(ActionEvent event) {
        if (isEditMode()) {
            newsItemFacade.revokeLock(assignment.getId());
        }
        notifyListeners("onSaveAssignment");
    }

    public void onDeleteAssignment(ActionEvent event) {
        if (newsItemFacade.deleteNewsItem(assignment.getId())) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(), "DialogAssignment_ASSIGNMENT_DELETED");
        } else {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "DialogAssignment_ASSIGNMENT_COULD_NOT_BE_DELETED");
        }
    }

    public void onRemoveEventFromAssignment(ActionEvent event) {
        assignment.setEvent(null);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(), "DialogAssignment_ASSIGNMENT_EVENT_REMOVED");
    }

    public void onNewPlacement(ActionEvent event) {
        this.selectedNewsItemPlacement = new NewsItemPlacement();
        this.selectedNewsItemPlacement.setNewsItem(assignment);
        this.selectedNewsItemPlacement.setOutlet(assignment.getOutlet());
        this.editionCandidates = new LinkedHashMap<String, EditionCandidate>();
        this.editionCandidate = null;
        this.editionDate = null;
    }

    public void onChangePlacementOutlet(ValueChangeEvent event) {
        this.selectedNewsItemPlacement.setOutlet((Outlet) event.getNewValue());
        setEditionDate(getEditionDate());
    }

    /**
     * Gets a {@link Map} of {@link Edition} candidates based on the selected edition date.
     * 
     * @return {@link Map} of {@link Edition} candidates based on the selected edition date
     */
    public Map<String, EditionCandidate> getEditionCandidates() {
        return this.editionCandidates;
    }

    public Map<String, UserRole> getOutletRoles() {
        return outletRoles;
    }

    public Map<String, UserAccount> getUsersInRole() {
        return usersInRole;
    }

    public Map<String, Outlet> getOutlets() {
        return outlets;
    }

    public Map<String, Edition> getEditions() {
        Map<String, Edition> editions = new LinkedHashMap<String, Edition>();
        if (assignment.getOutlet() != null) {
            for (Edition edition : outletFacade.findEditionsByStatus(true,
                    assignment.getOutlet())) {
                editions.put(edition.getFriendlyName(), edition);
            }
        }
        return editions;
    }

    public Map<String, Section> getEditionSections() {
        Map<String, Section> sections = new LinkedHashMap<String, Section>();
        if (assignment.getOutlet() != null) {
            for (Section section : assignment.getOutlet().getSections()) {
                if (section.isActive()) {
                    sections.put(section.getFullName(), section);
                }
            }
        }

        return sections;
    }

    public void setOutlets(Map<String, Outlet> outlets) {
        this.outlets = outlets;
    }

    public NewsItem getAssignment() {
        return assignment;
    }

    public void setAssignment(NewsItem assignment) {
        this.assignment = assignment;
    }

    public UserRole getSelectedOutletRole() {
        return selectedOutletRole;
    }

    public void setSelectedOutletRole(UserRole selectedOutletRole) {
        this.selectedOutletRole = selectedOutletRole;
        usersInRole.clear();
        if (selectedOutletRole != null) {
            for (UserAccount userAccount : userFacade.getMembers(
                    selectedOutletRole)) {
                usersInRole.put(userAccount.getFullName(), userAccount);
            }
        }
    }

    public UserAccount getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(UserAccount selectedUser) {
        this.selectedUser = selectedUser;
    }

    public NewsItemActor getSelectedActor() {
        return selectedActor;
    }

    public void setSelectedActor(NewsItemActor selectedActor) {
        this.selectedActor = selectedActor;
    }

    public void showStoryTab() {
        selectedTab = "tabStory";
    }

    public void showBriefingTab() {
        selectedTab = "tabBriefing";
    }

    public void showEventTab() {
        selectedTab = "tabEvent";
    }

    public void showPlacementTab() {
        selectedTab = "tabPlacement";
    }

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    public boolean isAddMode() {
        if (assignment == null || assignment.getId() == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEditMode() {
        return !isAddMode();
    }

    public boolean addListener(UIEventListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
            return true;
        } else {
            return false;
        }
    }

    private void notifyListeners(String string) {
        for (UIEventListener listener : listeners) {
            listener.onUIEvent(new UIEvent(string));
        }
    }

    public NewsItemHolder getNewsItemHolder() {
        return newsItemHolder;
    }

    /**
     * Determines if the current user is the current actor of the
     * {@link NewsItem}.
     *
     * @return {@code true} if the current user is among the current actors of
     *         the {@link NewsItem}, otherwise {@code false}
     */
    public boolean isCurrentActor() {
        boolean currentActor = false;

        switch (permission) {
            case USER:
            case ROLE:
                currentActor = true;
                break;

            default:
                currentActor = false;
        }
        return currentActor;
    }

    public void setNewsItemHolder(NewsItemHolder newsItemHolder) {
        this.newsItemHolder = newsItemHolder;
        this.assignment = this.newsItemHolder.getNewsItem();
        this.readOnly = this.newsItemHolder.isReadOnly();
        this.permission = this.newsItemHolder.getPermission();
    }

    public boolean isReadOnly() {
        return readOnly || (ContentItemPermission.ACTOR == permission);
    }

    public boolean isEditable() {
        return !(!isCurrentActor() || isReadOnly());
    }

    public EditionCandidate getEditionCandidate() {
        return editionCandidate;
    }

    public void setEditionCandidate(EditionCandidate editionCandidate) {
        this.editionCandidate = editionCandidate;
    }

    public Date getEditionDate() {
        return editionDate;
    }

    public void setEditionDate(Date editionDate) {
        this.editionDate = editionDate;

        this.editionCandidates = new LinkedHashMap<String, EditionCandidate>();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm zzz");

        if (editionDate != null) {

            java.util.Calendar editionCal = java.util.Calendar.getInstance();
            if (getUser().getTimeZone() != null) {
                editionCal.setTimeZone(getUser().getTimeZone());
            }
            editionCal.setTime(editionDate);

            List<EditionCandidate> editions = outletFacade.
                    findEditionCandidatesByDate(getSelectedNewsItemPlacement().
                    getOutlet(), editionCal, false);
            Collections.sort(editions, new BeanComparator("publicationDate"));
            for (EditionCandidate e : editions) {
                String label = "";
                if (e.getPublicationDate() != null) {
                    label = formatter.format(e.getPublicationDate().getTime());
                }
                this.editionCandidates.put(label, e);
            }
        }
    }

    /**
     * Gets a {@link Map} of active sections for the {@link Outlet}
     * in the selected {@link NewsItemPlacement}.
     * 
     * @return {@link Map} of active sections
     */
    public Map<String, Section> getSections() {
        Map<String, Section> sections = new LinkedHashMap<String, Section>();
        for (Section section : selectedNewsItemPlacement.getOutlet().
                getActiveSections()) {
            sections.put(section.getFullName(), section);
        }
        return sections;
    }

    public void onAddPlacement(ActionEvent event) {
        if (getEditionCandidate() != null) {
            if (getEditionCandidate().isExist()) {
                try {
                    selectedNewsItemPlacement.setEdition(
                            outletFacade.findEditionById(getEditionCandidate().
                            getEditionId()));
                } catch (DataNotFoundException ex) {
                    log.log(Level.INFO, "Edition {0} does not exist",
                            getEditionCandidate().getEditionId());
                }
            } else {
                selectedNewsItemPlacement.setEdition(outletFacade.createEdition(
                        getEditionCandidate()));
            }


            if (isEditMode()) {
                selectedNewsItemPlacement = newsItemFacade.createPlacement(
                        selectedNewsItemPlacement);
            }

            if (!assignment.getPlacements().contains(selectedNewsItemPlacement)) {
                assignment.getPlacements().add(selectedNewsItemPlacement);
            }
        }
    }

    public void onUpdatePlacement(ActionEvent event) {
        if (getEditionCandidate().isExist()) {
            try {
                selectedNewsItemPlacement.setEdition(outletFacade.
                        findEditionById(getEditionCandidate().getEditionId()));
            } catch (DataNotFoundException ex) {
                log.log(Level.INFO, "Edition {0} does not exist",
                        getEditionCandidate().getEditionId());
            }
        } else {
            selectedNewsItemPlacement.setEdition(outletFacade.createEdition(
                    getEditionCandidate()));
        }

        if (isEditMode()) {
            selectedNewsItemPlacement = newsItemFacade.updatePlacement(
                    selectedNewsItemPlacement);
        }

        if (!assignment.getPlacements().contains(selectedNewsItemPlacement)) {
            assignment.getPlacements().add(selectedNewsItemPlacement);
        }
    }

    public void onRemovePlacement(ActionEvent event) {
        if (assignment.getPlacements().contains(selectedNewsItemPlacement)) {
            assignment.getPlacements().remove(selectedNewsItemPlacement);
        }
        if (isEditMode()) {
            newsItemFacade.deletePlacement(selectedNewsItemPlacement);
        }
    }

    public NewsItemPlacement getSelectedNewsItemPlacement() {
        return selectedNewsItemPlacement;
    }

    public void setSelectedNewsItemPlacement(
            NewsItemPlacement selectedNewsItemPlacement) {
        this.selectedNewsItemPlacement = selectedNewsItemPlacement;
    }

    private UserAccount getUser() {
        return (UserAccount) JsfUtils.getValueOfValueExpression(
                "#{userSession.user}");
    }
}
