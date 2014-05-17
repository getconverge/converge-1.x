/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.core.content.catalogue;

import dk.i2m.converge.core.newswire.NewswireItemAttachment;
import dk.i2m.converge.core.security.UserRole;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.*;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * {@link Catalogue}s contains {@link MediaItem}s. A {@link Catalogue}
 * defines the list of available {@link Rendition}s for the {@link MediaItem}s
 * as well as a {@link Rendition} used for previewing items in the {@link Catalogue}.
 * 
 * {@link MediaItemRendition}s are stored in a hierarchy based on the date of the
 * {@link MediaItem}: {@code LOCATION/YEAR/MONTH/DAY/MEDIA_ITEM_ID/RENDITION_ID.EXT}.
 *
 * @author Allan Lykke Christensen
 */
@Entity()
@Table(name = "catalogue")
@NamedQueries({
    @NamedQuery(name = Catalogue.FIND_BY_USER, query = "SELECT c FROM Catalogue c JOIN c.userRole u JOIN c.editorRole e JOIN u.userAccounts a JOIN e.userAccounts AS editors WHERE c.enabled=true AND (a = :" + Catalogue.PARAM_FIND_BY_USER_USER + " OR editors = :" + Catalogue.PARAM_FIND_BY_USER_USER + ")"),
    @NamedQuery(name = Catalogue.FIND_ENABLED, query = "SELECT c FROM Catalogue c WHERE c.enabled=true"),
    @NamedQuery(name = Catalogue.FIND_WRITABLE, query = "SELECT c FROM Catalogue c WHERE c.readOnly=false AND c.enabled=true")
})
public class Catalogue implements Serializable {

    /** Query for finding the list of catalogues for a given user. */
    public static final String FIND_BY_USER = "Catalogue.findByUser";
    
    /** Query for finding the list of enabled catalogues. */
    public static final String FIND_ENABLED = "Catalogue.findEnabled";

    /** Query for finding the list of writable catalogues. */
    public static final String FIND_WRITABLE = "Catalogue.findWritable";

    /** Parameter used in the {@link Catalogue#FIND_BY_USER} query. */
    public static final String PARAM_FIND_BY_USER_USER = "user";

    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name = "";

    @Column(name = "description") @Lob
    private String description = "";

    @Column(name = "location") @Lob
    private String location = "";

    @Column(name = "web_access") @Lob
    private String webAccess = "";

    @Column(name = "watch_location") @Lob
    private String watchLocation = "";

    @Column(name = "read_only")
    private boolean readOnly = false;

    @Column(name = "enabled")
    private boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "preview_rendition")
    private Rendition previewRendition;

    @ManyToOne
    @JoinColumn(name = "original_rendition")
    private Rendition originalRendition;

    @Column(name = "last_index")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Calendar lastIndex;

    @Column(name = "last_item_count")
    private int itemCount = 0;

    @ManyToOne
    @JoinColumn(name = "editor_role_id")
    private UserRole editorRole;
    
    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;
    
    @Column(name = "max_file_upload_size")
    private Long maxFileUploadSize;

    @OneToMany(mappedBy = "catalogue")
    private List<NewswireItemAttachment> newswireItemAttachments;

    @OneToMany(mappedBy = "catalogue", fetch= FetchType.EAGER)
    @PrivateOwned
    private List<CatalogueHookInstance> hooks = new ArrayList<CatalogueHookInstance>();

    @ManyToMany
    @JoinTable(name = "catalogue_rendition",
    joinColumns = {@JoinColumn(referencedColumnName = "id", name = "catalogue_id", nullable = false)},
    inverseJoinColumns = {@JoinColumn(referencedColumnName = "id", name = "rendition_id", nullable = false)})
    private List<Rendition> renditions = new ArrayList<Rendition>();

    /**
     * Creates a new instance of {@link Catalogue}.
     */
    public Catalogue() {
        this.maxFileUploadSize = 0L;
    }

    /**
     * Gets the unique identifier of the {@link Catalogue}.
     * 
     * @return Unique identifier of the {@link Catalogue}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link Catalogue}.
     * 
     * @param id
     *          Unique identifier of the {@link Catalogue}
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Calendar getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(Calendar lastIndex) {
        this.lastIndex = lastIndex;
    }

    /**
     * Gets the location of the {@link Catalogue} folder. The {@link Catalogue}
     * folder contains the {@link MediaItem}s stored in the {@link Catalogue}.
     * 
     * @return Path to the {@link Catalogue} folder
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWatchLocation() {
        return watchLocation;
    }

    public void setWatchLocation(String watchLocation) {
        this.watchLocation = watchLocation;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * Get the {@link UserRole} of the editor of the {@link Catalogue}. Only
     * users with this {@link UserRole} can approve items for this 
     * {@link Catalogue}.
     * 
     * @return {@link UserRole} of the editor
     */
    public UserRole getEditorRole() {
        return editorRole;
    }

    /**
     * Set the {@link UserRole} of the editor of the {@link Catalogue}. Only
     * users with this {@link UserRole} can approve items for this 
     * {@link Catalogue}.
     * 
     * @param editorRole
     *          {@link UserRole} of editors who should be able to approve items
     */
    public void setEditorRole(UserRole editorRole) {
        this.editorRole = editorRole;
    }

    /**
     * Get the {@link UserRole} of users who should be able to submit items
     * to this {@link Catalogue}. Users without this {@link UserRole} cannot
     * submit items to the {@link Catalogue}.
     * 
     * @return {@link UserRole} of users who should be able to submit items
     */
    public UserRole getUserRole() {
        return userRole;
    }

    /**
     * Set the {@link UserRole} of users who should be able to submit items
     * to this {@link Catalogue}. Users without this {@link UserRole} cannot
     * submit items to the {@link Catalogue}.
     * 
     * @param userRole
     *          {@link UserRole} of users who should be able to submit items
     */
    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getWebAccess() {
        return webAccess;
    }

    public void setWebAccess(String webAccess) {
        this.webAccess = webAccess;
    }

    /**
     * Gets the {@link Rendition} used for showing
     * previews of {@link MediaItem}s in this catalogue.
     * 
     * @return {@link Rendition} used for showing previews
     */
    public Rendition getPreviewRendition() {
        return previewRendition;
    }

    /**
     * Sets the {@link Rendition} used for showing
     * previews of {@link MediaItem}s in this catalogue.
     * 
     * @param previewRendition
     *          {@link Rendition} used for showing previews 
     *          of {@link MediaItem}s in this catalogue
     */
    public void setPreviewRendition(Rendition previewRendition) {
        this.previewRendition = previewRendition;
    }

    /**
     * Gets the {@link Rendition} used for the original
     * {@link MediaItem} rendition in this catalogue.
     * 
     * @return Original {@link Rendition} of {@link MediaItem}s
     *         in this catalogue
     */
    public Rendition getOriginalRendition() {
        return originalRendition;
    }

    /**
     * Sets the {@link Rendition} used for the original
     * {@link MediaItem} rendition in this catalogue.
     * 
     * @param originalRendition 
     *          Original {@link Rendition} of {@link MediaItem}s
     *          in this catalogue
     */
    public void setOriginalRendition(Rendition originalRendition) {
        this.originalRendition = originalRendition;
    }

    /**
     * Gets the {@link CatalogueHookInstance}s defined for this
     * {@link Catalogue}.
     * 
     * @return {@link List} of {@link CatalogueHookInstance}s defined
     *         for this {@link Catalogue}
     */
    public List<CatalogueHookInstance> getHooks() {
        return hooks;
    }

    /**
     * Sets the {@link CatalogueHookInstance}s for this
     * {@link Catalogue}.
     * 
     * @param hooks
     *          {@link List} of {@link CatalogueHookInstance}s for
     *          this {@link Catalogue}
     */
    public void setHooks(List<CatalogueHookInstance> hooks) {
        this.hooks = hooks;
    }

    /**
     * Gets the available {@link Rendition}s for this catalogue.
     * 
     * @return {@link List} of available {@link Rendition}s for the
     *         {@link Catalogue}
     */
    public List<Rendition> getRenditions() {
        return renditions;
    }

    /**
     * Sets the available {@link Rendition}s of for the {@link Catalogue}.
     * 
     * @param renditions
     *          {@link List} of {@link Rendition}s for the {@link Catalogue}
     */
    public void setRenditions(List<Rendition> renditions) {
        this.renditions = renditions;
    }

    /**
     * Gets the maximum allowed file size (KB) for this {@link Catalogue}. A 
     * value of 0 indicates that there is no upload limit.
     * 
     * @return Maximum size of file upload in kilobytes.
     */
    public Long getMaxFileUploadSize() {
        return maxFileUploadSize;
    }

    /**
     * Sets the maximum allowed file size (KB) for this {@link Catalogue}.
     * 
     * @param maxFileUploadSize 
     *          Maximum size of file upload in kilobytes allowed. 0 indicates
     *          that there is no upload limit
     */
    public void setMaxFileUploadSize(Long maxFileUploadSize) {
        this.maxFileUploadSize = maxFileUploadSize;
    }
    
    /**
     * Gets the space (bytes) available in the {@link Catalogue}
     * {@link Catalogue#location}.
     * 
     * @return Bytes available in the {@link Catalogue} or
     *         {@code -1} if the {@link Catalogue#location} is
     *         not valid
     */
    public Long getSpaceAvailable() {
        if (isCatalogueLocationValid()) {
            File f = new File(getLocation());
            return f.getUsableSpace();
        } else {
            return -1L;
        }
    }
    
    /**
     * Gets the total space (bytes) in the {@link Catalogue}
     * {@link Catalogue#location}.
     * 
     * @return Bytes of space in the {@link Catalogue} or
     *         {@code -1} if the {@link Catalogue#location} is
     *         not valid
     */
    public Long getSpace() {
        if (isCatalogueLocationValid()) {
            File f = new File(getLocation());
            return f.getTotalSpace();
        } else {
            return -1L;
        }
    }
    
    /**
     * Determines if the {@link Catalogue#location} of the 
     * {@link Catalogue} is a directory and whether files can
     * be written and read from the directory.
     * 
     * @return {@code true} if the {@link Catalogue#location} of
     *         the catalogue is valid, otherwise {@code false}
     */
    public boolean isCatalogueLocationValid() {
        boolean status = true;
        File f = new File(getLocation());
        
        // Location must be a directory
        if (!f.isDirectory()) {
            status = false;
        }
        
        // Must be possible to read from the directory
        if (!f.canRead()) {
            status = false;
        }
        
        // If the location is read/write, it must be possible
        // to write to the location
        if (!isReadOnly() && !f.canWrite()) {
            status = false;
        }
        
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Catalogue other = (Catalogue) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
