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
package dk.i2m.converge.core.workflow;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * A pattern describing an edition that should automatically be created.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "edition_pattern")
@NamedQueries({})
public class EditionPattern implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    /** Day of the pattern. Use days in {@link java.util.Calendar}. */
    @Column(name = "edition_date")
    private int day;

    /** Start (hour) of the edition. */
    @Column(name = "start_hour")
    private int startHour;

    /** Start (minute) of the edition. */
    @Column(name = "start_minute")
    private int startMinute;

    /** End (hour) of the edition. */
    @Column(name = "end_hour")
    private int endHour;

    /** End (minute) of the edition. */
    @Column(name = "end_minute")
    private int endMinute;

    /** Automatic close edition hour. */
    @Column(name = "close_hour")
    private int closeHour;

    /** Automatic close edition minute. */
    @Column(name = "close_minute")
    private int closeMinute;

    @Column(name = "active_from")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date activeFrom;

    @Column(name = "active_to")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date activeTo;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    private Outlet outlet;

    /**
     * Creates a new instance of {@link EditionPattern}.
     */
    public EditionPattern() {
    }

    /**
     * Creates a new instance of {@link EditionPattern}.
     *
     * @param day
     *          Day
     * @param startHour
     *          Start hour
     * @param startMinute
     *          Start minute
     * @param endHour
     *          End hour
     * @param endMinute
     *          End minute
     * @param closeHour
     *          Hour when the edition should be closed
     * @param closeMinute
     *          Minute when the edition should be closed
     * @param outlet
     *          Outlet of the edition
     */
    public EditionPattern(int day, int startHour, int startMinute, int endHour, int endMinute, int closeHour, int closeMinute, Outlet outlet) {
        this.day = day;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.closeHour = closeHour;
        this.closeMinute = closeMinute;
        this.outlet = outlet;
    }

    /**
     * Gets the unique identifier of the pattern.
     *
     * @return Unique identifier of the pattern
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the pattern. This method should not
     * be invoked manually as the identifier is automatically set by JPA.
     *
     * @param id
     *          Unique identifier of the pattern
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the title of the pattern. The title identification purpose only.
     * 
     * @return Title of the pattern
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the pattern. The title identification purpose only.
     * 
     * @param title
     *          Title of the pattern
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Date and time from when this pattern is active.
     *
     * @return Date and time from when this pattern is active.
     */
    public Date getActiveFrom() {
        return activeFrom;
    }

    /**
     * Sets the date and time from when this pattern is active.
     *
     * @param activeFrom
     *          Date and time from when this pattern is active
     */
    public void setActiveFrom(Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    /**
     * Date and time from when this pattern is no longer active. If {@code null}
     * is returned there is no expiration for this pattern.
     *
     * @return Date and time for when this pattern is no longer active, or
     *         {@code null} if this pattern has no expiration
     */
    public Date getActiveTo() {
        return activeTo;
    }

    /**
     * Sets the date and time when this pattern should no longer be active. If
     * the pattern should not expire, set it to {@code null}.
     *
     * @param activeTo
     *          Date and time for when this pattern is no longer active, or
     *         {@code null} if this pattern should not have an expiration time
     */
    public void setActiveTo(Date activeTo) {
        this.activeTo = activeTo;
    }

    public int getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(int closeHour) {
        this.closeHour = closeHour;
    }

    public int getCloseMinute() {
        return closeMinute;
    }

    public void setCloseMinute(int closeMinute) {
        this.closeMinute = closeMinute;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    /**
     * Determines if the given date matches the start date of
     * this pattern.
     * 
     * @param start
     *          Start date of the edition
     * @return {@code true} if {@code start} matches the start
     *         date of this pattern, otherwise {@code false}
     */
    public boolean isMatchPublicationDate(Date start) {
        if (start == null) {
            return false;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(start);

        if (c.get(Calendar.HOUR_OF_DAY) == getStartHour() && c.get(Calendar.MINUTE) == getStartMinute()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMatchDay(Date day) {
        Calendar c = Calendar.getInstance();
        c.setTime(day);

        if (getDay() == c.get(Calendar.DAY_OF_WEEK)) {
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
        final EditionPattern other = (EditionPattern) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.day != other.day) {
            return false;
        }
        if (this.startHour != other.startHour) {
            return false;
        }
        if (this.startMinute != other.startMinute) {
            return false;
        }
        if (this.endHour != other.endHour) {
            return false;
        }
        if (this.endMinute != other.endMinute) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 59 * hash + this.day;
        hash = 59 * hash + this.startHour;
        hash = 59 * hash + this.startMinute;
        hash = 59 * hash + this.endHour;
        hash = 59 * hash + this.endMinute;
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "/day=" + day + "/startHour=" + startHour + "/startMinute=" + startMinute + "/endHour=" + endHour + "/endMinute=" + endMinute + "/closeHour=" + closeHour + "/closeMinute=" + closeMinute + "]";
    }
}
