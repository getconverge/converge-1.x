/*
 * Copyright (C) 2012 Interactive Media Management
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
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.plugin.CatalogueEvent;
import dk.i2m.converge.ejb.messaging.CatalogueHookMessageBean;
import javax.ejb.Local;

/**
 * Local interface for the {@link CatalogueServiceBean}
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface CatalogueServiceLocal {

    /**
     * Executes a {@link CatalogueHookInstance} on a {@link MediaItem}.
     * <p/>
     * @param mediaItemId    Unique identifier of the {@link MediaItem}
     * @param hookInstanceId Unique identifier of the {@link CatalogueHookInstance}
     * @param eventType      Event that has occurred
     * @throws DataNotFoundException If the given {@code mediaItemId} or {@code hookInstanceId} was invalid
     */
    void executeHook(Long mediaItemId, Long hookInstanceId,
            CatalogueEvent.Event eventType) throws
            DataNotFoundException;

    /**
     * Asynchrnous execution of a {@link CatalogueHookInstance} on a
     * {@link MediaItem}. <em>Note: Do not execute this function from
     * {@link CatalogueHookMessageBean} as it will cause an infinite loop.</em>
     * <p/>
     * @param mediaItemId    Unique identifier of the {@link MediaItem}
     * @param hookInstanceId Unique identifier of the {@link CatalogueHookInstance}
     * @param eventType      Event that has occurred
     */
    void executeAsynchronousHook(Long mediaItemId, Long hookInstanceId,
            CatalogueEvent.Event eventType);
}
