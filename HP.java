import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HP
 * https://arxiv.org/pdf/1608.05855.pdf
 * https://www.sciencedirect.com/science/article/pii/S147692711000040X
 */

public class HP {
    Population p;

    public static void main(String[] args) {
        HP hp = new HP();
        hp.genAlgo();
    }

    public HP() {
        p = new Population();

    }

    // public void test() {
    // p.creatTestPop();
    // p.printModel();
    // }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    ArrayList<String[]> dataLines = new ArrayList<>();

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated() throws IOException {
        File csvOutputFile = new File("/tmp/ga/output.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }

    }

    public void genAlgo() {
        p.createRandomPopulation(100);

        // population.printModel();
        double avgFitness = p.evaluation();
        int maxGeneration = 100;
        dataLines = new ArrayList<>();
        dataLines.add(new String[] { "Generation", "AvgFitness", "BestFitness", "BesteFitnessOverAll",
                "HydroContactsOverAll", "BestSequenz" });
        int round = 0;
        while (avgFitness < 200 &&
                p.generation < maxGeneration) {

            round++;
            if (round % 10 == 0) {
                System.out.println("Round: " + round + " diversity: " + p.getDiversity());
            }
            p.generation++;
            p = p.selection(); // age biased replacement
            // p.crossover();
            // p.mutation();

            avgFitness = p.evaluation();
            dataLines.add(new String[] { "" + p.generation, "" + avgFitness, "" + p.besteFitnessOverAll,
                    "" + p.bestHPModell.getFitness(),
                    "" + p.anzahlHydroContactsOverAll,
                    "" + p.bestHPModell.toString() });
            p.exportBestAsImage();
            // scanner.nextLine();
        }

        try {
            givenDataArray_whenConvertToCSV_thenOutputCreated();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
