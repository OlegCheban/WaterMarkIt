import com.markit.api.WatermarkService;
import com.markit.api.positioning.WatermarkPosition;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generates the README's original sample and its actual WaterMarkIt output.
 * Run from the repository root with the library and its dependencies on the classpath.
 */
public final class GenerateImagePreview {
    public static void main(String[] args) throws IOException {
        Path output = Path.of(args.length == 0 ? "target/readme-preview" : args[0]);
        Files.createDirectories(output);
        Path original = output.resolve("image-before.png");
        ImageIO.write(createSample(), "PNG", original.toFile());

        byte[] result = WatermarkService.create()
            .watermarkImage(original.toFile())
            .withText("DRAFT").color(new Color(190, 45, 55)).bold().end()
            .position(WatermarkPosition.CENTER).end()
            .rotation(25)
            .size(65)
            .opacity(50)
            .apply();

        Files.write(output.resolve("image-after.png"), result);
    }

    private static BufferedImage createSample() {
        BufferedImage image = new BufferedImage(800, 500, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setColor(new Color(238, 242, 247));
            graphics.fillRect(0, 0, 800, 500);
            graphics.setColor(Color.WHITE);
            graphics.fillRoundRect(28, 24, 744, 452, 20, 20);
            graphics.setColor(new Color(27, 46, 73));
            graphics.fillRoundRect(28, 24, 744, 106, 20, 20);
            graphics.fillRect(28, 100, 744, 30);

            drawText(graphics, "PROJECT PROPOSAL", 58, 72, 25, Font.BOLD, Color.WHITE);
            drawText(graphics, "Acme Studio  /  Sample document", 58, 105,
                16, Font.PLAIN, new Color(196, 212, 234));

            Color heading = new Color(27, 46, 73);
            Color body = new Color(82, 99, 120);
            drawText(graphics, "Website redesign", 58, 177, 24, Font.BOLD, heading);
            drawText(graphics, "A clear, accessible home for your next idea.", 58, 208,
                17, Font.PLAIN, body);

            graphics.setColor(new Color(228, 234, 242));
            graphics.fillRect(58, 231, 684, 2);
            drawText(graphics, "DELIVERABLE", 58, 268, 13, Font.BOLD, body);
            drawText(graphics, "ESTIMATE", 605, 268, 13, Font.BOLD, body);
            drawText(graphics, "Discovery and content planning", 58, 306,
                17, Font.PLAIN, heading);
            drawText(graphics, "1 week", 605, 306, 17, Font.PLAIN, heading);
            drawText(graphics, "Design and implementation", 58, 345,
                17, Font.PLAIN, heading);
            drawText(graphics, "3 weeks", 605, 345, 17, Font.PLAIN, heading);

            graphics.setColor(new Color(228, 234, 242));
            graphics.fillRect(58, 374, 684, 2);
            drawText(graphics, "WM-001  /  Prepared for review", 58, 427,
                15, Font.PLAIN, body);
        } finally {
            graphics.dispose();
        }
        return image;
    }

    private static void drawText(Graphics2D graphics, String text, int x, int y,
                                 int size, int style, Color color) {
        graphics.setFont(new Font(Font.SANS_SERIF, style, size));
        graphics.setColor(color);
        graphics.drawString(text, x, y);
    }
}
