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
package dk.i2m.converge.core.reporting.activity;

import dk.i2m.converge.core.security.UserRole;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model object containing a single line of an Activity Report.
 * 
 * To generate an activity report, a user role, workflow state 
 * and start and stop date must be selected.
 *
 * @author Allan Lykke Christensen
 */
public class ActivityReport {

    private Date start;

    private Date end;

    private UserRole userRole;

    private List<UserActivity> userActivity;

    public ActivityReport() {
        userActivity = new ArrayList<UserActivity>();
    }

    public ActivityReport(Date start, Date end, UserRole userRole) {
        this.start = start;
        this.end = end;
        this.userRole = userRole;
        userActivity = new ArrayList<UserActivity>();
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public List<UserActivity> getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(List<UserActivity> userActivity) {
        this.userActivity = userActivity;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
