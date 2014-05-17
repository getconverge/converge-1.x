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
package dk.i2m.converge.core.contacts;

import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.utils.BeanComparator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Entity containing information about a contact person.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "contact")
@NamedQueries({
    @NamedQuery(name = Contact.FIND_BY_NAME, query = "SELECT c FROM Contact AS c WHERE c.firstName LIKE :name OR c.lastName LIKE :name OR CONCAT(CONCAT(c.firstName, ' '),c.lastName) LIKE :name OR CONCAT(CONCAT(c.lastName, ' '),c.firstName) LIKE :name ORDER BY c.lastName ASC"),
    @NamedQuery(name = Contact.FIND_BY_ORGANISATION, query = "SELECT c FROM Contact AS c WHERE c.organisation = :organisation")
})
public class Contact implements Serializable {

    public static final String FIND_BY_NAME = "Contact.findByName";

    public static final String FIND_BY_ORGANISATION = "Contact.findByOrganisation";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName = "";

    @Column(name = "last_name")
    private String lastName = "";

    @Column(name = "organisation") @Lob
    private String organisation = "";

    @Column(name = "note") @Lob
    private String note = "";

    @Column(name = "job_title")
    private String jobTitle = "";

    @Column(name = "title")
    private String title = "";

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Calendar created = null;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Calendar updated = null;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserAccount createdBy = null;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private UserAccount updatedBy = null;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
    @OrderBy(value = "displayOrder ASC")
    @PrivateOwned
    private List<ContactPhone> phones = new ArrayList<ContactPhone>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<ContactEmail> emails = new ArrayList<ContactEmail>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<ContactAddress> addresses = new ArrayList<ContactAddress>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ContactAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ContactAddress> addresses) {
        this.addresses = addresses;
    }

    public List<ContactEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<ContactEmail> emails) {
        this.emails = emails;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public List<ContactPhone> getPhones() {
        return phones;
    }

    public void setPhones(List<ContactPhone> phones) {
        this.phones = phones;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getUpdated() {
        return updated;
    }

    public void setUpdated(Calendar updated) {
        this.updated = updated;
    }

    public UserAccount getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserAccount createdBy) {
        this.createdBy = createdBy;
    }

    public UserAccount getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserAccount updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Gets the primary phone number of the {@link Contact}. The primary phone
     * number is determined by the display order.
     *
     * @return Primary number of the {@link Contact}
     */
    public ContactPhone getPrimaryPhone() {
        ContactPhone candidate = null;
        for (ContactPhone cp : getPhones()) {
            if (candidate == null) {
                candidate = cp;
            } else if (candidate.getDisplayOrder() > cp.getDisplayOrder()) {
                candidate = cp;
            }
        }

        return candidate;
    }

    /**
     * Adds a phone number to the {@link Contact}. This method insures that the
     * {@code displayOrder} is incremented.
     *
     * @param phone
     *          {@link ContactPhone} to add
     * @return The added {@link ContactPhone}
     */
    public ContactPhone addPhone(ContactPhone phone) {
        int lastDisplayOrder = 1;

        for (ContactPhone cp : getPhones()) {
            if (lastDisplayOrder < cp.getDisplayOrder()) {
                lastDisplayOrder = cp.getDisplayOrder();
            }
        }
        phone.setDisplayOrder(++lastDisplayOrder);
        phone.setContact(this);
        getPhones().add(phone);
        return phone;
    }

    /**
     * Moves a {@link ContactPhone} up or down in the order of display.
     *
     * @param direction
     *          Direction to move the {@link ContactPhone}, {@code true} is down
     *          and {@code false is up}
     * @param phone
     *          {@link ContactPhone} to move
     */
    public void movePhone(boolean direction, ContactPhone phone) {
        Collections.sort(getPhones(), new BeanComparator("displayOrder"));

        int currentOrder = phone.getDisplayOrder();
        int newOrder = -1;
        int[] orders = new int[getPhones().size()];
        int pos = 0;

        for (ContactPhone p : getPhones()) {
            orders[pos++] = p.getDisplayOrder();
        }

        if (direction) {
            for (int displayOrder : orders) {
                if (newOrder == -1 && displayOrder > currentOrder) {
                    newOrder = displayOrder;
                } else if (displayOrder < newOrder && displayOrder > currentOrder) {
                    newOrder = displayOrder;
                }
            }
        } else {
            for (int displayOrder : orders) {
                if (newOrder == -1 && displayOrder < currentOrder) {
                    newOrder = displayOrder;
                } else if (displayOrder > newOrder && displayOrder < currentOrder) {
                    newOrder = displayOrder;
                }
            }
        }
        if (newOrder != -1) {

            for (ContactPhone p : getPhones()) {
                if (p.equals(phone)) {
                    p.setDisplayOrder(newOrder);
                } else if (p.getDisplayOrder() == newOrder) {
                    p.setDisplayOrder(currentOrder);
                }
            }

            Collections.sort(getPhones(), new BeanComparator("displayOrder"));
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
        final Contact other = (Contact) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
