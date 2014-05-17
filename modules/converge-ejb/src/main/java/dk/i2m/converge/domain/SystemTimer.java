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
package dk.i2m.converge.domain;

import java.util.Date;

/**
 * Value object represents a timer managed by the system.
 * 
 * @see Converge EJB TimerService
 * @author Allan Lykke Christensen
 */
public class SystemTimer {

    private String name;

    private Date nextTimeout;

    private Long timeRemaining;

    /**
     * Creates a new instance of {@link SystemTimer}.
     */
    public SystemTimer() {
    }

    /**
     * Creates a new instance of {@link SystemTimer}.
     * 
     * @param name
     *          Name of the timer
     * @param nextTimeout
     *          Next {@link Date} when the timer will be executed
     * @param timeRemaining
     *          Time remaining for the next timeout.
     */
    public SystemTimer(String name, Date nextTimeout, Long timeRemaining) {
        this.name = name;
        this.nextTimeout = nextTimeout;
        this.timeRemaining = timeRemaining;
    }

    /**
     * Gets the name of the timer.
     * 
     * @return Name of the timer
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the timer.
     * 
     * @param name
     *          Name of the timer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the next timeout.
     * 
     * @return {@link Date} of the next execution of the timer
     */
    public Date getNextTimeout() {
        return nextTimeout;
    }

    /**
     * Sets the next timeout.
     * 
     * @param nextTimeout
     *          {@link Date} of the next execution of the timer
     */
    public void setNextTimeout(Date nextTimeout) {
        this.nextTimeout = nextTimeout;
    }

    /**
     * Gets the time, in ms, before the next execution.
     * 
     * @return Time in ms before the next execution
     */
    public Long getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Sets the time, in ms, before the next execution.
     * 
     * @param timeRemaining
     *          Time in ms before the next execution
     */
    public void setTimeRemaining(Long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    /**
     * Determines if the timer is active.
     * 
     * @return {@code true} if the timer is active, otherwise {@code false}
     */
    public boolean isActive() {
        if (nextTimeout != null) {
            return true;
        } else {
            return false;
        }
    }
}
