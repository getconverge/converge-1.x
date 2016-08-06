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
package com.getconverge.ws.rest;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import dk.i2m.converge.ejb.facades.ActivityStreamFacadeLocal;
import dk.i2m.converge.core.activitystream.ActivityStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * REST Web Service exposing the activity stream and operations surrounding it.
 *
 * @author Allan Lykke Christensen
 */
@Path("activitystream")
@Api(value = "/activitystream", description = "Exposes the activity stream of the authenticated user")
@Produces(MediaType.APPLICATION_JSON)
public class ActivityStreamRestService extends AbstractRestService {

    private static final Logger LOG = Logger.getLogger(ActivityStreamRestService.class.getName());
    private final ActivityStreamFacadeLocal activityStreamFacade = lookupActivityStreamFacadeLocal();

    /**
     * Get the {@link ActivityStream} for the currently authorized user
     * formatted as a JSON document.
     *
     * @param security Security context of the request
     * @param page Page of the {@link ActivityStream} to fetch
     * @param size Number of activities to include in the {@Link ActivityStream}
     * @return {@link ActivityStream} of the logged in user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get the activity stream of the current user", notes = "Get the activity stream of the currently logged-in user", response = ActivityStream.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Activity stream found for the current user")})
    
    public ActivityStream getJson(@Context SecurityContext security,
            @ApiParam(value = "The page number to retrieve.", defaultValue = "1", required = false) @QueryParam("page") Integer page,
            @ApiParam(value = "Number of activities to retrieve per page.", defaultValue = "25", required = false) @QueryParam("size") Integer size) {
        authCheck(security);
        String username = security.getUserPrincipal().getName();
        int start = DEFAULT_START;
        Integer activitiesPerPage = size;

        if (activitiesPerPage == null) {
            activitiesPerPage = DEFAULT_SIZE;
        }

        if (page != null) {
            start = page * activitiesPerPage;
        }

        return activityStreamFacade.getActivityStream(username, start, activitiesPerPage);
    }

    private ActivityStreamFacadeLocal lookupActivityStreamFacadeLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ActivityStreamFacadeLocal) c.lookup("java:comp/env/ActivityStreamFacade");
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, "Could not retrieve EJB. {0}", ex.getMessage());
            LOG.log(Level.FINEST, null, ex);
            throw new RuntimeException(ex);
        }
    }

}
