
/**
 * HP
 * https://arxiv.org/pdf/1608.05855.pdf
 * https://www.sciencedirect.com/science/article/pii/S147692711000040X
 */

public class HP {
    Population population;

    public static void main(String[] args) {
        HP hp = new HP();
        hp.genAlgo();
    }

    public HP() {
        population = new Population();

    }

    public void test() {
        population.creatTestPop();
        population.printModel();
    }

    public void genAlgo() {
        population.createRandomPopulation(100);
        population.printModel();
    }
}
