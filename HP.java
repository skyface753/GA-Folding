import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Helpers.Helpers.RelDir;

/**
 * HP
 * https://arxiv.org/pdf/1608.05855.pdf
 * https://www.sciencedirect.com/science/article/pii/S147692711000040X
 */

public class HP {
    Population p;
    public static String outputFolderPrefix = "/tmp/ga/";
    public static String outputFolder;

    public static void main(String[] args) {
        HP hp = new HP();
        String datetime = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
        HP.outputFolderPrefix = HP.outputFolderPrefix + datetime + "/";
        boolean withCrossAndMutation = true;
        boolean imageOutput = false;
        int anzahlGenerationen = 100;
        int anzahlPopulation = 100;
        double mutationRate = 0.01;
        Boolean scaleMutationRate = false;
        Boolean tunierSelection = false;
        boolean withSigmaScaling = false;
        if (args.length == 1) {
            if (args[0].equals("-h")) {
                System.out.println("Usage: java HP -c true -i false -p 20 -g 100 -n 100");
                System.out.println("  -c true|false: with or without crossover and mutation (default: true)");
                System.out.println("  -i true|false: with or without image output (default: false)");
                System.out.println("  -g 100: number of generations");
                System.out.println("  -n 100: number of population");
                System.out.println("  -m 0.01: mutation rate");
                System.out.println("  -s true|false: scale mutation rate (default: false)");
                System.out.println("  -t true|false: tunier selection (default: false)");
                System.out.println("  -o true|false: with sigma (σ) scaling (default: false)");
                System.out.println("  -h: help");
                return;
                // } else if (args[0].equals("-t")) {
                // hp.test();
                // return;
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
                case "-g":
                    anzahlGenerationen = Integer.parseInt(value);
                    break;
                case "-n":
                    anzahlPopulation = Integer.parseInt(value);
                    break;
                case "-m":
                    mutationRate = Double.parseDouble(value);
                    break;
                case "-s":
                    scaleMutationRate = Boolean.parseBoolean(value);
                    break;
                case "-t":
                    tunierSelection = Boolean.parseBoolean(value);
                    break;
                case "-o":
                    withSigmaScaling = Boolean.parseBoolean(value);
                    break;
                default:
                    System.out.println("Unknown argument: " + arg);
                    return;
            }
        }

        System.out.println("withCrossAndMutation: " + withCrossAndMutation);
        System.out.println("imageOutput: " + imageOutput);
        System.out.println("anzahlGenerationen: " + anzahlGenerationen);
        System.out.println("anzahlPopulation: " + anzahlPopulation);
        System.out.println("mutationRate: " + mutationRate);
        System.out.println("scaleMutationRate: " + scaleMutationRate);
        System.out.println("tunierSelection: " + tunierSelection);
        System.out.println("withSigmaScaling: " + withSigmaScaling);
        System.out.println("----------------------");
        hp.test(withCrossAndMutation, imageOutput, anzahlGenerationen, anzahlPopulation, mutationRate,
                scaleMutationRate,
                tunierSelection, withSigmaScaling);
    }

    public HP() {
        p = new Population();

    }

    public void test(boolean withCrossAndMutation, boolean imageOutput, int maxGeneration, int populationSize,
            double mutationRate, Boolean scaleMutationRate, Boolean tunierSelection, Boolean withSigmaScaling) {
        String SEQ20 = "10100110100101100101";
        String SEQ24 = "110010010010010010010011";
        String SEQ25 = "0010011000011000011000011";
        String SEQ36 = "000110011000001111111001100001100100";
        String SEQ48 = "001001100110000011111111110000001100110010011111";
        String SEQ50 = "11010101011110100010001000010001000101111010101011";
        String[] seqs = { SEQ20, SEQ24, SEQ25, SEQ36, SEQ48, SEQ50 };
        for (String seq : seqs) {
            genAlgo(withCrossAndMutation, imageOutput, maxGeneration, populationSize, mutationRate, scaleMutationRate,
                    tunierSelection, withSigmaScaling, seq);
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
        File csvOutputFile = new File(HP.outputFolder + "output.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }

    }

    public void genAlgo(boolean withCrossAndMutation, boolean imageOutput, int maxGeneration, int populationSize,
            double initMutationRate, Boolean scaleMutationRate,
            Boolean tunierSelection, Boolean withSigmaScaling,
            String seq) {
        HP.outputFolder = outputFolderPrefix + seq + "/";
        new File(outputFolder).mkdirs();

        // int populationSize = 100;
        p = new Population();
        p.createFromSequenz(seq, populationSize);

        double avgFitness = p.evaluation();
        dataLines = new ArrayList<>();
        dataLines.add(
                new String[] { "Generation", "AvgFitness", "BestFitness", "BesteFitnessOverAll", "BestHydroContacts",
                        "BestOverlaps", "HydroContactsOverAll", "OverlapsOverAll",
                        "MutationRate",
                        "AnzahlMutationen",
                        "Diversity", "BestDirections",
                        "BestSequenz" }); // csv
        // header
        double mutationRate = initMutationRate;
        while (avgFitness < 45 &&
                p.generation < maxGeneration) {

            if (imageOutput) {
                p.exportBestAsImage();
            }
            p.generation++;
            if (withCrossAndMutation) {
                p.crossover();
                mutationRate = scaleMutationRate
                        ? (initMutationRate * (1 - (double) p.generation / maxGeneration) + 0.01) // scale mutation rate
                        : initMutationRate;
                int anzahlMutationen = p.mutation(mutationRate);
                addStatistik(avgFitness, mutationRate, anzahlMutationen);
            } else {
                addStatistik(avgFitness, 0, 0);
            }
            if (tunierSelection) {
                p = p.turnierSelection(); // turnier selection
            } else {
                p = p.selection(withSigmaScaling); // fitness proportional selection
            }

            avgFitness = p.evaluation();
            p.sigmaScale();
        }
        addStatistik(avgFitness, 0, 0);
        System.out.println("Sequenz: " + seq);
        System.out.println("Durchschnittliche Fitness: " + avgFitness);
        System.out.println("Beste Fitness: " + p.bestHPModell.getFitness());
        System.out.println("Beste Directions: " + RelDir.toString(p.bestHPModell.getDirections()));
        System.out.println("Beste Sequenz: " + p.bestHPModell.toString());
        System.out.println("----------------------");

        try {
            givenDataArray_whenConvertToCSV_thenOutputCreated(); // write csv file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addStatistik(double avgFitness, double mutationRate, int anzahlMutationen) {
        String[] newLine = new String[] { "" + p.generation, "" + avgFitness,
                "" + p.bestHPModell.getFitness(),
                "" + p.besteFitnessOverAll,
                "" + p.bestHPModell.getHydroContacts(),
                "" + p.bestHPModell.getOverlaps(),
                "" + p.anzahlHydroContactsOverAll,
                "" + p.anzahlOverlapsOverAll,
                "" + mutationRate,
                "" + anzahlMutationen,
                "" + p.getDiversity(),
                "" + RelDir.toString(p.bestHPModell.getDirections()),
                "" + p.bestHPModell.toString() };
        dataLines.add(newLine);
    }
}
