/*
 * Copyright (C) 2010 - 2014 Converge Consulting
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.workflow.WorkflowStateTransitionException;
import dk.i2m.commons.BeanComparator;
import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.EnrichException;
import dk.i2m.converge.core.content.*;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.MediaItemStatus;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.metadata.*;
import dk.i2m.converge.core.plugin.WorkflowValidatorException;
import dk.i2m.converge.core.search.QueueEntryOperation;
import dk.i2m.converge.core.search.QueueEntryType;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.utils.StringUtils;
import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.domain.search.SearchResult;
import dk.i2m.converge.domain.search.SearchResults;
import dk.i2m.converge.ejb.facades.*;
import dk.i2m.converge.ejb.services.ConfigurationServiceLocal;
import dk.i2m.converge.ejb.services.MetaDataServiceLocal;
import dk.i2m.jsf.JsfUtils;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.commons.io.FilenameUtils;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

/**
 * Managed backing bean for {@code /NewsItem.jspx}. The backing bean is kept
 * alive by the JSF file. Loading a news item is done by setting the ID of the
 * item using {@link NewsItem#setId(java.lang.Long)}.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItem {

    private static final Logger LOG = Logger.getLogger(NewsItem.class.getName());
    @EJB
    private CatalogueFacadeLocal catalogueFacade;
    @EJB
    private NewsItemFacadeLocal newsItemFacade;
    @EJB
    private MetaDataFacadeLocal metaDataFacade;
    @EJB
    private MetaDataServiceLocal metaDataService;
    @EJB
    private OutletFacadeLocal outletFacade;
    @EJB
    private UserFacadeLocal userFacade;
    @EJB
    private SearchEngineLocal searchEngine;
    @EJB
    private ConfigurationServiceLocal cfgService;
    private dk.i2m.converge.core.content.NewsItem selectedNewsItem = null;
    private WorkflowStep selectedStep = null;
    private WorkflowStateTransition selectedWorkflowStateTransition;
    private NewsItemActor selectedActor;
    private NewsItemActor newActor = new NewsItemActor();
    private Concept selectedMetaDataConcept;
    private Long id = 0L;
    private DataModel versions = new ListDataModel();
    private boolean validWorkflowStep = false;
    private String comment = "";
    private boolean readOnly = false;
    private boolean pullbackAvailable = false;
    private ContentItemPermission permission;
    private NewsItemMediaAttachment selectedAttachment;
    private String keyword = "";
    private DataModel searchResults = new ListDataModel();
    private Long selectedMediaItemId = null;
    private NewsItemMediaAttachment deleteMediaItem = null;
    private String newConcept = "";
    /**
     * Dev Note: Could not use a Concept object for direct entry as it is
     * abstract.
     */
    private String newConceptName = "";
    /**
     * Dev Note: Could not use a Concept object for direct entry as it is
     * abstract.
     */
    private String newConceptDescription = "";
    /**
     * Dev Note: Could not use a Concept object for direct entry as it is
     * abstract.
     */
    private String conceptType = "";
    private boolean conceptAdded = false;
    private Map<String, Boolean> fieldVisibible = new HashMap<String, Boolean>();
    private Date editionDate;
    private NewsItemPlacement selectedNewsItemPlacement;
    private EditionCandidate editionCandidate;
    private Map<String, EditionCandidate> editionCandidates = new LinkedHashMap<String, EditionCandidate>();
    private Catalogue selectedCatalogue = null;
    private boolean showClosedEditions = false;
    private Map<String, Concept> suggestedConcepts = new LinkedHashMap<String, Concept>();
    private List<Concept> selectedConcepts = new ArrayList<Concept>();
    private Long uploadedMediaItem = 0L;
    private SearchResults lastSearch = new SearchResults();

    /**
     * Creates a new instance of {@link NewsItem}.
     */
    public NewsItem() {
    }

    /**
     * Event handler for suggesting concepts based on inputted string. Note that
     * {@link dk.i2m.converge.core.metadata.Subject}s are not returned as they
     * are selected through the subject selection dialog.
     *
     * @param suggestion String for which to base the suggestions
     * @return {@link List} of suggested {@link Concept}s based on
     * {@code suggestion}
     */
    public List<Concept> onConceptSuggestion(Object suggestion) {
        String conceptName = (String) suggestion;
        List<Concept> suggestedConcepts = new ArrayList<Concept>();
        suggestedConcepts = metaDataFacade.findConceptsByName(conceptName,
                Person.class, GeoArea.class, PointOfInterest.class,
                Organisation.class);

        return suggestedConcepts;
    }

    public void onAddConcept(ActionEvent event) {
        try {
            Concept concept = metaDataFacade.findConceptByName(newConcept);
            if (!this.selectedNewsItem.getConcepts().contains(concept)) {
                this.selectedNewsItem.getConcepts().add(concept);
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                        Bundle.i18n.name(),
                        "NewsItem_CONCEPT_X_ADDED_TO_NEWS_ITEM_Y",
                        new Object[]{concept.getFullTitle(), selectedNewsItem.
                            getTitle()});
            }
            this.newConcept = "";
            conceptAdded = true;
            onAutoSave(event);
        } catch (DataNotFoundException ex) {
            this.conceptType = "";
            this.newConceptName = this.newConcept;
            conceptAdded = false;
        }
    }

    public void onAddNewConcept(ActionEvent event) {
        Concept c = null;
        if ("ORGANISATION".equalsIgnoreCase(conceptType)) {
            c = new Organisation(newConceptName, newConceptDescription);
        } else if ("PERSON".equalsIgnoreCase(conceptType)) {
            c = new Person(newConceptName, newConceptDescription);
        } else if ("LOCATION".equalsIgnoreCase(conceptType)) {
            c = new GeoArea(newConceptName, newConceptDescription);
        } else if ("POI".equalsIgnoreCase(conceptType)) {
            c = new PointOfInterest(newConceptName, newConceptDescription);
        } else {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "NewsItem_CONCEPT_TYPE_MISSING");
        }

        if (c != null) {
            c = metaDataFacade.create(c);
            if (!this.selectedNewsItem.getConcepts().contains(c)) {
                this.selectedNewsItem.getConcepts().add(c);
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                        Bundle.i18n.name(),
                        "NewsItem_CONCEPT_X_ADDED_TO_NEWS_ITEM_Y",
                        new Object[]{c.getFullTitle(),
                            selectedNewsItem.getTitle()});
                onAutoSave(event);
            }
            this.newConcept = "";
        }
    }

    public void onSelectSubject(NodeSelectedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        Subject subj = (Subject) tree.getRowData();
        this.selectedNewsItem.getConcepts().add(subj);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(),
                "NewsItem_CONCEPT_X_ADDED_TO_NEWS_ITEM_Y",
                new Object[]{subj.getFullTitle(), selectedNewsItem.getTitle()});
    }

    /**
     * Gets the unique identifier of the loaded news item.
     *
     * @return Unique identifier of the loaded news item
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the auto-save interval in milliseconds.
     *
     * @return Auto-save interval in milliseconds. A value less than 1 indicates
     * that auto-save is disabled
     */
    public Integer getAutoSaveInterval() {
        return cfgService.getInteger(ConfigurationKey.AUTO_SAVE_INTERVAL);
    }

    /**
     * Determines if auto-save is enabled.
     *
     * @return {@code true} if auto-save is enabled, otherwise {@code false}
     */
    public boolean isAutoSaveEnabled() {
        if (getAutoSaveInterval() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the id of the news item to load. Upon setting the identifier, the
     * news item will be checked-out from the database.
     *
     * @param id Unique identifier of the news item to load
     */
    public void setId(Long id) {
        LOG.log(Level.FINE, "Setting News Item #{0}", id);
        this.id = id;

        if (id == null) {
            return;
        }

        if (selectedNewsItem == null || (selectedNewsItem.getId() != id)) {
            String username = getUser().getUsername();
            LOG.log(Level.FINE,
                    "Checking if {0} is permitted to open news item #{1}",
                    new Object[]{username, id});

            NewsItemHolder nih;
            try {
                nih = newsItemFacade.checkout(id);
                this.permission = nih.getPermission();
                this.readOnly = nih.isReadOnly();
                this.selectedNewsItem = nih.getNewsItem();
                this.pullbackAvailable = nih.isPullbackAvailable();
                this.fieldVisibible = nih.getFieldVisibility();
                this.versions = new ListDataModel(nih.getVersions());
                if (!nih.isCheckedOut() && (this.permission
                        == ContentItemPermission.USER || this.permission
                        == ContentItemPermission.ROLE)) {
                    JsfUtils.createMessage("frmPage",
                            FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                            "NewsItem_OPEN_READ_ONLY",
                            new Object[]{selectedNewsItem.getCheckedOutBy().
                                getFullName()});
                }
            } catch (DataNotFoundException ex) {
                this.permission = ContentItemPermission.UNAUTHORIZED;
                this.readOnly = true;
                this.selectedNewsItem = null;
                this.pullbackAvailable = false;
            }
        }
    }

    /**
     * Determines if a news item has been loaded. If a news item cannot be
     * retrieved from {@link NewsItem#getSelectedNewsItem()} it is not loaded.
     *
     * @return {@code true} if a news item has been selected and loaded,
     * otherwise {@code false}
     */
    public boolean isNewsItemLoaded() {
        if (getSelectedNewsItem() == null) {
            return false;
        } else {
            return true;
        }
    }

    public void setSelectedNewsItem(
            dk.i2m.converge.core.content.NewsItem newsItem) {
        this.selectedNewsItem = newsItem;
    }

    public dk.i2m.converge.core.content.NewsItem getSelectedNewsItem() {
        return selectedNewsItem;
    }

    /**
     * Gets a {@link Map} containing the visibility indicators for each field.
     * The Map key is the name of the news item field, corresponding to the full
     * name of a {@link NewsItemField}.
     *
     * @return Visibility indicators for the news item fields
     */
    public Map<String, Boolean> getFieldVisible() {
        return this.fieldVisibible;
    }

    public String onSubmit() {
        if (getSelectedStep() == null) {
            try {
                selectedNewsItem = newsItemFacade.checkin(selectedNewsItem);
            } catch (LockingException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(), "NewsItem_COULD_NOT_SAVE_LOCKED");
                return null;
            }
            return "/inbox";
        } else {
            try {
                selectedNewsItem = newsItemFacade.step(selectedNewsItem,
                        selectedStep.getId(), getComment());
                comment = "";
                return "/inbox";
            } catch (WorkflowStateTransitionException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(), "Generic_AN_ERROR_OCCURRED_X",
                        new Object[]{ex.getMessage()});
                return null;
            }
        }
    }

    public void onApply(ActionEvent event) {
        try {
            selectedNewsItem = newsItemFacade.save(selectedNewsItem);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(), "NewsItem_CHANGES_SAVED");
        } catch (LockingException ex) {
            LOG.log(Level.INFO, ex.getMessage());
            LOG.log(Level.FINE, "", ex);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "NewsItem_COULD_NOT_SAVE_LOCKED");
        }
    }

    /**
     * Event handler invoked upon executing the periodic auto-save poller.
     *
     * @param event Event that invoked the auto-save
     */
    public void onAutoSave(ActionEvent event) {
        try {
            selectedNewsItem = newsItemFacade.save(selectedNewsItem);
        } catch (LockingException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "NewsItem_COULD_NOT_SAVE_LOCKED");
        }
    }

    public String onSave() {
        try {
            selectedNewsItem = newsItemFacade.checkin(selectedNewsItem);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(), "NewsItem_STORY_SAVED", null);
            return "/inbox";
        } catch (LockingException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "NewsItem_COULD_NOT_SAVE_LOCKED");
            return null;
        }
    }

    public String onClose() {
        newsItemFacade.revokeLock(selectedNewsItem.getId());
        return "/inbox";
    }

    public void onPullback(ActionEvent event) {
        try {
            newsItemFacade.pullback(selectedNewsItem.getId());
            Long theId = selectedNewsItem.getId();
            selectedNewsItem = null;
            setId(theId);
        } catch (LockingException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "NewsItem_COULD_NOT_PULLBACK");
        } catch (WorkflowStateTransitionException ex) {
            LOG.log(Level.SEVERE, "", ex);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "NewsItem_COULD_NOT_PULLBACK_X",
                    new Object[]{ex.getMessage()});
        }
    }

    public void onActorSelect(ActionEvent event) {
        this.newActor = new NewsItemActor();
    }

    public void setDeleteActor(NewsItemActor actor) {
        if (actor != null) {
            try {
                selectedNewsItem.getActors().remove(actor);
                selectedNewsItem = newsItemFacade.save(selectedNewsItem);
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                        Bundle.i18n.name(), "NewsItem_ACTOR_REMOVED");
            } catch (LockingException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(), "Generic_AN_ERROR_OCCURRED_X",
                        new Object[]{ex.getMessage()});
            }
        }
    }

    public void setUpdateAttachment(NewsItemMediaAttachment attachment) {
        attachment = newsItemFacade.update(attachment);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(), "NewsItem_MEDIA_ATTACHMENT_UPDATED");
    }

    public void onAddActor(ActionEvent event) {
        if (this.newActor != null && this.newActor.getRole() != null
                && this.newActor.getUser() != null) {
            boolean dup = false;

            for (NewsItemActor nia : selectedNewsItem.getActors()) {
                if (nia.getRole().equals(this.newActor.getRole())
                        && nia.getUser().equals(this.newActor.getUser())) {
                    dup = true;
                    JsfUtils.createMessage("frmPage",
                            FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                            "NewsItem_DUPLICATE_USER_ROLE",
                            new Object[]{this.newActor.getUser().getFullName(),
                                this.newActor.getRole().getName()});
                }
            }

            if (!dup) {

                this.newActor.setNewsItem(selectedNewsItem);
                newsItemFacade.addActorToNewsItem(newActor);
                try {
                    selectedNewsItem
                            = newsItemFacade.findNewsItemById(selectedNewsItem.
                                    getId());
                } catch (DataNotFoundException ex) {
                    JsfUtils.createMessage("frmPage",
                            FacesMessage.SEVERITY_ERROR,
                            Bundle.i18n.name(), "Generic_AN_ERROR_OCCURRED_X",
                            new Object[]{ex.getMessage()});
                }
            }
            this.newActor = new NewsItemActor();

        } else {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "NewsItem_SELECT_USER_AND_ROLE");
        }
    }

    public void onSelectMetaData(ActionEvent event) {
        try {
            selectedNewsItem.getConcepts().add(selectedMetaDataConcept);
            selectedNewsItem = newsItemFacade.save(selectedNewsItem);

            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "NewsItem_META_DATA_X_ADDED_TO_ASSIGNMENT",
                    new Object[]{getSelectedMetaDataConcept().getName()});
        } catch (LockingException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "Generic_AN_ERROR_OCCURRED_X",
                    new Object[]{ex.getMessage()});
        }
    }

    public void onDeleteSelectedConcept(ActionEvent event) {
        try {
            selectedNewsItem.getConcepts().remove(selectedMetaDataConcept);
            selectedNewsItem = newsItemFacade.save(selectedNewsItem);

            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "NewsItem_META_DATA_X_REMOVED_FROM_ASSIGNMENT",
                    new Object[]{getSelectedMetaDataConcept().getName()});
        } catch (LockingException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "Generic_AN_ERROR_OCCURRED_X",
                    new Object[]{ex.getMessage()});
        }
    }

    public void onReplaceHeadline(ActionEvent event) {
        selectedNewsItem.setTitle(selectedWorkflowStateTransition.
                getHeadlineVersion());
    }

    public void onReplaceBrief(ActionEvent event) {
        selectedNewsItem.setBrief(selectedWorkflowStateTransition.
                getBriefVersion());
    }

    public void onReplaceStory(ActionEvent event) {
        selectedNewsItem.setStory(selectedWorkflowStateTransition.
                getStoryVersion());
    }

    /**
     * Handler for validating the selected {@link WorkflowStep}.
     *
     * @param event Event that invoked the handler
     */
    public void onValidateWorkflowStep(ActionEvent event) {
        this.validWorkflowStep = true;

        if (selectedStep == null) {
            this.validWorkflowStep = false;
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "NewsItem_VALIDATE_SELECT_OPTION");
        }

        boolean isRoleValidated = false;
        UserRole requiredRole = selectedStep.getToState().getActorRole();
        boolean isUserRole = selectedStep.getToState().isUserPermission();

        if (isUserRole) {
            for (NewsItemActor actor : selectedNewsItem.getActors()) {
                if (actor.getRole().equals(requiredRole)) {
                    // User role was already added
                    isRoleValidated = true;
                    break;
                }
            }
        } else {
            isRoleValidated = true;
        }

        if (!isRoleValidated) {
            this.validWorkflowStep = false;
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(),
                    "NewsItem_VALIDATE_MISSING_ROLE", new Object[]{requiredRole.
                        getName()});
        }

        for (WorkflowStepValidator validator : selectedStep.getValidators()) {
            try {
                dk.i2m.converge.core.plugin.WorkflowValidator workflowValidator
                        = validator.getValidator();
                workflowValidator.execute(selectedNewsItem, selectedStep,
                        validator);
            } catch (WorkflowValidatorException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(), "Generic_AN_ERROR_OCCURRED_X",
                        new Object[]{ex.getMessage()});
                this.validWorkflowStep = false;
            }
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public NewsItemActor getSelectedActor() {
        return selectedActor;
    }

    public void setSelectedActor(NewsItemActor selectedActor) {
        this.selectedActor = selectedActor;
    }

    public NewsItemActor getNewActor() {
        return newActor;
    }

    public void setNewActor(NewsItemActor newActor) {
        this.newActor = newActor;
    }

    public Map<String, UserRole> getOutletRoles() {
        Map<String, UserRole> roles = new LinkedHashMap<String, UserRole>();

        for (UserRole role : selectedNewsItem.getOutlet().getRoles()) {
            roles.put(role.getName(), role);
        }
        return roles;
    }

    public Map<String, UserAccount> getRoleUsers() {
        Map<String, UserAccount> users
                = new LinkedHashMap<String, UserAccount>();

        if (newActor != null && newActor.getRole() != null) {
            List<UserAccount> accounts = userFacade.getMembers(
                    newActor.getRole());
            Collections.sort(accounts, new BeanComparator("fullName"));

            for (UserAccount user : accounts) {
                users.put(user.getFullName(), user);
            }
        }

        return users;
    }

    public DataModel getMetaDataSubjects() {
        return new ListDataModel(metaDataFacade.findConceptByType(Subject.class));
    }

    public DataModel getMetaDataOrganisations() {
        return new ListDataModel(metaDataFacade.findConceptByType(
                Organisation.class));
    }

    public DataModel getMetaDataLocations() {
        return new ListDataModel(metaDataFacade.findConceptByType(GeoArea.class));
    }

    public DataModel getMetaDataPointsOfInterest() {
        return new ListDataModel(metaDataFacade.findConceptByType(
                PointOfInterest.class));
    }

    public DataModel getMetaDataPersons() {
        return new ListDataModel(metaDataFacade.findConceptByType(Person.class));
    }

    public Concept getSelectedMetaDataConcept() {
        return selectedMetaDataConcept;
    }

    public void setSelectedMetaDataConcept(Concept selectedMetaDataConcept) {
        this.selectedMetaDataConcept = selectedMetaDataConcept;
    }

    public DataModel getVersions() {
        return versions;
    }

    public void setVersions(DataModel versions) {
        this.versions = versions;
    }

    public WorkflowStateTransition getSelectedWorkflowStateTransition() {
        return selectedWorkflowStateTransition;
    }

    public void setSelectedWorkflowStateTransition(
            WorkflowStateTransition selectedWorkflowStateTransition) {
        this.selectedWorkflowStateTransition = selectedWorkflowStateTransition;
    }

    /**
     * Gets a {@link Map} of available {@link WorkflowStep}s from the current
     * state of the {@link NewsItem}.
     *
     * @return {@link Map} of available {@link WorkflowStep}s from the current
     * state of the {@link NewsItem}
     */
    public Map<String, WorkflowStep> getAvailableWorkflowSteps() {
        Map<String, WorkflowStep> steps
                = new LinkedHashMap<String, WorkflowStep>();

        for (WorkflowStep step : getSelectedNewsItem().getCurrentState().
                getNextStates()) {
            boolean isValidForUser = !Collections.disjoint(step.getValidFor(),
                    getUser().getUserRoles());
            if (step.isValidForAll() || isValidForUser) {
                steps.put(step.getName(), step);
            }
        }

        return steps;
    }

    /**
     * Gets the selected {@link WorkflowStep}.
     *
     * @return {@link WorkflowStep} selected
     */
    public WorkflowStep getSelectedStep() {
        return selectedStep;
    }

    /**
     * Sets the selected {@link WorkflowStep}. Upon selecting a
     * {@link WorkflowStep}, validation will occur.
     *
     * @param selectedStep {@link WorkflowStep} selected
     */
    public void setSelectedStep(WorkflowStep selectedStep) {
        this.selectedStep = selectedStep;
    }

    public NewsItemMediaAttachment getSelectedAttachment() {
        return selectedAttachment;
    }

    public void setSelectedAttachment(NewsItemMediaAttachment selectedAttachment) {
        this.selectedAttachment = selectedAttachment;
    }

    /**
     * Determines if the selected {@link WorkflowStep} is valid.
     *
     * @return {@code true} if the selected {@link WorkflowStep} is valid,
     * otherwise {@code false}
     */
    public boolean isValidWorkflowStep() {
        return validWorkflowStep;
    }

    /**
     * Determines if the current user is the current actor of the
     * {@link NewsItem}.
     *
     * @return {@code true} if the current user is among the current actors of
     * the {@link NewsItem}, otherwise {@code false}
     */
    public boolean isCurrentActor() {
        boolean currentActor = false;
        if (permission == null) {
            return false;
        }

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

    public boolean isAuthorized() {
        if (permission == null) {
            return false;
        }

        return !(permission == ContentItemPermission.UNAUTHORIZED);
    }

    public boolean isPullbackAvailable() {
        return this.pullbackAvailable;
    }

    public boolean isEditable() {
        return isCurrentActor() && !isReadOnly();
    }

    /**
     * Determines if the news item is locked and thereby read-only or if the
     * user is a part of the actors but not the current actor.
     *
     * @return {@code true} if the news item is locked, otherwise {@code false}
     */
    public boolean isReadOnly() {
        return readOnly || (ContentItemPermission.ACTOR == permission);
    }

    public Map<String, Edition> getOpenEditions() {
        Map<String, Edition> editions = new LinkedHashMap<String, Edition>();
        if (selectedNewsItem.getOutlet() != null) {
            for (Edition edition : outletFacade.findEditionsByStatus(true,
                    selectedNewsItem.getOutlet())) {
                editions.put(edition.getFriendlyName(), edition);
            }
        }
        return editions;
    }

    /**
     * Gets a {@link Map} of active sections for the {@link Outlet} in the
     * selected {@link NewsItemPlacement}.
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

    private UserAccount getUser() {
        return (UserAccount) JsfUtils.getValueOfValueExpression(
                "#{userSession.user}");
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public DataModel getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(DataModel searchResults) {
        this.searchResults = searchResults;
    }

    public SearchResults getLastSearch() {
        return lastSearch;
    }

    /**
     * Event handler for starting the search.
     *
     * @param event Event that invoked the handler
     */
    public void onSearch(ActionEvent event) {
        if (!getKeyword().trim().isEmpty()) {
            lastSearch = searchEngine.search(getKeyword(), 0, 50, "type:Media");
            List<SearchResult> results = lastSearch.getHits();

            List<SearchResult> realResults = new ArrayList<SearchResult>();
            for (SearchResult result : results) {
                realResults.add(result);
            }

            if (realResults.isEmpty()) {
                searchResults = new ListDataModel(new ArrayList());
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                        Bundle.i18n.name(),
                        "NewsItem_NO_MEDIA_ITEM_MATCHING_X",
                        new Object[]{getKeyword()});
            } else {

                for (SearchResult hit : realResults) {
                    hit.setLink(
                            MessageFormat.format(hit.getLink(),
                                    new Object[]{
                                        JsfUtils.getValueOfValueExpression(
                                                "#{facesContext.externalContext.request.contextPath}")}));
                }
                searchResults = new ListDataModel(realResults);
            }
        }
    }

    public void setUploadedMediaItem(String id) {
        id = id.replaceAll("SUCCESS" + System.getProperty("line.separator"), "");
        this.uploadedMediaItem = Long.valueOf(id.trim());
        setSelectedMediaItemId(this.uploadedMediaItem);
    }

    public Long getSelectedMediaItemId() {
        return selectedMediaItemId;
    }

    public void setSelectedMediaItemId(Long selectedMediaItemId) {
        this.selectedMediaItemId = selectedMediaItemId;

        if (this.selectedMediaItemId != null) {
            this.selectedAttachment = new NewsItemMediaAttachment();
            this.selectedAttachment.setNewsItem(selectedNewsItem);

            try {
                MediaItem mi = catalogueFacade.findMediaItemById(
                        this.selectedMediaItemId);
                this.selectedAttachment.setMediaItem(mi);
                this.selectedAttachment.setCaption(this.selectedAttachment.
                        getMediaItem().getDescription());
            } catch (DataNotFoundException ex) {
                searchEngine.addToIndexQueue(QueueEntryType.MEDIA_ITEM,
                        this.selectedMediaItemId, QueueEntryOperation.REMOVE);
            }
        }
    }

    /**
     * Event handler for attaching a {@link MediaItem} to the
     * {@link dk.i2m.converge.core.content.NewsItem}.
     *
     * @param event Event that invoked the handler
     */
    public void onUseAttachment(ActionEvent event) {
        boolean isNewUpload = this.uploadedMediaItem.longValue()
                == this.selectedAttachment.getMediaItem().getId().longValue();

        if (isNewUpload) {
            try {
                MediaItem mediaItem = catalogueFacade.findMediaItemById(
                        this.uploadedMediaItem);
                mediaItem.setDescription(selectedAttachment.getCaption());
                mediaItem.setByLine(
                        selectedAttachment.getMediaItem().getByLine());
                mediaItem = catalogueFacade.update(mediaItem);
                selectedAttachment.setMediaItem(mediaItem);
            } catch (DataNotFoundException ex) {
                LOG.warning(ex.getMessage());
            }
        }

        this.selectedAttachment.setDisplayOrder(this.selectedNewsItem.
                getNextAssetAttachmentDisplayOrder());
        this.selectedAttachment = newsItemFacade.create(selectedAttachment);
        this.selectedNewsItem.getMediaAttachments().add(selectedAttachment);

        onPreAttachMediaFile(event);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(),
                "NewsItem_MEDIA_ITEM_ATTACHED_TO_NEWS_ITEM");
    }

    public void onPreAttachMediaFile(ActionEvent event) {
        this.selectedCatalogue = null;
        this.selectedCatalogue = getUser().getDefaultMediaRepository();
        this.selectedAttachment = new NewsItemMediaAttachment();
        this.selectedAttachment.setNewsItem(selectedNewsItem);
        searchResults = new ListDataModel(new ArrayList());
        setKeyword("");
    }

    public class DiscoveredProperty {

        private String property = "";
        private String value = "";

        public DiscoveredProperty() {
        }

        public DiscoveredProperty(String property, String value) {
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public NewsItemMediaAttachment getDeleteMediaItem() {
        return deleteMediaItem;
    }

    public void setDeleteMediaItem(NewsItemMediaAttachment deleteMediaItem) {
        this.deleteMediaItem = deleteMediaItem;

        if (this.deleteMediaItem != null) {
            newsItemFacade.deleteMediaAttachmentById(
                    this.deleteMediaItem.getId());
            this.selectedNewsItem.getMediaAttachments().remove(
                    this.deleteMediaItem);
        }
    }

    public String getConceptType() {
        return conceptType;
    }

    public void setConceptType(String conceptType) {
        this.conceptType = conceptType;
    }

    public String getNewConcept() {
        return newConcept;
    }

    public void setNewConcept(String newConcept) {
        this.newConcept = newConcept;
    }

    public String getNewConceptDescription() {
        return newConceptDescription;
    }

    public void setNewConceptDescription(String newConceptDescription) {
        this.newConceptDescription = newConceptDescription;
    }

    public String getNewConceptName() {
        return newConceptName;
    }

    public void setNewConceptName(String newConceptName) {
        this.newConceptName = newConceptName;
    }

    public boolean isConceptAdded() {
        return conceptAdded;
    }

    public void setRemoveConcept(Concept concept) {
        this.selectedNewsItem.getConcepts().remove(concept);
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
            editionCal.setTime(editionDate);

            List<EditionCandidate> editions = outletFacade.
                    findEditionCandidatesByDate(getSelectedNewsItemPlacement().
                            getOutlet(), editionCal, showClosedEditions);
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

    public void onToggleShowClosedEditions(ActionEvent event) {
        setEditionDate(editionDate);
    }

    public boolean isShowClosedEditions() {
        return showClosedEditions;
    }

    public void setShowClosedEditions(boolean showClosedEditions) {
        this.showClosedEditions = showClosedEditions;
    }

    public void onChangePlacementOutlet(ValueChangeEvent event) {
        this.selectedNewsItemPlacement.setOutlet((Outlet) event.getNewValue());
        setEditionDate(getEditionDate());
    }

    /**
     * Gets a {@link Map} of {@link Edition} candidates based on the selected
     * edition date.
     *
     * @return {@link Map} of {@link Edition} candidates based on the selected
     * edition date
     */
    public Map<String, EditionCandidate> getEditionCandidates() {
        return this.editionCandidates;
    }

    public void onNewPlacement(ActionEvent event) {
        this.selectedNewsItemPlacement = new NewsItemPlacement();
        this.selectedNewsItemPlacement.setNewsItem(selectedNewsItem);
        if (getUser().getDefaultOutlet() != null) {
            this.selectedNewsItemPlacement.setOutlet(
                    getUser().getDefaultOutlet());
        } else {
            this.selectedNewsItemPlacement.setOutlet(
                    selectedNewsItem.getOutlet());
        }

        if (getUser().getDefaultSection() != null
                && this.selectedNewsItemPlacement.getOutlet() != null
                && getUser().getDefaultSection().getOutlet().equals(this.selectedNewsItemPlacement.
                        getOutlet())) {
            this.selectedNewsItemPlacement.setSection(getUser().
                    getDefaultSection());
        }

        this.editionCandidates = new LinkedHashMap<String, EditionCandidate>();
        this.editionCandidate = null;
        this.editionDate = null;
    }

    public void onAddPlacement(ActionEvent event) {

        if (getEditionCandidate() == null) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(),
                    "NewsItem_OUTLET_PLACEMENT_SELECT_EDITION");
            return;
        }

        if (getEditionCandidate().isExist()) {
            try {
                selectedNewsItemPlacement.setEdition(outletFacade.
                        findEditionById(getEditionCandidate().getEditionId()));
            } catch (DataNotFoundException ex) {
                LOG.log(Level.INFO,
                        "Edition {0} could not be found in the database",
                        getEditionCandidate().getEditionId());
            }
        } else {
            selectedNewsItemPlacement.setEdition(outletFacade.createEdition(
                    getEditionCandidate()));
        }

        selectedNewsItemPlacement = newsItemFacade.createPlacement(
                selectedNewsItemPlacement);

        if (!selectedNewsItem.getPlacements().contains(selectedNewsItemPlacement)) {
            selectedNewsItem.getPlacements().add(selectedNewsItemPlacement);
        }
    }

    public void onUpdatePlacement(ActionEvent event) {
        if (getEditionCandidate() == null) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(),
                    "NewsItem_OUTLET_PLACEMENT_SELECT_EDITION");
            return;
        }

        if (getEditionCandidate().isExist()) {
            try {
                selectedNewsItemPlacement.setEdition(outletFacade.
                        findEditionById(getEditionCandidate().getEditionId()));
            } catch (DataNotFoundException ex) {
                LOG.log(Level.INFO, "Edition {0} does not exist",
                        getEditionCandidate().getEditionId());
            }
        } else {
            selectedNewsItemPlacement.setEdition(outletFacade.createEdition(
                    getEditionCandidate()));
        }

        selectedNewsItemPlacement = newsItemFacade.updatePlacement(
                selectedNewsItemPlacement);

        if (!selectedNewsItem.getPlacements().contains(selectedNewsItemPlacement)) {
            selectedNewsItem.getPlacements().add(selectedNewsItemPlacement);
        }
    }

    public void onRemovePlacement(ActionEvent event) {
        if (selectedNewsItem.getPlacements().contains(selectedNewsItemPlacement)) {
            selectedNewsItem.getPlacements().remove(selectedNewsItemPlacement);
        }
        newsItemFacade.deletePlacement(selectedNewsItemPlacement);
    }

    /**
     * Event handler for uploading a new {@link MediaItem}.
     *
     * @param event Event that invoked the handler
     * @throws IOException If the file upload could not complete
     */
    public void onUploadMediaItem(UploadEvent event) throws IOException {
        UploadItem item = event.getUploadItem();

        try {
            // Find the original rendition (assume this is what is being uploaded)
            Rendition rendition = getSelectedCatalogue().getOriginalRendition();

            // Create MediaItem placeholder
            MediaItem mediaItem = new MediaItem();
            mediaItem.setTitle(FilenameUtils.getBaseName(item.getFileName()));
            mediaItem.setStatus(MediaItemStatus.SELF_UPLOAD);
            mediaItem.setOwner(getUser());
            mediaItem.setByLine(getUser().getFullName());
            mediaItem.setStatus(MediaItemStatus.SELF_UPLOAD);
            mediaItem.setCatalogue(getSelectedCatalogue());
            mediaItem = catalogueFacade.create(mediaItem);

            // Store MediaItemRendition in Database
            MediaItemRendition mediaItemRendition = catalogueFacade.create(
                    item.getFile(),
                    mediaItem,
                    rendition,
                    item.getFileName(),
                    item.getContentType(),
                    true);

            LOG.log(Level.FINE, "New media item rendition created: {0}",
                    mediaItemRendition.getId());

            this.uploadedMediaItem = mediaItem.getId();
            setSelectedMediaItemId(this.uploadedMediaItem);
        } catch (IOException ex) {
            JsfUtils.createMessage("frmPage",
                    FacesMessage.SEVERITY_FATAL,
                    Bundle.i18n.name(),
                    "Generic_AN_ERROR_OCCURRED_X",
                    new Object[]{ex.getMessage()});
            LOG.log(Level.SEVERE, "Could not create media item rendition. {0}",
                    ex.getMessage());
        }
    }

    public NewsItemPlacement getSelectedNewsItemPlacement() {
        return selectedNewsItemPlacement;
    }

    public void setSelectedNewsItemPlacement(
            NewsItemPlacement selectedNewsItemPlacement) {
        this.selectedNewsItemPlacement = selectedNewsItemPlacement;
        if (this.selectedNewsItemPlacement != null
                && this.selectedNewsItemPlacement.getEdition() != null) {
            this.editionCandidate
                    = new EditionCandidate(this.selectedNewsItemPlacement.
                            getEdition());
        }
    }

    public EditionCandidate getEditionCandidate() {
        return editionCandidate;
    }

    public void setEditionCandidate(EditionCandidate editionCandidate) {
        this.editionCandidate = editionCandidate;
    }

    public Catalogue getSelectedCatalogue() {
        return selectedCatalogue;
    }

    public void setSelectedCatalogue(Catalogue selectedCatalogue) {
        this.selectedCatalogue = selectedCatalogue;
    }

    /**
     * Gets the number of columns to display in the grid of attached media
     * items.
     *
     * @return Number of columns to display in the media attachment grid
     */
    public int getNumberOfMediaAttachmentsColumns() {
        if (selectedNewsItem == null) {
            return 0;
        } else if (selectedNewsItem.getMediaAttachments().size() < 3) {
            return selectedNewsItem.getMediaAttachments().size();
        } else {
            return 3;
        }
    }

    public void onSuggestConcepts(ActionEvent event) {
        if (getSelectedNewsItem().getStory().trim().isEmpty()) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "NewsItem_CONCEPTS_SUGGEST_FAILED_NO_STORY");
            return;
        }

        try {
            List<Concept> concepts = metaDataService.enrich(StringUtils.
                    stripHtml(getSelectedNewsItem().getStory()));
            suggestedConcepts = new LinkedHashMap<String, Concept>();
            this.selectedConcepts = new ArrayList<Concept>();
            ResourceBundle bundle = JsfUtils.getResourceBundle(Bundle.i18n.name());
            for (Concept concept : concepts) {
                String type = bundle.getString("Generic_" + concept.getType()
                        + "_NAME");
                suggestedConcepts.put(concept.getName() + " (" + type + ")",
                        concept);
            }

            if (suggestedConcepts.isEmpty()) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                        Bundle.i18n.name(), "NewsItem_CONCEPTS_SUGGEST_NO_RESULTS");
            }

        } catch (EnrichException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    "i18n", "NewsItem_CONCEPTS_SUGGEST_FAILED", null);
        }
    }

    public void onSaveConceptSuggestions(ActionEvent event) {
        getSelectedNewsItem().getConcepts().addAll(getSelectedConcepts());

        List<Concept> allConcepts = getSelectedNewsItem().getConcepts();
        Set<Concept> uniqueConcepts = new HashSet<Concept>(allConcepts);
        allConcepts = new ArrayList<Concept>(uniqueConcepts);
        getSelectedNewsItem().setConcepts(allConcepts);
        onAutoSave(event);
    }

    public Map<String, Concept> getSuggestedConcepts() {
        return suggestedConcepts;
    }

    public List<Concept> getSelectedConcepts() {
        return selectedConcepts;
    }

    public void setSelectedConcepts(List<Concept> selectedConcepts) {
        this.selectedConcepts = selectedConcepts;
    }
}
