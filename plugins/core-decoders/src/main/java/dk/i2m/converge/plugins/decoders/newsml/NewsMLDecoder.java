/*
 *  Copyright (C) 2011 - 2012 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.decoders.newsml;

import dk.i2m.converge.core.content.ContentTag;
import dk.i2m.converge.core.content.catalogue.Catalogue;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.logging.LogSeverity;
import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.newswire.NewswireItemAttachment;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.newswire.NewswireServiceProperty;
import dk.i2m.converge.core.plugin.ArchiveException;
import dk.i2m.converge.core.plugin.NewswireDecoder;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.core.utils.FileExtensionFilter;
import dk.i2m.converge.core.utils.FileUtils;
import dk.i2m.converge.core.utils.StringUtils;
import com.getconverge.nar.newsml.v1_0.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Decoder for newswires formatted using the NewsML 1.0 format.
 *
 * @author <a href="mailto:allan@i2m.dk">Allan Lykke Christensen</a>
 */
@dk.i2m.converge.core.annotations.NewswireDecoder
public class NewsMLDecoder implements NewswireDecoder {

    private static final Logger LOG = Logger.getLogger(NewsMLDecoder.class.getName());
    private static final DateFormat NEWSML_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmssZZZZ");
    private static final String JAXB_CONTEXT = "dk.i2m.converge.nar.newsml.v1_0";
    private final ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.decoders.newsml.Messages");
    /**
     * Properties made available by the decoder.
     */
    private Map<String, String> availableProperties = null;
    /**
     * Instance properties used during the decoding process.
     */
    private Map<String, String> properties = new HashMap<String, String>();
    /**
     * Rendition mappings from the instance properties.
     */
    private Map<String, String> renditionMapping = new HashMap<String, String>();
    /**
     * Location from where the NewsML files should be processed.
     */
    private String location;
    /**
     * Location to move NewsML files after processing.
     */
    private String processedLocation;
    /**
     * Move the NewsML files after processing.
     */
    private boolean moveProcessed;
    /**
     * Delete the NewsML files after processing.
     */
    private boolean deleteProcessed;
    /**
     * Should a Catalogue be used to store NewsML attachments.
     */
    private boolean useCatalogue = false;
    /**
     * Catalogue to store NewsML attachments.
     */
    private Catalogue catalogue = null;
    /**
     * Context used during the decoding process.
     */
    private PluginContext pluginCtx;
    /**
     * Instance of the newswire service used during the decoding process.
     */
    private NewswireService newswireService;

    /**
     * Properties available for configuring the decoder.
     */
    public enum Property {

        /**
         * Location of the NewsML files to process.
         */
        PROPERTY_NEWSWIRE_LOCATION,
        /**
         * Unique identifier of the catalogue where to store NewsML attachments.
         */
        PROPERTY_ATTACHMENT_CATALOGUE,
        /**
         * Location where to move the processed NewsML files.
         */
        PROPERTY_NEWSWIRE_PROCESSED_LOCATION,
        /**
         * Should the NewsML files be deleted after processing.
         */
        PROPERTY_NEWSWIRE_DELETE_AFTER_PROCESS,
        /**
         * Mapping of attachment types to renditions in the attachment
         * catalogue.
         */
        PROPERTY_RENDITION_MAPPING
    }

    /**
     * {@inheritDoc }
     */
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

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return bundle.getString("PLUGIN_NAME");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return bundle.getString("PLUGIN_DESCRIPTION");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getVendor() {
        return bundle.getString("PLUGIN_VENDOR");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Date getDate() {
        try {
            final String PATTERN = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat format = new SimpleDateFormat(PATTERN);
            return format.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception ex) {
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void decode(PluginContext ctx, NewswireService newswire) {
        this.pluginCtx = ctx;
        this.newswireService = newswire;
        this.properties = newswire.getPropertiesMap();

        readRenditionMappings();
        readLocations();
        readCatalogue();

        if (!validateProperties()) {
            log(LogSeverity.SEVERE, "LOG_DECODER_ABORTED");
            return;
        }

        readNewswires();
    }

    /**
     * Determine if a given property is set in the newswire service instance.
     *
     * @param property {@link Property} to check
     * @return {@code true} if the {@link Property} is set, otherwise
     * {@code false}
     */
    private boolean isPropertySet(Property property) {
        return properties.containsKey(property.name());
    }

    /**
     * Gets a {@link Property} from the newswire service instance.
     *
     * @param property {@link Property} to get
     * @return Value of the {@link Property} or {@code null} if the
     * {@link Property} was not set
     */
    private String getProperty(Property property) {
        return properties.get(property.name());
    }

    /**
     * Read rendition mapping properties and store them in the
     * {@link NewsMLDecoder#renditionMapping} map.
     */
    private void readRenditionMappings() {
        final String SEPARATOR = ";";
        for (NewswireServiceProperty p : newswireService.getProperties()) {
            if (p.getKey().equals(Property.PROPERTY_RENDITION_MAPPING.name())) {
                String[] mapping = p.getValue().split(SEPARATOR);
                if (mapping != null && mapping.length == 2) {
                    renditionMapping.put(mapping[0], mapping[1]);
                }
            }
        }
    }

    /**
     * Read the location of the NewsML files to be processed into the plug-in.
     * After executing this method {@link NewsMLDecoder#moveProcessed},
     * {@link NewsMLDecoder#deleteProcessed} and
     * {@link NewsMLDecoder#processedLocation} are all set.
     */
    private void readLocations() {
        location = getProperty(Property.PROPERTY_NEWSWIRE_LOCATION);
        deleteProcessed = false;
        moveProcessed = false;

        if (isPropertySet(Property.PROPERTY_NEWSWIRE_PROCESSED_LOCATION)) {
            moveProcessed = true;
            deleteProcessed = false;
            processedLocation = getProperty(Property.PROPERTY_NEWSWIRE_PROCESSED_LOCATION);
        }

        if (isPropertySet(Property.PROPERTY_NEWSWIRE_DELETE_AFTER_PROCESS)) {
            deleteProcessed = true;
        }
    }

    /**
     * Reads the catalogue settings.
     */
    private void readCatalogue() {
        if (isPropertySet(Property.PROPERTY_ATTACHMENT_CATALOGUE)) {
            String catStr = getProperty(Property.PROPERTY_ATTACHMENT_CATALOGUE);
            try {
                Long catId = Long.valueOf(catStr);
                catalogue = pluginCtx.findCatalogue(catId);
                if (catalogue == null) {
                    log(LogSeverity.WARNING, "LOG_UNKNOWN_CATALOGUE", catId);
                    useCatalogue = false;
                } else {
                    useCatalogue = true;
                }
            } catch (NumberFormatException ex) {
                log(LogSeverity.SEVERE, "LOG_INVALID_CATALOGUE_ID", catStr);
            }
        } else {
            useCatalogue = false;
        }

    }

    /**
     * Validates the properties read from the newswire service instance.
     * <p/>
     * @return {@code true} of the properties are sufficient to execute the
     * decoder, otherwise {@code false}
     */
    private boolean validateProperties() {
        if (!isPropertySet(Property.PROPERTY_NEWSWIRE_LOCATION)) {
            log(LogSeverity.SEVERE, "LOG_LOCATION_MISSING");
            return false;
        }

        File newswireDirectory = new File(location);
        if (!newswireDirectory.exists()) {
            log(LogSeverity.SEVERE, "LOG_LOCATION_NOT_EXIST", location);
        }

        if (!moveProcessed && !deleteProcessed) {
            log(LogSeverity.SEVERE, "LOG_PROCESS_ACTION_MISSING");
            return false;
        }

        if (moveProcessed) {
            if (!isPropertySet(Property.PROPERTY_NEWSWIRE_PROCESSED_LOCATION)) {
                log(LogSeverity.SEVERE, "LOG_PROCESS_LOCATION_MISSING");
            } else {
                File processNewswireDirectory = new File(processedLocation);
                if (!processNewswireDirectory.exists()) {
                    log(LogSeverity.SEVERE, "LOG_PROCESS_LOCATION_NOT_EXIST", processedLocation);
                } else if (!processNewswireDirectory.canWrite()) {
                    log(LogSeverity.SEVERE, "LOG_PROCESS_LOCATION_NOT_WRITABLE", processedLocation);
                }
            }
        }

        return true;
    }

    private void readNewswires() {
        try {
            JAXBContext jc = JAXBContext.newInstance(JAXB_CONTEXT, getClass().getClassLoader());
            Unmarshaller u = jc.createUnmarshaller();

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = tFactory.newTransformer();
            } catch (TransformerConfigurationException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }

            // Read NewsML files from the newswire location
            File newswireDirectory = new File(location);

            if (!newswireDirectory.exists()) {
                LOG.log(Level.SEVERE, "Unknown directory {0}", location);
                return;
            }

            //File processedDirectory = new File(processedLocation);
            FilenameFilter xmlFiles = new FileExtensionFilter("xml");

            // Get list of NewsML files to process
            File[] xmlFilesToProcess = newswireDirectory.listFiles(xmlFiles);

            for (File file : xmlFilesToProcess) {
                boolean fileMissing = false;
                List<NewswireItem> results = pluginCtx.findNewswireItemsByExternalId(file.getName());

                if (results.isEmpty()) {
                    List<File> moveOrDelete = new ArrayList<File>();
                    NewswireItem newswireItem = new NewswireItem();
                    newswireItem.setExternalId(file.getName());
                    newswireItem.setNewswireService(newswireService);

                    NewsML newsMl = (NewsML) u.unmarshal(file);
                    Calendar itemCal = Calendar.getInstance();
                    try {
                        Date itemDate = NEWSML_DATE_FORMAT.parse(newsMl.getNewsEnvelope().getDateAndTime().getValue());
                        itemCal.setTime(itemDate);
                        newswireItem.setDate(itemCal);
                    } catch (ParseException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        newswireItem.setDate(itemCal);
                    }

                    for (NewsItem item : newsMl.getNewsItem()) {

                        List<TopicSet> topicSets = item.getNewsComponent().
                                getTopicSet();

                        for (TopicSet topicSet : topicSets) {
                            for (Topic topic : topicSet.getTopic()) {
                                for (Description description : topic.
                                        getDescription()) {
                                    ContentTag topicTag = pluginCtx.
                                            findOrCreateContentTag(description.
                                            getValue());

                                    if (!newswireItem.getTags().contains(
                                            topicTag)) {
                                        newswireItem.getTags().add(topicTag);
                                    }
                                }
                            }
                        }

                        for (NewsComponent c : item.getNewsComponent().
                                getNewsComponent()) {
                            if (c.getRole().getFormalName().equalsIgnoreCase(
                                    "Main Text")) {

                                StringBuilder byLine = new StringBuilder();
                                for (CreditLine cl : c.getNewsLines().
                                        getCreditLine()) {
                                    for (Object clObj : cl.getContent()) {
                                        byLine.append(clObj);
                                    }
                                }
                                newswireItem.setAuthor(byLine.toString());

                                List<Object> objs = c.getNewsLines().
                                        getHeadLineAndSubHeadLine();
                                StringBuilder title = new StringBuilder();

                                for (Object o : objs) {
                                    if (o instanceof HeadLine) {
                                        HeadLine headline = (HeadLine) o;

                                        for (Object line : headline.getContent()) {
                                            title.append(line).append(" ");
                                        }
                                    }
                                }
                                newswireItem.setTitle(title.toString().trim());

                                List<NewsLine> newsLines = c.getNewsLines().
                                        getNewsLine();
                                StringBuilder summary = new StringBuilder();
                                for (NewsLine nl : newsLines) {
                                    for (NewsLineText line :
                                            nl.getNewsLineText()) {
                                        for (Object objLine : line.getContent()) {
                                            summary.append(objLine).append(" ");
                                        }
                                    }
                                }

                                newswireItem.setSummary(
                                        summary.toString().trim());

                                for (ContentItem ci : c.getContentItem()) {
                                    StringWriter sw = new StringWriter();
                                    StreamResult result = new StreamResult(sw);

                                    try {
                                        if (ci.getFormat().getFormalName().
                                                equalsIgnoreCase("XHTML")) {
                                            for (DataContent dc : ci.
                                                    getDataContent()) {

                                                for (Object obj : dc.getAny()) {
                                                    Element element =
                                                            (Element) obj;
                                                    NodeList bodies =
                                                            element.
                                                            getElementsByTagName(
                                                            "body");
                                                    if (bodies.getLength()
                                                            > 0) {
                                                        Node body =
                                                                bodies.item(0);
                                                        DOMSource src =
                                                                new DOMSource(
                                                                body);
                                                        transformer.transform(
                                                                src,
                                                                result);
                                                        String storyBody =
                                                                sw.toString();
                                                        newswireItem.addContent(storyBody.
                                                                replaceAll(
                                                                "<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>",
                                                                ""));
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception ex) {
                                        LOG.log(Level.WARNING, ex.getMessage(),
                                                ex);
                                    }
                                }
                            } else if (c.getRole().getFormalName().
                                    equalsIgnoreCase("Main Picture") || c.
                                    getRole().getFormalName().
                                    equalsIgnoreCase("Main Graphic")) {


                                for (NewsComponent picComponent : c.
                                        getNewsComponent()) {
                                    String componentRole =
                                            picComponent.getRole().getFormalName();
                                    if (componentRole.equalsIgnoreCase(
                                            "Picture Caption")) {

                                        StringBuilder byLine =
                                                new StringBuilder();
                                        for (CreditLine cl : c.getNewsLines().
                                                getCreditLine()) {
                                            for (Object clObj : cl.getContent()) {
                                                byLine.append(clObj);
                                            }
                                        }
                                        newswireItem.setAuthor(byLine.toString());


                                        List<Object> objs = c.getNewsLines().
                                                getHeadLineAndSubHeadLine();
                                        StringBuilder title =
                                                new StringBuilder();

                                        for (Object o : objs) {
                                            if (o instanceof HeadLine) {
                                                HeadLine headline = (HeadLine) o;

                                                for (Object line : headline.
                                                        getContent()) {
                                                    title.append(line).append(
                                                            " ");
                                                }
                                            }
                                        }
                                        newswireItem.setTitle(title.toString().
                                                trim());




                                        for (ContentItem ci :
                                                picComponent.getContentItem()) {
                                            StringWriter sw =
                                                    new StringWriter();
                                            StreamResult result =
                                                    new StreamResult(sw);

                                            try {
                                                if (ci.getFormat().
                                                        getFormalName().
                                                        equalsIgnoreCase(
                                                        "XHTML")) {
                                                    for (DataContent dc :
                                                            ci.getDataContent()) {

                                                        for (Object obj :
                                                                dc.getAny()) {
                                                            Element element =
                                                                    (Element) obj;
                                                            NodeList bodies =
                                                                    element.
                                                                    getElementsByTagName(
                                                                    "body");
                                                            if (bodies.getLength()
                                                                    > 0) {
                                                                Node body =
                                                                        bodies.
                                                                        item(
                                                                        0);
                                                                DOMSource src =
                                                                        new DOMSource(
                                                                        body);
                                                                transformer.
                                                                        transform(
                                                                        src,
                                                                        result);
                                                                String storyBody =
                                                                        StringUtils.
                                                                        stripHtml(
                                                                        sw.
                                                                        toString());
                                                                newswireItem.
                                                                        addSummary(
                                                                        storyBody);
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (Exception ex) {
                                                LOG.log(Level.WARNING, ex.
                                                        getMessage(), ex);
                                            }
                                        }
                                    } else if (componentRole.equalsIgnoreCase(
                                            "Image Wrapper")) {
                                        String imgDir =
                                                this.properties.get(Property.PROPERTY_NEWSWIRE_LOCATION.
                                                name());
                                        for (ContentItem picContentItem :
                                                picComponent.getContentItem()) {
                                            if (picContentItem.getHref() != null) {
                                                for (com.getconverge.nar.newsml.v1_0.Property p :
                                                        picContentItem.
                                                        getCharacteristics().
                                                        getProperty()) {
                                                    if (p.getFormalName().
                                                            equalsIgnoreCase(
                                                            "PicType")) {
                                                        NewswireItemAttachment attachment =
                                                                new NewswireItemAttachment();
                                                        attachment.
                                                                setNewswireItem(
                                                                newswireItem);

                                                        File imgFile = new File(
                                                                imgDir,
                                                                picContentItem.
                                                                getHref());
                                                        moveOrDelete.add(imgFile);

                                                        if (useCatalogue) {
                                                            attachment.
                                                                    setCatalogue(
                                                                    catalogue);
                                                            try {
                                                                attachment.
                                                                        setCataloguePath(
                                                                        pluginCtx.
                                                                        archive(
                                                                        imgFile,
                                                                        catalogue.
                                                                        getId(),
                                                                        imgFile.
                                                                        getName()));

                                                            } catch (ArchiveException ex) {
                                                                fileMissing =
                                                                        true;
                                                            }
                                                        } else {
                                                            try {
                                                                attachment.
                                                                        setData(FileUtils.
                                                                        getBytes(
                                                                        imgFile));
                                                            } catch (IOException ex) {
                                                                LOG.log(Level.SEVERE,
                                                                        null, ex);
                                                            }
                                                        }
                                                        attachment.
                                                                setContentType(
                                                                "image/jpeg");
                                                        attachment.
                                                                setDescription(p.
                                                                getValue());
                                                        attachment.setFilename(picContentItem.
                                                                getHref());
                                                        attachment.setSize(imgFile.
                                                                length());

                                                        if (renditionMapping.
                                                                containsKey(p.
                                                                getValue())) {
                                                            String renditionId =
                                                                    renditionMapping.
                                                                    get(p.
                                                                    getValue());
                                                            Rendition rendition =
                                                                    pluginCtx.
                                                                    findRenditionByName(
                                                                    renditionId);
                                                            attachment.
                                                                    setRendition(
                                                                    rendition);
                                                        }

                                                        newswireItem.
                                                                getAttachments().
                                                                add(attachment);

                                                        if (p.getValue().
                                                                equalsIgnoreCase(
                                                                "Thumbnail")) {
                                                            newswireItem.
                                                                    setThumbnailUrl(
                                                                    attachment.
                                                                    getCatalogueUrl());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (fileMissing) {
                        log(LogSeverity.WARNING, "LOG_NEWSWIRE_FILE_MISSING", file.getName());
                    } else {
                        // Create the newswire in the database
                        NewswireItem nwi = pluginCtx.createNewswireItem(newswireItem);

                        // Index the newswire in the search engine
                        try {
                            pluginCtx.index(nwi);
                        } catch (SearchEngineIndexingException seie) {
                            LOG.log(Level.WARNING, seie.getMessage());
                            LOG.log(Level.FINEST, "", seie);
                        }

                        moveOrDelete.add(file);
                        for (File f : moveOrDelete) {
                            onPostProcess(f);
                        }
                    }
                }
            }
        } catch (JAXBException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
            LOG.log(Level.FINEST, "", ex);
        }
    }

    /**
     * Post processing of a {@link File}. If the processed files were set to be
     * moved, they will be relocated to the processed location. If they were set
     * to be deleted, they will be deleted from their original location.
     * <p/>
     * @param file {@link File} to process
     */
    private void onPostProcess(File file) {
        if (this.moveProcessed) {
            File moveTo = new File(this.processedLocation, file.getName());
            try {
                org.apache.commons.io.FileUtils.moveFile(file, moveTo);
            } catch (IOException ex) {
                log(LogSeverity.WARNING, "LOG_COULD_NOT_MOVE_X_TO_Y",
                        new Object[]{file.getAbsolutePath(), moveTo.
                            getAbsolutePath()});
            }
        } else if (this.deleteProcessed) {
            if (!file.delete()) {
                log(LogSeverity.WARNING, "LOG_COULD_NOT_DELETE_X",
                        new Object[]{file.getAbsolutePath()});
            }
        }
    }

    private void log(LogSeverity severity, String msg) {
        log(severity, msg, new Object[]{});
    }

    private void log(LogSeverity severity, String msg, Object param) {
        log(severity, msg, new Object[]{param});
    }

    private void log(LogSeverity severity, String msg, Object[] params) {
        this.pluginCtx.log(severity, bundle.getString(msg), params,
                this.newswireService,
                this.newswireService.getId());
    }
}
