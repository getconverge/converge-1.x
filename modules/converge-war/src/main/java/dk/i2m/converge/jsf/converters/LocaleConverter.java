/*
 * LocaleConverter.java
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

import java.util.Locale;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.apache.commons.lang.LocaleUtils;

/**
 * JSF {@link Converter} for {@link Locale} objects.
 *
 * @author Allan Lykke Christensen
 */
public class LocaleConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent comp, String value)
            throws ConverterException {
        String localeValue = value.replaceAll("-", "_");

        Locale locale = LocaleUtils.toLocale(localeValue);
        return locale;
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Object obj)
            throws ConverterException {
        Locale locale = (Locale) obj;
        return locale.toString();
    }
}
