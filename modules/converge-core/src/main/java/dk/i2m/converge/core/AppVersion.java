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
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity containing upgrade and migration history.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "app_version")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = AppVersion.FIND_BY_FROM_VERSION, query = "SELECT a FROM AppVersion a WHERE a.fromVersion = :fromVersion"),
    @NamedQuery(name = AppVersion.FIND_BY_TO_VERSION, query = "SELECT a FROM AppVersion a WHERE a.toVersion = :toVersion"),
    @NamedQuery(name = AppVersion.FIND_BY_MIGRATED, query = "SELECT a FROM AppVersion a WHERE a.migrated = :migrated"),
    @NamedQuery(name = AppVersion.FIND_BY_MIGRATION_AVAILABLE, query = "SELECT a FROM AppVersion a WHERE a.migrated = false AND a.fromVersion = :fromVersion"),
    @NamedQuery(name = AppVersion.FIND_LATEST_MIGRATIONS, query = "SELECT a FROM AppVersion a WHERE a.migrated = true ORDER BY a.migratedDate DESC")})
public class AppVersion implements Serializable {

    public static final String FIND_BY_FROM_VERSION = "AppVersion.findByFromVersion";

    public static final String FIND_BY_TO_VERSION = "AppVersion.findByToVersion";

    public static final String FIND_BY_MIGRATED = "AppVersion.findByMigrated";

    public static final String FIND_BY_MIGRATION_AVAILABLE = "AppVersion.findByMigrationAvailable";

    public static final String FIND_LATEST_MIGRATIONS = "AppVersion.findByLatestMigrations";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Basic(optional = false)
    @Column(name = "from_version")
    private String fromVersion;

    @Basic(optional = false)
    @Column(name = "to_version")
    private String toVersion;

    @Basic(optional = false)
    @Column(name = "migrated")
    private boolean migrated;

    @Column(name = "migrated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date migratedDate;

    public AppVersion() {
    }

    public AppVersion(Long id) {
        this.id = id;
    }

    public AppVersion(Long id, String fromVersion, String toVersion, boolean migrated) {
        this.id = id;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.migrated = migrated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    public String getToVersion() {
        return toVersion;
    }

    public void setToVersion(String toVersion) {
        this.toVersion = toVersion;
    }

    public boolean getMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

    public Date getMigratedDate() {
        return migratedDate;
    }

    public void setMigratedDate(Date migratedDate) {
        this.migratedDate = migratedDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AppVersion)) {
            return false;
        }
        AppVersion other = (AppVersion) object;
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
