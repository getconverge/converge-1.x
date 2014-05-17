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
package dk.i2m.converge.jsf.beans.administrator;

import dk.i2m.converge.core.BackgroundTask;
import dk.i2m.converge.domain.SystemTimer;
import dk.i2m.converge.ejb.facades.SystemFacadeLocal;
import dk.i2m.converge.ejb.services.PeriodicTimer;
import dk.i2m.converge.ejb.services.TimerServiceLocal;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * Backing bean for {@code /administrator/Timers.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class Timers {

    @EJB private TimerServiceLocal timerService;

    private DataModel systemTimers = null;

    /**
     * Event handler for starting the newswire timers.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onStartTimers(ActionEvent event) {
        timerService.startTimers();
        this.systemTimers = null;
    }

    /**
     * Event handler for stopping the newswire timers.
     *
     * @param event
     *          Event that invoked the handler
     */
    public void onStopTimers(ActionEvent event) {
        timerService.stopTimers();
        this.systemTimers = null;
    }

    public void setStartTimer(SystemTimer timer) {
        if (timer != null) {
            timerService.startTimer(PeriodicTimer.valueOf(timer.getName()));
            this.systemTimers = null;
        }
    }

    public void setStopTimer(SystemTimer timer) {
        if (timer != null) {
            timerService.stopTimer(PeriodicTimer.valueOf(timer.getName()));
            this.systemTimers = null;
        }
    }

    /**
     * Gets the system timers.
     * 
     * @return {@link DataModel} of system timers.
     */
    public DataModel getSystemTimers() {
        if (this.systemTimers == null) {
            this.systemTimers = new ListDataModel(timerService.getAllTimers());
        }
        return this.systemTimers;
    }

    public void onRefreshTimers(ActionEvent event) {
        this.systemTimers = null;
    }
}
