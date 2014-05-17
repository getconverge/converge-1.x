/*
 * Copyright (C) 2010 - 2011 Interactive Media Manangement
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.decoders.dailymail;

import dk.i2m.converge.core.content.ContentTag;
import dk.i2m.converge.core.newswire.NewswireItem;
import dk.i2m.converge.core.newswire.NewswireService;
import dk.i2m.converge.core.plugin.NewswireDecoder;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.search.SearchEngineIndexingException;
import dk.i2m.converge.core.utils.FileUtils;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.mail.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.WordUtils;

/**
 * Decoder for the Daily Mail newswire. Daily Mail is delivered via e-mail in a ZIP file containing structured plain text.
 *
 * @author Allan Lykke Christensen
 */
@dk.i2m.converge.core.annotations.NewswireDecoder
public class DailyMailDecoder implements NewswireDecoder {

    /**
     * IMAP transport. Possible value for {@link DailyMailDecoder#TRANSPORT}.
     */
    public static final String TRANSPORT_IMAP = "imap";

    /** IMAPS transport (secure). Possible value for {@link DailyMailDecoder#TRANSPORT}. */
    public static final String TRANSPORT_IMAPS = "imaps";

    /** How is the newswire fetched. For now only IMAP is supported. */
    public static final String TRANSPORT = "TRANSPORT";

    /** IMAP server name or IP address. */
    public static final String TRANSPORT_IMAP_SERVER = "TRANSPORT.IMAP.SERVER";

    /** IMAP server port. */
    public static final String TRANSPORT_IMAP_PORT = "TRANSPORT.IMAP.PORT";

    /** IMAP user name. */
    public static final String TRANSPORT_IMAP_USERNAME = "TRANSPORT.IMAP.USERNAME";

    /** IMAP password. */
    public static final String TRANSPORT_IMAP_PASSWORD = "TRANSPORT.IMAP.PASSWORD";

    /** IMAP folder containing the newswire mails. */
    public static final String TRANSPORT_IMAP_FOLDER_NEWSWIRE = "TRANSPORT.IMAP.FOLDER.NEWSWIRE";

    /** IMAP folder to move the newswire once processed. */
    public static final String TRANSPORT_IMAP_FOLDER_PROCESSED = "TRANSPORT.IMAP.FOLDER.PROCESSED";

    /** Should IMAP mails be deleted after being processed or moved to the processed directory. */
    public static final String TRANSPORT_IMAP_DELETE_PROCESSED = "TRANSPORT.IMAP.DELETE_PROCESSED";

    /** Should IMAP mails be deleted after being processed or moved to the processed directory. */
    public static final String CHARSET = "Charset";

    private static final String SCHEDULE_FILE_PREFIX = "schedule/dm";

    private static final Logger LOG = Logger.getLogger(DailyMailDecoder.class.getName());

    private ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.decoders.dailymail.Messages");

    private Map<String, String> availableProperties = null;

    private Map<String, String> properties = null;

    private Calendar releaseDate = new GregorianCalendar(2011, Calendar.APRIL, 27, 7, 00);

    private PluginContext pluginContext;

    private String charset = "UTF-8";

    /**
     * Creates a new instance of {@link DailyMailDecoder}.
     */
    public DailyMailDecoder() {
    }

    @Override
    public String getName() {
        return bundle.getString("PLUGIN_NAME");
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
        return releaseDate.getTime();
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();
            availableProperties.put(bundle.getString(TRANSPORT), TRANSPORT);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_SERVER), TRANSPORT_IMAP_SERVER);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_PORT), TRANSPORT_IMAP_PORT);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_USERNAME), TRANSPORT_IMAP_USERNAME);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_PASSWORD), TRANSPORT_IMAP_PASSWORD);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_FOLDER_NEWSWIRE), TRANSPORT_IMAP_FOLDER_NEWSWIRE);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_FOLDER_PROCESSED), TRANSPORT_IMAP_FOLDER_PROCESSED);
            availableProperties.put(bundle.getString(TRANSPORT_IMAP_DELETE_PROCESSED), TRANSPORT_IMAP_DELETE_PROCESSED);
            availableProperties.put(bundle.getString(CHARSET), CHARSET);
        }
        return this.availableProperties;
    }

    @Override
    public void decode(PluginContext ctx, NewswireService newswire) {
        LOG.log(Level.INFO, "Processing newswire service {0}", newswire.getSource());
        Map<String, Calendar> filesToProcess = new HashMap<String, Calendar>();
        pluginContext = ctx;

        StringBuilder workingDirectory = new StringBuilder(ctx.getWorkingDirectory());
        workingDirectory.append(File.separator);
        workingDirectory.append("newswires");
        workingDirectory.append(File.separator);
        workingDirectory.append(newswire.getId());
        workingDirectory.append(File.separator);

        //List<NewswireItem> result = new ArrayList<NewswireItem>();
        this.properties = newswire.getPropertiesMap();

        if (this.properties.containsKey(CHARSET)) {
            this.charset = this.properties.get(CHARSET);
        }

        if (properties.containsKey(TRANSPORT)) {
            String transport = properties.get(TRANSPORT);

            if (transport.equalsIgnoreCase(TRANSPORT_IMAP) || transport.equalsIgnoreCase(TRANSPORT_IMAPS)) {
                String host = properties.get(TRANSPORT_IMAP_SERVER);
                String port = properties.get(TRANSPORT_IMAP_PORT);
                String username = properties.get(TRANSPORT_IMAP_USERNAME);
                String password = properties.get(TRANSPORT_IMAP_PASSWORD);
                String folder_newswire = properties.get(
                        TRANSPORT_IMAP_FOLDER_NEWSWIRE);
                String folder_processed = properties.get(
                        TRANSPORT_IMAP_FOLDER_PROCESSED);

                boolean deleteAfterProcess = Boolean.parseBoolean(properties.get(TRANSPORT_IMAP_DELETE_PROCESSED));

                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);

                javax.mail.Store store;
                try {
                    store = session.getStore(transport);
                    store.connect(host, Integer.valueOf(port), username,
                            password);

                    Folder folder = store.getFolder(folder_newswire);
                    folder.open(Folder.READ_WRITE);


                    for (Message msg : folder.getMessages()) {
                        Calendar msgSent = Calendar.getInstance();
                        if (msg.getSentDate() != null) {
                            msgSent.setTime(msg.getSentDate());
                            LOG.log(Level.FINEST,
                                    "Sent date was not set on the mail");
                        } else if (msg.getReceivedDate() != null) {
                            msgSent.setTime(msg.getReceivedDate());
                            LOG.log(Level.FINEST,
                                    "Received date was not set on the mail");
                        } else {
                            LOG.log(Level.FINEST,
                                    "Using current timestamp as newswire item date");
                        }

                        Address[] recipients = msg.getFrom();
                        StringBuilder fromString = new StringBuilder();
                        if (recipients != null) {
                            for (Address from : recipients) {
                                fromString.append(from.toString());
                                fromString.append(" ");
                            }
                        }

                        LOG.log(Level.FINE,
                                "Processing mail from {0} with subject ''{1}''",
                                new Object[]{fromString.toString(), msg.getSubject()});

                        if (msg.getContent() instanceof Multipart) {
                            Multipart multipart = (Multipart) msg.getContent();
                            for (String attachment : processMultipart(
                                    multipart, workingDirectory.toString())) {
                                filesToProcess.put(attachment, msgSent);
                            }
                        } else {
                            LOG.log(Level.FINE,
                                    "Mail does not contain attachments. Skipping");
                        }

                        // Don't delete or move if nothing was found in the mail, hence the format probably wasn't recognised
                        if (!filesToProcess.isEmpty()) {
                            if (!deleteAfterProcess) {
                                LOG.log(Level.FINE, "Copying mail to {0}", folder_processed);
                                Folder processedFolder = store.getFolder(folder_processed);
                                processedFolder.open(Folder.READ_WRITE);
                                processedFolder.appendMessages(new Message[]{msg});
                                processedFolder.close(true);
                            }
                            LOG.log(Level.FINE, "Deleting original mail");
                            msg.setFlag(Flags.Flag.DELETED, true);
                        } else {
                            LOG.log(Level.FINE, "No Daily Mail newswire items found in mail. Mail left in folder");
                        }
                    }
                    folder.close(true);
                    store.close();
                } catch (NoSuchProviderException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (MessagingException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }

                LOG.log(Level.FINE, "Starting to process attachments {0}", filesToProcess.size());

                for (String file : filesToProcess.keySet()) {
                    LOG.log(Level.FINE, "Processing {0}", file);
                    Map<String, NewswireItem> newswireItems = decodeZip(file);

                    for (NewswireItem item : newswireItems.values()) {
                        item.setNewswireService(newswire);
                        if (item.getDate() == null) {
                            item.setDate(filesToProcess.get(file));
                        }
                        ctx.createNewswireItem(item);
                        try {
                            ctx.index(item);
                        } catch (SearchEngineIndexingException seie) {
                            LOG.log(Level.SEVERE, seie.getMessage());
                            LOG.log(Level.FINEST, "", seie);
                        }

                    }
                }
                LOG.log(Level.FINE, "Attachment processing ended");
            } else {
                LOG.log(Level.SEVERE, "Unknown transport ''{0}''", transport);
            }
        } else {
            LOG.log(Level.SEVERE, "Property '" + TRANSPORT + "' is missing");
        }
        //return result;
    }

    /**
     * Detects a Daily Mail schedule file in a {@link ZipFile}.
     * 
     * @param zf
     *          {@link ZipFile} containing the schedule file
     * @return Name of the {@link ZipEntry} containing the Daily Mail schedule
     * @throws ScheduleNotFoundException
     *          If no schedule file could be detected
     */
    private String detectSchedule(ZipFile zf) throws ScheduleNotFoundException {
        Enumeration<? extends ZipEntry> files = zf.entries();
        while (files.hasMoreElements()) {
            ZipEntry ze = files.nextElement();
            if (ze.getName().toLowerCase().startsWith(SCHEDULE_FILE_PREFIX)) {
                return ze.getName();
            }
        }

        throw new ScheduleNotFoundException("Schedule could not be found in ZipFile '"
                + zf.getName());
    }

    /**
     * Decodes the newswire embodied in a zip file.
     *
     * @param file
     *          Full path and filename of the zip file containing the newswire
     * @return {@link List} of decoded newswire items.
     */
    private Map<String, NewswireItem> decodeZip(String file) {
        Map<String, NewswireItem> foundItems =
                new HashMap<String, NewswireItem>();
        if (file.endsWith(".zip")) {
            ZipFile zf;
            try {
                zf = new ZipFile(file);

                try {
                    String scheduleFile = detectSchedule(zf);
                    readSchedule(foundItems, zf, zf.getEntry(scheduleFile));
                    readStories(foundItems, zf);
                } catch (ScheduleProcessingException ex) {
                    LOG.log(Level.INFO, ex.getMessage());
                }

            } catch (IOException ex) {
                Logger.getLogger(DailyMailDecoder.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        } else {
            LOG.log(Level.FINE, "{0} in not a zip file", file);
        }

        return foundItems;
    }

    private List<String> processMultipart(Multipart multipart,
            String workingDirectory) {
        List<String> filesToProcess = new ArrayList<String>();
        try {
            LOG.log(Level.FINE, "Processing {0} with {1} parts",
                    new Object[]{multipart.getContentType(), multipart.getCount()});
            for (int i = 0, n = multipart.getCount(); i < n; i++) {
                Part part = multipart.getBodyPart(i);

                if (part.getContent() instanceof Multipart) {
                    LOG.log(Level.FINE, "Part is multipart");
                    filesToProcess.addAll(processMultipart((Multipart) part.getContent(), workingDirectory));
                }
                String disposition = part.getDisposition();
                LOG.log(Level.FINE, "Disposition {0} Content Type {1}",
                        new Object[]{disposition, part.getContentType()});

                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)
                        || (disposition.equals(Part.INLINE))))) {
                    LOG.log(Level.FINE, "Contained attachment: {0}", part.getFileName());
                    filesToProcess.add(saveFile(workingDirectory, part.getFileName(), part.getInputStream()));
                }
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return filesToProcess;
    }

    private static String saveFile(String path, String filename,
            InputStream input) throws IOException {
        if (filename == null) {
            filename = File.createTempFile("" + Calendar.getInstance().
                    getTimeInMillis(), ".out").getName();
        } // Do no overwrite existing file
        File folder = new File(path);
        if (!folder.exists()) {
            LOG.log(Level.INFO, "Temporary directory {0} does not exist", path);
            if (folder.mkdirs()) {
                LOG.log(Level.INFO, "Temporary directory created");
            } else {
                throw new IOException(
                        "Temporary directory COULD NOT be created");
            }
        }

        File file = new File(path, filename);

        for (int i = 0; file.exists(); i++) {
            file = new File(path, i + filename);
        }
        LOG.log(Level.FINE, "Saving {0}", file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        BufferedInputStream bis = new BufferedInputStream(input);
        int aByte;
        while ((aByte = bis.read()) != -1) {
            bos.write(aByte);
        }
        bos.flush();
        bos.close();
        bis.close();
        return file.getAbsoluteFile().toString();
    }

    /**
     * Reads the schedule file and returns a {@link Map} of discovered
     * {@link NewswireItem}s with their title and externalId set.
     *
     * @param file
     *          {@link ZipFile} containing the schedule file
     * @param entry
     *          {@link ZipEntry} containing the schedule file
     * @param scheduleContent
     *          {@link Map} of discovered {@link NewswireItem}s
     * @throws ScheduleProcessingException
     *          If the schedule file could not be read and processed correctly
     */
    private void readSchedule(Map<String, NewswireItem> scheduleContent,
            ZipFile file, ZipEntry entry) throws ScheduleProcessingException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(file.getInputStream(
                    entry), charset));

            String line = null;

            ScheduleRead readStatus = ScheduleRead.START;
            String itemId = "";
            while ((line = br.readLine()) != null) {
                switch (readStatus) {
                    case ITEM_FOUND:
                        readStatus = ScheduleRead.ITEM_TITLE_FOUND;
                        scheduleContent.get(itemId).setTitle(line.trim());
                        break;
                    case ITEM_TITLE_FOUND:
                        if (line.trim().isEmpty()) {
                            readStatus = ScheduleRead.START;
                        } else {
                            scheduleContent.get(itemId).addSummary(StringEscapeUtils.escapeXml(line.trim()));
                            scheduleContent.get(itemId).addSummary(" ");
                        }
                        break;
                    default:
                        if (line.trim().endsWith(".txt")) {
                            readStatus = ScheduleRead.ITEM_FOUND;
                            NewswireItem item = new NewswireItem();
                            if (entry.getTime() != -1) {
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(entry.getTime());
                                item.setDate(c);
                            }
                            item.setExternalId(line.trim());
                            scheduleContent.put(line.trim(), item);
                            itemId = line.trim();
                        }
                }

            }
            br.close();
        } catch (IOException ex) {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioex) {
                }
            }
            throw new ScheduleProcessingException(ex);
        }

        for (String scheduleFile : scheduleContent.keySet()) {
            LOG.log(Level.FINE, "Schedule contains: {0}", scheduleFile);
        }
    }

    private void readStories(Map<String, NewswireItem> foundItems, ZipFile zf) {
        Enumeration<? extends ZipEntry> files = zf.entries();
        while (files.hasMoreElements()) {
            ZipEntry ze = files.nextElement();

            if (foundItems.containsKey(FileUtils.getFilename(ze.getName()))) {
                NewswireItem item = foundItems.get(FileUtils.getFilename(ze.getName()));
                String section = FileUtils.getFolder(ze.getName());
                item.setTitle(WordUtils.capitalizeFully(item.getTitle().
                        toLowerCase()));
                ContentTag tag = pluginContext.findOrCreateContentTag(section);
                item.getTags().add(tag);

                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze), charset));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        item.addContent(StringEscapeUtils.escapeXml(line));
                        item.addContent("<br/>");
                    }
                    br.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        br.close();
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                LOG.log(Level.WARNING,
                        "Ignoring {0} as it was not detected in the schedule",
                        ze.getName());
            }
        }
    }

    enum ScheduleRead {

        START, ITEM_FOUND, ITEM_TITLE_FOUND;
    }
}
