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
package dk.i2m.converge.core.search;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * Entry in the queue of items to index in the search engine.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "index_queue")
@NamedQueries({
    @NamedQuery(name = IndexQueueEntry.FIND_BY_TYPE_ID_AND_OPERATION, query = "SELECT e FROM IndexQueueEntry AS e WHERE e.entryId=:entryId AND e.type=:type AND e.operation=:operation")
})
public class IndexQueueEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FIND_BY_TYPE_ID_AND_OPERATION = "IndexQueueEntry.findByTypeIdAndOperation";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type")
    private QueueEntryType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_operation")
    private QueueEntryOperation operation;

    @Column(name = "entry_id")
    private Long entryId;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "added")
    private Date added;

    /**
     * Creates a new instance of {@link IndexQueueEntry}.
     */
    public IndexQueueEntry() {
        this(QueueEntryType.NEWS_ITEM, 0L, QueueEntryOperation.UPDATE);
    }

    /**
     * Creates a new instance if {@link IndexQueueEntry}.
     * 
     * @param type
     *          Type of entry
     * @param entryId 
     *          Unique identifier of entry
     * @param operation
     *          Type of operation to execute on the entry
     */
    public IndexQueueEntry(QueueEntryType type, Long entryId, QueueEntryOperation operation) {
        this.type = type;
        this.entryId = entryId;
        this.operation = operation;
        added = Calendar.getInstance().getTime();
    }

    /**
     * Gets the unique identifier of the queued item.
     * 
     * @return Unique identifier of the queued item
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the queued item.
     * 
     * @param id 
     *          Unique identifier of the queued item
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the date and time when the entry was added to the queue.
     * 
     * @return Date and time when the entry was added to the queue
     */
    public Date getAdded() {
        return added;
    }

    /**
     * Sets the date and time when the entry was added to the queue.
     * 
     * @param added
     *          Date and time when the entry was added to the queue
     */
    public void setAdded(Date added) {
        this.added = added;
    }

    /**
     * Gets the unique identifier of the entry to index.
     * 
     * @return Unique identifier of the entry to index
     */
    public Long getEntryId() {
        return entryId;
    }

    /**
     * Sets the unique identifier of the entry to index.
     * 
     * @param entryId 
     *          Unique identifier of the entry to index
     */
    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    /**
     * Gets the type of entity to index.
     * 
     * @return Type of entity to index
     */
    public QueueEntryType getType() {
        return type;
    }

    /**
     * Sets the type of entity to index.
     * 
     * @param type
     *          Type of entity to index
     */
    public void setType(QueueEntryType type) {
        this.type = type;
    }

    /**
     * Operation to perform.
     * 
     * @return Operation to perform on the entry
     */
    public QueueEntryOperation getOperation() {
        return operation;
    }

    /**
     * Operation to perform.
     * 
     * @param operation 
     *          Operation to perform on the entry
     */
    public void setOperation(QueueEntryOperation operation) {
        this.operation = operation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IndexQueueEntry other = (IndexQueueEntry) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.entryId != other.entryId && (this.entryId == null || !this.entryId.equals(other.entryId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 79 * hash + (this.entryId != null ? this.entryId.hashCode() : 0);
        return hash;
    }
}
