/*
 * Copyright (C) 2011 - 2012 Interactive Media Management
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
package dk.i2m.converge.jsf.beans;

import dk.i2m.converge.core.DataNotFoundException;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.Rendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.ejb.facades.CatalogueFacadeLocal;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;

/**
 * Backing bean for {@code /CropRendition.jspx}.
 *
 * @author Allan Lykke Christensen
 */
public class CropRendition {

    @EJB private CatalogueFacadeLocal catalogueFacade;

    private MediaItem mediaItem;

    private Rendition srcRendition;

    private Rendition targetRendition;

    private int cropX;

    private int cropY;

    private int cropX2;

    private int cropY2;

    private int cropHeight;

    private int cropWidth;

    private int generateRenditionHeight = 100;

    private int generateRenditionWidth = 100;

    private int targetWidth = 0;

    private int targetHeight = 0;

    private MediaItemRendition sourceMediaItemRendition;

    public int getGenerateRenditionHeight() {
        return generateRenditionHeight;
    }

    public void setGenerateRenditionHeight(int generateRenditionHeight) {
        this.generateRenditionHeight = generateRenditionHeight;
    }

    public int getGenerateRenditionWidth() {
        return generateRenditionWidth;
    }

    public void setGenerateRenditionWidth(int generateRenditionWidth) {
        this.generateRenditionWidth = generateRenditionWidth;
    }

    public MediaItem getMediaItem() {
        return mediaItem;
    }

    public void setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
    }

    public Rendition getSrcRendition() {
        return srcRendition;
    }

    public void setSrcRendition(Rendition srcRendition) {
        this.srcRendition = srcRendition;
    }

    public Rendition getTargetRendition() {
        return targetRendition;
    }

    public void setTargetRendition(Rendition targetRendition) {
        this.targetRendition = targetRendition;
    }

    public void setMediaItemId(Long id) throws DataNotFoundException {
        mediaItem = catalogueFacade.findMediaItemById(id);
    }

    public void setSourceRenditionId(Long id) throws DataNotFoundException, RenditionNotFoundException {
        srcRendition = catalogueFacade.findRenditionById(id);
        sourceMediaItemRendition = mediaItem.findRendition(srcRendition);
    }

    public void setTargetRenditionId(Long id) throws DataNotFoundException {
        targetRendition = catalogueFacade.findRenditionById(id);

        if (targetRendition.getDefaultHeight() != null && targetRendition.getDefaultHeight().intValue() > 0) {
            setGenerateRenditionHeight(targetRendition.getDefaultHeight());
        }

        if (targetRendition.getDefaultWidth() != null && targetRendition.getDefaultWidth().intValue() > 0) {
            setGenerateRenditionWidth(targetRendition.getDefaultWidth());
        }
    }

    public MediaItemRendition getSourceMediaItemRendition() {
        return sourceMediaItemRendition;
    }

    public void setSourceMediaItemRendition(MediaItemRendition sourceMediaItemRendition) {
        this.sourceMediaItemRendition = sourceMediaItemRendition;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropX() {
        return cropX;
    }

    public void setCropX(int cropX) {
        this.cropX = cropX;
    }

    public int getCropX2() {
        return cropX2;
    }

    public void setCropX2(int cropX2) {
        this.cropX2 = cropX2;
    }

    public int getCropY() {
        return cropY;
    }

    public void setCropY(int cropY) {
        this.cropY = cropY;
    }

    public int getCropY2() {
        return cropY2;
    }

    public void setCropY2(int cropY2) {
        this.cropY2 = cropY2;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public void setTargetWidth(int targetWidth) {
        this.targetWidth = targetWidth;
    }

    /**
     * Event handler for cropping the image based on the current user selection.
     * 
     * @param event Event that invoked the handler
     */
    public void onCrop(ActionEvent event) {
        try {
            // Calculate the scale of the crop selection depending on the size of the image in the browser
            BufferedImage src = ImageIO.read(new URL(sourceMediaItemRendition.getAbsoluteFilename()));
            int originalW = src.getWidth(); //sourceMediaItemRendition.getWidth();
            float increase = (float) originalW / (float) getTargetWidth();
            int calcCropX = (int) (getCropX() * (increase));
            int calcCropY = (int) (getCropY() * (increase));
            int calcCropWidth = (int) ((float) getCropWidth() * (increase));
            int calcCropHeight = (int) ((float) getCropHeight() * (increase));

            // Crop image
            BufferedImage dest = src.getSubimage(calcCropX, calcCropY, calcCropWidth, calcCropHeight);

            // Scale down/up image based on the requested rendition size
            BufferedImage scaledImage = new BufferedImage(getGenerateRenditionWidth(), getGenerateRenditionHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(dest, 0, 0, getGenerateRenditionWidth(), getGenerateRenditionHeight(), null);
            graphics2D.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(scaledImage, "png", baos);

            File tempFile = File.createTempFile("000" + getMediaItem().getId(), "" + getTargetRendition().getId());
            FileUtils.writeByteArrayToFile(tempFile, baos.toByteArray());

            MediaItemRendition mir = catalogueFacade.create(tempFile, mediaItem, targetRendition, targetRendition.getId() + ".png", "image/png", false);
        } catch (IOException ex) {
            Logger.getLogger(CropRendition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
