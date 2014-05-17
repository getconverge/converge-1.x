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
package dk.i2m.converge.core.workflow;

import java.io.Serializable;
import java.util.Date;

/**
 * Candidate for an {@link Edition}
 *
 * @author Allan Lykke Christensen
 */
public class EditionCandidate implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long editionId;

    private Long outletId;

    private Date publicationDate;

    private Date expirationDate;

    private Date closeDate;

    public EditionCandidate() {
    }

    public EditionCandidate(Long editionId, Long outletId, Date publicationDate, Date expirationDate, Date closeDate) {
        this.editionId = editionId;
        this.outletId = outletId;
        this.publicationDate = publicationDate;
        this.expirationDate = expirationDate;
        this.closeDate = closeDate;
    }

    public EditionCandidate(Edition edition) {
        if (edition == null) {
            throw new IllegalArgumentException("Edition cannot be null");
        }
        this.editionId = edition.getId();
        if (edition.getOutlet() != null) {
            this.outletId = edition.getOutlet().getId();
        }
        if (edition.getPublicationDate() != null) {
            this.publicationDate = edition.getPublicationDate().getTime();
        }
        if (edition.getExpirationDate() != null) {
            this.expirationDate = edition.getExpirationDate().getTime();
        }
        if (edition.getCloseDate() != null) {
            this.closeDate = edition.getCloseDate();
        }
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Long getEditionId() {
        return editionId;
    }

    public void setEditionId(Long editionId) {
        this.editionId = editionId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * Determines if the {@link Edition} already exist in the database.
     * 
     * @return {@code true} if the {@link Edition} already exists in
     *         the database, otherwise {@code false}
     */
    public boolean isExist() {
        if (getEditionId() != null) {
            return true;
        } else {
            return false;
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
        final EditionCandidate other = (EditionCandidate) obj;
        if (other.getEditionId() != null && other.getEditionId() == this.editionId) {
            return true;
        }
        
        if (this.editionId != other.editionId && (this.editionId == null || !this.editionId.equals(other.editionId))) {
            return false;
        }
        if (this.outletId != other.outletId && (this.outletId == null || !this.outletId.equals(other.outletId))) {
            return false;
        }
        if (this.publicationDate != other.publicationDate && (this.publicationDate == null || !this.publicationDate.equals(other.publicationDate))) {
            return false;
        }
        if (this.expirationDate != other.expirationDate && (this.expirationDate == null || !this.expirationDate.equals(other.expirationDate))) {
            return false;
        }
        if (this.closeDate != other.closeDate && (this.closeDate == null || !this.closeDate.equals(other.closeDate))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.editionId != null ? this.editionId.hashCode() : 0);
        hash = 73 * hash + (this.outletId != null ? this.outletId.hashCode() : 0);
        hash = 73 * hash + (this.publicationDate != null ? this.publicationDate.hashCode() : 0);
        hash = 73 * hash + (this.expirationDate != null ? this.expirationDate.hashCode() : 0);
        hash = 73 * hash + (this.closeDate != null ? this.closeDate.hashCode() : 0);
        return hash;
    }
    
    
}
