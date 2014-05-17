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
package dk.i2m.converge.web.servlets.listeners;

import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener for doing a sanity check of the system
 * upon application deployment.
 *
 * @author Allan Lykke Christensen
 */
public class StartupCheckListener implements ServletContextListener {

    @EJB private SystemFacadeLocal systemFacade;

    /**
     * Performs a sanity check upon deployment.
     *
     * @param event
     *          Event that invoked the listener
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        systemFacade.sanityCheck();
    }

    /**
     * Context is undeployed from the servlet container.
     *
     * @param event
     *          Event that invoked the listener
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
