/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
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

import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.jsf.JsfUtils;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/Renditions.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Renditions {

    @EJB private CatalogueFacadeLocal mediaDatabaseFacade;

    private Rendition selectedLabel = null;

    private DataModel labels = null;

    public void onNewMediaItemVersionLabel(ActionEvent event) {
        this.selectedLabel = new Rendition();
    }

    public void onSaveMediaItemVersionLabel(ActionEvent event) {
        this.labels = null;
        if (isEditMode()) {
            selectedLabel = mediaDatabaseFacade.update(selectedLabel);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Renditions_RENDITION_UPDATED");
        } else {
            selectedLabel = mediaDatabaseFacade.create(selectedLabel);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Renditions_RENDITION_CREATED");
        }
    }

    public void onDeleteMediaItemVersionLabel(ActionEvent event) {
        if (isEditMode()) {
            mediaDatabaseFacade.deleteRendition(selectedLabel.getId());
            this.labels = null;
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO,
                    Bundle.i18n.name(),
                    "administrator_Renditions_RENDITION_DELETED");
        }
    }

    public boolean isEditMode() {
        if (selectedLabel == null || selectedLabel.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    public Rendition getSelectedLabel() {
        return selectedLabel;
    }

    public void setSelectedLabel(Rendition selectedLabel) {
        this.selectedLabel = selectedLabel;
    }

    public DataModel getLabels() {
        if (this.labels == null) {
            this.labels =
                    new ListDataModel(mediaDatabaseFacade.findRenditions());
        }
        return this.labels;
    }
}
