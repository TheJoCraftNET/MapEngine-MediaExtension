package de.pianoman911.mapengine.media.movingimages;

import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.api.util.ImageUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FFmpegFrameSource implements FrameSource {

    private static final Java2DFrameConverter FRAME_CONVERTER = new Java2DFrameConverter();

    private final FFmpegFrameGrabber grabber;
    private final MovingImagePlayer player;
    private final boolean resize;

    private FFmpegFrameSource(FFmpegFrameGrabber grabber, int bufferSize, IDrawingSpace space, boolean resize) {
        this.grabber = grabber;
        this.resize = resize;
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
            BufferedImage image = FRAME_CONVERTER.convert(frame);
            if (resize) {
                image = ImageUtils.resize(image, player.drawingSpace().buffer().x(), player.drawingSpace().buffer().y());
            }
            return new FullSpacedColorBuffer(ImageUtils.rgb(image), image.getWidth(), image.getHeight());
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
