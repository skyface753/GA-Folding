
public class GA03 {
    public void einfacherGenetischerAlgorithmus(int maxGeneration) {

        Population p = new Population();
        double avgFitness = p.evaluation();
        while (avgFitness < 200 &&
                p.generation < maxGeneration) {
            p.generation++;
            p = p.selection(); // age biased replacement
            p.crossover();
            p.mutation();

            avgFitness = p.evaluation();
            // scanner.nextLine();
        }
        p.print();
        // scanner.close();
    }

    public static void main(String[] args) {
        new GA03().einfacherGenetischerAlgorithmus(100);
    }

}
