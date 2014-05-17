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

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import dk.i2m.jsf.JsfUtils;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Managed bean for displaying user roles.
 *
 * @author Allan Lykke Christensen
 */
public class Roles {

    @EJB private UserFacadeLocal userFacade;

    private DataModel roles = null;

    private UserRole selected;

    private String selectedTab = "";

    private boolean update = false;

    /**
     * Creates a new instance of {@link Roles}.
     */
    public Roles() {
    }

    /**
     * Gets the {@link DataModel} of {@link UserRole}s.
     *
     * @return {@link DataModel} of {@link UserRole}s.
     */
    public DataModel getRoles() {
        if (this.roles == null) {
            this.roles = new ListDataModel(userFacade.getUserRoles());
        }
        return this.roles;
    }

    /**
     * Gets the selected {@link UserRole}.
     *
     * @return Selected {@link UserRole}
     */
    public UserRole getSelected() {
        return selected;
    }

    /**
     * Sets the selected {@link UserRole}.
     *
     * @param selected
     *          Selected {@link UserRole}
     */
    public void setSelected(UserRole selected) {
        this.selected = selected;
    }

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void onNew(ActionEvent event) {
        this.update = false;
        this.selected = new UserRole();
        setSelectedTab("tabDetails");
    }

    public void onModify(ActionEvent event) {
        this.update = true;
        String id = JsfUtils.getRequestParameterMap().get("id");
        try {
            this.selected = userFacade.findUserRoleById(Long.valueOf(id));
            setSelectedTab("tabDetails");
        } catch (DataNotFoundException ex) {
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    public void onSave(ActionEvent event) {
        if (selected.getId() != null) {
            userFacade.update(selected);
        } else {
            userFacade.create(selected);
        }
        this.roles = null;
    }

    public void onDelete(ActionEvent event) {
        userFacade.delete(selected);
        this.roles = null;
    }
}
