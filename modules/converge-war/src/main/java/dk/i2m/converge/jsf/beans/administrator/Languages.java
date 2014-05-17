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
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.converge.core.content.Language;
import dk.i2m.converge.ejb.facades.ReferentialIntegrityException;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.jsf.JsfUtils;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/Languages.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Languages {

    @EJB private SystemFacadeLocal systemFacade;

    private DataModel languages = null;

    private Language selectedLanguage = null;

    public Languages() {
    }

    public void onNew(ActionEvent event) {
        selectedLanguage = new Language();
    }

    public void onSave(ActionEvent event) {
        if (isEditMode()) {
            selectedLanguage = systemFacade.updateLanguage(selectedLanguage);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, "language_CREATED", selectedLanguage.getName());
        } else {
            selectedLanguage = systemFacade.createLanguage(selectedLanguage);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, "language_UPDATED", selectedLanguage.getName());
        }
        languages = null;
    }

    public void onDelete(ActionEvent event) {
        if (isEditMode()) {
            try {
                systemFacade.deleteLanguage(selectedLanguage.getId());
            } catch (ReferentialIntegrityException ex) {
                JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, "language_LANGUAGE_IN_USE", selectedLanguage.getName());
            }
        }
        languages = null;
    }

    public DataModel getLanguages() {
        if (languages == null) {
            this.languages = new ListDataModel(systemFacade.getLanguages());
        }
        return languages;
    }

    public void setLanguages(DataModel languages) {
        this.languages = languages;
    }

    public Language getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(Language selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public boolean isEditMode() {
        if (selectedLanguage == null || selectedLanguage.getId() == null) {
            return false;
        } else {
            return true;
        }
    }
}
