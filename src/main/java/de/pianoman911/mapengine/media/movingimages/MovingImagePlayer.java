package de.pianoman911.mapengine.media.movingimages;

import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.media.util.NanoTimer;
import org.bytedeco.ffmpeg.global.avutil;

import java.util.ArrayDeque;
import java.util.Queue;

public class MovingImagePlayer {

    static {
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
    }

    private final FrameSource source;
    private final int bufferSize;
    private final IDrawingSpace drawingSpace;
    private final Queue<FullSpacedColorBuffer> buffer;
    private boolean running;
    private boolean bufferBuilt;

    public MovingImagePlayer(FrameSource source, int bufferSize, IDrawingSpace drawingSpace) {
        this.source = source;
        this.bufferSize = bufferSize;
        this.drawingSpace = drawingSpace;
        this.buffer = new ArrayDeque<>(bufferSize);

        new NanoTimer(() -> {
            if (running && !source.ended()) {
                FullSpacedColorBuffer frame = source.next();
                if (frame != null) {
                    buffer.add(frame);
                }

                if (bufferBuilt || buffer.size() >= bufferSize) {
                    bufferBuilt = true;
                    if (!buffer.isEmpty()) {
                        FullSpacedColorBuffer current = buffer.poll();
                        System.arraycopy(current.buffer(), 0, drawingSpace.buffer().buffer(), 0, current.buffer().length);
                        drawingSpace.flush();
                    }
                }
            }
        }, 1000, source.frameRate());
    }

    public FrameSource source() {
        return source;
    }

    public int bufferSize() {
        return bufferSize;
    }

    public IDrawingSpace drawingSpace() {
        return drawingSpace;
    }

    public Queue<FullSpacedColorBuffer> buffer() {
        return buffer;
    }

    public boolean running() {
        return running;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void restart() {
        if (running) stop();
        start();
    }
}
