/*
 * WorkflowStateConverter.java
 * 
 * Copyright (C) 2009 Interactive Media Management
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
package dk.i2m.converge.jsf.converters;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.workflow.WorkflowState;
import dk.i2m.converge.ejb.facades.WorkflowFacadeLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * JSF {@link Converter} for {@link WorkflowState} objects.
 *
 * @author Allan Lykke Christensen
 */
public class WorkflowStateConverter implements Converter {

    /** Application logger. */
    private static Logger logger = Logger.getLogger(
            WorkflowStateConverter.class.getName());

    /** Facade used for looking up domain objects. */
    private WorkflowFacadeLocal workflowFacade;

    /**
     * Creates a new instance of {@link WorkflowStateConverter}.
     *
     * @param workflowFacade
     *          Workflow facade to use for looking up domain objects
     */
    public WorkflowStateConverter(WorkflowFacadeLocal workflowFacade) {
        this.workflowFacade = workflowFacade;
    }

    public Object getAsObject(FacesContext ctx, UIComponent component,
            String value) {
        try {
            return workflowFacade.findWorkflowStateById(Long.valueOf(value));
        } catch (DataNotFoundException ex) {
            logger.log(Level.WARNING, "No workflow state matching [" + value +
                    "]", ex);
            return null;
        }
    }

    public String getAsString(FacesContext ctx, UIComponent component,
            Object value) {
        WorkflowState state = (WorkflowState) value;
        if (state == null) {
            return "";
        } else {
            return String.valueOf(state.getId());
        }
    }
}
