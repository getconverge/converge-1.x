/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.workflow.Department;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.ejb.services.NewswireServiceLocal;
import dk.i2m.converge.jsf.converters.NewsItemAuthorDisplayConverter;
import dk.i2m.converge.jsf.converters.UserRoleConverter;
import dk.i2m.converge.jsf.converters.WorkflowConverter;
import dk.i2m.converge.jsf.converters.WorkflowStateConverter;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.core.calendar.EventCategory;
import dk.i2m.converge.core.metadata.Concept;
import dk.i2m.converge.core.workflow.Workflow;
import dk.i2m.converge.core.workflow.WorkflowState;
import dk.i2m.converge.core.workflow.WorkflowStep;
import dk.i2m.converge.ejb.facades.CalendarFacadeLocal;
import dk.i2m.converge.ejb.facades.ListingFacadeLocal;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import dk.i2m.converge.ejb.facades.MetaDataFacadeLocal;
import dk.i2m.converge.ejb.facades.OutletFacadeLocal;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import dk.i2m.converge.ejb.facades.WorkflowFacadeLocal;
import dk.i2m.converge.jsf.converters.ClassConverter;
import dk.i2m.converge.jsf.converters.ConceptConverter;
import dk.i2m.converge.jsf.converters.CurrencyConverter;
import dk.i2m.converge.jsf.converters.DepartmentConverter;
import dk.i2m.converge.jsf.converters.EditionCandidateConverter;
import dk.i2m.converge.jsf.converters.EditionConverter;
import dk.i2m.converge.jsf.converters.NewsItemFieldConverter;
import dk.i2m.converge.jsf.converters.EventCategoryConverter;
import dk.i2m.converge.jsf.converters.FinancialMarketConverter;
import dk.i2m.converge.jsf.converters.LanguageConverter;
import dk.i2m.converge.jsf.converters.LocaleConverter;
import dk.i2m.converge.jsf.converters.RenditionConverter;
import dk.i2m.converge.jsf.converters.CatalogueConverter;
import dk.i2m.converge.jsf.converters.NewswireServiceConverter;
import dk.i2m.converge.jsf.converters.OutletConverter;
import dk.i2m.converge.jsf.converters.SectionConverter;
import dk.i2m.converge.jsf.converters.SystemPrivilegeConverter;
import dk.i2m.converge.jsf.converters.UserAccountConverter;
import dk.i2m.converge.jsf.converters.WeatherLocationConverter;
import dk.i2m.converge.jsf.converters.WeatherSituationConverter;
import dk.i2m.converge.jsf.converters.WorkflowStepConverter;
import dk.i2m.jsf.converters.EnumTypeConverter;
import java.util.Locale;
import javax.ejb.EJB;
import javax.faces.convert.Converter;

/**
 * Managed bean with access to JSF converters.
 *
 * @author Allan Lykke Christensen
 */
public class Converters {

    @EJB private OutletFacadeLocal outletFacade;

    @EJB private WorkflowFacadeLocal workflowFacade;

    @EJB private UserFacadeLocal userFacade;

    @EJB private MetaDataFacadeLocal metaDataFacade;

    @EJB private CalendarFacadeLocal calendarFacade;

    @EJB private CatalogueFacadeLocal catalogueFacade;

    @EJB private SystemFacadeLocal systemFacade;

    @EJB private ListingFacadeLocal listingFacade;

    @EJB private NewswireServiceLocal newswireService;

    /**
     * Gets an instance of the {@link SystemPrivilegeConverter}.
     *
     * @return Instance of the {@link SystemPrivilegeConverter}.
     */
    public Converter getSystemPrivilegeConverter() {
        return new SystemPrivilegeConverter(userFacade);
    }

    /**
     * Gets the {@link Converter} for {@link Outlet} objects.
     *
     * @return {@link Converter} for {@link Outlet} objects
     */
    public Converter getOutletConverter() {
        return new OutletConverter(outletFacade);
    }

    public Converter getWeatherLocationConverter() {
        return new WeatherLocationConverter(listingFacade);
    }

    public Converter getWeatherSituationConverter() {
        return new WeatherSituationConverter(listingFacade);
    }

    public Converter getCurrencyConverter() {
        return new CurrencyConverter(listingFacade);
    }

    public Converter getFinancialMarketConverter() {
        return new FinancialMarketConverter(listingFacade);
    }

    public Converter getNewsItemAuthorDisplayConverter() {
        return new NewsItemAuthorDisplayConverter();
    }

    /**
     * Gets a {@link Converter} for {@link Concept} objects.
     *
     * @return {@link Converter} for {@link Concept} objects
     */
    public Converter getConceptConverter() {
        return new ConceptConverter(metaDataFacade);
    }

    /**
     * Gets a {@link Converter} for {@link WorkflowState} objects.
     *
     * @return {@link Converter} for {@link WorkflowState} objects
     */
    public Converter getWorkflowStateConverter() {
        return new WorkflowStateConverter(workflowFacade);
    }

    /**
     * Gets a {@link Converter} for {@link WorkflowStep} objects.
     *
     * @return {@link Converter} for {@link WorkflowStep} objects
     */
    public Converter getWorkflowStepConverter() {
        return new WorkflowStepConverter(workflowFacade);
    }

    /**
     * Gets a {@link Converter} for {@link Workflow} objects.
     *
     * @return {@link Converter} for {@link Workflow} objects
     */
    public Converter getWorkflowConverter() {
        return new WorkflowConverter(workflowFacade);
    }

    /**
     * Gets a {@link Converter} for {@link UserRole} objects.
     *
     * @return {@link Converter} for {@link UserRole} objects
     */
    public Converter getUserRoleConverter() {
        return new UserRoleConverter(userFacade);
    }

    /**
     * Gets a {@link Converter} for {@link UserAccount} objects.
     *
     * @return {@link Converter} for {@link UserAccount} objects
     */
    public Converter getUserAccountConverter() {
        return new UserAccountConverter(userFacade);
    }

    /**
     * Gets a {@link Converter} for {@link Department} objects.
     *
     * @return {@link Converter} for {@link Department} objects
     */
    public Converter getDepartmentConverter() {
        return new DepartmentConverter(outletFacade);
    }

    /**
     * Gets a {@link Converter} for enumerations.
     *
     * @return {@link Converter} for enumerations
     */
    public Converter getEnumTypeConverter() {
        return new EnumTypeConverter();
    }

    /**
     * Gets a {@link Converter} for NewsItemFields
     *
     * @return {@link Converter} for NewsItemFields
     */
    public Converter getNewsItemFieldConverter() {
        return new NewsItemFieldConverter();
    }

    /**
     * Gets a {@link Converter} for {@link EventCategory} objects.
     *
     * @return {@link Converter} for {@link EventCategory} objects
     */
    public Converter getEventCategoryConverter() {
        return new EventCategoryConverter(calendarFacade);
    }

    /**
     * Gets a {@link Converter} for {@link Locale} objects.
     *
     * @return {@link Converter} for {@link Locale} objects
     */
    public Converter getLocaleConverter() {
        return new LocaleConverter();
    }

    /**
     * Gets a {@link Converter} for {@link Edition} objects.
     *
     * @return {@link Converter} for {@link Edition} objects
     */
    public Converter getEditionConverter() {
        return new EditionConverter(outletFacade);
    }

    /**
     * Gets a {@link Converter} for {@link Section} objects.
     *
     * @return {@link Converter} for {@link Section} objects
     */
    public Converter getSectionConverter() {
        return new SectionConverter(outletFacade);
    }

    /**
     * Gets a {@link Conveter} for {@link Class}es.
     *
     * @return {@link Conveter} for {@link Class}es.
     */
    public Converter getClassConverter() {
        return new ClassConverter();
    }

    public Converter getEditionCandidateConverter() {
        return new EditionCandidateConverter();
    }

    /**
     * Gets a {@link Converter} for media repositories.
     *
     * @return {@link Converter} for media repositories
     */
    public Converter getMediaRepositoryConverter() {
        return new CatalogueConverter(catalogueFacade);
    }

    
    /**
     * Gets a {@link Converter} for {@link Rendition}s.
     * 
     * @return JSF {@link Converter} for {@link Rendition}s
     */
    public Converter getRenditionConverter() {
        return new RenditionConverter(catalogueFacade);
    }

    public Converter getLanguageConverter() {
        return new LanguageConverter(systemFacade);
    }

    public Converter getNewswireServiceConverter() {
        return new NewswireServiceConverter(newswireService);
    }
}
