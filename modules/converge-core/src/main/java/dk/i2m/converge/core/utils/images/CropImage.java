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
package dk.i2m.converge.core.utils.images;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author Allan Lykke Christensen
 */
public class CropImage {

    private final BufferedImage originalImage;
    private BufferedImage output;
    private final int originalWidth;

    public CropImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
        this.originalWidth = this.originalImage.getWidth();
    }

    public BufferedImage crop(int targetWidth, int cropX, int cropY, int cropWidth, int cropHeight, int newWidth, int newHeight) {
        float increase = (float) this.originalWidth / (float) targetWidth;

        // Calculate the crop size based on the resizing (increase) of the image
        int calcCropX = (int) (cropX * (increase));
        int calcCropY = (int) (cropY * (increase));
        int calcCropWidth = (int) ((float) cropWidth * (increase));
        int calcCropHeight = (int) ((float) cropHeight * (increase));

        // Crop image
        BufferedImage croppedImage = this.originalImage.getSubimage(calcCropX, calcCropY, calcCropWidth, calcCropHeight);

        // Scale down/up image based on the new image size
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(croppedImage, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();
        return scaledImage;
    }

}
