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

import dk.i2m.converge.ejb.facades.ActivityStreamFacadeLocal;
import dk.i2m.converge.core.activitystream.ActivityStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service exposing the activity stream and operations surrounding it.
 *
 * @author Allan Lykke Christensen
 */
@Path("activitystream")
@Produces(MediaType.APPLICATION_JSON)
public class ActivityStreamRestService extends AbstractRestService {

    private static final Logger LOG = Logger.getLogger(ActivityStreamRestService.class.getName());
    private final ActivityStreamFacadeLocal activityStreamFacade = lookupActivityStreamFacadeLocal();

    @Context
    private UriInfo context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ActivityStream getJson(@Context SecurityContext security) {
        authCheck(security);
        String username = security.getUserPrincipal().getName();
        return activityStreamFacade.getActivityStream(username, 0, 25);
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
