import java.util.ArrayList;
import java.util.Arrays;

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
    Population population;

    public static void main(String[] args) {
        HP hp = new HP();
        hp.test();
    }

    public HP() {
        population = new Population();

    }

    public void test() {
        population.creatTestPop();
        population.createMazeFromPopulation();
        population.printModel();
    }
}

class Population {
    private ArrayList<HPModell> hpModellPopulation;

    public Population() {
        this.hpModellPopulation = new ArrayList<HPModell>();
    }

    public void creatTestPop() {
        String[] sequenzes = { "HFPRPRHFPRHF", // Own Example = -2
                "HFPRHFPRPRHLHLPRHFPLPRHLPRHLHLPRPRHFPRHF", // GA01 Example 1 = -4
                "HFPRHFPRPRHLHLPRHFPRPRHLPRHLHLPRPRHFPRHF", // GA01 Example 2 = -9
                "HLPLPFPLHLHRPRHF", // GAP00 Praktikum Faltung 1 = -3
                "HFPLHLHFPRPRPRHF", // GAP00 Praktikum Faltung 2 = -2
                "PFPRPRPR" // Test = 0
        };
        for (String sequenz : sequenzes) {
            HPModell hpModell = new HPModell();
            hpModell.createFromSequenz(sequenz);
            this.hpModellPopulation.add(hpModell);
        }
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

    public void createRandomPopulation() {
        // Node lastNode = null;
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

            this.proteins.add(new Node(direction, isHydrophobic));

        }
    }

    public void printPopulation() {
        for (Node node : this.proteins) {
            System.out.println((node.getIsHydrophobic() ? "H" : "P")
                    + " " + node.getDirection());
        }
    }

    public void createMazeFromPopulation() {
        int x = 10;
        int y = 10;
        maze = new int[20][20];
        for (int i = 0; i < 20; i++) {
            Arrays.fill(maze[i], -1);
        }

        H_Richtung lastH_Richtung = H_Richtung.Nord; // Starting direction

        for (Node currentNode : this.proteins) {

            maze[x][y] = currentNode.getIsHydrophobic() ? 1 : 0;
            currentNode.setX(x);
            currentNode.setY(y);

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

            continue;

        }
    }

    public void calcFitness() {

        this.fitness = 0;
        int firstThree = 3;
        int x = 10;
        int y = 10;
        maze = new int[20][20];
        for (int i = 0; i < 20; i++) {
            Arrays.fill(maze[i], -1);
        }
        int lastX = x;
        int lastY = y;

        H_Richtung lastH_Richtung = H_Richtung.Nord; // Starting direction

        for (Node currentNode : this.proteins) {

            maze[x][y] = currentNode.getIsHydrophobic() ? 1 : 0;
            currentNode.setX(x);
            currentNode.setY(y);

            int initRichtung = lastH_Richtung.ordinal();
            int relRichtung = currentNode.getDirection().getValue();
            int intFromEnums = initRichtung + relRichtung;
            int intFromEnumsMod = intFromEnums % 4;
            lastH_Richtung = H_Richtung.values()[intFromEnumsMod < 0 ? intFromEnumsMod + 4 : intFromEnumsMod];

            if (firstThree <= 0 && currentNode.getIsHydrophobic()) {
                // Check if left, right, up or down is hydrophobic (without the cords from the
                // last node)
                int left = maze[currentNode.getX()][currentNode.getY() - 1];
                int right = maze[currentNode.getX()][currentNode.getY() + 1];
                int up = maze[currentNode.getX() - 1][currentNode.getY()];
                int down = maze[currentNode.getX() + 1][currentNode.getY()];

                if (left == 1 && lastY != currentNode.getY() - 1) {
                    this.fitness--;
                }
                if (right == 1 && lastY != currentNode.getY() + 1) {
                    this.fitness--;
                }
                if (up == 1 && lastX != currentNode.getX() - 1) {
                    this.fitness--;
                }
                if (down == 1 && lastX != currentNode.getX() + 1) {
                    this.fitness--;
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

            lastX = currentNode.getX();
            lastY = currentNode.getY();
            firstThree--;
            continue;

        }

    }

    public int getFitness() {
        return this.fitness;
    }

    public void printMaze() {
        for (int i = 0; i < maze.length; i++) {
            System.out.print("|");
            for (int j = 0; j < maze[i].length; j++) {
                System.out.print(maze[i][j] == 1 ? "H "
                        : maze[i][j] == 0 ? "P "
                                : "  ");
                System.out.print("|");
            }
            System.out.println();
        }
    }
}

// 0 = hydrophil, "white"
// 1 = hydrophob, "black"

// enum TempNodeType {
// Hydro(3), Polar(4);

// public final int value;

// private TempNodeType(int value) {
// this.value = value;
// }

// public int getValue() {
// return this.value;
// }
// }

class Node {
    // private Node nextNode;
    private RelDir direction;
    private boolean isHydrophobic;
    private int x, y;

    public Node(RelDir direction, boolean isHydrophobic) {
        this.direction = direction;
        this.isHydrophobic = isHydrophobic;
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

    // public void setNextNode(Node nextNode) {
    // this.nextNode = nextNode;
    // }

    public void setDirection(RelDir direction) {
        this.direction = direction;
    }

}