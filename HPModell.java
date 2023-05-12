import java.util.ArrayList;
import java.util.Arrays;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.awt.BasicStroke;

import javax.imageio.ImageIO;

import static Helpers.Helpers.RelDir;
import static Helpers.Helpers.H_Richtung;

class HPModell {
    private ArrayList<Node> proteins;
    private int hydroContacts;
    private int overlaps;

    public HPModell() {
        this.proteins = new ArrayList<Node>();

    }

    public void createFromSequenz(String sequenz) {
        for (int i = 0; i < sequenz.length(); i = i + 2) {
            boolean isHydrophobic = sequenz.charAt(i) == 'H';
            RelDir direction = null;
            switch (sequenz.charAt(i + 1)) {
                case 'L':
                    direction = RelDir.Left;
                    break;
                case 'F':
                    direction = RelDir.Forward;
                    break;
                case 'R':
                    direction = RelDir.Right;
                    break;
            }
            this.proteins.add(new Node(direction, isHydrophobic));
        }
    }

    public void createRandomHPModell(int length) {
        RelDir lastTreeDirections[] = new RelDir[3];
        for (int i = 0; i < length; i++) {
            boolean isHydrophobic = Math.random() < 0.5;
            RelDir direction = RelDir.values()[(int) (Math.random() * RelDir.values().length)];
            // Random direction
            while ((lastTreeDirections[0] == direction) && (lastTreeDirections[1] == direction)
                    && (lastTreeDirections[2] == direction)) {
                System.out.println("3x the same direction");
                direction = RelDir.values()[(int) (Math.random() * RelDir.values().length)];
            }

            lastTreeDirections[0] = lastTreeDirections[1];
            lastTreeDirections[1] = lastTreeDirections[2];
            lastTreeDirections[2] = direction;

            this.proteins.add(new Node(direction, isHydrophobic));

        }
    }

    public void printPopulation() {
        for (Node node : this.proteins) {
            System.out.print((node.getIsHydrophobic() ? "H" : "P")
                    + (node.getDirection().toString()).substring(0, 1) + " ");
        }
        System.out.println();
    }

    @Override
    public String toString() {
        String result = "";
        for (Node node : this.proteins) {
            result += (node.getIsHydrophobic() ? "H" : "P") + (node.getDirection().toString()).substring(0, 1);
        }
        return result;
    }

    private int[][] createOverlappingMaze() {
        int anzProteins = this.proteins.size();
        int x = anzProteins;
        int y = anzProteins;
        int maze[][] = new int[x * 2][y * 2];
        for (int i = 0; i < x * 2; i++) {
            Arrays.fill(maze[i], 0);
        }

        H_Richtung lastH_Richtung = H_Richtung.Nord; // Starting direction

        for (Node currentNode : this.proteins) {

            // maze[x][y] = currentNode.getIsHydrophobic() ? 1 : 0;
            maze[x][y] = maze[x][y] + 1;
            currentNode.setX(x);
            currentNode.setY(y);

            int initRichtung = lastH_Richtung.ordinal();
            int relRichtung = currentNode.getDirection().getValue();
            int intFromEnums = initRichtung + relRichtung;
            int intFromEnumsMod = intFromEnums % 4;
            lastH_Richtung = H_Richtung.values()[intFromEnumsMod < 0 ? intFromEnumsMod +
                    4 : intFromEnumsMod];
            // Update x and y
            switch (lastH_Richtung) {
                case Nord:
                    x--;
                    break;
                case Ost:
                    y++;
                    break;
                case Sued:
                    x++;
                    break;
                case West:
                    y--;
                    break;
            }

            continue;

        }
        return maze;

    }

    public void calcFitness() {

        this.hydroContacts = 0;
        int firstThree = 3;
        int x = 0;
        int y = 0;
        // // maze = new int[20][20];
        // for (int i = 0; i < 20; i++) {
        // Arrays.fill(maze[i], -1);
        // }
        // int lastX = x;
        // int lastY = y;

        H_Richtung lastH_Richtung = H_Richtung.Nord; // Starting direction
        overlaps = 0;

        for (int i = 0; i < this.proteins.size(); i++) {
            Node currentNode = this.proteins.get(i);

            // maze[x][y] = currentNode.getIsHydrophobic() ? 1 : 0;
            currentNode.setX(x);
            currentNode.setY(y);

            int initRichtung = lastH_Richtung.ordinal();
            int relRichtung = currentNode.getDirection().getValue();
            int intFromEnums = initRichtung + relRichtung;
            int intFromEnumsMod = intFromEnums % 4;
            lastH_Richtung = H_Richtung.values()[intFromEnumsMod < 0 ? intFromEnumsMod + 4 : intFromEnumsMod];

            if (firstThree <= 0) { // Less than 3 nodes => No hydrophobic contacts and no overlaps
                for (int j = 0; j < i - 2; j++) // j < i-1 => Excludes self and last
                {
                    Node otherNode = this.proteins.get(j);
                    int otherX = otherNode.getX();
                    int otherY = otherNode.getY();
                    if (otherX == x && otherY == y) { // Overlap
                        overlaps++;
                    } else if (currentNode.getIsHydrophobic() && otherNode.getIsHydrophobic()) { // No overlap &&
                                                                                                 // currentNode is
                                                                                                 // hydrophobic => Check
                        // for hydrophobic contacts
                        if (otherX == x && otherY == y - 1) { // Left
                            this.hydroContacts++;
                        } else if (otherX == x && otherY == y + 1) { // Right
                            this.hydroContacts++;
                        } else if (otherX == x - 1 && otherY == y) { // Up
                            this.hydroContacts++;
                        } else if (otherX == x + 1 && otherY == y) { // Down
                            this.hydroContacts++;
                        }
                    }
                }
            }

            // Update x and y
            switch (lastH_Richtung) {
                case Nord:
                    x--;
                    break;
                case Ost:
                    y++;
                    break;
                case Sued:
                    x++;
                    break;
                case West:
                    y--;
                    break;
            }

            // lastX = currentNode.getX();
            // lastY = currentNode.getY();
            firstThree--;
            continue;

        }

    }

    public double getFitness() {
        // return (this.hydroContacts) / (this.overlaps + 1);
        double hydroContacts = this.hydroContacts;
        double overlaps = this.overlaps;
        return (hydroContacts) / (overlaps + 1);
    }

    public int getOverlaps() {
        return this.overlaps;
    }

    public int getHydroContacts() {
        return this.hydroContacts;
    }

    public void printOverlappingMaze() {
        int maze[][] = this.createOverlappingMaze();
        for (int i = 0; i < maze.length; i++) {
            System.out.print("|");
            for (int j = 0; j < maze[i].length; j++) {
                System.out.print(maze[i][j] != 0 ? maze[i][j] : " ");
                System.out.print("|");
            }
            System.out.println();
        }
    }

    private void paintBorder(Graphics2D g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();

        float thickness = 3;
        g.setColor(Color.BLACK);
        // top
        g.setStroke(new BasicStroke(thickness));
        g.drawLine(x, y, x + width, y);
        // bottom
        g.drawLine(x, y + height, x + width, y + height);
        // right
        g.drawLine(x + width, y, x + width, y + height);
        // left
        g.drawLine(x, y, x, y + height);
        g.setColor(oldColor);
    }

    public void exportAsImage() {
        this.calcFitness();

        int height = 1000;
        int width = 1000;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        // Draw overlaps, hydrophobic contacts and fitness
        g2.setColor(Color.BLACK);
        g2.drawString("Overlaps: " + this.overlaps, 10, 20);
        g2.drawString("Hydrophobic contacts: " + this.hydroContacts, 10, 40);
        g2.drawString("Fitness: " + this.getFitness(), 10, 60);

        int cellSize = 50;
        int x = 500;
        int y = 500;
        H_Richtung lastH_Richtung = H_Richtung.Nord; // Starting direction
        for (int i = 0; i < this.proteins.size(); i++) {
            Node currentNode = this.proteins.get(i);
            int initRichtung = lastH_Richtung.ordinal();
            int relRichtung = currentNode.getDirection().getValue();
            int intFromEnums = initRichtung + relRichtung;
            int intFromEnumsMod = intFromEnums % 4;
            lastH_Richtung = H_Richtung.values()[intFromEnumsMod < 0 ? intFromEnumsMod + 4 : intFromEnumsMod];

            if (currentNode.getIsHydrophobic()) {
                g2.setColor(Color.BLACK);

            } else {
                g2.setColor(Color.WHITE);
            }
            paintBorder(g2, x, y, cellSize, cellSize);
            g2.fillRect(x, y, cellSize, cellSize);

            String label = Integer.toString(i);
            Font font = new Font("Serif", Font.PLAIN, 20);
            g2.setFont(font);
            FontMetrics metrics = g2.getFontMetrics(font);
            int ascent = metrics.getAscent();
            int labelWidth = metrics.stringWidth(label);
            g2.setColor(currentNode.getIsHydrophobic() ? Color.WHITE : Color.BLACK);
            g2.drawString(label, x + (cellSize - labelWidth) / 2, y + (cellSize + ascent) / 2);

            // Update x and y
            g2.setColor(Color.RED);

            switch (lastH_Richtung) {
                case Nord:
                    g2.drawLine(x + cellSize / 2, y, x + cellSize / 2, y - cellSize);
                    y -= (cellSize * 2);
                    break;
                case Ost:
                    g2.drawLine(x + cellSize, y + cellSize / 2, x + cellSize * 2, y + cellSize / 2);
                    x += (cellSize * 2);
                    break;
                case Sued:
                    g2.drawLine(x + cellSize / 2, y + cellSize, x + cellSize / 2, y + cellSize * 2);
                    y += (cellSize * 2);
                    break;
                case West:
                    g2.drawLine(x, y + cellSize / 2, x - cellSize, y + cellSize / 2);
                    x -= (cellSize * 2);
                    break;
            }
        }

        String folder = "/tmp/ga";
        String filename = this.toString() + ".png";
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