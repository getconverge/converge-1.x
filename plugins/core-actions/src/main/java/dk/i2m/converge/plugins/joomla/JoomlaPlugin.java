/*
 * Copyright (C) 2010 - 2011 Interactive Media Management
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
package dk.i2m.converge.plugins.joomla;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemActor;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.core.metadata.Concept;
import dk.i2m.converge.core.utils.FileUtils;
import dk.i2m.converge.core.utils.StringUtils;
import dk.i2m.converge.plugins.joomla.client.JoomlaConnection;
import dk.i2m.converge.plugins.joomla.client.JoomlaException;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;

/**
 * Base functionality used by the Joomla plug-ins.
 *
 * @author Allan Lykke Christensen
 */
public abstract class JoomlaPlugin {

    private static final Logger LOG = Logger.getLogger(JoomlaPlugin.class.
            getName());

    protected static final String IMAGE_PATTERN =
            "<p><img class=\"caption\" src=\"{0}\" border=\"0\" alt=\"{1}\" title=\"{1}\" align=\"left\" /></p>";

    /** Adapter version for which the connector is compatible. */
    public static final String ADAPTER_COMPATIBILITY = "1.0";

    /** Name of the property containing the URL of the Joomla Adapter. */
    public static final String PROPERTY_URL = "joomla.url";

    /** Name of the method to invoke on the Converge XML-RPC API. */
    public static final String PROPERTY_METHOD = "joomla.method";

    /** Property containing the username for accessing the Joomla Adapter. */
    public static final String PROPERTY_USERNAME = "joomla.username";

    /** Property containing the password for accessing the Joomla Adapter. */
    public static final String PROPERTY_PASSWORD = "joomla.password";

    /** Property, if it exist, the news item will be posted immediately and the edition disregarded. */
    public static final String PROPERTY_POST_IMMEDIATELY = "post_immediately";

    /** Property, if it exist, the posting of the news item will delay for the number of minutes specified. */
    public static final String PROPERTY_POST_DELAY = "post_delay";

    /** Property, if it exist, it will use it to determine after how many hours, the news item should expire. */
    public static final String PROPERTY_EXPIRE_AFTER = "expire_after";

    /** Property, if it exist, the news item will never expire automatically. */
    public static final String PROPERTY_EXPIRE_NEVER = "expire_never";

    /** Property containing the undisclosed author label for articles with undisclosed authors. */
    public static final String PROPERTY_UNDISCLOSED_AUTHOR_LABEL =
            "undisclosed_author_label";

    /** Name of the property containing media version label to use when uploading news items. */
    public static final String PROPERTY_MEDIA_TAG = "media.tag";

    public static final String PROPERTY_IMAGE_UPLOAD = "image.upload";

    public static final String PROPERTY_CATEGORY_IMAGE_RESIZE =
            "category.image.resize";

    /** Property containing the mapping of a single section in the system with a category in Joomla. */
    public static final String PROPERTY_CATEGORY_MAPPING = "category_mapping";

    public static final String PROPERTY_FRONTPAGE_MAPPING = "mapping.frontpage";

    public static final String PROPERTY_EXCLUDE_MEDIA_ITEM_CONTENT_TYPE =
            "exclude.mediaitem.contenttype";

    public static final String PROPERTY_XMLRPC_TIMEOUT = "xmlrpc.timeout";

    /** Property containing the XMLRPC reply timeout. */
    public static final String PROPERTY_XMLRPC_REPLY_TIMEOUT =
            "xmlrpc.timeout.reply";

    /** XML RPC method for determining the version of the adapter. */
    public static final String XMLRPC_METHOD_VERSION = "converge.version";

    /** XML RPC method for obtaining categories of the Joomla installation. */
    public static final String XMLRPC_METHOD_LIST_CATEGORIES =
            "converge.listCategories";

    /** XML RPC method for submitting a new article. */
    public static final String XMLRPC_METHOD_NEW_ARTICLE = "converge.newArticle";

    /** XML RPC method for updating an existing article. */
    public static final String XMLRPC_METHOD_UPDATE_ARTICLE =
            "converge.editArticle";

    /** XML RPC method for deleting an existing article. */
    public static final String XMLRPC_METHOD_DELETE_ARTICLE =
            "converge.deleteArticle";

    /** XML RPC method for uploading a photo. */
    public static final String XMLRPC_METHOD_NEW_MEDIA = "converge.newMedia";

    protected Map<String, String> properties = new HashMap<String, String>();

    protected Map<String, String> categoryMapping =
            new HashMap<String, String>();

    protected Map<String, String> categoryImageMapping =
            new HashMap<String, String>();

    protected Map<String, Integer> categoryExpire =
            new HashMap<String, Integer>();

    protected Map<String, Integer> categoryPublish =
            new HashMap<String, Integer>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm Z");

    private Map<Integer, String> joomlaCategories =
            new HashMap<Integer, String>();

    private Map<String, String> availableProperties = null;

    protected static final int DEFAULT_TIMEOUT = 30;

    protected static final int DEFAULT_REPLY_TIMEOUT = 0;

    public abstract ResourceBundle getBundle();

    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            addProperty(PROPERTY_URL);
            addProperty(PROPERTY_METHOD);
            addProperty(PROPERTY_USERNAME);
            addProperty(PROPERTY_PASSWORD);
            addProperty(PROPERTY_CATEGORY_MAPPING);
            addProperty(PROPERTY_FRONTPAGE_MAPPING);
            addProperty(PROPERTY_UNDISCLOSED_AUTHOR_LABEL);
            addProperty(PROPERTY_POST_IMMEDIATELY);
            addProperty(PROPERTY_EXPIRE_AFTER);
            addProperty(PROPERTY_EXPIRE_NEVER);
            addProperty(PROPERTY_POST_DELAY);
            addProperty(PROPERTY_MEDIA_TAG);
            addProperty(PROPERTY_IMAGE_UPLOAD);
            addProperty(PROPERTY_XMLRPC_TIMEOUT);
            addProperty(PROPERTY_XMLRPC_REPLY_TIMEOUT);
            addProperty(PROPERTY_CATEGORY_IMAGE_RESIZE);
            addProperty(PROPERTY_EXCLUDE_MEDIA_ITEM_CONTENT_TYPE);
        }
        return availableProperties;
    }

    private void addProperty(String key) {
        availableProperties.put(getBundle().getString(key), key);
    }

    /**
     * Determines if the section of the news item has been mapped to
     * a valid category on the Joomla installation. The method assumes
     * the the Joomla categories has been fetched ({@link JoomlaPlugin#fetchJoomlaCategories(dk.i2m.joomla.JoomlaConnection)})
     * before execution.
     *
     * @param placement News item to check
     * @return {@code true} if the section of the news item is
     * mapped to a valid category on the Joomla installation
     * otherwise {@code false}
     */
    protected boolean isCategoryMapped(NewsItemPlacement placement) {
        if (placement.getSection() == null) {
            if (categoryMapping.containsKey("0")) {
                String catId = categoryMapping.get("0");

                try {
                    return joomlaCategories.containsKey(Integer.valueOf(catId));
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, ex.getMessage(), ex);
                    return false;
                }
            } else {
                return false;
            }
        }

        if (categoryMapping.containsKey(String.valueOf(placement.getSection().
                getId()))) {
            String catId = categoryMapping.get(String.valueOf(placement.
                    getSection().getId()));
            try {
                return joomlaCategories.containsKey(Integer.valueOf(catId));
            } catch (Exception ex) {
                LOG.log(Level.WARNING, ex.getMessage(), ex);
                return false;
            }
        } else {
            return false;
        }
    }

    protected String generateCategoryId(NewsItemPlacement placement) {
        if (placement.getSection() == null) {
            if (categoryMapping.containsKey("0")) {
                return categoryMapping.get("0");
            } else {
                return "0";
            }
        }
        if (categoryMapping.containsKey(String.valueOf(placement.getSection().
                getId()))) {
            return categoryMapping.get(String.valueOf(placement.getSection().
                    getId()));
        } else {
            return "0";
        }
    }

    protected String generateMetaDescription(NewsItem item) {
        String description = StringUtils.stripHtml(item.getStory());
        description = org.apache.commons.lang.StringUtils.abbreviate(description,
                450);
        return description;
    }

    protected String generateIntro(NewsItem item,
            List<UploadedMediaFile> mediaFiles) {
        String htmlStrippedStory = StringUtils.stripHtml(item.getStory());
        htmlStrippedStory = htmlStrippedStory.replaceAll("\\p{C}", "");
        StringBuilder intro = new StringBuilder();

        for (UploadedMediaFile umf : mediaFiles) {
            String img = MessageFormat.format(IMAGE_PATTERN, new Object[]{umf.
                        getUrl(), umf.getCaption()});
            intro.append(img);
            break;
        }
        intro.append(org.apache.commons.lang.StringUtils.abbreviate(
                htmlStrippedStory, 450));

        return intro.toString();
    }

    protected String generateKeywords(NewsItem item) {
        StringBuilder keywords = new StringBuilder();
        boolean first = true;
        for (Concept concept : item.getConcepts()) {
            if (!first) {
                keywords.append(", ");
            } else {
                first = false;
            }

            keywords.append(concept.getName());
        }

        return keywords.toString();
    }

    protected String generateStory(NewsItem item,
            List<UploadedMediaFile> mediaFiles) {
        StringBuilder body = new StringBuilder();
        for (UploadedMediaFile umf : mediaFiles) {
            String img = MessageFormat.format(IMAGE_PATTERN, new Object[]{umf.
                        getUrl(), umf.getCaption()});
            body.append(img);
        }

        body.append(item.getStory().replaceAll("\\p{C}", ""));
        return body.toString();
    }

    protected String generateAuthors(NewsItem item) {
        if (item.isUndisclosedAuthor()) {
            return properties.get(PROPERTY_UNDISCLOSED_AUTHOR_LABEL);
        } else {
            if (item.getByLine().trim().isEmpty()) {
                StringBuilder by = new StringBuilder();

                for (NewsItemActor actor : item.getActors()) {
                    boolean firstActor = true;
                    if (actor.getRole().equals(item.getOutlet().getWorkflow().
                            getStartState().getActorRole())) {
                        if (!firstActor) {
                            by.append(", ");
                        } else {
                            firstActor = false;
                        }
                        by.append(actor.getUser().getFullName());
                    }
                }
                return by.toString();
            } else {
                return item.getByLine();
            }
        }
    }

    protected boolean generateFrontPage(NewsItemPlacement placement) {
        if (properties.containsKey(PROPERTY_FRONTPAGE_MAPPING)) {
            if (properties.get(PROPERTY_FRONTPAGE_MAPPING).equalsIgnoreCase(String.
                    valueOf(placement.getStart()))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected Date generatePublishDate(NewsItemPlacement placement) {

        // Check if the section has specific instructions
        if (placement.getSection() != null) {
            if (categoryPublish.containsKey(String.valueOf(placement.getSection().
                    getId()))) {
                int hours = categoryPublish.get(String.valueOf(placement.
                        getSection().getId()));

                Date now = Calendar.getInstance().getTime();
                if (placement.getEdition() != null) {
                    now = placement.getEdition().getPublicationDate().getTime();
                }
                Calendar publishDate = Calendar.getInstance();
                publishDate.setTime(now);
                publishDate.add(Calendar.HOUR_OF_DAY, hours);
                return publishDate.getTime();
            }
        }

        if (properties.containsKey(PROPERTY_POST_IMMEDIATELY) && properties.
                containsKey(PROPERTY_POST_DELAY)) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR_OF_DAY, Integer.valueOf(properties.get(
                    PROPERTY_POST_DELAY)));
            return now.getTime();
        } else if (properties.containsKey(PROPERTY_POST_IMMEDIATELY)) {
            return null;
        } else if (properties.containsKey(PROPERTY_POST_DELAY)) {
            Date now = Calendar.getInstance().getTime();
            if (placement.getEdition() != null) {
                now = placement.getEdition().getPublicationDate().getTime();
            }
            Calendar publishDate = Calendar.getInstance();
            publishDate.setTime(now);
            publishDate.add(Calendar.HOUR_OF_DAY,
                    Integer.valueOf(properties.get(PROPERTY_POST_DELAY)));
            return publishDate.getTime();
        } else {
            return placement.getEdition().getPublicationDate().getTime();
        }
    }

    /**
     * Generates the expiration date of a news item. There are two
     * properties determining the expiration date;
     * {@link JoomlaPlugin#PROPERTY_EXPIRE_NEVER} and
     * {@link JoomlaPlugin#PROPERTY_EXPIRE_AFTER}.
     *
     * If neither property is set, the expiration date and time will
     * be the same as the expiration date of the edition.
     *
     * If {@link JoomlaPlugin#PROPERTY_EXPIRE_NEVER} is set, the
     * news item will not have a expiration date (returns {@code null}).
     *
     * If {@link JoomlaPlugin#PROPERTY_EXPIRE_AFTER} is set, the
     * news item will add the number of hours specified in the property
     * to the expiration date of the edition.
     *
     * @param placement News item placement for which to generate an expiration date
     * @return Date and time when the news item should expire. {@code null}
     * is returned if the news item should not have an expiration date
     */
    protected Date generateExpireDate(NewsItemPlacement placement) {
        // Check if the section has specific instructions
        if (placement.getSection() != null) {
            if (categoryExpire.containsKey(String.valueOf(placement.getSection().
                    getId()))) {
                int hours = categoryExpire.get(String.valueOf(placement.
                        getSection().getId()));

                Date now = Calendar.getInstance().getTime();
                if (placement.getEdition() != null) {
                    now = placement.getEdition().getExpirationDate().getTime();
                }
                Calendar expireDate = Calendar.getInstance();
                expireDate.setTime(now);
                expireDate.add(Calendar.HOUR_OF_DAY, hours);
                return expireDate.getTime();
            }
        }

        if (properties.containsKey(PROPERTY_EXPIRE_NEVER)) {
            return null;
        } else {
            if (properties.containsKey(PROPERTY_EXPIRE_AFTER)) {
                int hours = Integer.valueOf(
                        properties.get(PROPERTY_EXPIRE_AFTER));
                Date now = Calendar.getInstance().getTime();
                if (placement.getEdition() != null) {
                    now = placement.getEdition().getExpirationDate().getTime();
                }
                Calendar expireDate = Calendar.getInstance();
                expireDate.setTime(now);
                expireDate.add(Calendar.HOUR_OF_DAY, hours);
                return expireDate.getTime();
            } else {
                return placement.getEdition().getExpirationDate().getTime();
            }
        }
    }

    protected void fetchJoomlaCategories(JoomlaConnection connection) {
        try {
            this.joomlaCategories = connection.listCategories();
        } catch (JoomlaException ex) {
            LOG.log(Level.WARNING, "Could not fetch Joomla categories", ex);
        }
    }

    /**
     * Connects to the Joomla installation and uploads a new news item.
     *
     * @param connection Connection to the Joomla installation
     * @param placement  News item to upload
     * @return Unique identifier of the create article on Joomla
     * @throws JoomlaActionException If the news item could not be uploaded
     */
    protected Integer newArticle(JoomlaConnection connection,
            NewsItemPlacement placement) throws JoomlaActionException {

        NewsItem newsItem = placement.getNewsItem();
        try {

            String joomlaCategoryId = generateCategoryId(placement);

            boolean imageUpload = true;
            if (properties.containsKey(PROPERTY_IMAGE_UPLOAD)) {
                imageUpload = Boolean.parseBoolean(properties.get(
                        PROPERTY_IMAGE_UPLOAD));
            }

            List<UploadedMediaFile> mediaFiles =
                    new ArrayList<UploadedMediaFile>();
            if (imageUpload) {
                LOG.log(Level.INFO, "Uploading media files for news item #{0}",
                        new Object[]{newsItem.getId()});
                mediaFiles = uploadMediaItems(connection, newsItem,
                        joomlaCategoryId);
            }

            Date publishDate = generatePublishDate(placement);
            Date expireDate = generateExpireDate(placement);

            LOG.log(Level.INFO, "Original publish date for #{0} set to {1}. Publish date on Joomla set to {2}. Expire date on Joomla set to {3}", new Object[]{newsItem.getId(), dateFormat.format(placement.getEdition().getPublicationDate().getTime()), dateFormat.format(publishDate.getTime()), expireDate != null ? dateFormat.format(expireDate.getTime()) : "null"});


            Integer foreignId = connection.newArticle(String.valueOf(newsItem.
                    getId()),
                    newsItem.getTitle(), generateIntro(newsItem, mediaFiles),
                    generateStory(newsItem, mediaFiles), generateAuthors(
                    newsItem),
                    joomlaCategoryId, generateFrontPage(placement),
                    String.valueOf(placement.getStart())
                    + String.valueOf(placement.getPosition()),
                    generateKeywords(newsItem),
                    generateMetaDescription(newsItem),
                    publishDate, expireDate);
            

            if (foreignId != null) {
                LOG.log(Level.INFO,
                        "News item #{0} created or updated in Joomla with article id #{1}",
                        new Object[]{newsItem.getId(), foreignId});
                return foreignId;
            } else {
                LOG.log(Level.WARNING,
                        "Foreign key was not received from Converge Joomla API");
                return 0;
            }

        } catch (JoomlaException ex) {
            throw new JoomlaActionException(ex);
        }
    }

    protected void deleteArticle(JoomlaConnection connection, NewsItem newsItem)
            throws JoomlaActionException {
        try {
            LOG.log(Level.INFO, "Deleting news item #{0} from Joomla",
                    new Object[]{newsItem.getId()});
            connection.deleteArticle(String.valueOf(newsItem.getId()));
        } catch (JoomlaException ex) {
            throw new JoomlaActionException(ex);
        }
    }

    protected List<UploadedMediaFile> uploadMediaItems(
            JoomlaConnection connection, NewsItem newsItem,
            String joomlaCategoryId) {
        String renditionName = properties.get(PROPERTY_MEDIA_TAG);
        String[] excludeContentTypes = new String[]{};
        if (properties.containsKey(PROPERTY_EXCLUDE_MEDIA_ITEM_CONTENT_TYPE)) {
            excludeContentTypes = properties.get(
                    PROPERTY_EXCLUDE_MEDIA_ITEM_CONTENT_TYPE).split(";");
        }

        MediaItemRendition webVersion = null;
        List<UploadedMediaFile> uploadedImages =
                new ArrayList<UploadedMediaFile>();

        for (NewsItemMediaAttachment attachment : newsItem.getMediaAttachments()) {

            MediaItem item = attachment.getMediaItem();
            // verify that the item exist and renditions are attached
            if (item == null || !item.isRenditionsAttached()) {
                continue;
            }

            // Check if there is a category setting for this media item
            if (this.categoryImageMapping.containsKey(joomlaCategoryId)) {
                LOG.log(Level.FINE, "Special settings for Joomla Category {0}",
                        new Object[]{joomlaCategoryId});
                String imgCat = this.categoryImageMapping.get(joomlaCategoryId);
                String[] imgCatSettings = imgCat.split(";");
                renditionName = imgCatSettings[1];
            }

            // Check if a rendition of the image exist
            String renditionFile = "";
            try {
                webVersion = item.findRendition(renditionName);

                // Check if the file should be excluded
                if (ArrayUtils.contains(excludeContentTypes, webVersion.
                        getContentType())) {
                    LOG.log(Level.FINE,
                            "Ignoring media item #{0} with content type {1}",
                            new Object[]{item.getId(),
                                webVersion.getContentType()});
                    continue;
                }

                String filename = newsItem.getId() + "-" + webVersion.getId()
                        + "." + webVersion.getExtension();
                renditionFile = webVersion.getAbsoluteFilename();
                byte[] filedata = FileUtils.getBytes(new URL(renditionFile));
                String webImgLocation = connection.uploadMediaFile(String.
                        valueOf(newsItem.getId()), filename, filedata);
                uploadedImages.add(new UploadedMediaFile(webImgLocation,
                        attachment.getCaption()));

            } catch (RenditionNotFoundException ex) {
                LOG.log(Level.WARNING,
                        "Rendition ({0}) missing for Media Item #{1}. Ignoring Media Item.",
                        new Object[]{renditionName, item.getId()});
            } catch (IOException ex) {
                LOG.log(Level.WARNING,
                        "Rendition ({0} / {2}) could not be retrieved for Media Item #{1}. Ignoring Media Item.",
                        new Object[]{renditionName, item.getId(), renditionFile});
            } catch (JoomlaException ex) {
                LOG.log(Level.WARNING, "Could not upload media file to Joomla",
                        ex);
            }
        }

        return uploadedImages;
    }
}
