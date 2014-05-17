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

import dk.i2m.converge.core.workflow.Department;
import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.security.Privilege;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.DataNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 * {@link Local} interface of the {@link UserFacadeBean}.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface UserFacadeLocal {

    /**
     * Gets the {@link UserAccount} matching the given username.
     *
     * @param username
     *          Unique identifier of the {@link UserAccount}
     * @return {@link UserAccount} matching the identifier
     * @throws DataNotFoundException
     *          If no {@link UserAccount} could be found with the given username
     */
    UserAccount findById(String username) throws DataNotFoundException;
    
    UserAccount findByIdWithLocks(String username) throws DataNotFoundException;

    /**
     * Gets the {@link UserAccount} matching the given username. The
     * {@code sync} parameter determines if the {@link UserAccount} should be
     * synchronised with the directory server.
     *
     * @param username
     *          Unique identifier of the {@link UserAccount}
     * @param sync
     *          Should the {@link UserAccount} be synchronised with the
     *          directory server
     * @return {@link UserAccount} matching the identifier and synchronised with
     *         the directory server if {@code sync} is set
     * @throws DataNotFoundException
     *          If no {@link UserAccount} could be found with the given username
     */
    UserAccount findById(String username, boolean sync) throws DataNotFoundException;

    /**
     * Gets the {@link UserRole} matching a given ID.
     *
     * @param id
     *          Unique ID of the {@link UserRole}
     * @return {@link UserRole} matching the given <code>role</code>
     * @throws DataNotFoundException
     * If there was no {@link UserRole} matching the
     * <code>role</code>
     */
    UserRole findUserRoleById(final Long id) throws DataNotFoundException;

    /**
     * Gets a {@link List} of available {@link UserRole}s.
     *
     * @return {@link List} of available {@link UserRole}s.
     */
    List<UserRole> getUserRoles();

    /**
     * Updates and existing {@link UserRole}.
     *
     * @param userRole
     *          {@link UserRole} to update
     */
    void update(UserRole userRole);

    /**
     * Creates a new {@link UserRole}.
     *
     * @param userRole
     *          {@link UserRole} to create
     * @return userRole
     *          {@link UserRole} created
     */
    UserRole create(UserRole userRole);

    /**
     * Deletes an existing {@link UserRole} from the database.
     * 
     * @param userRole
     *          {@link UserRole} to delete
     */
    void delete(UserRole userRole);

    /**
     * Finds a {@link Privilege} in the database. If the {@link Privilege} could
     * not be found, but a {@link SystemPrivilege} exist, a new
     * {@link Privilege} will be created. If a {@link SystemPrivilege} with the
     * give id does not exist, a {@link DataNotFoundException} is thrown.
     *
     * @param id
     *          Unique identifier of the {@link Privilege}
     * @return {@link Privilege} matching the id
     * @throws DataNotFoundException
     *          If a corresponding {@link SystemPrivilege} does not exist
     */
    Privilege findPrivilegeById(java.lang.String id) throws DataNotFoundException;

    /**
     * Gets {@link UserAccount} member of the given {@link UserRole}.
     *
     * @param role
     *          {@link UserRole} for which to get the members
     * @return {@link UserAccount} member of the given {@link UserRole}
     */
    List<UserAccount> getMembers(UserRole role);

    /**
     * Finds all the {@link UserAccount}s with a given privilege for a given
     * outlet.
     * 
     * @param outletId
     *          Unique identifier of the outlet
     * @param privilege
     *          System privilege to check
     * @return {@link UserAccount}s with a given privilege for a given
     *         outlet
     */
    List<UserAccount> getMembers(Long outletId, SystemPrivilege privilege);

    /**
     * Finds all the {@link UserAccount}s in a given {@link Department}.
     *
     * @param departmentId
     *          Unique identifier of the department
     * @return {@link UserAccount}s in a given {@link Department}
     */
    List<UserAccount> getMembers(Long departmentId);

    List<Notification> getNotifications(String username);

    /**
     * Dismisses a {@link Notification}.
     *
     * @param notification
     *          {@link Notification} to dismiss
     */
    void dismiss(Notification notification);

    /**
     * Dismisses all {@link Notification}s for the given user.
     *
     * @param user
     *          {@link UserAccount} for which to dismiss the notifications
     */
    void dismiss(UserAccount user);

    UserAccount update(UserAccount user);

    java.util.List<dk.i2m.converge.core.security.UserAccount> getUsers();

    void synchroniseWithDirectory();

    Long getNotificationCount(java.lang.String username);

    boolean isCatalogueEditor(String username);

    java.util.List<dk.i2m.converge.core.Notification> getNotifications(java.lang.String username, int start, int count);
}
