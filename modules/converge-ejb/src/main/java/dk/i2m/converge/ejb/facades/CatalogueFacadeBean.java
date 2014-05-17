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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.Notification;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.*;
import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.newswire.NewswireItemAttachment;
import dk.i2m.converge.core.plugin.CatalogueEvent;
import dk.i2m.converge.core.plugin.CatalogueEventException;
import dk.i2m.converge.core.plugin.CatalogueHook;
import dk.i2m.converge.core.search.QueueEntryOperation;
import dk.i2m.converge.core.search.QueueEntryType;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.utils.StringUtils;
import dk.i2m.converge.ejb.services.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Stateless enterprise java bean providing a facade for interacting with
 * {@link Catalogue}s.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class CatalogueFacadeBean implements CatalogueFacadeLocal {

    private static final Logger LOG =
            Logger.getLogger(CatalogueFacadeBean.class.getName());

    @EJB private DaoServiceLocal daoService;

    @EJB private SearchEngineLocal searchEngine;

    @EJB private UserFacadeLocal userFacade;

    @EJB private PluginContextBeanLocal pluginContext;

    @EJB private MetaDataServiceLocal metaDataService;

    @EJB private SystemFacadeLocal systemFacade;

    @EJB private NotificationServiceLocal notificationService;

    @EJB private CatalogueServiceLocal catalogueService;

    @Resource private SessionContext ctx;

    /** {@inheritDoc } */
    @Override
    public Catalogue create(Catalogue catalogue) {
        return daoService.create(catalogue);
    }

    /** {@inheritDoc } */
    @Override
    public Catalogue update(Catalogue catalogue) {
        return daoService.update(catalogue);
    }

    /** {@inheritDoc } */
    @Override
    public void deleteCatalogueById(Long id) throws DataNotFoundException {
        daoService.delete(Catalogue.class, id);
    }

    /** {@inheritDoc } */
    @Override
    public List<Catalogue> findAllCatalogues() {
        return daoService.findAll(Catalogue.class);
    }

    /**
     * Finds a {@link List} of all enabled and writable {@link Catalogue}s.
     *
     * @return {@link List} of enabled writable {@link Catalogue}s
     */
    @Override
    public List<Catalogue> findWritableCatalogues() {
        return daoService.findWithNamedQuery(Catalogue.FIND_WRITABLE);
    }

    
    @Override
    public List<Catalogue> findCataloguesByUser(String username) {
        UserAccount ua;
        try {
            ua = userFacade.findById(username);
        } catch (DataNotFoundException ex) {
            return Collections.EMPTY_LIST;
        }

        List<Catalogue> catalogues = daoService.findWithNamedQuery(
                Catalogue.FIND_BY_USER,
                QueryBuilder.with(Catalogue.PARAM_FIND_BY_USER_USER, ua).
                parameters());

        return catalogues;
    }

    /**
     * Finds an existing {@link Catalogue} in the database.
     *
     * @param id Unique identifier of the {@link Catalogue}
     * @return {@link Catalogue} matching the given {@code id}
     * @throws DataNotFoundException If no {@link Catalogue} could be matched to the given {@code id}
     */
    @Override
    public Catalogue findCatalogueById(Long id) throws DataNotFoundException {
        return daoService.findById(Catalogue.class, id);
    }

    /**
     * Indexes enabled {@link Catalogue}s.
     *
     * @throws InvalidMediaRepositoryException  If the location of the {@link Catalogue} is not valid
     * @throws MediaRepositoryIndexingException If the location of the {@link Catalogue} could not be indexed
     */
    @Override
    public void indexCatalogues() throws InvalidMediaRepositoryException,
            MediaRepositoryIndexingException {
        Map<String, Object> parameters = QueryBuilder.with("status",
                MediaItemStatus.APPROVED).parameters();
        List<MediaItem> items = daoService.findWithNamedQuery(
                MediaItem.FIND_BY_STATUS, parameters);

        for (MediaItem item : items) {
            searchEngine.addToIndexQueue(QueueEntryType.MEDIA_ITEM, item.getId(),
                    QueueEntryOperation.UPDATE);
        }
    }

    /**
     * Finds a {@link List} of {@link Rendition}s.
     *
     * @return {@link List} of {@link Rendition}s
     */
    @Override
    public List<Rendition> findRenditions() {
        return daoService.findAll(Rendition.class);
    }

    /**
     * Finds a {@link Rendition} from its unique identifier.
     * <p/>
     * @param id Unique identifier of the {@link Rendition}
     * @return {@link Rendition} matching the unique {@code id}
     * @throws DataNotFoundException If the {@link Rendition} could not be found
     */
    @Override
    public Rendition findRenditionById(Long id) throws DataNotFoundException {
        return daoService.findById(Rendition.class, id);
    }

    /**
     * Finds a {@link Rendition} by its name.
     * <p/>
     * @param name Name of the {@link Rendition}
     * @return {@link Rendition} matching the name
     * @throws DataNotFoundException * If the {@link Rendition} could not be found
     */
    @Override
    public Rendition findRenditionByName(String name) throws
            DataNotFoundException {
        Map<String, Object> params =
                QueryBuilder.with("name", name).parameters();

        List<Rendition> results = daoService.findWithNamedQuery(
                Rendition.FIND_BY_NAME, params, 1);
        if (results.isEmpty()) {
            throw new DataNotFoundException();
        }
        return results.iterator().next();
    }

    @Override
    public Rendition create(Rendition rendition) {
        return daoService.create(rendition);
    }

    @Override
    public Rendition update(Rendition rendition) {
        return daoService.update(rendition);
    }

    /** {@inheritDoc } */
    @Override
    public void deleteRendition(Long id) {
        daoService.delete(Rendition.class, id);
    }

    /** {@inheritDoc } */
    @Override
    public MediaItemRendition create(File file, MediaItem item,
            Rendition rendition, String filename, String contentType,
            boolean executeHooks) throws
            IOException {
        Catalogue catalogue = item.getCatalogue();

        // Remove path from filename if file was uploaded from Windows
        String originalExtension = FilenameUtils.getExtension(filename);

        StringBuilder realFilename = new StringBuilder();
        realFilename.append(rendition.getId()).append(".");
        realFilename.append(originalExtension);

        // Check if there is already a rendition of this type
        MediaItemRendition mediaItemRendition;
        try {
            mediaItemRendition = item.findRendition(rendition);
        } catch (RenditionNotFoundException ex) {
            mediaItemRendition = new MediaItemRendition();
        }

        // Set-up the media item rendition
        //MediaItemRendition mediaItemRendition = new MediaItemRendition();
        mediaItemRendition.setMediaItem(item);
        mediaItemRendition.setFilename(realFilename.toString());
        mediaItemRendition.setSize(file.length());
        mediaItemRendition.setContentType(contentType);
        mediaItemRendition.setRendition(rendition);

        // Store file and set path
        String path = archive(file, catalogue, mediaItemRendition);

        // Load meta data into the rendition
        fillWithMetadata(mediaItemRendition);

        // Store the rendition in the database
        if (mediaItemRendition.getId() == null) {
            mediaItemRendition = daoService.create(mediaItemRendition);
        } else {
            mediaItemRendition = daoService.update(mediaItemRendition);
        }

        // Execute hooks
        if (executeHooks) {
            for (CatalogueHookInstance hookInstance : catalogue.getHooks()) {
                if (!hookInstance.isManual()) {
                    try {
                        if (hookInstance.isAsynchronous()) {
                            catalogueService.executeAsynchronousHook(
                                    item.getId(),
                                    hookInstance.getId(),
                                    CatalogueEvent.Event.UploadRendition);
                        } else {
                            catalogueService.executeHook(item.getId(),
                                    hookInstance.getId(),
                                    CatalogueEvent.Event.UploadRendition);
                        }
                    } catch (DataNotFoundException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
        }

        return mediaItemRendition;
    }

    /** {@inheritDoc } */
    @Override
    public MediaItemRendition update(File file, String filename,
            String contentType, MediaItemRendition mediaItemRendition,
            boolean executeHooks) throws
            IOException {
        Catalogue catalogue = mediaItemRendition.getMediaItem().getCatalogue();

        // Remove path from filename if file was uploaded from Windows
        String originalExtension = FilenameUtils.getExtension(filename);
        StringBuilder realFilename = new StringBuilder();
        realFilename.append(mediaItemRendition.getRendition().getId()).append(
                ".");
        realFilename.append(originalExtension);

        mediaItemRendition.setFilename(realFilename.toString());
        mediaItemRendition.setSize(file.length());
        mediaItemRendition.setContentType(contentType);

        // Store file
        archive(file, catalogue, mediaItemRendition);

        fillWithMetadata(mediaItemRendition);

        // Store rendition in database
        mediaItemRendition = daoService.update(mediaItemRendition);

        // Execute hooks
        if (executeHooks) {
            for (CatalogueHookInstance hookInstance : catalogue.getHooks()) {
                if (!hookInstance.isManual()) {
                    try {
                        if (hookInstance.isAsynchronous()) {
                            catalogueService.executeAsynchronousHook(
                                    mediaItemRendition.getMediaItem().
                                    getId(), hookInstance.getId(),
                                    CatalogueEvent.Event.UpdateRendition);
                        } else {
                            catalogueService.executeHook(mediaItemRendition.
                                    getMediaItem().
                                    getId(), hookInstance.getId(),
                                    CatalogueEvent.Event.UpdateRendition);
                        }
                    } catch (DataNotFoundException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage());
                    }
                }
            }
        }
        return mediaItemRendition;
    }

    /** {@inheritDoc } */
    @Override
    public MediaItemRendition update(MediaItemRendition mir) {
        return daoService.update(mir);
    }

    /** {@inheritDoc} */
    @Override
    public void executeHook(Long mediaItemId, Long hookInstanceId) throws
            DataNotFoundException {

        CatalogueHookInstance hookInstance =
                daoService.findById(CatalogueHookInstance.class, hookInstanceId);

        if (hookInstance.isAsynchronous()) {
            catalogueService.executeAsynchronousHook(mediaItemId, hookInstanceId,
                    CatalogueEvent.Event.UpdateRendition);
        } else {
            catalogueService.executeHook(mediaItemId, hookInstanceId,
                    CatalogueEvent.Event.UpdateRendition);
        }
    }

    @Override
    public void executeBatchHook(CatalogueHookInstance hookInstance,
            Long catalogueId) throws DataNotFoundException {
        LOG.log(Level.INFO, "Executing Batch Hook");
        CatalogueHook hook;
        try {
            hook = hookInstance.getHook();
        } catch (CatalogueEventException ex) {
            LOG.log(Level.SEVERE, "Could not instantiate hook", ex);
            return;
        }

        if (!hook.isSupportBatch()) {
            LOG.log(Level.INFO, "{0} doesn''t support batch execution", hook.
                    getName());
            return;
        }

        Long taskId = systemFacade.createBackgroundTask("Batch hook "
                + hookInstance.getLabel() + " (" + hook.getName() + ")");

        Catalogue catalogue = findCatalogueById(catalogueId);
        Map<String, Object> params = QueryBuilder.with("catalogue", catalogue).
                parameters();
        List<MediaItem> mediaItems = daoService.findWithNamedQuery(
                MediaItem.FIND_BY_CATALOGUE, params);

        for (MediaItem item : mediaItems) {
            LOG.log(Level.INFO, "Executing Batch Hook for Media Item #{0}",
                    item.getId());
            Long innerTaskId = systemFacade.createBackgroundTask("Executing "
                    + hookInstance.getLabel() + " for Media Item #"
                    + item.getId());

            catalogueService.executeHook(item.getId(), hookInstance.getId(),
                    CatalogueEvent.Event.UpdateRendition);

            systemFacade.removeBackgroundTask(innerTaskId);
        }

        systemFacade.removeBackgroundTask(taskId);
    }

    private void fillWithMetadata(MediaItemRendition mediaItemRendition) {
        // Discover meta data and format info
        Map<String, String> metaData =
                metaDataService.extract(mediaItemRendition.getFileLocation());

        for (String key : metaData.keySet()) {
            if (key.equalsIgnoreCase("width")) {
                mediaItemRendition.setWidth(Integer.valueOf(metaData.get(key)));
            } else if (key.equalsIgnoreCase("height")) {
                mediaItemRendition.setHeight(Integer.valueOf(metaData.get(key)));
            } else if (key.equalsIgnoreCase("colourSpace")) {
                mediaItemRendition.setColourSpace(metaData.get(key));
            } else if (key.equalsIgnoreCase("Resolution")) {
                mediaItemRendition.setResolution(Integer.valueOf(metaData.get(
                        key)));
            }
        }
    }

    /**
     * Deletes an existing {@link MediaItemRendition} from a {@link MediaItem}.
     * <p/>
     * @param id Unique identifier of the {@link MediaItemRendition}
     */
    @Override
    public void deleteMediaItemRenditionById(Long id) {
        try {
            MediaItemRendition mir = daoService.findById(
                    MediaItemRendition.class, id);
            File f = new File(mir.getFileLocation());
            if (f.exists()) {
                f.delete();
            } else {
                LOG.log(Level.WARNING,
                        "{0} does not exist and could not be deleted", f.
                        getAbsoluteFile().toString());
            }

            daoService.delete(MediaItemRendition.class, id);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "MediaItemRendition #{0} does not exist", id);
        }
    }

    /** {@inheritDoc } */
    @Override
    public MediaItem create(MediaItem mediaItem) {
        mediaItem.setCreated(Calendar.getInstance());
        mediaItem.setUpdated(mediaItem.getCreated());

        if (mediaItem.getId() == null) {
            mediaItem = daoService.create(mediaItem);
            if (mediaItem.getStatus() == null || !mediaItem.getStatus().equals(
                    MediaItemStatus.APPROVED)) {
                searchEngine.addToIndexQueue(QueueEntryType.MEDIA_ITEM,
                        mediaItem.getId(), QueueEntryOperation.REMOVE);
            } else {
                searchEngine.addToIndexQueue(QueueEntryType.MEDIA_ITEM,
                        mediaItem.getId(), QueueEntryOperation.UPDATE);
            }
        }
        return mediaItem;
    }

    /**
     * Updates an existing {@link MediaItem} in the database. Upon updating the
     * {@link MediaItem} will be updated and possibly deleted from the search
     * engine.
     *
     * @param mediaItem {@link MediaItem} to update
     * @return Updated {@link MediaItem}
     */
    @Override
    public MediaItem update(MediaItem mediaItem) {
        mediaItem.setUpdated(Calendar.getInstance());
        mediaItem = daoService.update(mediaItem);

        if (mediaItem.getStatus() == null || !mediaItem.getStatus().equals(
                MediaItemStatus.APPROVED)) {
            searchEngine.addToIndexQueue(QueueEntryType.MEDIA_ITEM, mediaItem.
                    getId(), QueueEntryOperation.REMOVE);
        } else {
            searchEngine.addToIndexQueue(QueueEntryType.MEDIA_ITEM, mediaItem.
                    getId(), QueueEntryOperation.UPDATE);
        }

        // TODO: Set-up as workflow

        if (mediaItem.getStatus() != null) {
            UserAccount user = null;
            try {
                user = userFacade.findById(ctx.getCallerPrincipal().getName());
            } catch (DataNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            Notification notification;
            switch (mediaItem.getStatus()) {
                case APPROVED:
                    notification = new Notification("<b>" + mediaItem.getTitle()
                            + "</b> was approved", "MediaItemDetails.xhtml?id="
                            + mediaItem.getId(), mediaItem.getOwner(), user);
                    notificationService.create(notification);
                    if (!mediaItem.getOwner().equals(user)) {
                        notification = new Notification("Approved <b>"
                                + mediaItem.getTitle() + "</b>", "MediaItemDetails.xhtml?id="
                                + mediaItem.getId(), user, user);
                        notificationService.create(notification);
                    }
                    break;
                case REJECTED:
                    notification = new Notification("<b>" + mediaItem.getTitle()
                            + "</b> was rejected", "MediaItemDetails.xhtml?id="
                            + mediaItem.getId(), mediaItem.getOwner(), user);
                    notificationService.create(notification);
                    notification = new Notification("Rejected <b>" + mediaItem.
                            getTitle() + "</b>", "MediaItemDetails.xhtml?id="
                            + mediaItem.getId(), user, user);
                    notificationService.create(notification);
                    break;
                case SUBMITTED:
                    for (UserAccount editor : mediaItem.getCatalogue().
                            getEditorRole().getUserAccounts()) {
                        notification = new Notification("<b>" + mediaItem.
                                getTitle() + "</b> was submitted for approval", "MediaItemDetails.xhtml?id="
                                + mediaItem.getId(), editor,
                                mediaItem.getOwner());
                        notificationService.create(notification);
                    }
                    notification = new Notification("Submitted <b>" + mediaItem.
                            getTitle() + "</b> for approval", "MediaItemDetails.xhtml?id="
                            + mediaItem.getId(), user, user);
                    notificationService.create(notification);
                    break;
                case SELF_UPLOAD:
                    for (UserAccount editor : mediaItem.getCatalogue().
                            getEditorRole().getUserAccounts()) {
                        notification = new Notification("<b>" + mediaItem.
                                getTitle()
                                + "</b> was self-uploaded for approval", "MediaItemDetails.xhtml?id="
                                + mediaItem.getId(), editor,
                                mediaItem.getOwner());
                        notificationService.create(notification);
                    }
                    notification = new Notification("Submitted <b>" + mediaItem.
                            getTitle() + "</b> for approval", "MediaItemDetails.xhtml?id="
                            + mediaItem.getId(), user, user);
                    notificationService.create(notification);
                    break;
            }
        }


        return mediaItem;
    }

    /**
     * Creates a {@link MediaItem} based on a {@link NewswireItem}.
     * <p/>
     * @param newswireItem {@link NewswireItem} to base the {@link MediaItem}
     * @param catalogue    {@link Catalogue} to add the {@link MediaItem}
     * @return {@link MediaItem} created
     */
    @Override
    public MediaItem create(NewswireItem newswireItem, Catalogue catalogue) {
        UserAccount user = null;
        try {
            user = userFacade.findById(ctx.getCallerPrincipal().getName());
        } catch (DataNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        MediaItem item = new MediaItem();
        item.setByLine(newswireItem.getAuthor());
        Calendar now = Calendar.getInstance();
        item.setCreated(now);
        item.setUpdated(now);

        StringBuilder description = new StringBuilder();
        if (org.apache.commons.lang.StringUtils.isNotBlank(newswireItem.
                getSummary())) {
            description.append(newswireItem.getSummary());
        }

        if (org.apache.commons.lang.StringUtils.isNotBlank(newswireItem.
                getContent())) {
            description.append(StringUtils.stripHtml(newswireItem.getContent()));
        }

        item.setDescription(description.toString());
        item.setTitle(newswireItem.getTitle());
        item.setOwner(user);
        item.setCatalogue(catalogue);
        item.setStatus(MediaItemStatus.APPROVED);
        item = create(item);

        // TODO: Import tags

        for (NewswireItemAttachment attachment : newswireItem.getAttachments()) {
            if (attachment.isStoredInCatalogue() && attachment.isRenditionSet()) {
                try {
                    MediaItemRendition mir = new MediaItemRendition();
                    mir.setContentType(attachment.getContentType());
                    File mediaFile = new File(attachment.
                            getCatalogueFileLocation());
                    mir.setPath(archive(mediaFile, catalogue, attachment.
                            getFilename()));
                    mir.setFilename(attachment.getFilename());
                    mir.setRendition(attachment.getRendition());
                    mir.setMediaItem(item);
                    mir = update(mediaFile, attachment.getFilename(),
                            attachment.getContentType(), mir, true);
                    item.getRenditions().add(mir);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }

        return item;
    }

    /**
     * Deletes an existing {@link MediaItem} from the database. Upon deletion
     * the {@link MediaItem} will also be removed from the search engine.
     *
     * @param id Unique identifier of the {@link MediaItem}
     */
    @Override
    public void deleteMediaItemById(Long id) {
        // Remove from search engine
        searchEngine.addToIndexQueue(QueueEntryType.MEDIA_ITEM, id,
                QueueEntryOperation.REMOVE);

        // Remove renditions
        try {
            MediaItem mi = daoService.findById(MediaItem.class, id);

            String path = null;
            for (MediaItemRendition mir : mi.getRenditions()) {
                path = mir.getPath();
                deleteMediaItemRenditionById(mir.getId());
            }

            if (path != null) {
                File p = new File(mi.getCatalogue().getLocation(), path);
                if (p.exists() && p.isDirectory()) {
                    p.delete();
                } else {
                    LOG.log(Level.WARNING,
                            "Could not remove MediaItem #{0} path ({1})",
                            new Object[]{id, p.getAbsoluteFile().toString()});
                }
            }

            daoService.delete(MediaItem.class, id);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "MediaItem #{0} does not exist", id);
        }
    }

    @Override
    public MediaItem findMediaItemById(Long id) throws DataNotFoundException {
        return daoService.findById(MediaItem.class, id);
    }

    @Override
    public List<MediaItem> findMediaItemsByStatus(MediaItemStatus status) {
        Map<String, Object> params = QueryBuilder.with("status", status).
                parameters();
        return daoService.findWithNamedQuery(MediaItem.FIND_BY_STATUS, params);
    }

    @Override
    public List<MediaItem> findMediaItemsByOwner(UserAccount owner) {
        Map<String, Object> params = QueryBuilder.with("owner", owner).
                parameters();
        return daoService.findWithNamedQuery(MediaItem.FIND_BY_OWNER, params);
    }

    @Override
    public List<MediaItem> findCurrentMediaItems(UserAccount user,
            Long catalogueId) {
        try {
            Catalogue catalogue = daoService.findById(Catalogue.class,
                    catalogueId);
            Map<String, Object> params = QueryBuilder.with("user", user).and(
                    "mediaRepository", catalogue).parameters();

            List<MediaItem> items = new ArrayList<MediaItem>();

            items.addAll(daoService.findWithNamedQuery(
                    MediaItem.FIND_CURRENT_AS_OWNER, params));
            items.addAll(daoService.findWithNamedQuery(
                    MediaItem.FIND_CURRENT_AS_EDITOR, params));

            Set set = new HashSet(items);
            return new ArrayList(set);
        } catch (DataNotFoundException ex) {
            return Collections.EMPTY_LIST;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<MediaItem> findCurrentMediaItems(UserAccount user,
            MediaItemStatus mediaItemStatus, Long catalogueId) {
        final int MAX_RETURN = 200;
        try {
            Catalogue catalogue = daoService.findById(Catalogue.class,
                    catalogueId);
            Map<String, Object> params = QueryBuilder.with("user", user).
                    and("status", mediaItemStatus).
                    and("catalogue", catalogue).parameters();
            return daoService.findWithNamedQuery(
                    MediaItem.FIND_BY_OWNER_AND_STATUS, params, MAX_RETURN);
        } catch (DataNotFoundException ex) {
            return Collections.EMPTY_LIST;
        }
    }

    /** {@inheritDoc } */
    @Override
    public boolean isMediaItemUsed(Long id) {
        try {
            MediaItem mediaItem = daoService.findById(MediaItem.class, id);
            Map<String, Object> params = QueryBuilder.with("mediaItem",
                    mediaItem).parameters();
            List results =
                    daoService.findWithNamedQuery(
                    NewsItemMediaAttachment.FIND_BY_MEDIA_ITEM, params);

            if (results.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (DataNotFoundException ex) {
            return false;
        }
    }

    /** {@inheritDoc } */
    @Override
    public List<MediaItemUsage> getMediaItemUsage(Long id) throws
            DataNotFoundException {
        MediaItem mediaItem = daoService.findById(MediaItem.class, id);
        Map<String, Object> params = QueryBuilder.with("mediaItem", mediaItem).
                parameters();
        List<NewsItemMediaAttachment> results =
                daoService.findWithNamedQuery(
                NewsItemMediaAttachment.FIND_BY_MEDIA_ITEM, params);
        List<MediaItemUsage> output = new ArrayList<MediaItemUsage>();

        for (NewsItemMediaAttachment attachment : results) {

            if (!attachment.getNewsItem().getPlacements().isEmpty()) {
                for (NewsItemPlacement placement : attachment.getNewsItem().
                        getPlacements()) {
                    MediaItemUsage usage = new MediaItemUsage();
                    usage.setNewsItemId(attachment.getNewsItem().getId());
                    usage.setTitle(attachment.getNewsItem().getTitle());
                    usage.setCaption(attachment.getCaption());
                    usage.setDate(placement.getEdition().getPublicationDate().
                            getTime());
                    usage.setOutlet(
                            placement.getEdition().getOutlet().getTitle());
                    usage.setSection(placement.getSection().getFullName());
                    usage.setStart(placement.getStart());
                    usage.setPosition(placement.getPosition());
                    usage.setPublished(!placement.getEdition().isOpen());
                    output.add(usage);
                }
            } else {
                MediaItemUsage usage = new MediaItemUsage();
                usage.setNewsItemId(attachment.getNewsItem().getId());
                usage.setTitle(attachment.getNewsItem().getTitle());
                usage.setCaption(attachment.getCaption());
                usage.setDate(attachment.getNewsItem().getUpdated().getTime());
                usage.setOutlet("");
                usage.setSection("");
                usage.setStart(0);
                usage.setPosition(0);
                usage.setPublished(false);
                output.add(usage);
            }
        }

        return output;
    }

    /**
     * Scans all the active catalogue drop points for files and processes new files.
     */
    @Override
    public void scanDropPoints() {
        //TODO: Drop point scanning not yet complete
//        List<MediaRepository> catalogues = daoService.findAll(MediaRepository.class);
//
//        for (MediaRepository catalogue : catalogues) {
//            if (catalogue.isEnabled() && !catalogue.isReadOnly()) {
//                logger.log(Level.INFO, "Scanning ''{0}'' drop point [{1}]", new Object[]{catalogue.getName(), catalogue.getWatchLocation()});
//                File dropPoint = new File(catalogue.getWatchLocation());
//                if (!dropPoint.exists()) {
//                    logger.log(Level.WARNING, "{0} does not exist", new Object[]{catalogue.getWatchLocation()});
//                    continue;
//                }
//
//                if (!dropPoint.isDirectory()) {
//                    logger.log(Level.WARNING, "{0} is not a directory", new Object[]{catalogue.getWatchLocation()});
//                    continue;
//                }
//
//                for (File found : dropPoint.listFiles()) {
//                    logger.log(Level.INFO, "Found ''{0}''", found.getName());
//                    try {
//                        Map<String, String> xmp = indexXmp(FileUtils.getBytes(found));
//                        for (String key : xmp.keySet()) {
//                            logger.log(Level.INFO, "{0} = {1}", new Object[]{key, xmp.get(key)});
//                        }
//
//                    } catch (IOException ex) {
//                        Logger.getLogger(MediaDatabaseFacadeBean.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (CannotIndexException ex) {
//                        Logger.getLogger(MediaDatabaseFacadeBean.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//        }
    }

    /**
     * Archives a {@link MediaItemRendition} in a {@link Catalogue}.
     * <p/>
     * @param file      File to archive
     * @param catalogue Catalogue used for archiving the file
     * @param rendition Rendition to store
     * @return Path where the rendition was stored on the {@link Catalogue}
     * @throws IOException * If the file could not be archived
     */
    public String archive(File file, Catalogue catalogue,
            MediaItemRendition rendition) throws IOException {
        Calendar now = Calendar.getInstance();

        StringBuilder cataloguePath = new StringBuilder();
        cataloguePath.append(now.get(Calendar.YEAR)).append(File.separator).
                append(now.get(Calendar.MONTH) + 1).append(File.separator).
                append(now.get(Calendar.DAY_OF_MONTH)).append(File.separator).
                append(rendition.getMediaItem().getId());

        StringBuilder catalogueLocation = new StringBuilder(catalogue.
                getLocation());
        catalogueLocation.append(File.separator).append(cataloguePath.toString());

        // Get the repository location
        File dir = new File(catalogueLocation.toString());

        // Check if it exist
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Determine the location and name of the file being uploaded
        File mediaFile = new File(dir, rendition.getFilename());

        // Move file to the new location
        LOG.log(Level.FINE, "Archiving {0} at {1}", new Object[]{file.
                    getAbsolutePath(), mediaFile.getAbsolutePath()});
        FileUtils.copyFile(file, mediaFile);

        rendition.setPath(cataloguePath.toString());

        return cataloguePath.toString();
    }

    /**
     * Archives a {@link File} in a {@link Catalogue}.
     * <p/>
     * @param file      File to archive
     * @param catalogue Catalogue used for archiving the file
     * @param fileName  File name of the file
     * @return Path where the file was stored on the {@link Catalogue}
     * @throws IOException If the file could not be archived
     */
    @Override
    public String archive(File file, Catalogue catalogue, String fileName)
            throws IOException {
        if (!file.exists()) {
            throw new IOException("Source file does not exist");
        }
        Calendar now = Calendar.getInstance();

        StringBuilder cataloguePath = new StringBuilder();
        cataloguePath.append(now.get(Calendar.YEAR)).append(File.separator).
                append(now.get(Calendar.MONTH) + 1).append(File.separator).
                append(now.get(Calendar.DAY_OF_MONTH));

        StringBuilder catalogueLocation = new StringBuilder(catalogue.
                getLocation());
        catalogueLocation.append(File.separator).append(cataloguePath.toString());

        // Get the repository location
        File dir = new File(catalogueLocation.toString());

        // Check if it exist
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Determine the location and name of the file being uploaded
        File mediaFile = new File(dir, fileName);

        // Move file to the new location
        FileUtils.copyFile(file, mediaFile);

        return cataloguePath.toString();
    }

    @Override
    public CatalogueHookInstance createCatalogueAction(
            CatalogueHookInstance action) {
        return daoService.create(action);
    }

    @Override
    public CatalogueHookInstance updateCatalogueAction(
            CatalogueHookInstance action) {
        return daoService.update(action);
    }
}
