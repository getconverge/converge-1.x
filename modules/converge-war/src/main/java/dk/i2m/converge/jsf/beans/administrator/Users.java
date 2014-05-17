/*
 * Copyright (C) 2010 Interactive Media Management
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
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import dk.i2m.converge.jsf.beans.BaseBean;
import dk.i2m.jsf.JsfUtils;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing-bean for <code>/administrator/Users.jspx</code>.
 *
 * @author Allan Lykke Christensen
 */
public class Users extends BaseBean {

    @EJB private UserFacadeLocal userFacade;

    private DataModel users = null;

    private UserAccount displayUser;


    /**
     * Creates a new instance of {@link Users}.
     */
    public Users() {
    }

    /**
     * Event handler for displaying a specific user.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onDisplayUser(ActionEvent event) {
        String uid = JsfUtils.getRequestParameterMap().get("uid");
        try {
            this.displayUser = userFacade.findById(uid);
        } catch (DataNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the {@link DataModel} of {@link UserAccount}s.
     *
     * @return {@link DataModel} of {@link UserAccount}s.
     */
    public DataModel getUsers() {
        if (users == null) {
            users = new ListDataModel(userFacade.getUsers());
        }
        return users;
    }

    /**
     * Gets the {@link UserAccount} to display.
     *
     * @return {@link UserAccount} to display
     */
    public UserAccount getDisplayUser() {
        return displayUser;
    }

    public void onSynchroniseWithDirectory(ActionEvent event) {
        userFacade.synchroniseWithDirectory();
        this.users = null;
    }
}
