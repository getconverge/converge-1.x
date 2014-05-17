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

import dk.i2m.converge.core.dto.EditionView;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.ejb.facades.OutletFacadeLocal;
import dk.i2m.jsf.JsfUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

/**
 * Backing bean for {@code /SearchByEdition.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class SearchByEdition {

    private boolean showResults = false;

    @EJB
    private OutletFacadeLocal outletFacade;

    private Outlet selectedOutlet;

    private Date selectedDate;

    private List<EditionView> editions = new ArrayList<EditionView>();

    @PostConstruct
    private void onInit() {
        selectedOutlet = getUser().getDefaultOutlet();
        selectedDate = java.util.Calendar.getInstance().getTime();
    }

    public boolean isShowResults() {
        return showResults;
    }

    public void setShowResults(boolean showResults) {
        this.showResults = showResults;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Outlet getSelectedOutlet() {
        return selectedOutlet;
    }

    public void setSelectedOutlet(Outlet selectedOutlet) {
        this.selectedOutlet = selectedOutlet;
    }

    public void onSearch(ActionEvent event) {
        setShowResults(true);
        editions = outletFacade.findEditionViewsByDate(selectedOutlet.getId(), selectedDate, false, true);
    }

    private UserAccount getUser() {
        final String valueExpression = "#{userSession.user}";
        return (UserAccount) JsfUtils.getValueOfValueExpression(valueExpression);
    }

    public List<EditionView> getEditions() {
        return editions;
    }

    public void setEditions(List<EditionView> editions) {
        this.editions = editions;
    }
}
