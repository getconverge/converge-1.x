/*
 *  Copyright (C) 2010 Interactive Media Management
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
package dk.i2m.converge.web.servlets;

import dk.i2m.converge.ejb.facades.CalendarFacadeLocal;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for serving the VCal version of the calendar.
 *
 * @author Allan Lykke Christensen
 */
public class CalendarServlet extends HttpServlet {

    @EJB private CalendarFacadeLocal calendarFacade;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/calendar;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println(calendarFacade.generateVCal());
        } finally {
            out.close();
        }
    }

    /** 
     * Handles the HTTP <code>GET</code> method.
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
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
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     *
     * @return String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Serves the VCAL version of the Calendar";
    }
}
