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
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.CatalogueHookInstance;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.plugin.CatalogueEvent;
import dk.i2m.converge.core.plugin.CatalogueEventException;
import dk.i2m.converge.core.plugin.CatalogueHook;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.ejb.messaging.CatalogueHookMessageBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.*;

/**
 * Stateless session bean providing {@link Catalogue} services.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class CatalogueServiceBean implements CatalogueServiceLocal {

    private static final Logger LOG =
            Logger.getLogger(CatalogueServiceBean.class.getName());

    @EJB private DaoServiceLocal daoService;

    @EJB private SystemFacadeLocal systemFacade;

    @EJB private PluginContextBeanLocal pluginContext;

    @Resource(mappedName = "jms/catalogueHookQueue") private Destination queue;

    @Resource(mappedName = "jms/connectionFactory") private ConnectionFactory jms;

    /** {@inheritDoc } */
    @Override
    public void executeHook(Long mediaItemId, Long hookInstanceId,
            CatalogueEvent.Event eventType) throws
            DataNotFoundException {

        CatalogueHookInstance hookInstance =
                daoService.findById(CatalogueHookInstance.class, hookInstanceId);

        MediaItem mediaItem = daoService.findById(MediaItem.class, mediaItemId);

        // Add background task indicator
        Long taskId = systemFacade.createBackgroundTask("Executing "
                + hookInstance.getLabel() + " for Media Item #" + mediaItem.
                getId());

        // Process renditions
        for (MediaItemRendition mir : mediaItem.getRenditions()) {
            CatalogueEvent event = new CatalogueEvent(eventType, mediaItem, mir);
            try {
                CatalogueHook hook = hookInstance.getHook();
                hook.execute(pluginContext, event, hookInstance);
            } catch (CatalogueEventException ex) {
                LOG.log(Level.WARNING, ex.getMessage());
                LOG.log(Level.FINE, "Could not execute hook", ex);
            }
        }

        // Remove background task indicator
        systemFacade.removeBackgroundTask(taskId);
    }

    /** {@inheritDoc } */
    @Override
    public void executeAsynchronousHook(Long mediaItemId, Long hookInstanceId,
            CatalogueEvent.Event eventType) {
        Connection connection = null;
        try {
            connection = jms.createConnection();
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

            // Prepare message
            final String PROPERTY_HOOK_ID = CatalogueHookMessageBean.Property.CATALOGUE_HOOK_INSTANCE_ID.name();
            final String PROPERTY_MEDIA_ITEM_ID = CatalogueHookMessageBean.Property.MEDIA_ITEM_ID.name();
            final String PROPERTY_EVENT_TYPE = CatalogueHookMessageBean.Property.EVENT_TYPE.name();
            
            // Construct and dispatch message
            MessageProducer producer = session.createProducer(queue);
            MapMessage message = session.createMapMessage();
            message.setLongProperty(PROPERTY_HOOK_ID, hookInstanceId);
            message.setLongProperty(PROPERTY_MEDIA_ITEM_ID, mediaItemId);
            message.setStringProperty(PROPERTY_EVENT_TYPE, eventType.name());
            producer.send(message);

            session.close();
            connection.close();
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }

    }
}
