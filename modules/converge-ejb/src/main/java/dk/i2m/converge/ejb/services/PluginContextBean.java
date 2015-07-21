/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.EnrichException;
import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.content.ContentTag;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemEditionState;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.content.forex.Rate;
import dk.i2m.converge.core.content.markets.MarketValue;
import dk.i2m.converge.core.content.weather.Forecast;
import dk.i2m.converge.core.logging.LogSubject;
import dk.i2m.converge.core.newswire.NewswireDecoderException;
import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.plugin.ArchiveException;
import dk.i2m.converge.core.search.QueueEntryOperation;
import dk.i2m.converge.core.search.QueueEntryType;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.ejb.facades.*;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Implementation of the {@link dk.i2m.converge.core.plugin.PluginContext}.
 *
 * @author Allan Lykke Christensen
 * @author Raymond Wanyoike
 */
@Stateless
public class PluginContextBean implements PluginContextBeanLocal {

    private static final Logger LOG = Logger.getLogger(PluginContextBean.class.getName());
    @EJB
    private SystemFacadeLocal systemFacade;
    @EJB
    private ConfigurationServiceLocal cfgService;
    @EJB
    private NewswireServiceLocal newswireService;
    @EJB
    private NotificationServiceLocal notificationService;
    @EJB
    private UserServiceLocal userService;
    @EJB
    private NewsItemFacadeLocal newsItemFacade;
    @EJB
    private SearchEngineLocal searchEngine;
    @EJB
    private ListingFacadeLocal listingFacade;
    @EJB
    private DaoServiceLocal daoService;
    @EJB
    private CatalogueFacadeLocal catalogueFacade;
    @EJB
    private MetaDataServiceLocal metaDataService;
    @EJB
    private OutletFacadeLocal outletFacade;
    private UserAccount currentUserAccount = null;

    @Override
    public void setCurrentUserAccount(UserAccount userAccount) {
        this.currentUserAccount = userAccount;
    }

    @Override
    public UserAccount getCurrentUserAccount() {
        return this.currentUserAccount;
    }

    @Override
    public String getWorkingDirectory() {
        return cfgService.getString(ConfigurationKey.WORKING_DIRECTORY);
    }

    @Override
    public NewswireItem createNewswireItem(NewswireItem item) {
        if (item.getTitle().trim().isEmpty()) {
            // TODO: I18n
            item.setTitle("Untitled");
        }
        return newswireService.create(item);
    }

    @Override
    public List<NewswireItem> findNewswireItemsByExternalId(String externalId) {
        return newswireService.findByExternalId(externalId);
    }

    @Override
    public void fetch(NewswireService service) throws NewswireDecoderException {
        try {
            newswireService.downloadNewswireService(service.getId());
        } catch (DataNotFoundException ex) {
            throw new NewswireDecoderException(ex);
        }
    }

    @Override
    public void dispatchMail(String to, String from, String subject,
            String content) {
        notificationService.dispatchMail(to, from, subject, content);
    }

    @Override
    public Notification createNotification(Notification notifcation) {
        return notificationService.create(notifcation);
    }

    @Override
    public List<UserAccount> findUserAccountsByRole(String roleName) {
        return userService.findUserAccountsByUserRoleName(roleName);
    }

    @Override
    public List<NewsItem> findNewsItemsByStateAndOutlet(String stateName,
            Outlet outlet) {
        return newsItemFacade.findByStateAndOutlet(stateName, outlet);
    }

    @Override
    public void index(NewsItem item) throws SearchEngineIndexingException {
        searchEngine.addToIndexQueue(QueueEntryType.NEWS_ITEM, item.getId(),
                QueueEntryOperation.UPDATE);
    }

    @Override
    public List<MarketValue> findMarketListing() {
        return listingFacade.findLatestMarketValues();
    }

    @Override
    public List<Rate> findForexListing() {
        return listingFacade.findLatestForexRates();
    }

    @Override
    public List<Forecast> findWeatherForecast() {
        return listingFacade.findLatestForecasts();
    }

    @Override
    public ContentTag findOrCreateContentTag(String name) {
        Map<String, Object> params
                = QueryBuilder.with("name", name).parameters();

        ContentTag tag;

        try {
            tag = daoService.findObjectWithNamedQuery(ContentTag.class,
                    ContentTag.FIND_BY_NAME, params);
        } catch (DataNotFoundException ex) {
            tag = daoService.create(new ContentTag(name));
        }

        return tag;
    }

    @Override
    public Catalogue findCatalogue(Long catalogueId) {
        try {
            return catalogueFacade.findCatalogueById(catalogueId);
        } catch (DataNotFoundException ex) {
            return null;
        }
    }

    @Override
    public Rendition findRenditionByName(String name) {
        try {
            return catalogueFacade.findRenditionByName(name);
        } catch (DataNotFoundException ex) {
            return null;
        }
    }

    @Override
    public String archive(File file, Long catalogueId, String fileName) throws
            ArchiveException {
        try {
            Catalogue catalogue = catalogueFacade.findCatalogueById(catalogueId);
            return catalogueFacade.archive(file, catalogue, fileName);
        } catch (DataNotFoundException ex) {
            throw new ArchiveException(ex);
        } catch (IOException ex) {
            throw new ArchiveException(ex);
        }
    }

    @Override
    public MediaItemRendition createMediaItemRendition(File file,
            Long mediaItemId, Long renditionId, String filename,
            String contentType) throws IOException {
        try {
            MediaItem mediaItem = catalogueFacade.findMediaItemById(mediaItemId);
            Rendition rendition = catalogueFacade.findRenditionById(renditionId);
            return catalogueFacade.create(file, mediaItem, rendition, filename,
                    contentType, false);
        } catch (DataNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public MediaItemRendition updateMediaItemRendition(java.io.File file,
            String filename, String contentType,
            dk.i2m.converge.core.content.catalogue.MediaItemRendition mediaItemRendition)
            throws IOException {
        return catalogueFacade.update(file, filename, contentType,
                mediaItemRendition, false);
    }

    @Override
    public List<dk.i2m.converge.core.metadata.Concept> enrich(String story)
            throws EnrichException {
        return metaDataService.enrich(story);
    }

    @Override
    public String extractContent(MediaItemRendition mediaItemRendition) {
        return metaDataService.extractContent(mediaItemRendition);
    }

    @Override
    public String getConfiguration(ConfigurationKey key) {
        return cfgService.getString(key);
    }

    @Override
    public void index(NewswireItem item) throws SearchEngineIndexingException {
        newswireService.index(item);
    }

    @Override
    public void log(dk.i2m.converge.core.logging.LogSeverity severity,
            java.lang.String message, java.lang.Object[] messageArguments,
            java.lang.Object origin,
            java.lang.Object originId) {
        String msg = MessageFormat.format(message, messageArguments);
        systemFacade.log(severity, msg, origin, String.valueOf(originId));
    }

    @Override
    public void log(dk.i2m.converge.core.logging.LogSeverity severity,
            java.lang.String message, java.lang.Object origin,
            java.lang.Object originId) {
        systemFacade.log(severity, message, origin, String.valueOf(
                originId));
    }

    @Override
    public void log(dk.i2m.converge.core.logging.LogSeverity severity,
            java.lang.String message, java.lang.Object[] messageArguments,
            List<LogSubject> subjects) {
        systemFacade.log(severity, message, subjects);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Outlet findOutletById(Long id) throws DataNotFoundException {
        return outletFacade.findOutletById(id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Edition findNextEdition(Long id) throws DataNotFoundException {
        Outlet outlet = outletFacade.findOutletById(id);
        return outletFacade.findNextEdition(outlet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Edition updateEdition(Edition edition) {
        return outletFacade.updateEdition(edition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Edition createEdition(Edition edition) {
        return outletFacade.createEdition(edition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NewsItemPlacement createPlacement(NewsItemPlacement placement) {
        return newsItemFacade.createPlacement(placement);
    }

    @Override
    public NewsItemEditionState addNewsItemEditionState(Long editionId, Long newsItemId, String property, String value) {
        return newsItemFacade.addNewsItemEditionState(editionId, newsItemId, property, value);
    }

    @Override
    public NewsItemEditionState updateNewsItemEditionState(NewsItemEditionState newsItemEditionState) {
        return newsItemFacade.updateNewsItemEditionState(newsItemEditionState);
    }

    @Override
    public NewsItemEditionState findNewsItemEditionState(Long editionId, Long newsItemId, String property) throws DataNotFoundException {
        return newsItemFacade.findNewsItemEditionState(editionId, newsItemId, property);
    }

    @Override
    public NewsItemEditionState findNewsItemEditionStateOrCreate(Long editionId, Long newsItemId, String property, String value) {
        return newsItemFacade.findNewsItemEditionStateOrCreate(editionId, newsItemId, property, value);
    }
}
