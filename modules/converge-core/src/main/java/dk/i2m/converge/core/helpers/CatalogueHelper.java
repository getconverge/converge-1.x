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
package dk.i2m.converge.core.helpers;

/**
 * Helper for interacting with a catalogue.
 *
 * @author Allan Lykke Christensen
 */
public class CatalogueHelper {

//    private static CatalogueHelper instance = null;
//
//    
//    private String generateUniqueFilename(MediaItem item) {
//        StringBuilder filename = new StringBuilder();
//        filename.append(item.getId()).append("-").append(item.getFilename());
//        return filename.toString();
//    }
//
//
//    /**
//     * Stores a file in {@link MediaItem}. After the file has been stored
//     * a thumbnail is generated for the file depending on the type of file.
//     * 
//     * @param file
//     *          Byte array containing the file to store in the {@link MediaItem}
//     * @param item
//     *          {@link MediaItem} to store the <code>file</code>
//     * @return {@link Map} of meta data properties found in the <code>file</code>
//     */
//    @SuppressWarnings("unchecked")
//    public Map<String, String> store(File file, MediaItem item) {
//
//        String catalogueLocation = item.getCatalogue().getLocation();
//        String fileName = generateUniqueFilename(item);
//        item.setFilename(fileName);
//
//        // Get the repository location
//        File dir = new File(catalogueLocation);
//
//        // Check if it exist
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        // Determine the location and name of the file being uploaded
//        File mediaFile = new File(dir, fileName);
//
//        // Move file to the new location
//        LOG.log(Level.INFO, "Moving file to {0}", mediaFile.getAbsolutePath());
//        try {
//            copyFile(file, mediaFile);
//        } catch (IOException ex) {
//            Logger.getLogger(CatalogueHelper.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        // Generate thumbnail
//        byte[] originalContent = null;
//        try {
//            originalContent = FileUtils.getBytes(mediaFile);
//
//            byte[] thumb = MediaItemThumbnailGenerator.getInstance().generateThumbnail(originalContent, item);
//            FileUtils.writeToFile(thumb, catalogueLocation + File.separator + item.getId() + "-thumb.jpg");
//        } catch (UnknownMediaItemException ex) {
//            LOG.log(Level.INFO, ex.getMessage());
//            return Collections.EMPTY_MAP;
//        } catch (ThumbnailGeneratorException ex) {
//            LOG.log(Level.INFO, ex.getMessage());
//            return Collections.EMPTY_MAP;
//        } catch (IOException ex) {
//            LOG.log(Level.INFO, ex.getMessage());
//            return Collections.EMPTY_MAP;
//        }
//
//        // Retrieve metadata
//        if (item.getFilename().trim().toLowerCase().endsWith("mp3")) {
//            try {
//                return retrieveMp3MetaData(item.getFileLocation());
//            } catch (CannotIndexException ex) {
//                LOG.log(Level.WARNING, "Could not retrieve metadata from {0}", item.getFilename());
//                return Collections.EMPTY_MAP;
//            }
//        }
//
//        try {
//            return indexXmp(originalContent);
//        } catch (CannotIndexException ex) {
//            LOG.log(Level.WARNING, "Could not index media file " + item.getFilename(), ex);
//            return Collections.EMPTY_MAP;
//        }
//    }
//
//    
//
//    public static CatalogueHelper getInstance() {
//        if (instance == null) {
//            instance = new CatalogueHelper();
//        }
//        return instance;
//    }
//
//    private CatalogueHelper() {
//    }
}
