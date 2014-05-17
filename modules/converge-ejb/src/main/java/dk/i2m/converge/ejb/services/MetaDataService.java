/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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
package dk.i2m.converge.ejb.services;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.properties.XMPProperty;
import com.google.common.base.Splitter;
import com.xuggle.xuggler.*;
import dk.i2m.converge.core.ConfigurationKey;
import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.EnrichException;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.metadata.*;
import dk.i2m.converge.ejb.facades.MetaDataFacadeLocal;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.common.ImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata.Item;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

/**
 * Service bean used for extracting meta data from files.
 *
 * @author Allan Lykke Christensen
 */
@Stateless
public class MetaDataService implements MetaDataServiceLocal {

    private static final Logger LOG = Logger.getLogger(MetaDataService.class.
            getName());

    /** URL to the OpenCalais service. */
    private static final String OPEN_CALAIS_URL =
            "http://api.opencalais.com/tag/rs/enrich";

    /** Namespace of the dublin core. */
    private static final String NS_DC = "http://purl.org/dc/elements/1.1/";

    /** Namespace of photoshop tags. */
    private static final String NS_PHOTOSHOP =
            "http://ns.adobe.com/photoshop/1.0/";

    @EJB private ConfigurationServiceLocal cfgService;

    @EJB private MetaDataFacadeLocal metaDataFacade;

    /** {@inheritDoc } */
    @Override
    public Map<String, String> extract(String location) {
        Map<String, String> metaData = new HashMap<String, String>();

        try {
            metaData.putAll(extractFromMp3(location));
        } catch (CannotExtractMetaDataException ex) {
            LOG.log(Level.FINE, ex.getMessage());
        }

        try {
            metaData.putAll(extractXmp(location));
        } catch (CannotExtractMetaDataException ex) {
            LOG.log(Level.FINE, ex.getMessage());
        }

        try {
            metaData.putAll(extractIPTC(location));
        } catch (CannotExtractMetaDataException ex) {
            LOG.log(Level.FINE, ex.getMessage());
        }

        try {
            metaData.putAll(extractImageInfo(location));
        } catch (CannotExtractMetaDataException ex) {
            LOG.log(Level.FINE, ex.getMessage());
        }

        try {
            metaData.putAll(extractMediaContainer(location));
        } catch (CannotExtractMetaDataException ex) {
            LOG.log(Level.FINE, ex.getMessage());
        }

        return metaData;
    }

    /** {@inheritDoc } */
    @Override
    public Map<String, String> extractFromMp3(String location) throws
            CannotExtractMetaDataException {
        MediaFile oMediaFile = new MP3File(new File(location));
        Map<String, String> properties = new HashMap<String, String>();
        try {
            ID3Tag[] tags = oMediaFile.getTags();
            for (ID3Tag tag : tags) {
                // check to see if we read a v1.0 tag, or a v2.3.0 tag (just for example..)
                if (tag instanceof ID3V1_0Tag) {
                    ID3V1_0Tag tagV1 = (ID3V1_0Tag) tag;
                    // does this tag happen to contain a title?
                    if (tagV1.getTitle() != null) {
                        properties.put("headline", tagV1.getTitle());
                        properties.put("title", tagV1.getTitle());
                    }

                    if (tagV1.getComment() != null) {
                        properties.put("description", tagV1.getComment());
                    }
                } else if (tag instanceof ID3V2_3_0Tag) {
                    ID3V2_3_0Tag tagV2 = (ID3V2_3_0Tag) tag;
                    if (tagV2.getTitle() != null) {
                        properties.put("title", tagV2.getTitle());
                        properties.put("headline", tagV2.getTitle());
                    }

                    if (tagV2.getTIT2TextInformationFrame() != null) {
                        properties.put("headline", tagV2.
                                getTIT2TextInformationFrame().getTitle());
                    }

                    if (tagV2.getComment() != null) {
                        properties.put("description", tagV2.getComment());
                    }
                }
            }

        } catch (ID3Exception ex) {
            throw new CannotExtractMetaDataException(ex);
        }

        return properties;
    }

    /** {@inheritDoc } */
    @Override
    public Map<String, String> extractXmp(String location) throws
            CannotExtractMetaDataException {
        Map<String, String> properties = new HashMap<String, String>();
        try {
            String xml = Sanselan.getXmpXml(new File(location));

            if (xml == null) {
                return properties;
            }

            XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xml);

            //XMPIterator iterator = xmpMeta.iterator();
            //while (iterator.hasNext()) {
            //Object obj = iterator.next();
            //logger.log(Level.INFO, "" + obj.getClass().getName() + " " + obj);
            //}

            if (xmpMeta.doesPropertyExist(NS_PHOTOSHOP, "Headline")) {
                XMPProperty headlineProperty = xmpMeta.getProperty(NS_PHOTOSHOP,
                        "Headline");
                properties.put("headline",
                        ((String) headlineProperty.getValue()).trim());
            }

            if (xmpMeta.doesArrayItemExist(NS_DC, "description", 1)) {
                XMPProperty descriptionProperty = xmpMeta.getArrayItem(NS_DC,
                        "description", 1);
                properties.put("description", ((String) descriptionProperty.
                        getValue()).trim());
            }

            if (xmpMeta.doesArrayItemExist(NS_DC, "title", 1)) {
                XMPProperty titleProperty = xmpMeta.getArrayItem(NS_DC, "title",
                        1);
                properties.put("title",
                        ((String) titleProperty.getValue()).trim());
            }

            int subjectCount = xmpMeta.countArrayItems(NS_DC, "subject");
            if (subjectCount > 0) {

                for (int i = 1; i <= subjectCount; i++) {
                    XMPProperty subjectProperty = xmpMeta.getArrayItem(NS_DC,
                            "subject", i);
                    properties.put("subject-" + i, ((String) subjectProperty.
                            getValue()).trim());
                }
            }

        } catch (XMPException ex) {
            throw new CannotExtractMetaDataException(ex);
        } catch (ImageReadException ex) {
            throw new CannotExtractMetaDataException(ex);
        } catch (IOException ex) {
            throw new CannotExtractMetaDataException(ex);
        }

        return properties;
    }

    /** {@inheritDoc } */
    @Override
    public Map<String, String> extractIPTC(String location) throws
            CannotExtractMetaDataException {
        Map<String, String> properties = new HashMap<String, String>();

        try {
            IImageMetadata meta = Sanselan.getMetadata(new File(location));

            if (meta != null) {
                ArrayList items = meta.getItems();
                for (int i = 0; i < items.size(); i++) {
                    try {
                        ImageMetadata.Item item =
                                (ImageMetadata.Item) items.get(i);

                        if (item instanceof TiffImageMetadata.Item) {
                            TiffImageMetadata.Item tiff = (Item) item;
                            properties.put(tiff.getTiffField().getTagName(), ""
                                    + tiff.getTiffField().getValue());
                        } else {
                            properties.put(item.getKeyword(), item.getText());
                        }
                    } catch (Exception ex) {
                        LOG.log(Level.INFO, ex.getMessage());
                        LOG.log(Level.FINE, "", ex);
                    }
                }
            }
        } catch (ImageReadException ex) {
            throw new CannotExtractMetaDataException(ex);
        } catch (IOException ex) {
            throw new CannotExtractMetaDataException(ex);
        }

        return properties;
    }

    /** {@inheritDoc } */
    @Override
    public Map<String, String> extractImageInfo(String location) throws
            CannotExtractMetaDataException {
        Map<String, String> properties = new HashMap<String, String>();

        try {
            ImageInfo imageInfo = Sanselan.getImageInfo(new File(location));

            if (imageInfo != null) {
                properties.put("colourSpace",
                        imageInfo.getColorTypeDescription());
                properties.put("height", String.valueOf(imageInfo.getHeight()));
                properties.put("width", String.valueOf(imageInfo.getWidth()));
                properties.put("progressive", String.valueOf(imageInfo.
                        getIsProgressive()));
            }
        } catch (ImageReadException ex) {
            throw new CannotExtractMetaDataException(ex);
        } catch (IOException ex) {
            throw new CannotExtractMetaDataException(ex);
        }

        return properties;
    }

    public Map<String, String> extractMediaContainer(String location) throws
            CannotExtractMetaDataException {
        Map<String, String> properties = new HashMap<String, String>();


        try {
            IContainer container = IContainer.make();

            // Open up the container
            if (container.open(location, IContainer.Type.READ, null) < 0) {
                throw new CannotExtractMetaDataException("could not open file: "
                        + location);
            }

            if (container.queryStreamMetaData() < 0) {
                throw new CannotExtractMetaDataException(
                        "couldn't query stream meta data for some reason...");
            }

            for (int i = 0; i < container.getNumProperties(); i++) {
                IProperty prop = container.getPropertyMetaData(i);
                properties.put(prop.getName(),
                        container.getPropertyAsString(prop.getName()));
            }

            properties.put("streams", String.valueOf(container.getNumStreams()));
            if (container.getDuration() == Global.NO_PTS) {
                properties.put("duration", String.valueOf(
                        container.getDuration()));
            } else {
                properties.put("duration", String.valueOf(container.getDuration()
                        / 1000));
            }

            if (container.getStartTime() == Global.NO_PTS) {
                properties.put("startTime", String.valueOf(container.
                        getStartTime()));
            } else {
                properties.put("startTime", String.valueOf(container.
                        getStartTime() / 1000));
            }
            properties.put("bitrate", String.valueOf(container.getBitRate()));


            for (String meta : container.getMetaData().getKeys()) {
                properties.put("container." + meta, container.getMetaData().
                        getValue(meta));
            }

            for (int i = 0; i < container.getNumStreams(); i++) {
                IStream stream = container.getStream(i);
                // Get the pre-configured decoder that can decode this stream;
                IStreamCoder coder = stream.getStreamCoder();

                for (String meta : stream.getMetaData().getKeys()) {
                    properties.put("stream." + i + ".meta." + meta, stream.
                            getMetaData().getValue(meta));
                }
                properties.put("stream." + i + ".type", coder.getCodecType().
                        name());
                properties.put("stream." + i + ".codec",
                        coder.getCodecID().name());
                properties.put("stream." + i + ".duration",
                        String.valueOf(stream.getDuration()));

                if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                    properties.put("stream." + i + ".sampleRate",
                            String.valueOf(coder.getSampleRate()));
                    properties.put("stream." + i + ".channels",
                            String.valueOf(coder.getChannels()));
                    properties.put("stream." + i + ".format", coder.
                            getSampleFormat().name());
                } else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                    properties.put("stream." + i + ".width",
                            String.valueOf(coder.getWidth()));
                    properties.put("stream." + i + ".height",
                            String.valueOf(coder.getHeight()));
                    properties.put("stream." + i + ".format",
                            coder.getPixelType().name());
                    properties.put("stream." + i + ".frameRate",
                            String.valueOf(coder.getFrameRate().getDouble()));
                }
            }
        } catch (UnsatisfiedLinkError ex) {
            LOG.log(Level.SEVERE, "Could not extract meta data. {0}", ex.
                    getMessage());
        } catch (NoClassDefFoundError ex) {
            LOG.log(Level.SEVERE, "Could not extract meta data. {0}", ex.
                    getMessage());
        }

        return properties;
    }

    /** {@inheritDoc } */
    @Override
    public List<Concept> enrich(String story) throws EnrichException {

        // OpenCalais support max 100KB per call
        int chunkSize = 100000;
        Iterable<String> chunks = Splitter.fixedLength(chunkSize).split(story);

        List<Concept> concepts = new ArrayList<Concept>();
        for (Iterator<String> i = chunks.iterator(); i.hasNext();) {
            String chunk = i.next();
            concepts.addAll(enrichChunk(chunk));
        }

        Set<Concept> set = new HashSet<Concept>(concepts);
        concepts = new ArrayList<Concept>(set);

        return concepts;
    }

    private List<Concept> enrichChunk(String chunk) throws EnrichException {
        if (chunk.trim().isEmpty()) {
            return new ArrayList<Concept>();
        }

        List<Concept> concepts = new ArrayList<Concept>();

        PostMethod method = new PostMethod(OPEN_CALAIS_URL);
        method.setRequestHeader("x-calais-licenseID",
                cfgService.getString(ConfigurationKey.OPEN_CALAIS_API_KEY));
        method.setRequestHeader("Content-Type", "text/raw; charset=UTF-8");
        method.setRequestHeader("Accept", "application/json");
        method.setRequestHeader("enableMetadataType", "SocialTags");
        method.setRequestEntity(new StringRequestEntity(chunk));

        boolean fail = false;
        EnrichException exception = new EnrichException();

        try {
            HttpClient client = new HttpClient();
            int returnCode = client.executeMethod(method);
            if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                LOG.log(Level.WARNING, "The Post method is not implemented by this URI");
                // still consume the response body
                method.getResponseBodyAsString();
            } else if (returnCode == HttpStatus.SC_OK) {

                JSONObject response = JSONObject.fromObject(method.
                        getResponseBodyAsString());

                List<OpenCalaisMapping> mappings = metaDataFacade.
                        getOpenCalaisMappings();


                for (Object key : response.keySet()) {
                    String sKey = (String) key;

                    if (sKey.startsWith("http://d.opencalais.com/")) {
                        JSONObject entity = response.getJSONObject(sKey);

                        String typeGroup = (String) entity.get("_typeGroup");
                        String fieldValue = "";

                        // Mapping existing concepts
                        boolean mappingOccured = false;
                        for (OpenCalaisMapping mapping : mappings) {
                            try {
                                fieldValue = (String) entity.get(mapping.
                                        getField());
                                ;
                            } catch (Exception ex) {
                                fieldValue = "";
                            }

                            if (mapping.getTypeGroup().equals(typeGroup)
                                    && entity.containsKey(mapping.getField())
                                    && fieldValue.equals(mapping.getValue())) {
                                concepts.add(mapping.getConcept());
                                mappingOccured = true;
                            }
                        }

                        if (!mappingOccured) {

                            if (((String) entity.get("_typeGroup")).
                                    equalsIgnoreCase("entities")) {
                                String conceptType =
                                        (String) entity.get("_type");
                                String conceptName = (String) entity.get("name");
                                Concept match = null;
                                try {
                                    match = metaDataFacade.findConceptByName(
                                            conceptName);
                                } catch (DataNotFoundException dnfe) {
                                }


                                if (entity.containsKey("_type")) {
                                    if (conceptType.equalsIgnoreCase("company")
                                            || conceptType.equalsIgnoreCase(
                                            "organization")) {

                                        if (match == null
                                                || (!(match instanceof Organisation))) {
                                            match = new Organisation(conceptName,
                                                    "");
                                            match = metaDataFacade.create(match);
                                        }

                                        if (match instanceof Organisation) {
                                            concepts.add(match);
                                        }
                                    } else if (conceptType.equalsIgnoreCase(
                                            "person")) {
                                        if (match == null
                                                || (!(match instanceof Person))) {
                                            match = new Person(conceptName, "");
                                            match = metaDataFacade.create(match);
                                        }

                                        if (match instanceof Person) {
                                            concepts.add(match);
                                        }
                                    } else if (conceptType.equalsIgnoreCase(
                                            "city") || conceptType.
                                            equalsIgnoreCase("country")
                                            || conceptType.equalsIgnoreCase(
                                            "continent") || conceptType.
                                            equalsIgnoreCase("ProvinceOrState")
                                            || conceptType.equalsIgnoreCase(
                                            "region")) {
                                        if (match == null
                                                || (!(match instanceof GeoArea))) {
                                            match = new GeoArea(conceptName, "");
                                            match = metaDataFacade.create(match);
                                        }

                                        if (match instanceof GeoArea) {
                                            concepts.add(match);
                                        }
                                    } else if (conceptType.equalsIgnoreCase(
                                            "facility")) {
                                        if (match == null
                                                || (!(match instanceof PointOfInterest))) {
                                            match = new PointOfInterest(
                                                    conceptName, "");
                                            match = metaDataFacade.create(match);
                                        }

                                        if (match instanceof PointOfInterest) {
                                            concepts.add(match);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LOG.log(Level.WARNING,
                        "Invalid response received from OpenCalais [{0}] {1}",
                        new Object[]{returnCode,
                            method.getResponseBodyAsString()});
            }

        } catch (Exception e) {
            fail = true;
            exception = new EnrichException(e);
            LOG.log(Level.FINE, "", e);
        } finally {
            method.releaseConnection();
        }

        if (fail) {
            throw exception;
        }

        return concepts;
    }

    /** {@inheritDoc } */
    @Override
    public String extractContent(MediaItemRendition mir) {
        String contentType = mir.getContentType();
        String story = "";

        if (contentType == null) {
            LOG.log(Level.WARNING, "Content type is null");
            return story;
        }

        if (contentType.equals("application/pdf")) {
            // Extract text in PDF
            try {
                URL originalFile = new URL(mir.getAbsoluteFilename());
                PDDocument doc = null;
                try {
                    // Read PDF
                    PDFParser parser = new PDFParser(originalFile.openStream());
                    parser.parse();
                    COSDocument cosDoc = parser.getDocument();
                    PDDocument pdDoc = new PDDocument(cosDoc);

                    PDFTextStripper stripper = new PDFTextStripper();
                    story = stripper.getText(pdDoc);

                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } finally {
                    if (doc != null) {
                        try {
                            doc.close();
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                }


            } catch (MalformedURLException ex) {
            }
        } else if (contentType.equals("application/msword")
                || contentType.equals(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            try {
                URL originalFile = new URL(mir.getAbsoluteFilename());
                HWPFDocument doc = new HWPFDocument(originalFile.openStream());
                WordExtractor extractor = new WordExtractor(doc);
                story = extractor.getText();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return story;
    }
}
