package de.pianoman911.mapengine.media.converter;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameConverter;

import java.nio.ByteBuffer;

public class MapEngineConverter extends FrameConverter<FullSpacedColorBuffer> {

    @Override
    public Frame convert(FullSpacedColorBuffer bufferedImage) {
        return null;
    }

    @Override
    public FullSpacedColorBuffer convert(Frame frame) {
        if (frame.imageChannels != 3) {
            throw new IllegalStateException("Frame must have 3 channels, we need RGBA. This frame has " + frame.imageChannels + " channels. Please contact me if you need support for this.");
        }
        FullSpacedColorBuffer result = new FullSpacedColorBuffer(frame.imageWidth, frame.imageHeight);
        ByteBuffer buffer = (ByteBuffer) frame.image[0].slice();
        if (buffer.remaining() == 0) {
            return null;
        }
        int[] data = new int[buffer.remaining() / 3];
        for (int i = 0; i < data.length; i++) {
            int a = 255;
            int b = buffer.get() & 0xFF;
            int g = buffer.get() & 0xFF;
            int r = buffer.get() & 0xFF;

            data[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        System.arraycopy(data, 0, result.buffer(), 0, data.length);

        return result;
    }
}
