// package hda.fbi.ga.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

// Beispiel zu Java 2D-Graphik für Genetische Algorithmen
// (C) Alexander del Pino

public class GraphikBeispiel {

    public static void main(String[] args) {

        int height = 500;
        int width = 800;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.YELLOW);
        g2.fillRect(0, 0, width, height);

        int cellSize = 80;

        g2.setColor(new Color(0, 200, 0)); // green
        g2.fillRect(50, 50, cellSize, cellSize);

        g2.setColor(new Color(255, 0, 0)); // red
        g2.fillRect(250, 50, cellSize, cellSize);

        g2.setColor(Color.BLACK);
        g2.drawLine(50 + cellSize, 50 + cellSize / 2, 250, 60 + cellSize / 2);

        g2.setColor(new Color(255, 255, 255)); // white
        String label = "GA";
        Font font = new Font("Serif", Font.PLAIN, 40);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();
        int ascent = metrics.getAscent();
        int labelWidth = metrics.stringWidth(label);

        g2.drawString(label, 50 + cellSize / 2 - labelWidth / 2, 50 + cellSize / 2 + ascent / 2);

        String folder = "/tmp/alex/ga";
        String filename = "bild.png";
        if (new File(folder).exists() == false)
            new File(folder).mkdirs();

        try {
            ImageIO.write(image, "png", new File(folder + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

}
