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
package dk.i2m.converge.plugins.drupalclient;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemEditionState;

/**
 * Helper class for {@link DrupalEditionAction}.
 *
 * @author Allan Lykke Christensen
 */
public class DrupalEditionActionHelper {

    /**
     * Determine if the Drupal Node is outdated. The method check to see if the
     * {@link NewsItem} has been updated since it was last uploaded to Drupal.
     *
     * @param newsItem {@link NewsItem} to check
     * @param version Last known version stamp stored in a
     * {@link NewsItemEditionState} property
     * @return {@link true} if the Drupal node is outdated and the
     * {@link NewsItem} should be uploaded to Drupal, otherwise {@link false} if
     * the item in Converge is the same as on Drupal
     */
    public static boolean isDrupalNodeOutdated(NewsItem newsItem, NewsItemEditionState version) {
        long storyLastUpdated = newsItem.getUpdated().getTimeInMillis();
        long storyUploadedLastUpdated;
        try {
            storyUploadedLastUpdated = Long.valueOf(version.getValue());
        } catch (NumberFormatException ex) {
            storyUploadedLastUpdated = 0;
        }
        return (storyLastUpdated != storyUploadedLastUpdated);
    }
}
