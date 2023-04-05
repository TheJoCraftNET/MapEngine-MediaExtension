package de.pianoman911.mapengine.media.movingimages;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

public interface FrameSource {

    FullSpacedColorBuffer next();

    void start();

    void stop();

    double frameRate();

    boolean ended();

    long frames();
}
