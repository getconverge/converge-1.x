/*
 * Copyright (C) 2010 - 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.newswire.NewswireItemAttachment;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import dk.i2m.converge.ejb.services.NewswireServiceLocal;
import dk.i2m.jsf.JsfUtils;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

/**
 * JSF backing bean for {@code /NewswireItem.jspx}
 *
 * @author Allan Lykke Christensen
 */
public class NewswireItem {

    private static final Logger LOG = Logger.getLogger(NewswireItem.class.
            getName());

    @EJB private NewswireServiceLocal newswireService;

    @EJB private CatalogueFacadeLocal catalogueFacade;

    private Long id = 0L;

    private dk.i2m.converge.core.newswire.NewswireItem selectedItem = null;

    private Catalogue importCatalogue = null;

    public NewswireItem() {
    }

    @PostConstruct
    public void init() {
        // Set the default catalogue
        Catalogue defaultCatalogue = getUser().getDefaultMediaRepository();
        if (defaultCatalogue != null) {
            importCatalogue = defaultCatalogue;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;

        if (selectedItem == null || (selectedItem.getId() != id)) {
            try {
                this.selectedItem = newswireService.findNewswireItemById(id);
            } catch (DataNotFoundException ex) {
                this.selectedItem = null;
            }
        }
    }

    public dk.i2m.converge.core.newswire.NewswireItem getSelectedItem() {
        return selectedItem;
    }

    public Catalogue getImportCatalogue() {
        return importCatalogue;
    }

    public void setImportCatalogue(Catalogue importCatalogue) {
        this.importCatalogue = importCatalogue;
    }

    private UserAccount getUser() {
        return (UserAccount) JsfUtils.getValueOfValueExpression(
                "#{userSession.user}");
    }

    /**
     * Determines if a news item has been loaded. If a news item cannot be
     * retrieved from {@link NewsItem#getSelectedNewsItem()} it is not loaded.
     *
     * @return {@code true} if a news item has been selected and loaded,
     * otherwise {@code false}
     */
    public boolean isItemLoaded() {
        if (getSelectedItem() == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if the {@link dk.i2m.converge.core.newswire.NewswireItem}
     * can be imported to a {@link Catalogue}. It is only possible
     * to import {@link dk.i2m.converge.core.newswire.NewswireItem}s where
     * attachments have {@link Rendition}s set.
     * <p/>
     * @return {@code true} if the {@link NewswireItem} can be imported,
     * otherwise {@code false}
     */
    public boolean isImportReady() {
        boolean ready = false;
        if (getSelectedItem() != null) {
            for (NewswireItemAttachment attachment : getSelectedItem().
                    getAttachments()) {
                if (attachment.isRenditionSet()) {
                    ready = true;
                }
            }
        }
        return ready;
    }

    /**
     * Adds the attachments of a {@link NewswireItem} to the users
     * default catalogue.
     * <p/>
     * @param event * Event that invoked the handler
     */
    public void onAddToCatalogue(ActionEvent event) {
        if (importCatalogue != null) {
            MediaItem item = catalogueFacade.create(selectedItem,
                    importCatalogue);
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_INFO, Bundle.i18n.name(),
                    "NewswireItem_IMPORT_SUCCESSFUL", new Object[]{importCatalogue.getName()});
        } else {
            JsfUtils.createMessage("frmPage", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(),
                    "NewswireItem_IMPORT_FAILED");
        }
    }
}
