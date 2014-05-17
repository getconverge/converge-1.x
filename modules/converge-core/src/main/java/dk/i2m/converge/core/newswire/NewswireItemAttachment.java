/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
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
package dk.i2m.converge.core.newswire;

import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.Rendition;
import java.io.File;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Attachment of a {@link NewswireItem}.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "newswire_item_attachment")
public class NewswireItemAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name") @Lob
    private String name = "";

    @Column(name = "description") @Lob
    private String description = "";

    @ManyToOne
    @JoinColumn(name = "newswire_item_id")
    private NewswireItem newswireItem;

    /** Name of the file. */
    @Column(name = "file_name")
    private String filename = "";

    @ManyToOne
    @JoinColumn(name = "catalogue_id")
    private Catalogue catalogue;

    @Column(name = "catalogue_path") @Lob
    private String cataloguePath = "";

    /** Binary data contained in the file. */
    @Column(name = "file_binary_data") @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

    /** Content type of the file. */
    @Column(name = "file_content_type")
    private String contentType = "";

    /** Size (in bytes) of the file. */
    @Column(name = "file_size")
    private long size = 0L;

    /** Rendition of the attachment. */
    @ManyToOne
    @JoinColumn(name = "rendition_id")
    private Rendition rendition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NewswireItem getNewswireItem() {
        return newswireItem;
    }

    public void setNewswireItem(NewswireItem newswireItem) {
        this.newswireItem = newswireItem;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Catalogue getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(Catalogue catalogue) {
        this.catalogue = catalogue;
    }

    public String getCataloguePath() {
        return cataloguePath;
    }

    public void setCataloguePath(String cataloguePath) {
        this.cataloguePath = cataloguePath;
    }

    public Rendition getRendition() {
        return rendition;
    }

    public void setRendition(Rendition rendition) {
        this.rendition = rendition;
    }

    public String getCatalogueUrl() {
        if (isStoredInCatalogue()) {
            StringBuilder url = new StringBuilder();
            url.append(catalogue.getWebAccess()).append("/").append(getCataloguePath()).append("/").append(getFilename());
            return url.toString();
        } else {
            return "";
        }
    }

    /**
     * Gets the file location of the catalogue. If the attachment is not stored
     * in a catalogue an empty string is returned.
     * 
     * @return File location of the catalogue, or an empty string if the
     *         attachment is not stored in a catalogue
     */
    public String getCatalogueFileLocation() {
        if (isStoredInCatalogue()) {
            StringBuilder url = new StringBuilder();
            url.append(catalogue.getLocation());
            url.append(File.separator);
            url.append(getCataloguePath());
            url.append(File.separator);
            url.append(getFilename());
            return url.toString();
        } else {
            return "";
        }
    }

    /**
     * Determines if the file is stored in a catalogue ({@link #getCatalogue() }
     * or internally {@link #getData()}
     * 
     * @return {@code true} if the file is stored in a catalogue {@link #getCatalogue() }, or 
     *         {@code false} if the file is stored in {@link #getData()}
     */
    public boolean isStoredInCatalogue() {
        if (catalogue == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if the {@link Rendition} is set for the attachment.
     * 
     * @return {@code true} if the {@link Rendition} is set,
     *         otherwise {@code false}
     */
    public boolean isRenditionSet() {
        if (rendition == null) {
            return false;
        } else {
            return true;
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
        final NewswireItemAttachment other = (NewswireItemAttachment) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }
}
