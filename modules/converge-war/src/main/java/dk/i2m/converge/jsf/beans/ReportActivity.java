/*
 * Copyright (C) 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.reporting.activity.ActivityReport;
import dk.i2m.converge.core.reporting.activity.UserActivity;
import dk.i2m.converge.core.security.UserRole;
import dk.i2m.converge.ejb.facades.ReportingFacadeLocal;
import dk.i2m.converge.utils.CalendarUtils;
import dk.i2m.jsf.JsfUtils;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * Backing bean for the {@code ReportActivity.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class ReportActivity {

    @EJB private ReportingFacadeLocal reportingFacade;

    private UserRole userRole = null;

    private boolean userRoleSubmitter = true;

    private Date startDate = null;

    private Date endDate = null;

    private DataModel report = new ListDataModel(new ArrayList());

    private UserActivity selectedUserActivity = null;

    private ActivityReport generatedReport = null;

    private ResourceBundle bundle;

    public ReportActivity() {
    }

    @PostConstruct
    public void onInit() {
        java.util.Calendar firstDay = CalendarUtils.getFirstDayOfMonth();
        java.util.Calendar lastDay = CalendarUtils.getLastDayOfMonth();
        this.startDate = firstDay.getTime();
        this.endDate = lastDay.getTime();
        this.bundle = JsfUtils.getResourceBundle(Bundle.i18n.name());
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public boolean isUserRoleSubmitter() {
        return userRoleSubmitter;
    }

    public void setUserRoleSubmitter(boolean userRoleSubmitter) {
        this.userRoleSubmitter = userRoleSubmitter;
    }

    public UserActivity getSelectedUserActivity() {
        return selectedUserActivity;
    }

    public void setSelectedUserActivity(UserActivity selectedUserActivity) {
        this.selectedUserActivity = selectedUserActivity;
    }

    public DataModel getReport() {
        return report;
    }

    /**
     * Determines if a generated report is available.
     * 
     * @return {@code true} if a report is available, otherwise {@code false}
     */
    public boolean isReportAvailable() {
        if (this.generatedReport != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Event handler for generating the on-screen report.
     * 
     * @param event 
     *          Event that invoked the handler
     */
    public void onGenerateReport(ActionEvent event) {
        this.generatedReport = reportingFacade.generateActivityReport(startDate, endDate, userRole, userRoleSubmitter);
        this.report = new ListDataModel(this.generatedReport.getUserActivity());
    }

    /**
     * Event handler for downloading the generated report as a
     * Microsoft Excel spreadsheet.
     * 
     * @param event 
     *          Event that invoked the handler
     */
    public void onDownloadXls(ActionEvent event) {

        if (isReportAvailable()) {
            byte[] file = reportingFacade.convertToExcel(generatedReport);
            String filename = getDownloadXlsFilename();

            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            try {
                ServletOutputStream out = response.getOutputStream();
                out.write(file);
                out.flush();
                out.close();
            } catch (IOException ex) {
                JsfUtils.createMessage("frmReporting", FacesMessage.SEVERITY_ERROR, Bundle.i18n.name(), "ReportActivity_REPORT_GENERATION_ERROR_X", new Object[]{ex.getMessage()});
            }

            FacesContext faces = FacesContext.getCurrentInstance();
            faces.responseComplete();
        }
    }

    /**
     * Utility method for generating the name of the file
     * that will be sent back to the user as the name of
     * the Excel report.
     * 
     * @return Filename of the XLS report
     */
    private String getDownloadXlsFilename() {
        final String FILENAME_KEY = "ReportActivity_DOWNLOAD_XLS_FILENAME";
        String msgPattern = "";
        try {
            msgPattern = this.bundle.getString(FILENAME_KEY);
        } catch (MissingResourceException ex) {
            msgPattern = FILENAME_KEY;
        }
        String msg = msgPattern;

        Object[] params = new Object[]{getStartDate(), getEndDate()};

        msg = MessageFormat.format(msgPattern, params);
        return msg;
    }
}
