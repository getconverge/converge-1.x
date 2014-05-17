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

import dk.i2m.converge.ejb.facades.NewsItemFacadeLocal;
import dk.i2m.converge.web.UserSessions;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Listens for the start and end of HTTP user sessions.
 *
 * @author Allan Lykke Christensen
 */
public class UserSessionListener implements HttpSessionListener {

    private static final Logger log = Logger.getLogger(UserSessionListener.class.getName());

    @EJB private NewsItemFacadeLocal newsItemFacade;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.log(Level.INFO, "User session {0} created", se.getSession().getId());
        UserSessions.getInstance().add(se.getSession());
        log.log(Level.INFO, "{0} active user {0, choice, 0#sessions|1#session|2#sessions}", UserSessions.getInstance().getNumberOfActiveSessions());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.log(Level.INFO, "User session {0} destroyed", se.getSession().getId());
        UserSessions.getInstance().remove(se.getSession().getId());
        log.log(Level.INFO, "{0} active user sessions", UserSessions.getInstance().getNumberOfActiveSessions());

        if (se.getSession().getAttribute("uid") != null) {
            String uid = (String) se.getSession().getAttribute("uid");
            boolean loggedin = UserSessions.getInstance().isUserLoggedIn(uid);

            if (loggedin) {
                log.log(Level.INFO, "{0} is logged-in in a different session", uid);
            } else {
                log.log(Level.INFO, "{0} is completely logged off. Clearing locks", uid);
                int removed = newsItemFacade.revokeLocks(uid);
                log.log(Level.INFO, "{0} {0, choice, 0#locks|1#lock|2#locks} removed", removed);
            }
        }
    }
}
