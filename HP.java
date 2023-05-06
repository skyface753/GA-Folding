import java.util.ArrayList;
import java.util.List;

/**
 * HP
 * https://arxiv.org/pdf/1608.05855.pdf
 * https://www.sciencedirect.com/science/article/pii/S147692711000040X
 */
enum RelDir {
    Left(-1), Forward(0), Right(1);

    public final int value;

    private RelDir(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}

enum H_Richtung {
    Nord, Ost, Sued, West
};

public class HP {

    public static void main(String[] args) {
        HP hp = new HP();
    }

    public HP() {
        Population population = new Population();
        population.createRandomPopulation();
        population.createMazeFromPopulation();
        population.printModel();
    }
}

class Population {
    private ArrayList<HPModell> hpModellPopulation;

    public Population() {
        this.hpModellPopulation = new ArrayList<HPModell>();
    }

    public void createRandomPopulation() {
        for (int i = 0; i < 10; i++) {
            HPModell hpModell = new HPModell();
            hpModell.createRandomPopulation();
            this.hpModellPopulation.add(hpModell);
        }
    }

    public void printPopulation() {
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.printPopulation();
        }
    }

    public void createMazeFromPopulation() {
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.createMazeFromPopulation();
        }
    }

    public void printModel() {
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.printPopulation();
            hpModell.printMaze();
            hpModell.calcFitness();
            System.out.println(hpModell.getFitness());
        }
    }

}

class HPModell {
    private ArrayList<Node> proteins;
    private int[][] maze = new int[20][20];
    private int fitness;

    public HPModell() {
        this.proteins = new ArrayList<Node>();

    }

    public void createRandomPopulation() {
        Node lastNode = null;
        RelDir lastTwoDirections[] = new RelDir[2];
        for (int i = 0; i < 10; i++) {
            boolean isHydrophobic = Math.random() < 0.5;
            // Random direction
            RelDir direction = RelDir.values()[(int) (Math.random() * RelDir.values().length)];
            // Check if the last two directions are the same
            while ((lastTwoDirections[0] == direction) && (lastTwoDirections[1] == direction)) {
                System.out.println("Same direction -> get new direction");
                direction = RelDir.values()[(int) (Math.random() * RelDir.values().length)];
            }
            lastTwoDirections[0] = lastTwoDirections[1];
            lastTwoDirections[1] = direction;

            this.proteins.add(new Node(null, direction, isHydrophobic));
            if (lastNode != null) {
                lastNode.setNextNode(this.proteins.get(i));
            }
            lastNode = this.proteins.get(i);
        }
    }

    public void printPopulation() {
        Node currentNode = this.proteins.get(0);
        while (currentNode != null) {
            System.out.println((currentNode.getIsHydrophobic() ? "H" : "P")
                    + " " + currentNode.getDirection());
            currentNode = currentNode.getNextNode();
        }
    }

    public void createMazeFromPopulation() {
        int x = 10;
        int y = 10;
        maze = new int[20][20];
        Node currentNode = this.proteins.get(0);

        H_Richtung lastH_Richtung = H_Richtung.Nord; // Starting direction

        List<Node> nodes = new ArrayList<Node>(); // To check overlapping

        while (currentNode != null) {
            // Check overlapping
            for (Node node : nodes) {
                if (node.getX() == x && node.getY() == y) {
                    System.out.println("Overlapping");
                    return;
                }
            }
            maze[x][y] = currentNode.getIsHydrophobic() ? TempNodeType.Hydro.getValue() : TempNodeType.Polar.getValue();
            currentNode.setX(x);
            currentNode.setY(y);
            nodes.add(currentNode);

            int initRichtung = lastH_Richtung.ordinal();
            int relRichtung = currentNode.getDirection().getValue();
            int intFromEnums = initRichtung + relRichtung;
            int intFromEnumsMod = intFromEnums % 4;
            lastH_Richtung = H_Richtung.values()[intFromEnumsMod < 0 ? intFromEnumsMod + 4 : intFromEnumsMod];
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

            currentNode = currentNode.getNextNode();
            continue;

        }
    }

    public void calcFitness() {
        Node currentNode = this.proteins.get(0);
        int lastX, lastY;
        lastX = currentNode.getX();
        lastY = currentNode.getY();
        this.fitness = 0;
        while (currentNode != null) {
            if (currentNode.getIsHydrophobic()) {
                // Check if left, right, up or down is hydrophobic (without the cords from the
                // last node)

                if (maze[currentNode.getX() - 1][currentNode.getY()] == TempNodeType.Hydro.getValue()) {
                    if (currentNode.getX() - 1 != lastX && currentNode.getY() != lastY) {
                        fitness--;
                    }
                } else if (maze[currentNode.getX() + 1][currentNode.getY()] == TempNodeType.Hydro.getValue()) {
                    if (currentNode.getX() + 1 != lastX && currentNode.getY() != lastY) {
                        fitness--;
                    }
                } else if (maze[currentNode.getX()][currentNode.getY() - 1] == TempNodeType.Hydro.getValue()) {
                    if (currentNode.getX() != lastX && currentNode.getY() - 1 != lastY) {
                        fitness--;
                    }
                } else if (maze[currentNode.getX()][currentNode.getY() + 1] == TempNodeType.Hydro.getValue()) {
                    if (currentNode.getX() != lastX && currentNode.getY() + 1 != lastY) {
                        fitness--;
                    }
                }
            }
            lastX = currentNode.getX();
            lastY = currentNode.getY();
            currentNode = currentNode.getNextNode();
        }

    }

    public int getFitness() {
        return this.fitness;
    }

    public void printMaze() {
        for (int i = 0; i < maze.length; i++) {
            System.out.print("|");
            for (int j = 0; j < maze[i].length; j++) {
                System.out.print(maze[i][j] == TempNodeType.Hydro.getValue() ? "H "
                        : maze[i][j] == TempNodeType.Polar.getValue() ? "P "
                                : "  ");
                System.out.print("|");
            }
            System.out.println();
        }
    }
}

enum TempNodeType {
    Hydro(3), Polar(4);

    public final int value;

    private TempNodeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}

class Node {
    private Node nextNode;
    private RelDir direction;
    private boolean isHydrophobic;
    private int x, y;

    public Node(Node nextNode, RelDir direction, boolean isHydrophobic) {
        this.nextNode = nextNode;
        this.direction = direction;
        this.isHydrophobic = isHydrophobic;
    }

    public Node getNextNode() {
        return this.nextNode;
    }

    public RelDir getDirection() {
        return this.direction;
    }

    public boolean getIsHydrophobic() {
        return this.isHydrophobic;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setDirection(RelDir direction) {
        this.direction = direction;
    }

}