package com.markit.pdf.overlay;

import com.markit.api.WatermarkAttributes;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import java.io.IOException;
import java.util.List;

/**
 * @author Oleg Cheban
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class DefaultOverlayPdfWatermarker implements OverlayPdfWatermarker {

    public DefaultOverlayPdfWatermarker() {
    }

    @Override
    public int getPriority() {
        return DEFAULT_PRIORITY;
    }

    @Override
    public void watermark(PDDocument document, int pageIndex, List<WatermarkAttributes> attrs) throws IOException {
        var watermarkPositioner = new WatermarkPositioner();
        var imageBasedOverlayWatermarker = new ImageBasedOverlayWatermarker(watermarkPositioner);
        var textBasedOverlayWatermarker = new TextBasedOverlayWatermarker(new TrademarkHandler(), watermarkPositioner);
        var page = document.getPage(pageIndex);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            attrs.forEach(attr -> {
                try {
                    contentStream.setGraphicsStateParameters(setOpacity(attr.getOpacity()));
                    if (attr.getImage().isPresent()){
                        var image = LosslessFactory.createFromImage(document, attr.getImage().get());
                        imageBasedOverlayWatermarker.overlay(contentStream, image, page.getMediaBox(), attr);
                    } else {
                        textBasedOverlayWatermarker.overlay(contentStream, page.getMediaBox(), attr);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private PDExtendedGraphicsState setOpacity(int opacity){
        if (opacity <= 0 || opacity >= 100) throw new IllegalArgumentException("opacity not in valid range of 0-100");
        float opacityFloat = opacity/100f;
        var transparencyState = new PDExtendedGraphicsState();
        transparencyState.setNonStrokingAlphaConstant(opacityFloat);
        return transparencyState;
    }
}
