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
package dk.i2m.converge.jsf.beans;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.ejb.facades.OutletFacadeLocal;
import javax.ejb.EJB;
import dk.i2m.jsf.JsfUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.faces.event.ActionEvent;

/**
 * Managed bean for {@code /NewsReader.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class NewsReader {

    private static final Logger log = Logger.getLogger(NewsReader.class.getName());

    @EJB private OutletFacadeLocal outletFacade;

    private dk.i2m.converge.core.workflow.Outlet selectedOutlet;

    private Date selectedDate;

    private List<Edition> selectedEditions = new ArrayList<Edition>();

    private Edition selectedEdition;
    
    private String selectedEditionTitle = "";

    @PostConstruct
    public void onInit() {
        selectedDate = null;
        selectedOutlet = getUser().getDefaultOutlet();
        fetchEditions();
    }

    private UserAccount getUser() {
        return (UserAccount) JsfUtils.getValueOfValueExpression("#{userSession.user}");
    }

    /**
     * Gets a {@link Map} of the {@link Outlet}s where the current user has
     * news reader privileges.
     *
     * @return {@link Map} of the {@link Outlet}s where the current user has
     *         news reader privileges
     */
    public Map<String, Outlet> getOutletsMap() {
        Map<String, Outlet> outlets = new LinkedHashMap<String, Outlet>();
        UserAccount currentUser = (UserAccount) JsfUtils.getValueOfValueExpression("#{userSession.user}");
        for (Outlet outlet : currentUser.getPrivilegedOutlets(SystemPrivilege.NEWS_READER)) {
            outlets.put(outlet.getTitle(), outlet);
        }
        return outlets;
    }

    /**
     * Gets a {@link List} of the {@link Outlet}s where the current user has
     * outlet planning privileges.
     *
     * @return {@link List} of the {@link Outlet}s where the current user has
     *         outlet planning privileges
     */
    public List<Outlet> getOutlets() {
        UserAccount currentUser = (UserAccount) JsfUtils.getValueOfValueExpression("#{userSession.user}");
        return currentUser.getPrivilegedOutlets(SystemPrivilege.NEWS_READER);
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    /**
     * Event handler for when a date is selected from the calendar.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onSelectDate(ActionEvent event) {
        fetchEditions();
    }

    private void fetchEditions() {
        if (isOutletSelected() && isDateSelected()) {
            java.util.Calendar editionDate = java.util.Calendar.getInstance();
            editionDate.setTime(selectedDate);

            //selectedEditions = outletFacade.findEditionByOutletAndDate(selectedOutlet.getId(), editionDate);
            selectedEditions = outletFacade.findEditionsByDate(selectedOutlet, editionDate);
        } else {
            log.log(Level.FINEST, "Outlet [{0}] or date [{1}] is not selected", new Object[]{selectedOutlet, selectedDate});
        }
    }

    /**
     * Determines if an {@link Outlet} has been selected.
     *
     * @return <code>true</code> if an {@link Outlet} has been selected, 
     *         otherwise <code>false</code>
     */
    public boolean isOutletSelected() {
        if (selectedOutlet == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if a date has been selected.
     *
     * @return <code>true</code> if a date has been selected, otherwise
     *         <code>false</code>
     */
    public boolean isDateSelected() {
        if (selectedDate == null) {
            return false;
        } else {
            return true;
        }
    }

    public List<Edition> getSelectedEditions() {
        return selectedEditions;
    }

    public void setSelectedEditions(List<Edition> selectedEditions) {
        this.selectedEditions = selectedEditions;
    }

    public Outlet getSelectedOutlet() {
        return selectedOutlet;
    }

    public void setSelectedOutlet(Outlet selectedOutlet) {
        this.selectedOutlet = selectedOutlet;
    }

    public Edition getSelectedEdition() {
        return selectedEdition;
    }

    public void setSelectedEdition(Edition selectedEdition) {
        this.selectedEdition = selectedEdition;
        try {
            DateFormat df = new SimpleDateFormat("EEEE d. MMMM yyyy hh:mm aa");
            df.setTimeZone((TimeZone) JsfUtils.getValueOfValueExpression("#{common.systemTimeZone}"));
            this.selectedEditionTitle = getSelectedEdition().getOutlet().getTitle() + " - " + df.format(getSelectedEdition().getPublicationDate().getTime());
        } catch (Exception ex) {
            this.selectedEditionTitle = ex.getMessage();
        }
    }

    public String getSelectedEditionTitle() {
        return this.selectedEditionTitle;
    }
}
