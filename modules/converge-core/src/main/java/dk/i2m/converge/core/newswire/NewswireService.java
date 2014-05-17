/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.newswire;

import dk.i2m.converge.core.plugin.NewswireDecoder;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * {@link Entity} representing a news feed to include in the newswire service.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "newswire_service")
@NamedQueries({
    @NamedQuery(name = NewswireService.RESET_PROCESSING, query = "UPDATE NewswireService ns SET ns.processing = false"),
    @NamedQuery(name = NewswireService.COUNT_SUBSCRIBERS, query = "select count(ns.subscribers) from NewswireService ns where ns.id=:id"),
    @NamedQuery(name = NewswireService.DELETE_EXPIRED_ITEMS, query = "DELETE FROM NewswireItem ni WHERE ni.newswireService.id=:id AND ni.date <= :expirationDate"),
    @NamedQuery(name = NewswireService.COUNT_ITEMS, query = "select count(ns.items) from NewswireService ns where ns.id=:id"),
    @NamedQuery(name = NewswireService.FIND_BY_STATUS, query = "SELECT ns FROM NewswireService ns WHERE ns.active = :active ORDER BY ns.source ASC")})
public class NewswireService implements Serializable {

    /**
     * Update query for removing all expired newswire items from a service. Parameters are {@code id} (ID of the newswire service) and {@code expirationDate} (Date of expiration).
     */
    public static final String DELETE_EXPIRED_ITEMS = "NewswireService.deleteExpiredItems";

    /** Query for resetting the processing status of all newswire services. */
    public static final String RESET_PROCESSING = "NewswireService.resetProcessing";
    
    public static final String COUNT_SUBSCRIBERS = "NewswireService.countSubscribers";

    public static final String COUNT_ITEMS = "NewswireService.countItems";

    public static final String FIND_BY_STATUS = "NewswireService.findByActive";

    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "source")
    private String source = "";

    @Column(name = "decoder_class") @Lob
    private String decoderClass = "";

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "last_fetch")
    private Calendar lastFetch;

    @Column(name = "days_to_keep")
    private Integer daysToKeep = 0;

    @OneToMany(mappedBy = "newswireService", fetch = FetchType.LAZY)
    @PrivateOwned
    private List<NewswireItem> items = new ArrayList<NewswireItem>();

    @ManyToMany(mappedBy = "newswireServices", fetch = FetchType.LAZY)
    private List<UserAccount> subscribers = new ArrayList<UserAccount>();

    @OneToMany(mappedBy = "newswireService", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @PrivateOwned
    private List<NewswireServiceProperty> properties = new ArrayList<NewswireServiceProperty>();

    @Column(name = "active")
    private boolean active = true;
    
    @Column(name = "processing")
    private boolean processing = false;

    @ManyToMany
    @JoinTable(name = "newswire_restriction",
    joinColumns = {@JoinColumn(referencedColumnName = "id", name = "newswire_service_id", nullable = false)},
    inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "user_role_id", nullable = false)})
    private List<UserRole> restrictedTo = new ArrayList<UserRole>();

    @Transient
    private Long numberOfSubscribers = 0L;

    @Transient
    private Long numberOfItems = 0L;

    @ManyToMany(mappedBy = "appliesTo", fetch = FetchType.LAZY)
    private List<NewswireBasket> baskets = new ArrayList<NewswireBasket>();
    
    @Column(name = "copyright")
    @Lob
    private String copyright = "";

    /**
     * Creates a new instance of {@link NewswireService}.
     */
    public NewswireService() {
    }

    /**
     * Creates a new instance of {@link NewswireService}.
     *
     * @param source
     *          Source of the {@link NewswireService}
     */
    public NewswireService(String source) {
        this.source = source;
    }

    /**
     * Gets the unique ID of the {@link NewswireService}.
     * 
     * @return Unique ID of the {@link NewswireService}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique ID of the {@link NewswireService}. This method should not be invoked manually as {@link NewFeed}s are
     * automatically assigned identifiers by the database.
     * 
     * @param id
     *          Unique ID of the {@link NewswireService}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the source of the {@link NewswireService}.
     *
     * @return Source of the {@link NewswireService}
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source of the {@link NewswireService}. The source appears next to each of the items in the feed. It would
     * typically be a read-friendly name.
     * 
     * @param source
     *          Source of the {@link NewswireService}
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the number of days to retain items for this service.
     * 
     * @return Number of days to retain items for this service
     */
    public Integer getDaysToKeep() {
        return daysToKeep;
    }

    /**
     * Sets the number of days to retain items for this service.
     * 
     * @param daysToKeep
     *          Number of days to retain items for this service
     */
    public void setDaysToKeep(Integer daysToKeep) {
        this.daysToKeep = daysToKeep;
    }

    /**
     * Gets the classname of the decoder used for the service.
     *
     * @return Classname of the decoder used for the service
     */
    public String getDecoderClass() {
        return decoderClass;
    }

    /**
     * Sets the classname of the decoder used for the service.
     *
     * @param decoderClass
     *          Classname of the decoder
     */
    public void setDecoderClass(String decoderClass) {
        this.decoderClass = decoderClass;
    }

    /**
     * Creates an instance of the decoder specified in {@link NewswireService#getDecoderClass()}.
     *
     * @return Instance of the decoder
     * @throws NewswireDecoderException
     *          If the decoder could not be instantiated
     */
    public NewswireDecoder getDecoder() throws NewswireDecoderException {
        try {
            Class c = Class.forName(getDecoderClass());
            NewswireDecoder decoder = (NewswireDecoder) c.newInstance();
            return decoder;
        } catch (ClassNotFoundException ex) {
            throw new NewswireDecoderException("Could not find connector: " + getDecoderClass(), ex);
        } catch (InstantiationException ex) {
            throw new NewswireDecoderException("Could not instantiate connector [" + getDecoderClass() + "]. Check to ensure that the decoder has a public contructor with no arguments", ex);
        } catch (IllegalAccessException ex) {
            throw new NewswireDecoderException("Could not access connector: " + getDecoderClass(), ex);
        }
    }

    public List<NewswireItem> getItems() {
        return items;
    }

    public void setItems(List<NewswireItem> newswireItems) {
        this.items = newswireItems;
    }

    public List<UserAccount> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<UserAccount> subscribers) {
        this.subscribers = subscribers;
    }

    public Calendar getLastFetch() {
        return lastFetch;
    }

    public void setLastFetch(Calendar lastFetch) {
        this.lastFetch = lastFetch;
    }

    public List<NewswireServiceProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<NewswireServiceProperty> properties) {
        this.properties = properties;
    }

    public List<UserRole> getRestrictedTo() {
        return restrictedTo;
    }

    public void setRestrictedTo(List<UserRole> restrictedTo) {
        this.restrictedTo = restrictedTo;
    }

    /**
     * Determines if the newswire service is availble to
     * all users.
     * 
     * @return {@code true} if the {@link NewswireService} is
     *         available to all users, otherwise {@code false}
     */
    public boolean isPublic() {
        return restrictedTo.isEmpty();
    }

    /**
     * Determines if the {@link NewswireService} is active and should
     * be automatically fetched.
     * 
     * @return {@code true} if the {@link NewswireService} is active
     *         and should be automatically fetched, otherwise
     *         {@code false}
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Determines if the {@link NewswireService} is active and should
     * be automatically fetched.
     * 
     * @param active
     *         {@code true} if the {@link NewswireService} is active
     *         and should be automatically fetched, otherwise {@code false}
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Determines if the {@link NewswireService} is currently being processed.
     * 
     * @return {@code true} if the {@link NewswireService} is currently being 
     *         processed, otherwise {@code false}.
     */
    public boolean isProcessing() {
        return processing;
    }

    /**
     * Determines if the {@link NewswireService} is currently being processed.
     * 
     * @param processing {@code true} if the {@link NewswireService} is
     *                   currently being processed, otherwise {@code false}.
     */
    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public Long getNumberOfSubscribers() {
        return this.numberOfSubscribers;
    }

    public Long getNumberOfItems() {
        return this.numberOfItems;
    }

    public void setNumberOfItems(Long numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public void setNumberOfSubscribers(Long numberOfSubscribers) {
        this.numberOfSubscribers = numberOfSubscribers;
    }

    /**
     * Gets the copyright for items in this service.
     * 
     * @return Copyright notice for items in this service
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Sets the copyright notice for items in this service.
     * 
     * @param copyright Notice for items in this service
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
    
    /**
     * Gets a {@link Map} containing the properties of the service.
     *
     * @return {@link Map} containing the properties of the service
     */
    public Map<String, String> getPropertiesMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (NewswireServiceProperty property : this.properties) {
            map.put(property.getKey(), property.getValue());
        }

        return map;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NewswireService other = (NewswireService) obj;
        if (this.id != other.id
                && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }
}
