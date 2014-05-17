/*
 * Copyright (C) 2011 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.actions.resizeimage;

import dk.i2m.converge.core.annotations.CatalogueAction;
import dk.i2m.converge.core.content.catalogue.CatalogueHookInstance;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.plugin.CatalogueEvent;
import dk.i2m.converge.core.plugin.CatalogueEventException;
import dk.i2m.converge.core.plugin.CatalogueHook;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.utils.FileUtils;
import dk.i2m.converge.core.utils.ImageUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * {@link CatalogueHook} resizing an uploaded image and saves the resized image as a {@link Rendition} of the {@link MediaItem}.
 *
 * @author Allan Lykke Christensen
 */
@CatalogueAction
public class ResizeImageHook extends CatalogueHook {

    public static final String ORIGINAL_RENDITION = "rendition_original";

    public static final String RESIZED_RENDITION = "rendition_resized";

    public static final String RESIZE_WIDTH = "width";

    public static final String RESIZE_HEIGHT = "height";

    public static final String RESIZE_QUALITY = "quality";

    public static final String ENABLE_ON_UPDATE = "ENABLE_ON_UPDATE";

    private Map<String, String> instanceProperties = new HashMap<String, String>();

    private Map<String, String> availableProperties = null;

    private String originalRendition;

    private String resizeRendition;

    private Integer width;

    private Integer height;

    private Integer quality;

    private ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.actions.resizeimage.Messages");

    @Override
    public void execute(PluginContext ctx, CatalogueEvent event, CatalogueHookInstance instance) throws CatalogueEventException {

        instanceProperties = instance.getPropertiesAsMap();

        validateProperties();

        boolean executeOnUpdate = false;

        if (instanceProperties.containsKey(ENABLE_ON_UPDATE)) {
            executeOnUpdate = Boolean.parseBoolean(instanceProperties.get(ENABLE_ON_UPDATE));

        }

        // Check that we only re-act to the Upload of new or update renditions
        if (event.getType() != CatalogueEvent.Event.UploadRendition && event.getType() != CatalogueEvent.Event.UpdateRendition) {
            return;
        }

        // Check that we only re-act to the Upload of updated renditions if the ENABLE_ON_UPDATE was set
        if (!executeOnUpdate && event.getType() == CatalogueEvent.Event.UpdateRendition) {
            return;
        }


        // Determine if we need to act on the uploaded rendition
        MediaItemRendition uploadRendition = event.getRendition();
        Rendition rendition = uploadRendition.getRendition();

        if (!rendition.getName().equalsIgnoreCase(originalRendition)) {
            return;
        }

        if (!uploadRendition.isImage()) {
            return;
        }

        // Which rendition should be generated
        Rendition generateRendition = ctx.findRenditionByName(resizeRendition);

        if (generateRendition == null) {
            throw new CatalogueEventException("Rendition " + resizeRendition + " does not exist");
        }

        // Load the original item
        URL originalFile;
        try {
            originalFile = new URL(uploadRendition.getAbsoluteFilename());
        } catch (MalformedURLException ex) {
            throw new CatalogueEventException(ex);
        }
        try {
            // Generate thumbnail
            byte[] filedata = ImageUtils.generateThumbnail(FileUtils.getBytes(originalFile), width, height, quality);

            // Create temporary file to store the thumbnail
            String prefix = "xxx-" + uploadRendition.getMediaItem().getId();
            String suffix = "" + generateRendition.getId();
            File tempFile = File.createTempFile(prefix, suffix);

            // Output thumbnail into temporary file
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(filedata);
            fos.close();

            // Create the Media Item Rendition
            MediaItemRendition mir = ctx.createMediaItemRendition(tempFile,
                    event.getItem().getId(), generateRendition.getId(),
                    tempFile.getName() + "." + uploadRendition.getExtension(),
                    uploadRendition.getContentType());
        } catch (InterruptedException ex) {
            throw new CatalogueEventException(ex);
        } catch (IOException ex) {
            throw new CatalogueEventException(ex);
        } catch (IllegalArgumentException ex) {
            throw new CatalogueEventException("Could not resize image", ex);
        }
    }

    /**
     * Validates that the necessary properties have been provided. If not, a 
     * {@link CatalogueEventException} will be thrown. Validated properties
     * are initialised in the necessary instance properties.
     * 
     * @throws CatalogueEventException 
     *          If a required property is missing or specified incorrectly
     */
    private void validateProperties() throws CatalogueEventException {
        if (!instanceProperties.containsKey(ORIGINAL_RENDITION)) {
            throw new CatalogueEventException("Property " + ORIGINAL_RENDITION + " missing");
        } else {
            this.originalRendition = instanceProperties.get(ORIGINAL_RENDITION);
        }

        if (!instanceProperties.containsKey(RESIZED_RENDITION)) {
            throw new CatalogueEventException("Property " + RESIZED_RENDITION + " missing");
        } else {
            this.resizeRendition = instanceProperties.get(RESIZED_RENDITION);
        }

        if (instanceProperties.containsKey(RESIZE_WIDTH)) {
            String resizeWidth = instanceProperties.get(RESIZE_WIDTH);
            try {
                this.width = Integer.valueOf(resizeWidth.trim());
            } catch (NumberFormatException ex) {
                throw new CatalogueEventException("Property " + RESIZE_WIDTH + " is not a number");
            }
        } else {
            throw new CatalogueEventException("Property " + RESIZE_WIDTH + " missing");
        }

        if (instanceProperties.containsKey(RESIZE_HEIGHT)) {
            String resizeHeight = instanceProperties.get(RESIZE_HEIGHT);
            try {
                this.height = Integer.valueOf(resizeHeight.trim());
            } catch (NumberFormatException ex) {
                throw new CatalogueEventException("Property " + RESIZE_HEIGHT + " is not a number");
            }
        } else {
            throw new CatalogueEventException("Property " + RESIZE_HEIGHT + " missing");
        }

        if (instanceProperties.containsKey(RESIZE_QUALITY)) {
            String resizeQuality = instanceProperties.get(RESIZE_QUALITY);
            try {
                this.quality = Integer.valueOf(resizeQuality.trim());
            } catch (NumberFormatException ex) {
                throw new CatalogueEventException("Property " + RESIZE_QUALITY + " is not a number");
            }
        } else {
            throw new CatalogueEventException("Property " + RESIZE_QUALITY + " missing");
        }
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            availableProperties.put(bundle.getString(ORIGINAL_RENDITION), ORIGINAL_RENDITION);
            availableProperties.put(bundle.getString(RESIZED_RENDITION), RESIZED_RENDITION);
            availableProperties.put(bundle.getString(RESIZE_WIDTH), RESIZE_WIDTH);
            availableProperties.put(bundle.getString(RESIZE_HEIGHT), RESIZE_HEIGHT);
            availableProperties.put(bundle.getString(RESIZE_QUALITY), RESIZE_QUALITY);
            availableProperties.put(bundle.getString(ENABLE_ON_UPDATE), ENABLE_ON_UPDATE);
        }
        return availableProperties;
    }

    @Override
    public String getName() {
        return bundle.getString("PLUGIN_NAME");
    }

    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }

    @Override
    public String getDescription() {
        return bundle.getString("PLUGIN_DESCRIPTION");
    }

    @Override
    public String getVendor() {
        return bundle.getString("PLUGIN_VENDOR");
    }

    @Override
    public Date getDate() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception ex) {
            return Calendar.getInstance().getTime();
        }
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public boolean isSupportBatch() {
        return true;
    }
}
