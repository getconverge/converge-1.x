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
package dk.i2m.converge.jsf.converters;

import dk.i2m.converge.core.content.Language;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.core.DataNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Converter for {@link Language}s.
 *
 * @author Allan Lykke Christensen
 */
public class LanguageConverter implements Converter {

    private static final Logger logger = Logger.getLogger(LanguageConverter.class.getName());

    private SystemFacadeLocal systemFacade;

    /**
     * Creates a new instance of {@link LanguageConverer}.
     *
     * @param systemFacade
     *          System facade used by the converter
     */
    public LanguageConverter(SystemFacadeLocal systemFacade) {
        this.systemFacade = systemFacade;
    }

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        try {
            return systemFacade.findLanguageById(Long.valueOf(value));
        } catch (DataNotFoundException ex) {
            logger.log(Level.WARNING, "No matching Language {0}", value);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent component, Object value) {
        Language language = (Language) value;
        if (language == null) {
            return "";
        } else {
            return String.valueOf(language.getId());
        }
    }
}
