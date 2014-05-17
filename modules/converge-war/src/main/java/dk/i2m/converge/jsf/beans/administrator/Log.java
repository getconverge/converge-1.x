/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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

import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/Log.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Log {

    @EJB private SystemFacadeLocal systemFacade;

    private DataModel logEntries = new ListDataModel();

    /**
     * Creates a new instance of {@link Log}.
     */
    public Log() {
    }

    /**
     * Event handler invoked after construction of the page.
     */
    @PostConstruct
    public void onInit() {
        onRefresh(null);
    }

    /**
     * Event handler for updating the log listing.
     * <p/>
     * @param event Event that invoked the handler
     */
    public void onRefresh(ActionEvent event) {
        logEntries = new ListDataModel(systemFacade.findLogEntries(0, 2000));
    }

    /**
     * Gets the log listing.
     * <p/>
     * @return {@link DataModel} containing the log listing
     */
    public DataModel getLogEntries() {
        return logEntries;
    }
}
