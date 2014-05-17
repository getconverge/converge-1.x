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
package dk.i2m.converge.plugins.actions.videopreview;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.IContainer;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Extracts a still image from a video.
 *
 * @author Allan Lykke Christensen
 */
public class StillVideo {

    private int width = 200;

    private long grabAt = -1;

    private String inputFilename = "";

    private int mVideoStreamIndex = -1;

    private boolean gotStill = false;

    private byte[] stillShot = null;

    /**
     * Creates a new instance of {@link StillVideo} for grabbing a still image
     * half way through the video.
     *
     * @param width Size of the extracted still video
     * @param input File location of the video
     */
    public StillVideo(int width, String input) {
        this.width = width;
        this.inputFilename = input;
    }

    /**
     * Creates a new instance of {@link StillVideo} for grabbing a still image x
     * number of sections into the video.
     *
     * @param grabAt Seconds into the video to grab the image
     * @param width  Size of the extracted still video
     * @param input  File location of the video
     */
    public StillVideo(long grabAt, int width, String input) {
        this.grabAt = grabAt;
        this.width = width;
        this.inputFilename = input;
    }

    /**
     * Grab a still image from the video and store it in
     * {@link StillVideo#getStill()}.
     */
    public void grab() {
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        mediaReader.addListener(new ImageSnapListener());

        if (grabAt == -1) {
            // Get a screenshot half way through the video
            grabAt = (getVideoDuration() / 2);
        }

        try {
            while (mediaReader.readPacket() == null && !gotStill) ;
        } catch (Throwable t) {
            Logger.getLogger(StillVideo.class.getName()).log(Level.INFO, t.
                    getMessage());
        }
    }

    /**
     * Gets the binary content of the still image that was grabbed from the
     * video.
     *
     * @return Byte array containing the still image of the video
     */
    public byte[] getStill() {
        return this.stillShot;
    }

    /**
     * Determine if the {@link StillVideo#grab() } method was able to extract a
     * still image.
     *
     * @return {@code true} if a still image is available in
     *         {@link StillVideo#getStill()} otherwise {@code false}
     */
    public boolean isStillAvailable() {
        return gotStill;
    }

    /**
     * Gets the duration of the input video.
     *
     * @return Duration if the video in ms
     */
    private long getVideoDuration() {
        // Create a Xuggler container object 
        IContainer container = IContainer.make();
        // Open up the container 
        if (container.open(inputFilename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("Could not open file: "
                    + inputFilename);
        }
        return container.getDuration();
    }

    /**
     * MediaListener for extracting a still shot from a video stream.
     */
    class ImageSnapListener extends MediaListenerAdapter {

        /**
         * Event handler for dealing with each frame in the stream.
         *
         * @param event Event that occurred
         */
        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
            if (event.getStreamIndex() != mVideoStreamIndex) {
                if (mVideoStreamIndex == -1) {
                    mVideoStreamIndex = event.getStreamIndex();
                } else {
                    return;
                }
            }

            if (event.getTimeStamp() != 0 && event.getTimeStamp() >= grabAt) {
                dumpImage(event.getImage());
                gotStill = true;
            }
        }

        /**
         * Extract and store the {@link BufferedImage} in memory for retrieval
         * through {@link StillVideo#stillShot}.
         *
         * @param image {@link BufferedImage} to extract
         */
        private void dumpImage(BufferedImage image) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                
                int origWidth = image.getWidth();

                if (origWidth > width && width != 0) {

                    double ratio = origWidth / (double) width;
                    double height = (double) image.getHeight() / ratio;

                    BufferedImage scaledImage = new BufferedImage((int) width,
                            (int) height, BufferedImage.TYPE_INT_ARGB);

                    Graphics2D graphics2D = scaledImage.createGraphics();
                    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    graphics2D.drawImage(image, 0, 0, (int) width, (int) height,
                            null);
                    ImageIO.write(scaledImage, "png", baos);
                    graphics2D.dispose();

                } else {
                    ImageIO.write(image, "png", baos);
                }
                stillShot = baos.toByteArray();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}