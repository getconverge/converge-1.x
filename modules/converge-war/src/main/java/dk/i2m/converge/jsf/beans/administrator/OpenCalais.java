/*
 * Copyright (C) 2011 Interactive Media Management
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

import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.metadata.GeoArea;
import dk.i2m.converge.core.metadata.OpenCalaisMapping;
import dk.i2m.converge.core.metadata.Organisation;
import dk.i2m.converge.core.metadata.Person;
import dk.i2m.converge.core.metadata.PointOfInterest;
import dk.i2m.converge.core.metadata.Subject;
import dk.i2m.converge.ejb.facades.MetaDataFacadeLocal;
import dk.i2m.converge.ejb.services.ConfigurationServiceLocal;
import dk.i2m.jsf.JsfUtils;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 *
 * @author Allan Lykke Christensen
 */
public class OpenCalais {

    private String apiKey = "";

    @EJB private ConfigurationServiceLocal cfgService;

    @EJB private MetaDataFacadeLocal metaDataFacade;

    private DataModel mappings = null;

    private OpenCalaisMapping selectedMapping;

    private Long conceptId = null;

    public OpenCalais() {
    }

    @PostConstruct
    public void onInit() {
        setApiKey(cfgService.getString(ConfigurationKey.OPEN_CALAIS_API_KEY));
    }

    public void onSaveApiKey(ActionEvent event) {
        cfgService.set(ConfigurationKey.OPEN_CALAIS_API_KEY, getApiKey());
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, "i18n", "administrator_OpenCalais_API_KEY_SAVED", null);
    }

    public void onNewMapping(ActionEvent event) {
        selectedMapping = new OpenCalaisMapping();
    }

    public void onSaveMapping(ActionEvent event) {
        selectedMapping = metaDataFacade.create(selectedMapping);
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, "i18n", "administrator_OpenCalais_MAPPING_SAVED", null);
        this.mappings = null;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public DataModel getMappings() {
        if (mappings == null) {
            mappings = new ListDataModel(metaDataFacade.getOpenCalaisMappings());
        }
        return this.mappings;
    }

    public DataModel getSubjects() {
        return new ListDataModel(metaDataFacade.findConceptByType(Subject.class));
    }

    public DataModel getOrganisations() {
        return new ListDataModel(metaDataFacade.findConceptByType(Organisation.class));
    }

    public DataModel getLocations() {
        return new ListDataModel(metaDataFacade.findConceptByType(GeoArea.class));
    }

    public DataModel getPointsOfInterest() {
        return new ListDataModel(metaDataFacade.findConceptByType(PointOfInterest.class));
    }

    public DataModel getPersons() {
        return new ListDataModel(metaDataFacade.findConceptByType(Person.class));
    }

    public OpenCalaisMapping getSelectedMapping() {
        return selectedMapping;
    }

    public void setSelectedMapping(OpenCalaisMapping selectedMapping) {
        this.selectedMapping = selectedMapping;
    }

    public void setDeleteMapping(Long id) {
        metaDataFacade.deleteOpenCalaisMapping(id);
        this.mappings = null;
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, "i18n", "administrator_OpenCalais_MAPPING_REMOVED", null);
    }

    public Long getConceptId() {
        return conceptId;
    }

    public void setConceptId(Long conceptId) {
        this.conceptId = conceptId;
    }
}
