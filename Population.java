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
    // double bestFitness = 0;
    // String bestSequenz = "";

    public Population() {
        this.hpModellPopulation = new ArrayList<HPModell>();
    }

    // public void creatTestPop() {
    // String[] sequenzes = {
    // "HFPRPRHFPRHF", // Own Example = -2; 0 Overlaps
    // "HFPRHFPRPRHLHLPRHFPLPRHLPRHLHLPRPRHFPRHF", // GA01 Example 1 = -4; 0
    // Overlaps
    // "HFPRHFPRPRHLHLPRHFPRPRHLPRHLHLPRPRHFPRHF", // GA01 Example 2 = -9; 0
    // Overlaps
    // "HLPLPFPLHLHRPRHF", // GAP00 Praktikum Faltung 1 = -2; 0 Overlaps
    // "HFPLHLHFPRPRPRHF", // GAP00 Praktikum Faltung 2 = -3; 1 Overlap
    // "PFPRPRPR", // Test = 0; 0 Overlaps
    // "HFHRHRHRHRHRHRHRHFHLHLHLHF" // Test = -22; 9 Overlaps
    // };
    // for (String sequenz : sequenzes) {
    // HPModell hpModell = new HPModell();
    // hpModell.createFromSequenz(sequenz);
    // this.hpModellPopulation.add(hpModell);
    // }
    // }

    public void createRandomPopulation(int size) {
        for (int i = 0; i < size; i++) {
            HPModell hpModell = new HPModell();
            hpModell.createRandomHPModell(20);
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
            // if (hpModell.getFitness() > this.bestFitness) {
            // this.bestFitness = hpModell.getFitness();
            // this.bestSequenz = hpModell.toString();
            // }
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

    public void exportBestAsImage() {
        this.bestHPModell.exportAsImage();
    }

}