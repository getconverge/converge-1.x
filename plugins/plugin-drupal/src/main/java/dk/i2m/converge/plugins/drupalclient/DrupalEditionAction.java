/*
 * Copyright (C) 2012 - 2013 Interactive Media Management
 * Copyright (C) 2014 - 2015 Allan Lykke Christensen
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
package dk.i2m.converge.plugins.drupalclient;

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
import java.nio.charset.Charset;
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
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import static dk.i2m.converge.plugins.drupalclient.DrupalEditionActionHelper.*;
import java.util.ArrayList;
import java.util.MissingResourceException;

/**
 * {@link EditionAction} for uploading content to Drupal websites using the
 * Services module.
 *
 * @author Raymond Wanyoike
 * @author Allan Lykke Christensen
 */
@OutletAction
public class DrupalEditionAction implements EditionAction {

    public enum Property {

        CONNECTION_TIMEOUT,
        IMAGE_RENDITION,
        NODE_LANGUAGE,
        NODE_TYPE,
        PASSWORD,
        PUBLISH_DELAY,
        PUBLISH_IMMEDIATELY,
        SECTION_MAPPING,
        IGNORED_MAPPING,
        SERVICE_ENDPOINT,
        SOCKET_TIMEOUT,
        URL,
        USERNAME
    }

    private static final int DEFAULT_SOCKET_TIMEOUT = 30000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 30000;

    /**
     * This is the resource type created for the Converge service in Drupal.
     */
    private static final String CONVERGE_DRUPAL_RESOURCE_TYPE = "newsitem";
    private static final String DRUPAL_IMAGE_FIELD_NAME = "field_image";
    private static final Logger LOG = Logger.getLogger(DrupalEditionAction.class.getName());
    private static final DateFormat DRUPAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String UPLOADING = "UPLOADING";
    private static final String UPLOADED = "UPLOADED";
    private static final String FAILED = "FAILED";
    public static final String STATE_DATE = "date";
    public static final String STATE_NID = "nid";
    public static final String STATE_URI = "uri";
    public static final String STATE_PATH = "path";
    public static final String STATE_STATUS = "status";
    public static final String STATE_LAST_UPDATED = "version";
    private final ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.drupalclient.Messages");
    private Map<String, String> availableProperties;
    private OutletEditionAction action;
    private PluginContext pluginContext;
    private DrupalServicesClient drupalServiceClient;
    private int errors = 0;

    // Keys from the resource bundle
    public enum BundleKey {

        PLUGIN_NAME,
        PLUGIN_DESCRIPTION,
        PLUGIN_VENDOR,
        PLUGIN_BUILD_TIME,
        PLUGIN_ABOUT,
        LOG_UPLOAD_INITIATED,
        LOG_COULD_NOT_LOGIN,
        LOG_NEWS_ITEMS_IN_EDITION,
        LOG_COULD_NOT_CONNECT,
        LOG_ERRORS_ENCOUNTERED_DURING_PROCESSING,
        LOG_NO_ERRORS_ENCOUNTERED_DURING_PROCESSING,
        LOG_FINISHED_UPLOADING_EDITION,
        LOG_PROCESSING_PLACEMENT,
        LOG_LOGIN_SUCCESSFUL,
        LOG_COULD_NOT_PROCESS,
        LOG_PLACEMENT_UPLOADED,
        LOG_NEWS_ITEM_INCOMPLETE,
        LOG_NEWS_ITEM_SECTION_UNMAPPED,
        LOG_COULD_NOT_CHECK_IF_EXISTS,
        LOG_CREATING_STATE_FOR_NEWS_ITEM,
        LOG_CREATING_DRUPAL_NODE_AND_UPLOADING_IMAGES,
        LOG_CREATED_DRUPAL_NODE,
        LOG_IMAGES_UPLOADED_TO_DRUPAL,
        LOG_GETTING_DRUPAL_PATH,
        LOG_COULD_NOT_CREATE,
        LOG_UPDATING_STATE_FOR_NEWS_ITEM,
        LOG_FINISHED_UPLOAD_NEWS_ITEM,
        LOG_NODE_OUTDATED,
        LOG_NODE_UP_TO_DATE,
        LOG_UPDATING_DRUPAL_NODE_AND_UPLOADING_IMAGES,
        LOG_COULD_NOT_UPDATE
    }

    @Override
    public void execute(PluginContext ctx, Edition edition, OutletEditionAction action) {
        this.pluginContext = ctx;
        this.errors = 0;
        this.action = action;

        log(LogSeverity.INFO, BundleKey.LOG_UPLOAD_INITIATED, new Object[]{}, edition.getId());

        init();

        try {
            if (!this.drupalServiceClient.login()) {
                log(LogSeverity.SEVERE, BundleKey.LOG_COULD_NOT_LOGIN, edition.getId());
                return;
            }
            log(LogSeverity.INFO, BundleKey.LOG_NEWS_ITEMS_IN_EDITION, new Object[]{edition.getNumberOfPlacements()}, edition.getId());

            for (NewsItemPlacement nip : edition.getPlacements()) {
                processPlacement(nip);
            }

        } catch (DrupalServerConnectionException ex) {
            log(LogSeverity.SEVERE, BundleKey.LOG_COULD_NOT_CONNECT, new Object[]{ex.getMessage()}, edition.getId());
            LOG.log(Level.FINEST, null, ex);
            return;
        } finally {
            try {
                drupalServiceClient.logout();
            } catch (DrupalServerConnectionException ex) {
                LOG.log(Level.SEVERE, "{2}", new Object[]{action.getId(), action.getLabel(), ex.getMessage()});
                LOG.log(Level.FINEST, null, ex);
            }
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

        init();

        try {
            if (!drupalServiceClient.login()) {
                log(LogSeverity.SEVERE, BundleKey.LOG_COULD_NOT_LOGIN, editionId, newsItemId);
                return;
            } else {
                log(LogSeverity.INFO, BundleKey.LOG_LOGIN_SUCCESSFUL, editionId, newsItemId);
            }
            processPlacement(placement);
        } catch (DrupalServerConnectionException ex) {
            log(LogSeverity.SEVERE, BundleKey.LOG_COULD_NOT_PROCESS, new Object[]{newsItemId, newsItem.getTitle(), ex.getMessage()}, editionId, newsItemId);
            LOG.log(Level.FINEST, null, ex);
            this.errors++;
            return;
        } finally {
            try {
                drupalServiceClient.logout();
            } catch (DrupalServerConnectionException ex) {
                LOG.log(Level.SEVERE, "{2}", new Object[]{action.getId(), action.getLabel(), ex.getMessage()});
                LOG.log(Level.FINEST, null, ex);
                this.errors++;
            }
        }

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
            return DRUPAL_DATE_FORMAT.parse(getMsg(BundleKey.PLUGIN_BUILD_TIME));
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
     * validating properties and preparing the Drupal services client.
     */
    private void init() {
        Map<String, String> properties = action.getPropertiesAsMap();

        String hostname = properties.get(Property.URL.name());
        String endpoint = properties.get(Property.SERVICE_ENDPOINT.name());
        String username = properties.get(Property.USERNAME.name());
        String password = properties.get(Property.PASSWORD.name());
        Integer connectionTimeout = NumberUtils.toInt(properties.get(Property.CONNECTION_TIMEOUT.name()), DEFAULT_CONNECTION_TIMEOUT);
        Integer socketTimeout = NumberUtils.toInt(properties.get(Property.SOCKET_TIMEOUT.name()), DEFAULT_SOCKET_TIMEOUT);

        if (hostname == null) {
            throw new IllegalArgumentException("'hostname' cannot be null");
        } else if (endpoint == null) {
            throw new IllegalArgumentException("'endpoint' cannot be null");
        } else if (username == null) {
            throw new IllegalArgumentException("'username' cannot be null");
        } else if (password == null) {
            throw new IllegalArgumentException("'password' cannot be null");
        }

        this.drupalServiceClient = new DrupalServicesClient(hostname, endpoint, username, password, socketTimeout, connectionTimeout);
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

        // Ignore NewsItem if it hasn't reached the end state of the workflow
        if (!newsItem.isEndState()) {
            log(LogSeverity.WARNING, BundleKey.LOG_NEWS_ITEM_INCOMPLETE, new Object[]{newsItemId, newsItem.getTitle(), newsItem.getCurrentState().getName()}, editionId, newsItemId);
            return;
        }

        // Ignore NewsItem if the section of the NewsItemPlacement is not mapped
        List<NameValuePair> params;
        try {
            NewsItemPlacementToNameValuePairsConverter converter = new NewsItemPlacementToNameValuePairsConverter();
            params = converter.convert(action, nip);
        } catch (UnmappedSectionException ex) {
            log(LogSeverity.WARNING, BundleKey.LOG_NEWS_ITEM_SECTION_UNMAPPED, new Object[]{newsItemId, newsItem.getTitle(), ex.getMessage()}, editionId, newsItemId);
            LOG.log(Level.FINEST, null, ex);
            return;
        }

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Charset.defaultCharset());

        boolean update;
        try {
            // determine if the news item is already uploaded
            update = this.drupalServiceClient.exists(CONVERGE_DRUPAL_RESOURCE_TYPE, nip.getNewsItem().getId());
        } catch (DrupalServerConnectionException ex) {
            log(LogSeverity.SEVERE, BundleKey.LOG_COULD_NOT_CHECK_IF_EXISTS, new Object[]{newsItemId, newsItem.getTitle(), ex.getMessage()}, editionId, newsItemId);
            LOG.log(Level.FINEST, null, ex);
            errors++;
            return;
        }

        NewsItemPlacementToFileInfosConverter fileInfosConverter = new NewsItemPlacementToFileInfosConverter();
        List<FileInfo> mediaItems = fileInfosConverter.convert(action, nip);

        if (update) {
            updateNode(nip, mediaItems, entity);
        } else {
            createNode(nip, mediaItems, entity);
        }
    }

    private void createNode(NewsItemPlacement newsItemPlacement, List<FileInfo> mediaItems, UrlEncodedFormEntity entity) {
        NewsItem newsItem = newsItemPlacement.getNewsItem();
        Edition edition = newsItemPlacement.getEdition();
        Long newsItemId = newsItem.getId();
        Long editionId = edition.getId();

        log(LogSeverity.INFO, BundleKey.LOG_CREATING_STATE_FOR_NEWS_ITEM, new Object[]{newsItemId}, editionId, newsItemId);
        NewsItemEditionState status = pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATE_STATUS, UPLOADING);
        NewsItemEditionState nid = pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATE_NID, null);
        NewsItemEditionState uri = pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATE_URI, null);
        NewsItemEditionState path = pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATE_PATH, null);
        NewsItemEditionState version = pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATE_LAST_UPDATED, "0");
        pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATE_DATE, new Date().toString());

        log(LogSeverity.INFO, BundleKey.LOG_CREATING_DRUPAL_NODE_AND_UPLOADING_IMAGES, new Object[]{newsItemId, mediaItems.size()}, editionId, newsItemId);

        try {
            NodeInfo newNode = drupalServiceClient.createNode(entity);
            log(LogSeverity.INFO, BundleKey.LOG_CREATED_DRUPAL_NODE, new Object[]{newsItemId, newsItem.getTitle()}, editionId, newsItemId);
            if (mediaItems.size() > 0) {
                drupalServiceClient.attachFile(newNode.getId(), DRUPAL_IMAGE_FIELD_NAME, mediaItems);
                log(LogSeverity.INFO, BundleKey.LOG_IMAGES_UPLOADED_TO_DRUPAL, new Object[]{newsItemId}, editionId, newsItemId);
            }

            log(LogSeverity.INFO, BundleKey.LOG_GETTING_DRUPAL_PATH, new Object[]{newsItemId}, editionId, newsItemId);
            String nodePath = drupalServiceClient.retrieveNodePath(newNode.getId());

            // Updating NewsItem Edition States
            nid.setValue(newNode.getId().toString());
            uri.setValue(newNode.getUri());
            path.setValue(nodePath);
            version.setValue(String.valueOf(newsItem.getUpdated().getTimeInMillis()));
            status.setValue(UPLOADED);
        } catch (DrupalServerConnectionException ex) {
            this.errors++;
            status.setValue(FAILED);
            log(LogSeverity.SEVERE, BundleKey.LOG_COULD_NOT_CREATE, new Object[]{ex.getMessage()}, editionId, newsItemId);
            LOG.log(Level.FINEST, "", ex);
        }

        log(LogSeverity.INFO, BundleKey.LOG_UPDATING_STATE_FOR_NEWS_ITEM, new Object[]{newsItemId}, editionId, newsItemId);
        pluginContext.updateNewsItemEditionState(status);
        pluginContext.updateNewsItemEditionState(nid);
        pluginContext.updateNewsItemEditionState(uri);
        pluginContext.updateNewsItemEditionState(path);
        pluginContext.updateNewsItemEditionState(version);

        log(LogSeverity.INFO, BundleKey.LOG_FINISHED_UPLOAD_NEWS_ITEM, new Object[]{newsItemId, newsItem.getTitle()}, editionId, newsItemId);
    }

    private void updateNode(NewsItemPlacement nip, List<FileInfo> mediaItems, UrlEncodedFormEntity entity) {
        NewsItem ni = nip.getNewsItem();
        Edition edition = nip.getEdition();
        Long newsItemId = ni.getId();
        Long editionId = edition.getId();

        NewsItemEditionState version = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, STATE_LAST_UPDATED, "0");
        NewsItemEditionState status = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, STATE_STATUS, UPLOADING);
        NewsItemEditionState nid = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, STATE_NID, null);
        NewsItemEditionState uri = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, STATE_URI, null);
        NewsItemEditionState path = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, STATE_PATH, null);
        NewsItemEditionState submitted = pluginContext.findNewsItemEditionStateOrCreate(edition.getId(), newsItemId, STATE_DATE, null);

        try {
            if (isDrupalNodeOutdated(ni, version)) {
                log(LogSeverity.INFO, BundleKey.LOG_NODE_OUTDATED, new Object[]{newsItemId}, editionId, newsItemId);
                submitted.setValue(new Date().toString());
                pluginContext.updateNewsItemEditionState(submitted);

                Long nodeId = drupalServiceClient.retrieveNodeIdFromResource(CONVERGE_DRUPAL_RESOURCE_TYPE, newsItemId);

                log(LogSeverity.INFO, BundleKey.LOG_UPDATING_DRUPAL_NODE_AND_UPLOADING_IMAGES, new Object[]{nodeId, newsItemId, mediaItems.size()}, editionId, newsItemId);

                NodeInfo updatedNode = drupalServiceClient.updateNode(nodeId, entity);
                //TODO; Remove existing media items
                if (mediaItems.size() > 0) {
                    drupalServiceClient.attachFile(nodeId, DRUPAL_IMAGE_FIELD_NAME, mediaItems);
                    log(LogSeverity.INFO, BundleKey.LOG_IMAGES_UPLOADED_TO_DRUPAL, new Object[]{newsItemId}, editionId, newsItemId);
                }

                log(LogSeverity.INFO, BundleKey.LOG_GETTING_DRUPAL_PATH, new Object[]{newsItemId}, editionId, newsItemId);
                String nodePath = drupalServiceClient.retrieveNodePath(updatedNode.getId());

                // Updating NewsItem Edition States
                nid.setValue(String.valueOf(updatedNode.getId()));
                uri.setValue(updatedNode.getUri());
                path.setValue(nodePath);
                version.setValue(String.valueOf(ni.getUpdated().getTimeInMillis()));
                status.setValue(UPLOADED);
            } else {
                log(LogSeverity.INFO, BundleKey.LOG_NODE_UP_TO_DATE, new Object[]{newsItemId}, editionId, newsItemId);
            }
        } catch (DrupalServerConnectionException ex) {
            this.errors++;
            status.setValue(FAILED);
            log(LogSeverity.SEVERE, BundleKey.LOG_COULD_NOT_UPDATE, new Object[]{ex.getMessage()}, editionId, newsItemId);
            LOG.log(Level.FINEST, "", ex);
        }

        log(LogSeverity.INFO, BundleKey.LOG_UPDATING_STATE_FOR_NEWS_ITEM, new Object[]{newsItemId}, editionId, newsItemId);

        pluginContext.updateNewsItemEditionState(status);
        pluginContext.updateNewsItemEditionState(nid);
        pluginContext.updateNewsItemEditionState(uri);
        pluginContext.updateNewsItemEditionState(path);
        pluginContext.updateNewsItemEditionState(version);
        log(LogSeverity.INFO, BundleKey.LOG_FINISHED_UPLOAD_NEWS_ITEM, new Object[]{newsItemId, ni.getTitle()}, editionId, newsItemId);
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
