/*
 *  Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.commons.BeanComparator;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.subscriber.OutletSubscriber;
import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.ejb.facades.EntityReferenceException;
import dk.i2m.converge.ejb.facades.OutletFacadeLocal;
import dk.i2m.converge.jsf.beans.BaseBean;
import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.jsf.JsfUtils;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/Outlets.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Outlets extends BaseBean {

    @EJB private OutletFacadeLocal outletFacade;

    private DataModel outlets = null;

    private String selectedOutletTab = "tabOutlet";

    private Outlet selectedOutlet = null;

    private DataModel selectedOutletSubscribers = new ListDataModel();

    private OutletSubscriber selectedOutletSubscriber = null;

    private Department selectedDepartment = null;

    private Section selectedSection = null;

    private OutletEditionActionProperty deleteProperty;

    private EditionPattern selectedEditionPattern;

    private DataModel outletEditionActionProperties = null;

    /**
     * Creates a new instance of {@link Outlets}.
     */
    public Outlets() {
    }

    /**
     * Gets the {@link Outlet} that is currently selected.
     * <p/>
     * @return Currently selected {@link Outlet}
     */
    public Outlet getSelectedOutlet() {
        return selectedOutlet;
    }

    /**
     * Sets the {@link Outlet} that was selected by the user.
     * <p/>
     * @param selectedOutlet {@link Outlet} selected
     */
    public void setSelectedOutlet(Outlet selectedOutlet) {
        this.selectedOutlet = selectedOutlet;
        onLoadSelectedOutletSubscribers(null);
    }

    /**
     * Event handler for loading the subscribers of the selected outlet.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onLoadSelectedOutletSubscribers(ActionEvent event) {
        if (getSelectedOutlet() != null && getSelectedOutlet().getId() != null) {
            List<OutletSubscriber> subscribers = outletFacade.
                    findOutletSubscribers(getSelectedOutlet().getId(), 0, 100);
            this.selectedOutletSubscribers = new ListDataModel(subscribers);
        } else {
            this.selectedOutletSubscribers = new ListDataModel();
        }
    }

    /**
     * Gets a {@link DataModel} containing the available outlets.
     *
     * @return {@link DataModel} containing the available outlets
     */
    public DataModel getOutlets() {
        if (outlets == null) {
            outlets = new ListDataModel(outletFacade.findAllOutlets());
        }
        return outlets;
    }

    /**
     * Gets a {@link DataModel} containing the subscribers of the {@link Outlet}.
     * <p/>
     * @return {@link Datamodel} containing the subscribers of the selected {@link Outlet}
     */
    public DataModel getSelectedOutletSubscribers() {
        return selectedOutletSubscribers;
    }

    /**
     * Gets the selected {@link OutletSubscriber}.
     * <p/>
     * @return Selected {@link OutletSubscriber} or {@code null} if no subscriber is selected
     */
    public OutletSubscriber getSelectedOutletSubscriber() {
        return selectedOutletSubscriber;
    }

    /**
     * Sets the selected {@link OutletSubscriber}.
     * <p/>
     * @param selectedOutletSubscriber Selected {@link OutletSubscriber}
     */
    public void setSelectedOutletSubscriber(
            OutletSubscriber selectedOutletSubscriber) {
        this.selectedOutletSubscriber = selectedOutletSubscriber;
    }

    public String getSelectedOutletTab() {
        return selectedOutletTab;
    }

    public void setSelectedOutletTab(String selectedOutletTab) {
        this.selectedOutletTab = selectedOutletTab;
    }

    public Department getSelectedDepartment() {
        return selectedDepartment;
    }

    public void setSelectedDepartment(Department selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
    }

    public Section getSelectedSection() {
        return selectedSection;
    }

    public void setSelectedSection(Section selectedSection) {
        this.selectedSection = selectedSection;
    }

    /**
     * Determines if the {@link Outlet} is in <em>edit</em> or <em>add</em>
     * mode.
     *
     * @return {@code true} if the {@link Outlet} is in <em>edit</em> mode and {@code false} if in <em>add</em> mode
     */
    public boolean isOutletEditMode() {
        if (selectedOutlet == null || selectedOutlet.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if the {@link Outlet} is in <em>add</em> mode.
     *
     * @return {@code true} if the {@link Outlet} is in <em>add</em> mode and {@code false} if in <em>edit</em> mode
     */
    public boolean isOutletAddMode() {
        return !isOutletEditMode();
    }

    public boolean isDepartmentEditMode() {
        if (selectedDepartment == null || selectedDepartment.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isDepartmentAddMode() {
        return !isDepartmentEditMode();
    }

    public boolean isSectionEditMode() {
        if (selectedSection == null || selectedSection.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSectionAddMode() {
        return !isSectionEditMode();
    }

    public void onNewOutlet(ActionEvent event) {
        selectedOutlet = new Outlet();
    }

    public void onRecloseEditions(ActionEvent event) {
        if (selectedOutlet.getId() != null) {
            outletFacade.scheduleActionsOnOutlet(selectedOutlet.getId());
        }
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                Bundle.i18n.name(), "administrator_Outlets_EDITIONS_RECLOSED");
    }

    /**
     * Executes an {@link OutletEditionAction} of all editions of the selected
     * {@link Outlet}.
     * <p/>
     * @param action * {@link OutletEditionAction} to execute
     */
    public void setExecuteAction(OutletEditionAction action) {
        if (selectedOutlet != null && action != null) {
            outletFacade.scheduleActionOnOutlet(selectedOutlet.getId(), action.
                    getId());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(),
                    "administrator_Outlets_EXECUTE_ACTION_ON_EDITIONS");
        }
    }

    /**
     * Event handler for saving updates to the {@link Outlet}.
     *
     * @param event Event that invoked the handler
     */
    public void onSaveOutlet(ActionEvent event) {
        if (isOutletAddMode()) {
            selectedOutlet = outletFacade.createOutlet(selectedOutlet);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(),
                    "administrator_Outlets_OUTLET_CREATED");
        } else {
            selectedOutlet = outletFacade.updateOutlet(selectedOutlet);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(),
                    "administrator_Outlets_OUTLET_UPDATED");
        }

        if (!selectedOutlet.isValid()) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "administrator_Outlets_INVALID_OUTLET");
        }

        this.outlets = null;
    }

    /**
     * Event handler for deleting the {@link Outlet}.
     *
     * @param event Event that invoked the handler
     */
    public void onDeleteOutlet(ActionEvent event) {
        outletFacade.deleteOutletById(selectedOutlet.getId());
        this.outlets = null;
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                Bundle.i18n.name(), "administrator_Outlets_OUTLET_DELETED");
    }

    public void onNewDepartment(ActionEvent event) {
        selectedDepartment = new Department();
        selectedDepartment.setOutlet(selectedOutlet);
    }

    public void onNewSection(ActionEvent event) {
        selectedSection = new Section();
        selectedSection.setOutlet(selectedOutlet);
    }

    public void onSaveSection(ActionEvent event) {
        if (isSectionAddMode()) {
            selectedSection = outletFacade.createSection(selectedSection);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(), "administrator_Outlets_SECTION_CREATED");
        } else {
            selectedSection = outletFacade.updateSection(selectedSection);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(), "administrator_Outlets_SECTION_UPDATED");
        }
        reloadSelectedOutlet();
    }

    public void onDeleteSection(ActionEvent event) {
        try {
            outletFacade.deleteSection(selectedSection.getId());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(), "administrator_Outlets_SECTION_DELETED");
        } catch (EntityReferenceException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Outlets_SECTION_CANNOT_BE_DELETED_ENTITY_REFERENCE");
        }

        reloadSelectedOutlet();
    }

    /**
     * Event handler for preparing the creation of a new outlet subscriber.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onNewOutletSubscriber(ActionEvent event) {
        this.selectedOutletSubscriber = new OutletSubscriber();
        this.selectedOutletSubscriber.setOutlet(selectedOutlet);
    }

    public void onSaveOutletSubscriber(ActionEvent event) {
        Date now = Calendar.getInstance().getTime();
        if (isOutletAddMode()) {
            if (!selectedOutletSubscriber.isSubscribed()) {
                selectedOutletSubscriber.setUnsubscriptionDate(now);
            } else {
                selectedOutletSubscriber.setSubscriptionDate(now);
            }
            
            selectedOutletSubscriber = outletFacade.createSubscriber(
                    selectedOutletSubscriber);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(),
                    "administrator_Outlets_OUTLET_SUBSCRIBER_CREATED");
        } else {
            selectedOutletSubscriber = outletFacade.updateSubscriber(
                    selectedOutletSubscriber);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(),
                    "administrator_Outlets_OUTLET_SUBSCRIBER_UPDATED");
        }
        onLoadSelectedOutletSubscribers(event);
    }

    public void onDeleteOutletSubscriber(ActionEvent event) {
        outletFacade.deleteSubscriberById(selectedOutletSubscriber.getId());
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                Bundle.i18n.name(),
                "administrator_Outlets_OUTLET_SUBSCRIBER_DELETED");
        onLoadSelectedOutletSubscribers(event);
    }

    public boolean isOutletSubscriberAddMode() {
        if (selectedOutletSubscriber == null || selectedOutletSubscriber.getId()
                == null) {
            return true;
        } else {
            return false;
        }
    }

    private void reloadSelectedOutlet() {
        try {
            selectedOutlet = outletFacade.findOutletById(selectedOutlet.getId());
        } catch (DataNotFoundException ex) {
            Logger.getLogger(Outlets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, Section> getActiveSections() {
        Map<String, Section> activeSections =
                new LinkedHashMap<String, Section>();

        if (selectedOutlet != null) {
            Collections.sort(selectedOutlet.getSections(), new BeanComparator(
                    "fullName"));

            for (Section section : selectedOutlet.getSections()) {
                if (section.isActive()) {
                    activeSections.put(section.getFullName(), section);
                }
            }
        }

        return activeSections;
    }

    private OutletEditionAction selectedOutletEditionAction;

    private OutletEditionActionProperty selectedOutletEditionActionProperty =
            new OutletEditionActionProperty();

    private String selectedOutletEditionDetailsTab = "";

    public void onNewOutletAction(ActionEvent event) {
        selectedOutletEditionAction = new OutletEditionAction();
        selectedOutletEditionAction.setOutlet(selectedOutlet);
        selectedOutletEditionAction.setExecuteOrder(1);
        // A default action class is required to avoid NullPointerException from JSF
        selectedOutletEditionAction.setActionClass(dk.i2m.converge.plugins.indexedition.IndexEditionAction.class.
                getName());
    }

    public void onAddActionProperty(ActionEvent event) {
        selectedOutletEditionActionProperty.setOutletEditionAction(
                selectedOutletEditionAction);
        selectedOutletEditionAction.getProperties().add(
                selectedOutletEditionActionProperty);
        selectedOutletEditionActionProperty = new OutletEditionActionProperty();
        outletEditionActionProperties = null;
    }

    public OutletEditionAction getSelectedOutletEditionAction() {
        return selectedOutletEditionAction;
    }

    public void setSelectedOutletEditionAction(
            OutletEditionAction selectedOutletEditionAction) {
        this.selectedOutletEditionAction = selectedOutletEditionAction;
        this.outletEditionActionProperties = null;
    }

    public DataModel getOutletEditionActionProperties() {
        if (outletEditionActionProperties == null) {
            if (this.selectedOutletEditionAction != null) {
                this.outletEditionActionProperties =
                        new ListDataModel(this.selectedOutletEditionAction.
                        getProperties());
            } else {
                this.outletEditionActionProperties =
                        new ListDataModel(new ArrayList());
            }
        }
        return outletEditionActionProperties;
    }

    public String getSelectedOutletEditionDetailsTab() {
        return selectedOutletEditionDetailsTab;
    }

    public void setSelectedOutletEditionDetailsTab(
            String selectedOutletEditionDetailsTab) {
        this.selectedOutletEditionDetailsTab = selectedOutletEditionDetailsTab;
    }

    public OutletEditionActionProperty getSelectedOutletEditionActionProperty() {
        return selectedOutletEditionActionProperty;
    }

    public void setSelectedOutletEditionActionProperty(
            OutletEditionActionProperty selectedOutletEditionActionProperty) {
        this.selectedOutletEditionActionProperty =
                selectedOutletEditionActionProperty;
    }

    public OutletEditionActionProperty getDeleteProperty() {
        return deleteProperty;
    }

    public void setDeleteProperty(OutletEditionActionProperty deleteProperty) {
        this.deleteProperty = deleteProperty;
        selectedOutletEditionAction.getProperties().remove(deleteProperty);
        this.outletEditionActionProperties = null;
    }

    public boolean isActionEditMode() {
        if (selectedOutletEditionAction == null || selectedOutletEditionAction.
                getId() == null) {
            return false;
        } else {
            return true;
        }

    }

    public boolean isActionAddMode() {
        return !isActionEditMode();
    }

    /**
     * Event handler for removing an action from an {@link Outlet}.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onDeleteOutletAction(ActionEvent event) {
        selectedOutlet.getEditionActions().remove(selectedOutletEditionAction);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                Bundle.i18n.name(),
                "administrator_Outlets_OUTLET_ACTION_REMOVE");

    }

    /**
     * Event handler for updating or creating an outlet action.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onSaveOutletAction(ActionEvent event) {
        if (isActionAddMode()) {
            selectedOutletEditionAction.setOutlet(selectedOutlet);
            selectedOutletEditionAction = outletFacade.createOutletAction(
                    selectedOutletEditionAction);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(),
                    "administrator_Outlets_OUTLET_ACTION_CREATED");
        } else {
            selectedOutletEditionAction = outletFacade.updateOutletAction(
                    selectedOutletEditionAction);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(),
                    "administrator_Outlets_OUTLET_ACTION_UPDATED");
        }

        // Update the outlet (as it will now have different actions)
        try {
            selectedOutlet = outletFacade.findOutletById(selectedOutlet.getId());
        } catch (DataNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public EditionPattern getSelectedEditionPattern() {
        return selectedEditionPattern;
    }

    public void setSelectedEditionPattern(EditionPattern selectedEditionPattern) {
        this.selectedEditionPattern = selectedEditionPattern;
    }

    public boolean isEditionPatternEditMode() {
        if (selectedEditionPattern == null || selectedEditionPattern.getId()
                == null) {
            return false;
        } else {
            return true;
        }
    }

    public void onNewEditionPattern(ActionEvent event) {
        selectedEditionPattern = new EditionPattern();
        selectedEditionPattern.setOutlet(selectedOutlet);
    }

    public void onDeleteEditionPattern(ActionEvent event) {
        selectedOutlet.getEditionPatterns().remove(selectedEditionPattern);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(),
                "administrator_Outlets_EDITION_PATTERN_DELETED");
    }

    public void onSaveEditionPattern(ActionEvent event) {

        if (isEditionPatternEditMode()) {
            selectedEditionPattern = outletFacade.updateEditionPattern(
                    selectedEditionPattern);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Outlets_EDITION_PATTERN_UPDATED");

        } else {
            selectedEditionPattern.setOutlet(selectedOutlet);
            selectedEditionPattern = outletFacade.createEditionPattern(
                    selectedEditionPattern);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, 
                    Bundle.i18n.name(),
                    "administrator_Outlets_EDITION_PATTERN_CREATED");
        }

        // Update the outlet (as it will now have different patterns)
        try {
            selectedOutlet = outletFacade.findOutletById(selectedOutlet.getId());
        } catch (DataNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }

    }
}
