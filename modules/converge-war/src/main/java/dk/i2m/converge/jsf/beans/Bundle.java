/*
 * Copyright (C) 2012 Interactive Media Management
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

/**
 * Identifier of {@link java.util.ResourceBundle}s available in
 * {@code FacesContext.xml}.
 *
 * @author Allan Lykke Christensen
 */
public enum Bundle {

    /**
     * Old ResourceBundle containing messages.
     *
     * @deprecated Use {@link Bundle#i18n}
     */
    msgs,
    /**
     * Old ResourceBundle containing resources.
     *
     * @deprecated Use {@link Bundle#i18n}
     */
    res,
    /**
     * ResourceBundle containing messages and resources for the web application.
     */
    i18n;
}
