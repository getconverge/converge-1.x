/*
 * Copyright (C) 2014 - 2015 Allan Lykke Christensen
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
package dk.i2m.converge.core.content;

import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.Workflow;
import dk.i2m.converge.core.workflow.WorkflowState;
import dk.i2m.converge.core.workflow.WorkflowStatePermission;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link NewsItem}.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemTest {

    @Test
    public void newsItem_withoutByLine_returnAuthorsFromInitialActors() {
        // Arrange
        NewsItem newsItem = getNewsItemWithoutByline();
        final String expectedAuthors = "Allan Lykke Christensen, Nikholai Mukalazi";

        // Act
        String actualAuthors = newsItem.getAuthors();

        // Assert
        assertEquals(expectedAuthors, actualAuthors);
    }

    @Test
    public void newsItem_withByLine_returnByLine() {
        // Arrange
        NewsItem newsItem = getNewsItemWithByline();
        final String expectedAuthors = "By Reporters";

        // Act
        final String actualAuthors = newsItem.getAuthors();

        // Assert
        assertEquals(expectedAuthors, actualAuthors);
    }

    @Test
    public void newsItem_withoutCurrentState_returnEmptyCurrentActor() {
        // Arrange
        NewsItem newsItem = getNewsItemWithoutCurrentState();

        // Act
        final String actualCurrentActor = newsItem.getCurrentActor();

        // Assert
        assertEquals("", actualCurrentActor);
    }

    @Test
    public void newsItem_withUserCurrentState_returnCurrentUserName() {
        // Arrange
        NewsItem newsItem = getNewsItemWithUserCurrentState();

        // Act
        final String actualCurrentActor = newsItem.getCurrentActor();

        // Assert
        assertEquals("Allan Lykke Christensen", actualCurrentActor);
    }

    @Test
    public void newsItem_withRoleCurrentState_returnCurrentRoleName() {
        // Arrange
        NewsItem newsItem = getNewsItemWithRoleCurrentState();

        // Act
        final String actualCurrentActor = newsItem.getCurrentActor();

        // Assert
        assertEquals("Editor", actualCurrentActor);
    }

    // <editor-fold defaultstate="collapsed" desc="Methods for obtaining test data">
    private NewsItem getNewsItemWithoutByline() {
        WorkflowState startState = new WorkflowState();
        startState.setActorRole(new UserRole(1L, "Author"));
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
        newsItem.getActors().add(new NewsItemActor(actor3, new UserRole(2L, "Editor"), newsItem));
        return newsItem;
    }

    private NewsItem getNewsItemWithByline() {
        WorkflowState startState = new WorkflowState();
        startState.setActorRole(new UserRole(1L, "Author"));
        Workflow workflow = new Workflow();
        workflow.setName("Test Workflow");
        workflow.setStartState(startState);
        Outlet outlet = new Outlet();
        outlet.setTitle("Test Outlet");
        outlet.setWorkflow(workflow);
        NewsItem newsItem = new NewsItem();
        newsItem.setByLine("By Reporters");
        newsItem.setOutlet(outlet);
        UserAccount actor1 = new UserAccount();
        actor1.setFullName("Allan Lykke Christensen");
        UserAccount actor2 = new UserAccount();
        actor2.setFullName("Nikholai Mukalazi");
        UserAccount actor3 = new UserAccount();
        actor3.setFullName("Mackenzie Ndiga");
        newsItem.getActors().add(new NewsItemActor(actor1, startState.getActorRole(), newsItem));
        newsItem.getActors().add(new NewsItemActor(actor2, startState.getActorRole(), newsItem));
        newsItem.getActors().add(new NewsItemActor(actor3, new UserRole(2L, "Editor"), newsItem));
        return newsItem;
    }

    private NewsItem getNewsItemWithoutCurrentState() {
        NewsItem newsItem = new NewsItem();
        return newsItem;
    }

    private NewsItem getNewsItemWithUserCurrentState() {
        WorkflowState startState = new WorkflowState();
        startState.setPermission(WorkflowStatePermission.USER);
        startState.setActorRole(new UserRole(1L, "Author"));
        Workflow workflow = new Workflow();
        workflow.setName("Test Workflow");
        workflow.setStartState(startState);
        Outlet outlet = new Outlet();
        outlet.setTitle("Test Outlet");
        outlet.setWorkflow(workflow);
        NewsItem newsItem = new NewsItem();
        newsItem.setOutlet(outlet);
        newsItem.setCurrentState(startState);
        UserAccount actor1 = new UserAccount();
        actor1.setFullName("Allan Lykke Christensen");
        newsItem.getActors().add(new NewsItemActor(actor1, startState.getActorRole(), newsItem));
        return newsItem;
    }

    private NewsItem getNewsItemWithRoleCurrentState() {
        WorkflowState startState = new WorkflowState();
        startState.setPermission(WorkflowStatePermission.USER);
        startState.setActorRole(new UserRole(1L, "Author"));
        WorkflowState reviewState = new WorkflowState();
        reviewState.setPermission(WorkflowStatePermission.GROUP);
        reviewState.setActorRole(new UserRole(2L, "Editor"));
        Workflow workflow = new Workflow();
        workflow.setName("Test Workflow");
        workflow.setStartState(startState);
        Outlet outlet = new Outlet();
        outlet.setTitle("Test Outlet");
        outlet.setWorkflow(workflow);
        NewsItem newsItem = new NewsItem();
        newsItem.setOutlet(outlet);
        newsItem.setCurrentState(reviewState);
        UserAccount actor1 = new UserAccount();
        actor1.setFullName("Allan Lykke Christensen");
        newsItem.getActors().add(new NewsItemActor(actor1, startState.getActorRole(), newsItem));
        return newsItem;
    }
    // </editor-fold>
}
