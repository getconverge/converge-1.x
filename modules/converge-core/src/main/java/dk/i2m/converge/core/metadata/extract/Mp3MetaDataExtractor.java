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
package dk.i2m.converge.core.metadata.extract;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

/**
 * Extracts meta data from an MP3 file.
 *
 * @author Allan Lykke Christensen
 */
public class Mp3MetaDataExtractor implements MetaDataExtractor {

    private final Map<String, String> properties = new HashMap<String, String>();
    /**
     * Meta data property containing the title of an MP3 track.
     */
    public static final String META_TITLE = "title";
    /**
     * Meta data property containing the headline of an MP3 track.
     */
    public static final String META_HEADLINE = "headline";
    /**
     * Meta data property containing the description of an MP3 track.
     */
    public static final String META_DESCRIPTION = "description";
    /**
     * Meta data property containing the ID3 version of the MP3 track.
     */
    public static final String META_ID3_VERSION = "id3version";

    @Override
    public Map<String, String> extract(File file) throws CannotExtractMetaDataException {
        this.properties.clear();

        MediaFile mediaFile = new MP3File(file);

        try {
            ID3Tag[] tags = mediaFile.getTags();

            for (ID3Tag tag : tags) {
                processTag(tag);
            }

        } catch (ID3Exception ex) {
            throw new CannotExtractMetaDataException(ex);
        }

        return properties;
    }

    private void processTag(ID3Tag tag) {
        if (tag instanceof ID3V1_0Tag) {
            processId3v1(tag);
        } else if (tag instanceof ID3V2_3_0Tag) {
            processId3v2_3_0(tag);
        }
    }

    private void processId3v1(ID3Tag tag) {
        if (!this.properties.containsKey(META_ID3_VERSION)) {
            this.properties.put(META_ID3_VERSION, "V1_0");
        }

        ID3V1_0Tag tagV1 = (ID3V1_0Tag) tag;
        if (tagV1.getTitle() != null) {
            this.properties.put(META_HEADLINE, tagV1.getTitle());
            this.properties.put(META_TITLE, tagV1.getTitle());
        }

        if (tagV1.getComment() != null) {
            this.properties.put(META_DESCRIPTION, tagV1.getComment());
        }
    }

    private void processId3v2_3_0(ID3Tag tag) {
        if (!this.properties.containsKey(META_ID3_VERSION)) {
            this.properties.put(META_ID3_VERSION, "V2_3_0");
        }

        ID3V2_3_0Tag tagV2 = (ID3V2_3_0Tag) tag;
        if (tagV2.getTitle() != null) {
            this.properties.put(META_TITLE, tagV2.getTitle());
            this.properties.put(META_HEADLINE, tagV2.getTitle());
        }

        if (tagV2.getTIT2TextInformationFrame() != null) {
            this.properties.put(META_HEADLINE, tagV2.
                    getTIT2TextInformationFrame().getTitle());
        }

        if (tagV2.getComment() != null) {
            this.properties.put(META_DESCRIPTION, tagV2.getComment());
        }
    }
}
