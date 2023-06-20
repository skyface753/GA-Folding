import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import Helpers.Helpers.RelDir;

class Population {
    private ArrayList<Folding> foldingPopulation;
    int generation = 0;
    Folding bestFolding;
    double besteFitnessOverAll = 0;
    int anzahlHydroContactsOverAll = 0;
    int anzahlOverlapsOverAll = 0;
    double avgFitness = 0;
    double sd = 0;

    public Population() {
        this.foldingPopulation = new ArrayList<Folding>();
    }

    public void createFromSequenz(String currSeq, int anzahlPopulation) {
        Folding.anzahlNodes = currSeq.length();
        String[] seq20permutations = new String[anzahlPopulation];
        for (int i = 0; i < anzahlPopulation; i++) {
            seq20permutations[i] = "";
            for (int j = 0; j < currSeq.length(); j++) {
                seq20permutations[i] += currSeq.charAt(j);
                int reldirint = (int) (Math.random() * 3); // 0, 1, 2
                switch (reldirint) {
                    case 0:
                        seq20permutations[i] += "L";
                        break;
                    case 1:
                        seq20permutations[i] += "R";
                        break;
                    case 2:
                        seq20permutations[i] += "F";
                        break;
                }
            }
        }

        for (int i = 0; i < anzahlPopulation; i++) {
            Folding folding = new Folding(seq20permutations[i]);
            this.foldingPopulation.add(folding);
        }
    }

    public int getDiversity() {
        Set<String> set = new HashSet<String>();
        for (Folding folding : this.foldingPopulation) {
            set.add(folding.toString());
        }
        return set.size();
    }

    public int mutation(double mutationRate) {
        int anzahlDirections = (int) (this.foldingPopulation.size() * Folding.anzahlNodes);
        int anzahlMutationen = (int) (this.foldingPopulation.size() * Folding.anzahlNodes * mutationRate);
        anzahlMutationen = Math.max(1, anzahlMutationen); // mindestens 1 Mutation
        for (int i = 0; i < anzahlMutationen; i++) {
            int randomIndexPlusOffset = (int) (Math.random() * anzahlDirections);
            int randomIndex = randomIndexPlusOffset % Folding.anzahlNodes;
            int randomOffset = randomIndexPlusOffset / Folding.anzahlNodes;
            this.foldingPopulation.get(randomOffset).mutateDirection(randomIndex);
        }
        return anzahlMutationen;
    }

    public void crossover() {
        // 25% der Population
        int anzahlCrossover = (int) (this.foldingPopulation.size() * 0.25);
        anzahlCrossover = Math.max(1, anzahlCrossover); // mindestens 1 Crossover

        // Paarweise auswählen
        for (int i = 0; i < anzahlCrossover; i = i + 2) {
            if (i == anzahlCrossover - 1) {
                // 50% Chance, dass die letzte Folding auch noch ein Crossover bekommt
                if (Math.random() < 0.5)
                    break;
            }
            int randomIndex1 = (int) (Math.random() * this.foldingPopulation.size());

            int randomIndex2 = (int) (Math.random() * this.foldingPopulation.size());
            while (randomIndex1 == randomIndex2) {
                randomIndex2 = (int) (Math.random() * this.foldingPopulation.size());
            }
            Folding folding1 = this.foldingPopulation.get(randomIndex1);
            Folding folding2 = this.foldingPopulation.get(randomIndex2);
            RelDir[] relDirs1 = folding1.getDirections();
            RelDir[] relDirs2 = folding2.getDirections();
            RelDir[] relDirs1New = new RelDir[Folding.anzahlNodes];
            RelDir[] relDirs2New = new RelDir[Folding.anzahlNodes];
            int cutIndex = (int) (Math.random() * Folding.anzahlNodes);
            for (int j = 0; j < cutIndex; j++) {
                relDirs1New[j] = relDirs1[j];
                relDirs2New[j] = relDirs2[j];
            }
            for (int j = cutIndex; j < Folding.anzahlNodes; j++) {
                relDirs1New[j] = relDirs2[j];
                relDirs2New[j] = relDirs1[j];
            }
            folding1.setDirections(relDirs1New);
            folding2.setDirections(relDirs2New);

        }
    }

    public void allToImages() {
        for (Folding folding : this.foldingPopulation) {
            folding.exportAsImage(generation);
        }
    }

    public double evaluation() {
        // double avgFitness = 0;
        avgFitness = 0;
        int firstIndex = 0;
        this.foldingPopulation.get(firstIndex).calcFitness();
        bestFolding = this.foldingPopulation.get(firstIndex);

        for (Folding folding : this.foldingPopulation) {
            folding.calcFitness();
            avgFitness += folding.getFitness();
            if (folding.getFitness() > bestFolding.getFitness()) {
                bestFolding = folding;
            }
            if (folding.getFitness() > this.besteFitnessOverAll) {
                this.besteFitnessOverAll = folding.getFitness();
            }
            if (folding.getHydroContacts() > this.anzahlHydroContactsOverAll) {
                this.anzahlHydroContactsOverAll = folding.getHydroContacts();
            }
            if (folding.getOverlaps() > this.anzahlOverlapsOverAll) {
                this.anzahlOverlapsOverAll = folding.getOverlaps();
            }
        }
        avgFitness = avgFitness / this.foldingPopulation.size();
        // this.avgFitness = avgFitness;
        return avgFitness;
    }

    private void sigmaScale() {
        // standard deviation
        // double sd = 0;
        // double avgFitness = 0;
        for (Folding folding : this.foldingPopulation) {
            folding.calcFitness();
            avgFitness += folding.getFitness();
        }
        avgFitness = avgFitness / this.foldingPopulation.size();
        for (Folding folding : this.foldingPopulation) {
            sd += Math.pow(folding.getFitness() - avgFitness, 2);
        }
        sd = Math.sqrt(sd / this.foldingPopulation.size());
        // System.out.println("sd: " + sd);
        double c = 2;
        // f` = max(f-(avgFitness-c*sd),0)
        for (Folding folding : this.foldingPopulation) {
            double fitness = folding.getFitness();
            fitness = Math.max(fitness - (avgFitness - c * sd), 0);
            folding.fitnessScaled = fitness;
        }
    }

    public Population selection(boolean withSigmaScaling, boolean elitismus) {
        RandomSelector randomSelector = new RandomSelector();
        if (withSigmaScaling) {
            sigmaScale();
        }
        for (int i = 0; i < this.foldingPopulation.size(); i++) {
            if (withSigmaScaling) {
                // 1 + (fitnessScaled - avgFitness) / (2 * sd)
                double fitnessScaled = this.foldingPopulation.get(i).fitnessScaled;
                double expValue = Math.max(1 + (fitnessScaled - avgFitness) / (2 * sd), 0.1);
                randomSelector.add(expValue, this.foldingPopulation.get(i).toString());
            } else {
                this.foldingPopulation.get(i).calcFitness();
                randomSelector.add(this.foldingPopulation.get(i).getFitness(),
                        this.foldingPopulation.get(i).toString());
            }
            // randomSelector.add(this.foldingPopulation.get(i).getFitness(),
            // this.foldingPopulation.get(i).toString());
        }
        ArrayList<Folding> newPopulation = new ArrayList<>();
        int anzahl = this.foldingPopulation.size();
        if (elitismus) {
            anzahl--;
            newPopulation.add(new Folding(bestFolding.toString()));
        }
        for (int i = 0; i < anzahl; i++) {
            Folding folding = new Folding(randomSelector.next());
            newPopulation.add(folding);
        }

        this.foldingPopulation = newPopulation;
        return this;
    }

    public Population turnierSelection(boolean elitismus) {
        int anzahl = this.foldingPopulation.size();
        ArrayList<Folding> newPopulation = new ArrayList<>();
        Random r = new Random();
        // int anzahlKandidaten = r.nextInt(anzahl - 2) + 2; // mindestens 2 Kandidaten,
        // maximal alle
        int anzahlKandidaten = 7; // Kandidaten pro Turnier
        double t = 0.90;
        boolean searchForBest = true;
        if (anzahlKandidaten == 2 && (Math.random() > t)) {
            searchForBest = false;
        } // 90% Chance, dass der beste gewinnt

        if (elitismus) {
            anzahl--;
            newPopulation.add(new Folding(bestFolding.toString()));
        }
        // Reset aller Fitness-Werte
        for (Folding folding : this.foldingPopulation) {
            folding.resetFitness();
        }
        for (int i = 0; i < anzahl; i++) {

            Set<Integer> set = new HashSet<>();
            while (set.size() < anzahlKandidaten) { // solange Kandidaten auswählen, bis genug da sind
                set.add(r.nextInt(anzahl));
            }
            Folding tunierWinner = this.foldingPopulation.get(set.iterator().next()); // erstes Element als
                                                                                      // Vergleichswert
            for (Integer j : set) {
                if (searchForBest) {
                    if (this.foldingPopulation.get(j).getFitness() > tunierWinner.getFitness()) {
                        tunierWinner = this.foldingPopulation.get(j);
                    }
                } else {
                    if (this.foldingPopulation.get(j).getFitness() < tunierWinner.getFitness()) {
                        tunierWinner = this.foldingPopulation.get(j);
                    }
                }
            }
            Folding folding = new Folding(tunierWinner.toString());
            newPopulation.add(folding);
        }
        this.foldingPopulation = newPopulation;
        return this;
    }

    public void exportBestAsImage() {
        this.bestFolding.exportAsImage(generation);
    }

    public int anzahlFoldingen() {
        return this.foldingPopulation.size();
    }

    // Varianz der Fitness-Werte
    private double getVarianz() {
        double varianz = 0;
        for (Folding folding : this.foldingPopulation) {
            varianz += Math.pow(folding.getFitness() - avgFitness, 2);
        }
        varianz = varianz / this.foldingPopulation.size();
        return varianz;
    }

    // Standardabweichung der Fitness-Werte
    public double getStandardabweichung() {
        double varianz = getVarianz();
        double sd = Math.sqrt(varianz);
        return sd;
    }
}