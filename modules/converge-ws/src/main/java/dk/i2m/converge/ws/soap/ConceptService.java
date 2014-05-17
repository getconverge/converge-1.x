/*
 * Copyright (C) 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.ws.soap;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.metadata.Concept;
import dk.i2m.converge.ejb.facades.MetaDataFacadeLocal;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 *
 * @author alc
 */
@WebService(serviceName = "ConceptService")
public class ConceptService {

    private static final Logger LOG = Logger.getLogger(ConceptService.class.getName());

    @EJB private MetaDataFacadeLocal metaDataFacade;

    @Resource private WebServiceContext context;

    @WebMethod(operationName = "findConceptByName")
    public dk.i2m.converge.ws.model.Concept findConceptByName(@WebParam(name = "name") String name) throws DataNotFoundException {
        Concept c = metaDataFacade.findConceptByName(name);
        dk.i2m.converge.ws.model.Concept concept = new dk.i2m.converge.ws.model.Concept();

        concept.setId(c.getId());
        concept.setName(c.getName());
        concept.setDefinition(c.getDefinition());
        concept.setType(c.getType());
        concept.setFullName(c.getFullTitle());

        return concept;

    }
}
