/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
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

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.logging.LogEntry;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.newswire.NewswireServiceProperty;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.ejb.services.NewswireServiceLocal;
import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.converge.plugins.decoders.rss.RssDecoder;
import dk.i2m.jsf.JsfUtils;
import static dk.i2m.jsf.JsfUtils.createMessage;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/Newsfeeds.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Newsfeeds {

    @EJB private NewswireServiceLocal newswire;

    @EJB private SystemFacadeLocal systemFacade;

    private DataModel newswires = null;

    private NewswireService selectedNewsfeed = null;

    private NewswireServiceProperty selectedNewswireProperty =
            new NewswireServiceProperty();

    private NewswireServiceProperty deletedProperty = null;

    private String selectedTab = "tabDetails";

    private DataModel log = new ListDataModel();

    /**
     * Creates a new instance of {@link Newsfeeds}.
     */
    public Newsfeeds() {
    }

    /**
     * Event handler for preparing the creation of a new
     * {@link NewswireService}.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onNew(ActionEvent event) {
        selectedNewsfeed = new NewswireService();
        // Default decoder class - cannot be null
        selectedNewsfeed.setDecoderClass(RssDecoder.class.getName());
        selectedNewswireProperty = new NewswireServiceProperty();
        selectedTab = "tabDetails";
    }

    /**
     * Event handler for saving or applying changes to a
     * {@link NewswireService}.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onSave(ActionEvent event) {
        if (isAddMode()) {
            selectedNewsfeed = newswire.create(selectedNewsfeed);
            createMessage("frmPage", FacesMessage.SEVERITY_INFO, "i18n",
                    "administrator_Newsfeeds_NEWSWIRE_ADDED", null);
        } else {
            newswire.update(selectedNewsfeed);
            createMessage("frmPage", FacesMessage.SEVERITY_INFO, "i18n",
                    "administrator_Newsfeeds_NEWSWIRE_UPDATED", null);
        }
        this.newswires = null;
    }

    /**
     * Event handler for deleting a {@link NewswireService}.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onDelete(ActionEvent event) {
        try {
            newswire.delete(selectedNewsfeed.getId());
            this.newswires = null;
            createMessage("frmPage", FacesMessage.SEVERITY_INFO, "i18n",
                    "administrator_Newsfeeds_NEWSWIRE_DELETED", null);
        } catch (DataNotFoundException ex) {
            createMessage("frmPage", FacesMessage.SEVERITY_WARN, "i18n",
                    "administrator_Newsfeeds_NEWSWIRE_DELETED_FAILED", null);
        }
        this.newswires = null;
    }

    /**
     * Event handler for processing all active the {@link NewswireService}s.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onDownloadFeeds(ActionEvent event) {
        newswire.downloadNewswireServices();
        createMessage("frmPage", FacesMessage.SEVERITY_INFO, "i18n",
                "administrator_Newsfeeds_NEWSWIRE_DOWNLOAD_SCHEDULED", null);
    }

    /**
     * Event handler for processing a selected {@link NewswireService}.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onDownloadNewswireService(ActionEvent event) {
        try {
            newswire.downloadNewswireService(selectedNewsfeed.getId());
            createMessage("frmPage", FacesMessage.SEVERITY_INFO, "i18n",
                    "administrator_Newsfeeds_NEWSWIRE_DOWNLOAD_SCHEDULED", null);
        } catch (DataNotFoundException ex) {
        }
    }

    /**
     * Event handler for emptying the selected {@link NewswireService}.
     *
     * @param event 
     *          Event that invoked the handler
     */
    public void onEmptyNewswireService(ActionEvent event) {
        int deleted = newswire.emptyNewswireService(selectedNewsfeed.getId());
        createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                "administrator_Newsfeeds_NEWSWIRE_SERVICE_EMPTIED",
                new Object[]{deleted});
        this.newswires = null;
    }

    /**
     * Event handler for emptying all the {@link NewswireService}s.
     * 
     * @param event 
     *          Event that invoked the handler
     */
    public void onEmptyNewswireServices(ActionEvent event) {
        for (NewswireService service : newswire.getNewswireServices()) {
            newswire.emptyNewswireService(service.getId());
        }
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(),
                "administrator_Newsfeeds_NEWSWIRE_SERVICES_EMPTIED");

        this.newswires = null;
    }

    /**
     * Event handler for updating the status of a {@link NewswireService}.
     *
     * @param event 
     *          Event that invoked the handler
     */
    public void onUpdateStatus(ActionEvent event) {
        selectedNewsfeed.setActive(!selectedNewsfeed.isActive());
        newswire.update(selectedNewsfeed);

        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(), 
                "administrator_Newsfeeds_NEWSWIRE_STATUS_TOGGLED");
        this.newswires = null;
    }

    /**
     * Event handler for adding a property to a {@link NewswireService}.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onAddProperty(ActionEvent event) {
        if (selectedNewswireProperty.getKey() != null) {
            selectedNewswireProperty.setNewswireService(selectedNewsfeed);
            selectedNewsfeed.getProperties().add(selectedNewswireProperty);
            selectedNewswireProperty = new NewswireServiceProperty();
        }
    }

    /**
     * Event handler for refreshing the log of the selected
     * {@link NewswireService}.
     *
     * @param event Event that invoked the handler
     */
    public void onRefreshLog(ActionEvent event) {
        if (this.selectedNewsfeed == null) {
            this.log = new ListDataModel();
        } else {
            List<LogEntry> entries = systemFacade.findLogEntries(
                    this.selectedNewsfeed, "" + this.selectedNewsfeed.getId(), 0,
                    2000);
            this.log = new ListDataModel(entries);
        }
    }

    /**
     * Event handler for clearing the log of the selected
     * {@link NewswireService}.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onClearLog(ActionEvent event) {
        systemFacade.removeLogEntries(
                this.selectedNewsfeed, "" + this.selectedNewsfeed.getId());
        onRefreshLog(null);
    }

    public NewswireService getSelectedNewsfeed() {
        return selectedNewsfeed;
    }

    /**
     * Executed when a {@link NewswireService} will be edited.
     *
     * @param selectedNewsfeed {@link NewswireService} to edit
     */
    public void setSelectedNewsfeed(NewswireService selectedNewsfeed) {
        this.selectedNewsfeed = selectedNewsfeed;
    }

    public NewswireServiceProperty getSelectedNewswireProperty() {
        return selectedNewswireProperty;
    }

    public void setSelectedNewswireProperty(
            NewswireServiceProperty selectedNewswireProperty) {
        this.selectedNewswireProperty = selectedNewswireProperty;
    }

    public NewswireServiceProperty getDeletedProperty() {
        return deletedProperty;
    }

    public void setDeletedProperty(NewswireServiceProperty deletedProperty) {
        this.deletedProperty = deletedProperty;
        selectedNewsfeed.getProperties().remove(deletedProperty);
    }

    /**
     * Gets the {@link DataModel} of {@link NewswireService}s.
     *
     * @return {@link DataModel} of {@link NewswireService}s.
     */
    public DataModel getNewsFeeds() {
        if (this.newswires == null) {
            this.newswires = new ListDataModel(newswire.
                    getNewswireServicesWithSubscribersAndItems());
        }
        return this.newswires;
    }

    public boolean isEditMode() {
        if (selectedNewsfeed == null || selectedNewsfeed.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isAddMode() {
        return !isEditMode();
    }

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    public DataModel getLog() {
        return log;
    }
}
