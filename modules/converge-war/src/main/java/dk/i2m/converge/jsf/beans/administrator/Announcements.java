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
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.converge.core.Announcement;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import java.util.Calendar;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/Announcements.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Announcements {

    @EJB private SystemFacadeLocal systemFacade;

    private DataModel announcements = null;

    private Announcement selectedAnnouncement = null;

    public void onNew(ActionEvent event) {
        selectedAnnouncement = new Announcement();
        selectedAnnouncement.setPublished(true);
        selectedAnnouncement.setDate(Calendar.getInstance());
        selectedAnnouncement.setThumb("/images/announcement/megaphone.png");
    }

    public void onSave(ActionEvent event) {
        if (isEditMode()) {
            selectedAnnouncement = systemFacade.updateAnnouncement(selectedAnnouncement);
        } else {
            selectedAnnouncement = systemFacade.createAnnouncement(selectedAnnouncement);
        }
        announcements = null;
    }

    public void onDelete(ActionEvent event) {
        if (isEditMode()) {
            systemFacade.deleteAnnouncement(selectedAnnouncement.getId());
        }
        announcements = null;
    }

    public boolean isEditMode() {
        if (selectedAnnouncement != null && selectedAnnouncement.getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    public DataModel getAnnouncements() {
        if (announcements == null) {
            announcements = new ListDataModel(systemFacade.getAnnouncements());
        }
        return announcements;
    }

    public Announcement getSelectedAnnouncement() {
        return selectedAnnouncement;
    }

    public void setSelectedAnnouncement(Announcement selectedAnnouncement) {
        this.selectedAnnouncement = selectedAnnouncement;
    }
}
