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
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.Section;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Helper {

    public static final String SERVICE_ENDPOINT = "http://0.0.0.0:8888/thestar/api/converge";
    public static final String NODE_TYPE = "article";
    public static final String NODE_ALIAS = "node";
    public static final String USER_ALIAS = "user";
    public static final String USERNAME = "converge";
    public static final String PASSWORD = "converge";

    public static NewsItemPlacement getPlacement(Long id) {
        NewsItemPlacement placement = new NewsItemPlacement();
        placement.setId(id);
        placement.setStart(2);
        placement.setPosition(4);

        return placement;
    }

    public static Outlet getOutlet(Long id) {
        Outlet outlet = new Outlet();
        outlet.setId(id);
        outlet.setTitle("Test Outlet");

        return outlet;
    }

    public static Edition getEdition(Long id) {
        Calendar publicationDate = new GregorianCalendar(2010, Calendar.JULY, 3);
        Calendar expirationDate = (Calendar) publicationDate.clone();
        expirationDate.add(Calendar.DAY_OF_MONTH, 1);

        Edition edition = new Edition();
        edition.setId(id);
        edition.setPublicationDate(publicationDate);
        edition.setExpirationDate(expirationDate);
        edition.setCloseDate(publicationDate.getTime());
        edition.getPlacements().add(new NewsItemPlacement());

        return edition;
    }

    public static Section getSection(Long id) {
        Section section = new Section();
        section.setId(id);
        section.setName("Test Section");

        return section;
    }

    public static NewsItem getNewsItem(Long id) {
        NewsItem newsItem = new NewsItem();
        newsItem.setId(id);
        newsItem.setTitle("Test NewsItem Title");
        newsItem.setStory("<p>This is a test sports story</p>");
        newsItem.setByLine("By Test");
        newsItem.setUpdated(Calendar.getInstance());

        return newsItem;
    }

    public static String getFieldMapping() {
        return String.format("%s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s:%s, %s:%s",
                DrupalUtils.KEY_BODY, "body",
                DrupalUtils.KEY_DATE, "field_converge_date",
                DrupalUtils.KEY_BYLINE, "field_converge_byline",
                DrupalUtils.KEY_SECTION, "field_section",
                DrupalUtils.KEY_EDITION_ID, "field_converge_edition_id",
                DrupalUtils.KEY_NEWSITEM_ID, "field_converge_newsitem_id",
                DrupalUtils.KEY_START, "field_converge_start",
                DrupalUtils.KEY_POSITION, "field_converge_position",
                DrupalUtils.KEY_IMAGE, "field_converge_image");
    }

    public static String getSectionMapping(Long section_id, Integer taxonomy_id) {
        return String.format("%d:%d", section_id, taxonomy_id);
    }
}
