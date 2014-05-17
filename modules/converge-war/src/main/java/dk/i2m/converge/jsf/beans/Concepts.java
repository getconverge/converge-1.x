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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.metadata.*;
import dk.i2m.converge.ejb.facades.MetaDataFacadeLocal;
import dk.i2m.jsf.JsfUtils;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.LocaleUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

/**
 * Backing bean for the {@code Concepts}.
 *
 * @author Allan Lykke Christensen
 */
public class Concepts {

    private static final Logger log = Logger.getLogger(Concepts.class.getName());

    @EJB private MetaDataFacadeLocal metaDataFacade;

    private DataModel mostPopular = null;

    private DataModel mostRecent = null;

    private String search = "";

    private String show = "";

    private String importLanguage;

    private String importFormat = "NEWSML_G2_KNOWLEDGE_ITEM";

    private Long id;

    private List<UploadItem> uploadedConcepts = new ArrayList<UploadItem>();

    private Map<String, String> availableLanguages =
            new LinkedHashMap<String, String>();

    private Concept selectedConcept;

    private DataModel searchResult;

    private String newName = "";

    private String newType = "";

    private boolean updatingConcept = false;

    private Concept selectedMetaDataConcept;

    private String conceptAddType = "";
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;

        if (id != null) {
            try {
                Logger.getLogger(Concepts.class.getName()).log(Level.INFO,
                        "Loading concept {0}", id);
                selectedConcept = metaDataFacade.findConceptById(id);
                show = "SHOW_CONCEPT";
            } catch (DataNotFoundException ex) {
                Logger.getLogger(Concepts.class.getName()).log(Level.SEVERE,
                        "Unknown concept identifier", ex);
            }
        }
    }

    public void onSearch(ActionEvent event) {
        List<Concept> found = metaDataFacade.search(search);
        searchResult = new ListDataModel(found);
        show = "SEARCH_RESULTS";
        updatingConcept = false;
    }

    public void onShowConcept(ActionEvent event) {
        show = "SHOW_CONCEPT";
        updatingConcept = false;
    }

    public void onEditConcept(ActionEvent event) {
        updatingConcept = true;
    }

    public void onSaveConcept(ActionEvent event) {
        selectedConcept = metaDataFacade.update(selectedConcept);
        updatingConcept = false;
        mostRecent = null;
    }

    public void onSaveNewConcept(ActionEvent event) {
        show = "SHOW_CONCEPT";
        Class c;
        try {
            c = Class.forName(newType);
            selectedConcept = (Concept) c.newInstance();
            selectedConcept.setName(newName);
            selectedConcept = metaDataFacade.create(selectedConcept);
            updatingConcept = true;
            mostRecent = null;
        } catch (InstantiationException ex) {
            Logger.getLogger(Concepts.class.getName()).log(Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Concepts.class.getName()).log(Level.SEVERE, null,
                    ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Concepts.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    public void onShowSubjects(ActionEvent event) {
        show = "SEARCH_RESULTS";
        List<Concept> found = metaDataFacade.findConceptByType(Subject.class);
        searchResult = new ListDataModel(found);
    }

    public void onShowOverview(ActionEvent event) {
        show = "OVERVIEW";
    }

    public void onShowGeoAreas(ActionEvent event) {
        show = "SEARCH_RESULTS";
        List<Concept> found = metaDataFacade.findConceptByType(GeoArea.class);
        searchResult = new ListDataModel(found);
    }

    public void onShowPersons(ActionEvent event) {
        show = "SEARCH_RESULTS";
        List<Concept> found = metaDataFacade.findConceptByType(Person.class);
        searchResult = new ListDataModel(found);
    }

    public void onShowPoi(ActionEvent event) {
        show = "SEARCH_RESULTS";
        List<Concept> found = metaDataFacade.findConceptByType(
                PointOfInterest.class);
        searchResult = new ListDataModel(found);
    }

    public void onShowOrganisations(ActionEvent event) {
        show = "SEARCH_RESULTS";
        List<Concept> found = metaDataFacade.findConceptByType(
                Organisation.class);
        searchResult = new ListDataModel(found);
    }

    public void onReadAvailableLanguages(ActionEvent event) throws IOException {
        this.availableLanguages.clear();

        for (UploadItem item : this.uploadedConcepts) {
            byte[] fileData;
            if (item.isTempFile()) {
                fileData = FileUtils.readFileToByteArray(item.getFile());
            } else {
                fileData = item.getData();
            }

            String xml = new String(fileData);

            String languages[] = metaDataFacade.getLanguagesAvailableForImport(
                    xml);

            for (String lang : languages) {
                lang = lang.replaceAll("-", "_");
                Locale locale = LocaleUtils.toLocale(lang);
                this.availableLanguages.put(locale.getDisplayLanguage(), lang);
            }
        }
    }

    /**
     * Event handler for uploading and importing concepts.
     *
     * @param event Event that invoked the handler
     */
    public void onUploadConcepts(UploadEvent event) throws IOException {
        UploadItem item = event.getUploadItem();
        this.uploadedConcepts.add(item);
    }

    /**
     * Event handler for preparing the import of concepts.
     *
     * @param event Event that invoked the handler
     */
    public void onPreImport(ActionEvent event) {
        this.uploadedConcepts = new ArrayList<UploadItem>();
        this.availableLanguages = new LinkedHashMap<String, String>();
    }
    
    /**
     * Event handler for exporting subjects in a Microsoft Excel spreadsheet.
     * 
     * @param event Event that invoked the handler
     */
    public void onExportSubjects(ActionEvent event) {
        byte[] output = metaDataFacade.exportConcepts(Subject.class, ConceptOutput.MICROSOFT_EXCEL);
        
         try {
            // here you need to get the byte[] representation of 
            // the file you want to send
            byte[] binary_data = output;
            String filename = "subjects.xls";
            FacesContext fctx = FacesContext.getCurrentInstance();   
            ExternalContext ectx = fctx.getExternalContext();
 
            
            
            HttpServletResponse response = (HttpServletResponse) ectx.getResponse();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Content-Transfer-Encoding", "Binary");
            response.setHeader("Pragma", "private");
            response.setHeader("cache-control", "private, must-revalidate");
            response.setContentType("application/vnd.ms-excel");
 
            ServletOutputStream outs = response.getOutputStream();
            outs.write(binary_data);
            outs.flush();
            outs.close();
            response.flushBuffer();
 
            fctx.responseComplete();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Event handler for importing the uploaded subject files.
     *
     * @param event Event that invoked the handler
     * @throws IOException If any of the uploaded files could not be read
     */
    public void onImport(ActionEvent event) throws IOException {
        int imported = 0;
        for (UploadItem item : this.uploadedConcepts) {

            byte[] fileData;
            if (item.isTempFile()) {
                fileData = FileUtils.readFileToByteArray(item.getFile());
            } else {
                fileData = item.getData();
            }

            String xml = new String(fileData);

            if (getImportLanguage() != null) {
                imported += metaDataFacade.importKnowledgeItem(xml,
                        getImportLanguage());
            }
        }

        JsfUtils.createMessage("frmSubjects", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                "Concepts_X_SUBJECTS_IMPORTED", new Object[]{imported});
    }

    public void onDeleteConcept(ActionEvent event) {
        if (getSelectedConcept() != null) {
            metaDataFacade.delete(getSelectedConcept().getClass(),
                    getSelectedConcept().getId());
        }
        updatingConcept = false;
        show = "OVERVIEW";
        mostRecent = null;
        JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                "Concepts_CONCEPT_DELETED");
    }

    public void onNewConcept(ActionEvent event) {
        newName = "";
        newType = "";
    }

    public boolean isUpdatingConcept() {
        return updatingConcept;
    }

    public void setUpdatingConcept(boolean updatingConcept) {
        this.updatingConcept = updatingConcept;
    }

    public DataModel getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(DataModel searchResult) {
        this.searchResult = searchResult;
    }

    public Concept getSelectedConcept() {
        return selectedConcept;
    }

    public void setSelectedConcept(Concept selectedConcept) {
        this.selectedConcept = selectedConcept;
    }

    public DataModel getMostPopular() {
        if (mostPopular == null) {
            mostPopular = new ListDataModel(new ArrayList());
        }
        return mostPopular;
    }

    public DataModel getMostRecent() {
        if (mostRecent == null) {
            mostRecent =
                    new ListDataModel(metaDataFacade.findRecentConcepts(10));
        }
        return mostRecent;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public boolean isShowConcept() {
        return show.equalsIgnoreCase("SHOW_CONCEPT");
    }

    public boolean isShowSearchResults() {
        return show.equalsIgnoreCase("SEARCH_RESULTS");
    }

    public boolean isShowOverview() {
        return show.equalsIgnoreCase("OVERVIEW");
    }

    public Map<String, String> getAvailableLanguages() {
        return availableLanguages;
    }

    public void setAvailableLanguages(Map<String, String> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }

    public String getImportFormat() {
        return importFormat;
    }

    public void setImportFormat(String importFormat) {
        this.importFormat = importFormat;
    }

    public String getImportLanguage() {
        return importLanguage;
    }

    public void setImportLanguage(String importLanguage) {
        this.importLanguage = importLanguage;
    }

    public List<UploadItem> getUploadedConcepts() {
        return uploadedConcepts;
    }

    public void setUploadedConcepts(List<UploadItem> uploadedFiles) {
        this.uploadedConcepts = uploadedFiles;
    }

    /**
     * Gets the available languages for importing based on the already uploaded
     * files.
     *
     * @return {@link Map} of {@link Locale}s with the available languages for
     * importing subjects codes
     * @throws IOException
* If any of the uploaded files could not be read
     */
    public Map<String, String> getAvailableImportLanguages() {
        return this.availableLanguages;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getNewType() {
        return newType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }

    public DataModel getMetaDataSubjects() {
        return new ListDataModel(metaDataFacade.findConceptByType(Subject.class));
    }

    public DataModel getMetaDataOrganisations() {
        return new ListDataModel(metaDataFacade.findConceptByType(
                Organisation.class));
    }

    public DataModel getMetaDataLocations() {
        return new ListDataModel(metaDataFacade.findConceptByType(GeoArea.class));
    }

    public DataModel getMetaDataPointsOfInterest() {
        return new ListDataModel(metaDataFacade.findConceptByType(
                PointOfInterest.class));
    }

    public DataModel getMetaDataPersons() {
        return new ListDataModel(metaDataFacade.findConceptByType(Person.class));
    }

    public void onSelectMetaData(ActionEvent event) {

        if (conceptAddType.equalsIgnoreCase("RELATED")) {
            selectedConcept.getRelated().add(selectedMetaDataConcept);
            selectedConcept = metaDataFacade.update(selectedConcept);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Concepts_META_DATA_X_SELECTED_RELATED",
                    new Object[]{getSelectedMetaDataConcept().getName(),
                        getSelectedConcept().getName()});
        } else if (conceptAddType.equalsIgnoreCase("SAME_AS")) {
            selectedConcept.getSameAs().add(selectedMetaDataConcept);
            selectedConcept = metaDataFacade.update(selectedConcept);

            try {
                selectedMetaDataConcept =
                        metaDataFacade.findConceptById(selectedMetaDataConcept.
                        getId());
                selectedMetaDataConcept.getSameAs().add(selectedConcept);
                selectedMetaDataConcept = metaDataFacade.update(
                        selectedMetaDataConcept);
                selectedConcept =
                        metaDataFacade.findConceptById(selectedConcept.getId());
            } catch (Exception ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.getMessage()});
            }

            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Concepts_META_DATA_X_SELECTED_SAME_AS",
                    new Object[]{getSelectedMetaDataConcept().getName(),
                        getSelectedConcept().getName()});
        } else if (conceptAddType.equalsIgnoreCase("BROADER")) {
            selectedConcept.getBroader().add(selectedMetaDataConcept);
            selectedConcept = metaDataFacade.update(selectedConcept);

            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Concepts_META_DATA_X_SELECTED_BROADER",
                    new Object[]{getSelectedMetaDataConcept().getName(),
                        getSelectedConcept().getName()});
        } else if (conceptAddType.equalsIgnoreCase("NARROWER")) {
            selectedConcept.getNarrower().add(selectedMetaDataConcept);
            selectedConcept = metaDataFacade.update(selectedConcept);

            try {
                selectedMetaDataConcept =
                        metaDataFacade.findConceptById(selectedMetaDataConcept.
                        getId());
                selectedMetaDataConcept.getBroader().add(selectedConcept);
                selectedMetaDataConcept = metaDataFacade.update(
                        selectedMetaDataConcept);
                selectedConcept =
                        metaDataFacade.findConceptById(selectedConcept.getId());
            } catch (Exception ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.getMessage()});
            }

            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Concepts_META_DATA_X_SELECTED_NARROWER",
                    new Object[]{getSelectedMetaDataConcept().getName(),
                        getSelectedConcept().getName()});
        } else {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "Concepts_META_DATA_SELECTED_ERROR");
        }

    }

    public void onSelectMetaDataRelated(ActionEvent event) {
        selectedMetaDataConcept = null;
        conceptAddType = "RELATED";
    }

    public void onSelectMetaDataSameAs(ActionEvent event) {
        selectedMetaDataConcept = null;
        conceptAddType = "SAME_AS";
    }

    public void onSelectMetaDataBroader(ActionEvent event) {
        selectedMetaDataConcept = null;
        conceptAddType = "BROADER";
    }

    public void onSelectMetaDataNarrower(ActionEvent event) {
        selectedMetaDataConcept = null;
        conceptAddType = "NARROWER";
    }

    public Concept getSelectedMetaDataConcept() {
        return selectedMetaDataConcept;
    }

    public void setSelectedMetaDataConcept(Concept selectedMetaDataConcept) {
        this.selectedMetaDataConcept = selectedMetaDataConcept;
    }

    public void onRemoveNarrowerFromConcept(ActionEvent event) {
        selectedMetaDataConcept.getBroader().remove(selectedConcept);
        selectedMetaDataConcept = metaDataFacade.update(selectedMetaDataConcept);

        try {
            selectedConcept = metaDataFacade.findConceptById(selectedConcept.
                    getId());
        } catch (DataNotFoundException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.getMessage()});
        }
    }

    public void onRemoveBroaderFromConcept(ActionEvent event) {
        try {
            selectedConcept = metaDataFacade.findConceptById(selectedConcept.
                    getId());
            selectedConcept.getBroader().remove(selectedMetaDataConcept);
            selectedConcept = metaDataFacade.update(selectedConcept);
        } catch (DataNotFoundException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.getMessage()});
        }
    }

    public void onRemoveRelatedFromConcept(ActionEvent event) {
        try {
            selectedConcept = metaDataFacade.findConceptById(selectedConcept.
                    getId());
            selectedConcept.getRelated().remove(selectedMetaDataConcept);
            selectedConcept = metaDataFacade.update(selectedConcept);
        } catch (DataNotFoundException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.getMessage()});
        }
    }

    public void onRemoveSameAsFromConcept(ActionEvent event) {
        try {
            selectedMetaDataConcept.getSameAs().remove(selectedConcept);
            selectedMetaDataConcept = metaDataFacade.update(
                    selectedMetaDataConcept);

            selectedConcept = metaDataFacade.findConceptById(selectedConcept.
                    getId());
            selectedConcept.getSameAs().remove(selectedMetaDataConcept);
            selectedConcept = metaDataFacade.update(selectedConcept);
        } catch (DataNotFoundException ex) {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                        "Generic_AN_ERROR_OCCURRED_X", new Object[]{ex.getMessage()});
        }
    }

    public void onCancelConcept(ActionEvent event) {
        updatingConcept = false;
    }
}
