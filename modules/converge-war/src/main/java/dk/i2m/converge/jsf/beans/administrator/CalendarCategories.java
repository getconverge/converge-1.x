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

import dk.i2m.converge.core.calendar.EventCategory;
import dk.i2m.converge.ejb.facades.CalendarFacadeLocal;
import dk.i2m.converge.jsf.beans.Bundle;
import dk.i2m.jsf.JsfUtils;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/CalendarCategories.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class CalendarCategories {

    @EJB private CalendarFacadeLocal calendarFacade;

    private DataModel categories = null;

    private EventCategory selectedCategory;

    public void onSave(ActionEvent event) {
        if (event.getComponent().getId().equalsIgnoreCase("lnkSaveEventCategoryDetails")) {
            selectedCategory = calendarFacade.create(selectedCategory);
            JsfUtils.createMessage("frmEventCategories", 
                    FacesMessage.SEVERITY_INFO, Bundle.i18n.name(), 
                    "administrator_CalendarCategories_EVENT_CATEGORY_CREATED");
        } else {
            selectedCategory = calendarFacade.update(selectedCategory);
            JsfUtils.createMessage("frmEventCategories", 
                    FacesMessage.SEVERITY_INFO, Bundle.i18n.name(), 
                    "administrator_CalendarCategories_EVENT_CATEGORY_UPDATED");
        }
        categories = null;
    }

    public void onNew(ActionEvent event) {
        selectedCategory = new EventCategory();
    }

    public void onDelete(ActionEvent event) {
        if (selectedCategory != null) {
            calendarFacade.deleteEventCategory(selectedCategory.getId());
        }
        categories = null;
    }

    public DataModel getCategories() {
        if (categories == null) {
            categories = new ListDataModel(calendarFacade.findAllCategories());
        }
        return categories;
    }

    public EventCategory getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(EventCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public boolean isEditMode() {
        if (selectedCategory == null || selectedCategory.getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isAddMode() {
        return !isEditMode();
    }

}
