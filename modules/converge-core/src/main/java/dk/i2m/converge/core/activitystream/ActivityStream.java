/*
 * Copyright (C) 2014 Allan Lykke Christensen
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
package dk.i2m.converge.core.activitystream;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * An activity stream is a collection of {@linkplain Activity activities}.
 *
 * @author Allan Lykke Christensen
 */
@XmlType
@XmlRootElement(name = "activityStream")
public class ActivityStream {

    private Long totalItems;
    private List<Activity> items;
    private String baseUrl;

    /**
     * Creates a new instance of {@link ActivityStream}.
     */
    public ActivityStream() {
        this(new ArrayList<Activity>(), 0L, "");
    }

    /**
     * Creates a new initialized instance of {@link ActivityStream}.
     *
     * @param items Collection of {@linkplain Activity activities}. This can be
     * a subset of all the activities for a user
     * @param totalItems Non-negative integer specifying the total number of
     * activities within the stream
     * @param baseUrl
     */
    public ActivityStream(List<Activity> items, Long totalItems, String baseUrl) {
        this.items = items;
        this.totalItems = totalItems;
        this.baseUrl = baseUrl;
    }

    /**
     * Collection of {@linkplain Activity activities}. This can be a subset of
     * all the activities for a user.
     *
     * @return {@link List} of {@linkplain Activity activities} for a user
     */
    @XmlElement(name = "items")
    public List<Activity> getItems() {
        return items;
    }

    /**
     * Collection of {@linkplain Activity activities}. This can be a subset of
     * all the activities for a user.
     *
     * @param items {@link List} of {@linkplain Activity activities} for a user
     */
    public void setItems(List<Activity> items) {
        this.items = items;
    }

    /**
     * Adds an {@link Activity} to the list of {@linkplain Activity activities}.
     *
     * @param activity {@link Activity} to add to the stream
     */
    public void addItem(Activity activity) {
        activity.setUrl(getBaseUrl().concat(activity.getUrl()));
        getItems().add(activity);
    }

    /**
     * Non-negative integer specifying the total number of activities within the
     * stream.
     *
     * @return Non-negative integer specifying the total number of activities
     * within the stream
     */
    @XmlElement(name = "totalItems")
    public Long getTotalItems() {
        return totalItems;
    }

    /**
     * Sets the total number of activities within the stream.
     *
     * @param totalItems Non-negative integer specifying the total number of
     * activities within the stream
     */
    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Base URL of the activity links. This is appended in front of all activity
     * links in the activity stream using the {@link #addItem(dk.i2m.converge.core.activitystream.Activity)
     * } method.
     *
     * @return Base URL of the activity links
     */
    @XmlTransient
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Base URL of the activity links. This is appended in front of all activity
     * links in the activity stream using the {@link #addItem(dk.i2m.converge.core.activitystream.Activity)
     * } method.
     *
     * @param baseUrl Base URL to add to all the activity links
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
