package njit.cs634.lintang.assignments.associationRule;

import java.util.Set;

/**
 * <p>
 * Utils
 * </p>
 *
 * @author lintang
 * @version 2016-11-18
 */
public class Utils {
    // return if itemset1 contains itemset2
    // if length of itemset1 < length of itemset2, return false
    public static boolean contains(Set<String> s1, Set<String> s2){
        if (s1.size() < s2.size()) return false;
        for (String s : s2){
            if (!s1.contains(s)) return false;
        }
        return true;
    }
}