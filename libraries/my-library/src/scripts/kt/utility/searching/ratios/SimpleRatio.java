package scripts.kt.utility.searching.ratios;

import scripts.kt.utility.searching.Ratio;
import scripts.kt.utility.searching.ToStringFunction;
import scripts.kt.utility.searching.diffUtils.DiffUtils;

public class SimpleRatio implements Ratio {

    /**
     * Computes a simple Levenshtein distance ratio between the strings
     *
     * @param s1 Input string
     * @param s2 Input string
     * @return The resulting ratio of similarity
     */
    @Override
    public int apply(String s1, String s2) {

        return (int) Math.round(100 * DiffUtils.getRatio(s1, s2));

    }

    @Override
    public int apply(String s1, String s2, ToStringFunction<String> sp) {
        return apply(sp.apply(s1), sp.apply(s2));
    }
}
