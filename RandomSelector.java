import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomSelector {
    private final NavigableMap<Double, String> map = new TreeMap<Double, String>();
    private final Random random;
    private double total = 0;

    public RandomSelector() {
        this.random = new Random();
        // this(new Random());
    }

    // public RandomSelector(Random random) {
    // this.random = random;
    // }

    public RandomSelector add(double weight, String result) {
        if (weight <= 0)
            return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public String next() {
        double value = random.nextDouble() * total;

        // try {

        return map.higherEntry(value).getValue();
        // } catch (Exception e) {
        // System.out.println("Error: " + e);
        // System.out.println("Value: " + value + " Total: " + total);
        // return map.firstEntry().getValue();
        // }
    }
}