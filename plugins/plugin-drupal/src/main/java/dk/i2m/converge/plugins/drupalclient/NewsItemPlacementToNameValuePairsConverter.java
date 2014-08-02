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
import dk.i2m.converge.core.content.NewsItemActor;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.Workflow;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Converts a {@link NewsItemPlacement} to a {@link List} of {@link ValuePair}
 * objects.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemPlacementToNameValuePairsConverter {

    private final DateFormat DRUPAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public List<NameValuePair> convert(NewsItemPlacement placement) {
        Edition edition = placement.getEdition();
        NewsItem newsItem = placement.getNewsItem();

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("date", DRUPAL_DATE_FORMAT.format(edition.getPublicationDate().getTime())));
        params.add(new BasicNameValuePair("title", StringUtils.left(StringEscapeUtils.escapeHtml(newsItem.getTitle()), 255)));
        params.add(new BasicNameValuePair("language", "und"));
        params.add(new BasicNameValuePair("body[und][0][value]", newsItem.getStory()));
        params.add(new BasicNameValuePair("body[und][0][format]", "full_html"));
        params.add(new BasicNameValuePair("field_author[und][0][value]", getAuthor(newsItem)));
        params.add(new BasicNameValuePair("field_newsitem[und][0][value]", "" + newsItem.getId()));
        params.add(new BasicNameValuePair("field_edition[und][0][value]", "" + edition.getId()));

        if (placement.getStart() != null) {
            params.add(new BasicNameValuePair("field_placement_start[und][0]", "" + placement.getStart()));
        }

        if (placement.getPosition() != null) {
            params.add(new BasicNameValuePair("field_placement_position[und][0]", "" + placement.getPosition()));
        }

        return params;
    }

    /**
     * Get Author text field. If the by-line of the {@link NewsItem} is empty,
     * it will generate an author string based on the initial actors.
     *
     * @param newsItem {@link NewsItem}
     * @return By-line to use for the story when published on Drupal
     */
    public String getAuthor(NewsItem newsItem) {
        if (StringUtils.isBlank(newsItem.getByLine())) {
            // No by-line specified in the news item. 
            // Generate by-line from actors on news item

            StringBuilder sb = new StringBuilder();

            Workflow workflow = newsItem.getOutlet().getWorkflow();
            UserRole authorRole = workflow.getStartState().getActorRole();

            // Iterate through actors specified on the news item
            boolean firstActor = true;
            for (NewsItemActor actor : newsItem.getActors()) {

                // If the actor has the role from the initial state of the 
                // workflow, he is the author of the story
                if (actor.getRole().equals(authorRole)) {
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
