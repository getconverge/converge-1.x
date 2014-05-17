/*
 * Copyright (C) 2010 Interactive Media Management
 *
 * This program is free software: yoIu can redistribute it and/or modify
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

import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.ejb.services.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Stateless session bean for managing workflow configurations. This bean is
 * not used for processing items in the workflow.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class WorkflowFacadeBean implements WorkflowFacadeLocal {

    private static final Logger LOG = Logger.getLogger(WorkflowFacadeBean.class.getName());

    @EJB private DaoServiceLocal daoService;

    /**
     * Creates a new instance of {@link WorkflowFacadeBean}.
     */
    public WorkflowFacadeBean() {
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowState createWorkflowState(WorkflowState workflowState) {
        return daoService.create(workflowState);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowState findWorkflowStateById(Long id) throws DataNotFoundException {
        return daoService.findById(WorkflowState.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowState> findWorkflowStateByWorkflowId(Long workflowId) {
        List<WorkflowState> states;
        try {
            Workflow wf = findWorkflowById(workflowId);
            Map params = QueryBuilder.with("workflow", wf).parameters();
            states = daoService.findWithNamedQuery(WorkflowState.FIND_BY_WORKFLOW, params);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Couldn't find workflow", ex);
            states = new ArrayList<WorkflowState>();
        }

        return states;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowState updateWorkflowState(WorkflowState state) {
        return daoService.update(state);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteWorkflowStateById(Long id) {
        daoService.delete(WorkflowState.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public Workflow createWorkflow(Workflow workflow) {
        return daoService.create(workflow);
    }

    /** {@inheritDoc} */
    @Override
    public List<Workflow> findAllWorkflows() {
        return daoService.findAll(Workflow.class);
    }

    /** {@inheritDoc} */
    @Override
    public Workflow findWorkflowById(Long id) throws DataNotFoundException {
        return daoService.findById(Workflow.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public Workflow updateWorkflow(Workflow workflow) {
        return daoService.update(workflow);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteWorkflowById(Long id) {
        daoService.delete(Workflow.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowStep createWorkflowStep(WorkflowStep step) {
        return daoService.create(step);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteWorkflowStepById(Long id) {
        daoService.delete(WorkflowStep.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowStep> findAllWorkflowSteps(Long workflowStateId) {
        List<WorkflowStep> steps;
        try {
            WorkflowState ws = daoService.findById(WorkflowState.class, workflowStateId);
            Map params = QueryBuilder.with("workflowState", ws).parameters();

            steps = daoService.findWithNamedQuery(WorkflowStep.FIND_BY_WORKFLOW_STATE, params);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Could not find workflow state", ex);
            steps = new ArrayList<WorkflowStep>();
        }
        return steps;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowStep findWorkflowStepById(Long id) throws DataNotFoundException {
        return daoService.findById(WorkflowStep.class, id);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowStep updateWorkflowStep(WorkflowStep step) {
        return daoService.update(step);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowStepAction createWorkflowStepAction(WorkflowStepAction stepAction) {
        return daoService.create(stepAction);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowStepAction updateWorkflowStepAction(WorkflowStepAction stepAction) {
        return daoService.update(stepAction);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteWorkflowStepActionById(Long id) {
        daoService.delete(WorkflowStepAction.class, id);
    }
    
    @Override
    public WorkflowStepValidator createWorkflowStepValidator(WorkflowStepValidator validator) {
        return daoService.create(validator);
    }
    
    @Override
    public WorkflowStepValidator updateWorkflowStepValidator(WorkflowStepValidator validator) {
        return daoService.update(validator);
    }

    @Override
    public void deleteWorkflowStepValidatorById(Long id) {
        daoService.delete(WorkflowStepValidator.class, id);
    }
}
