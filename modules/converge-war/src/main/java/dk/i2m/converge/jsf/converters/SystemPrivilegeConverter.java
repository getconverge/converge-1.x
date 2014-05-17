/*
 *  Copyright (C) 2010 Allan Lykke Christensen
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
package dk.i2m.converge.jsf.converters;

import dk.i2m.converge.core.security.Privilege;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import dk.i2m.converge.core.DataNotFoundException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Converter for turning {@link SystemPrivilege}s into {@link Privilege}s.
 *
 * @author Allan Lykke Christensen
 */
public class SystemPrivilegeConverter implements Converter {

    private UserFacadeLocal userFacade;

    public SystemPrivilegeConverter(UserFacadeLocal userFacade) {
        this.userFacade = userFacade;
    }

    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        try {
            return userFacade.findPrivilegeById(string);
        } catch (DataNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof Privilege) {
            return ((Privilege) o).getId().name();
        } else {
            System.out.println("Not instance of Privilege: " + o.getClass().
                    getName());
            return "";
        }
    }
}
