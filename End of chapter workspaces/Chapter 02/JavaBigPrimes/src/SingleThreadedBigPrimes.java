import java.math.BigInteger;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class SingleThreadedBigPrimes {
    public static void main(String[] args) {

        Long start = System.currentTimeMillis();

        SortedSet<BigInteger> primes = new TreeSet<>();

        while (primes.size() < 20) {
            BigInteger bigInteger = new BigInteger(2000, new Random());
            primes.add(bigInteger.nextProbablePrime());
        }

        Long end = System.currentTimeMillis();
        System.out.println(primes);
        
        System.out.println("The time taken was " + (end - start) + " ms.");
    }
}
