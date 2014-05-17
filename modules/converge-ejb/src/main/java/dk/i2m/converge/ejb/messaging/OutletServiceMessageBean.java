/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.EditionActionException;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import dk.i2m.converge.ejb.facades.OutletFacadeLocal;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.ejb.services.PluginContextBeanLocal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Message driven bean responding to edition service actions. The bean is
 * capable of executing {@link OutletEditionAction}s for {@link Edition}s and
 * {@link NewsItemPlacement}s. The bean expects the following properties:
 * <ul>
 * <li>editionId</li>
 * <li>actionId</li>
 * <li>newsItemPlacementId (If the bean is to execute the action on a single
 * placement rather than a complete edition)</li>
 * </ul>
 *
 * @author Allan Lykke Christensen
 */
@MessageDriven(mappedName = "jms/outletServiceQueue")
public class OutletServiceMessageBean implements MessageListener {

    public enum Property {

        USER_ACCOUNT_ID,
        OUTLET_ID,
        ACTION_ID,
    }

    private static final Logger LOG = Logger.
            getLogger(OutletServiceMessageBean.class.getName());

    @EJB private OutletFacadeLocal outletFacade;

    @EJB private DaoServiceLocal daoService;

    @EJB private PluginContextBeanLocal pluginContext;

    @EJB private SystemFacadeLocal systemFacade;

    @EJB private UserFacadeLocal userFacade;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(Message msg) {
        Long id = 0L;

        try {
            Long outletId = msg.getLongProperty(Property.OUTLET_ID.name());
            Long actionId = msg.getLongProperty(Property.ACTION_ID.name());
            String uid = msg.getStringProperty(Property.USER_ACCOUNT_ID.name());

            try {
                UserAccount ua = userFacade.findById(uid);
                pluginContext.setCurrentUserAccount(ua);
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "User with ID ''{0}'' could not be set",
                        new Object[]{uid});
            }

            try {
                Outlet outlet = outletFacade.findOutletById(outletId);
                OutletEditionAction action = daoService.
                        findById(OutletEditionAction.class, actionId);

                id = systemFacade.createBackgroundTask(action.getAction().
                        getName() + " - " + action.getLabel());
                EditionAction editionAction = action.getAction();

                Long editionTask = 0L;
                List<Edition> editions = outletFacade.
                        findEditionsByStatus(false, outlet);
                int executed = 0;
                int total = editions.size();

                for (Edition edition : editions) {
                    try {
                        executed++;

                        String message = "Executing " + action.getAction().
                                getName() + " = " + action.getLabel()
                                + ", Edition = " + edition.getId() + ", Date = "
                                + edition.getCloseDate() + ", (" + executed
                                + "/" + total + ")";
                        editionTask = systemFacade.createBackgroundTask(message);
                        editionAction.execute(pluginContext, edition, action);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, ex.getMessage());
                        LOG.log(Level.FINEST, "", ex);
                    } finally {
                        systemFacade.removeBackgroundTask(editionTask);
                    }

                }
            } catch (DataNotFoundException ex) {
                pluginContext.log(LogSeverity.WARNING, ex.getMessage(),
                        new OutletEditionAction(), actionId);
            } catch (EditionActionException ex) {
                pluginContext.log(LogSeverity.WARNING, ex.getMessage(),
                        new OutletEditionAction(), actionId);
            }
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            systemFacade.removeBackgroundTask(id);
        }
    }
}
