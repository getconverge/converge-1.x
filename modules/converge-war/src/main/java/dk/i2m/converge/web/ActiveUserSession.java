/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
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

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.security.UserAccount;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Holds information about an active user session.
 *
 * @author Allan Lykke Christensen
 */
public class ActiveUserSession {

    private Calendar start;

    private Calendar lastActivity;

    private String sessionId;

    private String ipAddress = "";

    private UserAccount userAccount = null;

    private List<NewsItem> locks = new ArrayList<NewsItem>();

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Calendar getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Calendar lastActivity) {
        this.lastActivity = lastActivity;
    }

    public int getDurationHours() {
        long duration = lastActivity.getTimeInMillis() - start.getTimeInMillis();

        return (int) (duration / 1000 / 60 / 60);
    }

    public int getDurationMinutes() {
        long duration = lastActivity.getTimeInMillis() - start.getTimeInMillis();
        return (int) (duration / 1000 / 60);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    /**
     * Gets a {@link List} of {@link NewsItem}s locked
     * by the {@link UserAccount}.
     * 
     * @return {@link List} of {@link NewsItem}s locked
     *         by the {@link UserAccount}.
     */
    public List<NewsItem> getLocks() {
        return locks;
    }

    /**
     * Sets the {@link List} of {@link NewsItem}s locked
     * by the {@link UserAccount}.
     * 
     * @param locks
     *         {@link List} of {@link NewsItem}s locked
     *         by the {@link UserAccount}.
     */
    public void setLocks(List<NewsItem> locks) {
        this.locks = locks;
    }

    /**
     * Gets the number of locks held by the 
     * {@link UserAccount}.
     * 
     * @return Number of {@link NewsItem} locks held
     *         by the {@link UserAccount}
     */
    public int getNumberOfLocks() {
        if (this.locks == null) {
            return 0;
        } else {
            return this.locks.size();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActiveUserSession other = (ActiveUserSession) obj;
        if ((this.sessionId == null) ? (other.sessionId != null) : !this.sessionId.equals(other.sessionId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.sessionId != null ? this.sessionId.hashCode() : 0);
        return hash;
    }
}
