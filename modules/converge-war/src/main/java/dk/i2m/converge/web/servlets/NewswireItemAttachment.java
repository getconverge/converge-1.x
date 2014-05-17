/*
 * Copyright (C) 2011 Interactive Media Management
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

import dk.i2m.commons.FilePackage;
import dk.i2m.commons.HttpUtils;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.ejb.services.NewswireServiceLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author alc
 */
public class NewswireItemAttachment extends HttpServlet {

    @EJB private NewswireServiceLocal newswireService;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            Long id = Long.valueOf(request.getParameter("aid"));
            Long newswireItemId = Long.valueOf(request.getParameter("nid"));
            
            boolean force = false;
            if (request.getParameterMap().containsKey("force")) {
                force = true;
                Logger.getLogger(NewswireItemAttachment.class.getName()).log(Level.INFO, "Force download");
            }
            
            dk.i2m.converge.core.newswire.NewswireItemAttachment attachment = newswireService.findNewswireItemAttachmentById(id);

            if (attachment.getNewswireItem().getId().equals(newswireItemId)) {
                HttpUtils.sendBinary(response, attachment.getFilename(), attachment.getData(), attachment.getContentType());
            } else {
                Logger.getLogger(NewswireItemAttachment.class.getName()).log(Level.SEVERE, "Attachment and NewswireItem mismatch");
            }

        } catch (DataNotFoundException ex) {
            Logger.getLogger(NewswireItemAttachment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
