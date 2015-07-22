/*
 * Copyright (C) 2015 Allan Lykke Christensen
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
package dk.i2m.converge.core.logging;

import java.util.logging.Level;

/**
 * Helper class for logging in Converge.
 *
 * @author Allan Lykke Christensen
 */
public class LogHelper {

    /**
     * Maps a java.util.logging {@link Level} to a Converge {@link LogSeverity}.
     *
     * @param level {@Link Level} to map
     * @return {@link LogSeverity} mapping to the given {@code level}
     */
    public static LogSeverity toSeverity(Level level) {
        if (level == Level.CONFIG) {
            return LogSeverity.CONFIG;
        } else if (level == Level.FINE) {
            return LogSeverity.FINE;
        } else if (level == Level.FINER) {
            return LogSeverity.FINER;
        } else if (level == Level.FINEST) {
            return LogSeverity.FINEST;
        } else if (level == Level.INFO) {
            return LogSeverity.INFO;
        } else if (level == Level.SEVERE) {
            return LogSeverity.SEVERE;
        } else if (level == Level.WARNING) {
            return LogSeverity.WARNING;
        } else {
            return LogSeverity.INFO;
        }
    }

    /**
     * Maps a Converge {@link LogSeverity} to a java.util.logging {@link Level}.
     *
     * @param severity {@Link LogSeverity} to map
     * @return {@link Level} mapping to the given {@code severity}
     */
    public static Level toLevel(LogSeverity severity) {
        if (severity == LogSeverity.CONFIG) {
            return Level.CONFIG;
        } else if (severity == LogSeverity.FINE) {
            return Level.FINE;
        } else if (severity == LogSeverity.FINER) {
            return Level.FINER;
        } else if (severity == LogSeverity.FINEST) {
            return Level.FINEST;
        } else if (severity == LogSeverity.INFO) {
            return Level.INFO;
        } else if (severity == LogSeverity.SEVERE) {
            return Level.SEVERE;
        } else if (severity == LogSeverity.WARNING) {
            return Level.WARNING;
        } else {
            return Level.INFO;
        }
    }

}
