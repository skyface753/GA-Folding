import java.util.HashMap;
import java.util.HashSet;

/**
 * P01OhneOverlap
 */
public class P01HashSeh {

    public static void main(String[] args) {
        PopulationOO population = new PopulationOO();
        // population.evaluation();
        // for (int i = 0; i < 100; i++) {
        // population.selection();
        // population.evaluation();
        // }
    }
}

class PopulationOO {
    private HPModellOO[] hpModellPopulation;

    public PopulationOO() {
        this.hpModellPopulation = new HPModellOO[100];
        for (int i = 0; i < this.hpModellPopulation.length; i++) {
            // Create a random HPModell
            String sequenz = "";
            int x = 20;
            int y = 20;
            for (int j = 0; j < 10; j++) {
                int hydrophob = (int) (Math.random() * 2);
                if (hydrophob == 0) {
                    sequenz += "H";
                } else {
                    sequenz += "P";
                }
                sequenz += x;
                sequenz += y;
                int reldirint = (int) (Math.random() * 4); // 0, 1, 2, 3
                switch (reldirint) {
                    case 0:
                        x--;
                        break;
                    case 1:
                        x++;
                        break;
                    case 2:
                        y--;
                        break;
                    case 3:
                        y++;
                        break;

                }

            }
            System.out.println(sequenz);
            this.hpModellPopulation[i] = new HPModellOO(sequenz);
        }
    }
}

class HPModellOO {
    private HashMap<PointOO, NodeOO> nodes;
    // private HashSet<PointOO> points;

    // Sequenz: "H11P12P13P23H22H21"
    public HPModellOO(String sequenz) {
        this.nodes = new HashMap<PointOO, NodeOO>();
        for (int i = 0; i < sequenz.length(); i = i + 3) {
            int x = Integer.parseInt(sequenz.substring(i + 1, i + 2));
            int y = Integer.parseInt(sequenz.substring(i + 2, i + 3));
            PointOO point = new PointOO(x, y);
            this.nodes.put(point, new NodeOO());
        }
    }
}

class PointOO {
    int x;
    int y;

    public PointOO(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class NodeOO {
    private boolean isHydrophob;

    public NodeOO() {
        this.isHydrophob = false;
    }

    public boolean isHydrophob() {
        return this.isHydrophob;
    }

    public void setHydrophob(boolean isHydrophob) {
        this.isHydrophob = isHydrophob;
    }
}