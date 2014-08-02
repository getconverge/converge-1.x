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
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.Workflow;
import dk.i2m.converge.core.workflow.WorkflowState;
import java.util.Calendar;
import java.util.List;
import org.apache.http.NameValuePair;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit test for {@link NewsItemPlacementToNameValuePairsConverter}.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemPlacementToNameValuePairsConverterTest {

    @Test
    public void newsItemPlacementToNameValuePairsConverter_newsItemWithoutPlacement_returnNameValuePairsWithoutPlacement() {
        // Arrange
        NewsItemPlacement placement = getNewsItemPlacementWithoutPlacement();

        // Act
        NewsItemPlacementToNameValuePairsConverter converter = new NewsItemPlacementToNameValuePairsConverter();
        List<NameValuePair> nameValuePairs = converter.convert(placement);

        // Assert
        boolean assertedLanguage = false;
        boolean assertedTitle = false;
        for (NameValuePair nameValue : nameValuePairs) {
            if (nameValue.getName().equals("field_placement_start[und][0]")) {
                fail(nameValue.getName() + " was found in the list with value: " + nameValue.getValue());
            } else if (nameValue.getName().equals("field_placement_position[und][0]")) {
                fail(nameValue.getName() + " was found in the list with value: " + nameValue.getValue());
            } else if (nameValue.getName().equals("language")) {
                assertEquals("und", nameValue.getValue());
                assertedLanguage = true;
            } else if (nameValue.getName().equals("title")) {
                assertEquals("This is the title of the story", nameValue.getValue());
                assertedTitle = true;
            }
        }
        assertTrue(assertedLanguage && assertedTitle);
    }

    @Test
    public void newsItemPlacementToNameValuePairsConverter_newsItemPlacement_returnNameValuePairsWithPlacement() {
        // Arrange
        NewsItemPlacement placement = getNewsItemPlacementWithPlacement();

        // Act
        NewsItemPlacementToNameValuePairsConverter converter = new NewsItemPlacementToNameValuePairsConverter();
        List<NameValuePair> nameValuePairs = converter.convert(placement);

        // Assert
        boolean assertedStart = false;
        boolean assertedPosition = false;
        for (NameValuePair nameValue : nameValuePairs) {
            if (nameValue.getName().equals("field_placement_start[und][0]")) {
                assertEquals("5", nameValue.getValue());
                assertedStart = true;
            } else if (nameValue.getName().equals("field_placement_position[und][0]")) {
                assertEquals("32", nameValue.getValue());
                assertedPosition = true;
            }
        }
        assertTrue(assertedStart && assertedPosition);
    }

    private NewsItem getNewsItemWithByline() {
        WorkflowState startState = new WorkflowState();
        startState.setActorRole(new UserRole("Author"));
        Workflow workflow = new Workflow();
        workflow.setName("Test Workflow");
        workflow.setStartState(startState);
        Outlet outlet = new Outlet();
        outlet.setTitle("Test Outlet");
        outlet.setWorkflow(workflow);
        NewsItem newsItem = new NewsItem();
        newsItem.setOutlet(outlet);
        UserAccount actor1 = new UserAccount();
        actor1.setFullName("Allan Lykke Christensen");
        UserAccount actor2 = new UserAccount();
        actor2.setFullName("Nikholai Mukalazi");
        UserAccount actor3 = new UserAccount();
        actor3.setFullName("Mackenzie Ndiga");
        newsItem.getActors().add(new NewsItemActor(actor1, startState.getActorRole(), newsItem));
        newsItem.getActors().add(new NewsItemActor(actor2, startState.getActorRole(), newsItem));
        newsItem.getActors().add(new NewsItemActor(actor3, startState.getActorRole(), newsItem));
        newsItem.setByLine("By Reporters");
        newsItem.setTitle("This is the title of the story");
        return newsItem;
    }

    private NewsItemPlacement getNewsItemPlacementWithoutPlacement() {
        NewsItemPlacement placement = new NewsItemPlacement();
        placement.setNewsItem(getNewsItemWithByline());
        placement.setOutlet(placement.getNewsItem().getOutlet());
        Edition edition = new Edition();
        edition.setOutlet(placement.getOutlet());
        edition.setPublicationDate(Calendar.getInstance());
        placement.setEdition(edition);
        placement.setPosition(null);
        placement.setStart(null);
        return placement;
    }

    private NewsItemPlacement getNewsItemPlacementWithPlacement() {
        NewsItemPlacement placement = new NewsItemPlacement();
        placement.setNewsItem(getNewsItemWithByline());
        placement.setOutlet(placement.getNewsItem().getOutlet());
        Edition edition = new Edition();
        edition.setOutlet(placement.getOutlet());
        edition.setPublicationDate(Calendar.getInstance());
        placement.setEdition(edition);
        placement.setPosition(32);
        placement.setStart(5);
        return placement;
    }
}
