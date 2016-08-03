/*
 * Copyright (C) 2016 Allan Lykke Christensen
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
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.converge.core.workflow.Workflow;
import dk.i2m.converge.core.workflow.WorkflowState;
import org.junit.Test;
import static org.junit.Assert.*;

public class WorkflowsTest {

    @Test
    public void workflows_workflowNotSelected_isNotInEditMode() {
        // Arrange
        Workflows workflows = new Workflows();
        workflows.setSelected(null);

        // Act
        boolean actual = workflows.isEditMode();

        // Assert
        assertFalse("Workflow should not be edit mode when not selected", actual);
    }

    @Test
    public void workflows_workflowSelected_isInEditMode() {
        // Arrange
        Workflow workflow = new Workflow();
        workflow.setId(1L);
        Workflows workflows = new Workflows();
        workflows.setSelected(workflow);

        // Act
        boolean actual = workflows.isEditMode();

        // Assert
        assertTrue("Workflow should be in edit mode when selected", actual);
    }

    @Test
    public void workflows_workflowSelected_isNotInAddMode() {
        // Arrange
        Workflow workflow = new Workflow();
        workflow.setId(1L);
        Workflows workflows = new Workflows();
        workflows.setSelected(workflow);

        // Act
        boolean actual = workflows.isAddMode();

        // Assert
        assertFalse("Workflow should not be in add mode when selected", actual);
    }

    @Test
    public void workflows_workflowNotSelected_isAddMode() {
        // Arrange
        Workflows workflows = new Workflows();
        workflows.setSelected(null);

        // Act
        boolean actual = workflows.isAddMode();

        // Assert
        assertTrue("Workflow should be in add mode when not selected", actual);
    }

    @Test
    public void workflows_workflowStateNotSelected_isNotInWorkflowStateEditMode() {
        // Arrange
        Workflows workflows = new Workflows();

        // Act
        boolean actual = workflows.isWorkflowStateEditMode();

        // Assert
        assertFalse("Workflow should not be in edit mode when workflowstate is not selected", actual);
    }

    @Test
    public void workflows_workflowStateSelected_isInWorkflowStateEditMode() {
        // Arrange
        WorkflowState workflowState = new WorkflowState();
        workflowState.setId(1L);
        Workflows workflows = new Workflows();
        workflows.setSelectedWorkflowState(workflowState);

        // Act
        boolean actual = workflows.isWorkflowStateEditMode();

        // Assert
        assertTrue("Workflow should be in edit mode when a WorkflowState is selected", actual);
    }

    @Test
    public void workflows_workflowStateNotSelected_isInWorkflowStateAddMode() {
        // Arrange
        Workflows workflows = new Workflows();

        // Act
        boolean actual = workflows.isWorkflowStateAddMode();

        // Assert
        assertTrue("Workflow should be in add mode when workflowstate is selected", actual);
    }

    @Test
    public void workflows_workflowStateSelected_isNotInWorkflowStateAddMode() {
        // Arrange
        WorkflowState workflowState = new WorkflowState();
        workflowState.setId(1L);
        Workflows workflows = new Workflows();
        workflows.setSelectedWorkflowState(workflowState);

        // Act
        boolean actual = workflows.isWorkflowStateAddMode();

        // Assert
        assertFalse("Workflow should not be in add mode when a WorkflowState is selected", actual);
    }

}
