/*
 * Copyright (C) 2012 - 2013 Interactive Media Management
 * Copyright (C) 2014 Allan Lykke Christensen
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
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.io.IOException;
import java.net.URISyntaxException;
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
    private static final Logger LOG = Logger.getLogger(DrupalEditionAction.class.getName());
    private static final String LOG_PREFIX = "[#{0}:{1}] ";
    private final DateFormat DRUPAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String UPLOADING = "UPLOADING";
    private static final String UPLOADED = "UPLOADED";
    private static final String FAILED = "FAILED";
    private static final String DATE = "date";
    private static final String NID_LABEL = "nid";
    private static final String URI_LABEL = "uri";
    private static final String STATUS_LABEL = "status";
    private final ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.drupalclient.Messages");
    private Map<String, String> availableProperties;
    private OutletEditionAction action;
    private PluginContext pluginContext;
    private DrupalServicesClient drupalServiceClient;
    private int errors = 0;

    @Override
    public void execute(PluginContext ctx, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, LOG_PREFIX + "Executing DrupalEditionAction on Edition #{2}", new Object[]{action.getId(), action.getLabel(), edition.getId()});
        this.errors = 0;
        this.action = action;
        this.pluginContext = ctx;

        init();

        try {
            if (!this.drupalServiceClient.login()) {
                LOG.log(Level.SEVERE, LOG_PREFIX + "Could not log-in to the configured Drupal Instance", new Object[]{action.getId(), action.getLabel()});
                return;
            }
            LOG.log(Level.INFO, LOG_PREFIX + "Number of items in Edition #{2}: {3}", new Object[]{action.getId(), action.getLabel(), edition.getId(), edition.getNumberOfPlacements()});

            for (NewsItemPlacement nip : edition.getPlacements()) {
                processPlacement(nip);
            }

        } catch (DrupalServerConnectionException ex) {
            LOG.log(Level.SEVERE, LOG_PREFIX + "{2}", new Object[]{action.getId(), action.getLabel(), ex.getMessage()});
            LOG.log(Level.FINEST, null, ex);
            return;
        } finally {
            try {
                drupalServiceClient.logout();
            } catch (DrupalServerConnectionException ex) {
                LOG.log(Level.SEVERE, LOG_PREFIX + "{2}", new Object[]{action.getId(), action.getLabel(), ex.getMessage()});
                LOG.log(Level.FINEST, null, ex);
            }
        }

        LOG.log(Level.INFO, LOG_PREFIX + "{2} errors encounted", new Object[]{action.getId(), action.getLabel(), this.errors});
        LOG.log(Level.INFO, LOG_PREFIX + "Finishing action. Edition #{2}", new Object[]{action.getId(), action.getLabel(), edition.getId()});
    }

    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, LOG_PREFIX + "Executing DrupalEditionAction for NewsItem #{2} in Edition #{3}", new Object[]{action.getId(), action.getLabel(), placement.getNewsItem().getId(), edition.getId()});
        this.errors = 0;
        this.action = action;
        this.pluginContext = ctx;

        init();

        try {
            if (!drupalServiceClient.login()) {
                LOG.log(Level.SEVERE, LOG_PREFIX + "Could not log-in to the configured Drupal Instance", new Object[]{action.getId(), action.getLabel()});
                return;
            } else {
                LOG.log(Level.INFO, LOG_PREFIX + "Logged into Drupal successfully", new Object[]{action.getId(), action.getLabel()});
            }
            processPlacement(placement);
        } catch (DrupalServerConnectionException ex) {
            LOG.log(Level.SEVERE, LOG_PREFIX + "{0}", new Object[]{action.getId(), action.getLabel(), ex.getMessage()});
            LOG.log(Level.FINEST, null, ex);
            return;
        } finally {
            try {
                drupalServiceClient.logout();
            } catch (DrupalServerConnectionException ex) {
                LOG.log(Level.SEVERE, LOG_PREFIX + "{2}", new Object[]{action.getId(), action.getLabel(), ex.getMessage()});
                LOG.log(Level.FINEST, null, ex);
            }
        }

        LOG.log(Level.INFO, "{0} errors encounted", new Object[]{this.errors});
        LOG.log(Level.INFO, "Finishing action. Edition #{0}", new Object[]{edition.getId()});
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
        return bundle.getString("PLUGIN_NAME");
    }

    @Override
    public String getDescription() {
        return bundle.getString("PLUGIN_DESCRIPTION");
    }

    @Override
    public String getVendor() {
        return bundle.getString("PLUGIN_VENDOR");
    }

    @Override
    public Date getDate() {
        try {
            return DRUPAL_DATE_FORMAT.parse(bundle.getString("PLUGIN_BUILD_TIME"));
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
        return bundle.getString("PLUGIN_ABOUT");
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

        // Ignore NewsItem if it hasn't reached the end state of the workflow
        if (!newsItem.isEndState()) {
            LOG.log(Level.INFO, LOG_PREFIX + "Ignoring NewsItemPlacement #{2} / NewsItem #{3}. Not yet complete.", new Object[]{action.getId(), action.getLabel(), nip.getId(), newsItem.getId()});
            return;
        }

        // Ignore NewsItem if the section of the NewsItemPlacement is not mapped
        List<NameValuePair> params;
        try {
            NewsItemPlacementToNameValuePairsConverter converter = new NewsItemPlacementToNameValuePairsConverter();
            params = converter.convert(action, nip);
        } catch (UnmappedSectionException ex) {
            LOG.log(Level.INFO, LOG_PREFIX + "Ignoring NewsItemPlacement #{2} / NewsItem #{3}. {4}", new Object[]{action.getId(), action.getLabel(), nip.getId(), newsItem.getId(), ex.getMessage()});
            return;
        }

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Charset.defaultCharset());

        boolean update;
        try {
            // determine if the news item is already uploaded
            update = this.drupalServiceClient.exists("newsitem", nip.getNewsItem().getId());
        } catch (DrupalServerConnectionException ex) {
            LOG.log(Level.SEVERE, LOG_PREFIX + "Could not determine if NewsItem #{2} exists. {3}", new Object[]{action.getId(), action.getLabel(), newsItem.getId(), ex.getMessage()});
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

        LOG.log(Level.INFO, "Creating new Node for NewsItem #{0} & {1} image(s)", new Object[]{newsItem.getId(), mediaItems.size()});

        NewsItemEditionState status = pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATUS_LABEL, UPLOADING);
        NewsItemEditionState nid = pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), NID_LABEL, null);
        NewsItemEditionState uri = pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), URI_LABEL, null);
        NewsItemEditionState submitted = pluginContext.addNewsItemEditionState(edition.getId(), newsItem.getId(), DATE, null);

        try {
            NodeInfo newNode = drupalServiceClient.createNode(entity);
            drupalServiceClient.attachFile(newNode.getId(), "field_image", mediaItems);

            nid.setValue(newNode.getId().toString());
            uri.setValue(newNode.getUri());
            submitted.setValue(new Date().toString());
            status.setValue(UPLOADED);
        } catch (DrupalServerConnectionException ex) {
            this.errors++;
            status.setValue(FAILED);
            LOG.log(Level.SEVERE, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);

            pluginContext.updateNewsItemEditionState(status);
            pluginContext.updateNewsItemEditionState(nid);
            pluginContext.updateNewsItemEditionState(uri);
            pluginContext.updateNewsItemEditionState(submitted);
        }

        pluginContext.updateNewsItemEditionState(status);
        pluginContext.updateNewsItemEditionState(nid);
        pluginContext.updateNewsItemEditionState(uri);
        pluginContext.updateNewsItemEditionState(submitted);
    }

    private void updateNode(NewsItemPlacement nip, List<FileInfo> mediaItems, UrlEncodedFormEntity entity) {
        try {
            Long nodeId = drupalServiceClient.retrieveNodeIdFromResource("newsitem", nip.getNewsItem().getId());
            LOG.log(Level.INFO, "Updating Node #{0} with NewsItem #{1} & {2} image(s)", new Object[]{nodeId, nip.getNewsItem().getId(), mediaItems.size()});
            drupalServiceClient.updateNode(nodeId, entity);
            drupalServiceClient.attachFile(nodeId, "field_image", mediaItems);
        } catch (DrupalServerConnectionException ex) {
            this.errors++;
            LOG.log(Level.SEVERE, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        }
    }

}
