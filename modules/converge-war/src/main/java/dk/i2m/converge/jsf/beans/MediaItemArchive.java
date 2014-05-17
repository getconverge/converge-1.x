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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import dk.i2m.jsf.JsfUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /MediaItemArchive.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class MediaItemArchive {

    private static final Logger LOG = Logger.getLogger(MediaItemArchive.class.getName());

    @EJB private CatalogueFacadeLocal catalogueFacade;

    /** {@link MediaItem} being displayed. */
    private MediaItem selectedMediaItem;

    /** Unique identifier of the {@link MediaItem} to display. */
    private Long id;

    /** {@link DataModel} showing the available {@link MediaItemRendition}s for the selected {@link MediaItem}. */
    private DataModel availableRenditions;

    /** {@link DataModel} showing where the selected {@link MediaItem} was used. */
    private DataModel usage;

    /**
     * Gets the unique identifier of the {@link MediaItem} displayed.
     * 
     * @return Unique identifier of the {@link MediaItem} displayed
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link MediaItem} to display. This method is 
     * used to initialise the item to display.
     * 
     * @param id
     *          Unique identifier of the {@link MediaItem} to display
     */
    public void setId(Long id) {
        this.id = id;

        if (this.id != null && id != null && selectedMediaItem == null) {
            try {
                selectedMediaItem = catalogueFacade.findMediaItemById(id);
                this.usage = new ListDataModel(catalogueFacade.getMediaItemUsage(id));
                this.availableRenditions = null;
            } catch (DataNotFoundException ex) {
                LOG.log(Level.SEVERE, ex.getMessage());
            }
        }
    }

    public MediaItem getSelectedMediaItem() {
        return selectedMediaItem;
    }

    public void setSelectedMediaItem(MediaItem selectedMediaItem) {
        this.selectedMediaItem = selectedMediaItem;
    }

    /**
     * Gets a {@link DataModel} containing the available {@link MediaItemRendition}s
     * for the {@link MediaItemArchive#selectedMediaItem}.
     * 
     * @return {@link DataModel} containing the available {@link MediaItemRendition}s
     */
    public DataModel getAvailableRenditions() {
        if (this.availableRenditions == null) {
            Catalogue catalogue = selectedMediaItem.getCatalogue();

            List<AvailableMediaItemRendition> availableMediaItemRenditions = new ArrayList<AvailableMediaItemRendition>();
            for (Rendition rendition : catalogue.getRenditions()) {
                try {
                    availableMediaItemRenditions.add(new AvailableMediaItemRendition(rendition, selectedMediaItem.findRendition(rendition)));
                } catch (RenditionNotFoundException rnfe) {
                    //availableMediaItemRenditions.add(new AvailableMediaItemRendition(rendition));
                }
            }

            availableRenditions = new ListDataModel(availableMediaItemRenditions);
        }
        return this.availableRenditions;
    }

    public DataModel getUsage() {
        return usage;
    }

    public class AvailableMediaItemRendition {

        private MediaItemRendition mediaItemRendition;

        private Rendition rendition;

        public AvailableMediaItemRendition(Rendition rendition, MediaItemRendition mediaItemRendition) {
            this.rendition = rendition;
            this.mediaItemRendition = mediaItemRendition;
        }

        public AvailableMediaItemRendition(Rendition rendition) {
            this(rendition, null);
        }

        public boolean isAvailable() {
            return this.mediaItemRendition != null;
        }

        public MediaItemRendition getMediaItemRendition() {
            return this.mediaItemRendition;
        }

        public Rendition getRendition() {
            return this.rendition;
        }
    }

    /**
     * Determines if the current user is an editor of the media repository.
     * 
     * @return {@code true} if the current user is an editor of the media repository, otherwise {@code false}
     */
    public boolean isEditor() {
        UserRole editorRole = getSelectedMediaItem().getCatalogue().getEditorRole();

        if (getUser().getUserRoles().contains(editorRole)) {
            return true;
        } else {
            return false;
        }
    }

    private UserAccount getUser() {
        final String valueExpression = "#{userSession.user}";
        return (UserAccount) JsfUtils.getValueOfValueExpression(valueExpression);
    }
}
