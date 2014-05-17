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
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.converge.core.plugin.Plugin;
import dk.i2m.converge.domain.Property;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for <code>/administrator/Configuration.jspx</code>.
 *
 * @author Allan Lykke Christensen
 */
public class Configuration {

    @EJB private SystemFacadeLocal systemFacade;

    private DataModel plugins = null;

    private DataModel properties = null;

    /**
     * Creates a new instance of {@link Configuration}.
     */
    public Configuration() {
    }

    /**
     * Event handler for updating all the properties in the {@link DataModel}.
     * 
     * @param event
     *          Event that invoked the handler
     */
    public void onUpdateProperties(ActionEvent event) {
        List<Property> propertyList = (List<Property>) this.properties.getWrappedData();
        systemFacade.updateSystemProperties(propertyList);
        this.properties = null;
    }


    /**
     * Gets a {@link DataModel} of the installed plugins.
     *
     * @return {@link DataModel} of installed plugins
     */
    public DataModel getPlugins() {
        if (this.plugins == null) {
            List<Plugin> p = new ArrayList<Plugin>();
            for (String key : systemFacade.getPlugins().keySet()) {
                p.add(systemFacade.getPlugins().get(key));
            }
            this.plugins = new ListDataModel(p);
        }
        return this.plugins;
    }

    /**
     * Gets a {@link DataModel} of system properties.
     *
     * @return {@link DataModel} of system properties
     */
    public DataModel getProperties() {
        if (this.properties == null) {
            this.properties = new ListDataModel(systemFacade.getSystemProperties());
        }
        return this.properties;
    }
}
