/*
 * Copyright (C) 2010 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.functions;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.StringUtils;

/**
 * JSF EL functions for working with {@link String}s
 *
 * @author Allan Lykke Christensen
 */
public class StringFunctions {

    /**
     * Abbreviates a given {@link String}.
     *
     * @param string
     *          {@link String} to abbreviate
     * @param maxWidth Maximum length of the {@link String}
     * @return Abbreviated {@link String}
     * @see org.apache.commons.lang.StringUtils#abbreviate(java.lang.String, int)
     */
    public static String abbreviate(String string, int maxWidth) {
        return StringUtils.abbreviate(string, maxWidth);
    }

    /**
     * Returns a message from a {@link ResourceBundle} based on a given key. If the key could not be found in the bundle an empty {@link String} is returned.
     *
     * @param bundle
     *          {@link ResourceBundle} containing the message
     * @param key
     *          Key of the message to extract
     * @return {@link String} containing the message
     */
    public static String message(ResourceBundle bundle, String key) {
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        } else {
            return "";
        }
    }

    /**
     * Returns a message from a {@link ResourceBundle} based on a given key. If the key could not be found in the bundle an empty {@link String} is returned.
     *
     * @param bundleId
     *          ID of the bundle in FacesConfig.xml
     * @param key
     *          Key of the message to extract
     * @param param
     *          Parameters to merge into the message
     * @return {@link String} containing the message
     */
    public static String message(String bundleId, String key, Object param1, Object param2, Object param3, Object param4, Object param5) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Application application = ctx.getApplication();
        String msg = key;
        try {
            ResourceBundle bundle = application.getResourceBundle(ctx, bundleId);
            String msgPattern = bundle.getString(key);
            msg = msgPattern;

            Object[] params = new Object[]{param1, param2, param3, param4, param5};
            msg = MessageFormat.format(msg, params);
            return msg;
        } catch (Exception ex) {
            return "";
        }
    }
}
