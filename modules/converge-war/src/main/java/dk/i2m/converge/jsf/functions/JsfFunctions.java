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
package dk.i2m.converge.jsf.functions;

import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * JSF EL utility functions.
 *
 * @author Allan Lykke Christensen
 */
public class JsfFunctions {

    public static boolean isValid(String componentId) {
        return !isNotValid(componentId);
    }

    public static boolean isNotValid(String componentId) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        UIComponent component = ctx.getViewRoot().findComponent(componentId);

        if (component == null) {
            return false;
        }

        Iterator<FacesMessage> msgs = ctx.getMessages(componentId);

        while (msgs.hasNext()) {
            FacesMessage msg = msgs.next();
            if (msg.getSeverity().equals(FacesMessage.SEVERITY_WARN) || msg.getSeverity().equals(FacesMessage.SEVERITY_ERROR) || msg.getSeverity().equals(FacesMessage.SEVERITY_FATAL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValid() {
        return !isNotValid();
    }

    public static boolean isNotValid() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        Iterator<FacesMessage> msgs = ctx.getMessages();

        while (msgs.hasNext()) {
            FacesMessage msg = msgs.next();
            if (msg.getSeverity().equals(FacesMessage.SEVERITY_WARN) || msg.getSeverity().equals(FacesMessage.SEVERITY_ERROR) || msg.getSeverity().equals(FacesMessage.SEVERITY_FATAL)) {
                return true;
            }
        }
        return false;
    }
}
