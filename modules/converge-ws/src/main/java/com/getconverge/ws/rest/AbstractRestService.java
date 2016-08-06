/*
 * Copyright (C) 2014 - 2016 Allan Lykke Christensen
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
package com.getconverge.ws.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

/**
 * Abstract base class for REST services providing common functions such as
 * authentication.
 *
 * @author Allan Lykke Christensen
 */
public abstract class AbstractRestService {

    /**
     * Checks if the user has logged in.
     *
     * @param securityContext REST {@link SecurityContext}
     * @throws WebApplicationException If the user is not logged in
     */
    public void authCheck(SecurityContext securityContext) {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
    }

    /**
     * Checks if the user has logged in and has a given role.
     *
     * @param securityContext REST {@link SecurityContext}
     * @param role Role which the user must have
     * @throws WebApplicationException If the user is not logged in or does not
     * have the given {@code role}
     */
    public void authCheck(SecurityContext securityContext, SecurityRole role) {
        authCheck(securityContext);

        if (!securityContext.isUserInRole(role.name())) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
    }
}
