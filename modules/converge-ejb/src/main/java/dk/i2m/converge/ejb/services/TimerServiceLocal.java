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
package dk.i2m.converge.ejb.services;

import dk.i2m.converge.domain.SystemTimer;
import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for the timer service.
 *
 * @author Allan Lykke Christensen
 */
@Local
public interface TimerServiceLocal {

    /**
     * Starts all system timers.
     */
    void startTimers();

    /**
     * Stops all system timers.
     */
    void stopTimers();

    /**
     * Starts a given timer. If the timer is already started, it 
     * will be ignored.
     * 
     * @param timer
     *          Timer to start
     */
    void startTimer(PeriodicTimer timer);

    /**
     * Stops a given timer. If the timer is already stopped, it
     * will be ignored.
     * 
     * @param timer 
     *          Timer to stop
     */
    void stopTimer(PeriodicTimer timer);

    /**
     * Gets a {@link SystemTimer}.
     * 
     * @param timer
     *          Identifier of the {@link SystemTimer}
     * @return {@link SystemTimer} representing 
     *         the {@link PeriodicTimer}
     * @throw TimerException
     *          If a {@link SystemTimer} could not be
     *          found for the {@link PeriodicTimer}
     */
    SystemTimer getTimer(PeriodicTimer timer) throws TimerException;

    /**
     * Gets a {@link List} of active timers.
     *
     * @return {@link List} of active timers
     */
    List<SystemTimer> getActiveTimers();

    /**
     * Gets a {@link List} of all timers.
     *
     * @return {@link List} of all timers
     */
    List<SystemTimer> getAllTimers();
}
