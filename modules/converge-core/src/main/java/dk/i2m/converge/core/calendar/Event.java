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
package dk.i2m.converge.core.calendar;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.security.UserAccount;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.*;

/**
 * Domain model object representing an event in a calendar.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "event")
@NamedQueries({
    @NamedQuery(name = Event.FIND_BY_DATE, query = "SELECT e FROM Event AS e WHERE e.startDate >= :start AND e.startDate <= :end ORDER BY e.startDate ASC"),
    @NamedQuery(name = Event.FIND_BY_START_DATE, query = "SELECT e FROM Event AS e WHERE e.startDate >= :start OR (e.startDate <= :start AND e.endDate >= :start) ORDER BY e.startDate ASC"),
    @NamedQuery(name = Event.FIND_BY_BETWEEN, query = "SELECT e FROM Event AS e WHERE :date BETWEEN e.startDate AND e.endDate ORDER BY e.startDate ASC")
})
public class Event implements Serializable {

    /** Query for finding events by a given date. */
    public static final String FIND_BY_DATE = "Event.findByDate";

    /** Query for finding events by a given start date. */
    public static final String FIND_BY_START_DATE = "Event.findByStartDate";

    /** Query for finding events in a given period. */
    public static final String FIND_BY_BETWEEN = "Event.findByBetween";

    /** Unique ID of the event. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Summary of the event. */
    @Column(name = "summary")
    private String summary = "";

    /** Location where the event will take place. */
    @Column(name = "location")
    private String location = "";

    /** Category of the event. */
    @Column(name = "category")
    private String category = "";

    /** Detailed description of the event. */
    @Column(name = "description") @Lob
    private String description = "";

    /** Start time of the event. */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Calendar startDate;

    /** End time of the event. */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Calendar endDate;

    @ManyToOne
    @JoinColumn(name = "originator_id")
    private UserAccount originator = null;

    /** All day event indicator. */
    @Column(name = "all_day_event")
    private boolean allDayEvent;

    /** Assignments created for this event. */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<NewsItem> newsItem = new ArrayList<NewsItem>();

    /**
     * Creates a new instance of {@link Event}.
     */
    public Event() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAllDayEvent() {
        return allDayEvent;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        this.allDayEvent = allDayEvent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<NewsItem> getNewsItem() {
        return newsItem;
    }

    public void setNewsItem(List<NewsItem> newsItem) {
        this.newsItem = newsItem;
    }

    public UserAccount getOriginator() {
        return originator;
    }

    public void setOriginator(UserAccount originator) {
        this.originator = originator;
    }

    /**
     * Determines if the start and end day are the same.
     *
     * @return <code>true</code> if the start and end day are the same,
     *         otherwise <code>false</code>
     */
    public boolean isStartAndEndSameDay() {

        if (getStartDate() == null || getEndDate() == null) {
            return false;
        }

        int startYear = getStartDate().get(Calendar.YEAR);
        int startMonth = getStartDate().get(Calendar.MONTH);
        int startDay = getStartDate().get(Calendar.DAY_OF_MONTH);

        int endYear = getEndDate().get(Calendar.YEAR);
        int endMonth = getEndDate().get(Calendar.MONTH);
        int endDay = getEndDate().get(Calendar.DAY_OF_MONTH);

        if (startYear == endYear && startMonth == endMonth && startDay == endDay) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the event is assigned to anyone.
     *
     * @return <code>true</code> if the event is assigned, otherwise
     *         <code>false</code>
     */
    public boolean isAssigned() {
        if (this.newsItem == null) {
            return false;
        }

        if (this.newsItem.size() > 0) {
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
        final Event other = (Event) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + this.id + "]";
    }
}
