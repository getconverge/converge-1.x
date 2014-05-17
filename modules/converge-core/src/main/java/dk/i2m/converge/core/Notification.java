/*
 *  Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.core;

import dk.i2m.converge.core.security.UserAccount;
import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * {@link Notification} to a {@link UserAccount}. The purpose of this entity is
 * to contain a message for a give {@link UserAccount}. {@link Notification}s
 * are persistent and must be actively closed by the recipient
 * {@link UserAccount} to be removed from the database.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "notification")
@NamedQueries({
    @NamedQuery(name = Notification.FIND_BY_USERNAME, query = "SELECT n FROM Notification n WHERE n.recipient.username = :username ORDER BY n.added DESC"),
    @NamedQuery(name = Notification.COUNT_BY_USERNAME, query = "SELECT COUNT(n) FROM Notification n WHERE n.recipient.username = :username")
})
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Query for finding all notifications for a particular user by {@code username}. */
    public static final String FIND_BY_USERNAME = "Notification.findByUsername";

    /** Query for getting the count of notifications for a particular user by {@code username}. */
    public static final String COUNT_BY_USERNAME = "Notification.countByUsername";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "message") @Lob
    private String message;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "added")
    private Calendar added;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserAccount recipient;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserAccount sender;

    @Column(name = "link")
    private String link;

    public Notification() {
        this("", null);
    }

    public Notification(String message, UserAccount recipient) {
        this.message = message;
        this.recipient = recipient;
        this.added = Calendar.getInstance();
    }
    
    public Notification(String message, String link, UserAccount recipient, UserAccount sender) {
        this.message = message;
        this.link = link;
        this.recipient = recipient;
        this.sender = sender;
        this.added = Calendar.getInstance();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
    public boolean isLinkSet() {
        if (link == null || link.trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public UserAccount getSender() {
        return sender;
    }

    public void setSender(UserAccount sender) {
        this.sender = sender;
    }

    public UserAccount getRecipient() {
        return recipient;
    }

    public void setRecipient(UserAccount recipient) {
        this.recipient = recipient;
    }

    public Calendar getAdded() {
        return added;
    }

    public void setAdded(Calendar added) {
        this.added = added;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + this.id + "]";
    }
}
