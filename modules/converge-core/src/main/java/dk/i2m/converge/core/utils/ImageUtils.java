/*
 * Copyright 2010 Interactive Media Management
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
package dk.i2m.converge.core.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Allan Lykke Christensen
 */
public class ImageUtils {

    /**
     * Non-instantiable class.
     */
    private ImageUtils() {
    }

    /**
     * Generates a thumbnail of a given image.
     *
     * @param img
     *          Original image
     * @param width
     *          Thumbnail width
     * @param height
     *          Thumbnail height
     * @param quality
     *          Image quality
     * @return Thumbnail as byte array
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    public static byte[] generateThumbnail(byte[] img, int width, int height, int quality) throws InterruptedException, IOException {
        Image image = Toolkit.getDefaultToolkit().createImage(img);
        MediaTracker mediaTracker = new MediaTracker(new Container());
        mediaTracker.addImage(image, 0);
        mediaTracker.waitForID(0);

        // determine thumbnail size from WIDTH and HEIGHT
        double thumbRatio = (double) width / (double) height;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double) imageWidth / (double) imageHeight;

        if (thumbRatio < imageRatio) {
            height = (int) (width / imageRatio);
        } else {
            width = (int) (height * imageRatio);
        }

        // draw original image to thumbnail image object and
        // scale it to the new size on-the-fly
        BufferedImage thumbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, width, height, null);

        // save thumbnail image to OUTFILE
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbImage, "png", baos);

        return baos.toByteArray();
    }
    
    public static byte[] cropImage(byte[] img, int width, int height, int quality) throws InterruptedException, IOException {
        return null;
    }
}
