/*
 * Copyright (C) 2011 - 2013 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.ejb.messaging;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.newswire.NewswireDecoderException;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.plugin.NewswireDecoder;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.ejb.services.NewswireServiceLocal;
import dk.i2m.converge.ejb.services.PluginContextBeanLocal;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Message-driven bean for processing a {@link NewswireService}. The bean
 * expects a single long property {@code newswireServiceId} containing the
 * unique identifier of the {@link NewswireService} to be processed.
 *
 * @author <a href="mailto:allan@i2m.dk">Allan Lykke Christensen</a>
 */
@MessageDriven(mappedName = "jms/newswireServiceQueue")
public class NewswireDecoderMessageBean implements MessageListener {

    /**
     * Long property for sending the unique identifier of the
     * {@link NewswireService} to be processed.
     */
    public static final String NEWSWIRE_SERVICE_ID = "newswireServiceId";
    private static final Logger LOG = Logger.getLogger(NewswireDecoderMessageBean.class.getName());
    @EJB
    private DaoServiceLocal daoService;
    @EJB
    private PluginContextBeanLocal pluginContext;
    @EJB
    private SystemFacadeLocal systemFacade;
    @EJB
    private NewswireServiceLocal newswireServiceBean;

    @Override
    public void onMessage(Message msg) {
        try {
            Long newswireServiceId;
            try {
                newswireServiceId = msg.getLongProperty(NEWSWIRE_SERVICE_ID);
            } catch (NumberFormatException ex) {
                // Invalid or missing value sent to bean
                return;
            }
            fetchNewswire(newswireServiceId);
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private void fetchNewswire(Long id) {
        Long taskId = 0L;

        try {
            NewswireService service = daoService.findById(NewswireService.class, id);
            newswireServiceBean.startProcessingNewswireService(id);
            pluginContext.log(LogSeverity.INFO, "Fetching newswire service: {0}", new Object[]{service.getSource()}, service, service.getId());
            taskId = systemFacade.createBackgroundTask("Fetching newswire service " + service.getSource());
            NewswireDecoder decoder = service.getDecoder();
            decoder.decode(pluginContext, service);
            service.setLastFetch(Calendar.getInstance());
            daoService.update(service);
            pluginContext.log(LogSeverity.INFO, "Finished fetching newswire service: {0}", new Object[]{service.getSource()}, service, service.getId());
        } catch (DataNotFoundException ex) {
//            LOG.log(Level.WARNING, ex.getMessage());
            pluginContext.log(LogSeverity.WARNING, ex.getMessage(), NewswireService.class.getName(), "" + id);
        } catch (NewswireDecoderException ex) {
            //LOG.log(Level.SEVERE, null, ex);
            pluginContext.log(LogSeverity.SEVERE, ex.getMessage(), NewswireService.class.getName(), "" + id);
        } finally {
            systemFacade.removeBackgroundTask(taskId);
            newswireServiceBean.stopProcessingNewswireService(id);
        }
    }
}
