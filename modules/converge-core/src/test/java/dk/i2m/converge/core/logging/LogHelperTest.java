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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link LogHelper}.
 *
 * @author Allan Lykke Christensen
 */
public class LogHelperTest {

    @Test
    public void logHelperTest_configLogLevel_mapToConfigLogSeverity() {
        // Arrange
        Level level = Level.CONFIG;

        // Act
        LogSeverity severity = LogHelper.toSeverity(level);

        // Assert
        assertEquals(LogSeverity.CONFIG.toString(), severity.toString());
    }

    @Test
    public void logHelperTest_fineLogLevel_mapToFineLogSeverity() {
        // Arrange
        Level level = Level.FINE;

        // Act
        LogSeverity severity = LogHelper.toSeverity(level);

        // Assert
        assertEquals(LogSeverity.FINE.toString(), severity.toString());
    }

    @Test
    public void logHelperTest_finerLogLevel_mapToFinerLogSeverity() {
        // Arrange
        Level level = Level.FINER;

        // Act
        LogSeverity severity = LogHelper.toSeverity(level);

        // Assert
        assertEquals(LogSeverity.FINER.toString(), severity.toString());
    }

    @Test
    public void logHelperTest_finestLogLevel_mapToFinestLogSeverity() {
        // Arrange
        Level level = Level.FINEST;

        // Act
        LogSeverity severity = LogHelper.toSeverity(level);

        // Assert
        assertEquals(LogSeverity.FINEST.toString(), severity.toString());
    }

    @Test
    public void logHelperTest_infoLogLevel_mapToInfoLogSeverity() {
        // Arrange
        Level level = Level.INFO;

        // Act
        LogSeverity severity = LogHelper.toSeverity(level);

        // Assert
        assertEquals(LogSeverity.INFO.toString(), severity.toString());
    }

    @Test
    public void logHelperTest_severeLogLevel_mapToSevereLogSeverity() {
        // Arrange
        Level level = Level.SEVERE;

        // Act
        LogSeverity severity = LogHelper.toSeverity(level);

        // Assert
        assertEquals(LogSeverity.SEVERE.toString(), severity.toString());
    }

    @Test
    public void logHelperTest_warningLogLevel_mapToWarningLogSeverity() {
        // Arrange
        Level level = Level.WARNING;

        // Act
        LogSeverity severity = LogHelper.toSeverity(level);

        // Assert
        assertEquals(LogSeverity.WARNING.toString(), severity.toString());
    }

    @Test
    public void logHelperTest_unknownLogLevel_mapToInfoLogSeverity() {
        // Arrange
        Level level = Level.OFF;

        // Act
        LogSeverity severity = LogHelper.toSeverity(level);

        // Assert
        assertEquals(LogSeverity.INFO.toString(), severity.toString());
    }

    @Test
    public void logHelperTest_configLogSeverity_mapToConfigLogLevel() {
        // Arrange
        LogSeverity severity = LogSeverity.CONFIG;

        // Act
        Level level = LogHelper.toLevel(severity);

        // Assert
        assertEquals(Level.CONFIG.toString(), level.toString());
    }

    @Test
    public void logHelperTest_fineLogSeverity_mapToFineLogLevel() {
        // Arrange
        LogSeverity severity = LogSeverity.FINE;

        // Act
        Level level = LogHelper.toLevel(severity);

        // Assert
        assertEquals(Level.FINE.toString(), level.toString());
    }

    @Test
    public void logHelperTest_finerLogSeverity_mapToFinerLogLevel() {
        // Arrange
        LogSeverity severity = LogSeverity.FINER;

        // Act
        Level level = LogHelper.toLevel(severity);

        // Assert
        assertEquals(Level.FINER.toString(), level.toString());
    }

    @Test
    public void logHelperTest_finestLogSeverity_mapToFinestLogLevel() {
        // Arrange
        LogSeverity severity = LogSeverity.FINEST;

        // Act
        Level level = LogHelper.toLevel(severity);

        // Assert
        assertEquals(Level.FINEST.toString(), level.toString());
    }

    @Test
    public void logHelperTest_infoLogSeverity_mapToInfoLogLevel() {
        // Arrange
        LogSeverity severity = LogSeverity.INFO;

        // Act
        Level level = LogHelper.toLevel(severity);

        // Assert
        assertEquals(Level.INFO.toString(), level.toString());
    }

    @Test
    public void logHelperTest_severeLogSeverity_mapToSevereLogLevel() {
        // Arrange
        LogSeverity severity = LogSeverity.SEVERE;

        // Act
        Level level = LogHelper.toLevel(severity);

        // Assert
        assertEquals(Level.SEVERE.toString(), level.toString());
    }

    @Test
    public void logHelperTest_warningLogSeverity_mapToWarningLogLevel() {
        // Arrange
        LogSeverity severity = LogSeverity.WARNING;

        // Act
        Level level = LogHelper.toLevel(severity);

        // Assert
        assertEquals(Level.WARNING.toString(), level.toString());
    }

    @Test
    public void logHelperTest_nullLogSeverity_mapToInfoLogLevel() {
        // Arrange
        LogSeverity severity = null;

        // Act
        Level level = LogHelper.toLevel(severity);

        // Assert
        assertEquals(Level.INFO.toString(), level.toString());
    }

}
