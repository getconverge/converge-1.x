/*
 *  Copyright (C) 2010 - 2011 Allan Lykke Christensen
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
 * A pattern describing an {@link dk.i2m.converge.core.workflow.Edition} that
 * should automatically be created for an {@link Outlet}.
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

    @Column(name = "edition_date")
    private int day;

    @Column(name = "start_hour")
    private int startHour;

    @Column(name = "start_minute")
    private int startMinute;

    @Column(name = "end_hour")
    private int endHour;

    @Column(name = "end_minute")
    private int endMinute;

    @Column(name = "close_hour")
    private int closeHour;

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
     * @param day Day
     * @param startHour Start hour
     * @param startMinute Start minute
     * @param endHour End hour
     * @param endMinute End minute
     * @param closeHour Hour when the edition should be closed
     * @param closeMinute Minute when the edition should be closed
     * @param outlet Outlet of the edition
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
     * Sets the unique identifier of the pattern. This method should not be
     * invoked manually as the identifier is automatically set by JPA.
     *
     * @param id Unique identifier of the pattern
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
     * @param title Title of the pattern
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
     * @param activeFrom Date and time from when this pattern is active
     */
    public void setActiveFrom(Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    /**
     * Date and time from when this pattern is no longer active. If {@code null}
     * is returned there is no expiration for this pattern.
     *
     * @return Date and time for when this pattern is no longer active, or
     * {@code null} if this pattern has no expiration
     */
    public Date getActiveTo() {
        return activeTo;
    }

    /**
     * Sets the date and time when this pattern should no longer be active. If
     * the pattern should not expire, set it to {@code null}.
     *
     * @param activeTo Date and time for when this pattern is no longer active,
     * or {@code null} if this pattern should not have an expiration time
     */
    public void setActiveTo(Date activeTo) {
        this.activeTo = activeTo;
    }

    /**
     * Gets the automatic close edition hour.
     *
     * @return Automatic close edition hour
     */
    public int getCloseHour() {
        return closeHour;
    }

    /**
     * Sets the automatic close edition hour.
     *
     * @param closeHour Automatic close edition hour
     */
    public void setCloseHour(int closeHour) {
        this.closeHour = closeHour;
    }

    /**
     * Gets the automatic close edition minute.
     *
     * @return Automatic close edition minute
     */
    public int getCloseMinute() {
        return closeMinute;
    }

    /**
     * Sets the automatic close edition minute.
     *
     * @param closeMinute Automatic close edition minute
     */
    public void setCloseMinute(int closeMinute) {
        this.closeMinute = closeMinute;
    }

    /**
     * Gets the day of the pattern. Use days in {@link java.util.Calendar}.
     *
     * @return Day of the pattern
     */
    public int getDay() {
        return day;
    }

    /**
     * Sets the day of the pattern. Use days in {@link java.util.Calendar}.
     *
     * @param day Day of the pattern
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Gets the end (hour) of the edition.
     *
     * @return End (hour) of the edition
     */
    public int getEndHour() {
        return endHour;
    }

    /**
     * Sets the end (hour) of the edition.
     *
     * @param endHour End (hour) of the edition
     */
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    /**
     * Gets the end (minute) of the edition.
     *
     * @return End (minute) of the edition
     */
    public int getEndMinute() {
        return endMinute;
    }

    /**
     * Sets the end (minute) of the edition.
     *
     * @param endMinute End (minute) of the edition
     */
    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }

    /**
     * Gets the start (hour) of the edition.
     *
     * @return Start (hour) of the edition
     */
    public int getStartHour() {
        return startHour;
    }

    /**
     * Sets the start (hour) of the edition.
     *
     * @param startHour Start (hour) of the edition
     */
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    /**
     * Gets the start (minute) of the edition.
     *
     * @return Start (minute) of the edition.
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * Sets the start (minute) of the edition.
     *
     * @param startMinute Start (minute) of the edition.
     */
    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    /**
     * Determines if the given date matches the start date of this pattern.
     *
     * @param start Start date of the edition
     * @return {@code true} if {@code start} matches the start date of this
     * pattern, otherwise {@code false}
     */
    public boolean isMatchPublicationDate(Date start) {
        if (start == null) {
            return false;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(start);

        return c.get(Calendar.HOUR_OF_DAY) == getStartHour() && c.get(Calendar.MINUTE) == getStartMinute();
    }

    public boolean isMatchDay(Date day) {
        Calendar c = Calendar.getInstance();
        c.setTime(day);

        return getDay() == c.get(Calendar.DAY_OF_WEEK);
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
