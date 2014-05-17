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
package dk.i2m.converge.core.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object (DTO) for {@link Edition}s.
 *
 * @author Allan Lykke Christensen
 */
public class EditionView {

    private Long id;

    private Long outletId;

    private String outletName;

    private boolean open;

    private Date publicationDate;

    private Date expirationDate;

    private Date closeDate;

    private List<EditionAssignmentView> assignments = new ArrayList<EditionAssignmentView>();

    public EditionView() {
    }

    public EditionView(Long id, Long outletId, String outletName, boolean open, Calendar publicationDate, Calendar expirationDate, Date closeDate) {
        this.id = id;
        this.outletId = outletId;
        this.outletName = outletName;
        this.open = open;
        this.publicationDate = publicationDate.getTime();
        this.expirationDate = expirationDate.getTime();
        this.closeDate = closeDate;
    }

    public List<EditionAssignmentView> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<EditionAssignmentView> assignments) {
        this.assignments = assignments;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EditionView other = (EditionView) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "EditionView{" + "id=" + id + ", outletName=" + outletName + ", open=" + open + ", publicationDate=" + publicationDate + ", expirationDate=" + expirationDate + ", closeDate=" + closeDate + "}";
    }
}
