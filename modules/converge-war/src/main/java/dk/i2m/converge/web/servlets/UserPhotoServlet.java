/*
 * Copyright (C) 2010 Interactive Media Management
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

import dk.i2m.commons.FileUtils;
import dk.i2m.commons.RequestUtils;
import dk.i2m.commons.ResponseUtils;
import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import java.io.File;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link HttpServlet} for displaying the photo of a given user. The user is
 * specified by passing the identifier of the users in a parameter called
 * <code>uid</code>. If a photo for the user does not exist, an anonymous
 * image is returned.
 *
 * @author Allan Lykke Christensen
 */
public class UserPhotoServlet extends HttpServlet {

    /** URL for photo to display when the user did not have a photo. */
    private static final String NO_PHOTO_URL = "/converge/images/no_profile_photo.gif";

    /** Request parameter containing the username of the photo to return. */
    private static final String PARAM_UID = "uid";

    @EJB private SystemFacadeLocal systemFacade;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> 
     * methods.
     *
     * @param request
     *          servlet request
     * @param response
     *          servlet response
     * @throws ServletException
     *          if a servlet-specific error occurs
     * @throws IOException
     *          if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uid = RequestUtils.getParameter(request, PARAM_UID, "");
        String workingDirectory = systemFacade.getProperty(ConfigurationKey.WORKING_DIRECTORY) + System.getProperty("file.separator") + "users" + System.getProperty("file.separator");

        File userPhoto = new File(workingDirectory, uid + ".jpg");

        if (userPhoto.canRead() && userPhoto.exists()) {
            byte[] thumb = FileUtils.getBytes(userPhoto);
            ResponseUtils.sendBinary(response, "profile.jpg", thumb, "image/jpeg");
        } else {
            response.sendRedirect(NO_PHOTO_URL);
        }

    }

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Serves photos of users";
    }
}
