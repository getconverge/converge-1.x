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
package dk.i2m.converge.core;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.*;

/**
 * Entity representing a background task. This entity is purely for
 * information and does not contain any logic for managing or
 * running background tasks.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "background_task")
public class BackgroundTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "task_start")
    private Date taskStart;

    /**
     * Creates a new instance of {@link BackgroundTask}.
     */
    public BackgroundTask() {
    }

    /**
     * Gets the unique identifier of the task.
     *
     * @return Unique identifier of the task
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the task.
     *
     * @param id Unique identifier of the task
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the task.
     *
     * @return Name of the task
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the task.
     *
     * @param name Name of the task
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the date and time when the task started.
     *
     * @return Date and time when the task started
     */
    public Date getTaskStart() {
        return taskStart;
    }

    /**
     * Sets the date and time when the task started.
     *
     * @param taskStart Date and time when the task started
     */
    public void setTaskStart(Date taskStart) {
        this.taskStart = taskStart;
    }

    /**
     * Calculate for how long the background tasks has been running.
     *
     * @return Duration of the task in milliseconds
     */
    public Long getDuration() {
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(taskStart);

        return now.getTimeInMillis() - start.getTimeInMillis();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BackgroundTask)) {
            return false;
        }
        BackgroundTask other = (BackgroundTask) object;
        if ((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }
}
