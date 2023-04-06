package de.pianoman911.mapengine.media.movingimages;

import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.media.converter.MapEngineConverter;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FFmpegFrameSource implements FrameSource {

    private static final MapEngineConverter FRAME_CONVERTER = new MapEngineConverter();

    static {
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
    }

    private final FFmpegFrameGrabber grabber;
    private final MovingImagePlayer player;
    private final boolean resize;

    private FFmpegFrameSource(FFmpegFrameGrabber grabber, int bufferSize, IDrawingSpace space, boolean resize) {
        this.grabber = grabber;
        this.resize = resize;
        try {
            grabber.start();
            grabber.stop(); // This is needed to get the content information
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.player = new MovingImagePlayer(this, bufferSize, space);
    }

    public FFmpegFrameSource(File file, int bufferSize, IDrawingSpace space, boolean resize) {
        this(new FFmpegFrameGrabber(file), bufferSize, space, resize);
    }

    public FFmpegFrameSource(URI url, int bufferSize, IDrawingSpace space, boolean resize) {
        this(new FFmpegFrameGrabber(url.toString()), bufferSize, space, resize);
    }

    @Override
    public FullSpacedColorBuffer next() {
        try {
            Frame frame = grabber.grabImage();
            if (frame == null) {
                return null;
            }
            FullSpacedColorBuffer buffer = FRAME_CONVERTER.convert(frame);
            if (buffer == null) {
                return null;
            }

            if (resize) {
                double widthScale = player.drawingSpace().buffer().width() / (double) buffer.width();
                double heightScale = player.drawingSpace().buffer().height() / (double) buffer.height();
                buffer = buffer.scale(widthScale, heightScale, true);
            }
            return buffer;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void start() {
        player.start();
        try {
            grabber.start();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void stop() {
        player.stop();
        try {
            grabber.stop();
            grabber.release();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public double frameRate() {
        return grabber.getFrameRate();
    }

    @Override
    public boolean ended() {
        return grabber.isCloseInputStream();
    }

    @Override
    public long frames() {
        return grabber.getLengthInVideoFrames();
    }

    public MovingImagePlayer player() {
        return player;
    }
}
