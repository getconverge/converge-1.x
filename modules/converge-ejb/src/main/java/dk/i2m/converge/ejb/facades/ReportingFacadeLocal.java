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
package dk.i2m.converge.ejb.facades;

import dk.i2m.converge.core.reporting.activity.ActivityReport;
import javax.ejb.Local;

/**
 * Local interface for the reporting facade.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface ReportingFacadeLocal {

    dk.i2m.converge.core.reporting.activity.ActivityReport generateActivityReport(java.util.Date start, java.util.Date end, dk.i2m.converge.core.security.UserRole userRole, boolean submitter);
    
    dk.i2m.converge.core.reporting.activity.UserActivity generateUserActivityReport(java.util.Date start, java.util.Date end, dk.i2m.converge.core.security.UserAccount user, boolean submitter);

    dk.i2m.converge.core.reporting.activity.UserActivitySummary generateUserActivitySummary(java.util.Date start, java.util.Date end, dk.i2m.converge.core.security.UserAccount user);

    byte[] convertToExcel(ActivityReport report);
}
