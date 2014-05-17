/*
 * Copyright (C) 2010 - 2011 Interactive Media Management
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
package dk.i2m.converge.core.content.catalogue;

import dk.i2m.converge.core.utils.FileUtils;
import dk.i2m.converge.core.utils.ImageUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builder for generating {@link MediaItem}s.
 *
 * @author Allan Lykke Christensen
 */
public class MediaItemThumbnailGenerator {

    private static final Logger logger = Logger.getLogger(MediaItemThumbnailGenerator.class.getName());

    private Map<String, ThumbnailGenerator> generators = new HashMap<String, ThumbnailGenerator>();

    private static MediaItemThumbnailGenerator instance = null;

    private MediaItemThumbnailGenerator() {
        // Register thumbnail generators
        generators.put("image", new ImageThumbnailGenerator());
        generators.put("audio", new AudioThumbnailGenerator());
        generators.put("", new UnknownThumbnailGenerator());
    }

    // TODO: Optimise for new hooks
    
    public byte[] generateThumbnail(byte[] original, MediaItem mediaItem) throws UnknownMediaItemException, ThumbnailGeneratorException {
        if (mediaItem == null) {
            throw new ThumbnailGeneratorException("MediaItem is null");
        }

        String contentType = null; // = mediaItem.getContentType();

        if (contentType == null) {
            throw new UnknownMediaItemException("Content type not set on media item");
        }

        String[] hierachy = contentType.split("/");

        if (generators.containsKey(contentType)) {
            // Check if a generator is specified for the particular content type
            ThumbnailGenerator generator = generators.get(contentType);
            return generator.generate(original, mediaItem);
        } else if (generators.containsKey(hierachy[0])) {
            // Check if a generator is specified for the content type category
            ThumbnailGenerator generator = generators.get(hierachy[0]);
            return generator.generate(original, mediaItem);
        } else {
            // Unknown thumbnail generator
            ThumbnailGenerator generator = generators.get("");
            return generator.generate(original, mediaItem);
        }
    }

    public static MediaItemThumbnailGenerator getInstance() {
        if (instance == null) {
            instance = new MediaItemThumbnailGenerator();
        }
        return instance;
    }

    interface ThumbnailGenerator {

        byte[] generate(byte[] original, MediaItem mediaItem) throws ThumbnailGeneratorException;
    }

    public class ImageThumbnailGenerator implements ThumbnailGenerator {

        private static final int HEIGHT = 150;

        private static final int WIDTH = 150;

        @Override
        public byte[] generate(byte[] original, MediaItem mediaItem) throws ThumbnailGeneratorException {
            try {
                byte[] thumb = ImageUtils.generateThumbnail(original, WIDTH, HEIGHT, 100);
                return thumb;
            } catch (InterruptedException ex) {
                logger.log(Level.WARNING, "Could not generate thumbnail", ex);
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Could not generate thumbnail", ex);
            }

            // Fallback
            InputStream is = null;
            byte[] output = null;
            try {
                is = getClass().getResourceAsStream("/dk/i2m/converge/core/content/unknown-media-item.png");

                try {
                    output = FileUtils.getBytes(is);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Could not generate audio thumbnail", e);
                } finally {
                    is.close();
                }

                return output;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Could not generate audio thumbnail", ex);
                throw new ThumbnailGeneratorException("Could not generate fallback thumbnail", ex);
            }
        }
    }

    public class UnknownThumbnailGenerator implements ThumbnailGenerator {

        @Override
        public byte[] generate(byte[] original, MediaItem mediaItem) throws ThumbnailGeneratorException {
            InputStream is = null;
            byte[] output = null;
            try {
                is = getClass().getResourceAsStream("/dk/i2m/converge/core/content/unknown-media-item.png");

                try {
                    output = FileUtils.getBytes(is);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Could not generate thumbnail for unknown item format", e);
                } finally {
                    is.close();
                }

                return output;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Could not generate thumbnail for unknown item format", ex);
                throw new ThumbnailGeneratorException("Could not generate fallback thumbnail", ex);
            }
        }
    }

    public class AudioThumbnailGenerator implements ThumbnailGenerator {

        @Override
        public byte[] generate(byte[] original, MediaItem mediaItem) throws ThumbnailGeneratorException {
            InputStream is = null;
            byte[] output = null;
            try {
                is = getClass().getResourceAsStream("/dk/i2m/converge/core/content/audio-media-item.png");

                try {
                    output = FileUtils.getBytes(is);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Could not generate audio thumbnail", e);
                } finally {
                    is.close();
                }

                return output;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Could not generate audio thumbnail", ex);
                throw new ThumbnailGeneratorException("Could not generate fallback thumbnail", ex);
            }
        }
    }
}
