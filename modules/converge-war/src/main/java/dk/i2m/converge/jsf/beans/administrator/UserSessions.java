/*
 * Copyright (C) 2010 - 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import dk.i2m.converge.web.ActiveUserSession;
import dk.i2m.jsf.JsfUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpSession;

/**
 * Backing bean for {@code /administrator/UserSessions.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class UserSessions {

    private DataModel sessions;

    @EJB private UserFacadeLocal userFacade;

    private int anonymousSessions = 0;

    /**
     * Creates a new instance of {@link UserSessions}.
     */
    public UserSessions() {
    }

    public DataModel getSessions() {
        List<ActiveUserSession> activeSessions =
                new ArrayList<ActiveUserSession>();
        anonymousSessions = 0;

        List<HttpSession> sessionRegistry = dk.i2m.converge.web.UserSessions.
                getInstance().getCurrentSessions();

        for (HttpSession httpSession : sessionRegistry) {

            try {
                String uid = (String) httpSession.getAttribute("uid");
                String ip = (String) httpSession.getAttribute("ip");
                ActiveUserSession session = new ActiveUserSession();
                if (uid != null) {
                    UserAccount ua = userFacade.findByIdWithLocks(uid);
                    session.setUserAccount(ua);
                    session.setLocks(ua.getCheckedOut());
                } else {
                    anonymousSessions++;
                    continue;
                }

                Calendar start = Calendar.getInstance();
                start.setTimeInMillis(httpSession.getCreationTime());

                Calendar lastActivity = Calendar.getInstance();
                lastActivity.setTimeInMillis(httpSession.getLastAccessedTime());

                session.setStart(start);
                session.setSessionId(httpSession.getId());

                session.setLastActivity(lastActivity);
                session.setIpAddress(ip);

                activeSessions.add(session);
            } catch (DataNotFoundException ex) {
                // Session not related to user, no need to show
            } catch (IllegalStateException ex) {
                // Session has been invalidated
            }
        }
        this.sessions = new ListDataModel(activeSessions);
        return this.sessions;
    }

    /**
     * Get the number of anonymous sessions.
     * 
     * @return Number of anonymous user sessions
     */
    public int getAnonymousSessions() {
        return anonymousSessions;
    }

    /**
     * Invalidates an {@link ActiveUserSession}.
     *
     * @param session {@link ActiveUserSession} to invalidate
     */
    public void setInvalidateSession(ActiveUserSession session) {
        List<HttpSession> sessionRegistry = dk.i2m.converge.web.UserSessions.
                getInstance().getCurrentSessions();

        for (HttpSession httpSession : sessionRegistry) {
            if (httpSession.getId().equals(session.getSessionId())) {
                httpSession.invalidate();
                JsfUtils.createMessage("frmUserSessions",
                        FacesMessage.SEVERITY_INFO, "i18n",
                        "administrator_UserSessions_USER_SESSION_TERMINATED",
                        null);
                return;
            }
        }
    }
}
