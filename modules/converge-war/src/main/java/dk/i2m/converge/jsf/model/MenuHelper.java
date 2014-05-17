/*
 * Copyright (C) 2011 Interactive Media Management
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
package dk.i2m.converge.jsf.model;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Helper for setting up {@link MenuItems}.
 *
 * @author Allan Lykke Christensen
 */
public class MenuHelper {

    private static final Logger LOG = Logger.getLogger(MenuHelper.class.toString());

    private static final String MENU_XML = "/dk/i2m/converge/menu.xml";

    private MenuItems menuItems = null;

    private static MenuHelper instance = null;

    private MenuHelper() {
    }

    /**
     * Gets an instance of {@link MenuHelper}.
     * 
     * @return Instance of the {@link MenuHelper} singleton
     */
    public static MenuHelper getInstance() {
//        if (instance == null) {
            instance = new MenuHelper();
//        }
        return instance;
    }

    /**
     * Gets all the available {@link MenuItems} of the application.
     * 
     * @return  {@link MenuItems} of the application
     */
    public MenuItems getMenuItems() {
        if (this.menuItems == null) {
            try {
                // Load menu items from XML file
                JAXBContext ctx = JAXBContext.newInstance(new Class[]{dk.i2m.converge.jsf.model.MenuItems.class, dk.i2m.converge.jsf.model.MenuItem.class});
                Unmarshaller um = ctx.createUnmarshaller();
                InputStream is = null;

                try {
                    is = getClass().getResourceAsStream(MENU_XML);
                    this.menuItems = (MenuItems) um.unmarshal(is);
                } catch (Exception e) {
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception e2) {
                        }
                    }
                }
            } catch (JAXBException ex) {
                LOG.log(Level.SEVERE, "Could not generate menu from menu.xml", ex);
            }
        }

        return this.menuItems;
    }
}
