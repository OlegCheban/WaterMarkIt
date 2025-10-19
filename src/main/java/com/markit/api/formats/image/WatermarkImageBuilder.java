package com.markit.api.formats.image;

import com.markit.api.WatermarkProcessor;
import com.markit.api.builders.DefaultVisualWatermarkBuilder;
import com.markit.exceptions.WatermarkingException;
import com.markit.image.ImageWatermarker;
import com.markit.servicelocator.ServiceFactory;

import java.io.File;

/**
 * @author Oleg Cheban
 * @since 1.3.0
 */
public final class WatermarkImageBuilder
        extends DefaultVisualWatermarkBuilder<WatermarkImageService, WatermarkImageService.WatermarkImageBuilder>
        implements WatermarkImageService, WatermarkImageService.WatermarkImageBuilder {

    public WatermarkImageBuilder(byte[] fileBytes) {
        super(createWatermarkProcessor(fileBytes));
    }

    public WatermarkImageBuilder(File file) {
        super(createWatermarkProcessor(file));
    }

    private static WatermarkProcessor createWatermarkProcessor(File file) {
        return watermarks -> {
            try {
                return getImageWatermarker().watermark(file, watermarks);
            } catch (Exception e) {
                throw new WatermarkingException("Error watermarking the image", e);
            }
        };
    }

    private static WatermarkProcessor createWatermarkProcessor(byte[] fileBytes) {
        return watermarks -> {
            try {
                return getImageWatermarker().watermark(fileBytes, watermarks);
            } catch (Exception e) {
                throw new WatermarkingException("Error watermarking the image", e);
            }
        };
    }

    private static ImageWatermarker getImageWatermarker() {
        return (ImageWatermarker) ServiceFactory.getInstance().getService(ImageWatermarker.class);
    }
}
