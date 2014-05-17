/*
 *  Copyright (C) 2010 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

/**
 * Singleton containing the active {@link HttpSession}s.
 *
 * @author Allan Lykke Christensen
 */
public class UserSessions implements Serializable {

    private static UserSessions instance = null;

    private Map<String, HttpSession> sessions;

    /**
     * Create the singleton instance of {@link UserSessions}.
     */
    private UserSessions() {
        sessions = new HashMap<String, HttpSession>();
    }

    /**
     * Gets the singleton instance of {@link UserSessions}.
     *
     * @return Singleton instance of {@link UserSession}
     */
    public static UserSessions getInstance() {
        if (instance == null) {
            instance = new UserSessions();
        }
        return instance;
    }

    /**
     * Gets a {@link List} current {@link HttpSession}s.
     *
     * @return {@link List} of current {@link HttpSession}s
     */
    public List<HttpSession> getCurrentSessions() {
        List<HttpSession> sessionList = new ArrayList<HttpSession>();

        for (HttpSession session : sessions.values()) {
            sessionList.add(session);
        }

        return sessionList;
    }

    /**
     * Removes an {@link HttpSession} from the registry of active sessions.
     *
     * @param sessionId
     *          Unique identifier of the {@link HttpSession}
     * @return <code>true</code> if the {@link HttpSession} was removed,
     *         <code>false</code> if the {@link HttpSession} could not be found
     */
    public boolean remove(String sessionId) {
        if (sessions.containsKey(sessionId)) {
            sessions.remove(sessionId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds an {@link HttpSession} to the registry of active sessions.
     * 
     * @param session
     *          {@link HttpSession} to add to the registry
     */
    public void add(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    public int getNumberOfActiveSessions() {
        return sessions.size();
    }

    /**
     * Determines if a given user is logged in.
     *
     * @param username
     *          Username of the user
     * @return {@code true} if the user is logged in, otherwise {@code false}
     */
    public boolean isUserLoggedIn(String username) {
        for (HttpSession session : getCurrentSessions()) {
            try {
                if (session.getAttribute("uid") != null && session.getAttribute("uid").equals(username)) {
                    return true;
                }
            } catch (IllegalStateException ex) {
            }
        }
        return false;
    }
}
