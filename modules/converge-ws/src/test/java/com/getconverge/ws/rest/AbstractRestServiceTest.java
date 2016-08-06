/*
 * Copyright (C) 2016 Allan Lykke Christensen
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

import java.security.Principal;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import static org.easymock.EasyMock.*;
import javax.ws.rs.core.SecurityContext;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class AbstractRestServiceTest {

    private SecurityContext securityContext;

    @Before
    public void setUp() {
        securityContext = createMock(SecurityContext.class);

    }

    @Test
    public void abstractRestService_whenSecurityContextIsNull_throwWebApplicationException() {
        // Arrange
        SecurityContext nullSecurityContext = null;
        AbstractRestService abstractRestService = new AbstractRestServiceImpl();
        Status expected = Status.UNAUTHORIZED;

        // Act
        try {
            abstractRestService.authCheck(nullSecurityContext);
        } catch (WebApplicationException ex) {
            // Assert
            assertEquals(expected.getStatusCode(), ex.getResponse().getStatus());
        }
    }

    @Test
    public void abstractRestService_whenAnonymous_throwWebApplicationException() {
        // Arrange
        expect(securityContext.getUserPrincipal()).andReturn(null);
        replay(securityContext);
        AbstractRestService abstractRestService = new AbstractRestServiceImpl();
        Status expected = Status.UNAUTHORIZED;

        // Act
        try {
            abstractRestService.authCheck(securityContext);
        } catch (WebApplicationException ex) {
            // Assert
            assertEquals(expected.getStatusCode(), ex.getResponse().getStatus());
        }
    }

    @Test
    public void abstractRestService_whenAuthenticated_dontThrowWebApplicationException() {
        // Arrange
        Principal principal = createMock(Principal.class);
        expect(securityContext.getUserPrincipal()).andReturn(principal);
        replay(securityContext);
        AbstractRestService abstractRestService = new AbstractRestServiceImpl();

        // Act
        try {
            abstractRestService.authCheck(securityContext);
        } catch (WebApplicationException ex) {
            // Assert
            fail("WebApplicationException not expected when user is authorized");
        }
    }

    @Test
    public void abstractRestService_whenAuthenticatedWithRequiredRole_dontThrowWebApplicationException() {
        // Arrange
        Principal principal = createMock(Principal.class);
        expect(principal.getName()).andReturn(SecurityRole.ADMINISTRATOR.name());
        expect(securityContext.getUserPrincipal()).andReturn(principal);
        expect(securityContext.isUserInRole(SecurityRole.ADMINISTRATOR.name())).andReturn(true);
        replay(principal, securityContext);
        AbstractRestService abstractRestService = new AbstractRestServiceImpl();

        // Act
        try {
            abstractRestService.authCheck(securityContext, SecurityRole.ADMINISTRATOR);
        } catch (WebApplicationException ex) {
            // Assert
            fail("WebApplicationException not expected when user is authorized with required role");
        }
    }

    @Test
    public void abstractRestService_whenAuthenticatedWithoutRequiredRole_throwWebApplicationException() {
        // Arrange
        Principal principal = createMock(Principal.class);
        expect(principal.getName()).andReturn(SecurityRole.ADMINISTRATOR.name());
        expect(securityContext.getUserPrincipal()).andReturn(principal);
        expect(securityContext.isUserInRole(SecurityRole.ADMINISTRATOR.name())).andReturn(false);
        replay(principal, securityContext);
        AbstractRestService abstractRestService = new AbstractRestServiceImpl();
        Status expected = Status.UNAUTHORIZED;

        // Act
        try {
            abstractRestService.authCheck(securityContext, SecurityRole.ADMINISTRATOR);
        } catch (WebApplicationException ex) {
            // Assert
            assertEquals(expected.getStatusCode(), ex.getResponse().getStatus());
        }
    }

    public class AbstractRestServiceImpl extends AbstractRestService {
    }
}
