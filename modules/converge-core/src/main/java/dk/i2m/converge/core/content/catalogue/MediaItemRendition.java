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
package dk.i2m.converge.core.content.catalogue;

import java.io.File;
import java.io.Serializable;
import javax.persistence.*;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Persisted model representing a {@link Rendition} of a
 * {@link MediaItem}.
 *
 * @author Allan Lykke Christensen
 */
@Entity
@Table(name = "media_item_rendition")
public class MediaItemRendition implements Serializable {

    private static final long serialVersionUID = 2L;

    /** Unique identifier of the specific rendition. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Specific rendition of content this component represents. */
    @ManyToOne
    @JoinColumn(name = "rendition_id")
    private Rendition rendition;

    /** Locator of the target resource. */
    @Column(name = "filename") @Lob
    private String filename;

    /** Locator of the target resource. */
    @Column(name = "path") @Lob
    private String path;

    /** IANA (Internet Assigned Numbers Authority) MIME type of the target resource. */
    @Column(name = "content_type")
    private String contentType;

    /** Size in bytes of the target resource. */
    @Column(name = "file_size")
    private Long size;

    /** Width of an image in pixels. */
    @Column(name = "width")
    private Integer width;

    /** Height of an image in pixels. */
    @Column(name = "height")
    private Integer height;

    /** Colour space of an image. */
    @Column(name = "colourSpace")
    private String colourSpace;

    /** Recommended printing resolution for an image in dots per inch. */
    @Column(name = "resolution")
    private Integer resolution;

    /** Audio bit rate in Kbps. */
    @Column(name = "audio_bitrate")
    private Integer audioBitrate;

    /** Audio sound system. */
    @Column(name = "audio_channels")
    private String audioChannels;

    /** Applicable codec for audio data. */
    @Column(name = "audio_codec")
    private String audioCodec;

    /** Number of audio samples per second, expressed as a sampling frequency in Hz. */
    @Column(name = "audio_sample_rate")
    private Integer audioSampleRate;

    /** Number of bits per audio sample. */
    @Column(name = "audio_sample_size")
    private Integer audioSampleSize;

    /** Indication that the audio data is encoded with a variable bit rate. */
    @Column(name = "audio_variable_bitrate")
    private boolean audioVariableBitrate;

    /** Clip duration in seconds. */
    @Column(name = "duration")
    private Integer duration;

    @Column(name = "video_codec")
    private String videoCodec;

    @Column(name = "video_average_bit_rate")
    private Integer videoAverageBitRate;

    @Column(name = "video_variable_bit_rate")
    private boolean videoVariableBitRate;

    @Column(name = "video_frame_rate")
    private Integer videoFrameRate;

    @Column(name = "video_scan_technique")
    private String videoScanTechnique;

    @Column(name = "video_aspect_ratio")
    private String videoAspectRatio;

    @Column(name = "video_sampling_method")
    private String videoSamplingMethod;

    /** Reference to {@link MediaItem}. */
    @ManyToOne
    @JoinColumn(name = "media_item_id")
    private MediaItem mediaItem;

    /**
     * Gets the unique identifier of the {@link MediaItemRendition}.
     * <p/>
     * @return Unique identifier of the {@link MediaItemRendition}
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the {@link MediaItemRendition}.
     * <p/>
     * @param id * Unique identifier of the {@link MediaItemRendition}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the rendition
     */
    public Rendition getRendition() {
        return rendition;
    }

    /**
     * @param rendition the rendition to set
     */
    public void setRendition(Rendition rendition) {
        this.rendition = rendition;
    }

    /**
     * The locator of the target resource.
     * <p/>
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * The locator of the target resource.
     * <p/>
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the path on the catalogue where the
     * file can be found.
     * <p/>
     * @return Path on the catalogue where the file
     * can be found
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path on the catalogue where the
     * file can be found.
     * <p/>
     * @param path * Path on the catalogue where the file
     * can be found
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * The IANA (Internet Assigned Numbers Authority) MIME type of the target resource.
     * <p/>
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * The IANA (Internet Assigned Numbers Authority) MIME type of the target resource.
     * <p/>
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * The width of an image in pixels.
     * <p/>
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * The width of an image in pixels.
     * <p/>
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * The height of an image in pixels.
     * <p/>
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * The height of an image in pixels.
     * <p/>
     * @param height the height to set
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * The colour space of an image.
     * <p/>
     * @return the colourSpace
     */
    public String getColourSpace() {
        return colourSpace;
    }

    /**
     * The colour space of an image.
     * <p/>
     * @param colourSpace the colourSpace to set
     */
    public void setColourSpace(String colourSpace) {
        this.colourSpace = colourSpace;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    /**
     * The audio bit rate in Kbps.
     * <p/>
     * @return the audioBitrate
     */
    public Integer getAudioBitrate() {
        return audioBitrate;
    }

    /**
     * The audio bit rate in Kbps.
     * <p/>
     * @param audioBitrate the audioBitrate to set
     */
    public void setAudioBitrate(Integer audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    /**
     * The audio sound system.
     * <p/>
     * @return the audioChannels
     */
    public String getAudioChannels() {
        return audioChannels;
    }

    /**
     * The audio sound system.
     * <p/>
     * @param audioChannels the audioChannels to set
     */
    public void setAudioChannels(String audioChannels) {
        this.audioChannels = audioChannels;
    }

    /**
     * The applicable codec for audio data.
     * <p/>
     * @return the audioCodec
     */
    public String getAudioCodec() {
        return audioCodec;
    }

    /**
     * The applicable codec for audio data.
     * <p/>
     * @param audioCodec the audioCodec to set
     */
    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }

    /**
     * The number of audio samples per second, expressed as a sampling frequency in Hz.
     * <p/>
     * @return the audioSampleRate
     */
    public Integer getAudioSampleRate() {
        return audioSampleRate;
    }

    /**
     * The number of audio samples per second, expressed as a sampling frequency in Hz.
     * <p/>
     * @param audioSampleRate the audioSampleRate to set
     */
    public void setAudioSampleRate(Integer audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    /**
     * The number of bits per audio sample.
     * <p/>
     * @return the audioSampleSize
     */
    public Integer getAudioSampleSize() {
        return audioSampleSize;
    }

    /**
     * The number of bits per audio sample.
     * <p/>
     * @param audioSampleSize the audioSampleSize to set
     */
    public void setAudioSampleSize(Integer audioSampleSize) {
        this.audioSampleSize = audioSampleSize;
    }

    /**
     * An indication that the audio data is encoded with a variable bit rate.
     * <p/>
     * @return the audioVariableBitrate
     */
    public boolean isAudioVariableBitrate() {
        return audioVariableBitrate;
    }

    /**
     * An indication that the audio data is encoded with a variable bit rate.
     * <p/>
     * @param audioVariableBitrate the audioVariableBitrate to set
     */
    public void setAudioVariableBitrate(boolean audioVariableBitrate) {
        this.audioVariableBitrate = audioVariableBitrate;
    }

    /**
     * The clip duration in seconds.
     * <p/>
     * @return the duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * The clip duration in seconds.
     * <p/>
     * @param duration the duration to set
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getVideoAspectRatio() {
        return videoAspectRatio;
    }

    public void setVideoAspectRatio(String videoAspectRatio) {
        this.videoAspectRatio = videoAspectRatio;
    }

    public Integer getVideoAverageBitRate() {
        return videoAverageBitRate;
    }

    public void setVideoAverageBitRate(Integer videoAverageBitRate) {
        this.videoAverageBitRate = videoAverageBitRate;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public Integer getVideoFrameRate() {
        return videoFrameRate;
    }

    public void setVideoFrameRate(Integer videoFrameRate) {
        this.videoFrameRate = videoFrameRate;
    }

    public String getVideoSamplingMethod() {
        return videoSamplingMethod;
    }

    public void setVideoSamplingMethod(String videoSamplingMethod) {
        this.videoSamplingMethod = videoSamplingMethod;
    }

    public String getVideoScanTechnique() {
        return videoScanTechnique;
    }

    public void setVideoScanTechnique(String videoScanTechnique) {
        this.videoScanTechnique = videoScanTechnique;
    }

    public boolean isVideoVariableBitRate() {
        return videoVariableBitRate;
    }

    public void setVideoVariableBitRate(boolean videoVariableBitRate) {
        this.videoVariableBitRate = videoVariableBitRate;
    }

    public MediaItem getMediaItem() {
        return mediaItem;
    }

    public void setMediaItem(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
    }

    /**
     * Gets the absolute URL of the {@link MediaItem}. This is
     * dynamically calculated based on the {@link MediaRepository} associated
     * with the {@link MediaItem}.
     *
     * @return Absolute URL of the {@link MediaItem}
     */
    public String getAbsoluteFilename() {
        if (mediaItem == null || mediaItem.getCatalogue() == null
                || getFilename() == null) {
            return "#";
        }

        StringBuilder absoluteFilename = new StringBuilder(mediaItem.
                getCatalogue().getWebAccess());
        absoluteFilename.append("/");
        if (!StringUtils.isBlank(getPath())) {
            absoluteFilename.append(getPath().replaceAll(File.separator, "/"));
            absoluteFilename.append("/");
        }

        if (!StringUtils.isBlank(getFilename())) {
            try {
                absoluteFilename.append(URIUtil.encodePath(getFilename(),
                        "UTF-8"));
            } catch (URIException ex) {
                absoluteFilename.append(getFilename());
            }
        }

        return absoluteFilename.toString();
    }

    /**
     * Gets the physical file location of the file.
     * <p/>
     * @return Physical file location of the file
     */
    public String getFileLocation() {
        String filePath;
        if (StringUtils.isBlank(getPath())) {
            filePath = "";
        } else {
            // Replace File.separator slashes with URL slashes
            filePath = getPath();
        }

        return mediaItem.getCatalogue().getLocation() + File.separator
                + filePath + File.separator + getFilename();
    }

    /**
     * Gets the file extension of the {@link MediaItem}.
     *
     * @return File extension of the {@link MediaItem}, or an empty
     *         {@link String} is the extension could not be detected
     */
    public String getExtension() {
        return FilenameUtils.getExtension(getFilename());
    }

    /**
     * Determines if the {@link Rendition} is a video.
     * <p/>
     * @return {@code true} if the {@link Rendition} is a video, otherwise {@code false}
     */
    public boolean isVideo() {
        if (getContentType() == null) {
            return false;
        } else if (getContentType().startsWith("video")) {
            return true;
        } else if (getContentType().startsWith("application/vnd.rn-realmedia")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the {@link Rendition} is an audio clip.
     * <p/>
     * @return {@code true} if the {@ink Rendition}
     * is an audio clip, otherwise {@code false}
     */
    public boolean isAudio() {
        try {
            return getContentType().startsWith("audio");
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Determines if the {@link Rendition} is an image.
     * <p/>
     * @return {@code true} if the {@ink Rendition}
     * is an image, otherwise {@code false}
     */
    public boolean isImage() {
        try {
            return getContentType().startsWith("image");
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Determines if the {@link Rendition} is a document.
     * <p/>
     * @return {@code true} if the {@ink Rendition}
     * is a document, otherwise {@code false}
     */
    public boolean isDocument() {
        try {
            if (getContentType().startsWith("text")) {
                return true;
            }

            if (getContentType().startsWith("application/xhtml")) {
                return true;
            }

            if (getContentType().startsWith("application/pdf")) {
                return true;
            }

            if (getContentType().startsWith("application/msword")) {
                return true;
            }

            if (getContentType().startsWith(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                return true;
            }
        } catch (Exception ex) {
            return false;
        }

        return false;
    }

    /**
     * Determines if the {@link Rendition} of this {@link MediaItemRendition}
     * is the same as the original {@link Rendition} of the {@link MediaItem}
     * {@link Catalogue}.
     * <p/>
     * @return {@code true} if this is the original {@link Rendition},
     * otherwise {@code false}
     */
    public boolean isOriginalRendition() {
        if (getMediaItem() == null || getMediaItem().getCatalogue() == null
                || getMediaItem().getCatalogue().getOriginalRendition() == null) {
            return false;
        } else {
            if (getMediaItem().getCatalogue().getOriginalRendition().equals(
                    getRendition())) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MediaItemRendition)) {
            return false;
        }
        MediaItemRendition other = (MediaItemRendition) object;
        if ((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[ id=" + id + " ]";
    }
}
