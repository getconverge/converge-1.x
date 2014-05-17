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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.facades.NewsItemFacadeLocal;
import dk.i2m.jsf.JsfUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

/**
 * Managed backing bean for {@code /NewsItemArchive.jspx}. The backing bean is
 * kept alive by the JSF file. Loading a news item is done by setting the ID of
 * the item using {@link NewsItemArchive#setId(java.lang.Long)}.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemArchive {

    private static final Logger log = Logger.getLogger(NewsItemArchive.class.getName());

    @EJB private NewsItemFacadeLocal newsItemFacade;

    private dk.i2m.converge.core.content.NewsItem selectedNewsItem = null;

    private Long id = 0L;

    public NewsItemArchive() {
    }

    /**
     * Gets the unique identifier of the loaded news item.
     *
     * @return Unique identifier of the loaded news item
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of the news item to load. Upon setting the identifier, the
     * news item will be loaded from the database.
     *
     * @param id
     *          Unique identifier of the news item to load
     */
    public void setId(Long id) {
        log.log(Level.INFO, "Setting News Item #{0}", id);
        this.id = id;

        if (selectedNewsItem == null || (selectedNewsItem.getId() != id)) {
            try {
                this.selectedNewsItem = newsItemFacade.findNewsItemFromArchive(id);
            } catch (DataNotFoundException ex) {
                this.selectedNewsItem = null;
            }
        }
    }

    /**
     * Determines if a news item has been loaded. If a news item cannot be
     * retrieved from {@link NewsItem#getSelectedNewsItem()} it is not loaded.
     *
     * @return {@code true} if a news item has been selected and loaded,
     *         otherwise {@code false}
     */
    public boolean isNewsItemLoaded() {
        if (getSelectedNewsItem() == null) {
            return false;
        } else {
            return true;
        }
    }

     public dk.i2m.converge.core.content.NewsItem getSelectedNewsItem() {
        return selectedNewsItem;
    }
    
    private UserAccount getUser() {
        return (UserAccount) JsfUtils.getValueOfValueExpression("#{userSession.user}");
    }
}
