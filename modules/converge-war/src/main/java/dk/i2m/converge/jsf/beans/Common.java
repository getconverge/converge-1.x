/*
 * Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.Announcement;
import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.calendar.EventCategory;
import dk.i2m.converge.core.content.AssignmentType;
import dk.i2m.converge.core.content.Language;
import dk.i2m.converge.core.content.NewsItemField;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.MediaItemStatus;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.content.forex.Currency;
import dk.i2m.converge.core.content.markets.FinancialMarket;
import dk.i2m.converge.core.content.weather.Location;
import dk.i2m.converge.core.content.weather.Situation;
import dk.i2m.converge.core.metadata.*;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.plugin.*;
import dk.i2m.converge.core.security.*;
import dk.i2m.converge.core.workflow.*;
import dk.i2m.converge.ejb.facades.*;
import dk.i2m.converge.ejb.services.NewswireServiceLocal;
import dk.i2m.jsf.JsfUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.StringUtils;

/**
 * JSF application bean containing common data.
 *
 * @author Allan Lykke Christensen
 */
public class Common {

    private static final Logger LOG = Logger.getLogger(Common.class.toString());

    @EJB private CalendarFacadeLocal calendarFacade;

    @EJB private UserFacadeLocal userFacade;

    @EJB private OutletFacadeLocal outletFacade;

    @EJB private SystemFacadeLocal systemFacade;

    @EJB private WorkflowFacadeLocal workflowFacade;

    @EJB private MetaDataFacadeLocal metaDataFacade;

    @EJB private NewswireServiceLocal newswireService;

    @EJB private CatalogueFacadeLocal catalogueFacade;

    @EJB private ListingFacadeLocal listingFacade;
    
    private ResourceBundle i18n = JsfUtils.getResourceBundle(Bundle.i18n.name());

    /**
     * Get the local time on the server.
     * 
     * @return Local time on the server
     */
    public Date getLocalTime() {
        return java.util.Calendar.getInstance().getTime();
    }
    
    public TimeZone getSystemTimeZone() {
        try {
            String timeZone = systemFacade.getProperty(ConfigurationKey.TIME_ZONE);
            return TimeZone.getTimeZone(timeZone);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Could not obtain system time zone", ex.getMessage());
            return TimeZone.getDefault();
        }
    }

    /**
     * Gets the dynamic {@link UserRole}s of the system.
     *
     * @return {@link Map} of dynamic {@link UserRole}s.
     */
    public Map<String, UserRole> getUserRoles() {
        List<UserRole> original = userFacade.getUserRoles();
        Map<String, UserRole> map = new LinkedHashMap<String, UserRole>();

        for (UserRole userRole : original) {
            map.put(userRole.getName(), userRole);
        }

        return map;
    }

    /**
     * Gets a {@link Map} of {@link Outlet}s from the database.
     *
     * @return {@link Map} of {@link Outlet}s from the database.
     */
    public Map<String, Outlet> getOutlets() {
        List<Outlet> original = outletFacade.findAllOutlets();
        Map<String, Outlet> map = new LinkedHashMap<String, Outlet>();

        for (Outlet outlet : original) {
            map.put(outlet.getTitle(), outlet);
        }

        return map;
    }

    /**
     * Gets a {@link Map} of {@link Workflow}s from the database.
     *
     * @return {@link Map} of {@link Workflow}s from the database.
     */
    public Map<String, Workflow> getWorkflows() {
        List<Workflow> original = workflowFacade.findAllWorkflows();
        Map<String, Workflow> map = new LinkedHashMap<String, Workflow>();

        for (Workflow workflow : original) {
            map.put(workflow.getName(), workflow);
        }

        return map;
    }

    public Map<String, WorkflowState> getWorkflowStates() {
        List<Workflow> original = workflowFacade.findAllWorkflows();
        Map<String, WorkflowState> map = new LinkedHashMap<String, WorkflowState>();

        for (Workflow workflow : original) {
            for (WorkflowState state : workflow.getStates()) {
                map.put(workflow.getName() + " >> " + state.getName(), state);
            }
        }

        return map;
    }

    /**
     * Gets a {@link Map} of available {@link WorkflowStateType}s.
     *
     * @return {@link Map} of available {@link WorkflowStateType}s
     */
    public Map<String, WorkflowStateType> getWorkflowStateTypes() {
        Map<String, WorkflowStateType> types = new LinkedHashMap<String, WorkflowStateType>();

        for (WorkflowStateType type : WorkflowStateType.values()) {
            types.put(i18n.getString("Generic_WORKFLOW_STATE_TYPE_" + type.name()), type);
        }

        return types;
    }

    /**
     * Gets a {@link Map} of available {@link NewsItemField} user fields.
     *
     * @return {@link Map} of available {@link NewsItemField} user fields
     */
    public Map<String, NewsItemField> getNewsItemFields() {
        Map<String, NewsItemField> fields = new LinkedHashMap<String, NewsItemField>();

        for (NewsItemField field : NewsItemField.values()) {
            fields.put(i18n.getString("Generic_NEWS_ITEM_FIELD_" + field.name()), field);
        }

        return fields;
    }

    /**
     * Gets a {@link Map} of available {@link Privilege}s.
     *
     * @return {@link Map} of available {@link Privilege}s
     */
    public Map<String, Privilege> getPrivileges() {
        Map<String, Privilege> privileges = new LinkedHashMap<String, Privilege>();

        for (SystemPrivilege privilege : SystemPrivilege.values()) {
            String lbl = i18n.getString("Generic_" + privilege.getClass().getName() + "." + privilege.name());
            privileges.put(lbl, new Privilege(privilege));
        }

        return privileges;
    }

    /**
     * Gets a {@link Map} of available {@link OutletType}s.
     *
     * @return {@link Map} of available {@link OutletType}s
     */
    public Map<String, OutletType> getOutletTypes() {
        Map<String, OutletType> types = new LinkedHashMap<String, OutletType>();
        
        for (OutletType type : OutletType.values()) {
            types.put(i18n.getString("Generic_OUTLET_TYPE_" + type.name()), type);
        }

        return types;
    }

    /**
     * Gets a {@link Map} of available {@link FeeType}s.
     *
     * @return {@link Map} of available {@link FeeType}s
     */
    public Map<String, FeeType> getFeeTypes() {
        Map<String, FeeType> types = new LinkedHashMap<String, FeeType>();

        for (FeeType type : FeeType.values()) {
            types.put(i18n.getString("Generic_FEE_TYPE_" + type.name()), type);
        }

        return types;
    }

    /**
     * Gets a {@link Map} of available {@link EmploymentType}s.
     *
     * @return {@link Map} of available {@link EmploymentType}s
     */
    public Map<String, EmploymentType> getEmploymentTypes() {
        Map<String, EmploymentType> types =
                new LinkedHashMap<String, EmploymentType>();

        for (EmploymentType type : EmploymentType.values()) {
            types.put(i18n.getString("Generic_EMPLOYMENT_TYPE_" + type.name()), type);
        }

        return types;
    }

    /**
     * Gets a {@link Map} of available {@link EmploymentType}s.
     *
     * @return {@link Map} of available {@link EmploymentType}s
     */
    public Map<String, WorkflowStatePermission> getWorkflowStatePermissions() {
        Map<String, WorkflowStatePermission> types = new LinkedHashMap<String, WorkflowStatePermission>();

        for (WorkflowStatePermission permission : WorkflowStatePermission.values()) {
            types.put(i18n.getString("Generic_WORKFLOW_STATE_PERMISSION_" + permission.name()), permission);
        }

        return types;
    }

    /**
     * Gets the current year.
     *
     * @return Current year
     */
    public int getCurrentYear() {
        return java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    }

    public Map<String, String> getHours() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < 10; i++) {
            map.put("0" + i, "0" + i);
        }
        for (int i = 10; i < 24; i++) {
            map.put("" + i, "" + i);
        }

        return map;
    }

    public Map<String, String> getMinutes() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("00", "00");
        map.put("10", "10");
        map.put("20", "20");
        map.put("30", "30");
        map.put("40", "40");
        map.put("50", "50");
        return map;
    }

    public Map<String, EventCategory> getEventCategories() {
        Map<String, EventCategory> map =
                new LinkedHashMap<String, EventCategory>();

        for (EventCategory category : calendarFacade.findAllCategories()) {
            map.put(category.getName(), category);
        }
        return map;
    }

    public Map<String, String> getEventCategoriesAsStrings() {
        Map<String, String> map =
                new LinkedHashMap<String, String>();

        for (EventCategory category : calendarFacade.findAllCategories()) {
            map.put(category.getName(), category.getName());
        }
        return map;
    }

    public Map<String, Concept> getMetadataSubjects() {
        List<Concept> subjects = metaDataFacade.findConceptByType(Subject.class);
        Map<String, Concept> map = new LinkedHashMap<String, Concept>();

        for (Concept subject : subjects) {
            map.put(((Subject) subject).getFullTitle(), subject);
        }

        return map;
    }

    public Map<String, Class> getMetadataTypes() {

        Map<String, Class> map = new LinkedHashMap<String, Class>();

        String label = i18n.getString("Generic_" + Subject.class.getName() + "_NAME");
        map.put(label, Subject.class);

        label = i18n.getString("Generic_" + Person.class.getName() + "_NAME");
        map.put(label, Person.class);

        label = i18n.getString("Generic_" + Organisation.class.getName() + "_NAME");
        map.put(label, Organisation.class);

        label = i18n.getString("Generic_" + GeoArea.class.getName() + "_NAME");
        map.put(label, GeoArea.class);

        label = i18n.getString("Generic_" + PointOfInterest.class.getName() + "_NAME");
        map.put(label, PointOfInterest.class);

        return map;
    }

    public Map<String, Concept> getMetadataSubjectsWithType() {
        List<Concept> subjects = metaDataFacade.findConceptByType(Subject.class);
        Map<String, Concept> map = new LinkedHashMap<String, Concept>();

        for (Concept subject : subjects) {
            String label = ((Subject) subject).getFullTitle();
            map.put(StringUtils.abbreviate(label, 20), subject);
        }

        return map;
    }

//    private Map<String, Concept> getMetadataTypeWithType(Class clazz) {
//        List<Concept> concepts = metaDataFacade.findConceptByType(clazz);
//        Map<String, Concept> map = new LinkedHashMap<String, Concept>();
//
//
//        String typeName = JsfUtils.getResourceBundle().getString(clazz.getName()
//                + "_NAME");
//
//        for (Concept concept : concepts) {
//            String label = concept.getName() + " (" + typeName + ")";
//            map.put(StringUtils.abbreviate(label, 40), concept);
//        }
//
//        return map;
//    }
    public String getApplicationVersion() {
        return systemFacade.getApplicationVersion();
    }

    public String getShortApplicationVersion() {
        return systemFacade.getShortApplicationVersion();
    }

    /**
     * Gets a unique URL encoded build number.
     *
     * @return Unique URL encoded build number
     */
    public String getBuildNumber() {
        try {
            String buildDate = systemFacade.getApplicationVersion();
            return URLEncoder.encode(buildDate, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Long now = java.util.Calendar.getInstance().getTimeInMillis();
            return "" + now;
        }
    }

    public List<NewswireService> getNewswireServicesList() {
        return newswireService.findActiveNewswireServices();
    }

    /**
     * Gets a {@link Map} of available languages supported by the application. The languages are fetched from the JavaServer Faces configuration.
     *
     * @return {@link Map} of supported languages
     */
    public Map<String, String> getAvailableLanguages() {
        Map<String, String> languages = new LinkedHashMap<String, String>();

        Application app = FacesContext.getCurrentInstance().getApplication();

        Locale loc = app.getDefaultLocale();
        languages.put(loc.getDisplayLanguage(loc), loc.getLanguage());

        for (Iterator i = app.getSupportedLocales(); i.hasNext();) {
            loc = (Locale) i.next();
            languages.put(loc.getDisplayLanguage(loc), loc.getLanguage());
        }

        return languages;
    }

    /**
     * Gets a {@link Map} of available time zones.
     *
     * @return {@link Map} of available time zones.
     */
    public Map<String, String> getTimeZones() {
        Date today = new Date();
        Map<String, String> timeZones = new LinkedHashMap<String, String>();
        String[] zoneIds = TimeZone.getAvailableIDs();

        for (String timeZone : zoneIds) {
            TimeZone tz = TimeZone.getTimeZone(timeZone);
            String shortTimeZoneName = tz.getDisplayName(tz.inDaylightTime(today), TimeZone.SHORT);
            String longTimeZoneName = tz.getDisplayName(tz.inDaylightTime(today), TimeZone.LONG);
            timeZones.put(longTimeZoneName + " (" + shortTimeZoneName + ")", shortTimeZoneName);
        }

        return timeZones;
    }

    public Map<String, UserAccount> getUsers() {
        Map<String, UserAccount> users = new LinkedHashMap<String, UserAccount>();

        for (UserAccount userAccount : userFacade.getUsers()) {
            users.put(userAccount.getFullName() + " (" + userAccount.getUsername() + ")", userAccount);
        }

        return users;
    }

    public Map<String, String> getNewswireDecoders() {
        Map<String, String> decoders = new LinkedHashMap<String, String>();

        Map<String, NewswireDecoder> discovered = newswireService.getNewswireDecoders();

        for (NewswireDecoder decoder : discovered.values()) {
            decoders.put(decoder.getName(), decoder.getClass().getName());
        }

        return decoders;
    }

    public Map<String, String> getWorkflowActions() {
        Map<String, String> actions = new LinkedHashMap<String, String>();

        Map<String, Plugin> plugins = systemFacade.getPlugins();

        for (Plugin plugin : plugins.values()) {
            if (plugin instanceof WorkflowAction) {
                WorkflowAction action = (WorkflowAction) plugin;
                actions.put(action.getName(), action.getClass().getName());
            }
        }

        return actions;
    }

    public Map<String, String> getWorkflowValidators() {
        Map<String, String> validators = new LinkedHashMap<String, String>();

        Map<String, Plugin> plugins = systemFacade.getPlugins();

        for (Plugin plugin : plugins.values()) {
            if (plugin instanceof WorkflowValidator) {
                WorkflowValidator validator = (WorkflowValidator) plugin;
                validators.put(validator.getName(), validator.getClass().getName());
            }
        }

        return validators;
    }

    public Map<String, String> getOutletActions() {
        Map<String, String> actions = new LinkedHashMap<String, String>();

        Map<String, Plugin> plugins = systemFacade.getPlugins();

        for (Plugin plugin : plugins.values()) {
            if (plugin instanceof EditionAction) {
                EditionAction action = (EditionAction) plugin;
                actions.put(action.getName(), action.getClass().getName());
            }
        }

        return actions;
    }

    /**
     * Gets a {@link Map} of discovered {@link Catalogue} actions.
     *
     * @return {@link Map} of discovered {@link Catalogue} actions
     */
    public Map<String, String> getCatalogueActions() {
        Map<String, String> actions = new LinkedHashMap<String, String>();

        Map<String, Plugin> plugins = systemFacade.getPlugins();

        for (Plugin plugin : plugins.values()) {
            if (plugin instanceof CatalogueHook) {
                CatalogueHook action = (CatalogueHook) plugin;
                actions.put(action.getName(), action.getClass().getName());
            }
        }

        return actions;
    }

    /**
     * Gets a {@link Map} of discovered {@link NewsItem} actions.
     *
     * @return {@link Map} of discovered {@link NewsItem} actions
     */
    public Map<String, String> getNewsItemActions() {
        Map<String, String> actions = new LinkedHashMap<String, String>();

        Map<String, Plugin> plugins = systemFacade.getPlugins();

        for (Plugin plugin : plugins.values()) {
            if (plugin instanceof CatalogueHook) {
                CatalogueHook action = (CatalogueHook) plugin;
                actions.put(action.getName(), action.getClass().getName());
            }
        }

        return actions;
    }

    public Map<String, AssignmentType> getAssignmentTypes() {
        Map<String, AssignmentType> types = new LinkedHashMap<String, AssignmentType>();

        for (AssignmentType type : AssignmentType.values()) {
            types.put(i18n.getString("MyProfile_ASSIGNMENT_TYPE_" + type.name()), type);
        }

        return types;
    }

    /**
     * Gets a {@link Map} of writable media repositories.
     *
     * @return {@link Map} of writable media repositories
     */
    public Map<String, Catalogue> getWritableMediaRepositories() {
        Map<String, Catalogue> repositories = new LinkedHashMap<String, Catalogue>();

        List<Catalogue> mediaRepositories = catalogueFacade.findWritableCatalogues();

        for (Catalogue repository : mediaRepositories) {
            repositories.put(repository.getName(), repository);
        }

        return repositories;
    }

    public Map<String, MediaItemStatus> getMediaItemStatuses() {
        Map<String, MediaItemStatus> statuses = new LinkedHashMap<String, MediaItemStatus>();

        for (MediaItemStatus status : MediaItemStatus.values()) {
            String lbl = i18n.getString("Generic_MEDIA_ITEM_STATUS_" + status.name());
            statuses.put(lbl, status);
        }

        return statuses;
    }

    public Map<String, MediaItemStatus> getMediaItemOwnerStatuses() {
        Map<String, MediaItemStatus> statuses = new LinkedHashMap<String, MediaItemStatus>();

        statuses.put(i18n.getString("Generic_MEDIA_ITEM_STATUS_OWNER_" + MediaItemStatus.SUBMITTED.name()), MediaItemStatus.SUBMITTED);
        statuses.put(i18n.getString("Generic_MEDIA_ITEM_STATUS_OWNER_" + MediaItemStatus.UNSUBMITTED.name()), MediaItemStatus.UNSUBMITTED);

        return statuses;
    }

    public Map<String, Rendition> getRenditions() {
        Map<String, Rendition> renditions = new LinkedHashMap<String, Rendition>();

        List<Rendition> results = catalogueFacade.findRenditions();

        for (Rendition rendition : results) {
            renditions.put(rendition.getLabel(), rendition);
        }

        return renditions;
    }

    public Subject[] getParentSubjects() {
        List<Subject> parents = metaDataFacade.findTopLevelSubjects();
        Subject[] topSubjects = parents.toArray(new Subject[parents.size()]);
        return topSubjects;
    }

    public boolean isAnnouncementsAvailable() {
        return (getPublishedAnnouncements().size() > 0);
    }

    public List<Announcement> getPublishedAnnouncements() {
        return systemFacade.getPublishedAnnouncements();
    }

    public Map<String, Language> getLanguages() {
        List<Language> languages = systemFacade.getLanguages();
        Map<String, Language> map = new LinkedHashMap<String, Language>();

        for (Language language : languages) {
            map.put(language.getName(), language);
        }
        return map;
    }

    public Map<String, Currency> getCurrencies() {
        List<Currency> currList = listingFacade.findCurrencies();
        Map<String, Currency> currencies = new LinkedHashMap<String, Currency>();

        for (Currency currency : currList) {
            currencies.put(currency.getName() + " (" + currency.getShortName() + ")", currency);
        }

        return currencies;
    }

    public Map<String, FinancialMarket> getFinancialMarkets() {
        List<FinancialMarket> marketList = listingFacade.findFinancialMarkets();
        Map<String, FinancialMarket> financialMarkets = new LinkedHashMap<String, FinancialMarket>();

        for (FinancialMarket market : marketList) {
            financialMarkets.put(market.getName() + " (" + market.getShortName() + ")", market);
        }

        return financialMarkets;
    }

    public Map<String, Location> getWeatherLocation() {
        List<Location> locationList = listingFacade.findWeatherLocations();
        Map<String, Location> locations = new LinkedHashMap<String, Location>();

        for (Location location : locationList) {
            locations.put(location.getName(), location);
        }

        return locations;
    }

    public Map<String, Situation> getWeatherSituation() {
        List<Situation> situationList = listingFacade.findWeatherSituations();
        Map<String, Situation> situations = new LinkedHashMap<String, Situation>();

        for (Situation situation : situationList) {
            situations.put(situation.getName(), situation);
        }

        return situations;
    }
}
