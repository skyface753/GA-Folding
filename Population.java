import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class Population {
    private ArrayList<HPModell> hpModellPopulation;
    int generation = 0;
    HPModell bestHPModell;
    double besteFitnessOverAll = 0;
    int anzahlHydroContactsOverAll = 0;
    int anzahlOverlapsOverAll = 0;

    public Population() {
        this.hpModellPopulation = new ArrayList<HPModell>();
    }

    public void creatTestPop() {
        String[] sequenzes = {
                "HFPRPRHFPRHF", // Own Example = -2; 0 Overlaps
                "HFPRHFPRPRHLHLPRHFPLPRHLPRHLHLPRPRHFPRHF", // GA01 Example 1 = -4; 0 Overlaps
                "HFPRHFPRPRHLHLPRHFPRPRHLPRHLHLPRPRHFPRHF", // GA01 Example 2 = -9; 0 Overlaps
                "HLPLPFPLHLHRPRHF", // GAP00 Praktikum Faltung 1 = -2; 0 Overlaps
                "HFPLHLHFPRPRPRHF", // GAP00 Praktikum Faltung 2 = -3; 1 Overlap
                "PFPRPRPR", // Test = 0; 0 Overlaps
                "HFHRHRHRHRHRHRHRHFHLHLHLHF" // Test = -22; 9 Overlaps
        };
        for (String sequenz : sequenzes) {
            HPModell hpModell = new HPModell(sequenz);
            this.hpModellPopulation.add(hpModell);
        }
    }

    public void creatTestPop1() {
        String[] sequenzes = {
                "HLPLPFPLHLPLPFPL",
                "HLHRPRHFHLHRPRHF"
        };
        for (String sequenz : sequenzes) {
            HPModell hpModell = new HPModell(sequenz);
            this.hpModellPopulation.add(hpModell);
        }
    }

    public void createRandomPopulation(int size) {
        for (int i = 0; i < size; i++) {
            HPModell hpModell = new HPModell();
            hpModell.createRandomHPModell();
            this.hpModellPopulation.add(hpModell);
        }
    }

    public int getDiversity() {
        Set<String> set = new HashSet<String>();
        for (HPModell hpModell : this.hpModellPopulation) {
            set.add(hpModell.toString());
        }
        return set.size();
    }

    public void mutation() {
        int anzahlMutationen = (int) (this.hpModellPopulation.size() * HPModell.anzahlNodes * 0.01);
        anzahlMutationen = Math.max(1, anzahlMutationen); // mindestens 1 Mutation
        for (int i = 0; i < anzahlMutationen; i++) {
            int randomIndex = (int) (Math.random() * this.hpModellPopulation.size());
            this.hpModellPopulation.get(randomIndex).mutation();
        }
    }

    public void crossover() {
        // 25% der Population
        int anzahlCrossover = (int) (this.hpModellPopulation.size() * 0.25);
        anzahlCrossover = Math.max(1, anzahlCrossover); // mindestens 1 Crossover

        // Paarweise auswählen
        for (int i = 0; i < anzahlCrossover; i = i + 2) {
            if (i == anzahlCrossover - 1) {
                // 50% Chance, dass das letzte HPModell auch noch ein Crossover bekommt
                if (Math.random() < 0.5)
                    break;
            }
            int randomIndex1 = (int) (Math.random() * this.hpModellPopulation.size());

            int randomIndex2 = (int) (Math.random() * this.hpModellPopulation.size());
            while (randomIndex1 == randomIndex2) {
                randomIndex2 = (int) (Math.random() * this.hpModellPopulation.size());
            }
            HPModell hpModell1 = this.hpModellPopulation.get(randomIndex1);
            HPModell hpModell2 = this.hpModellPopulation.get(randomIndex2);
            ArrayList<Node> proteins1 = hpModell1.getProteins();
            ArrayList<Node> proteins2 = hpModell2.getProteins();
            ArrayList<Node> proteins1New = new ArrayList<Node>();
            ArrayList<Node> proteins2New = new ArrayList<Node>();
            int cutIndex = (int) (Math.random() * HPModell.anzahlNodes);

            for (int j = 0; j < cutIndex; j++) {
                proteins1New.add(proteins1.get(j));
                proteins2New.add(proteins2.get(j));
            }
            for (int j = cutIndex; j < HPModell.anzahlNodes; j++) {
                proteins1New.add(proteins2.get(j));
                proteins2New.add(proteins1.get(j));
            }
            hpModell1.setProteins(proteins1New);
            hpModell2.setProteins(proteins2New);
        }
    }

    public void printModel() {
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.printPopulation();
            hpModell.calcFitness();
            System.out.println("HydroContacts: " + hpModell.getHydroContacts());
            System.out.println("Overlaps: " + hpModell.getOverlaps());
            System.out.println("Fitness: " + hpModell.getFitness());
            System.out.println();
        }
    }

    public void allToImages() {
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.exportAsImage(generation);
        }
    }

    public double evaluation() {
        double avgFitness = 0;
        bestHPModell = this.hpModellPopulation.get(0);
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.calcFitness();
            avgFitness += hpModell.getFitness();
            if (hpModell.getFitness() > bestHPModell.getFitness()) {
                bestHPModell = hpModell;
            }
            if (hpModell.getFitness() > this.besteFitnessOverAll) {
                this.besteFitnessOverAll = hpModell.getFitness();
            }
            if (hpModell.getHydroContacts() > this.anzahlHydroContactsOverAll) {
                this.anzahlHydroContactsOverAll = hpModell.getHydroContacts();
            }
            if (hpModell.getOverlaps() > this.anzahlOverlapsOverAll) {
                this.anzahlOverlapsOverAll = hpModell.getOverlaps();
            }
        }
        avgFitness = avgFitness / this.hpModellPopulation.size();
        return avgFitness;
    }

    public Population selection() {
        RandomSelector randomSelector = new RandomSelector();
        for (int i = 0; i < this.hpModellPopulation.size(); i++) {
            this.hpModellPopulation.get(i).calcFitness();
            randomSelector.add(this.hpModellPopulation.get(i).getFitness(), this.hpModellPopulation.get(i).toString());
        }
        ArrayList<HPModell> newPopulation = new ArrayList<>();
        int anzahl = this.hpModellPopulation.size();
        for (int i = 0; i < anzahl; i++) {
            HPModell hpModell = new HPModell(randomSelector.next());
            newPopulation.add(hpModell);
        }

        this.hpModellPopulation = newPopulation;
        return this;
    }

    public void exportBestAsImage() {
        this.bestHPModell.exportAsImage(generation);
    }

    public int anzahlHPModelle() {
        return this.hpModellPopulation.size();
    }

}