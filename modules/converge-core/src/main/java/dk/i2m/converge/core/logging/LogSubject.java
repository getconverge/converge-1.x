/*
 * Copyright (C) 2012 Interactive Media Management
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
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

/**
 * Subject of a give {@link LogEntry}. A {@link LogSubject} refers to the entity
 * in Converge for which a {@link LogEntry} is related.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "log_subject")
public class LogSubject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "entity")
    private String entity;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "link")
    private String link;

    @ManyToOne
    @JoinColumn(name="log_entry_id")
    private LogEntry logEntry;

    /**
     * Creates a new instance of {@link LogSubject}.
     */
    public LogSubject() {
        this("", "");
    }

    /**
     * Creates a new instance of {@link LogSubject}.
     *
     * @param entity   Unique name of the entity
     * @param entityId Unique identifier of the entity
     */
    public LogSubject(String entity, String entityId) {
        this(entity, entityId, "");
    }

    /**
     * Creates a new instance of {@link LogSubject}.
     *
     * @param entity   Unique name of the entity
     * @param entityId Unique identifier of the entity
     * @param link     Link to the entity
     */
    public LogSubject(String entity, String entityId, String link) {
        this.entity = entity;
        this.entityId = entityId;
        this.link = link;
    }

    /**
     * Gets the unique identifier of the subject.
     *
     * @return Unique identifier of the subject
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the subject.
     *
     * @param id Unique identifier of the subject
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the unique name of the entity. The name is typically the full name
     * of the entity class.
     *
     * @return Unique name of the entity.
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Sets the unique name of the entity. The name is typically the full name
     * of the entity class.
     *
     * @param entity Unique name of the entity.
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Determines if the the subject can be linked to.
     * <p/>
     * @return {@code true} if the subject can be linked to, otherwise
     *         {@code false}
     */
    public boolean isLinkAvailable() {
        return StringUtils.isNotBlank(link);
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof LogSubject)) {
            return false;
        }
        LogSubject other = (LogSubject) object;
        if ((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass() + "[id=" + id + "/]";
    }
}
