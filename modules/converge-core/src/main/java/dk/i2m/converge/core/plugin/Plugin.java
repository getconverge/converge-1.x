/*
 * Copyright (C) 2010 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.plugin;

import java.util.Date;
import java.util.ResourceBundle;

/**
 * Interface for implementing a Converge {@link Plugin}.
 *
 * @author Allan Lykke Christensen
 */
public interface Plugin {

    /**
     * Gets a human readable name of the plug-in.
     *
     * @return Human readable name of the plug-in
     */
    String getName();

    /**
     * Gets a description of the plug-in.
     *
     * @return Description of the plug-in
     */
    String getDescription();

    /**
     * Gets the vendor of the plug-in.
     *
     * @return Vendor of the plug-in
     */
    String getVendor();

    /**
     * Gets the release date of the plug-in.
     *
     * @return Release date of the plug-in
     */
    Date getDate();

    /**
     * Gets a {@link ResourceBundle} containing
     * localised messages for the {@link Plugin}.
     * 
     * @return {@link ResourceBundle} containing
     *         localised messages for the {@link Plugin}.
     */
    ResourceBundle getBundle();

    /**
     * Gets information/help about the plug-in.
     * 
     * @return Information or help for the plug-in
     */
    String getAbout();
}
