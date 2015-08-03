/*
 * Copyright (C) 2015 Allan Lykke Christensen
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
package com.getconverge.plugins.wordpress;

import com.getconverge.plugins.wordpress.client.PostStatus;
import com.getconverge.plugins.wordpress.client.WpXmlRpcClient;
import com.getconverge.plugins.wordpress.client.WpXmlRpcClientException;
import dk.i2m.converge.core.annotations.OutletAction;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemEditionState;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.logging.LogSubject;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.NumberUtils;
import java.util.ArrayList;
import java.util.MissingResourceException;
import static com.getconverge.plugins.wordpress.WpEditionActionHelper.*;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.utils.FileUtils;
import dk.i2m.converge.core.workflow.WorkflowStateTransitionException;
import java.io.File;
import java.io.IOException;

/**
 * {@link EditionAction} for uploading content to Wordpress web sites using the
 * built-in XML RPC API.
 *
 * @author Allan Lykke Christensen
 */
@OutletAction
public class WpEditionAction implements EditionAction {

    private long uploadWorkflowStateId;
    private long uploadSuccessfulWorkflowOptionId;
    private long uploadFailedWorkflowOptionId;
    private int errors;
    private final static String UPLOADING = "Uploading";
    private int authorId;
    private String rendition;

    // Properties exposed by the Plugin
    public enum Property {

        AUTHOR_ID,
        CONNECTION_TIMEOUT,
        REPLY_TIMEOUT,
        IMAGE_RENDITION,
        PASSWORD,
        URL,
        USERNAME,
        UPLOAD_STATE,
        UPLOADED_TRANSITION,
        FAILED_TRANSITION,
    }

    // Keys from the resource bundle
    public enum BundleKey {

        PLUGIN_ABOUT,
        PLUGIN_BUILD_TIME,
        PLUGIN_DESCRIPTION,
        PLUGIN_NAME,
        PLUGIN_VENDOR,
        LOG_INITIALIZATION_ERROR,
        LOG_NEWS_ITEMS_IN_EDITION,
        LOG_ERRORS_ENCOUNTERED_DURING_PROCESSING,
        LOG_NO_ERRORS_ENCOUNTERED_DURING_PROCESSING,
        LOG_FINISHED_UPLOADING_EDITION,
        LOG_PROCESSING_PLACEMENT,
        LOG_PLACEMENT_UPLOADED,
        LOG_NEWS_ITEM_INCOMPLETE,
        LOG_ERROR_PROCESS_PLACEMENT, 
        LOG_COULD_NOT_TRANSITION_WORKFLOW, 
        LOG_IMAGE_UPLOAD_MISSING_RENDITION, 
        LOG_IMAGE_UPLOAD_FAILED
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int DEFAULT_REPLY_TIMEOUT = 30000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 30000;

    private static final Logger LOG = Logger.getLogger(WpEditionAction.class.getName());
    private final ResourceBundle bundle = ResourceBundle.getBundle("com.getconverge.plugins.wordpress.Messages");
    private Map<String, String> availableProperties;
    private OutletEditionAction action;
    private PluginContext pluginContext;
    private WpXmlRpcClient wordpress;

    @Override
    public void execute(PluginContext ctx, Edition edition, OutletEditionAction action) {
        this.errors = 0;
        this.pluginContext = ctx;
        this.action = action;

        try {
            init();
        } catch (IllegalArgumentException ex) {
            log(LogSeverity.SEVERE, BundleKey.LOG_INITIALIZATION_ERROR, new Object[]{ex.getMessage()}, edition.getId());
            return;
        }

        log(LogSeverity.INFO, BundleKey.LOG_NEWS_ITEMS_IN_EDITION, new Object[]{edition.getNumberOfPlacements()}, edition.getId());

        for (NewsItemPlacement nip : edition.getPlacements()) {
            processPlacement(nip);
        }

        if (this.errors > 0) {
            log(LogSeverity.WARNING, BundleKey.LOG_ERRORS_ENCOUNTERED_DURING_PROCESSING, new Object[]{this.errors}, edition.getId());
        } else {
            log(LogSeverity.INFO, BundleKey.LOG_NO_ERRORS_ENCOUNTERED_DURING_PROCESSING, edition.getId());
        }
        log(LogSeverity.INFO, BundleKey.LOG_FINISHED_UPLOADING_EDITION, edition.getId());
    }

    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement, Edition edition, OutletEditionAction action) {
        this.errors = 0;
        this.action = action;
        this.pluginContext = ctx;

        NewsItem newsItem = placement.getNewsItem();
        Long editionId = edition.getId();
        Long newsItemId = newsItem.getId();

        log(LogSeverity.INFO, BundleKey.LOG_PROCESSING_PLACEMENT, new Object[]{newsItemId, newsItem.getTitle()}, editionId, newsItemId);

        try {
            init();
        } catch (IllegalArgumentException ex) {
            log(LogSeverity.SEVERE, BundleKey.LOG_INITIALIZATION_ERROR, new Object[]{ex.getMessage()}, editionId, newsItemId);
            return;
        }
        processPlacement(placement);

        log(LogSeverity.INFO, BundleKey.LOG_ERRORS_ENCOUNTERED_DURING_PROCESSING, new Object[]{this.errors}, editionId, newsItemId);
        log(LogSeverity.INFO, BundleKey.LOG_PLACEMENT_UPLOADED, new Object[]{newsItemId}, editionId, newsItemId);
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

            for (Property p : Property.values()) {
                availableProperties.put(bundle.getString(p.name()), p.name());
            }
        }

        return availableProperties;
    }

    @Override
    public String getName() {
        return getMsg(BundleKey.PLUGIN_NAME);
    }

    @Override
    public String getDescription() {
        return getMsg(BundleKey.PLUGIN_DESCRIPTION);
    }

    @Override
    public String getVendor() {
        return getMsg(BundleKey.PLUGIN_VENDOR);
    }

    @Override
    public Date getDate() {
        try {
            return DATE_FORMAT.parse(getMsg(BundleKey.PLUGIN_BUILD_TIME));
        } catch (ParseException e) {
            return new Date();
        }
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public String getAbout() {
        return getMsg(BundleKey.PLUGIN_ABOUT);
    }

    /**
     * Initialises the plug-in. The initialisation includes loading and
     * validating properties and preparing the Wordpress client.
     */
    private void init() {
        Map<String, String> properties = action.getPropertiesAsMap();

        String url = properties.get(Property.URL.name());
        String username = properties.get(Property.USERNAME.name());
        String password = properties.get(Property.PASSWORD.name());
        Integer connectionTimeout = NumberUtils.toInt(properties.get(Property.CONNECTION_TIMEOUT.name()), DEFAULT_CONNECTION_TIMEOUT);
        Integer replyTimeout = NumberUtils.toInt(properties.get(Property.REPLY_TIMEOUT.name()), DEFAULT_REPLY_TIMEOUT);

        this.uploadWorkflowStateId = NumberUtils.toLong(properties.get(Property.UPLOAD_STATE.name()));
        this.uploadSuccessfulWorkflowOptionId = NumberUtils.toLong(properties.get(Property.UPLOADED_TRANSITION.name()));
        this.uploadFailedWorkflowOptionId = NumberUtils.toLong(properties.get(Property.FAILED_TRANSITION.name()));
        this.authorId = NumberUtils.toInt(properties.get(Property.AUTHOR_ID.name()), 1);
        this.rendition = properties.get(Property.IMAGE_RENDITION.name());

        if (url == null) {
            throw new IllegalArgumentException("'url' cannot be null");
        } else if (username == null) {
            throw new IllegalArgumentException("'username' cannot be null");
        } else if (password == null) {
            throw new IllegalArgumentException("'password' cannot be null");
        } else if (uploadWorkflowStateId == 0) {
            throw new IllegalArgumentException("'upload state' must be a valid identifier of a Workflow State");
        } else if (uploadSuccessfulWorkflowOptionId == 0) {
            throw new IllegalArgumentException("'uploaded transition' must be a valid identifier of a Workflow State Option");
        } else if (uploadFailedWorkflowOptionId == 0) {
            throw new IllegalArgumentException("'failed transition' must be a valid identifier of a Workflow State Option");
        }

        this.wordpress = new WpXmlRpcClient(url, username, password, connectionTimeout, replyTimeout);
    }

    /**
     * Process a single {@link NewsItemPlacement}. The processing includes
     * creating or updating a corresponding node in Drupal.
     *
     * @param nip {@link NewsItemPlacement} to process
     */
    private void processPlacement(NewsItemPlacement nip) {
        NewsItem newsItem = nip.getNewsItem();
        Edition edition = nip.getEdition();
        Long newsItemId = newsItem.getId();
        Long editionId = edition.getId();

        // Ignore NewsItem if it hasn't reached the "upload" state of the workflow
        if (!newsItem.getCurrentState().getId().equals(this.uploadWorkflowStateId)) {
            log(LogSeverity.FINE, BundleKey.LOG_NEWS_ITEM_INCOMPLETE, new Object[]{newsItemId, newsItem.getTitle(), newsItem.getCurrentState().getName()}, editionId, newsItemId);
            return;
        }

        NewsItemEditionState stateVersion = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, State.VERSION.name(), "0");
        NewsItemEditionState stateStatus = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, State.STATUS.name(), "");
        NewsItemEditionState stateWpId = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, State.WORDPRESS_ID.name(), null);
        NewsItemEditionState stateUrl = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, State.URL.name(), null);
        NewsItemEditionState stateUploadDate = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, State.UPLOAD_DATE.name(), new Date().toString());
        stateUploadDate.setValue(new Date().toString());

        boolean exist = false;
        Integer postId = Integer.valueOf(stateWpId.getValue());

        try {
            if (postId != null) {
                exist = wordpress.exists(postId);
            }

            if (exist) {
                boolean updated = wordpress.editPost(postId, "post", PostStatus.PUBLISH, this.authorId, newsItem.getTitle(), newsItem.getStory(), newsItem.getBrief(), getConcepts(newsItem), getCategories(edition, newsItem));
                if (updated) {
                    stateStatus.setValue("Updated");
                    throw new WpXmlRpcClientException("Could not update post on Wordpress");
                } else {
                    stateStatus.setValue("Failed updating");
                }
            } else {
                postId = wordpress.createPost("post", PostStatus.PUBLISH, this.authorId, newsItem.getTitle(), newsItem.getStory(), newsItem.getBrief(), getConcepts(newsItem), getCategories(edition, newsItem));
                stateStatus.setValue("Created");
            }

            for (NewsItemMediaAttachment attachment : newsItem.getMediaAttachments()) {
                StringBuilder images = new StringBuilder();
                try {
                    MediaItemRendition image = attachment.getMediaItem().findRendition(this.rendition);
                    Map<String, Object> uploadedFile = wordpress.uploadFile(image.getFilename(), image.getContentType(), FileUtils.getBytes(new File(image.getFileLocation())), true, postId);
                    images.append(getImageHtml(uploadedFile, attachment));
                } catch (IOException ex) {
                    log(LogSeverity.WARNING, BundleKey.LOG_IMAGE_UPLOAD_FAILED, new Object[]{newsItemId, newsItem.getTitle(), ex.getMessage()}, editionId, newsItemId);
                } catch (RenditionNotFoundException ex) {
                    log(LogSeverity.WARNING, BundleKey.LOG_IMAGE_UPLOAD_MISSING_RENDITION, new Object[]{newsItemId, newsItem.getTitle(), this.rendition}, editionId, newsItemId);
                }
                if (!images.toString().isEmpty()) {
                    wordpress.editPost(postId, "post", PostStatus.PUBLISH, this.authorId, newsItem.getTitle(), images.toString() + newsItem.getStory(), newsItem.getBrief(), getConcepts(newsItem), getCategories(edition, newsItem));
                }
            }

            stateWpId.setValue(String.valueOf(postId));
            stateVersion.setValue(String.valueOf(newsItem.getUpdated().getTimeInMillis()));
            pluginContext.updateNewsItemEditionState(stateVersion);
            pluginContext.updateNewsItemEditionState(stateStatus);
            pluginContext.updateNewsItemEditionState(stateWpId);
            pluginContext.updateNewsItemEditionState(stateUrl);
            pluginContext.updateNewsItemEditionState(stateUploadDate);

            try {
                this.pluginContext.workflowTransition(newsItemId, UserAccount.SYSTEM_ACCOUNT, this.uploadSuccessfulWorkflowOptionId);
            } catch (WorkflowStateTransitionException wstex) {
                log(LogSeverity.SEVERE, BundleKey.LOG_COULD_NOT_TRANSITION_WORKFLOW, new Object[]{newsItemId, newsItem.getTitle(), wstex.getMessage()}, editionId, newsItemId);
                this.errors++;
            }
        } catch (WpXmlRpcClientException ex) {
            log(LogSeverity.SEVERE, BundleKey.LOG_ERROR_PROCESS_PLACEMENT, new Object[]{newsItem.getId(), newsItem.getTitle(), ex}, editionId, newsItemId);
            this.errors++;
            try {
                this.pluginContext.workflowTransition(newsItemId, UserAccount.SYSTEM_ACCOUNT, this.uploadFailedWorkflowOptionId);
            } catch (WorkflowStateTransitionException wstex) {
                log(LogSeverity.SEVERE, BundleKey.LOG_COULD_NOT_TRANSITION_WORKFLOW, new Object[]{newsItemId, newsItem.getTitle(), wstex.getMessage()}, editionId, newsItemId);
                this.errors++;
            }
        }
    }

    private void log(LogSeverity severity, BundleKey message, Long editionId) {
        log(severity, message, new Object[]{}, editionId);
    }

    private void log(LogSeverity severity, BundleKey message, Long editionId, Long newsItemId) {
        log(severity, message, new Object[]{}, editionId, newsItemId);
    }

    private void log(LogSeverity severity, BundleKey message, Object[] arguments, Long editionId) {
        if (pluginContext == null) {
            LOG.log(Level.SEVERE, "PluginContext not yet set. Cannot log");
            return;
        }
        LogSubject logEdition = new LogSubject(Edition.class.getName(), String.valueOf(editionId));
        List<LogSubject> subjects = new ArrayList<LogSubject>();
        subjects.add(logEdition);
        pluginContext.log(severity, getMsg(message), arguments, subjects);
    }

    private void log(LogSeverity severity, BundleKey message, Object[] arguments, Long editionId, Long newsItemId) {
        if (pluginContext == null) {
            LOG.log(Level.SEVERE, "PluginContext not yet set. Cannot log");
            return;
        }
        LogSubject logEdition = new LogSubject(Edition.class.getName(), String.valueOf(editionId));
        LogSubject logNewsItem = new LogSubject(NewsItem.class.getName(), String.valueOf(newsItemId));
        List<LogSubject> subjects = new ArrayList<LogSubject>();
        subjects.add(logEdition);
        subjects.add(logNewsItem);
        pluginContext.log(severity, getMsg(message), arguments, subjects);
    }

    /**
     * Gets a message from the {@link #bundle}.
     *
     * @param key Key of the message to retrieve
     * @return Message stored behind the key
     */
    private String getMsg(BundleKey key) {
        try {
            return bundle.getString(key.name());
        } catch (MissingResourceException ex) {
            return key.name();
        }
    }
}
