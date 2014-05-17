/*
 *  Copyright (C) 2010 - 2012 Interactive Media Management
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
package dk.i2m.converge.core.plugin;

import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.EnrichException;
import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemEditionState;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.content.forex.Rate;
import dk.i2m.converge.core.content.markets.MarketValue;
import dk.i2m.converge.core.content.weather.Forecast;
import dk.i2m.converge.core.logging.LogSubject;
import dk.i2m.converge.core.newswire.NewswireDecoderException;
import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.Outlet;
import dk.i2m.converge.core.workflow.WorkflowState;
import java.util.List;

/**
 * Context of which a plug-in is being executed.
 *
 * @author Allan Lykke Christensen
 * @author Raymond Wanyoike
 */
public interface PluginContext {

    /**
     * Logs activity.
     *
     * @param severity Severity of the log statement
     * @param message Message to log
     * @param origin Origin of the log
     * @param originId ID of the origin
     */
    void log(dk.i2m.converge.core.logging.LogSeverity severity,
            java.lang.String message, java.lang.Object origin,
            java.lang.Object originId);

    /**
     * Logs activity.
     *
     * @param severity Severity of the log statement
     * @param message Message to log
     * @param messageArguments Arguments for the message
     * @param origin Origin of the log
     * @param originId ID of the origin
     */
    void log(dk.i2m.converge.core.logging.LogSeverity severity,
            java.lang.String message, java.lang.Object[] messageArguments,
            java.lang.Object origin,
            java.lang.Object originId);

    /**
     * Logs activity.
     *
     * @param severity Severity of the log statement
     * @param message Message to log
     * @param messageArguments Arguments for the message
     * @param subjects {@link List} of subjects relating to the log
     */
    void log(dk.i2m.converge.core.logging.LogSeverity severity,
            java.lang.String message, java.lang.Object[] messageArguments,
            List<LogSubject> subjects);

    /**
     * Gets a given configuration from the database.
     * <p/>
     * @param key Key of the configuration
     * @return Configuration matching the key, or {@code null} if nothing
     * matched the key
     */
    String getConfiguration(ConfigurationKey key);

    /**
     * Gets the working directory of Converge.
     *
     * @return String containing the working directory of Converge
     */
    String getWorkingDirectory();

    /**
     * Creates a {@link NewswireItem} in the Converge database.
     *
     * @param item {@link NewswireItem} to create
     * @return Created {@link NewswireItem}
     */
    NewswireItem createNewswireItem(NewswireItem item);

    /**
     * Finds a {@link List} of {@link NewswireItem}s with a given external
     * identifier.
     *
     * @param externalId External identifier of the {@link NewswireItem}
     * @return {@link List} of {@link NewswireItem}s with the given
     * {@code externalId}
     */
    List<NewswireItem> findNewswireItemsByExternalId(String externalId);

    /**
     * Fetches the newswire items of a given {@link NewswireService}.
     *
     * @param service {@link NewswireService} to fetch
     * @throws NewswireDecoderException If the decoder of the service could not
     * fetch the items
     */
    void fetch(NewswireService service) throws NewswireDecoderException;

    /**
     * Indexes a given {@link NewswireItem} in the search engine.
     *
     * @param item {@link NewswireItem} to index
     * @throws SearchEngineIndexingException If the {@link NewswireItem} could
     * not be indexed
     */
    void index(NewswireItem item) throws SearchEngineIndexingException;

    /**
     * Find or create a newswire {@link ContentTag}.
     * <p/>
     * @param name Name of the {@link ContentTag}
     * @return {@link ContentTag} for the given name
     */
    dk.i2m.converge.core.content.ContentTag findOrCreateContentTag(
            java.lang.String name);

    /**
     * Dispatches e-mail.
     *
     * @param to Recipient
     * @param from Addressee
     * @param subject Subject of the e-mail
     * @param content Body of the e-mail
     */
    void dispatchMail(String to, String from, String subject, String content);

    /**
     * Creates a {@link Notification}.
     *
     * @param notifcation {@link Notification} to create
     * @return Created {@link Notification}
     */
    Notification createNotification(Notification notifcation);

    /**
     * Gets the {@link UserAccount} of the currently logged in user.
     *
     * @return {@link UserAccount} of the currently logged in user, or
     * {@code null} if the {@link UserAccount} is not available (e.g. invoked by
     * a system timer)
     *
     */
    UserAccount getCurrentUserAccount();

    /**
     * Finds {@link UserAccount}s in a given role.
     * <p/>
     * @param roleName Name of the user role
     * @return {@link List} of {@link UserAccount}s with the given role
     */
    List<UserAccount> findUserAccountsByRole(String roleName);

    /**
     * Finds {@link NewsItem}s by their {@link WorkflowState} and
     * {@link Outlet}.
     * <p/>
     * @param stateName Name of the {@link WorkflowState}
     * @param outlet {@link Outlet} of the {@link NewsItem}s
     * @return {@link List} of {@link NewsItem}s in the given
     * {@link WorkflowState} and {@link Outlet}
     */
    List<NewsItem> findNewsItemsByStateAndOutlet(String stateName, Outlet outlet);

    /**
     * Indexes a given {@link NewsItem} in the search engine.
     *
     * @param item {@link NewsItem} to index
     * @throws SearchEngineIndexingException If the {@link NewsItem} could not
     * be indexed
     */
    void index(NewsItem item) throws SearchEngineIndexingException;

    /**
     * Gets a {@link List} of the latest {@link FinancialMarket} values.
     *
     * @return {@link List} of latest {@link FinancialMarket} values
     */
    List<MarketValue> findMarketListing();

    /**
     * Gets a {@link List} of the latest forex {@Link Rate}s.
     * <p/>
     * @return {@link List} of the latest forex {@Link Rate}s
     */
    List<Rate> findForexListing();

    List<Forecast> findWeatherForecast();

    Catalogue findCatalogue(Long catalogueId);

    Rendition findRenditionByName(String name);

    /**
     * Archives a {@link File} in a
     * {@link dk.i2m.converge.core.content.catalogue.Catalogue}.
     *
     * @param file {@link File} to archive
     * @param catalogueId Unique identifier of the
     * {@link dk.i2m.converge.core.content.catalogue.Catalogue} where the file
     * should be archived
     * @param fileName Name of the file
     * @return Path and name of the archived file
     * @throws ArchiveException If the file could not be archived in the given
     * catalogue
     */
    java.lang.String archive(java.io.File file, Long catalogueId,
            String fileName) throws ArchiveException;

    dk.i2m.converge.core.content.catalogue.MediaItemRendition createMediaItemRendition(
            java.io.File file, java.lang.Long mediaItemId,
            java.lang.Long renditionId, java.lang.String filename,
            java.lang.String contentType) throws
            java.lang.IllegalArgumentException, java.io.IOException;

    dk.i2m.converge.core.content.catalogue.MediaItemRendition updateMediaItemRendition(
            java.io.File file, String filename, String contentType,
            dk.i2m.converge.core.content.catalogue.MediaItemRendition mediaItemRendition)
            throws java.io.IOException;

    /**
     * Discover {@link Concept}s matching the given story.
     *
     * @param story Story for which to discover {@link Concept}s
     * @return {@link List} of {@link Concept}s discovered by OpenCalais for the
     * given story
     * @throws EnrichException * If the story could not be enriched due to
     * connectivity or overload to the OpenCalais server
     */
    List<dk.i2m.converge.core.metadata.Concept> enrich(String story) throws EnrichException;

    /**
     * Extract content from a {@link MediaItem}. This is mainly used for
     * documents such as PDF and Word. If no content could be detected an empty
     * String is returned.
     * <p/>
     * @param mediaItemRendition Rendition of the {@link MediaItem} for which to
     * extract content
     * @return Content detected inside the {@code mediaItemRendition}, or an
     * Empty string if nothing was detected
     */
    String extractContent(MediaItemRendition mediaItemRendition);

    /**
     * Finds an {@link Outlet} by its unique identifier.
     *
     * @param id Unique identifier of the {@link Outlet}
     * @return {@link Outlet} matching the unique identifier
     * @throws DataNotFoundException If an {@link Outlet} with the given
     * {@code id} could not be found
     */
    Outlet findOutletById(Long id) throws DataNotFoundException;

    /**
     * Finds the next {@link Edition} of a given {@link Outlet}.
     *
     * @param id Unique identifier of the {@link Outlet}
     * @return Next {@link Edition} for the given {@link Outlet}
     * @throws DataNotFoundException If the {@link Outlet} does not have any
     * future {@link Edition}s
     */
    Edition findNextEdition(Long id) throws DataNotFoundException;

    /**
     * Update an existing {@link Edition}.
     *
     * @param edition {@link Edition} to update
     * @return Updated {@link Edition}
     */
    Edition updateEdition(Edition edition);

    /**
     * Creates a new {@link Edition}.
     *
     * @param edition {@link Edition} to create
     * @return Created {@link Edition}
     */
    Edition createEdition(Edition edition);

    /**
     * Creates a new {@link NewsItemPlacement} for a {@link NewsItem}.
     *
     * @param placement {@link NewsItemPlacement} to create
     * @return Created {@link NewsItemPlacement}
     */
    NewsItemPlacement createPlacement(NewsItemPlacement placement);

    /**
     * Create a new {@link NewsItemEditionState} for a {@link NewsItem}.
     *
     * @param edition {@link Edition} id the {@link NewsItem} belongs to
     * @param newsItem {@link NewsItem} id that will have a new state created
     * @param property State property
     * @param value Property value
     */
    NewsItemEditionState addNewsItemEditionState(Long editionId, Long newsItemId, String property, String value);

    /**
     * Update an existing {@link NewsItemEditionState}
     *
     * @param newsItemEditionState {@link NewsItemEditionState} to update
     * @return Updated {@link NewsItemEditionState}
     */
    NewsItemEditionState updateNewsItemEditionState(NewsItemEditionState newsItemEditionState);
}
