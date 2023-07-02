package de.pianoman911.mapengine.media.movingimages;

import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class MovingImagePlayer {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
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

        EXECUTOR.execute(() -> {
            while (!Thread.interrupted()) {
                if (running && !source.ended()) {
                    long start = System.nanoTime();
                    FullSpacedColorBuffer frame = source.next();
                    if (frame != null) {
                        buffer.add(frame);
                    }

                    if (bufferBuilt || buffer.size() >= bufferSize) {
                        bufferBuilt = true;
                        if (!buffer.isEmpty()) {
                            FullSpacedColorBuffer current = buffer.poll();
                            drawingSpace.buffer(current, 0, 0);
                            drawingSpace.flush();
                        }
                    }
                    long offset = (long) ((1.0 / source.frameRate()) * 1000000000);
                    long end = System.nanoTime();
                    long sleep = offset - (end - start);
                    if (sleep > 0) {
                        LockSupport.parkNanos(sleep);
                    }
                }
            }
        });
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
