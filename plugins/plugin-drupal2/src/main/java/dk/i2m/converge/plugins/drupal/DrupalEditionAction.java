/*
 * Copyright (C) 2015 Raymond Wanyoike
 *
 * This file is part of Converge.
 *
 * Converge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Converge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Converge. If not, see <http://www.gnu.org/licenses/>.
 */

package dk.i2m.converge.plugins.drupal;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dk.i2m.converge.core.annotations.OutletAction;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemActionState;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.logging.LogSubject;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import dk.i2m.converge.core.workflow.Section;
import dk.i2m.converge.core.workflow.WorkflowStateTransitionException;
import dk.i2m.drupal.services.entities.NodeEntity;
import org.apache.commons.lang.math.NumberUtils;
import retrofit.RetrofitError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Drupal Edition Action for uploading editions to Drupal.
 */
@OutletAction
public class DrupalEditionAction implements EditionAction {

    private static final Logger LOG = Logger.getLogger(DrupalEditionAction.class.getName());

    private static final String STATE_FAILED = "FAILED";
    private static final String STATE_UPLOADED = "UPLOADED";
    private static final String STATE_UPLOADING = "UPLOADING";

    private final ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.drupal.Messages");

    private DrupalServicesClient servicesClient;
    private OutletEditionAction editionAction;
    private PluginContext pluginContext;
    private Edition edition;
    private Gson gson = new Gson();

    private Map<String, String> availableProperties;
    private Map<String, String> sectionMapping = new HashMap<String, String>();

    private String[] fieldMapping;
    private String nodeType;
    private String renditionName;

    private long workflowFailedState;
    private long workflowUploadedState;
    private long workflowUploadState;

    // Errors encountered
    private int errors = 0;

    @Override
    public void execute(PluginContext context, Edition edition, OutletEditionAction action) {
        pluginContext = context;
        this.edition = edition;
        editionAction = action;

        logActivity(LogSeverity.INFO, LogKey.EDITION_START, new Object[]{
                edition.getId()}, null);

        if (!initialize(null)) {
            return;
        }

        logActivity(LogSeverity.INFO, LogKey.NEWS_ITEMS, new Object[]{
                edition.getNumberOfPlacements()}, null);

        try {
            for (NewsItemPlacement placement : edition.getPlacements()) {
                NewsItem newsItem = placement.getNewsItem();
                processPlacement(placement, newsItem);
            }
        } catch (RetrofitError ex) {
            // TODO: Identify throw point, and store action state
            logActivity(LogSeverity.SEVERE, LogKey.CONNECT_ERROR, new Object[]{
                    ex.getMessage()}, null);
            LOG.log(Level.FINEST, null, ex);
            errors++;
        } finally {
            try {
                servicesClient.logoutUser();
            } catch (RetrofitError ex) {
                LOG.log(Level.FINEST, null, ex);
                errors++;
            }
        }

        if (errors > 0) {
            logActivity(LogSeverity.WARNING, LogKey.ERRORS, new Object[]{
                    errors}, null);
        } else {
            logActivity(LogSeverity.INFO, LogKey.ERRORS, new Object[]{
                    errors}, null);
        }

        logActivity(LogSeverity.INFO, LogKey.EDITION_STOP, new Object[]{
                edition.getId()}, null);
    }

    @Override
    public void executePlacement(PluginContext context, NewsItemPlacement placement, Edition edition,
                                 OutletEditionAction action) {
        pluginContext = context;
        this.edition = edition;
        editionAction = action;

        NewsItem newsItem = placement.getNewsItem();
        logActivity(LogSeverity.INFO, LogKey.PLACEMENT_START, new Object[]{
                newsItem.getId()}, newsItem);

        if (!initialize(newsItem)) {
            return;
        }

        try {
            processPlacement(placement, newsItem);
        } catch (RetrofitError ex) {
            // TODO: Identify throw point, and store action state
            logActivity(LogSeverity.SEVERE, LogKey.CONNECT_ERROR, new Object[]{
                    ex.getMessage()}, newsItem);
            LOG.log(Level.FINEST, null, ex);
            errors++;
        } finally {
            try {
                servicesClient.logoutUser();
            } catch (RetrofitError ex) {
                LOG.log(Level.FINEST, null, ex);
                errors++;
            }
        }

        if (errors > 0) {
            logActivity(LogSeverity.SEVERE, LogKey.ERRORS, new Object[]{
                    errors}, newsItem);
        } else {
            logActivity(LogSeverity.INFO, LogKey.ERRORS, new Object[]{
                    errors}, newsItem);
        }

        logActivity(LogSeverity.INFO, LogKey.PLACEMENT_STOP, new Object[]{
                newsItem.getId()}, newsItem);
    }

    @Override
    public boolean isSupportEditionExecute() {
        return true;
    }

    @Override
    public boolean isSupportPlacementExecute() {
        return true;
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            for (Property property : Property.values()) {
                availableProperties.put(bundle.getString(property.name()), property.name());
            }
        }

        return availableProperties;
    }

    @Override
    public String getName() {
        return bundle.getString(BundleKey.PLUGIN_NAME.name());
    }

    @Override
    public String getDescription() {
        return bundle.getString(BundleKey.PLUGIN_DESCRIPTION.name());
    }

    @Override
    public String getVendor() {
        return bundle.getString(BundleKey.PLUGIN_VENDOR.name());
    }

    @Override
    public Date getDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DrupalUtils.DATE_FORMAT);
            return sdf.parse(bundle.getString(BundleKey.PLUGIN_BUILD_TIME.name()));
        } catch (ParseException e) {
            // TODO: Log exception
            return new Date();
        }
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public String getAbout() {
        return bundle.getString(BundleKey.PLUGIN_ABOUT.name());
    }

    /**
     * Initializes the plug-in. Initialization includes loading and validating
     * properties.
     *
     * @param newsItem
     * @return true if valid
     */
    private boolean initialize(NewsItem newsItem) {
        boolean valid = true;

        try {
            Map<String, String> properties = editionAction.getPropertiesAsMap();

            List<String> names = new ArrayList<String>();
            names.add(Property.SERVICE_ENDPOINT.name());
            names.add(Property.USERNAME.name());
            names.add(Property.PASSWORD.name());
            names.add(Property.MAPPING_FIELD.name());
            names.add(Property.MAPPING_SECTION.name());
            names.add(Property.NODE_TYPE.name());
            names.add(Property.STATE_UPLOAD.name());
            names.add(Property.STATE_UPLOADED.name());
            names.add(Property.STATE_FAILED.name());

            DrupalUtils.checkProperties(properties, names);

            String endpoint = properties.get(Property.SERVICE_ENDPOINT.name());
            String username = properties.get(Property.USERNAME.name());
            String password = properties.get(Property.PASSWORD.name());
            String userAlias = properties.get(Property.ALIAS_USER.name());
            String nodeAlias = properties.get(Property.ALIAS_NODE.name());
            String fields = properties.get(Property.MAPPING_FIELD.name());
            String sections = properties.get(Property.MAPPING_SECTION.name());

            if (!fields.contains(DrupalUtils.KEY_NEWSITEM_ID)) {
                throw new IllegalArgumentException("\"MAPPING_FIELD\" must contain a NEWSITEM_ID map");
            }

            if (DrupalUtils.convertStringMap(fields) == null) {
                throw new IllegalArgumentException("\"MAPPING_FIELD\" property is badly formatted");
            }

            if (DrupalUtils.convertStringMap(sections) == null) {
                throw new IllegalArgumentException("\"MAPPING_SECTION\" property is badly formatted");
            }

            Long uploadState = NumberUtils.toLong(properties.get(Property.STATE_UPLOAD.name()));
            Long uploadedState = NumberUtils.toLong(properties.get(Property.STATE_UPLOADED.name()));
            Long failedState = NumberUtils.toLong(properties.get(Property.STATE_FAILED.name()));

            if (uploadState == 0 || uploadedState == 0 || failedState == 0) {
                throw new IllegalArgumentException(
                        "\"UPLOAD_STATE|STATE_UPLOADED|STATE_FAILED \" must be valid Workflow State ids");
            }

            fieldMapping = DrupalUtils.convertStringArrayA(fields);
            sectionMapping = DrupalUtils.convertStringMap(sections);
            nodeType = properties.get(Property.NODE_TYPE.name());
            renditionName = properties.get(Property.IMAGE_RENDITION.name());
            workflowUploadState = uploadState;
            workflowUploadedState = uploadedState;
            workflowFailedState = failedState;
            servicesClient = new DrupalServicesClient(endpoint, username, password);
            servicesClient.setNodeAlias(nodeAlias);
            servicesClient.setUserAlias(userAlias);
        } catch (IllegalArgumentException ex) {
            logActivity(LogSeverity.SEVERE, LogKey.INIT_ERROR, new Object[]{ex.getMessage()}, newsItem);
            valid = false;
        }

        return valid;
    }

    /**
     * Validate if a NewsItem is in a right state to be processed.
     *
     * @param newsItem
     * @param section
     * @return true if valid
     */
    private boolean validate(NewsItem newsItem, Section section) {
        boolean valid = true;

        if (!newsItem.getCurrentState().getId().equals(workflowUploadState)) {
            // Ignore NewsItem if it hasn't reached the UPLOAD_STATE
            logActivity(LogSeverity.WARNING, LogKey.INCOMPLETE_STATE, new Object[]{
                    newsItem.getCurrentState().getName()}, newsItem);
            valid = false;
        } else if (section == null) {
            // Ignore NewsItem if the section is not set
            logActivity(LogSeverity.WARNING, LogKey.MISSING_SECTION, new Object[]{
                    null}, newsItem);
            valid = false;
        } else if (sectionMapping.get(String.valueOf(section.getId())) == null) {
            // Ignore NewsItem if the section is not mapped
            logActivity(LogSeverity.WARNING, LogKey.MISSING_SECTION_MAP, new Object[]{
                    section.getId(), section.getFullName()}, newsItem);
            valid = false;
        }

        if (newsItem.getWordCount() == 0) {
            // Ignore NewsItem if the word length is 0
            logActivity(LogSeverity.WARNING, LogKey.EMPTY_STORY, new Object[]{}, newsItem);
            valid = false;
        }

        return valid;
    }

    /**
     * Process a single {@link NewsItemPlacement}. Processing includes
     * creating, or updating a corresponding node in Drupal.
     *
     * @param placement
     */
    private void processPlacement(NewsItemPlacement placement, NewsItem newsItem) {
        logActivity(LogSeverity.INFO, LogKey.PROCESS_START, new Object[]{
                newsItem.getId(), newsItem.getTitle()}, newsItem);

        if (validate(newsItem, placement.getSection())) {
            servicesClient.loginUser();
            upload(placement, newsItem);
        }

        logActivity(LogSeverity.INFO, LogKey.PROCESS_STOP, new Object[]{
                newsItem.getId(), newsItem.getTitle()}, newsItem);
    }

    /**
     * Upload a {@link NewsItem} to Drupal.
     *
     * @param newsItem
     */
    private void upload(NewsItemPlacement placement, NewsItem newsItem) {
        Map<String, String> nodeParams = DrupalUtils.nodeParams(placement, nodeType, fieldMapping, sectionMapping);
        List<NodeEntity> nodeEntities;

        try {
            String newsItemIdField = DrupalUtils.getKeyValue(fieldMapping, DrupalUtils.KEY_NEWSITEM_ID);
            Map<String, String> options = new LinkedHashMap<String, String>();
            options.put("parameters[type]", nodeType);
            // Assume the NewsItem id is unique
            options.put(String.format("parameters[%s]", newsItemIdField), String.valueOf(newsItem.getId()));
            // Search for a NewsItem on Drupal
            nodeEntities = servicesClient.indexNode(options);
        } catch (RetrofitError ex) {
            logActivity(LogSeverity.SEVERE, LogKey.CHECK_ERROR, new Object[]{
                    ex.getMessage()}, newsItem);
            LOG.log(Level.FINEST, null, ex);
            workflowTransition(newsItem, workflowFailedState);
            errors++;
            return;
        }

        Map<String, Object> fileParams = DrupalUtils.fileParams(newsItem, renditionName);
        NodeEntity nodeEntity = null;

        // Avoid duplicate action states. See findNewsItemActionStateOrCreate
        NewsItemActionState actionState = pluginContext.findNewsItemActionStateOrCreate(
                edition.getId(), newsItem.getId(), getClass().getName(), STATE_UPLOADING,
                gson.toJson(new NewsItemStateData()));

        try {
            if (!nodeEntities.isEmpty()) {
                // FIXME: When a NewsItem workflow state changes, its 'updated'
                // field also changes - we can't compare against a NewsItem
                // 'updated' field to determine if it need to be updated.
                // FIXME: Remove existing files from the node, this only
                // applies if the NewsItem has 0 images, otherwise normal image
                // uploads replace already uploaded images.
                // FIXME: NodeResource.attachFile with an empty body, will
                // throw an error on Drupal.

                NodeEntity indexEntity = nodeEntities.get(0);

                logActivity(LogSeverity.INFO, LogKey.NODE_IMAGES_UPDATE, new Object[]{
                        indexEntity.getId(), fileParams.size() / 3}, newsItem);
                // Update a NewsItem on Drupal
                nodeEntity = servicesClient.updateNode(indexEntity.getId(), nodeParams);
            } else {
                logActivity(LogSeverity.INFO, LogKey.NODE_IMAGES_CREATE, new Object[]{
                        fileParams.size() / 3}, newsItem);
                // Create a NewsItem on Drupal
                nodeEntity = servicesClient.createNode(nodeParams);
            }

            String imageField = DrupalUtils.getKeyValue(fieldMapping, DrupalUtils.KEY_IMAGE);
            // Attach files to a node on Drupal.
            servicesClient.attachFiles(nodeEntity, imageField, fileParams);
            workflowTransition(newsItem, workflowUploadedState);
            actionState.setState(STATE_UPLOADED);
        } catch (RetrofitError ex) {
            if (!nodeEntities.isEmpty()) {
                logActivity(LogSeverity.SEVERE, LogKey.UPDATE_ERROR, new Object[]{
                        ex.getMessage()}, newsItem);
            } else {
                logActivity(LogSeverity.SEVERE, LogKey.CREATE_ERROR, new Object[]{
                        ex.getMessage()}, newsItem);
            }

            LOG.log(Level.FINEST, null, ex);
            workflowTransition(newsItem, workflowFailedState);
            actionState.setState(STATE_FAILED);
            errors++;
        }

        updateState(actionState, newsItem, nodeEntity);
        pluginContext.updateNewsItemActionState(actionState);
    }

    /**
     * @param actionState {@link NewsItem} action state
     * @param newsItem
     * @param nodeEntity
     */
    private void updateState(NewsItemActionState actionState, NewsItem newsItem, NodeEntity nodeEntity) {
        if (nodeEntity != null) {
            NewsItemStateData stateData = getStateData(actionState);
            stateData.setId(nodeEntity.getId());
            stateData.setVersion(newsItem.getUpdated().getTimeInMillis());
            actionState.setData(gson.toJson(stateData));
        }
    }

    /**
     * @param actionState
     * @return
     */
    private NewsItemStateData getStateData(NewsItemActionState actionState) {
        try {
            NewsItemStateData stateData = gson.fromJson(actionState.getData(), NewsItemStateData.class);
            if (stateData == null) {
                // stateData should never be null
                stateData = new NewsItemStateData();
            }
            return stateData;
        } catch (JsonParseException ex) {
            return new NewsItemStateData();
        }
    }

    /**
     * @param newsItem
     * @param step     {@link dk.i2m.converge.core.workflow.WorkflowStep} id
     */
    private void workflowTransition(NewsItem newsItem, Long step) {
        try {
            pluginContext.workflowTransition(newsItem.getId(), UserAccount.SYSTEM_ACCOUNT, step);
        } catch (WorkflowStateTransitionException ex) {
            logActivity(LogSeverity.SEVERE, LogKey.TRANSITION_ERROR, new Object[]{
                    ex.getMessage()}, newsItem);
            LOG.log(Level.FINEST, null, ex);
            errors++;
        }
    }

    /**
     * @param severity
     * @param message
     * @param arguments
     * @param newsItem
     */
    private void logActivity(LogSeverity severity, LogKey message, Object[] arguments, NewsItem newsItem) {
        if (pluginContext == null) {
            LOG.log(Level.SEVERE, "PluginContext not yet set. Cannot log");
            return;
        }

        List<LogSubject> subjects = new ArrayList<LogSubject>();

        LogSubject logEdition = new LogSubject(Edition.class.getName(), String.valueOf(edition.getId()));
        subjects.add(logEdition);

        if (newsItem != null) {
            LogSubject logNewsItem = new LogSubject(NewsItem.class.getName(), String.valueOf(newsItem.getId()));
            subjects.add(logNewsItem);
        }

        pluginContext.log(severity, bundle.getString(message.name()), arguments, subjects);
    }

    public enum BundleKey {

        PLUGIN_ABOUT,
        PLUGIN_BUILD_TIME,
        PLUGIN_DESCRIPTION,
        PLUGIN_NAME,
        PLUGIN_VENDOR
    }

    public enum LogKey {

        CHECK_ERROR,
        CONNECT_ERROR,
        CREATE_ERROR,
        EDITION_START,
        EDITION_STOP,
        EMPTY_STORY,
        ERRORS,
        INCOMPLETE_STATE,
        INIT_ERROR,
        MISSING_SECTION,
        MISSING_SECTION_MAP,
        NEWS_ITEMS,
        NODE_IMAGES_CREATE,
        NODE_IMAGES_UPDATE,
        NODE_UP_TO_DATE,
        PLACEMENT_START,
        PLACEMENT_STOP,
        PROCESS_START,
        PROCESS_STOP,
        TRANSITION_ERROR,
        UPDATE_ERROR,
    }
}
