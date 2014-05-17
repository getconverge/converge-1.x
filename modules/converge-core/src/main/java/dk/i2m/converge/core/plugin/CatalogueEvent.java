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
package dk.i2m.converge.core.plugin;

import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;

/**
 * Event that has occurred for a Catalogue.
 *
 * @author Allan Lykke Christensen
 */
public class CatalogueEvent {

    public enum Event {

        NewItem, UploadRendition, UpdateRendition
    }

    private Event type;

    private MediaItemRendition rendition;

    private MediaItem item;

    /**
     * Creates a new instance of {@link CatalogueEvent}.
     * 
     * @param type 
     *          Type of event
     */
    public CatalogueEvent(Event type, MediaItem item, MediaItemRendition rendition) {
        this.type = type;
        this.item = item;
        this.rendition = rendition;
    }

    /**
     * Gets the type of event that occurred.
     * 
     * @return {@link Event} that occurred
     */
    public Event getType() {
        return type;
    }

    public MediaItem getItem() {
        return item;
    }

    public MediaItemRendition getRendition() {
        return rendition;
    }
}
