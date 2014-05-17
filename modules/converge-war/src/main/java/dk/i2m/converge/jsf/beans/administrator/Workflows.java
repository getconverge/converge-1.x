/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.NewsItemField;
import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.ejb.facades.WorkflowFacadeLocal;
import dk.i2m.converge.jsf.beans.BaseBean;
import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.jsf.JsfUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/Workflows.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Workflows extends BaseBean {

    @EJB private WorkflowFacadeLocal workflowFacade;

    private DataModel workflows;

    private Workflow selected;

    private WorkflowState selectedWorkflowState = new WorkflowState();

    private String selectedTab = "tabWorkflow";

    private String selectedWorkflowStateTab = "tabWorkflowState";

    private String selectedWorkflowStepTab = "tabWorkflowStep";

    private String selectedWorkflowStepActionTab = "tabWorkflowStepAction";

    private String selectedWorkflowStepValidatorTab = "tabWorkflowStepValidator";

    private WorkflowStateType selectedWorkflowStateType;

    private WorkflowStep selectedWorkflowStep;

    private WorkflowStepAction selectedWorkflowStepAction;

    private WorkflowStepActionProperty selectedWorkflowStepActionProperty =
            new WorkflowStepActionProperty();

    private WorkflowStepValidatorProperty selectedWorkflowStepValidatorProperty =
            new WorkflowStepValidatorProperty();

    private List<NewsItemField> workflowStateVisibleFields;

    private WorkflowStepValidator selectedWorkflowStepValidator;

    /**
     * Gets a {@link DataModel} containing the available workflows.
     *
     * @return {@link DataModel} containing the available workflows
     */
    public DataModel getWorkflows() {
        if (workflows == null) {
            workflows = new ListDataModel(workflowFacade.findAllWorkflows());
        }
        return workflows;
    }

    public Workflow getSelected() {
        return selected;
    }

    public void setSelected(Workflow selected) {
        this.selected = selected;
    }

    public WorkflowState getSelectedWorkflowState() {
        return selectedWorkflowState;
    }

    public void setSelectedWorkflowState(WorkflowState selectedWorkflowState) {
        this.selectedWorkflowState = selectedWorkflowState;
        setSelectedWorkflowStateType(selectedWorkflowState.getType());

        if (selectedWorkflowState.getVisibleFields() != null) {
            workflowStateVisibleFields = new ArrayList<NewsItemField>();
            for (NewsItemFieldVisible nifv : selectedWorkflowState.
                    getVisibleFields()) {
                workflowStateVisibleFields.add(nifv.getField());
            }
        }

    }

    public WorkflowStateType getSelectedWorkflowStateType() {
        return selectedWorkflowStateType;
    }

    public void setSelectedWorkflowStateType(
            WorkflowStateType selectedWorkflowStateType) {
        this.selectedWorkflowStateType = selectedWorkflowStateType;
    }

    /**
     * Gets the name of the selected tab.
     *
     * @return Name of the selected tab
     */
    public String getSelectedTab() {
        return selectedTab;
    }

    /**
     * Sets the name of the tab to select.
     *
     * @param selectedTab
     *          Name of the tab to select
     */
    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    public String getSelectedWorkflowStateTab() {
        return selectedWorkflowStateTab;
    }

    public void setSelectedWorkflowStateTab(String selectedWorkflowStateTab) {
        this.selectedWorkflowStateTab = selectedWorkflowStateTab;
    }

    public WorkflowStep getSelectedWorkflowStep() {
        return selectedWorkflowStep;
    }

    public void setSelectedWorkflowStep(WorkflowStep selectedWorkflowStep) {
        this.selectedWorkflowStep = selectedWorkflowStep;

//        if (selectedWorkflowStep.getValidation() != null) {
//            workflowStepRequiredFields = new ArrayList<NewsItemField>();
//            for (WorkflowStepValidation validation : selectedWorkflowStep.getValidation()) {
//                workflowStepRequiredFields.add(validation.getField());
//            }
//        }
    }

    public String getSelectedWorkflowStepTab() {
        return selectedWorkflowStepTab;
    }

    public void setSelectedWorkflowStepTab(String selectedWorkflowStepTab) {
        this.selectedWorkflowStepTab = selectedWorkflowStepTab;
    }

    /**
     * Determines if the {@link Workflow} is in <em>edit</em> or <em>add</em>
     * mode.
     *
     * @return <code>true</code> if the {@link Workflow} is in <em>edit</em>
     *         mode and <code>false</code> if in <em>add</em> mode
     */
    public boolean isEditMode() {
        if (selected == null || selected.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if the {@link Workflow} is in <em>add</em> mode.
     *
     * @return <code>true</code> if the {@link Workflow} is in <em>add</em> mode
     *         and <code>false</code> if in <em>edit</em> mode
     */
    public boolean isAddMode() {
        return !isEditMode();
    }

    /**
     * Determines if the {@link WorkflowState} is in <em>edit</em> or <em>add</em>
     * mode.
     *
     * @return <code>true</code> if the {@link WorkflowState} is in <em>edit</em>
     *         mode and <code>false</code> if in <em>add</em> mode
     */
    public boolean isWorkflowStateEditMode() {
        if (selectedWorkflowState == null || selectedWorkflowState.getId()
                == null) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Determines if the {@link WorkflowState} is in <em>add</em> mode.
     *
     * @return <code>true</code> if the {@link WorkflowState} is in <em>add</em> mode
     *         and <code>false</code> if in <em>edit</em> mode
     */
    public boolean isWorkflowStateAddMode() {
        return !isWorkflowStateEditMode();
    }

    /**
     * Event handler for saving updates to the {@link Workflow}.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onApplyWorkflow(ActionEvent event) {
        if (isAddMode()) {
            selected = workflowFacade.createWorkflow(selected);
            JsfUtils.createMessage("frmWorkflowDetails",
                    FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_CREATED");
        } else {
            workflowFacade.updateWorkflow(selected);
            JsfUtils.createMessage("frmWorkflowDetails",
                    FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_UPDATED");
        }

        this.workflows = null;
    }

    /**
     * Event handler for saving a {@link Workflow}.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onSaveWorkflow(ActionEvent event) {
        if (isAddMode()) {
            selected = workflowFacade.createWorkflow(selected);
            JsfUtils.createMessage("frmWorkflowDetails",
                    FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_CREATED");
        } else {
            workflowFacade.updateWorkflow(selected);
            JsfUtils.createMessage("frmWorkflowDetails",
                    FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_UPDATED");
        }

        this.workflows = null;
    }

    /**
     * Event handler for saving updates to the {@link Workflow}.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onDeleteWorkflow(ActionEvent event) {
        if (!isAddMode()) {
            workflowFacade.deleteWorkflowById(selected.getId());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_DELETED");
        }

        this.workflows = null;
    }

    /**
     * Event handler for creating a new {@link Workflow}.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onNewWorkflow(ActionEvent event) {
        selected = new Workflow();
    }

    /**
     * Event handler for creating a new {@link WorkflowState}.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onNewWorkflowState(ActionEvent event) {
        selectedWorkflowState = new WorkflowState();
        selectedWorkflowState.setWorkflow(selected);
        selectedWorkflowState.setPermission(WorkflowStatePermission.USER);
        selectedWorkflowStateType = WorkflowStateType.MIDDLE;
        workflowStateVisibleFields = new ArrayList<NewsItemField>();
    }

    /**
     * Event handler for creating a new {@link WorkflowStateOption}.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onNewWorkflowStep(ActionEvent event) {
        selectedWorkflowStep = new WorkflowStep();
        selectedWorkflowStep.setFromState(selectedWorkflowState);
//        workflowStepRequiredFields = new ArrayList<NewsItemField>();
    }

    public void onNewWorkflowStepAction(ActionEvent event) {
        selectedWorkflowStepAction = new WorkflowStepAction();
        selectedWorkflowStepAction.setWorkflowStep(selectedWorkflowStep);
        // A default action class is required to avoid NullPointerException from JSF
        selectedWorkflowStepAction.setActionClass(dk.i2m.converge.plugins.alertaction.AlertAction.class.
                getName());
    }

    public void onNewWorkflowStepValidator(ActionEvent event) {
        selectedWorkflowStepValidator = new WorkflowStepValidator();
        selectedWorkflowStepValidator.setStep(selectedWorkflowStep);
        // A default action class is required to avoid NullPointerException from JSF
        selectedWorkflowStepValidator.setValidatorClass(dk.i2m.converge.plugins.validators.lengthvalidator.LengthValidator.class.
                getName());
    }

    /**
     * Event handler for saving updates to the {@link WorkflowState}.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onSaveWorkflowState(ActionEvent event) {

        if (isWorkflowStateAddMode()) {
            for (NewsItemField nif : this.workflowStateVisibleFields) {
                NewsItemFieldVisible nifv = new NewsItemFieldVisible();
                nifv.setField(nif);
                nifv.setWorkflowState(selectedWorkflowState);
                selectedWorkflowState.getVisibleFields().add(nifv);
            }

            selectedWorkflowState = workflowFacade.createWorkflowState(
                    selectedWorkflowState);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_STATE_CREATED");
        } else {
            selectedWorkflowState.getVisibleFields().clear();

            for (NewsItemField nif : this.workflowStateVisibleFields) {
                NewsItemFieldVisible nifv = new NewsItemFieldVisible();
                nifv.setField(nif);
                nifv.setWorkflowState(selectedWorkflowState);
                selectedWorkflowState.getVisibleFields().add(nifv);
            }

            selectedWorkflowState = workflowFacade.updateWorkflowState(
                    selectedWorkflowState);

            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_STATE_UPDATED");
        }

        try {
            selected = workflowFacade.findWorkflowById(selected.getId());

            if (selected.getStartState() != null && selected.getStartState().
                    equals(selectedWorkflowState)) {
                selected.setStartState(null);
            }

            if (selected.getEndState() != null && selected.getEndState().equals(
                    selectedWorkflowState)) {
                selected.setEndState(null);
            }

            if (selected.getTrashState() != null && selected.getTrashState().
                    equals(selectedWorkflowState)) {
                selected.setTrashState(null);
            }

            if (selectedWorkflowStateType.equals(WorkflowStateType.START)) {
                selected.setStartState(selectedWorkflowState);
            } else if (selectedWorkflowStateType.equals(WorkflowStateType.END)) {
                selected.setEndState(selectedWorkflowState);
            } else if (selectedWorkflowStateType.equals(WorkflowStateType.TRASH)) {
                selected.setTrashState(selectedWorkflowState);
            }
            workflowFacade.updateWorkflow(selected);
            selected = workflowFacade.findWorkflowById(selected.getId());
        } catch (DataNotFoundException ex) {
            logger.log(Level.WARNING, "Workflow does not exist", ex);
        }
    }

    public void onApplyWorkflowState(ActionEvent event) {
        onSaveWorkflowState(event);
    }

    public void onDeleteWorkflowState(ActionEvent event) {
        try {
            workflowFacade.deleteWorkflowStateById(selectedWorkflowState.getId());
            selected = workflowFacade.findWorkflowById(selected.getId());
        } catch (DataNotFoundException ex) {
            logger.log(Level.WARNING, "Workflow does not exist", ex);
        }
    }

    /**
     * Event handler for deleting a {@link WorkflowStepAction} from a
     * {@link WorkflowStep}.
     * 
     * @param event
     *          Event that invoked the handler
     */
    public void onDeleteWorkflowStepAction(ActionEvent event) {
        workflowFacade.deleteWorkflowStepActionById(selectedWorkflowStepAction.
                getId());
        selectedWorkflowStep.getActions().remove(selectedWorkflowStepAction);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(),
                "administrator_Workflows_WORKFLOW_STEP_ACTION_DELETED");
    }

    /**
     * Event handler for saving updates to the {@link WorkflowStep}.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onSaveWorkflowStep(ActionEvent event) {
        if (isWorkflowStepAddMode()) {
//            for (NewsItemField nif : this.workflowStepRequiredFields) {
//                WorkflowStepValidation validation = new WorkflowStepValidation();
//                validation.setField(nif);
//                validation.setWorkflowStep(selectedWorkflowStep);
//                selectedWorkflowStep.getValidation().add(validation);
//            }

            selectedWorkflowStep = workflowFacade.createWorkflowStep(
                    selectedWorkflowStep);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_STEP_CREATED");
        } else {
//            // Remove existing validation
//            selectedWorkflowStep.getValidation().clear();
//
//            // Add new validation
//            for (NewsItemField nif : this.workflowStepRequiredFields) {
//                WorkflowStepValidation validation = new WorkflowStepValidation();
//                validation.setField(nif);
//                validation.setWorkflowStep(selectedWorkflowStep);
//                selectedWorkflowStep.getValidation().add(validation);
//            }

            selectedWorkflowStep = workflowFacade.updateWorkflowStep(
                    selectedWorkflowStep);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_STEP_UPDATED");
        }


        // Update the workflow state (as it will not have different steps)
        try {
            selectedWorkflowState =
                    workflowFacade.findWorkflowStateById(selectedWorkflowState.
                    getId());
        } catch (DataNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void onApplyWorkflowStep(ActionEvent event) {
        onSaveWorkflowStep(event);
    }

    public void onDeleteWorkflowStep(ActionEvent event) {
        try {
            workflowFacade.deleteWorkflowStepById(selectedWorkflowStep.getId());
            selectedWorkflowState =
                    workflowFacade.findWorkflowStateById(selectedWorkflowState.
                    getId());
        } catch (DataNotFoundException ex) {
            logger.log(Level.WARNING, "Workflow does not exist", ex);
        }
    }

    /**
     * Gets a {@link Map} of {@link WorkflowState}s for the workflow.
     *
     * @return {@link Map} of {@link WorkflowState}s
     */
    public Map<String, WorkflowState> getWorkflowStates() {
        Map<String, WorkflowState> states =
                new LinkedHashMap<String, WorkflowState>();
        for (WorkflowState state : selectedWorkflowStep.getFromState().
                getWorkflow().getStates()) {
            String lbl;
            if (state.isGroupPermission()) {
                lbl = "administrator_Workflows_WORKFLOW_STATE_ROLE";
            } else {
                lbl = "administrator_Workflows_WORKFLOW_STATE_USER";
            }
            String stateLbl =
                    JsfUtils.getMessage(Bundle.i18n.name(), lbl,
                    new Object[]{state.getName(), state.getActorRole().getName()});
            states.put(stateLbl, state);
        }

        return states;
    }

    /**
     * Determines if the {@link WorkflowStep} is in <em>edit</em> or <em>add</em>
     * mode.
     *
     * @return <code>true</code> if the {@link WorkflowStep} is in <em>edit</em>
     *         mode and <code>false</code> if in <em>add</em> mode
     */
    public boolean isWorkflowStepEditMode() {
        if (selectedWorkflowStep == null || selectedWorkflowStep.getId() == null) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Determines if the {@link WorkflowStep} is in <em>add</em> mode.
     *
     * @return <code>true</code> if the {@link WorkflowStep} is in <em>add</em> mode
     *         and <code>false</code> if in <em>edit</em> mode
     */
    public boolean isWorkflowStepAddMode() {
        return !isWorkflowStepEditMode();
    }

    public List<NewsItemField> getWorkflowStateVisibleFields() {
        return workflowStateVisibleFields;
    }

    public void setWorkflowStateVisibleFields(
            List<NewsItemField> workflowStateVisibleFields) {
        this.workflowStateVisibleFields = workflowStateVisibleFields;
    }

    public WorkflowStepAction getSelectedWorkflowStepAction() {
        return selectedWorkflowStepAction;
    }

    public void setSelectedWorkflowStepAction(
            WorkflowStepAction selectedWorkflowStepAction) {
        this.selectedWorkflowStepAction = selectedWorkflowStepAction;
    }

    public String getSelectedWorkflowStepActionTab() {
        return selectedWorkflowStepActionTab;
    }

    public void setSelectedWorkflowStepActionTab(
            String selectedWorkflowStepActionTab) {
        this.selectedWorkflowStepActionTab = selectedWorkflowStepActionTab;
    }

    /**
     * Determines if the {@link WorkflowStepAction} is in <em>edit</em> or
     * <em>add</em> mode.
     *
     * @return <code>true</code> if the {@link WorkflowStepAction} is in <em>edit</em>
     *         mode and <code>false</code> if in <em>add</em> mode
     */
    public boolean isWorkflowStepActionEditMode() {
        if (selectedWorkflowStepAction == null || selectedWorkflowStepAction.
                getId() == null) {
            return false;
        } else {
            return true;
        }

    }

    public boolean isWorkflowStepActionAddMode() {
        return !isWorkflowStepActionEditMode();
    }

    public boolean isWorkflowStepValidatorEditMode() {
        if (selectedWorkflowStepValidator == null
                || selectedWorkflowStepValidator.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isWorkflowStepValidatorAddMode() {
        return !isWorkflowStepValidatorEditMode();
    }

    public void setDeleteProperty(WorkflowStepActionProperty property) {
        selectedWorkflowStepAction.getProperties().remove(property);
    }

    public void setDeleteValidatorProperty(
            WorkflowStepValidatorProperty property) {
        selectedWorkflowStepValidator.getProperties().remove(property);
    }

    public WorkflowStepActionProperty getSelectedWorkflowStepActionProperty() {
        return selectedWorkflowStepActionProperty;
    }

    public void setSelectedWorkflowStepActionProperty(
            WorkflowStepActionProperty selectedWorkflowStepActionProperty) {
        this.selectedWorkflowStepActionProperty =
                selectedWorkflowStepActionProperty;
    }

    public void onAddWorkflowStepActionProperty(ActionEvent event) {
        if (selectedWorkflowStepActionProperty.getKey() != null) {
            selectedWorkflowStepActionProperty.setWorkflowStepAction(
                    selectedWorkflowStepAction);
            selectedWorkflowStepAction.getProperties().add(
                    selectedWorkflowStepActionProperty);
            selectedWorkflowStepActionProperty =
                    new WorkflowStepActionProperty();
        }
    }

    public void onAddWorkflowStepValidatorProperty(ActionEvent event) {
        if (selectedWorkflowStepValidatorProperty.getKey() != null) {
            selectedWorkflowStepValidatorProperty.setWorkflowStepValidator(
                    selectedWorkflowStepValidator);
            selectedWorkflowStepValidator.getProperties().add(
                    selectedWorkflowStepValidatorProperty);
            selectedWorkflowStepValidatorProperty =
                    new WorkflowStepValidatorProperty();
        }
    }

    public void onSaveWorkflowStepAction(ActionEvent event) {

        if (isWorkflowStepActionAddMode()) {
            selectedWorkflowStepAction.setWorkflowStep(selectedWorkflowStep);
            selectedWorkflowStepAction =
                    workflowFacade.createWorkflowStepAction(
                    selectedWorkflowStepAction);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_STEP_ACTION_CREATED");
        } else {
            selectedWorkflowStepAction =
                    workflowFacade.updateWorkflowStepAction(
                    selectedWorkflowStepAction);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_STEP_ACTION_UPDATED");
        }

        // Update the workflow state (as it will not have different steps)
        try {
            selectedWorkflowStep =
                    workflowFacade.findWorkflowStepById(selectedWorkflowStep.
                    getId());
        } catch (DataNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }

    }

    public void onSaveWorkflowStepValidator(ActionEvent event) {

        if (isWorkflowStepValidatorAddMode()) {
            selectedWorkflowStepValidator.setStep(selectedWorkflowStep);
            selectedWorkflowStepValidator = workflowFacade.
                    createWorkflowStepValidator(selectedWorkflowStepValidator);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_STEP_VALIDATOR_CREATED");
        } else {
            selectedWorkflowStepValidator = workflowFacade.
                    updateWorkflowStepValidator(selectedWorkflowStepValidator);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Workflows_WORKFLOW_STEP_VALIDATOR_UPDATED");
        }

        // Update the workflow state (as it will not have different steps)
        try {
            selectedWorkflowStep =
                    workflowFacade.findWorkflowStepById(selectedWorkflowStep.
                    getId());
        } catch (DataNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }

    }

    public void onDeleteWorkflowStepValidator(ActionEvent event) {
        workflowFacade.deleteWorkflowStepValidatorById(selectedWorkflowStepValidator.
                getId());
        selectedWorkflowStep.getValidators().remove(
                selectedWorkflowStepValidator);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(),
                "administrator_Workflows_WORKFLOW_STEP_VALIDATOR_DELETED");
    }

    public WorkflowStepValidator getSelectedWorkflowStepValidator() {
        return selectedWorkflowStepValidator;
    }

    public void setSelectedWorkflowStepValidator(
            WorkflowStepValidator selectedWorkflowStepValidator) {
        this.selectedWorkflowStepValidator = selectedWorkflowStepValidator;
    }

    public String getSelectedWorkflowStepValidatorTab() {
        return selectedWorkflowStepValidatorTab;
    }

    public void setSelectedWorkflowStepValidatorTab(
            String selectedWorkflowStepValidatorTab) {
        this.selectedWorkflowStepValidatorTab = selectedWorkflowStepValidatorTab;
    }

    public WorkflowStepValidatorProperty getSelectedWorkflowStepValidatorProperty() {
        return selectedWorkflowStepValidatorProperty;
    }

    public void setSelectedWorkflowStepValidatorProperty(
            WorkflowStepValidatorProperty selectedWorkflowStepValidatorProperty) {
        this.selectedWorkflowStepValidatorProperty =
                selectedWorkflowStepValidatorProperty;
    }
}
