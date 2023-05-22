import java.util.ArrayList;

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
    public double fitnessScaled;
    // public double fitnessProzent;
    public static int anzahlNodes = 20;

    public ArrayList<Node> getProteins() {
        return this.proteins;
    }

    // private void setProteins(ArrayList<Node> proteins) {
    // this.proteins = proteins;
    // }

    public HPModell() {
        this.proteins = new ArrayList<Node>();

    }

    public HPModell(String sequenz) {
        this.proteins = new ArrayList<Node>();
        this.createFromSequenz(sequenz);
    }

    private void createFromSequenz(String sequenz) {
        for (int i = 0; i < sequenz.length(); i = i + 2) {
            boolean isHydrophobic = sequenz.charAt(i) == '1';
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
                default:
                    throw new IllegalArgumentException("Invalid direction: " + sequenz.charAt(i + 1));
            }
            this.proteins.add(new Node(direction, isHydrophobic));
        }
    }

    public RelDir[] getDirections() {
        RelDir[] directions = new RelDir[this.proteins.size()];
        for (int i = 0; i < this.proteins.size(); i++) {
            directions[i] = this.proteins.get(i).getDirection();
        }
        return directions;
    }

    public void setDirections(RelDir[] directions) {
        for (int i = 0; i < directions.length; i++) {
            this.proteins.get(i).setDirection(directions[i]);
        }
    }

    // public void createRandomHPModell() {
    // RelDir lastTwoDirections[] = new RelDir[2];
    // for (int i = 0; i < anzahlNodes; i++) {
    // boolean isHydrophobic = Math.random() < 0.5;
    // RelDir direction = RelDir.values()[(int) (Math.random() *
    // RelDir.values().length)];
    // while ((lastTwoDirections[0] == direction) && (lastTwoDirections[1] ==
    // direction)) {
    // direction = RelDir.values()[(int) (Math.random() * RelDir.values().length)];
    // }
    // lastTwoDirections[0] = lastTwoDirections[1];
    // lastTwoDirections[1] = direction;
    // this.proteins.add(new Node(direction, isHydrophobic));

    // }
    // }

    // public void mutation() {
    // int index = (int) (Math.random() * this.proteins.size());
    // // boolean fullMutation = Math.random() < 0.01;
    // Node node = this.proteins.get(index);
    // // if (fullMutation) {
    // // this.mutateFull(node);
    // // } else {
    // // if (Math.random() < 0.5) {
    // this.mutateDirection(node);
    // // } else {
    // // this.mutateHydrophobic(node);
    // // }
    // // }
    // }

    // private void mutateFull(Node node) {
    // node.setIsHydrophobic(Math.random() < 0.5);
    // node.setDirection(RelDir.values()[(int) (Math.random() *
    // RelDir.values().length)]);
    // }

    public void mutateDirection() {
        int index = (int) (Math.random() * this.proteins.size());
        Node node = this.proteins.get(index);
        int plusorminus = (int) (Math.random() * 2);
        int newDirection = 0;
        if (plusorminus == 0) {
            newDirection = (node.getDirection().ordinal() + 1) % RelDir.values().length;
        } else {
            newDirection = (node.getDirection().ordinal() - 1) % RelDir.values().length;

        }
        // If negative, add 3
        newDirection = newDirection < 0 ? newDirection + 3 : newDirection;
        node.setDirection(RelDir.values()[newDirection]);
    }

    // private void mutateHydrophobic(Node node) {
    // node.setIsHydrophobic(!node.getIsHydrophobic());
    // }

    // public void printPopulation() {
    // for (Node node : this.proteins) {
    // System.out.print((node.getIsHydrophobic() ? "H" : "P")
    // + (node.getDirection().toString()).substring(0, 1) + " ");
    // }
    // System.out.println();
    // }

    @Override
    public String toString() {
        String result = "";
        for (Node node : this.proteins) {
            result += (node.getIsHydrophobic() ? "1" : "0") + (node.getDirection().toString()).substring(0, 1);
        }
        return result;
    }

    public void calcFitness() {
        this.hydroContacts = 0;
        // int firstThree = 3;
        int x = 0;
        int y = 0;

        H_Richtung lastH_Richtung = H_Richtung.Nord; // Starting direction
        overlaps = 0;

        for (int i = 0; i < this.proteins.size(); i++) {
            Node currentNode = this.proteins.get(i);
            currentNode.setX(x);
            currentNode.setY(y);

            int initRichtung = lastH_Richtung.ordinal();
            int relRichtung = currentNode.getDirection().getValue();
            int intFromEnums = initRichtung + relRichtung;
            int intFromEnumsMod = intFromEnums % 4;
            lastH_Richtung = H_Richtung.values()[intFromEnumsMod < 0 ? intFromEnumsMod + 4 : intFromEnumsMod];

            // if (firstThree <= 0) {
            // Less than 3 nodes => No hydrophobic contacts and no overlaps
            if (i >= 3) {
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
            // firstThree--;
            continue;

        }

    }

    public double getFitness() {
        if (this.overlaps == -1 || this.hydroContacts == -1) {
            this.calcFitness();
        }
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

    public void exportAsImage(int generation) {
        this.calcFitness();

        String folder = HP.outputFolder;
        String filename = generation + "_" +
                this.toString() + ".png";
        if (new File(folder).exists() == false)
            new File(folder).mkdirs();
        // Check if file exists
        File file = new File(folder + "/" + filename);
        if (file.exists()) {
            return;
        }

        int height = 2000;
        int width = 2000;
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
        int x = 1000;
        int y = 1000;
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

            // Dont draw last line
            if (i != this.proteins.size() - 1) {

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

        }

        try {
            ImageIO.write(image, "png", new File(folder + File.separator + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    public void resetFitness() {
        this.overlaps = -1;
        this.hydroContacts = -1;
    }
}