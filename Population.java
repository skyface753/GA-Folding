import java.util.ArrayList;

class Population {
    private ArrayList<HPModell> hpModellPopulation;

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
            hpModell.createRandomHPModell(20);
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

}