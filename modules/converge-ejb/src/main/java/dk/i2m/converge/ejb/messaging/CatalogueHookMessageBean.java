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
package dk.i2m.converge.ejb.messaging;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.catalogue.CatalogueHookInstance;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.plugin.CatalogueEvent;
import dk.i2m.converge.core.plugin.CatalogueHook;
import dk.i2m.converge.ejb.services.CatalogueServiceLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Message-Driven Bean for asynchronous execution of {@link CatalogueHook}s.
 *
 * @author Allan Lykke Christensen
 */
@MessageDriven(mappedName = "jms/catalogueHookQueue")
public class CatalogueHookMessageBean implements MessageListener {

    private static final Logger LOG =
            Logger.getLogger(CatalogueHookMessageBean.class.getName());

    @EJB private CatalogueServiceLocal catalogueService;

    /**
     * Available properties for the message.
     */
    public enum Property {

        /**
         * Mandatory property containing the identifier of the
         * {@link CatalogueHookInstance}.
         */
        CATALOGUE_HOOK_INSTANCE_ID,
        /**
         * Mandatory property containing the identifier of the
         * {@link MediaItem}.
         */
        MEDIA_ITEM_ID,
        /**
         * Mandatory String property containing the type of event. Must match
         * {@link CatalogueEvent#Event}.
         */
        EVENT_TYPE,
    }

    @Override
    public void onMessage(Message msg) {
        try {
            // Obtain properties from received message
            Long hookId = msg.getLongProperty(
                    Property.CATALOGUE_HOOK_INSTANCE_ID.name());
            Long itemId = msg.getLongProperty(Property.MEDIA_ITEM_ID.name());
            String type = msg.getStringProperty(Property.EVENT_TYPE.name());
            try {
                CatalogueEvent.Event event = CatalogueEvent.Event.valueOf(type);
                catalogueService.executeHook(itemId, hookId, event);
            } catch (DataNotFoundException ex) {
                LOG.log(Level.SEVERE, ex.getMessage());
            }

        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, "Asynchronous execution failed", ex);
        }
    }
}
