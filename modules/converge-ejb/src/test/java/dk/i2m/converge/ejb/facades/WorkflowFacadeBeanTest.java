/*
 *  Copyright (C) 2010 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.workflow.WorkflowStep;
import dk.i2m.converge.core.workflow.WorkflowState;
import dk.i2m.converge.core.workflow.Workflow;
import dk.i2m.converge.EjbTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Allan Lykke Christensen
 */
public class WorkflowFacadeBeanTest extends EjbTestCase {

    public static final String BEAN_INTERFACE = "WorkflowFacadeBeanLocal";

    @Test
    @Ignore
    public void testObtainBean() throws Exception {
        Object object = getInitialContext().lookup(BEAN_INTERFACE);
        assertNotNull(object);
        assertTrue(object instanceof WorkflowFacadeLocal);
    }

    @Test
    @Ignore
    public void testCascadeWorkflowStep() throws Exception {
        Object object = getInitialContext().lookup(BEAN_INTERFACE);
        WorkflowFacadeLocal workflowFacade = (WorkflowFacadeLocal) object;

        Workflow w = new Workflow();
        w.setName("Test Workflow");
        w.setDescription("Description of workflow");


        w = workflowFacade.createWorkflow(w);
        WorkflowState start = new WorkflowState("Assigned");
        w.addState(start);
        w.setStartState(start);
        w = workflowFacade.updateWorkflow(w);

        start = w.getStartState();
        WorkflowStep step = new WorkflowStep();
        step.setName("Send for review");
        step.setFromState(start);
        step.setToState(start);
        step.setOrder(1);
        start.getNextStates().add(step);

        w = workflowFacade.updateWorkflow(w);

    }
}
