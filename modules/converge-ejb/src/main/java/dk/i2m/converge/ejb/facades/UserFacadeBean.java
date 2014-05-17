/*
 * Copyright (C) 2010 Interactive Media Management
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

import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.security.Privilege;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.utils.BeanComparator;
import dk.i2m.converge.ejb.services.ConfigurationServiceLocal;
import dk.i2m.converge.ejb.services.DaoServiceLocal;
import dk.i2m.converge.ejb.services.DirectoryException;
import dk.i2m.converge.ejb.services.QueryBuilder;
import dk.i2m.converge.ejb.services.UserNotFoundException;
import dk.i2m.converge.ejb.services.UserServiceLocal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.NamingException;

/**
 * Enterprise session bean providing a facade for user management.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class UserFacadeBean implements UserFacadeLocal {

    private static final Logger LOG = Logger.getLogger(UserFacadeBean.class.getName());

    @EJB private UserServiceLocal userService;

    @EJB private CatalogueFacadeLocal catalogueFacade;

    @EJB private DaoServiceLocal daoService;

    @EJB private ConfigurationServiceLocal cfgService;

    /** {@inheritDoc} */
    @Override
    public List<UserAccount> getMembers(Long outletId, SystemPrivilege privilege) {
        return userService.getMembers(outletId, privilege);
    }

    /** {@inheritDoc} */
    @Override
    public List<UserAccount> getMembers(Long departmentId) {
        return userService.getMembers(departmentId);
    }

    /** {@inheritDoc} */
    @Override
    public UserAccount findById(String username) throws DataNotFoundException {
        try {
            return userService.findById(username);
        } catch (UserNotFoundException ex) {
            throw new DataNotFoundException(ex);
        } catch (DirectoryException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return new UserAccount(username);
        }
    }

    /** {@inheritDoc} */
    @Override
    public UserAccount findByIdWithLocks(String username) throws DataNotFoundException {
        try {
            UserAccount ua = userService.findById(username);
            // force fetching of lock before returning to the user
            ua.getCheckedOut();
            return ua;
        } catch (UserNotFoundException ex) {
            throw new DataNotFoundException(ex);
        } catch (DirectoryException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return new UserAccount(username);
        }
    }

    /** {@inheritDoc} */
    @Override
    public UserAccount findById(String username, boolean sync) throws DataNotFoundException {

        UserAccount userAccount = findById(username);

        if (sync) {
            try {
                userAccount = userService.syncWithDirectory(userAccount);
            } catch (UserNotFoundException ex) {
                throw new DataNotFoundException(ex);
            } catch (DirectoryException ex) {
                throw new DataNotFoundException(ex);
            }
        }

        return userAccount;
    }

    /** {@inheritDoc } */
    @Override
    public List<UserRole> getUserRoles() {
        return userService.getUserRoles();
    }

    /** {@inheritDoc } */
    @Override
    public UserRole findUserRoleById(final Long id) throws DataNotFoundException {
        return userService.findUserRoleById(id);
    }

    /**
     * Gets all {@link UserAccount}s from the datastore.
     *
     * @return {@link List} of all {@link UserAccount}s from the datastore
     */
    @Override
    public List<UserAccount> getUsers() {
        List<UserAccount> users = userService.findAll();
        Collections.sort(users, new BeanComparator("fullName"));
        return users;
    }

    /** {@inheritDoc}  */
    @Override
    public void update(UserRole userRole) {
        userService.update(userRole);
    }

    /** {@inheritDoc}  */
    @Override
    public UserRole create(UserRole userRole) {
        return userService.create(userRole);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(UserRole userRole) {
        userService.delete(userRole);
    }

    /** {@inheritDoc} */
    @Override
    public Privilege findPrivilegeById(String id) throws DataNotFoundException {
        return userService.findPrivilegeById(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<UserAccount> getMembers(UserRole role) {
        List<UserAccount> members = userService.getRoleMembers(role.getId());
        Collections.sort(members, new BeanComparator("fullName"));
        return members;
    }

    /**
     * Gets all the {@Link Notification}S awaiting a given user.
     *
     * @param username
     *          Username of the {@link UserAccount}
     * @return {@link List} of awaiting {@link Notification}s
     */
    @Override
    public List<Notification> getNotifications(String username) {
        Map<String, Object> params = QueryBuilder.with("username", username).parameters();
        return daoService.findWithNamedQuery(Notification.FIND_BY_USERNAME, params);
    }

    /**
     * Gets user {@Link Notification}s in the given interval
     *
     * @param username
     *          Username of the {@link UserAccount}
     * @param start
     *          First record to fetch
     * @param count
     *          Number of records to fetch
     * @return {@link List} of awaiting {@link Notification}s
     */
    @Override
    public List<Notification> getNotifications(String username, int start, int count) {
        Map<String, Object> params = QueryBuilder.with("username", username).parameters();
        return daoService.findWithNamedQuery(Notification.FIND_BY_USERNAME, params, start, count);
    }

    /**
     * Gets the count of notifications awaiting a given user.
     * 
     * @param username
     *          Username of the user
     * @return  Count of notifications
     */
    @Override
    public Long getNotificationCount(String username) {
        Map<String, Object> params = QueryBuilder.with("username", username).parameters();
        try {
            return daoService.findObjectWithNamedQuery(Long.class, Notification.COUNT_BY_USERNAME, params);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return 0L;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void dismiss(Notification notification) {
        daoService.delete(Notification.class, notification.getId());
    }

    /** {@inheritDoc} */
    @Override
    public void dismiss(UserAccount user) {
        for (Notification notification : getNotifications(user.getUsername())) {
            daoService.delete(Notification.class, notification.getId());
        }
    }

    @Override
    public UserAccount update(UserAccount userAccount) {
        return userService.update(userAccount);
    }

    /**
     * Determines if the given user is a catalogue editor.
     * 
     * @param username
     *          Username of the {@link UserAccount}
     * @return {@code true} if the user is an editor of a writable
     *         catalogue, otherwise {@code false}
     */
    @Override
    public boolean isCatalogueEditor(String username) {
        try {
            UserAccount user = findById(username);
            List<UserRole> roles = user.getUserRoles();
            List<Catalogue> catalogues = catalogueFacade.findWritableCatalogues();
            for (Catalogue catalogue : catalogues) {
                if (roles.contains(catalogue.getEditorRole())) {
                    return true;
                }
            }
        } catch (DataNotFoundException ex) {
            return false;
        }
        return false;
    }

    @Override
    public void synchroniseWithDirectory() {
        try {
            for (UserAccount user : userService.getDirectoryMembers()) {
                try {
                    LOG.log(Level.INFO, "Checking if {0} exists in the database", user.getUsername());
                    UserAccount acc = daoService.findObjectWithNamedQuery(UserAccount.class, UserAccount.FIND_BY_UID, QueryBuilder.with("username", user.getUsername()).parameters());
                    LOG.log(Level.INFO, "{0} exists. Starting to sync", user.getUsername());
                    try {
                        user = userService.syncWithDirectory(user);
                        userService.update(user);
                    } catch (UserNotFoundException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    } catch (DirectoryException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }

                } catch (DataNotFoundException ex) {
                    LOG.log(Level.INFO, "{0} does not exist. Creating user account", user.getUsername());
                    UserAccount newUser = daoService.create(user);
                    LOG.log(Level.INFO, "{0} created with id {1}", new Object[]{user.getUsername(), newUser.getId()});
                }
            }
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
