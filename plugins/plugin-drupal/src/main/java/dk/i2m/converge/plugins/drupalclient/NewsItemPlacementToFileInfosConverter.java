/*
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

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 * Responsible for turning a {@link NewsItemPlacement} into a {@link List} of
 * {@link FileInfo} objects for sending to Drupal.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemPlacementToFileInfosConverter {

    private static final Logger LOG = Logger.getLogger(NewsItemPlacementToFileInfosConverter.class.getName());
    private static final int MEDIA_ITEM_TITLE_LENGTH = 1024;
    private NewsItem newsItem;
    private Map<String, String> actionProperties;
    private String renditionName;

    /**
     * Takes {@link NewsItemMediaAttachment}s from the {@link NewsItemPlacement}
     * and creates a {@link List} of {@link FileInfo}s with relevant data that
     * should be sent to Drupal.
     *
     * @param action {@link OutletEditionAction} containing the plug-in
     * configuration
     * @param placement {@link NewsItemPlacement} from where attachments should
     * be extracted
     * @return {@link List} of {@link FileInfo} with relevant data for Drupal
     */
    public List<FileInfo> convert(OutletEditionAction action, NewsItemPlacement placement) {
        List<FileInfo> files = new ArrayList<FileInfo>();
        this.newsItem = placement.getNewsItem();
        this.actionProperties = action.getPropertiesAsMap();

        if (isPropertySet(DrupalEditionAction.Property.IMAGE_RENDITION)) {
            this.renditionName = getProperty(DrupalEditionAction.Property.IMAGE_RENDITION);
        } else {
            return files;
        }

        for (NewsItemMediaAttachment attachment : newsItem.getMediaAttachments()) {
            MediaItem mediaItem = attachment.getMediaItem();

            // Verify that the item exist and renditions are attached
            if (mediaItem != null && mediaItem.isRenditionsAttached()) {
                try {
                    MediaItemRendition rendition = mediaItem.findRendition(this.renditionName);
                    String abbreviatedCaption = StringUtils.abbreviate(attachment.getCaption(), MEDIA_ITEM_TITLE_LENGTH);
                    files.add(new FileInfo(new File(rendition.getFileLocation()), abbreviatedCaption));
                } catch (RenditionNotFoundException ex) {
                    LOG.log(Level.INFO, "Rendition ''{0}'' missing for MediaItem #{1}. Ignoring MediaItem #{1}.", new Object[]{renditionName, mediaItem.getId()});
                    LOG.log(Level.FINEST, null, ex);
                }
            }
        }

        return files;
    }

    private boolean isPropertySet(DrupalEditionAction.Property property) {
        return this.actionProperties.containsKey(property.toString());
    }

    private String getProperty(DrupalEditionAction.Property property) {
        return this.actionProperties.get(property.toString());
    }

}
