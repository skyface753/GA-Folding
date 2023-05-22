import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import Helpers.Helpers.RelDir;

class Population {
    private ArrayList<HPModell> hpModellPopulation;
    int generation = 0;
    HPModell bestHPModell;
    double besteFitnessOverAll = 0;
    int anzahlHydroContactsOverAll = 0;
    int anzahlOverlapsOverAll = 0;
    double avgFitness = 0;
    double sd = 0;

    public Population() {
        this.hpModellPopulation = new ArrayList<HPModell>();
    }

    public void createFromSequenz(String currSeq, int anzahlPopulation) {
        HPModell.anzahlNodes = currSeq.length();
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
            HPModell hpModell = new HPModell(seq20permutations[i]);
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

    public int mutation(double mutationRate) {
        int anzahlMutationen = (int) (this.hpModellPopulation.size() * HPModell.anzahlNodes * mutationRate);
        anzahlMutationen = Math.max(1, anzahlMutationen); // mindestens 1 Mutation
        for (int i = 0; i < anzahlMutationen; i++) {
            int randomIndex = (int) (Math.random() * this.hpModellPopulation.size());
            this.hpModellPopulation.get(randomIndex).mutateDirection();
        }
        return anzahlMutationen;
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
            RelDir[] relDirs1 = hpModell1.getDirections();
            RelDir[] relDirs2 = hpModell2.getDirections();
            RelDir[] relDirs1New = new RelDir[HPModell.anzahlNodes];
            RelDir[] relDirs2New = new RelDir[HPModell.anzahlNodes];
            int cutIndex = (int) (Math.random() * HPModell.anzahlNodes);
            for (int j = 0; j < cutIndex; j++) {
                relDirs1New[j] = relDirs1[j];
                relDirs2New[j] = relDirs2[j];
            }
            for (int j = cutIndex; j < HPModell.anzahlNodes; j++) {
                relDirs1New[j] = relDirs2[j];
                relDirs2New[j] = relDirs1[j];
            }
            hpModell1.setDirections(relDirs1New);
            hpModell2.setDirections(relDirs2New);

        }
    }

    // public void printModel() {
    // for (HPModell hpModell : this.hpModellPopulation) {
    // // hpModell.printPopulation();
    // hpModell.calcFitness();
    // System.out.println("HydroContacts: " + hpModell.getHydroContacts());
    // System.out.println("Overlaps: " + hpModell.getOverlaps());
    // System.out.println("Fitness: " + hpModell.getFitness());
    // System.out.println();
    // }
    // }

    public void allToImages() {
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.exportAsImage(generation);
        }
    }

    public double evaluation() {
        // double avgFitness = 0;
        bestHPModell = this.hpModellPopulation.get(0);
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.calcFitness();
            avgFitness += hpModell.getFitness();
            if (hpModell.getFitness() > bestHPModell.getFitness() && hpModell.getOverlaps() == 0) {
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
        // this.avgFitness = avgFitness;
        return avgFitness;
    }

    private void sigmaScale() {
        // standard deviation
        // double sd = 0;
        // double avgFitness = 0;
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.calcFitness();
            avgFitness += hpModell.getFitness();
        }
        avgFitness = avgFitness / this.hpModellPopulation.size();
        for (HPModell hpModell : this.hpModellPopulation) {
            sd += Math.pow(hpModell.getFitness() - avgFitness, 2);
        }
        sd = Math.sqrt(sd / this.hpModellPopulation.size());
        // System.out.println("sd: " + sd);
        double c = 2;
        // f` = max(f-(avgFitness-c*sd),0)
        for (HPModell hpModell : this.hpModellPopulation) {
            double fitness = hpModell.getFitness();
            fitness = Math.max(fitness - (avgFitness - c * sd), 0);
            hpModell.fitnessScaled = fitness;
        }
    }

    public Population selection(boolean withSigmaScaling, boolean elitismus) {
        RandomSelector randomSelector = new RandomSelector();
        if (withSigmaScaling) {
            sigmaScale();
        }
        for (int i = 0; i < this.hpModellPopulation.size(); i++) {
            if (withSigmaScaling) {
                // 1 + (fitnessScaled - avgFitness) / (2 * sd)
                double fitnessScaled = this.hpModellPopulation.get(i).fitnessScaled;
                double expValue = Math.max(1 + (fitnessScaled - avgFitness) / (2 * sd), 0.1);
                randomSelector.add(expValue, this.hpModellPopulation.get(i).toString());
            } else {
                this.hpModellPopulation.get(i).calcFitness();
                randomSelector.add(this.hpModellPopulation.get(i).getFitness(),
                        this.hpModellPopulation.get(i).toString());
            }
            // randomSelector.add(this.hpModellPopulation.get(i).getFitness(),
            // this.hpModellPopulation.get(i).toString());
        }
        ArrayList<HPModell> newPopulation = new ArrayList<>();
        int anzahl = this.hpModellPopulation.size();
        if (elitismus) {
            anzahl--;
            newPopulation.add(new HPModell(bestHPModell.toString()));
        }
        for (int i = 0; i < anzahl; i++) {
            HPModell hpModell = new HPModell(randomSelector.next());
            newPopulation.add(hpModell);
        }

        this.hpModellPopulation = newPopulation;
        return this;
    }

    public Population turnierSelection(boolean elitismus) {
        int anzahl = this.hpModellPopulation.size();
        ArrayList<HPModell> newPopulation = new ArrayList<>();
        Random r = new Random();
        int anzahlKandidaten = r.nextInt(anzahl - 2) + 2; // mindestens 2 Kandidaten, maximal alle
        double t = 0.90;
        boolean searchForBest = true;
        if (anzahlKandidaten == 2 && (Math.random() > t)) {
            searchForBest = false;
        } // 90% Chance, dass der beste gewinnt

        if (elitismus) {
            anzahl--;
            newPopulation.add(new HPModell(bestHPModell.toString()));
        }
        // Reset aller Fitness-Werte
        for (HPModell hpModell : this.hpModellPopulation) {
            hpModell.resetFitness();
        }
        for (int i = 0; i < anzahl; i++) {

            Set<Integer> set = new HashSet<>();
            while (set.size() < anzahlKandidaten) {
                set.add(r.nextInt(anzahl));
            }
            HPModell tunierWinner = this.hpModellPopulation.get(set.iterator().next());
            for (Integer j : set) {
                if (searchForBest) {
                    if (this.hpModellPopulation.get(j).getFitness() > tunierWinner.getFitness()) {
                        tunierWinner = this.hpModellPopulation.get(j);
                    }
                } else {
                    if (this.hpModellPopulation.get(j).getFitness() < tunierWinner.getFitness()) {
                        tunierWinner = this.hpModellPopulation.get(j);
                    }
                }
            }
            HPModell hpModell = new HPModell(tunierWinner.toString());
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