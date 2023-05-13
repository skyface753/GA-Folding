import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HP
 * https://arxiv.org/pdf/1608.05855.pdf
 * https://www.sciencedirect.com/science/article/pii/S147692711000040X
 */

public class HP {
    Population p;

    public static String outputFolder = "/tmp/ga/";

    public static void main(String[] args) {
        HP hp = new HP();
        hp.genAlgo(true);
        // hp.test();
    }

    public HP() {
        p = new Population();

    }

    // public void test() {
    // p.creatTestPop1();
    // p.printModel();
    // System.out.println("Diversity: " + p.getDiversity());
    // p.crossover();
    // p.printModel();
    // System.out.println("Diversity: " + p.getDiversity());
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

    public void genAlgo(boolean withCrossAndMutation) {
        if (withCrossAndMutation) {
            System.out.println("Genetic Algorithm with crossover and mutation");
        } else {
            System.out.println("Genetic Algorithm without crossover and mutation");
        }
        deleteFolder(new File(outputFolder));

        int populationSize = 100;
        p.createRandomPopulation(populationSize);

        // population.printModel();
        double avgFitness = p.evaluation();
        int maxGeneration = 100;
        dataLines = new ArrayList<>();
        dataLines.add(new String[] { "Generation", "AvgFitness", "BestFitness", "BesteFitnessOverAll",
                "HydroContactsOverAll", "BestSequenz" });
        // Scanner scanner = new Scanner(System.in);
        while (avgFitness < 8 &&
                p.generation < maxGeneration) {
            // if (p.anzahlHPModelle() < populationSize) {
            // throw new RuntimeException("Population size is too small");
            // }
            // if (p.generation % 10 == 0) {
            // System.out.println("Round: " + p.generation + " diversity: " +
            // p.getDiversity());
            // // Awating user input
            // // scanner.nextLine();
            // }
            p.generation++;
            p = p.selection(); // age biased replacement
            // System.out.println("Diversity vorher: " + p.getDiversity());
            if (withCrossAndMutation) {

                p.crossover();
                p.mutation();
            }
            // System.out.println("Diversity nachher: " + p.getDiversity());

            avgFitness = p.evaluation();
            String[] newLine = new String[] { "" + p.generation, "" + avgFitness,
                    "" + p.bestHPModell.getFitness(),
                    "" + p.besteFitnessOverAll,
                    "" + p.anzahlHydroContactsOverAll,
                    "" + p.bestHPModell.toString() };
            dataLines.add(newLine);

            p.exportBestAsImage();

        }
        // scanner.close();
        System.out.println("Beste Fitness: " + p.bestHPModell.getFitness());
        System.out.println("Beste Sequenz: " + p.bestHPModell.toString());
        System.out.println("Generation: " + p.generation);

        try {
            givenDataArray_whenConvertToCSV_thenOutputCreated();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { // some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
