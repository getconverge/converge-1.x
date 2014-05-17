/*
 * Copyright (C) 2012 Interactive Media Management
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
package dk.i2m.converge.web.servlets;

import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.catalogue.*;
import dk.i2m.converge.core.security.UserAccount;
import dk.i2m.converge.core.utils.HttpUtils;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.ejb.facades.UserFacadeLocal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * Servlet accepting file uploads for {@link Catalogue}s.
 *
 * @author Allan Lykke Christensen
 */
public class FileUploadServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(FileUploadServlet.class.
            getName());

    @EJB private CatalogueFacadeLocal catalogueFacade;

    @EJB private SystemFacadeLocal systemFacade;

    @EJB private UserFacadeLocal userFacade;

    /**
     * Operation determine how the {@link FileUploadServlet} should react to the
     * incoming {@link HttpServletRequest}.
     */
    private enum Operation {

        /** Operation for creating a new {@link MediaItem}. */
        NEW_MEDIA_ITEM,
        /** Operation for creating a new {@link MediaItemRendition}. */
        NEW_MEDIA_ITEM_RENDITION,
        /** Operation for updating an existing {@link MediaItemRendition}. */
        UPDATE_MEDIA_ITEM_RENDITION,
        /** Unknown/unsupported operation requested. */
        UNKNOWN
    }

    /** Authenticated user requesting an operation from the servlet. */
    private UserAccount user = null;

    /** Value returned after the SUCCESS message. */
    private String returnValue = "";

    /**
     * Processes the upload request by reading each chunk at a time and
     * combining it to a single file.
     * <p/>
     * @param request  Servlet request
     * @param response Servlet response
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        boolean access = securityCheck(request);

        if (!access) {
            return;
        }

        //Initialization for chunk management.
        boolean bLastChunk = false;
        int numChunk = 0;

        response.setContentType("text/plain");

        try {
            Enumeration paraNames = request.getParameterNames();

            String pkey;
            String pvalue;
            if (paraNames != null) {
                while (paraNames.hasMoreElements()) {
                    pkey = (String) paraNames.nextElement();
                    pvalue = request.getParameter(pkey);
                    if (pkey.equals("jufinal")) {
                        bLastChunk = pvalue.equals("1");
                    } else if (pkey.equals("jupart")) {
                        numChunk = Integer.parseInt(pvalue);
                    }
                }
            }

            Operation operation = determineOperation(request);

            if (operation == Operation.UNKNOWN) {
                LOG.warning("Unknown operation requested");
                return;
            }


            String mediaItemId = null;
            String renditionId = null;
            String mediaItemRenditionId = null;

            int ourMaxMemorySize = 10000000;
            //int ourMaxRequestSize = 4000000000;

            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constraints
            factory.setSizeThreshold(ourMaxMemorySize);
            factory.setRepository(new File(getTemporaryDirectory()));

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Set overall request size constraint
            //upload.setSizeMax(ourMaxRequestSize);

            // Parse the request
            if (request.getContentType() != null && request.getContentType().
                    startsWith("multipart/form-data")) {
                List<FileItem> items = upload.parseRequest(request);
                // Process the uploaded items

                File fout;
                for (FileItem fileItem : items) {
                    if (!fileItem.isFormField()) {

                        //If we are in chunk mode, we add ".partN" at the end of the file, where N is the chunk number.
                        String uploadedFilename = fileItem.getName() + (numChunk
                                > 0 ? ".part" + numChunk : "");
                        fout = new File(getTemporaryDirectory() + (new File(
                                uploadedFilename)).getName());
                        LOG.log(Level.FINE, "File out: {0}", fout.toString());
                        fileItem.write(fout);

                        // Execute requested operation
                        switch (operation) {
                            case NEW_MEDIA_ITEM:
                                executeNewMediaItem(request, fileItem);
                                break;
                            case UPDATE_MEDIA_ITEM_RENDITION:
                                executeUpdateRendition(request, fileItem);
                                break;
                            case NEW_MEDIA_ITEM_RENDITION:
                                executeNewRendition(request, fileItem);
                                break;
                            default:
                                LOG.log(Level.WARNING, "Unknown operation");
                                break;

                        }

                        // Chunk management: if it was the last chunk, let's 
                        // recover the complete file by concatenating all chunk
                        // parts.
                        if (bLastChunk) {
                            LOG.log(Level.FINEST,
                                    "Last chunk received: Rebuilding complete file ({0})",
                                    fileItem.getName());
                            //First: construct the final filename.
                            FileInputStream fis;
                            FileOutputStream fos =
                                    new FileOutputStream(getTemporaryDirectory()
                                    + fileItem.getName());
                            int nbBytes;
                            byte[] byteBuff = new byte[1024];
                            String filename;
                            for (int i = 1; i <= numChunk; i += 1) {
                                filename = fileItem.getName() + ".part" + i;
                                LOG.log(Level.FINEST, "Concatenating {0}",
                                        filename);

                                fis = new FileInputStream(getTemporaryDirectory()
                                        + filename);

                                while ((nbBytes = fis.read(byteBuff)) >= 0) {
                                    LOG.log(Level.FINEST, "Nb bytes read: {0}",
                                            nbBytes);
                                    fos.write(byteBuff, 0, nbBytes);
                                }
                                fis.close();
                            }
                            fos.close();

                        }
                        // End of chunk management

                        fileItem.delete();
                    }
                }
            }

            response.getWriter().println("SUCCESS");
            response.getWriter().print(this.returnValue);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "File upload failed. " + e.getMessage(), e);
        }
    }

    /**
     * Checks if the user is authenticated and exist in the database.
     * <p/>
     * @param request {@link HttpServletRequest} received from the user
     * @return {@code true} if the user is authenticated and existing, otherwise
     *         {@code false}
     */
    private boolean securityCheck(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal == null) {
            this.user = null;
            LOG.log(Level.WARNING, "Unauthorised access to FileUploadServlet "
                    + "attempted from {0}", request.getRemoteAddr());
            return false;
        }

        String uid = userPrincipal.getName();

        try {
            this.user = userFacade.findById(uid);
        } catch (DataNotFoundException ex) {
            this.user = null;
            LOG.log(Level.SEVERE, ex.getMessage());
            return false;
        }
        return true;
    }

    private Operation determineOperation(HttpServletRequest request) {
        if (request.getParameter("uploadType") != null && request.getParameter(
                "uploadType").equalsIgnoreCase(
                "replaceRendition")) {
            return Operation.UPDATE_MEDIA_ITEM_RENDITION;
        } else if (request.getParameter("uploadType") != null && request.
                getParameter("uploadType").equalsIgnoreCase("newRendition")) {
            return Operation.NEW_MEDIA_ITEM_RENDITION;
        } else if (request.getParameter("uploadType") != null && request.
                getParameter("uploadType").equalsIgnoreCase("newMediaItem")) {
            return Operation.NEW_MEDIA_ITEM;
        } else {
            return Operation.UNKNOWN;
        }
    }

    private void executeNewMediaItem(HttpServletRequest request,
            FileItem fileItem) {
        LOG.log(Level.FINE, "Creating new media item");

        String catalogueId = request.getParameter("catalogueId");

        if (catalogueId == null) {
            LOG.log(Level.WARNING, "Missing catalogueId parameter");
            return;
        }

        Catalogue catalogue = null;
        try {
            catalogue =
                    catalogueFacade.findCatalogueById(Long.valueOf(catalogueId));
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Invalid catalogue: {0}",
                    new Object[]{catalogueId});
            return;
        } catch (NumberFormatException ex) {
            LOG.log(Level.WARNING, "Invalid catalogue: {0}",
                    new Object[]{catalogueId});
            return;
        }

        MediaItem mediaItem = new MediaItem();
        mediaItem.setTitle(FilenameUtils.getBaseName(fileItem.getName()));
        mediaItem.setStatus(MediaItemStatus.SELF_UPLOAD);
        mediaItem.setCatalogue(catalogue);
        mediaItem.setOwner(user);
        mediaItem.setByLine("");
        mediaItem = catalogueFacade.create(mediaItem);
        this.returnValue = "" + mediaItem.getId();

        MediaItemRendition mir = new MediaItemRendition();
        mir.setMediaItem(mediaItem);
        mir.setRendition(catalogue.getOriginalRendition());
        mediaItem.getRenditions().add(mir);

        String filename = HttpUtils.getFilename(fileItem.getName());
        File uploadedFile = new File(getTemporaryDirectory(), filename);
        try {
            mir = catalogueFacade.create(uploadedFile,
                    mediaItem, mir.getRendition(), filename,
                    fileItem.getContentType(), true);

            LOG.log(Level.FINE,
                    "New media item and rendition created: {0} / {1}",
                    new Object[]{mediaItem.getId(), mir.getId()});
        } catch (IOException ioex) {
            LOG.log(Level.WARNING, "Could not create file. "
                    + ioex.getMessage(), ioex);
        }

        uploadedFile.delete();
    }

    private void executeUpdateRendition(HttpServletRequest request,
            FileItem fileItem) {

        // Retrieve parameters from request
        String paramMediaItemId = request.getParameter("mediaItemId");
        String paramRenditionId = request.getParameter("renditionId");

        Long mediaItemId;
        Long renditionId;

        try {
            mediaItemId = Long.valueOf(paramMediaItemId);
            renditionId = Long.valueOf(paramRenditionId);
        } catch (NumberFormatException ex) {
            LOG.log(Level.WARNING, "Invalid values supplied. {0}",
                    ex.getMessage());
            return;
        }

        // Retrieve the MediaItemRendition
        MediaItem mediaItem;
        Rendition rendition;
        MediaItemRendition mediaItemRendition;
        try {
            mediaItem = catalogueFacade.findMediaItemById(mediaItemId);
            rendition = catalogueFacade.findRenditionById(renditionId);
            mediaItemRendition = mediaItem.findRendition(rendition);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown data requested. {0}",
                    ex.getMessage());
            return;
        } catch (RenditionNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown rendition requested. {0}", ex.
                    getMessage());
            return;
        }

        // Update MediaItemRendition with the uploaded file
        File uploadedFile =
                new File(getTemporaryDirectory(), fileItem.getName());
        try {
            mediaItemRendition = catalogueFacade.update(uploadedFile, fileItem.
                    getName(), fileItem.getContentType(), mediaItemRendition, true);
            this.returnValue = "" + mediaItemRendition.getId();
            LOG.log(Level.FINE, "Media item #{0} was updated",
                    new Object[]{mediaItemRendition.getId()});
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not update catalogue. {0}", ex.
                    getMessage());
        }

        uploadedFile.delete();
    }

    private void executeNewRendition(HttpServletRequest request,
            FileItem fileItem) {

        // Retrieve parameters from request
        String paramMediaItemId = request.getParameter("mediaItemId");
        String paramRenditionId = request.getParameter("renditionId");

        Long mediaItemId;
        Long renditionId;

        try {
            mediaItemId = Long.valueOf(paramMediaItemId);
            renditionId = Long.valueOf(paramRenditionId);
        } catch (NumberFormatException ex) {
            LOG.log(Level.WARNING, "Invalid values supplied. {0}",
                    ex.getMessage());
            return;
        }

        // Retrieve objects from database
        MediaItem mediaItem;
        Rendition rendition;
        try {
            mediaItem = catalogueFacade.findMediaItemById(mediaItemId);
            rendition = catalogueFacade.findRenditionById(renditionId);
        } catch (DataNotFoundException ex) {
            LOG.log(Level.WARNING, "Unknown data requested. {0}", ex.getMessage());
            return;
        }

        File uploadedFile = new File(getTemporaryDirectory(), fileItem.getName());
        MediaItemRendition mediaItemRendition;
        try {
            mediaItemRendition = catalogueFacade.create(uploadedFile, mediaItem, rendition, fileItem.getName(), fileItem.getContentType(), true);
            LOG.log(Level.FINE, "New media item rendition created: {0}", mediaItemRendition.getId());
            this.returnValue = "" + mediaItemRendition.getId();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not create media item rendition. {0}", ex.getMessage());
        }
        
        uploadedFile.delete();
    }

    /**
     * Gets the temporary directory for storing incoming files.
     * <p/>
     * @return Temporary directory for storing incoming files
     */
    private String getTemporaryDirectory() {
        String home = systemFacade.getProperty(
                ConfigurationKey.WORKING_DIRECTORY);
        String tempDirectory = home + "/tmp/";
        return tempDirectory;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     * <p/>
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     * <p/>
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * <p/>
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
