
import java.util.Arrays;

class Individual {
    public byte[] genes = new byte[Population.geneLength];
    double fitness = 0;
    double fitnessProzent = 0;

    public Individual() {
        for (int i = 0; i < genes.length; i++) {
            genes[i] = (byte) Math.round(Math.random());
        }
    }

    public void calcFitness() {
        double x = decode();
        // f(x) = 150 + 42 * x * sin(20 *x) * cos(2*x)
        fitness = 150 + 42 * x * Math.sin(20 * x) * Math.cos(2 * x);

    }

    public double decode() {
        // Xmin + Genotyp * ((Xmax - Xmin) / (2^18 - 1))
        double xmin = 2.0;
        double xmax = 4.0;
        // Bytes to decimal
        double d = 0;
        for (int i = 0; i < genes.length; i++) {
            d += genes[i] * Math.pow(2, i);
        }
        double fraction = (xmax - xmin) / (Math.pow(2, genes.length) - 1);
        return xmin + d * fraction;
    }

    @Override
    public String toString() {
        return "Individual [genes=" + Arrays.toString(genes) + ", fitness=" + fitness + ", fitnessProzent="
                + fitnessProzent + "]";
    }
}
