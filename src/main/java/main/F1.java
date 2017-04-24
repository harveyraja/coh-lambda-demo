package main;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

import com.tangosol.util.InvocableMap.Entry;
import com.tangosol.util.ValueExtractor;

import com.tangosol.util.function.Remote.BiFunction;

import com.tangosol.util.stream.RemoteCollectors;

import main.model.Champ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * F1 is the 'driver' for demoing Coherence Distributed Lambdas.
 *
 * @author hr  2017.04.19
 */
public class F1
    {
    // ----- inner class: EnforcingFunction ---------------------------------

    /**
     * A {@link BiFunction} that returns a new {@link Champ}.
     * <p>
     * Note: the purposed of this class is to illustrate that it is not redefined
     *       on the server if the function is changed.
     */
    public static class EnforcingFunction
            implements BiFunction<Integer, Champ, Champ>
        {
        @Override
        public Champ apply(Integer NYear, Champ champ)
            {
            return new Champ(NYear.intValue(), "Nico Rosberg");
            }
        }

    // ----- helper functions -----------------------------------------------

    /**
     * Read each {@link Champ} from a file and pass it to the provided {@link Consumer}.
     * 
     * @param consumer  the Consumer that receives the Champ read from a file
     */
    public static void forEachChamp(Consumer<Champ> consumer)
        {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(F1.class.getResourceAsStream("/f1-champs.txt"))))
            {
            for (String sRow; (sRow = reader.readLine()) != null; )
                {
                String[] asRow = sRow.split(",");
                if (!"Year".equals(asRow[0]))
                    {
                    consumer.accept(new Champ(Integer.parseInt(asRow[0]), asRow[1].trim()));
                    }
                }
            }
        catch (IOException e)
            {
            }
        }

    /**
     * Driver method for Coherence Distributed Lambdas demo.
     *
     * @param asArgs  ignored
     */
    public static void main(String[] asArgs)
        {
        // get an instance of a cache
        NamedCache<Integer, Champ> cacheChamps = CacheFactory.getCache("champs");

        // if cache has not been loaded, load data from a file @see forEachChamp
        if (cacheChamps.isEmpty())
            {
            System.out.println("Loading the champs...");
            forEachChamp(champ -> cacheChamps.put(champ.getYear(), champ));
            }

        // #1: output the champs
        cacheChamps.forEach((NYear, champ) -> System.out.println(champ.getName() + " won in " + NYear));

        // #2a: aggregate the champs to show how many times they have the F1 championship
        System.out.println(cacheChamps.stream().map(Entry::getValue).collect(
                RemoteCollectors.groupingBy(Champ::getName, RemoteCollectors.counting())));

        // #2b: a more efficient way (use Coherence indices) to perform #2a
        System.out.println(cacheChamps.stream(ValueExtractor.identity()).collect(
                        RemoteCollectors.groupingBy(Champ::getName, RemoteCollectors.counting())));

        // #3: use compute to execute a Function on a remote node (the same node that owns the data)
        cacheChamps.computeIfAbsent(2017, NYear -> new Champ(NYear, "Sebastian Vettel"));

        // #4: use the EnforcingFunction class to update the champ for a year
        System.out.println(cacheChamps.get(2015)); // output: Lewis Hamilton
        cacheChamps.compute(2015, new EnforcingFunction());
        System.out.println(cacheChamps.get(2015)); // output: Nico Rosberg

        // attempt to update the string constant in the EnforcingFunctiontion
        // definition and re-executing highlights that the class is not re-defined
        // on the server

        // use a lambda (without restarting the server) to correct the mistake
        // of making Nico the winner of the 2015 championship
        System.out.println(cacheChamps.get(2015)); // output: Nico Rosberg
        cacheChamps.compute(2015, (NYear, champ) -> new Champ(NYear, "Lewis Hamilton"));
        System.out.println(cacheChamps.get(2015)); // output: Lewis Hamilton
        }
    }
