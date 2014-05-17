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
package dk.i2m.converge.ws.model;

import java.io.Serializable;

/**
 * Web service message containing information about a
 * {@link NewsItem} actor.
 *
 * @author Allan Lykke Christensen
 */
public class NewsItemActor implements Serializable {

    private Long roleId;

    private String role;

    private String name;

    private String username;

    /**
     * Creates a new instance of {@link NewsItemActor}.
     */
    public NewsItemActor() {
        this(0L, "", "", "");
    }

    /**
     * Creates a new instance of {@link NewsItemActor}.
     * 
     * @param roleId
     *          ID of the role
     * @param role
     *          Name of the role of the user
     * @param name
     *          Full name of the user
     * @param username 
     *          User name of the user
     */
    public NewsItemActor(Long roleId, String role, String name, String username) {
        this.roleId = roleId;
        this.role = role;
        this.name = name;
        this.username = username;
    }

    /**
     * Get the unique username of the user.
     * 
     * @return Unique username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the unique username of the user.
     * 
     * @param username
     *          Unique username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the full name of the user.
     * 
     * @return Full name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the full name of the user.
     * 
     * @param name
     *          Full name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the unique identifier of the user role.
     * 
     * @return Unique identifier of the user role
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Sets the unique identifier of the user role.
     * 
     * @param roleId 
     *          Unique identifier of the user role
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets the name of the user role.
     * 
     * @return Name of the user role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the name of the user role.
     * 
     * @param role 
     *          Name of the user role
     */
    public void setRole(String role) {
        this.role = role;
    }
}
