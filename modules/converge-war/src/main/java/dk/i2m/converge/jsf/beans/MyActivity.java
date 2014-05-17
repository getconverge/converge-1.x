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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.reporting.activity.UserActivity;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.facades.ReportingFacadeLocal;
import dk.i2m.converge.utils.CalendarUtils;
import dk.i2m.jsf.JsfUtils;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Allan Lykke Christensen
 */
public class MyActivity {
    
    @EJB private ReportingFacadeLocal reportingFacade;
    
    private Date from;
    
    private Date to;
    
    private UserActivity userActivity;
    
    public MyActivity() {
    }
    
    @PostConstruct
    public void init() {
        java.util.Calendar firstDay = CalendarUtils.getFirstDayOfMonth();
        java.util.Calendar lastDay = CalendarUtils.getLastDayOfMonth();
        from = firstDay.getTime();
        to = lastDay.getTime();
        onRefresh(null);
    }
    
    public void onRefresh(ActionEvent event) {
        userActivity = reportingFacade.generateUserActivityReport(from, to, getUser(), true);
    }
    
    public Date getFrom() {
        return from;
    }
    
    public void setFrom(Date from) {
        this.from = from;
    }
    
    public Date getTo() {
        return to;
    }
    
    public void setTo(Date to) {
        this.to = to;
    }
    
    public UserActivity getUserActivity() {
        return userActivity;
    }
    
    public void setUserActivity(UserActivity userActivity) {
        this.userActivity = userActivity;
    }
    
    private UserAccount getUser() {
        return (UserAccount) JsfUtils.getValueOfValueExpression("#{userSession.user}");
    }
}
