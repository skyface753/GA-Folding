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

    private ArrayList<Node> path;

    public static void main(String[] args) {
        HP hp = new HP();
    }

    public HP() {
        this.path = new ArrayList<Node>();
        // Random path
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
            this.path.add(new Node(null, direction, isHydrophobic));
            if (lastNode != null) {
                lastNode.setNextNode(this.path.get(i));
            }
            lastNode = this.path.get(i);
        }

        // Print path
        Node currentNode = this.path.get(0);
        while (currentNode != null) {
            System.out.println((currentNode.getIsHydrophobic() ? "H" : "P")
                    + " " + currentNode.getDirection());
            currentNode = currentNode.getNextNode();
        }

        // Create maze from path
        int[][] maze = new int[20][20];
        currentNode = this.path.get(0);
        int x = 10;
        int y = 10;
        boolean isTheFirst = true;

        H_Richtung lastH_Richtung = H_Richtung.Nord;
        // RelDir last = currentNode.getDirection();

        while (currentNode != null) {
            maze[x][y] = isTheFirst ? currentNode.getIsHydrophobic() ? -1 : -2
                    : currentNode.getIsHydrophobic() ? 1 : 2;
            isTheFirst = false;

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

        // print maze
        for (

                int i = 0; i < maze.length; i++) {
            System.out.print("|");
            for (int j = 0; j < maze[i].length; j++) {
                System.out.print(maze[i][j] == 0 ? " "
                        : maze[i][j] == -1 ? "Q"
                                : maze[i][j] == -2 ? "R"
                                        : maze[i][j] == 1 ? "H" : "P");
                System.out.print("|");
            }
            System.out.println();
        }
        // Print maze formatted
        // for (int i = 0; i < maze.length; i++) {
        // System.out.print("|");
        // for (int j = 0; j < maze[i].length; j++) {
        // System.out.print(maze[i][j] == 0 ? " " : maze[i][j] == -1 ? "S" : maze[i][j]
        // == 1 ? "H" : "P");
        // System.out.print("|");
        // }
        // System.out.println();
        // }

    }
}

class Node {
    private Node nextNode;
    private RelDir direction;
    private boolean isHydrophobic;

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

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setDirection(RelDir direction) {
        this.direction = direction;
    }

}