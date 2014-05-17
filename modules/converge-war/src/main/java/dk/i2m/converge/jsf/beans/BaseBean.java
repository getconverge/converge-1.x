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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import dk.i2m.jsf.JsfUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

/**
 * Base bean providing useful methods for JSF managed beans.
 *
 * @author Allan Lykke Christensen
 */
public abstract class BaseBean {

    protected static final Logger logger = Logger.getLogger(BaseBean.class.getName());

    @EJB private UserFacadeLocal userFacade;

    private UserAccount currentUserAccount;

    /**
     * Gets the current user, or {@code null} if the user is not logged in.
     *
     * @return Current user logged in.
     */
    public UserAccount getUser() {
        if (currentUserAccount == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            String uid = ctx.getExternalContext().getRemoteUser();

            // Used by the session overview to see the name of the users logged in
            JsfUtils.getHttpSession().setAttribute("uid", uid);

            if (uid != null || !uid.isEmpty()) {
                try {
                    currentUserAccount = userFacade.findById(uid);
                } catch (DataNotFoundException ex) {
                    logger.log(Level.INFO, "User [{0}] could not be found", uid);
                }
            }

        }
        return currentUserAccount;
    }

    /**
     * Determines if the current user is in a given role.
     *
     * @param role
     *          Name of the role
     * @return <code>true</code> if the user is in the given role, otherwise
     *         <code>false</code>
     */
    public boolean isUserInRole(String role) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        return ctx.getExternalContext().isUserInRole(role);
    }
}
