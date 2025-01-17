package com.markit.api.impl;

import com.markit.api.ImageType;
import com.markit.api.WatermarkImageService;
import com.markit.api.WatermarkPDFService;
import com.markit.api.WatermarkService;
import com.markit.exceptions.InvalidPDFFileException;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * @author Oleg Cheban
 * @since 1.0
 */
public class WatermarkServiceImpl implements WatermarkService.WatermarkServiceSelector {
    private Executor executor;

    public WatermarkServiceImpl() {
    }

    public WatermarkServiceImpl(Executor e) {
        this.executor = e;
    }

    @Override
    public WatermarkPDFService watermarkPDF(byte[] fileBytes) {
        try {
            return new WatermarkPDFServiceImpl(PDDocument.load(fileBytes), executor);
        } catch (IOException e) {
            throw new InvalidPDFFileException(e);
        }
    }

    @Override
    public WatermarkPDFService watermarkPDF(File file) {
        try {
            return new WatermarkPDFServiceImpl(PDDocument.load(file), executor);
        } catch (IOException e) {
            throw new InvalidPDFFileException(e);
        }
    }

    @Override
    public WatermarkPDFService watermarkPDF(PDDocument document) {
        return new WatermarkPDFServiceImpl(document, executor);
    }

    @Override
    public WatermarkImageService watermarkImage(byte[] fileBytes, ImageType imageType) {
        return new WatermarkImageServiceImpl(fileBytes, imageType);
    }

    @Override
    public WatermarkImageService watermarkImage(File file, ImageType imageType) {
        return new WatermarkImageServiceImpl(file, imageType);
    }
}
