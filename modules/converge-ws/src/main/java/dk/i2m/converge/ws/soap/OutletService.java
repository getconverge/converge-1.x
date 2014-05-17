/*
 * Copyright (C) 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.ws.soap;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.workflow.Section;
import dk.i2m.converge.ejb.facades.OutletFacadeLocal;
import dk.i2m.converge.ws.model.Edition;
import dk.i2m.converge.ws.model.ModelConverter;
import dk.i2m.converge.ws.model.Outlet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Service for accessing {@link Outlet}s.
 *
 * @author Allan Lykke Christensen
 */
@WebService(serviceName = "OutletService")
public class OutletService {

    private static final Logger LOG = Logger.getLogger(OutletService.class.
            getName());

    @EJB private OutletFacadeLocal outletFacade;

    @WebMethod(operationName = "getEditions")
    public List<dk.i2m.converge.ws.model.Edition> getEditions(@WebParam(name =
            "outletId") Long outletId, @WebParam(name = "date") Date date) {
        Calendar editionDate = Calendar.getInstance();
        editionDate.setTime(date);
        List<dk.i2m.converge.core.workflow.Edition> editions = outletFacade.
                findEditionByOutletAndDate(outletId, editionDate);
        List<Edition> outputEditions = new ArrayList<Edition>();

        for (dk.i2m.converge.core.workflow.Edition edition : editions) {
            Edition output = new Edition();
            output.setId(edition.getId());
            output.setCloseDate(edition.getCloseDate());
            output.setExpirationDate(edition.getExpirationDate().getTime());
            output.setPublicationDate(edition.getPublicationDate().getTime());
            outputEditions.add(output);
        }

        return outputEditions;
    }

    @WebMethod(operationName = "createEdition")
    public Edition createEdition(@WebParam(name = "outletId") Long outletId,
            @WebParam(name = "open") boolean open, @WebParam(name =
            "publicationDate") Date publicationDate, @WebParam(name =
            "expirationDate") Date expirationDate,
            @WebParam(name = "closeDate") Date closeDate) {
        dk.i2m.converge.core.workflow.Edition edition = outletFacade.
                createEdition(outletId, open, publicationDate, expirationDate,
                closeDate);
        Edition output = new Edition();
        output.setId(edition.getId());
        output.setCloseDate(edition.getCloseDate());
        output.setExpirationDate(edition.getExpirationDate().getTime());
        output.setPublicationDate(edition.getPublicationDate().getTime());
        return output;
    }

    /**
     * Obtains a given {@link Outlet} and its {@link Section}s.
     *
     * @param id Unique identifier of the {@link Outlet}
     * @return {@link Outlet} matching the given {@code id}
     */
    @WebMethod(operationName = "getOutlet")
    public Outlet getOutlet(@WebParam(name = "outletId") Long id) {
        Outlet outlet = new Outlet();
        try {
            dk.i2m.converge.core.workflow.Outlet convergeOutlet;
            convergeOutlet = outletFacade.findOutletById(id);
            outlet = ModelConverter.toOutlet(convergeOutlet);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown outlet requested");
        }
        return outlet;
    }

    @WebMethod(operationName = "createSection")
    public Long createSection(@WebParam(name = "outletId") Long outletId,
            @WebParam(name = "parentId") Long parentSectionId, @WebParam(name =
            "name") String name,
            @WebParam(name = "description") String description, @WebParam(name =
            "active") boolean active) throws DataNotFoundException {
        Section section = new Section();
        section.setActive(true);
        section.setDescription(description);
        section.setName(name);
        section.setOutlet(outletFacade.findOutletById(outletId));
        if (parentSectionId != null) {
            section.setParent(outletFacade.findSectionById(parentSectionId));
        }
        section = outletFacade.createSection(section);
        return section.getId();
    }

    @WebMethod(operationName = "findSectionByName")
    public List<dk.i2m.converge.ws.model.Section> findSectionByName(@WebParam(name =
            "outletId") Long outletId,
            @WebParam(name = "section") String sectionName) throws
            DataNotFoundException {
        List<Section> sections = outletFacade.findSectionByName(outletId,
                sectionName);
        List<dk.i2m.converge.ws.model.Section> results =
                new ArrayList<dk.i2m.converge.ws.model.Section>();

        for (Section section : sections) {
            dk.i2m.converge.ws.model.Section result =
                    new dk.i2m.converge.ws.model.Section();
            result.setId(section.getId());
            result.setTitle(section.getName());
            results.add(result);
        }
        return results;
    }

    /**
     * Obtains the published news items in a given edition.
     *
     * @param id Unique identifier of the {@link Edition}
     * @return {@link Edition} containing the published {@link NewsItem}s
     */
    @WebMethod(operationName = "getPublishedEdition")
    public Edition getPublishedEdition(@WebParam(name = "editionId") Long id) {
        Edition edition = new Edition();

        try {
            dk.i2m.converge.core.workflow.Edition convergeEdition;
            convergeEdition = outletFacade.findEditionById(id);

            edition.setId(convergeEdition.getId());
            edition.setCloseDate(convergeEdition.getCloseDate());
            edition.setPublicationDate(convergeEdition.getPublicationDate().
                    getTime());
            edition.setExpirationDate(convergeEdition.getExpirationDate().
                    getTime());

            for (NewsItemPlacement nip : convergeEdition.getPlacements()) {
                if (nip.getNewsItem().isEndState()) {
                    edition.getItems().add(ModelConverter.toNewsItem(nip));
                }
            }
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown outlet requested");
        }
        return edition;
    }

    @WebMethod(operationName = "scheduleAction")
    public void scheduleAction(@WebParam(name = "editionId") Long editionId,
            @WebParam(name = "actionId") Long actionId) {
        outletFacade.scheduleAction(editionId, actionId);
    }

    @WebMethod(operationName = "scheduleActions")
    public void scheduleActions(@WebParam(name = "editionId") Long editionId) {
        outletFacade.scheduleActions(editionId);
    }
}
