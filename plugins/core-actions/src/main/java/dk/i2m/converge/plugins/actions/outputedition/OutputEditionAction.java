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
package dk.i2m.converge.plugins.actions.outputedition;

import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.commons.io.FileUtils;

/**
 * Plug-in for outputting the edition assets to a folder.
 * <p/>
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.OutletAction
public class OutputEditionAction implements EditionAction {

    public static final String TEMPLATE_TAG_NEWS_ITEM_PLACEMENT = "newsItemPlacement";
    public static final String TEMPLATE_TAG_EDITION = "edition";
    public static final String TEMPLATE_TAG_MEDIA_ITEM_RENDITION = "mediaItemRendition";
    public static final String TEMPLATE_TAG_MEDIA_ITEM_ATTACHMENT = "mediaItemAttachment";
    
    private static final DateFormat DATE_PARSER = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private ResourceBundle bundle = ResourceBundle.getBundle(
            "dk.i2m.converge.plugins.actions.outputedition.Messages");
    private Map<String, String> availableProperties = null;
    private Map<String, String> instanceProperties =
            new HashMap<String, String>();
    private PluginContext pluginCtx;
    private OutletEditionAction actionInstance;


    enum Property {

        /**
         * Property containing the rendition to output.
         */
        OUTPUT_RENDITION,
        /**
         * Property containing the (local) output location of the assets.
         */
        OUTPUT_LOCATION,
        /**
         * Property determining the filenames of the outputted media items.
         */
        OUTPUT_MEDIA_ITEM_FILENAME,
        /**
         * Property determining if news items should be outputted (true/false)
         */
        OUTPUT_NEWS_ITEM,
        /**
         * Property containing the filenames of individual news items.
         */
        OUTPUT_NEWS_ITEM_FILENAME,
        /**
         * Property containing the template for outputting each news item.
         */
        OUTPUT_NEWS_ITEM_TEMPLATE,
        /**
         * Property determining if the outputted news item should be a digest
         * (true) or individual e-mails (false)
         */
        OUTPUT_NEWS_ITEM_DIGEST,
        /**
         * Property containing the filename of the digest.
         */
        OUTPUT_NEWS_ITEM_DIGEST_FILENAME,
        /**
         * Header template for the digest file.
         */
        OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_HEADER,
        /**
         * Body template for the digest file. This template is compiled for ever
         * entry in the edition.
         */
        OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_BODY,
        /**
         * Footer template for the digest file.
         */
        OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_FOOTER,}

    @Override
    public void execute(PluginContext ctx, Edition edition,
            OutletEditionAction action) {
        this.pluginCtx = ctx;
        this.actionInstance = action;
        this.instanceProperties = action.getPropertiesAsMap();

        String outputLocation;
        if (!isPropertySet(Property.OUTPUT_LOCATION)) {
            log(LogSeverity.SEVERE, "LOG_OUTPUT_LOCATION_MISSING");
            return;
        } else {
            outputLocation = getProperty(Property.OUTPUT_LOCATION);
            Map<String, Object> templateAttributes = new HashMap<String, Object>();
            templateAttributes.put(TEMPLATE_TAG_EDITION, edition);
            outputLocation = compileTemplate(outputLocation, templateAttributes);
        }

        Rendition rendition;
        if (!isPropertySet(Property.OUTPUT_RENDITION)) {
            log(LogSeverity.SEVERE, "LOG_OUTPUT_RENDITION_MISSING");
            return;
        } else {
            String renditionName = getProperty(Property.OUTPUT_RENDITION);
            rendition = pluginCtx.findRenditionByName(renditionName);
            if (rendition == null) {
                log(LogSeverity.SEVERE, "LOG_OUTPUT_RENDITION_INVALID", renditionName);
                return;
            }
        }
        
        String outputMediaItemFilename;
        if (!isPropertySet(Property.OUTPUT_MEDIA_ITEM_FILENAME)) {
            log(LogSeverity.SEVERE, "LOG_OUTPUT_MEDIA_ITEM_FILENAME_MISSING");
            return;
        } else {
            outputMediaItemFilename = getProperty(Property.OUTPUT_MEDIA_ITEM_FILENAME);
        }

        boolean outputNewsItem = false;
        if (isPropertySet(Property.OUTPUT_NEWS_ITEM)) {
            outputNewsItem = getPropertyAsBoolean(Property.OUTPUT_NEWS_ITEM);
        }

        String outputNewsItemTemplate = "";
        String outputNewsItemFilename = "";
        String outputNewsItemDigestTemplateBody = "";
        String outputNewsItemDigestFilename = "";
        String outputNewsItemDigestTemplateHeader = "";
        String outputNewsItemDigestTemplateFooter = "";
        boolean outputNewsItemDigest = false;

        if (outputNewsItem) {
            if (isPropertySet(Property.OUTPUT_NEWS_ITEM_DIGEST)) {
                outputNewsItemDigest = getPropertyAsBoolean(Property.OUTPUT_NEWS_ITEM_DIGEST);
            }

            if (outputNewsItemDigest) {
                if (!isPropertySet(Property.OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_BODY)) {
                    log(LogSeverity.SEVERE, "LOG_OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_BODY_MISSING");
                    outputNewsItem = false;
                } else {
                    outputNewsItemDigestTemplateBody = getProperty(Property.OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_BODY);
                }

                if (!isPropertySet(Property.OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_HEADER)) {
                    log(LogSeverity.SEVERE, "LOG_OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_HEADER_MISSING");
                    outputNewsItem = false;
                } else {
                    outputNewsItemDigestTemplateHeader = getProperty(Property.OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_HEADER);
                }

                if (!isPropertySet(Property.OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_FOOTER)) {
                    log(LogSeverity.SEVERE, "LOG_OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_BODY_MISSING");
                    outputNewsItem = false;
                } else {
                    outputNewsItemDigestTemplateFooter = getProperty(Property.OUTPUT_NEWS_ITEM_DIGEST_TEMPLATE_FOOTER);
                }

                if (!isPropertySet(Property.OUTPUT_NEWS_ITEM_DIGEST_FILENAME)) {
                    log(LogSeverity.SEVERE, "LOG_OUTPUT_NEWS_ITEM_DIGEST_FILENAME_MISSING");
                    outputNewsItem = false;
                } else {
                    outputNewsItemDigestFilename = getProperty(Property.OUTPUT_NEWS_ITEM_DIGEST_FILENAME);
                }

            } else {
                if (!isPropertySet(Property.OUTPUT_NEWS_ITEM_TEMPLATE)) {
                    log(LogSeverity.SEVERE, "LOG_OUTPUT_NEWS_ITEM_TEMPLATE_MISSING");
                    outputNewsItem = false;
                } else {
                    outputNewsItemTemplate = getProperty(Property.OUTPUT_NEWS_ITEM_TEMPLATE);
                }

                if (!isPropertySet(Property.OUTPUT_NEWS_ITEM_FILENAME)) {
                    log(LogSeverity.SEVERE, "LOG_OUTPUT_NEWS_ITEM_FILENAME_MISSING");
                    outputNewsItem = false;
                } else {
                    outputNewsItemFilename = getProperty(Property.OUTPUT_NEWS_ITEM_FILENAME);
                }
            }
        }

        // Handle News Item Output
        if (outputNewsItem) {

            if (outputNewsItemDigest) {
                StringBuilder digest = new StringBuilder();

                Map<String, Object> templateEdition = new HashMap<String, Object>();
                templateEdition.put(TEMPLATE_TAG_EDITION, edition);
                digest.append(compileTemplate(outputNewsItemDigestTemplateHeader, templateEdition));

                for (NewsItemPlacement p : edition.getPlacements()) {
                    Map<String, Object> templateAttributes = new HashMap<String, Object>();
                    templateAttributes.put(TEMPLATE_TAG_NEWS_ITEM_PLACEMENT, p);
                    String outputText = compileTemplate(outputNewsItemDigestTemplateBody, templateAttributes);
                    digest.append(outputText);
                }

                digest.append(compileTemplate(outputNewsItemDigestTemplateFooter, templateEdition));

                String newsItemFilename = compileTemplate(outputNewsItemDigestFilename, templateEdition);
                File newsItemOutput = new File(outputLocation, newsItemFilename);
                try {
                    FileUtils.writeByteArrayToFile(newsItemOutput, digest.toString().getBytes());
                } catch (IOException ex) {
                    log(LogSeverity.SEVERE, "LOG_COULD_NOT_CREATE_FILE_X_BECAUSE_Y", new Object[]{newsItemOutput.getAbsoluteFile().toString(), ex.getMessage()});
                }
            } else {
                for (NewsItemPlacement p : edition.getPlacements()) {
                    Map<String, Object> templateAttributes = new HashMap<String, Object>();
                    templateAttributes.put(TEMPLATE_TAG_NEWS_ITEM_PLACEMENT, p);
                    String outputText = compileTemplate(outputNewsItemTemplate, templateAttributes);
                    String newsItemFilename = compileTemplate(outputNewsItemFilename, templateAttributes);
                    //String newsItemFilename = "" + p.getStart() + "-" + p.getPosition() + "-" + ni.getId() + "." + outputNewsItemFormat;
                    File newsItemOutput = new File(outputLocation, newsItemFilename);
                    try {
                        FileUtils.writeStringToFile(newsItemOutput, outputText);
                    } catch (IOException ex) {
                        log(LogSeverity.SEVERE, "LOG_COULD_NOT_CREATE_FILE_X_BECAUSE_Y", new Object[]{newsItemOutput.getAbsoluteFile().toString(), ex.getMessage()});
                    }
                }
            }
        }

        // Handle Media Item Output
        for (NewsItemPlacement p : edition.getPlacements()) {
            NewsItem ni = p.getNewsItem();

            for (NewsItemMediaAttachment attachment : ni.getMediaAttachments()) {
                MediaItem mediaItem = attachment.getMediaItem();

                
                try {
                    MediaItemRendition mir = mediaItem.findRendition(rendition);
                    Map<String, Object> templateRendition = new HashMap<String, Object>();
                    templateRendition.put(TEMPLATE_TAG_MEDIA_ITEM_RENDITION, mir);
                    templateRendition.put(TEMPLATE_TAG_EDITION, edition);
                    templateRendition.put(TEMPLATE_TAG_NEWS_ITEM_PLACEMENT, p);
                    templateRendition.put(TEMPLATE_TAG_MEDIA_ITEM_ATTACHMENT, attachment);
                    
                    String outputFilename = compileTemplate(outputMediaItemFilename, templateRendition);
                    File copyFrom = new File(mir.getFileLocation());
                    File copyTo = new File(outputLocation, outputFilename);

                    try {
                        FileUtils.copyFile(copyFrom, copyTo);
                    } catch (IOException ex) {
                        log(LogSeverity.SEVERE, "LOG_COULD_NOT_COPY_X_TO_Y", new Object[]{copyFrom.getAbsoluteFile().toString(), copyTo.getAbsoluteFile().toString(), ex.getMessage()});
                    }

                } catch (RenditionNotFoundException ex) {
                    log(LogSeverity.WARNING, "LOG_RENDITION_X_NOT_AVAILABLE_FOR_MEDIA_ITEM_Y", new Object[]{rendition.getLabel(), mediaItem.getId()});
                }
            }
        }
    }

    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement,
            Edition edition, OutletEditionAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSupportEditionExecute() {
        return true;
    }

    @Override
    public boolean isSupportPlacementExecute() {
        return false;
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
            return DATE_PARSER.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception ex) {
            return Calendar.getInstance().getTime();
        }
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
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
                this.actionInstance,
                this.actionInstance.getId());
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

    /**
     * Compiles a given template with the given attributes.
     * <p/>
     * @param template   Template to compile
     * @param attributes Attributes to interpolate
     * @return Compiled template
     */
    private String compileTemplate(String template,
            Map<String, Object> attributes) {
        StringTemplate stringTemplate = new StringTemplate(template,
                DefaultTemplateLexer.class);
        for (String attr : attributes.keySet()) {
            stringTemplate.setAttribute(attr, attributes.get(attr));
        }
        return stringTemplate.toString();
    }
}
