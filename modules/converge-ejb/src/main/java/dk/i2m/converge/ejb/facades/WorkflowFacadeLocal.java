/*
 * Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.workflow.WorkflowStepValidator;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.workflow.Workflow;
import dk.i2m.converge.core.workflow.WorkflowState;
import dk.i2m.converge.core.workflow.WorkflowStep;
import dk.i2m.converge.core.workflow.WorkflowStepAction;
import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for the {@link WorkflowFacadeBean}.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface WorkflowFacadeLocal {

    /**
     * Creates a new {@link Workflow}.
     *
     * @param workflow
     *          Workflow to create
     * @return Created {@link Workflow}
     */
    Workflow createWorkflow(Workflow workflow);

    /**
     * Deletes an existing {@link Workflow} from the database.
     *
     * @param id
     *          Unique identifier of the {@link Workflow}
     */
    void deleteWorkflowById(Long id);
    
    /**
     * Deletes an existing {@link WorkflowStepAction} from the database.
     * 
     * @param id
     *          Unique identifier of the {@link WorkflowStepAction}
     */
    void deleteWorkflowStepActionById(Long id);

    /**
     * Finds the available workflows.
     *
     * @return {@link List} of available {@link Workflow}s
     */
    List<Workflow> findAllWorkflows();

    /**
     * Updates an existing {@link Workflow}.
     *
     * @param workflow
     *          {@link Workflow} to update
     * @return Updated {@link Workflow}
     */
    Workflow updateWorkflow(Workflow workflow);

    /**
     * Finds a {@link Workflow} by its unique identifier.
     *
     * @param id
     *          Unique identifier of the {@link Workflow}
     * @return {@link Workflow} matching the <code>id</code>
     * @throws DataNotFoundException
     *          If no {@link Workflow} matched the <code>id</code>
     */
    Workflow findWorkflowById(Long id) throws DataNotFoundException;

    /**
     * Creates a new {@link WorkflowState} in the database.
     *
     * @param workflowState
     *          {@link WorkflowState} to create
     * @return Updated {@link WorkflowState} with auto-generated values inserted
     */
    WorkflowState createWorkflowState(WorkflowState workflowState);

    /**
     * Deletes an existing {@link WorkflowState} from the database.
     *
     * @param id
     *          Unique identifier of the {@link WorkflowState} to delete
     */
    void deleteWorkflowStateById(Long id);

    /**
     * Finds an existing {@link WorkflowState} by its unique identifier.
     *
     * @param id
     * Unique identifier of the {@link WorkflowState}
     * @return {@link WorkflowState} matching the <code>id</code>
     * @throws DataNotFoundException
     * If no {@link WorkflowState} could be found with the given id
     */
    WorkflowState findWorkflowStateById(Long id) throws DataNotFoundException;

    /**
     * Updates an existing {@link WorkflowState} in the database.
     *
     * @param state
     * {@link WorkflowState} to update
     */
    WorkflowState updateWorkflowState(WorkflowState state);

    /**
     * Finds an existing {@link WorkflowState} by its unique identifier.
     *
     * @param workflowId
     *          Unique identifier of the {@link WorkflowState}
     * @return {@link WorkflowState} matching the <code>id</code>
     */
    List<WorkflowState> findWorkflowStateByWorkflowId(Long workflowId);

    /**
     * Creates a new {@link WorkflowStep}.
     *
     * @param step
     *          {@link WorkflowStep} to create
     * @return Created {@link WorkflowStep} with generated values inserted
     */
    WorkflowStep createWorkflowStep(WorkflowStep step);

    /**
     * Deletes an existing {@link WorkflowStep}.
     *
     * @param id
     *          Unique identifier of the {@link WorkflowStep}
     */
    void deleteWorkflowStepById(Long id);

    /**
     * Finds all {@link WorkflowStep}s for a given {@link WorkflowState}.
     *
     * @param workflowStateId
     *          Unique identifier of the {@link WorkflowState}
     * @return {@link List} of {@link WorkflowStep}s for the given
     *         {@link WorkflowState}
     */
    List<WorkflowStep> findAllWorkflowSteps(Long workflowStateId);

    /**
     * Finds a {@link WorkflowStep} by its unique identifier.
     *
     * @param id
     *          Unique identifier of the {@link WorkflowStep}
     * @return {@link WorkflowStep} matching the <code>id</code>
     * @throws DataNotFoundException
     *          If no {@link WorkflowStep} matched the <code>id</code>
     */
    WorkflowStep findWorkflowStepById(Long id) throws DataNotFoundException;

    /**
     * Updates an existing {@link WorkflowStep}.
     *
     * @param step
     *          {@link WorkflowStep} to update
     */
    WorkflowStep updateWorkflowStep(WorkflowStep step);

   /**
     * Creates a new {@link WorkflowStepAction}.
     *
     * @param stepAction
     *          {@link WorkflowStepAction} to create
     * @return Created {@link WorkflowStepAction} with generated values inserted
     */
    WorkflowStepAction createWorkflowStepAction(WorkflowStepAction stepAction);

    /**
     * Updates an existing {@link WorkflowStepAction}.
     *
     * @param stepAction
     *          {@link WorkflowStepAction} to create
     * @return Created {@link WorkflowStepAction} with generated values inserted
     */
    WorkflowStepAction updateWorkflowStepAction(WorkflowStepAction stepAction);

    
    WorkflowStepValidator createWorkflowStepValidator(WorkflowStepValidator validator);

    WorkflowStepValidator updateWorkflowStepValidator(WorkflowStepValidator validator);

    void deleteWorkflowStepValidatorById(Long id);
    
    
}
