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
package dk.i2m.converge.core;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * {@link Entity} representing a configuration that is stored in the database.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "config", uniqueConstraints =
@UniqueConstraint(columnNames = {"config_key"}))
@NamedQueries({
    @NamedQuery(name = Configuration.FIND_BY_KEY, query = "SELECT c FROM Configuration c WHERE c.key=:cfgKey")
})
public class Configuration implements Serializable {

    private static final long serialVersionUID = 2L;

    /** Query for finding a configuration setting by its unique key. */
    public static final String FIND_BY_KEY = "Configuration.findByKey";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key")
    @Enumerated(EnumType.STRING)
    private ConfigurationKey key;

    @Column(name = "config_value") @Lob
    private String value;

    /**
     * Creates a new instance of {@link Configuration}.
     */
    public Configuration() {
    }

    /**
     * Gets the unique identifier of the configuration. The unique identifier is
     * automatically generated and does not have any meaning in relation to the
     * key and value.
     *
     * @return Unique identifier of the configuration
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the configuration.
     *
     * @param id
     *          Unique identifier of the configuration
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the key of the configuration setting. The key describes the value of
     * the setting.
     *
     * @return Key of the configuration setting
     */
    public ConfigurationKey getKey() {
        return key;
    }

    /**
     * Sets the key of the configuration setting.
     *
     * @param key
     *          Key of the configuration setting
     */
    public void setKey(ConfigurationKey key) {
        this.key = key;
    }

    /**
     * Gets the value of the configuration setting.
     *
     * @return Value of the configuration setting
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the configuration setting.
     *
     * @param value
     *          Value of the configuration setting
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Configuration)) {
            return false;
        }
        Configuration other = (Configuration) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }
}
