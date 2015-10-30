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

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.plugins.drupal.converters.EntityConverter;
import dk.i2m.converge.plugins.drupal.entities.NodeEntity;
import dk.i2m.converge.plugins.drupal.wrappers.DateWrapper;
import dk.i2m.converge.plugins.drupal.wrappers.FieldWrapper;
import dk.i2m.converge.plugins.drupal.wrappers.VocabWrapper;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Useful methods for Drupal Edition Action.
 */
public class DrupalUtils {

    protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected static final String KEY_BODY = "BODY";
    protected static final String KEY_DATE = "DATE";
    protected static final String KEY_BYLINE = "BYLINE";
    protected static final String KEY_SECTION = "SECTION";
    protected static final String KEY_NEWSITEM_ID = "NEWSITEM_ID";
    protected static final String KEY_EDITION_ID = "EDITION_ID";
    protected static final String KEY_POSITION = "PLACEMENT_POSITION";
    protected static final String KEY_START = "PLACEMENT_START";
    protected static final String KEY_IMAGE = "IMAGE";

    private static final Logger LOG = Logger.getLogger(DrupalUtils.class.getName());

    private static final String SEPARATOR_A = ",";
    private static final String SEPARATOR_B = ":";

    private static final int TITLE_LENGTH_NEWS_ITEM = 255;
    private static final int ALT_LENGTH_MEDIA_ITEM = 512;
    private static final int TITLE_LENGTH_MEDIA_ITEM = 1024;

    /**
     * @param properties
     * @param names
     */
    protected static void checkProperties(Map<String, String> properties, List<String> names) {
        StringBuilder errors = new StringBuilder();

        for (String name : names) {
            if (properties.get(name) == null) {
                errors.append('\n');
                errors.append(String.format("%s may not be null", name));
            }
        }

        if (errors.length() != 0) {
            throw new IllegalArgumentException(errors.toString());
        }
    }

    /**
     * Convert a {@link String} into an array.
     * <p>
     * <p>The schema used is:
     * <pre>
     * &lt;THIS&gt;:&lt;THAT&gt;,
     * &lt;THIS&gt;:&lt;THAT&gt;,
     * &lt;THIS&gt;:&lt;THAT&gt;,
     * &lt;THIS&gt;:&lt;THAT&gt;,
     * ...
     * </pre>
     *
     * @param string the string to be converted
     * @return
     */
    protected static String[] convertStringArrayA(String string) {
        return string.trim().split(SEPARATOR_A);
    }

    /**
     * Convert a {@link String} into an array.
     * <p>
     * <p>The schema used is:
     * <pre>
     * &lt;THIS&gt;:
     * &lt;THAT&gt;:
     * &lt;THIS&gt;:
     * &lt;THAT&gt;...
     * </pre>
     *
     * @param string the string to be converted
     * @return
     */
    protected static String[] convertStringArrayB(String string) {
        return string.trim().split(SEPARATOR_B);
    }

    /**
     * Convert a {@link String} into a {@link Map}.
     * <p>
     * The schema used is:
     * <pre>
     * &lt;THIS&gt;:&lt;THAT&gt;,
     * &lt;THIS&gt;:&lt;THAT&gt;,
     * &lt;THIS&gt;:&lt;THAT&gt;,
     * &lt;THIS&gt;:&lt;THAT&gt;,
     * ...
     * </pre>
     *
     * @param string the string to be converted
     * @return <code>&lt;THIS&gt;</code>, <code>&lt;THAT&gt;</code> {@link Map}
     */
    protected static Map<String, String> convertStringMap(String string) {
        Map<String, String> map = new HashMap<String, String>();
        // Split string into individual items
        String[] arrayA = convertStringArrayA(string);

        for (String s : arrayA) {
            // Split individual items into key - var
            String[] arrayB = convertStringArrayB(s);

            // Handle out of bounds
            if (1 < arrayB.length) {
                String thisKey = arrayB[0].trim();
                String thatKey = arrayB[1].trim();
                map.put(thisKey, thatKey);
            } else {
                return null;
            }
        }

        return map;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param strings mapping
     * @param key     the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key
     */
    protected static String getKeyValue(String[] strings, String key) {
        for (String s : strings) {
            // Split individual items into key - var
            String[] arrayB = convertStringArrayB(s);

            // Handle out of bounds
            if (1 < arrayB.length) {
                // Caller handles out of bounds
                String thisKey = arrayB[0].trim();
                String thatKey = arrayB[1].trim();
                if (thisKey.equals(key)) {
                    return thatKey;
                }
            } else {
                return null;
            }
        }

        return null;
    }

    /**
     * Name and value parts for a multi-part request.
     *
     * @param placement
     * @param nodeType
     * @param fields
     * @param sections
     * @return
     */
    protected static Map<String, String> nodeParams(NewsItemPlacement placement, String nodeType, String[] fields,
                                                    Map<String, String> sections) {
        Map<String, String> nodeParams = new LinkedHashMap<String, String>();
        NodeEntity nodeEntity = new NodeEntity();
        FieldWrapper fieldWrapper = new FieldWrapper();
        Edition edition = placement.getEdition();
        NewsItem newsItem = placement.getNewsItem();

        String title = StringEscapeUtils.escapeHtml(newsItem.getTitle());
        String body = getKeyValue(fields, KEY_BODY);
        String date = getKeyValue(fields, KEY_DATE);
        String byline = getKeyValue(fields, KEY_BYLINE);
        String sectionId = getKeyValue(fields, KEY_SECTION);
        String newsItemId = getKeyValue(fields, KEY_NEWSITEM_ID);
        String editionId = getKeyValue(fields, KEY_EDITION_ID);
        String start = getKeyValue(fields, KEY_START);
        String position = getKeyValue(fields, KEY_POSITION);

        nodeEntity.setType(nodeType);
        nodeEntity.setTitle(StringUtils.left(title, TITLE_LENGTH_NEWS_ITEM));
        nodeParams.putAll(new EntityConverter<NodeEntity>().convert(nodeEntity));

        if (body != null) {
            nodeParams.putAll(fieldWrapper.wrap(body, newsItem.getStory()));
        }

        if (date != null) {
            DateWrapper dateWrapper = new DateWrapper();
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // Set the timezone
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String time = sdf.format(edition.getPublicationDate().getTime());
            nodeParams.putAll(dateWrapper.wrap(date, time));
        }

        if (byline != null) {
            nodeParams.putAll(fieldWrapper.wrap(byline, newsItem.getAuthors()));
        }

        if (sectionId != null) {
            VocabWrapper vocabWrapper = new VocabWrapper();
            String id = String.valueOf(placement.getSection().getId());
            nodeParams.putAll(vocabWrapper.wrap(sectionId, sections.get(id)));
        }

        if (newsItemId != null) {
            nodeParams.putAll(fieldWrapper.wrap(newsItemId, newsItem.getId()));
        }

        if (editionId != null) {
            nodeParams.putAll(fieldWrapper.wrap(editionId, edition.getId()));
        }

        if (start != null && placement.getStart() != null) {
            nodeParams.putAll(fieldWrapper.wrap(start, placement.getStart()));
        }

        if (position != null && placement.getPosition() != null) {
            nodeParams.putAll(fieldWrapper.wrap(position, placement.getPosition()));
        }

        return nodeParams;
    }

    /**
     * Name and value parts for a multi-part request.
     *
     * @param newsItem
     * @param forceRendition
     * @return
     */
    protected static Map<String, Object> fileParams(NewsItem newsItem, String renditionName, String[] forceRendition) {
        Map<String, Object> fileParams = new LinkedHashMap<String, Object>();
        Tika tika = new Tika();
        int index = 0;

        for (NewsItemMediaAttachment attachment : newsItem.getMediaAttachments()) {
            MediaItem mediaItem = attachment.getMediaItem();

            // Verify that the mediaItem exists and renditions are attached
            if (mediaItem != null && mediaItem.isRenditionsAttached()) {
                try {
                    MediaItemRendition rendition = null;

                    if (forceRendition != null) {
                        // FORCE_SYNC fallback renditions
                        for (String rnd : forceRendition) {
                            try {
                                rendition = mediaItem.findRendition(rnd);
                                break;
                            } catch (RenditionNotFoundException ex) {
                                // Continue
                            }
                        }

                        if (rendition == null) {
                            throw new RenditionNotFoundException();
                        }
                    } else {
                        rendition = mediaItem.findRendition(renditionName);
                    }

                    File file = new File(rendition.getFileLocation());
                    // FIXME: Better file check, avoid IOException
                    String mediaType = tika.detect(file); // rendition.getContentType();
                    // Generated renditions have duplicate filenames. To avoid
                    // this, explicitly set the upload filename
                    String fileName = String.format("%d.%s", mediaItem.getId(), rendition.getExtension());

                    NamedTypedFile typedFile = new NamedTypedFile(mediaType, file, fileName.toLowerCase());
                    String fileAlt = StringUtils.abbreviate(attachment.getCaption(), ALT_LENGTH_MEDIA_ITEM);
                    String fileTitle = StringUtils.abbreviate(attachment.getCaption(), TITLE_LENGTH_MEDIA_ITEM);

                    fileParams.put(String.format("files[%d]", index), typedFile);
                    fileParams.put(String.format("field_values[%d][alt]", index), fileAlt);
                    fileParams.put(String.format("field_values[%d][title]", index), fileTitle);

                    index++;
                } catch (RenditionNotFoundException ex) {
                    LOG.log(Level.WARNING, "Skipping MediaItem #{0} - Missing rendition (\"{1}\")", new Object[]{
                            mediaItem.getId(), renditionName});
                } catch (IOException ex) {
                    LOG.log(Level.WARNING, "Skipping MediaItem #{0}: {1}", new Object[]{
                            mediaItem.getId(), ex.getMessage()});
                    LOG.log(Level.FINEST, null, ex);
                }
            }
        }

        return fileParams;
    }
}
