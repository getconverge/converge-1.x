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
package dk.i2m.converge.jsf.converters;

import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import dk.i2m.converge.core.DataNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * JSF converter for {@link Rendition} objects.
 *
 * @author Allan Lykke Christensen
 */
public class RenditionConverter implements Converter {

    private static final Logger LOG = Logger.getLogger(RenditionConverter.class.getName());

    private CatalogueFacadeLocal catalogueFacade;

    /**
     * Creates a new instance of {@link RenditionConverter}.
     * 
     * @param catalogueFacade 
     *          Facade used for looking up {@link Rendition}s
     */
    public RenditionConverter(CatalogueFacadeLocal catalogueFacade) {
        this.catalogueFacade = catalogueFacade;
    }

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        try {
            if (value == null) {
                return null;
            }
            return catalogueFacade.findRenditionById(Long.valueOf(value));

        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "No Rendition matching [{0}]", value);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent component, Object value) {
        if (value == null) {
            return "";
        } else {
            Rendition label = (Rendition) value;
            return String.valueOf(label.getId());
        }
    }
}
