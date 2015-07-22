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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link CompressImage}.
 *
 * @author Allan Lykke Christensen
 */
public class CompressImageTest {

    /**
     * Smoke test that compresses a high resolution image and places it in the
     * {@code target/} directory for manual inspection.
     */
    @Test
    public void compressImage_inputHighResolutionJpg_output05CompressedJpg() {
        try {
            // Arrange
            InputStream is = getClass().getResourceAsStream("/dk/i2m/converge/utils/images/highresolution-image.jpg");
            BufferedImage src = ImageIO.read(is);

            // Act
            CompressImage compressImage = new CompressImage(src);
            compressImage.compress(0.5f, "jpeg");
            byte[] compressed = compressImage.getCompressedImage();
            FileUtils.writeByteArrayToFile(new File("target/compressImage_inputHighResolutionJpg_output05CompressedJpg.jpg"), compressed);

            // Assert
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Smoke test that compresses a high resolution image and places it in the
     * {@code target/} directory for manual inspection.
     */
    @Test
    public void compressImage_inputHighResolutionJpg_output01CompressedJpg() {
        try {
            // Arrange
            InputStream is = getClass().getResourceAsStream("/dk/i2m/converge/utils/images/highresolution-image.jpg");
            BufferedImage src = ImageIO.read(is);

            // Act
            CompressImage compressImage = new CompressImage(src);
            compressImage.compress(0.1f, "jpeg");
            byte[] compressed = compressImage.getCompressedImage();
            FileUtils.writeByteArrayToFile(new File("target/compressImage_inputHighResolutionJpg_output01CompressedJpg.jpg"), compressed);

            // Assert
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

}
