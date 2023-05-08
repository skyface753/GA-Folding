
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Population {

    static int anzahlIndividuen = 20;

    private List<Individual> population = new ArrayList<>();
    public int generation = 0;

    public Population() {
        for (int i = 0; i < anzahlIndividuen; i++) {
            population.add(new Individual());
        }
    }

    public double evaluation() {

        for (Individual i : population) {
            i.calcFitness();
        }

        double avgFitness = 0;
        for (Individual i : population) {
            avgFitness += i.fitness;
        }
        avgFitness /= population.size();
        System.out.println("Generation " + generation + " avgFitness " + avgFitness);
        generation++;
        return avgFitness;

    }

    public Population selection() {
        // Gesamtfitness berechnen
        double totalFitness = 0;
        for (Individual i : population) {
            i.calcFitness();
            totalFitness += i.fitness;
        }
        // Prozentsatz der Fitness berechnen
        for (Individual i : population) {
            i.fitnessProzent = i.fitness / totalFitness;
        }
        List<Individual> newPopulation = new ArrayList<>();
        // Individuen auswählen
        List<Individual> gluecksrad = new ArrayList<>();
        for (Individual i : population) {
            for (int j = 0; j < i.fitnessProzent * 100; j++) {
                gluecksrad.add(i);
            }

        }
        for (int i = 0; i < anzahlIndividuen; i++) {
            int index = (int) (Math.random() * gluecksrad.size());
            newPopulation.add(gluecksrad.get(index));
        }
        population = newPopulation;
        return this;
    }

    static int geneLength = 18;

    public void crossover() {
        // Crossover probability 25%
        for (int i = 0; i < population.size(); i++) {
            if (Math.random() < 0.25) {
                int j = (int) (Math.random() * population.size());
                int crossOverPoint = (int) (Math.random() * geneLength);
                byte[] temp = Arrays.copyOfRange(population.get(i).genes, 0, crossOverPoint);
                byte[] temp2 = Arrays.copyOfRange(population.get(j).genes, crossOverPoint, geneLength);
                byte[] temp3 = new byte[geneLength];
                byte[] temp4 = new byte[geneLength];
                System.arraycopy(temp, 0, temp3, 0, temp.length);
                System.arraycopy(temp2, 0, temp3, temp.length, temp2.length);
                System.arraycopy(temp2, 0, temp4, 0, temp2.length);
                System.arraycopy(temp, 0, temp4, temp2.length, temp.length);
                population.get(i).genes = temp3;
                population.get(j).genes = temp4;

            }
        }
    }

    public void mutation() {
        // Mutation probability 1%
        for (Individual i : population) {
            for (int j = 0; j < i.genes.length; j++) {
                if (Math.random() <= 0.01) {
                    i.genes[j] = (byte) (1 - i.genes[j]);
                }
            }
        }

    }

    public void print() {
        // Sort by fitness
        population.sort((i1, i2) -> Double.compare(i2.fitness, i1.fitness));
        evaluation();
        for (Individual i : population) {
            System.out.println(i.toString() + "\n");

        }
        System.out.println();
        generation++;
    }
}
