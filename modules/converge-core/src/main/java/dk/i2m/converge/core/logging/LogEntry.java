/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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
package dk.i2m.converge.core.logging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 * General purpose log entry from a {@link dk.i2m.converge.core.plugin.Plugin}
 * or content item.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "log_entry")
@NamedQueries({
    @NamedQuery(name = LogEntry.FIND_BY_ENTITY,
    query =
    "SELECT l FROM LogEntry l JOIN l.subjects s WHERE s.entity = :"
    + LogEntry.PARAMETER_ENTITY + " AND s.entityId = :"
    + LogEntry.PARAMETER_ENTITY_ID + " ORDER BY l.date DESC")
})
public class LogEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Query for finding log entries by a given {@link LogEntry#PARAMETER_ENTITY entity} and {@link LogEntry#PARAMETER_ENTITY_ID entityId}.
     */
    public static final String FIND_BY_ENTITY = "LogEntry.findByEntity";

    /** Parameter used to specify the entity. */
    public static final String PARAMETER_ENTITY = "entity";

    /** Parameter used to specify the unique ID of the entity. */
    public static final String PARAMETER_ENTITY_ID = "entityId";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private LogSeverity severity;

    @Column(name = "description") @Lob
    private String description = "";

    @Column(name = "log_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date date;

    @OneToMany(mappedBy = "logEntry", cascade = CascadeType.ALL)
    private List<LogSubject> subjects = new ArrayList<LogSubject>();

    /**
     * Creates a new instance of {@link LogEntry}.
     */
    public LogEntry() {
        this(LogSeverity.INFO, "");
    }

    /**
     * Creates a new {@link LogEntry}.
     *
     * @param severity    {@link LogSeverity} of the {@link LogEntry}
     * @param description Description of the {@link LogEntry}
     */
    public LogEntry(LogSeverity severity, String description) {
        this(severity, description, null, null);
    }

    /**
     * Creates a new {@link LogEntry}.
     *
     * @param severity    {@link LogSeverity} of the {@link LogEntry}
     * @param description Description of the {@link LogEntry}
     * @param entity      Entity relating to the {@link LogEntry}
     * @param entityId    Identifier of the entity
     */
    public LogEntry(LogSeverity severity, String description, String entity,
            String entityId) {
        this.severity = severity;
        this.description = description;
        if (entity != null) {
            addSubject(entity, entityId);
        }
    }

    /**
     * Gets the unique identifier of the entry.
     *
     * @return Unique identifier of the entry
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the entry.
     *
     * @param id Unique identifier of the entry
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the date and time of the entry.
     *
     * @return Date and time of the entry
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date and time of the entry.
     *
     * @param date Date and time of the entry
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the description / content of the entry.
     *
     * @return Description / content of the entry
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description / content of the entry.
     *
     * @param description Description / content of the entry
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the severity of the entry.
     *
     * @return Severity of the entry
     */
    public LogSeverity getSeverity() {
        return severity;
    }

    /**
     * Sets the severity of the entry.
     *
     * @param severity Severity of the entry
     */
    public void setSeverity(LogSeverity severity) {
        this.severity = severity;
    }

    /**
     * Gets a {@link List} of subjects relating to the {@link LogEntry}.
     *
     * @return {@link List} of subjects relating to the {@link LogEntry}
     */
    public List<LogSubject> getSubjects() {
        return subjects;
    }

    /**
     * Adds an entity that relates to the {@link LogEntry}.
     *
     * @param subject {@link LogSubject} representing the entity
     */
    public void addSubject(LogSubject subject) {
        subject.setLogEntry(this);
        subjects.add(subject);
    }

    /**
     * Adds an entity that relates to the {@link LogEntry}.
     *
     * @param entity   Entity relating to the {@link LogEntry}
     * @param entityId Unique ID of the entity
     */
    public final void addSubject(String entity, String entityId) {
        addSubject(new LogSubject(entity, entityId));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof LogEntry)) {
            return false;
        }
        LogEntry other = (LogEntry) object;
        if ((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }
}
