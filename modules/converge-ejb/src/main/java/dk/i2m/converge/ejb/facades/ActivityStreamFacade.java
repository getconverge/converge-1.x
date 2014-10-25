/*
 * Copyright (C) 2014 Allan Lykke Christensen
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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.activitystream.ActivityStream;
import dk.i2m.converge.core.activitystream.ActivityStreamMapper;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.ejb.services.QueryBuilder;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Stateless enterprise java bean implementing the {@link ActivityStreamFacade}.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class ActivityStreamFacade implements ActivityStreamFacadeLocal {

    private static final Logger LOG = Logger.getLogger(ActivityStreamFacade.class.getName());

    @EJB
    private DaoServiceLocal daoService;

    @EJB
    private SystemFacadeLocal systemFacade;

    /**
     * Gets all the {@Link Notification}S awaiting a given user.
     *
     * @param username Username of the {@link UserAccount}
     * @return {@link List} of awaiting {@link Notification}s
     */
    @Override
    public List<Notification> getNotifications(String username) {
        Map<String, Object> params = QueryBuilder.with(Notification.QUERY_PARAM_USERNAME, username).parameters();
        return daoService.findWithNamedQuery(Notification.FIND_BY_USERNAME, params);
    }

    /**
     * Gets user {@Link Notification}s in the given interval
     *
     * @param username Username of the {@link UserAccount}
     * @param start First record to fetch
     * @param count Number of records to fetch
     * @return {@link List} of awaiting {@link Notification}s
     */
    @Override
    public List<Notification> getNotifications(String username, int start, int count) {
        Map<String, Object> params = QueryBuilder.with(Notification.QUERY_PARAM_USERNAME, username).parameters();
        return daoService.findWithNamedQuery(Notification.FIND_BY_USERNAME, params, start, count);
    }

    /**
     * Gets the count of notifications awaiting a given user.
     *
     * @param username Username of the user
     * @return Count of notifications
     */
    @Override
    public Long getNotificationCount(String username) {
        Map<String, Object> params = QueryBuilder.with(Notification.QUERY_PARAM_USERNAME, username).parameters();
        try {
            return daoService.findObjectWithNamedQuery(Long.class, Notification.COUNT_BY_USERNAME, params);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            LOG.log(Level.FINEST, null, ex);
            return 0L;
        }
    }

    /**
     * Dismisses a {@link Notification}.
     *
     * @param notification {@link Notification} to dismiss
     */
    @Override
    public void dismiss(Notification notification) {
        daoService.delete(Notification.class, notification.getId());
    }

    /**
     * Dismisses all {@link Notification}s for the given user.
     *
     * @param username User name of the account for which to dismiss the
     * notifications
     */
    @Override
    public void dismiss(String username) {
        for (Notification notification : getNotifications(username)) {
            daoService.delete(Notification.class, notification.getId());
        }
    }

    /**
     * Gets a paged {@link ActivityStream} for a given user.
     *
     * @param username Username of the user
     * @param start First item to include in the stream
     * @param count Number of items to include in the stream
     * @return Paged {@link ActivityStream} for the {@link UserAccount user}
     */
    @Override
    public ActivityStream getActivityStream(String username, int start, int count) {
        String baseUrl = systemFacade.getProperty(ConfigurationKey.CONVERGE_HOME_URL);
        ActivityStream stream = new ActivityStream();
        stream.setTotalItems(getNotificationCount(username));
        stream.setBaseUrl(baseUrl);

        List<Notification> notifications = getNotifications(username, start, count);

        for (Notification notification : notifications) {
            stream.addItem(ActivityStreamMapper.from(notification));
        }

        return stream;
    }

}
