import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

        boolean withCrossAndMutation = true;
        boolean imageOutput = false;
        int anzahlGenerationen = 100;
        int anzahlPopulation = 100;
        double mutationRate = 0.01;
        boolean scaleMutationRate = false;
        boolean tunierSelection = false;
        boolean withSigmaScaling = false;
        boolean withElitism = false;
        if (args.length == 1) {
            if (args[0].equals("-h")) {
                System.out.println(
                        "Usage: java HP --crossover true --image false --generations 100 --population 100 --mutationrate 0.01 --scalemutate false --tunier false --sigmascaling false --elitism false");
                System.out.println("  --crossover true|false: with or without crossover and mutation (default: true)");
                System.out.println("  --image true|false: with or without image output (default: false)");
                System.out.println("  --generations 100: number of generations");
                System.out.println("  --population 100: number of population");
                System.out.println("  --mutationrate 0.01: mutation rate");
                System.out.println("  --scalemutate true|false: scale mutation rate (default: false)");
                System.out.println("  --tunier true|false: tunier selection (default: false)");
                System.out.println("  --sigmascaling true|false: with sigma (σ) scaling (default: false)");
                System.out.println("  --elitism true|false: with elitism (default: false)");
                System.out.println("  --help: help");
                return;
            }
        }
        for (int i = 0; i < args.length; i = i + 2) {
            String arg = args[i];
            String value = args[i + 1];
            switch (arg) {
                case "--crossover":
                    withCrossAndMutation = Boolean.parseBoolean(value);
                    break;
                case "--image":
                    imageOutput = Boolean.parseBoolean(value);
                    break;
                case "--generations":
                    anzahlGenerationen = Integer.parseInt(value);
                    break;
                case "--population":
                    anzahlPopulation = Integer.parseInt(value);
                    break;
                case "--mutationrate":
                    mutationRate = Double.parseDouble(value);
                    break;
                case "--scalemutate":
                    scaleMutationRate = Boolean.parseBoolean(value);
                    break;
                case "--tunier":
                    tunierSelection = Boolean.parseBoolean(value);
                    break;
                case "--sigmascaling":
                    withSigmaScaling = Boolean.parseBoolean(value);
                    break;
                case "--elitism":
                    withElitism = Boolean.parseBoolean(value);
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
        System.out.println("withElitism: " + withElitism);
        System.out.println("Folder: " + HP.outputFolderPrefix);
        System.out.println("----------------------");
        hp.test(withCrossAndMutation, imageOutput, anzahlGenerationen, anzahlPopulation, mutationRate,
                scaleMutationRate,
                tunierSelection, withSigmaScaling, withElitism);
    }

    public HP() {
        p = new Population();
        // String datetime = java.time.LocalDateTime.now().toString().replace(":",
        // "-").replace(".", "-");
        // HP.outputFolderPrefix = HP.outputFolderPrefix + datetime + "/";
        // Folder to current directory
        HP.outputFolderPrefix = System.getProperty("user.dir") + "/output/";

    }

    public void test(boolean withCrossAndMutation, boolean imageOutput, int maxGeneration, int populationSize,
            double mutationRate, boolean scaleMutationRate, boolean tunierSelection, boolean withSigmaScaling,
            boolean withElitism) {
        // String SEQ20 = "10100110100101100101";
        // String SEQ24 = "110010010010010010010011";
        // String SEQ25 = "0010011000011000011000011";
        // String SEQ36 = "000110011000001111111001100001100100";
        // String SEQ48 = "001001100110000011111111110000001100110010011111";
        // String SEQ50 = "11010101011110100010001000010001000101111010101011";
        // String SEQ60 =
        // "001110111111110001111111111010001111111111110000111111011010";

        String SEQ64 = "1111111111110101001100110010011001100100110011001010111111111111";

        // String[] seqs = { SEQ20, SEQ24, SEQ25, SEQ36, SEQ48, SEQ50 };
        String[] seqs = { SEQ64 };
        // String[] seqs = { SEQ25 };
        for (String seq : seqs) {
            genAlgo(withCrossAndMutation, imageOutput, maxGeneration, populationSize, mutationRate, scaleMutationRate,
                    tunierSelection, withSigmaScaling, withElitism, seq);
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
        System.out.println("Writing to: " + csvOutputFile.getAbsolutePath());
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }

    }

    public void genAlgo(boolean withCrossAndMutation, boolean imageOutput, int maxGeneration, int populationSize,
            double initMutationRate, boolean scaleMutationRate,
            boolean tunierSelection, boolean withSigmaScaling,
            boolean withElitism,
            String seq) {
        HP.outputFolder = outputFolderPrefix;
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
                        // "Standardabweichung",
                        "Diversity",
                        "BestFolding",
                // "BestSequenz"

                }); // csv
        // header
        double mutationRate = initMutationRate;
        if (!withCrossAndMutation) {
            mutationRate = 0;
        }
        int anzahlMutationen = 0;
        double standardabweichung = 0;
        boolean useDiversityAsMutationRate = true;

        avgFitness = p.evaluation();
        addStatistik(avgFitness, 0, 0, standardabweichung);
        while (avgFitness < 45 &&
                p.generation < maxGeneration) {

            if (imageOutput) {
                p.exportBestAsImage();
            }
            p.generation++;
            if (withCrossAndMutation) {
                p.crossover();
                if (scaleMutationRate) {
                    if (useDiversityAsMutationRate) {
                        double diversity = p.getDiversity();
                        diversity = diversity / populationSize;
                        mutationRate = initMutationRate * (1 / (diversity + 1));
                        mutationRate /= 2;
                    } else {
                        standardabweichung = p.getStandardabweichung();
                        // Mehr Mutation wenn die Standardabweichung niedrig ist
                        mutationRate = initMutationRate * (1 / (standardabweichung + 1));
                        mutationRate /= 2;
                    }
                } else {
                    mutationRate = initMutationRate;
                }
                // mutationRate = scaleMutationRate
                // ? (initMutationRate * (1 - (double) p.generation / maxGeneration) + 0.01) //
                // scale mutation rate
                // // from high to low

                // : initMutationRate;
                anzahlMutationen = p.mutation(mutationRate);
            }
            avgFitness = p.evaluation();
            addStatistik(avgFitness, mutationRate, anzahlMutationen, standardabweichung);
            if (tunierSelection) {
                p = p.turnierSelection(withElitism); // turnier selection
            } else {
                p = p.selection(withSigmaScaling, withElitism); // fitness proportional selection
            }

        }
        p.exportBestAsImage();
        // addStatistik(avgFitness, );
        System.out.println("Sequenz: " + seq);
        System.out.println("Durchschnittliche Fitness: " + avgFitness);
        System.out.println("Beste Fitness: " + p.bestFolding.getFitness());
        System.out.println("Beste Directions: " + RelDir.toString(p.bestFolding.getDirections()));
        // System.out.println("Beste Sequenz: " + p.bestFolding.toString());

        try {
            givenDataArray_whenConvertToCSV_thenOutputCreated(); // write csv file
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("----------------------");
    }

    private void addStatistik(double avgFitness, double mutationRate, int anzahlMutationen,
            double standardabweichung) {
        String[] newLine = new String[] { "" + p.generation, "" +
                // Avg to 2 decimal places
                avgFitness,
                // String.format("%.2f", avgFitness),
                "" + p.bestFolding.getFitness(),
                // String.format("%.2f", p.bestFolding.getFitness()),
                "" + p.besteFitnessOverAll,
                // String.format("%.2f", p.besteFitnessOverAll),
                "" + p.bestFolding.getHydroContacts(),
                "" + p.bestFolding.getOverlaps(),
                "" + p.anzahlHydroContactsOverAll,
                "" + p.anzahlOverlapsOverAll,
                // "" + mutationRate,
                String.format("%.2f", mutationRate),
                "" + anzahlMutationen,
                // "" + standardabweichung,
                "" + p.getDiversity(),
                "" + RelDir.toString(p.bestFolding.getDirections()),
                // "" + p.bestFolding.toString()

        };
        dataLines.add(newLine);
    }
}
