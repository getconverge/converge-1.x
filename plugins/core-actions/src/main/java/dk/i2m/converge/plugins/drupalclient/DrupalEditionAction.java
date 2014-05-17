/*
 * Copyright (C) 2012 - 2013 Interactive Media Management
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
import dk.i2m.converge.core.content.NewsItemActor;
import dk.i2m.converge.core.content.NewsItemEditionState;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import dk.i2m.converge.core.workflow.OutletEditionActionProperty;
import dk.i2m.converge.core.workflow.Section;
import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

/**
 * {@link EditionAction} for uploading content to Drupal websites using the
 * Services module.
 *
 * @author Raymond Wanyoike
 * @author <a href="mailto:allan@i2m.dk">Allan Lykke Christensen</a>
 */
@OutletAction
public class DrupalEditionAction implements EditionAction {

    public static final int MEDIA_ITEM_TITLE_LENGTH = 1024;
    private String hostname;
    private String endpoint;
    private String username;
    private String password;
    private String connectionTimeout;
    private String socketTimeout;
    private DrupalServicesClient drupalServiceClient;
    private int errors = 0;

    private enum Property {

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
    private static final Logger LOG = Logger.getLogger(DrupalEditionAction.class.getName());
    private static final String UPLOADING = "UPLOADING";
    private static final String UPLOADED = "UPLOADED";
    private static final String FAILED = "FAILED";
    private static final String DATE = "date";
    private static final String NID_LABEL = "nid";
    private static final String URI_LABEL = "uri";
    private static final String STATUS_LABEL = "status";
    private ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.drupalclient.Messages");
    private Map<String, String> availableProperties;
    private Map<Long, Long> sectionMapping;
    private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String publishDelay;
    private String publishImmediately;
    private String renditionName;
    private String nodeLanguage;
    private String nodeType;
    private String mappings;

    @Override
    public void execute(PluginContext ctx, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, "Executing DrupalEditionAction on Edition #{0}", edition.getId());

        init(action);
        this.errors = 0;

        try {
            if (!this.drupalServiceClient.login()) {
                LOG.log(Level.SEVERE, "Could not log-in to the configured Drupal Instance");
                return;
            }
        } catch (DrupalServerConnectionException ex) {
            LOG.log(Level.SEVERE, "Could not log-in to the configured Drupal Instance. {0}", ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
            return;
        }

        LOG.log(Level.INFO, "Number of items in Edition #{0}: {1}", new Object[]{edition.getId(), edition.getNumberOfPlacements()});

        for (NewsItemPlacement nip : edition.getPlacements()) {
            processPlacement(ctx, nip);
        }

        try {
            drupalServiceClient.logout();
        } catch (DrupalServerConnectionException ex) {
            LOG.log(Level.SEVERE, "Could not log-out. {0}", ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        }

        LOG.log(Level.WARNING, "{0} errors encounted", new Object[]{this.errors});
        LOG.log(Level.INFO, "Finishing action. Edition #{0}", new Object[]{edition.getId()});
    }

    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, "Executing DrupalEditionAction for NewsItem #{0} in Edition #{1}", new Object[]{placement.getNewsItem().getId(), edition.getId()});

        init(action);
        this.errors = 0;

        try {
            if (!this.drupalServiceClient.login()) {
                LOG.log(Level.SEVERE, "Could not log-in to the configured Drupal Instance");
                return;
            }
        } catch (DrupalServerConnectionException ex) {
            LOG.log(Level.SEVERE, "Could not log-in to the configured Drupal Instance. {0}", ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
            return;
        }

        processPlacement(ctx, placement);

        try {
            drupalServiceClient.logout();
        } catch (DrupalServerConnectionException ex) {
            LOG.log(Level.SEVERE, "Could not log-out. {0}", ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        }

        LOG.log(Level.WARNING, "{0} errors encounted", new Object[]{this.errors});
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
            return sdf.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception e) {
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
     * Decodes the section mappings and adding each mapping to
     * {@link #sectionMapping}.
     *
     * @param mapping mapping to set
     */
    private void setSectionMapping(String mapping) {
        String[] values = mapping.split(";");

        for (int i = 0; i < values.length; i++) {
            String[] value = values[i].split(":");
            Long convergeId = Long.valueOf(value[0].trim());
            Long drupalId = Long.valueOf(value[1].trim());
            sectionMapping.put(convergeId, drupalId);
            LOG.log(Level.INFO, "Mapping Converge Section #{0} to Drupal Section #{1}", new Object[]{convergeId, drupalId});
        }

        LOG.log(Level.INFO, "Found {0} Section mapping(s)", sectionMapping.size());
    }

    /**
     * Get Publish on text value.
     *
     * @return "YYYY-MM-DD HH:MM:SS" or ""
     */
    private String getPublishOn(Edition edition) {
        if (publishImmediately != null) {
            return null;
        }

        Calendar calendar = (Calendar) edition.getPublicationDate().clone();
        calendar.add(Calendar.HOUR_OF_DAY, Integer.valueOf(publishDelay));

        return sdf.format(calendar.getTime());
    }

    /**
     * Get Author text field.
     *
     * @param newsItem {@link NewsItem}
     * @return By-line to use for the story when published on Drupal
     */
    private String getAuthor(NewsItem newsItem) {
        if (newsItem.isUndisclosedAuthor()) {
            return "N/A";
        } else {
            if (StringUtils.isBlank(newsItem.getByLine())) {
                // No by-line specified in the news item. 
                // Generate by-line from actors on news item

                StringBuilder sb = new StringBuilder();

                // Iterate through actors specified on the news item
                boolean firstActor = true;
                for (NewsItemActor actor : newsItem.getActors()) {

                    // If the actor has the role from the initial state of the 
                    // workflow, he is the author of the story
                    if (actor.getRole().equals(newsItem.getOutlet().getWorkflow().getStartState().getActorRole())) {
                        if (!firstActor) {
                            sb.append(", ");
                        } else {
                            firstActor = false;
                        }

                        sb.append(actor.getUser().getFullName());
                    }
                }

                return sb.toString();
            } else {
                // Return the "by-line" of the NewsItem
                return newsItem.getByLine();
            }
        }
    }

    /**
     * Gets the ID of the Drupal section where the {@link NewsItemPlacement}
     * should be placed.
     *
     * @param nip {@link NewsItemPlacement} for which to get the Drupal section
     * @return ID of the Drupal section where the {@link NewsItemPlacement}
     * should be placed
     * @throws UnmappedSectionException If the {@link NewsItemPlacement} is not
     * mapped to a section in Drupal
     */
    private String getSection(NewsItemPlacement nip) throws UnmappedSectionException {
        Section section = nip.getSection();

        if (section != null) {
            if (sectionMapping.containsKey(section.getId())) {
                return sectionMapping.get(section.getId()).toString();
            }
        }

        throw new UnmappedSectionException(section + " is not mapped");
    }

    /**
     * Get {@link ImageField}s for {@link NewsItem}.
     *
     * @param newsItem NewsItem
     * @return
     */
    private List<FileInfo> getMediaItems(NewsItem newsItem) {
        List<FileInfo> mediaItems = new ArrayList<FileInfo>();

        for (NewsItemMediaAttachment nima : newsItem.getMediaAttachments()) {
            MediaItem mediaItem = nima.getMediaItem();

            // Verify that the item exist and any renditions are attached
            if (mediaItem == null || !mediaItem.isRenditionsAttached()) {
                continue;
            } else {
                try {
                    MediaItemRendition rendition = mediaItem.findRendition(this.renditionName);
                    String abbreviatedCaption = StringUtils.abbreviate(nima.getCaption(), MEDIA_ITEM_TITLE_LENGTH);
                    mediaItems.add(new FileInfo(new File(rendition.getFileLocation()), abbreviatedCaption));
                    LOG.log(Level.FINE, "Adding Rendition #{0} Located at: {1} with Caption: {2} Capped to: {3}", new Object[]{rendition.getId(), rendition.getFileLocation(), nima.getCaption(), abbreviatedCaption});
                } catch (RenditionNotFoundException ex) {
                    LOG.log(Level.INFO, "Rendition ''{0}'' missing for MediaItem #{1}. MediaItem #{1} will not be uploaded.", new Object[]{renditionName, mediaItem.getId()});
                    continue;
                }
            }
        }

        return mediaItems;
    }

    private boolean isInteger(String input) {
        try {
            Integer.valueOf(input);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Initialises the plug-in. The initialisation includes loading and
     * validating properties and preparing the Drupal services client.
     *
     * @param action {@link OutletEditionAction} that should be used for
     * initialisation
     */
    private void init(OutletEditionAction action) {
        Map<String, String> properties = action.getPropertiesAsMap();

        //mappings = properties.get(Property.SECTION_MAPPING.name());

        StringBuilder mapBuilder = new StringBuilder();
        for (OutletEditionActionProperty actionProperty : action.getProperties()) {
            if (actionProperty.getKey().equalsIgnoreCase(Property.SECTION_MAPPING.name())) {
                if (mapBuilder.length() > 0) {
                    mapBuilder.append(";");
                }
                mapBuilder.append(actionProperty.getValue());
            }
        }
        mappings = mapBuilder.toString();

        nodeType = properties.get(Property.NODE_TYPE.name());
        nodeLanguage = properties.get(Property.NODE_LANGUAGE.name());
        publishDelay = properties.get(Property.PUBLISH_DELAY.name());
        publishImmediately = properties.get(Property.PUBLISH_IMMEDIATELY.name());
        renditionName = properties.get(Property.IMAGE_RENDITION.name());

        sectionMapping = new HashMap<Long, Long>();

        this.hostname = properties.get(Property.URL.name());
        this.endpoint = properties.get(Property.SERVICE_ENDPOINT.name());
        this.username = properties.get(Property.USERNAME.name());
        this.password = properties.get(Property.PASSWORD.name());
        this.connectionTimeout = properties.get(Property.CONNECTION_TIMEOUT.name());
        this.socketTimeout = properties.get(Property.SOCKET_TIMEOUT.name());

        if (hostname == null) {
            throw new IllegalArgumentException("'hostname' cannot be null");
        } else if (endpoint == null) {
            throw new IllegalArgumentException("'endpoint' cannot be null");
        } else if (username == null) {
            throw new IllegalArgumentException("'username' cannot be null");
        } else if (password == null) {
            throw new IllegalArgumentException("'password' cannot be null");
        }

        if (nodeType == null) {
            throw new IllegalArgumentException("'nodeType' cannot be null");
        } else if (mappings == null) {
            throw new IllegalArgumentException("'mappings' cannot be null");
        }

        if (publishImmediately == null && publishDelay == null) {
            throw new IllegalArgumentException("'publishImmediately' or 'publishDelay' cannot be null");
        } else if (publishImmediately == null && publishDelay != null) {
            if (!isInteger(publishDelay)) {
                throw new IllegalArgumentException("'publishDelay' must be an integer");
            } else if (Integer.valueOf(publishDelay) <= 0) {
                throw new IllegalArgumentException("'publishDelay' cannot be <= 0");
            }
        }

        if (connectionTimeout == null) {
            connectionTimeout = "30000"; // 30 seconds
        } else if (!isInteger(connectionTimeout)) {
            throw new IllegalArgumentException("'connectionTimeout' must be an integer");
        }

        if (socketTimeout == null) {
            socketTimeout = "30000"; // 30 seconds
        } else if (!isInteger(socketTimeout)) {
            throw new IllegalArgumentException("'socketTimeout' must be an integer");
        }

        setSectionMapping(mappings);

        this.drupalServiceClient = new DrupalServicesClient(hostname, endpoint, username, password, Integer.valueOf(socketTimeout), Integer.valueOf(connectionTimeout));
    }

    /**
     * Process a single {@link NewsItemPlacement}. The processing includes
     * creating or updating a corresponding node in Drupal.
     *
     * @param ctx {@link PluginContext}
     * @param nip {@link NewsItemPlacement} to process
     */
    private void processPlacement(PluginContext ctx, NewsItemPlacement nip) {

        Edition edition = nip.getEdition();

        NewsItem newsItem = nip.getNewsItem();

        // Ignore NewsItem if it hasn't reached the end state of the workflow
        if (!newsItem.isEndState()) {
            return;
        }

        // Ignore NewsItem if the section of the NewsItemPlacement is not mapped
        try {
            getSection(nip);
        } catch (UnmappedSectionException usex) {
            return;
        }

        boolean update;
        try {
            // determine if the news item is already uploaded
            update = this.drupalServiceClient.exists("newsitem", nip.getNewsItem().getId());
        } catch (DrupalServerConnectionException ex) {
            LOG.log(Level.SEVERE, "Could not determine if NewsItem #{0} is already update. {1}", new Object[]{newsItem.getId(), ex.getMessage()});
            LOG.log(Level.FINEST, null, ex);
            errors++;
            return;
        }

        UrlEncodedFormEntity entity = toUrlEncodedFormEntity(nip, getPublishOn(edition));
        List<FileInfo> mediaItems = getMediaItems(newsItem);

        if (update) {
            try {
                Long nodeId = drupalServiceClient.retrieveNodeIdFromResource("newsitem", nip.getNewsItem().getId());
                LOG.log(Level.INFO, "Updating Node #{0} with NewsItem #{1} & {2} image(s)", new Object[]{nodeId, newsItem.getId(), mediaItems.size()});
                drupalServiceClient.updateNode(nodeId, entity);
                drupalServiceClient.attachFile(nodeId, "field_image", mediaItems);
            } catch (Exception ex) {
                this.errors++;
                LOG.log(Level.SEVERE, ex.getMessage());
                LOG.log(Level.FINEST, "", ex);
            }
        } else {
            LOG.log(Level.INFO, "Creating new Node for NewsItem #{0} & {1} image(s)", new Object[]{newsItem.getId(), mediaItems.size()});

            NewsItemEditionState status = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATUS_LABEL, UPLOADING.toString());
            NewsItemEditionState nid = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), NID_LABEL, null);
            NewsItemEditionState uri = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), URI_LABEL, null);
            NewsItemEditionState submitted = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), DATE, null);

            try {
                NodeInfo newNode = drupalServiceClient.createNode(entity);
                drupalServiceClient.attachFile(newNode.getId(), "field_image", mediaItems);

                nid.setValue(newNode.getId().toString());
                uri.setValue(newNode.getUri().toString());
                submitted.setValue(new Date().toString());
                status.setValue(UPLOADED.toString());
            } catch (DrupalServerConnectionException ex) {
                this.errors++;
                status.setValue(FAILED.toString());
                LOG.log(Level.SEVERE, ex.getMessage());
                LOG.log(Level.FINEST, "", ex);

                ctx.updateNewsItemEditionState(status);
                ctx.updateNewsItemEditionState(nid);
                ctx.updateNewsItemEditionState(uri);
                ctx.updateNewsItemEditionState(submitted);
            }

            ctx.updateNewsItemEditionState(status);
            ctx.updateNewsItemEditionState(nid);
            ctx.updateNewsItemEditionState(uri);
            ctx.updateNewsItemEditionState(submitted);
        }
    }

    /**
     * Turns a {@link NewsItemPlacement} into a {@link UrlEncodedFormEntity}
     * that can be sent to the Drupal Services API.
     *
     * @param nip {@link NewsItemPlacement} to turn into a
     * {@link UrlEncodedFormEntity}
     * @param publishOn Date when the {@link NewsItem} should be published
     * @return {@link UrlEncodedFormEntity} based on the
     * {@link NewsItemPlacement}
     */
    private UrlEncodedFormEntity toUrlEncodedFormEntity(NewsItemPlacement nip, String publishOn) {
        Edition edition = nip.getEdition();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("type", this.nodeType));
        params.add(new BasicNameValuePair("publish_on", publishOn));
        params.add(new BasicNameValuePair("date", sdf.format(edition.getPublicationDate().getTime())));
        params.add(new BasicNameValuePair("title", StringUtils.left(StringEscapeUtils.escapeHtml(nip.getNewsItem().getTitle()), 255)));
        params.add(new BasicNameValuePair("language", "und"));
        //params.add(new BasicNameValuePair("body[und][0][summary]", nip"This is the summary"));
        params.add(new BasicNameValuePair("body[und][0][value]", nip.getNewsItem().getStory()));
        params.add(new BasicNameValuePair("body[und][0][format]", "full_html"));
        params.add(new BasicNameValuePair("field_author[und][0][value]", getAuthor(nip.getNewsItem())));
        params.add(new BasicNameValuePair("field_newsitem[und][0][value]", "" + nip.getNewsItem().getId()));
        params.add(new BasicNameValuePair("field_edition[und][0][value]", "" + nip.getEdition().getId()));
        try {
            params.add(new BasicNameValuePair("field_section[und][0]", getSection(nip)));
        } catch (UnmappedSectionException ex) {
            // Section not mapped
        }
        if (nip.getStart() != null) {
            LOG.log(Level.FINE, "NewsItemPlacement # {0}. Setting Placement Start (" + nip.getStart() + ")", nip.getId());
            params.add(new BasicNameValuePair("field_placement_start[und][0]", nip.getStart().toString()));
        } else {
            LOG.log(Level.FINE, "NewsItemPlacement # {0}. Skipping Placement Start (null)", nip.getId());
        }

        if (nip.getPosition() != null) {
            LOG.log(Level.FINE, "NewsItemPlacement # {0}. Setting Placement Position (" + nip.getPosition() + ")", nip.getId());
            params.add(new BasicNameValuePair("field_placement_position[und][0]", nip.getPosition().toString()));
        } else {
            LOG.log(Level.FINE, "NewsItemPlacement # {0}. Skipping Placement Position (null)", nip.getId());
        }

        return new UrlEncodedFormEntity(params, Charset.defaultCharset());
    }
}
