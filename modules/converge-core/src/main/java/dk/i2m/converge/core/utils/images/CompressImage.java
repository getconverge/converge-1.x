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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * Compresses a {@link BufferedImage} and makes it available as a byte array.
 *
 * @author Allan Lykke Christensen
 */
public class CompressImage {

    private final BufferedImage originalImage;
    private byte[] compressedImage;

    /**
     * Creates a new compressed image.
     *
     * @param originalImage Original image to base the compressed image
     */
    public CompressImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    /**
     * Invokes the compression algorithm on the original image.
     *
     * @param quality The quality of the image ({@code 0.1f} to {@code 1.0})
     * @param format Name of the format of the output image (E.g. {@code jpeg})
     * @throws IOException If the file could not be generated
     */
    public void compress(float quality, String format) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(format);
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writers found");
        }

        ImageWriter writer = (ImageWriter) writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        // The output will be a ByteArrayOutputStream (in memory)
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32768);
        ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(this.originalImage, null, null), param);
        // otherwise the buffer size will be zero!
        ios.flush();
        
        this.compressedImage = bos.toByteArray();
    }

    /**
     * Get the compressed image. This method will return an empty byte array if
     * the {@link #compress(float, java.lang.String) } method has not been
     * invoked.
     *
     * @return Byte array containing the compressed image
     */
    public byte[] getCompressedImage() {
        return this.compressedImage;
    }

}
