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

    public static String outputFolder = "/tmp/ga/";

    public static void main(String[] args) {
        HP hp = new HP();
        boolean withCrossAndMutation = true;
        boolean imageOutput = false;
        int anzahlHPModellProteins = 20;
        int anzahlGenerationen = 100;
        int anzahlPopulation = 100;
        if (args.length == 1) {
            if (args[0].equals("-h")) {
                System.out.println("Usage: java HP -c true -i false -p 20 -g 100 -n 100");
                System.out.println("  -c true|false: with or without crossover and mutation (default: true)");
                System.out.println("  -i true|false: with or without image output (default: false)");
                System.out.println("  -p 20: number of HPModell proteins");
                System.out.println("  -g 100: number of generations");
                System.out.println("  -n 100: number of population");
                return;
            } else if (args[0].equals("-t")) {
                hp.test();
                return;
            }
        }
        for (int i = 0; i < args.length; i = i + 2) {
            String arg = args[i];
            String value = args[i + 1];
            switch (arg) {
                case "-c":
                    withCrossAndMutation = Boolean.parseBoolean(value);
                    break;
                case "-i":
                    imageOutput = Boolean.parseBoolean(value);
                    break;
                case "-p":
                    anzahlHPModellProteins = Integer.parseInt(value);
                    break;
                case "-g":
                    anzahlGenerationen = Integer.parseInt(value);
                    break;
                case "-n":
                    anzahlPopulation = Integer.parseInt(value);
                    break;
                default:
                    System.out.println("Unknown argument: " + arg);
                    return;
            }
        }

        System.out.println("withCrossAndMutation: " + withCrossAndMutation);
        System.out.println("imageOutput: " + imageOutput);
        System.out.println("anzahlHPModellProteins: " + anzahlHPModellProteins);
        System.out.println("anzahlGenerationen: " + anzahlGenerationen);
        System.out.println("anzahlPopulation: " + anzahlPopulation);
        HPModell.anzahlNodes = anzahlHPModellProteins;
        hp.genAlgo(withCrossAndMutation, imageOutput, anzahlGenerationen, anzahlPopulation);
        // hp.test();
    }

    public HP() {
        p = new Population();

    }

    public void test() {
        String SEQ20 = "10100110100101100101";
        String SEQ24 = "110010010010010010010011";
        String SEQ25 = "0010011000011000011000011";
        String SEQ36 = "000110011000001111111001100001100100";
        String SEQ48 = "001001100110000011111111110000001100110010011111";
        String SEQ50 = "11010101011110100010001000010001000101111010101011";
        String[] seqs = { SEQ20, SEQ24, SEQ25, SEQ36, SEQ48, SEQ50 };
        for (String seq : seqs) {
            p = new Population();
            p.createBenchmarkProp(seq);
            double before = p.evaluation();
            for (int i = 0; i < 100; i++) {
                p = p.selection();
                p.crossover();
                p.mutation();
            }
            System.out.println(seq + " before: " + before + " after: " + p.evaluation());
        }
    }

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
        File csvOutputFile = new File(outputFolder + "output.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }

    }

    public void genAlgo(boolean withCrossAndMutation, boolean imageOutput, int maxGeneration, int populationSize) {
        if (withCrossAndMutation) {
            System.out.println("Genetic Algorithm with crossover and mutation");
        } else {
            System.out.println("Genetic Algorithm without crossover and mutation");
        }
        // deleteFolder(new File(outputFolder));
        // Datetime as 2020-12-31-23-59-59
        String datetime = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
        outputFolder = outputFolder + datetime + "_crossandmutate_" + withCrossAndMutation + "/";
        new File(outputFolder).mkdirs();

        // int populationSize = 100;
        p.createRandomPopulation(populationSize);

        double avgFitness = p.evaluation();
        // int maxGeneration = 2000;
        dataLines = new ArrayList<>();
        dataLines.add(
                new String[] { "Generation", "AvgFitness", "BestFitness", "BesteFitnessOverAll", "BestHydroContacts",
                        "HydroContactsOverAll", "BestOverlaps", "OverlapsOverAll", "Diversity", "BestSequenz" }); // csv
                                                                                                                  // header
        while (avgFitness < 45 &&
                p.generation < maxGeneration) {
            addStatistik(avgFitness);
            if (imageOutput) {
                p.exportBestAsImage();
            }
            p.generation++;
            p = p.selection(); // fitness proportional selection
            if (withCrossAndMutation) {
                p.crossover();
                p.mutation();
            }
            if (p.generation % (maxGeneration / 10) == 0) {
                System.out.println("Generation: " + p.generation);
            }

            avgFitness = p.evaluation();

        }
        addStatistik(avgFitness);
        System.out.println("Beste Fitness: " + p.bestHPModell.getFitness());
        System.out.println("Beste Sequenz: " + p.bestHPModell.toString());
        System.out.println("Generation: " + p.generation);

        try {
            givenDataArray_whenConvertToCSV_thenOutputCreated(); // write csv file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addStatistik(double avgFitness) {
        String[] newLine = new String[] { "" + p.generation, "" + avgFitness,
                "" + p.bestHPModell.getFitness(),
                "" + p.besteFitnessOverAll,
                "" + p.bestHPModell.getHydroContacts(),
                "" + p.anzahlHydroContactsOverAll,
                "" + p.bestHPModell.getOverlaps(),
                "" + p.anzahlOverlapsOverAll,
                "" + p.getDiversity(),
                "" + p.bestHPModell.toString() };
        dataLines.add(newLine);
    }

    // private static void deleteFolder(File folder) {
    // File[] files = folder.listFiles();
    // if (files != null) { // some JVMs return null for empty dirs
    // for (File f : files) {
    // if (f.isDirectory()) {
    // deleteFolder(f);
    // } else {
    // f.delete();
    // }
    // }
    // }
    // folder.delete();
    // }
}
