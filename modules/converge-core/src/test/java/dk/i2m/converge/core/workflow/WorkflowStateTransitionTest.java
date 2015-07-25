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
package dk.i2m.converge.core.workflow;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.security.UserAccount;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for {@link WorkflowStateTransition}.
 *
 * @author Allan Lykke Christensen
 */
public class WorkflowStateTransitionTest {

    @Test
    public void workflowStateTransition_createTransitionFromStep_returnTransitionForStep() {
        // Arrange
        NewsItem newsItem = getNewsItem();
        WorkflowStep workflowStep = getWorkflowStep();
        UserAccount actor = getUserAccount();
        String comment = "Transition comment";

        // Act
        WorkflowStateTransition workflowStateTransition = new WorkflowStateTransition(newsItem, workflowStep, actor, comment);

        // Assert
        assertEquals(comment, workflowStateTransition.getComment());
        assertEquals(newsItem.getBrief(), workflowStateTransition.getBriefVersion());
        assertEquals(newsItem.getTitle(), workflowStateTransition.getHeadlineVersion());
        assertEquals(newsItem.getStory(), workflowStateTransition.getStoryVersion());
        assertEquals(newsItem, workflowStateTransition.getNewsItem());
        assertNotNull(workflowStateTransition.getTimestamp());
        assertEquals(actor, workflowStateTransition.getUser());
        assertEquals(workflowStep.getToState(), workflowStateTransition.getState());
        assertTrue(workflowStateTransition.isSubmitted());
    }
    
    @Test
    public void workflowStateTransition_createTransitionFromState_returnTransitionForState() {
        // Arrange
        NewsItem newsItem = getNewsItem();
        WorkflowState toState = getToState();
        UserAccount actor = getUserAccount();
        String comment = "Transition comment";

        // Act
        WorkflowStateTransition workflowStateTransition = new WorkflowStateTransition(newsItem, actor, comment, toState);

        // Assert
        assertEquals(comment, workflowStateTransition.getComment());
        assertEquals(newsItem.getBrief(), workflowStateTransition.getBriefVersion());
        assertEquals(newsItem.getTitle(), workflowStateTransition.getHeadlineVersion());
        assertEquals(newsItem.getStory(), workflowStateTransition.getStoryVersion());
        assertEquals(newsItem, workflowStateTransition.getNewsItem());
        assertNotNull(workflowStateTransition.getTimestamp());
        assertEquals(actor, workflowStateTransition.getUser());
        assertEquals(toState, workflowStateTransition.getState());
        assertFalse(workflowStateTransition.isSubmitted());
    }

    private WorkflowStep getWorkflowStep() {
        WorkflowStep step = new WorkflowStep();
        step.setId(100L);
        step.setName("Take this step");
        step.setDescription("Description of the step");
        step.setTreatAsSubmitted(true);
        step.setToState(getToState());
        step.setFromState(getFromState());
        return step;
    }

    private NewsItem getNewsItem() {
        NewsItem newsItem = new NewsItem();
        newsItem.setId(123L);
        newsItem.setTitle("News item title");
        newsItem.setBrief("News item brief");
        newsItem.setStory("News item story");
        return newsItem;
    }

    private UserAccount getUserAccount() {
        UserAccount userAccount = new UserAccount();
        userAccount.setId(1L);
        userAccount.setUsername("someuser");
        userAccount.setFullName("Name of the user");
        return userAccount;
    }

    private WorkflowState getToState() {
        WorkflowState state = new WorkflowState();
        state.setId(101L);
        state.setName("Next state");
        state.setDescription("The next state");
        return state;
    }

    private WorkflowState getFromState() {
        WorkflowState state = new WorkflowState();
        state.setId(102L);
        state.setName("Previous state");
        state.setDescription("The previous state");
        return state;
    }
}
