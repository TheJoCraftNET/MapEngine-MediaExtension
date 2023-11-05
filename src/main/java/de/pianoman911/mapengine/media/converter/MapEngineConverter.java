package de.pianoman911.mapengine.media.converter;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameConverter;

import java.nio.ByteBuffer;

public class MapEngineConverter extends FrameConverter<FullSpacedColorBuffer> {

    @Override
    public Frame convert(FullSpacedColorBuffer bufferedImage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FullSpacedColorBuffer convert(Frame frame) {
        if (frame.imageChannels != 3 && frame.imageChannels != 4) {
            throw new IllegalStateException("Frame must have 3/4 channels, we need BGR/ABGR. This frame has " + frame.imageChannels + " channels. Please contact me if you need support for this.");
        }

        ByteBuffer buffer = (ByteBuffer) frame.image[0].slice();
        if (buffer.remaining() == 0) {
            return null;
        }

        int[] data = new int[frame.imageWidth * frame.imageHeight];
        boolean hasAlpha = frame.imageChannels == 4;
        int index = 0;

        for (int y = 0; y < frame.imageHeight; y++) {
            for (int x = 0; x < frame.imageWidth; x++) {
                int r = buffer.get(index) & 0xFF;
                int g = buffer.get(index + 1) & 0xFF;
                int b = buffer.get(index + 2) & 0xFF;
                int a = hasAlpha ? buffer.get(index + 3) & 0xFF : 255;
                data[y * frame.imageWidth + x] = (a << 24) | (r << 16) | (g << 8) | b;
                index += frame.imageChannels;
            }
            index += frame.imageStride - frame.imageWidth * frame.imageChannels;
        }
        return new FullSpacedColorBuffer(data, frame.imageWidth, frame.imageHeight);
    }
}
