import java.util.ArrayList;

class Population {
    private ArrayList<HPModell> hpModellPopulation;
    int generation = 0;
    double bestFitness = 0;
    String bestSequenz = "";

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
            HPModell hpModell = new HPModell();
            hpModell.createFromSequenz(sequenz);
            this.hpModellPopulation.add(hpModell);
        }
    }

    public void createRandomPopulation(int size) {
        for (int i = 0; i < size; i++) {
            HPModell hpModell = new HPModell();
            hpModell.createRandomHPModell(40);
            this.hpModellPopulation.add(hpModell);
        }
    }

    // public void printPopulation() {
    // for (HPModell hpModell : this.hpModellPopulation) {
    // hpModell.printPopulation();
    // }
    // }

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
            hpModell.exportAsImage();
        }
    }

    public double evaluation() {
        double avgFitness = 0;
        bestFitness = 0;
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.calcFitness();
            avgFitness += hpModell.getFitness();
            if (hpModell.getFitness() > this.bestFitness) {
                this.bestFitness = hpModell.getFitness();
                this.bestSequenz = hpModell.toString();
            }
        }
        avgFitness = avgFitness / this.hpModellPopulation.size();
        // System.out.println("Generation: " + this.generation);
        // System.out.println("Average Fitness: " + avgFitness);
        return avgFitness;
    }

    public Population selection() {
        // Gesamtfitness berechnen
        double totalFitness = 0;
        for (HPModell i : this.hpModellPopulation) {
            i.calcFitness();
            totalFitness += i.getFitness();
        }
        // Prozentsatz der Fitness berechnen
        for (HPModell i : this.hpModellPopulation) {
            i.fitnessProzent = i.getFitness() / totalFitness;
        }
        ArrayList<HPModell> newPopulation = new ArrayList<>();
        // Individuen auswählen
        ArrayList<HPModell> gluecksrad = new ArrayList<>();
        for (HPModell i : this.hpModellPopulation) {
            for (int j = 0; j < i.fitnessProzent * 100; j++) {
                gluecksrad.add(i);
            }

        }
        for (int i = 0; i < this.hpModellPopulation.size(); i++) {
            int index = (int) (Math.random() * gluecksrad.size());
            newPopulation.add(gluecksrad.get(index));
        }
        this.hpModellPopulation = newPopulation;

        return this;
    }

}