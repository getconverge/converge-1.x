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
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import dk.i2m.converge.core.workflow.OutletEditionActionProperty;
import dk.i2m.converge.core.workflow.Section;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Responsible for turning a {@link NewsItemPlacement} into a {@link List} of
 * {@link ValuePair} objects for sending to Drupal.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemPlacementToNameValuePairsConverter {

    private final DateFormat DRUPAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String DEFAULT_NODE_LANGUAGE = "und";
    private static final int DEFAULT_PUBLISH_DELAY = 0;
    private boolean publishImmediately;
    private Integer publishDelay;
    private Edition edition;
    private NewsItem newsItem;
    private Section section;
    private String nodeType = "article";
    private String nodeLanguage;
    private String drupalSectionId = "";
    private Map<String, String> actionProperties;

    /**
     * Takes data from the {@link NewsItemPlacement} and creates a {@link List}
     * of {@link NameValuePair}s with relevant data that should be sent to
     * Drupal.
     *
     * @param action {@link OutletEditionAction} containing the plug-in
     * configuration
     * @param placement {@link NewsItemPlacement} from where data should be
     * extracted
     * @return {@link List} of {@link NameValuePair} with relevant data for
     * Drupal
     * @throws UnmappedSectionException If the {@link NewsItemPlacement} cannot
     * be mapped to a Drupal section
     */
    public List<NameValuePair> convert(OutletEditionAction action, NewsItemPlacement placement) throws UnmappedSectionException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        this.edition = placement.getEdition();
        this.newsItem = placement.getNewsItem();
        this.section = placement.getSection();
        this.actionProperties = action.getPropertiesAsMap();

        if (section == null) {
            throw new UnmappedSectionException("Section missing from NewsItemPlacement");
        }

        Long placementSectionId = section.getId();
        for (OutletEditionActionProperty property : action.getProperties()) {
            if (property.getKey().equalsIgnoreCase(DrupalEditionAction.Property.SECTION_MAPPING.name())) {
                String[] mapping = property.getValue().split(":");
                if (mapping.length != 2) {
                    continue;
                }
                try {
                    Long convergeId = Long.valueOf(mapping[0].trim());
                    if (convergeId == placementSectionId.longValue()) {
                        this.drupalSectionId = mapping[1].trim();
                    }
                } catch (NumberFormatException ex) {
                    // Incorrect format used in the section mapping, ignore.
                }
            }
        }

        if (this.drupalSectionId.isEmpty()) {
            throw new UnmappedSectionException("Converge Section #" + placementSectionId + " (for NewsItem #" + this.newsItem.getId() + ") is not mapped to a Drupal section");
        }

        if (isPropertySet(DrupalEditionAction.Property.NODE_TYPE)) {
            this.nodeType = getProperty(DrupalEditionAction.Property.NODE_TYPE);
        }
        if (isPropertySet(DrupalEditionAction.Property.NODE_LANGUAGE)) {
            this.nodeLanguage = getProperty(DrupalEditionAction.Property.NODE_LANGUAGE);
        } else {
            this.nodeLanguage = DEFAULT_NODE_LANGUAGE;
        }

        if (isPropertySet(DrupalEditionAction.Property.PUBLISH_IMMEDIATELY)) {
            this.publishImmediately = BooleanUtils.toBoolean(getProperty(DrupalEditionAction.Property.PUBLISH_IMMEDIATELY));
        }

        this.publishDelay = NumberUtils.toInt(getProperty(DrupalEditionAction.Property.PUBLISH_DELAY), DEFAULT_PUBLISH_DELAY);

        params.add(new BasicNameValuePair("publish_on", getPublishOn()));
        params.add(new BasicNameValuePair("title", getTitle()));
        params.add(new BasicNameValuePair("date", DRUPAL_DATE_FORMAT.format(edition.getPublicationDate().getTime())));
        params.add(new BasicNameValuePair("type", nodeType));
        params.add(new BasicNameValuePair("language", this.nodeLanguage));
        params.add(new BasicNameValuePair("body[" + this.nodeLanguage + "][0][value]", newsItem.getStory()));
        params.add(new BasicNameValuePair("body[" + this.nodeLanguage + "][0][format]", "full_html"));
        params.add(new BasicNameValuePair("field_author[" + this.nodeLanguage + "][0][value]", newsItem.getAuthors()));
        params.add(new BasicNameValuePair("field_newsitem[" + this.nodeLanguage + "][0][value]", "" + newsItem.getId()));
        params.add(new BasicNameValuePair("field_edition[" + this.nodeLanguage + "][0][value]", "" + edition.getId()));
        params.add(new BasicNameValuePair("field_section[" + this.nodeLanguage + "][0]", this.drupalSectionId));

        if (placement.getStart() != null) {
            params.add(new BasicNameValuePair("field_placement_start[" + this.nodeLanguage + "][0][value]", String.valueOf(placement.getStart())));
        }

        if (placement.getPosition() != null) {
            params.add(new BasicNameValuePair("field_placement_position[" + this.nodeLanguage + "][0][value]", String.valueOf(placement.getPosition())));
        }

        return params;
    }

    private String getTitle() {
        return StringUtils.left(StringEscapeUtils.escapeHtml(this.newsItem.getTitle()), 255);
    }

    private String getPublishOn() {
        if (publishImmediately) {
            return DRUPAL_DATE_FORMAT.format(Calendar.getInstance().getTime());
        } else {
            Calendar calendar = (Calendar) edition.getPublicationDate().clone();
            calendar.add(Calendar.HOUR_OF_DAY, publishDelay);
            return DRUPAL_DATE_FORMAT.format(calendar.getTime());
        }
    }

    private boolean isPropertySet(DrupalEditionAction.Property property) {
        return this.actionProperties.containsKey(property.toString());
    }

    private String getProperty(DrupalEditionAction.Property property) {
        return this.actionProperties.get(property.toString());
    }

}
