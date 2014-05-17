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
package dk.i2m.converge.core.reporting.activity;

/**
 * Summary of a {@link UserActivity} report. {@link UserActivitySummary}
 * only contains the indicators from the {@link UserActivity} and
 * not all the data points used to calculate the indicators. If the
 * data points are not required, it is advisable to use {@link UserActivitySummary}
 * to avoid using excessive memory.
 *
 * @author Allan Lykke Christensen
 */
public class UserActivitySummary {

    private int numberOfNewsItemsSubmitted = 0;

    private int numberOfNewsItemsSubmittedWithMediaItems = 0;

    private int numberOfNewsItemsUsed = 0;

    private int numberOfNewsItemsUsedWithMedia = 0;

    private double usage = 0.0;

    private double usageWithMedia = 0.0;

    private long totalWordCount = 0L;

    public UserActivitySummary(UserActivity userActivity) {
        numberOfNewsItemsSubmitted = userActivity.getNumberOfNewsItemsSubmitted();
        numberOfNewsItemsSubmittedWithMediaItems = userActivity.getNumberOfNewsItemsSubmittedWithMediaItems();
        numberOfNewsItemsUsed = userActivity.getNumberOfNewsItemsUsed();
        numberOfNewsItemsUsedWithMedia = userActivity.getNumberOfNewsItemsUsedWithMedia();
        usage = userActivity.getUsage();
        usageWithMedia = userActivity.getUsageWithMedia();
        totalWordCount = userActivity.getTotalWordCount();
    }

    public int getNumberOfNewsItemsSubmitted() {
        return this.numberOfNewsItemsSubmitted;
    }

    public int getNumberOfNewsItemsSubmittedWithMediaItems() {
        return this.numberOfNewsItemsSubmittedWithMediaItems;
    }

    public int getNumberOfNewsItemsUsed() {
        return this.numberOfNewsItemsUsed;
    }

    public int getNumberOfNewsItemsUsedWithMedia() {
        return this.numberOfNewsItemsUsedWithMedia;
    }

    public double getUsage() {
        return usage;
    }

    public double getUsageWithMedia() {
        return usageWithMedia;
    }

    public long getTotalWordCount() {
        return totalWordCount;
    }
}
