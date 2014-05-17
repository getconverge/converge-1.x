/*
 * Copyright (C) 2010 - 2011 Interactive Media Management
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

import dk.i2m.converge.core.content.NewsItemPlacement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * {@link Edition} of an {@link Outlet} containing information about the
 * edition a given {@link Outlet}, e.g. volume, number, publication date,
 * and expiration date.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "edition")
@NamedQueries({
    @NamedQuery(name = Edition.FIND_BY_OUTLET_AND_DATE, query = "SELECT e FROM Edition AS e WHERE e.outlet = :outlet AND e.publicationDate >= :start_date AND e.publicationDate <= :end_date ORDER BY e.publicationDate ASC"),
    @NamedQuery(name = Edition.VIEW_EDITION_PLANNING, query = "SELECT NEW dk.i2m.converge.core.dto.EditionView(e.id, e.outlet.id, e.outlet.title, e.open, e.publicationDate, e.expirationDate, e.closeDate) FROM Edition e WHERE e.outlet = :outlet AND e.publicationDate >= :start_date AND e.publicationDate <= :end_date ORDER BY e.publicationDate ASC"),
    @NamedQuery(name = Edition.FIND_BY_STATUS, query = "SELECT e FROM Edition AS e WHERE e.open = :status AND e.outlet = :outlet ORDER BY e.publicationDate DESC"),
    @NamedQuery(name = Edition.FIND_OVERDUE, query = "SELECT e FROM Edition e WHERE e.open = true AND e.closeDate IS NOT NULL AND e.closeDate <= CURRENT_TIMESTAMP")
})
public class Edition implements Serializable {

    private static final long serialVersionUID = 2L;

    /** Query for getting a view of edition for a particular outlet on a particular date. Returns {@link List} of {@link dk.i2m.converge.core.dto.EditionView}. */
    public static final String VIEW_EDITION_PLANNING = "Edition.viewEditionPlanning";
    
    /** Query for finding editions by a particular outlet and date. Returns {@link List} of {@link Edition}s. */
    public static final String FIND_BY_OUTLET_AND_DATE = "Edition.findByOutletAndDate";
    
    /** Query for finding editions by their status. */
    public static final String FIND_BY_STATUS = "Edition.findByStatus";

    /** Query for finding editions with overdue close dates. */
    public static final String FIND_OVERDUE = "Edition.closeOverdue";

    /** Unique ID of the {@link Edition}. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Outlet of the {@link Edition}. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "outlet_id")
    private Outlet outlet;

    /** Date when the {@link Edition} should be published. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "publication_date")
    private Calendar publicationDate = null;

    /** Date when the {@link Edition} should expire. */
    @Column(name = "expiration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar expirationDate = null;

    @Column(name = "close_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date closeDate;

    /** Volume of the publication. */
    @Column(name = "volume")
    private Integer volume = 0;

    /** Number of the publication. */
    @Column(name = "number")
    private Integer number = 0;

    @Column(name = "open_for_input")
    private boolean open = true;

    @OneToMany(mappedBy = "edition")
    @OrderBy("start ASC, position ASC")
    private List<NewsItemPlacement> placements = new ArrayList<NewsItemPlacement>();

    /**
     * Creates a new instance of {@link Edition}.
     */
    public Edition() {
    }

    /**
     * Creates a new instance of {@link Edition}.
     * 
     * @param outlet
     *          {@link Outlet} of the {@link Edition}
     * @param closeDate
     *          {@link Date} when the {@link Edition} should be closed
     */
    public Edition(Outlet outlet, Date closeDate) {
        this.outlet = outlet;
        this.closeDate = closeDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }

    public Calendar getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Calendar publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Calendar getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Calendar expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * Gets the {@link Date} when the {@link Edition} should be
     * automatically closed.
     * 
     * @return {@link Date} when the {@link Edition} should be
     *         closed automatically, or {@code null} if the 
     *         {@link Edition} should be closed manually.
     */
    public Date getCloseDate() {
        return closeDate;
    }

    /**
     * Sets the {@link Date} when the {@link Edition} should be
     * closed automatically.
     * 
     * @param closeDate
     *          {@link Date} when the {@link Edition} should be
     *          close automatically, or {@code null} if the
     *          {@link Edition} should be closed manually
     */
    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    /**
     * Gets the placements of the {@link Edition}.
     *
     * @return {@link List} of {@link NewsItemPlacement}s for the {@link Edition}
     */
    public List<NewsItemPlacement> getPlacements() {
        return placements;
    }

    /**
     * Sets the placements of the {@link Edition}.
     *
     * @param placements
     *          {@link List} of {@link NewsItemPlacement}s for the {@link Edition}
     */
    public void setPlacements(List<NewsItemPlacement> placements) {
        this.placements = placements;
    }

    /**
     * Adds a new placement to the {@link Eeition}.
     *
     * @param placement
     *          Placement to add to the {@link Edition}
     */
    public void addPlacement(NewsItemPlacement placement) {
        if (placements == null) {
            this.placements = new ArrayList<NewsItemPlacement>();
        }
        this.placements.add(placement);
    }

    /**
     * Gets the number of placements for the {@link Edition}.
     *
     * @return Number of placements for the {@link Edition}
     */
    public int getNumberOfPlacements() {
        if (placements == null) {
            return 0;
        } else {
            return placements.size();
        }
    }

    /**
     * Determines if the {@link Edition} exist in the database.
     * 
     * @return {@code true} if the {@link Edition} exist in the
     *         database, otherwise {@code false}
     */
    public boolean isExist() {
        if (id == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getPublicationYear() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        if (getPublicationDate() != null) {
            return formatter.format(getPublicationDate().getTime());
        } else {
            return "";
        }
    }

    public String getPublicationMonth() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        if (getPublicationDate() != null) {
            return formatter.format(getPublicationDate().getTime());
        } else {
            return "";
        }
    }

    public String getPublicationDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        if (getPublicationDate() != null) {
            return formatter.format(getPublicationDate().getTime());
        } else {
            return "";
        }
    }

    public String getPublicationHour() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        if (getPublicationDate() != null) {
            return formatter.format(getPublicationDate().getTime());
        } else {
            return "";
        }
    }

    public String getPublicationMinute() {
        SimpleDateFormat formatter = new SimpleDateFormat("mm");
        if (getPublicationDate() != null) {
            return formatter.format(getPublicationDate().getTime());
        } else {
            return "";
        }
    }

    public String getFriendlyName() {
        StringBuilder publicationTitle = new StringBuilder();
        publicationTitle.append(getOutlet().getTitle());
        publicationTitle.append(" Volume ");
        publicationTitle.append(getVolume());
        publicationTitle.append(" Number ");
        publicationTitle.append(getNumber());
        publicationTitle.append(" (Publication: ");
        if (getPublicationDate() == null) {
            publicationTitle.append("not set" + ")");
        } else {
            publicationTitle.append(getPublicationDate().getTime());
            publicationTitle.append(")");
        }
        return publicationTitle.toString();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Edition)) {
            return false;
        }
        Edition other = (Edition) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }
}
