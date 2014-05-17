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
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.CatalogueHookInstance;
import dk.i2m.converge.core.content.catalogue.CatalogueHookInstanceProperty;
import dk.i2m.converge.core.plugin.CatalogueHook;
import dk.i2m.converge.core.plugin.Plugin;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.jsf.JsfUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/Catalogues.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Catalogues {

    protected static final Logger LOG = Logger.getLogger(Catalogues.class.
            getName());

    @EJB private CatalogueFacadeLocal catalogueFacade;

    @EJB private SystemFacadeLocal systemFacade;

    private DataModel repositories = null;

    private Catalogue selectedMediaRepository = null;

    private String selectedCatalogueActionDetailsTab = "tabCatalogueAction";

    private CatalogueHookInstance selectedCatalogueAction;

    private CatalogueHookInstanceProperty selectedCatalogueActionProperty =
            new CatalogueHookInstanceProperty();

    private DataModel catalogueActionProperties = null;

    public Catalogues() {
    }

    public void onIndex(ActionEvent event) {
        try {
            catalogueFacade.indexCatalogues();
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Catalogues_INDEXING_COMPLETE");
        } catch (Exception ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR,
                    Bundle.i18n.name(), "Generic_AN_ERROR_OCCURRED_X",
                    new Object[]{ex.getMessage()});
        }
    }

    public void onNew(ActionEvent event) {
        selectedMediaRepository = new Catalogue();
    }

    public void onSave(ActionEvent event) {
        repositories = null;
        if (isEditMode()) {
            selectedMediaRepository = catalogueFacade.update(
                    selectedMediaRepository);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Catalogues_CATALOGUE_UPDATED");
        } else {
            selectedMediaRepository = catalogueFacade.create(
                    selectedMediaRepository);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Catalogues_CATALOGUE_CREATED");
        }
    }

    /**
     * Event handler for deleting the {@link Catalogues#selectedMediaRepository}.
     * 
     * @param event 
     *          Event that invoked the handler
     */
    public void onDelete(ActionEvent event) {
        try {
            catalogueFacade.deleteCatalogueById(selectedMediaRepository.getId());
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Catalogues_CATALOGUE_DELETED");
        } catch (DataNotFoundException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Catalogues_CATALOGUE_DELETED_FAILED");
        }
        this.repositories = null;
    }

    public void onActionBatchAll(ActionEvent event) {
        for (CatalogueHookInstance instance : selectedMediaRepository.getHooks()) {
            try {
                catalogueFacade.executeBatchHook(instance,
                        selectedMediaRepository.getId());
            } catch (DataNotFoundException ex) {
                return;
            }
        }
    }

    public void setActionBatch(CatalogueHookInstance instance) {
        try {
            catalogueFacade.executeBatchHook(instance, selectedMediaRepository.
                    getId());
        } catch (DataNotFoundException ex) {
            return;
        }
    }

    public DataModel getRepositories() {
        if (repositories == null) {
            repositories =
                    new ListDataModel(catalogueFacade.findAllCatalogues());
        }
        return repositories;
    }

    public Catalogue getSelectedMediaRepository() {
        return selectedMediaRepository;
    }

    public void setSelectedMediaRepository(Catalogue selectedMediaRepository) {
        this.selectedMediaRepository = selectedMediaRepository;
    }

    public boolean isEditMode() {
        if (selectedMediaRepository == null || selectedMediaRepository.getId()
                == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the name of the selected tab for the catalogue action.
     * 
     * @return Name of the selected tab for the catalogue action
     */
    public String getSelectedCatalogueActionDetailsTab() {
        return selectedCatalogueActionDetailsTab;
    }

    /**
     * Sets the selected tab for the catalogue action.
     * 
     * @param selectedCatalogueActionDetailsTab 
     *          ID of the selected tab
     */
    public void setSelectedCatalogueActionDetailsTab(
            String selectedCatalogueActionDetailsTab) {
        this.selectedCatalogueActionDetailsTab =
                selectedCatalogueActionDetailsTab;
    }

    /**
     * Gets the selected instance of the {@link CatalogueHook} being created or edited.
     * 
     * @return Selected instance of the {@link CatalogueHook} being created or edited
     */
    public CatalogueHookInstance getSelectedCatalogueAction() {
        return selectedCatalogueAction;
    }

    /**
     * Sets the selected instance of the {@link CatalogueHook} to be created or edited.
     * 
     * @param selectedCatalogueAction 
     *          Instance of {@link CatalogueHook} to be created or edited
     */
    public void setSelectedCatalogueAction(
            CatalogueHookInstance selectedCatalogueAction) {
        this.selectedCatalogueAction = selectedCatalogueAction;
    }

    /**
     * Gets the selected action property being created.
     * 
     * @return Selected action property being created
     */
    public CatalogueHookInstanceProperty getSelectedCatalogueActionProperty() {
        return selectedCatalogueActionProperty;
    }

    /**
     * Sets the action property being created.
     * 
     * @param selectedCatalogueActionProperty 
     *          Action property being created
     */
    public void setSelectedCatalogueActionProperty(
            CatalogueHookInstanceProperty selectedCatalogueActionProperty) {
        this.selectedCatalogueActionProperty = selectedCatalogueActionProperty;
    }

    public CatalogueHookInstanceProperty getDeleteProperty() {
        return null;
    }

    public void setDeleteProperty(CatalogueHookInstanceProperty deleteProperty) {
        selectedCatalogueAction.getProperties().remove(deleteProperty);
        this.catalogueActionProperties = null;
    }

    public DataModel getCatalogueActionProperties() {
        if (this.catalogueActionProperties == null) {
            if (this.selectedCatalogueAction != null) {
                this.catalogueActionProperties =
                        new ListDataModel(this.selectedCatalogueAction.
                        getProperties());
            } else {
                this.catalogueActionProperties =
                        new ListDataModel(new ArrayList());
            }
        }
        return this.catalogueActionProperties;
    }

    public boolean isActionEditMode() {
        if (selectedCatalogueAction == null || selectedCatalogueAction.getId()
                == null) {
            return false;
        } else {
            return true;
        }

    }

    public boolean isActionAddMode() {
        return !isActionEditMode();
    }

    public void onNewCatalogueAction(ActionEvent event) {
        selectedCatalogueActionProperty = new CatalogueHookInstanceProperty();
        selectedCatalogueAction = new CatalogueHookInstance();
        selectedCatalogueAction.setCatalogue(selectedMediaRepository);
        selectedCatalogueAction.setExecuteOrder(1);
        // A default action class is required to avoid NullPointerException from JSF
        Map<String, Plugin> plugins = systemFacade.getPlugins();

        for (Plugin plugin : plugins.values()) {
            if (plugin instanceof CatalogueHook) {
                CatalogueHook action = (CatalogueHook) plugin;
                selectedCatalogueAction.setHookClass(action.getClass().getName());
            }
        }
    }

    public void onDeleteCatalogueAction(ActionEvent event) {
        selectedMediaRepository.getHooks().remove(selectedCatalogueAction);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                Bundle.i18n.name(),
                "administrator_Catalogues_CATALOGUE_ACTION_DELETED");
    }

    public void onSaveCatalogueAction(ActionEvent event) {
        if (isActionAddMode()) {
            selectedCatalogueAction.setCatalogue(selectedMediaRepository);
            selectedCatalogueAction = catalogueFacade.createCatalogueAction(
                    selectedCatalogueAction);
            selectedMediaRepository.getHooks().add(selectedCatalogueAction);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Catalogues_CATALOGUE_ACTION_CREATED");
        } else {
            selectedCatalogueAction = catalogueFacade.updateCatalogueAction(
                    selectedCatalogueAction);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Catalogues_CATALOGUE_ACTION_UPDATED");
        }
    }

    public void onAddActionProperty(ActionEvent event) {
        selectedCatalogueActionProperty.setCatalogueHook(selectedCatalogueAction);
        selectedCatalogueAction.getProperties().add(
                selectedCatalogueActionProperty);
        selectedCatalogueActionProperty = new CatalogueHookInstanceProperty();

        // Refresh list of properties
        this.catalogueActionProperties = null;
    }
}
