/*
 * Copyright (C) 2012 Interactive Media Management
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
package dk.i2m.converge.plugins.actions.transcode;

import dk.i2m.converge.core.annotations.CatalogueAction;
import dk.i2m.converge.core.content.catalogue.CatalogueHookInstance;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.plugin.CatalogueEvent;
import dk.i2m.converge.core.plugin.CatalogueEventException;
import dk.i2m.converge.core.plugin.CatalogueHook;
import dk.i2m.converge.core.plugin.PluginContext;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.tika.Tika;

/**
 * {@link CatalogueHook} for transcoding a media file from one format to
 * another and saving the media file as a rendition.
 *
 * @author Allan Lykke Christensen
 */
@CatalogueAction
public class TranscodeHook extends CatalogueHook {

    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.actions.transcode.Messages");

    private Map<String, String> instanceProperties =
            new HashMap<String, String>();

    private Map<String, String> availableProperties = null;

    private PluginContext pluginCtx;

    private CatalogueHookInstance hookInstance;

    /** Validate properties for the {@link CatalogueHook}. */
    enum Property {

        ORIGINAL_RENDITION,
        GENERATE_RENDITION,
        GENERATE_RENDITION_FORMAT,
        GENERATE_RENDITION_USE_DEFAULT_DIMENSION,
        GENERATE_RENDITION_USE_ORIGINAL_DIMENSION,
        GENERATE_RENDITION_WIDTH,
        GENERATE_RENDITION_HEIGHT,
        ENABLE_ON_UPDATE
    }

    @Override
    public void execute(PluginContext ctx, CatalogueEvent event,
            CatalogueHookInstance instance) throws CatalogueEventException {
        this.pluginCtx = ctx;
        this.hookInstance = instance;
        instanceProperties = instance.getPropertiesAsMap();

        // Validate the instance properties
        if (!validateProperties()) {
            return;
        }

        boolean executeOnUpdate = false;

        if (isPropertySet(Property.ENABLE_ON_UPDATE)) {
            executeOnUpdate = getPropertyAsBoolean(Property.ENABLE_ON_UPDATE);
        }

        // Check that we only re-act to the Upload of new or update renditions
        if (event.getType() != CatalogueEvent.Event.UploadRendition && event.
                getType() != CatalogueEvent.Event.UpdateRendition) {
            return;
        }

        // Check that we only re-act to the Upload of updated renditions if the ENABLE_ON_UPDATE was set
        if (!executeOnUpdate && event.getType()
                == CatalogueEvent.Event.UpdateRendition) {
            return;
        }

        // Determine if we need to act on the uploaded rendition
        MediaItemRendition uploadRendition = event.getRendition();
        if (!uploadRendition.isVideo()) {
            // Ignore non-videos
            return;
        }
        
        Rendition rendition = uploadRendition.getRendition();

        if (!rendition.getName().equalsIgnoreCase(getProperty(
                Property.ORIGINAL_RENDITION))) {
            return;
        }

        if (!uploadRendition.isAudio() && !uploadRendition.isVideo()) {
            return;
        }

        // Which rendition should be generated
        String generateRenditionName = getProperty(Property.GENERATE_RENDITION);
        Rendition generateRendition = ctx.findRenditionByName(
                generateRenditionName);

        if (generateRendition == null) {
            throw new CatalogueEventException("Rendition "
                    + generateRenditionName + " does not exist");
        }

        // Determine size
        boolean useGenerateDimensions = getPropertyAsBoolean(
                Property.GENERATE_RENDITION_USE_DEFAULT_DIMENSION);
        boolean useDefaultimensions = getPropertyAsBoolean(
                Property.GENERATE_RENDITION_USE_ORIGINAL_DIMENSION);

        int width = 0;
        int height = 0;

        if (useGenerateDimensions) {
            width = generateRendition.getDefaultWidth();
            height = generateRendition.getDefaultHeight();
        } else if (useDefaultimensions) {
            width = 0;
            height = 0;
        } else {
            if (isPropertySet(Property.GENERATE_RENDITION_WIDTH)) {
                width = getPropertyAsInteger(Property.GENERATE_RENDITION_WIDTH);
            }
            if (isPropertySet(Property.GENERATE_RENDITION_HEIGHT)) {
                height =
                        getPropertyAsInteger(Property.GENERATE_RENDITION_HEIGHT);
            }
        }

        // Transcode the video
        // Create temporary file to store the still
        String prefix = "xxx-" + uploadRendition.getMediaItem().getId();
        String suffix = "" + generateRendition.getId() + "." + getProperty(Property.GENERATE_RENDITION_FORMAT);
        File tempFile;
        try {
            tempFile = File.createTempFile(prefix, suffix);
        } catch (IOException ex) {
            ctx.log(LogSeverity.SEVERE, "Could not create temporary file for storing the transcoded video. "
                    + ex.getMessage(), instance, instance.getId());
            return;
        }
        
        TranscodeVideo video = new TranscodeVideo(uploadRendition.
                getAbsoluteFilename(), tempFile.getAbsoluteFile().toString());
        video.setHeight(height);
        video.setWidth(width);

        try {
            video.encode();
        } catch (Throwable t) {
            ctx.log(LogSeverity.SEVERE, "Could not transcode video. " + t.
                    getMessage(), instance, instance.getId());
            return;
        }

        try {
            String contentType = "application/octet-stream";
            // Create the Media Item Rendition

            // Detect content type of generated file
            try {
                Tika tika = new Tika();
                contentType = tika.detect(tempFile);
            } catch (IOException ex) {
                log(LogSeverity.WARNING, "LOG_COULD_NOT_DETECT_FILE_TYPE_FOR_X",
                        new Object[]{ex.getMessage()});
            }

            MediaItemRendition mir = ctx.createMediaItemRendition(tempFile,
                    event.getItem().getId(), generateRendition.getId(),
                    tempFile.getName() + "." + getProperty(
                    Property.GENERATE_RENDITION_FORMAT), contentType);

        } catch (IOException ex) {
            throw new CatalogueEventException(ex);
        }
    }

    private boolean validateProperties() {
        if (!isPropertySet(Property.GENERATE_RENDITION_FORMAT)) {
            log(LogSeverity.SEVERE,
                    "LOG_GENERATE_RENDITION_FORMAT_NOT_SPECIFIED");
            return false;
        }

        if (!isPropertySet(Property.ORIGINAL_RENDITION)) {
            log(LogSeverity.SEVERE, "LOG_ORIGINAL_RENDITION_NOT_SPECIFIED");
            return false;
        }
        return true;
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            for (Property p : Property.values()) {
                availableProperties.put(bundle.getString(p.name()), p.name());
            }
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
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    // -- Utility Methods -----
    private void log(LogSeverity severity, String msg) {
        log(severity, msg, new Object[]{});
    }

    private void log(LogSeverity severity, String msg, Object param) {
        log(severity, msg, new Object[]{param});
    }

    private void log(LogSeverity severity, String msg, Object[] params) {
        this.pluginCtx.log(severity, bundle.getString(msg), params,
                this.hookInstance,
                this.hookInstance.getId());
    }

    private boolean isPropertySet(Property p) {
        return instanceProperties.containsKey(p.name());
    }

    private String getProperty(Property p) {
        return instanceProperties.get(p.name());
    }

    private Boolean getPropertyAsBoolean(Property p) {
        return Boolean.parseBoolean(getProperty(p));
    }

    private Long getPropertyAsLong(Property p) {
        return Long.valueOf(getProperty(p));
    }

    private Integer getPropertyAsInteger(Property p) {
        return Integer.valueOf(getProperty(p));
    }
}
