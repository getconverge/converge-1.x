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
package dk.i2m.converge.jsf.converters;

import dk.i2m.converge.core.workflow.Section;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.ejb.facades.OutletFacadeLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Allan Lykke Christensen
 */
public class SectionConverter implements Converter {

    private static final Logger log = Logger.getLogger(SectionConverter.class.getName());

    private OutletFacadeLocal outletFacade;

    public SectionConverter(OutletFacadeLocal outletFacade) {
        this.outletFacade = outletFacade;
    }

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        try {
            return outletFacade.findSectionById(Long.valueOf(value));
        } catch (DataNotFoundException ex) {
            log.log(Level.WARNING, "No matching section", ex);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent component, Object value) {
        Section section = (Section) value;
        if (section == null) {
            return "";
        } else {
            return String.valueOf(section.getId());
        }
    }
}
