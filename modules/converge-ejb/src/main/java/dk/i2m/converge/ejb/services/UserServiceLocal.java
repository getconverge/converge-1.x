/*
 * Copyright (C) 2009 - 2010 Interactive Media Management
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
import dk.i2m.converge.core.security.Privilege;
import dk.i2m.converge.core.security.SystemPrivilege;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import java.util.List;
import javax.ejb.Local;
import javax.naming.NamingException;

/**
 * Local interface for the user service enterprise bean.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface UserServiceLocal {

    /**
     * Finds all normal Converge {@link UserAccount}s.
     *
     * @return {@link List} of Converge {@link UserAccount}s
     */
    List<UserAccount> findAll();

    boolean exists(String id);

    UserAccount findById(String id) throws UserNotFoundException, DirectoryException;

    /**
     * Gets all the {@link UserRole}s registered in the database.
     *
     * @return {@link List} of {@link UserRole}s registered in the database
     */
    List<UserRole> getUserRoles();

    UserRole findUserRoleById(Long id) throws DataNotFoundException;

    /**
     * Finds all {@link UserAccount}s in the given user role.
     *
     * @param name
     *          Name of the {@link UserRole}
     * @return {@link List} of {@link UserAccount}s
     */
    List<UserAccount> findUserAccountsByUserRoleName(String name);

    /**
     * Updates an existing {@link UserRole}.
     *
     * @param userRole
     *          {@link UserRole} to update
     */
    void update(UserRole userRole);

    UserRole create(UserRole selected);

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
    Privilege findPrivilegeById(String id) throws DataNotFoundException;

    /**
     * Deletes an existing {@link UserRole} from the database.
     *
     * @param userRole
     *          {@link UserRole} to delete
     */
    void delete(UserRole userRole);

    /**
     * Updates an existing {@link UserAccount}.
     * 
     * @param user
     *          {@link UserAccount} to update
     * @return Updated {@link UserAccount}
     */
    UserAccount update(UserAccount user);

    /**
     * Finds all the {@link UserAccount}s in a given group.
     *
     * @param groupDn
     *          Distinguished name of the group
     * @return {@link List} of {@link UserAccount}s in the given group
     * @throws NamingException
     *          If the given <code>groupDn</code> does not exist
     */
    List<UserAccount> getMembers(String groupDn) throws NamingException;

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

    /**
     * Takes a {@link UserAccount} from the database and supplies it with
     * information from the configured LDAP directory.
     *
     * @param userAccount
     *          {@link UserAccount} to supply with information
     * @return {@link UserAccount} supplied with information from the LDAP
     *         directory
     * @throws UserNotFoundException
     *          If the user could not be found in the LDAP directory
     * @throws DirectoryException
     *          If a connection could not be established to the LDAP directory
     */
    UserAccount syncWithDirectory(UserAccount userAccount) throws UserNotFoundException, DirectoryException;

    /**
     * Gets {@link UserAccount}s with a given {@link UserRole}.
     *
     * @param roleId
     *          Unique identifier of the role
     * @return  {@link List} of {@link UserAccount} with the given role
     */
    java.util.List<dk.i2m.converge.core.security.UserAccount> getRoleMembers(java.lang.Long roleId);

    public java.util.List<dk.i2m.converge.core.security.UserAccount> getDirectoryMembers() throws javax.naming.NamingException;
}
