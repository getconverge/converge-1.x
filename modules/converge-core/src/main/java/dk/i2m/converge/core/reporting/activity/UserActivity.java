/*
 * Copyright (C) 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.reporting.activity;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.security.UserAccount;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link UserActivity} contains the submitted and used
 * {@link NewsItem}s for a given period of time for a given {@link UserAccount}.
 *
 * @author Allan Lykke Christensen
 */
public class UserActivity {

    private UserAccount user;

    private List<NewsItem> submitted = new ArrayList<NewsItem>();

    public int getNumberOfNewsItemsSubmitted() {
        return submitted.size();
    }

    public int getNumberOfNewsItemsSubmittedWithMediaItems() {
        int withMediaItems = 0;

        for (NewsItem item : submitted) {
            if (!item.getMediaAttachments().isEmpty()) {
                withMediaItems++;
            }
        }
        return withMediaItems;
    }

    public int getNumberOfNewsItemsUsed() {
        int used = 0;
        for (NewsItem item : submitted) {
            if (!item.getPlacements().isEmpty()) {
                for (NewsItemPlacement p : item.getPlacements()) {
                    if (!p.getEdition().isOpen()) {
                        used++;
                        break;
                    }
                }
            }
        }
        return used;
    }

    public int getNumberOfNewsItemsUsedWithMedia() {
        int used = 0;
        for (NewsItem item : submitted) {
            for (NewsItemMediaAttachment attachment : item.getMediaAttachments()) {
                for (NewsItemPlacement p : item.getPlacements()) {
                    if (!p.getEdition().isOpen()) {
                        used++;
                        break;
                    }
                }
                break;
            }
        }
        return used;
    }

    public List<NewsItem> getSubmitted() {
        return submitted;
    }

    public List<NewsItem> getSubmittedWithMedia() {
        List<NewsItem> used = new ArrayList<NewsItem>();
        for (NewsItem item : submitted) {
            for (NewsItemMediaAttachment attachment : item.getMediaAttachments()) {
//                if (attachment.getMediaItem().getOwner().equals(user)) {
                used.add(item);
                break;
//                }
            }
        }
        return used;
    }

    public void setSubmitted(List<NewsItem> submitted) {
        this.submitted = submitted;
    }

    public List<NewsItem> getUsed() {
        List<NewsItem> used = new ArrayList<NewsItem>();
        for (NewsItem item : submitted) {
            if (!item.getPlacements().isEmpty()) {
                used.add(item);
            }
        }
        return used;
    }

    public List<NewsItem> getUsedWithMedia() {
        List<NewsItem> used = new ArrayList<NewsItem>();
        for (NewsItem item : submitted) {
            for (NewsItemMediaAttachment attachment : item.getMediaAttachments()) {
                //              if (attachment.getMediaItem().getOwner().equals(user)) {
                if (!item.getPlacements().isEmpty()) {

                    for (NewsItemPlacement p : item.getPlacements()) {
                        if (!p.getEdition().isOpen()) {
                            used.add(item);
                            break;
                        }
                    }

                }
                break;
//                }
            }
        }
        return used;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public double getUsage() {
        if (getNumberOfNewsItemsSubmitted() == 0 || getNumberOfNewsItemsUsed() == 0) {
            return 0;
        } else {
            return (double) getNumberOfNewsItemsUsed() / (double) getNumberOfNewsItemsSubmitted();
        }
    }

    public double getUsageWithMedia() {
        if (getNumberOfNewsItemsSubmittedWithMediaItems() == 0 || getNumberOfNewsItemsUsedWithMedia() == 0) {
            return 0;
        } else {
            return (double) getNumberOfNewsItemsUsedWithMedia() / (double) getNumberOfNewsItemsSubmittedWithMediaItems();
        }
    }

    public long getTotalWordCount() {
        long wordCount = 0L;
        for (NewsItem item : getSubmitted()) {
            wordCount += item.getWordCount();
        }
        return wordCount;
    }
}
