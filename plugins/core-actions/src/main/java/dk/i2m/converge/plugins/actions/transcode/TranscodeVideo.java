/*
 * Copyright (C) 2012 Interactive Media Management
 * Copyright (C) 2014 Allan Lykke Christensen
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
package dk.i2m.converge.plugins.actions.transcode;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.*;
import com.xuggle.xuggler.*;
import java.awt.image.BufferedImage;

/**
 * Transcodes a video from one format to another.
 *
 * @author Allan Lykke Christensen
 */
public class TranscodeVideo {

    private final String input;
    private final String output;
    private int width;
    private int height;
    private IMediaWriter writer;

    public TranscodeVideo(String input, String output) {
        this.input = input;
        this.output = output;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void encode() {
        IMediaReader mediaReader = ToolFactory.makeReader(input);
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        mediaReader.addListener(new Transcoder());

        writer = ToolFactory.makeWriter(output, mediaReader);
        mediaReader.addListener(writer);

        while (mediaReader.readPacket() == null) {
            // read through input and process using the MediaWriter listener
        }
    }

    class Transcoder extends MediaToolAdapter {

        private IVideoResampler videoResampler = null;
        private IAudioResampler audioResampler = null;

        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
            IVideoPicture pic = event.getPicture();
            if (videoResampler == null) {
                videoResampler = IVideoResampler.make(getWidth(), getHeight(), pic.getPixelType(), pic.getWidth(), pic.getHeight(), pic.getPixelType());
            }
            IVideoPicture out = IVideoPicture.make(pic.getPixelType(), getWidth(), getHeight());
            videoResampler.resample(out, pic);

            IVideoPictureEvent asc = new VideoPictureEvent(event.getSource(), out, event.getStreamIndex());
            super.onVideoPicture(asc);
            out.delete();
        }

        @Override
        public void onAudioSamples(IAudioSamplesEvent event) {
            IAudioSamples samples = event.getAudioSamples();
            if (audioResampler == null) {
                audioResampler = IAudioResampler.make(2, samples.getChannels(), 44100, samples.getSampleRate());
            }
            if (event.getAudioSamples().getNumSamples() > 0) {
                IAudioSamples out = IAudioSamples.make(samples.getNumSamples(), samples.getChannels());
                audioResampler.resample(out, samples, samples.getNumSamples());

                AudioSamplesEvent asc = new AudioSamplesEvent(event.getSource(), out, event.getStreamIndex());
                super.onAudioSamples(asc);
                out.delete();
            }
        }
    }
}
