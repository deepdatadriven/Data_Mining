package njit.cs634.lintang.assignments.associationRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * AssociationRule
 * </p>
 *
 * @author lintang
 * @version 2016-11-18
 */
public class AssociationRule {
    private DataModel dm = null;
    private List<Set<String>> frequentItemSet = null;
    public AssociationRule(DataModel dm, List<Set<String>> frequentItemSet){
        this.dm = dm;
        this.frequentItemSet = frequentItemSet;
    }
    
    public List<Set<String>[]> getSignificantAssociationRule(double confidence){
        List<Set<String>[]> res = new ArrayList<Set<String>[]>();
        List<Set<String>[]> r = geAssociationRule(confidence);
        for (int i = 0; i < r.size(); i++){
            Set<String>[] rule1 = r.get(i);
            boolean keep = true;
            for (int j = 0; j < r.size(); j++){
                Set<String>[] rule2 = r.get(j);
                if (i == j) continue;
                if (Utils.contains(rule1[0], rule2[0]) && Utils.contains(rule2[1], rule1[1])){
                    keep = false;
                    break;
                }
            }
            if (keep) res.add(rule1);
        }
        return res;
    }
    
    @SuppressWarnings("unchecked")
    private List<Set<String>[]> geAssociationRule(double confidence){
        List<Set<String>[]> res = new ArrayList<Set<String>[]>();
        int start = 0;
        if (frequentItemSet.get(frequentItemSet.size() - 1).size() < 2) return res;
        while(frequentItemSet.get(start).size() < 2) start++;
        Set<String> s1 = new HashSet<String>();
        Set<String> s2 = new HashSet<String>();
        for (int i = start; i < frequentItemSet.size(); i++){
            Set<String> set = frequentItemSet.get(i);
            int support1 = dm.getCount(new ArrayList<String>(set));
            for (int left = 1; left <= set.size() - 1; left++){
                for (String str : set){
                    if (s1.size() < left) s1.add(str);
                    else s2.add(str);
                }
                int support2 = dm.getCount(new ArrayList<String>(s1));
                if (1.0 * support1 / support2 >= confidence){
                    res.add(new Set[]{s1, s2});
                    s1 = new HashSet<String>();
                    s2 = new HashSet<String>();
                }
                else{
                    s1.clear();
                    s2.clear();
                }
            }
        }
        return res;
    }
}