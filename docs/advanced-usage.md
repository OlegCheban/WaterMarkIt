# Advanced usage

[Back to the README](../README.md)

These examples describe the current source API. For a published dependency, check the matching Javadoc version. Snippets are method bodies with the imports shown; handle or declare `IOException` when saving results.

## Combine visual layers

Use `and()` to add another watermark. Each layer starts with fresh defaults, including the PDF method, so select `OVERLAY` on every layer if you want to preserve the page content.

```java
import com.markit.api.Font;
import com.markit.api.WatermarkService;
import com.markit.api.WatermarkingMethod;
import com.markit.api.positioning.WatermarkPosition;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

byte[] result = WatermarkService.create()
    .watermarkPDF(new File("input.pdf"))
    .withImage(new File("logo.png"))
    .method(WatermarkingMethod.OVERLAY)
    .position(WatermarkPosition.CENTER).end()
    .opacity(20)
    .and()
    .withText("WaterMarkIt")
        .font(Font.ARIAL)
        .bold()
        .color(Color.BLUE)
        .addTrademark()
        .end()
    .method(WatermarkingMethod.OVERLAY)
    .position(WatermarkPosition.TILED)
        .adjust(35, 0)
        .horizontalSpacing(10)
        .end()
    .opacity(10)
    .rotation(25)
    .size(110)
    .and()
    .withText("REVIEW COPY").end()
    .method(WatermarkingMethod.OVERLAY)
    .position(WatermarkPosition.TOP_RIGHT)
        .adjust(0, -30)
        .end()
    .size(50)
    .apply();

Files.write(Path.of("layered.pdf"), result);
```

`end()` returns from text or position configuration to the current watermark. `and()` finishes that watermark and begins the next one. `apply()` processes all configured layers and returns one encoded file.

Use `position(WatermarkPosition...)` for named positions, `adjust(x, y)` for offsets, or `position(x, y)` for explicit coordinates. `TILED` supports horizontal and vertical spacing. Coordinate and size behavior depends on the renderer; check the result in the target format.

The PDF pipeline processes `DRAW` layers before `OVERLAY` layers, regardless of their order in the builder. If any `DRAW` layer applies to a page, that page is rasterized before overlays are added.

## Parallel PDF rendering

Without an executor, PDF pages are processed sequentially. To process `DRAW` page tasks in a pool, supply an executor owned by your application:

```java
import com.markit.api.WatermarkService;
import com.markit.api.WatermarkingMethod;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

ExecutorService executor = Executors.newFixedThreadPool(2);
try {
    byte[] result = WatermarkService.create(executor)
        .watermarkPDF(new File("input.pdf"))
        .withText("PREVIEW").end()
        .method(WatermarkingMethod.DRAW)
        .dpi(150)
        .apply();

    Files.write(Path.of("parallel-preview.pdf"), result);
} finally {
    executor.shutdown();
}
```

The library submits a task for each page to the supplied executor and waits for the tasks to finish. It does not create a dedicated thread for every page or shut down your executor. `OVERLAY` remains sequential.

The example owns a pool for one operation. In a long-running application, a shared executor can be managed at application startup and shutdown. Choose concurrency with memory use in mind: rendering several large pages at once increases peak heap usage, particularly at high DPI.

## Replace a processing service

Implement an SPI interface, assign a priority above the default, and register the implementation through `ServiceLoader`. A concrete example is a PDF renderer that records the time spent on each rendered page.

Save this class as `com/example/TimedDrawPdfWatermarker.java`:

```java
package com.example;

import com.markit.api.WatermarkAttributes;
import com.markit.pdf.draw.DefaultDrawPdfWatermarker;
import com.markit.pdf.draw.DrawPdfWatermarker;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.List;

public final class TimedDrawPdfWatermarker implements DrawPdfWatermarker {
    private final DrawPdfWatermarker delegate = new DefaultDrawPdfWatermarker();
    private final System.Logger logger = System.getLogger(getClass().getName());

    @Override
    public int getPriority() {
        return DEFAULT_PRIORITY + 1;
    }

    @Override
    public void watermark(PDDocument document, int pageIndex,
                          List<WatermarkAttributes> attributes) throws IOException {
        long start = System.nanoTime();
        delegate.watermark(document, pageIndex, attributes);
        logger.log(System.Logger.Level.DEBUG,
            "Watermarked page {0} in {1} ms",
            pageIndex, (System.nanoTime() - start) / 1_000_000);
    }
}
```

Create `src/main/resources/META-INF/services/com.markit.pdf.draw.DrawPdfWatermarker` in your application or extension JAR with this content:

```text
com.example.TimedDrawPdfWatermarker
```

The implementation must be available on the runtime classpath and constructible by `ServiceLoader`. WaterMarkIt selects the provider with the highest priority and caches it for each service interface. Register providers before first use. If packaging a shaded JAR, preserve or merge the `META-INF/services` files.

Useful extension points include:

| Interface | Responsibility |
| --- | --- |
| `DrawPdfWatermarker` | Watermark a rasterized PDF page |
| `OverlayPdfWatermarker` | Add watermarks to existing PDF page content |
| `ImageWatermarker` | Process an image |
| `VideoWatermarker` | Process video |
| `AudioWatermarker` | Mix audible watermarks |
| `TextToSpeechEngine` | Synthesize text into encoded WAV audio |

For custom speech, implement `TextToSpeechEngine` and expose supported voices through `TextToSpeechVoice`, for example with an enum. Implement both synthesis overloads if your engine supports explicit voice selection. The provider determines which voices and languages are available.

See the [package map](../CONTRIBUTING.md#find-your-way-around) and the provider registrations in [META-INF/services](../src/main/resources/META-INF/services) to locate the interfaces and defaults.
