package njit.cs634.lintang.assignments.associationRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * FrequentItemSet
 * </p>
 *
 * @author lintang
 * @version 2016-11-18
 */
public class FrequentItemSet {
    private DataModel dm = null;
    private List<Set<String>> frequentItemSet = null;
    private List<Set<String>> maxFrequentItemSet = null;
    private Map<Set<String>, Integer> frequency = null;
    public FrequentItemSet(DataModel dm){
        this.dm = dm;
        frequency = new HashMap<Set<String>, Integer>();
    }
    public List<Set<String>> getFrequentItemSet(double support){
        List<Set<String>> res = new ArrayList<Set<String>>();
        List<List<String>> cur = new ArrayList<List<String>>();
        List<List<String>> next = new ArrayList<List<String>>();
        List<String> single = new ArrayList<String>();
        Map<String, Integer> count = dm.getCount();
        int rows = dm.getData().size();
        double supportCount = rows * support;
        
        for (String s : count.keySet()){
            if (count.get(s) >= supportCount) {
                single.add(s);
                List<String> p = new ArrayList<String>();
                p.add(s);
                cur.add(p);
                Set<String> set = new HashSet<String>();
                set.add(s);
                res.add(set);
                frequency.put(set, count.get(s));
            }
        }
        
        Collections.sort(single);
        
        for (int i = 2; i <= dm.getCount().size(); i++){
            for (List<String> list : cur){
                String last = list.get(list.size() - 1);
                int j = 0;
                while(single.get(j) != last) j++;
                List<String> pattern = new ArrayList<String>(list);
                for (int k = j + 1; k < single.size(); k++){
                    pattern.add(single.get(k));
                    if (dm.getCount(pattern) >= supportCount){
                        List<String> p = new ArrayList<String>(pattern);
                        Set<String> set = new HashSet<String>(p);
                        res.add(set);
                        frequency.put(set, dm.getCount(pattern));
                        next.add(p);
                    }
                    pattern.remove(pattern.size() - 1);
                }
            }
            if (next.size() == 0) break;
            cur = next;
            next = new ArrayList<List<String>>(); 
        }
        Collections.sort(res, new Comparator<Set<String>>(){

            @Override
            public int compare(Set<String> o1, Set<String> o2) {
                // TODO Auto-generated method stub
                return o1.size() - o2.size();
            }
            
        });
        frequentItemSet = new ArrayList<Set<String>>(res);
        return res;
    }
    public List<Set<String>> getMaxFrequentItemSet(double support){
        if (frequentItemSet == null) frequentItemSet = getFrequentItemSet(support);
        List<Set<String>> res = new ArrayList<Set<String>>();
        for (int i = 0; i < frequentItemSet.size(); i++){
            boolean isMax = true;
            for (int j = i + 1; j < frequentItemSet.size(); j++){
                if (Utils.contains(frequentItemSet.get(j), frequentItemSet.get(i))){
                    isMax = false;
                    break;
                }
            }
            if (isMax) res.add(frequentItemSet.get(i));
        }
        maxFrequentItemSet = res;
        return res;
    }

    /**
     * find the records that contain frequent item sets of length >= minLength
     * @param support
     * @param minLength
     * @return
     */
    public List<Integer> findFrequentRecord(double support, int minLength){
        if (frequentItemSet == null) frequentItemSet = getFrequentItemSet(support);
        List<Integer> res = new ArrayList<Integer>();
        if (frequentItemSet.get(frequentItemSet.size() - 1).size() < minLength) return res;
        int start = 0;
        while(frequentItemSet.get(start).size() < minLength) start++;
        for (int i = 0; i < dm.getData().size(); i++){
            Set<String> forest = dm.getData().get(i);
            boolean isFreq = false;
            for (int j = start; j < frequentItemSet.size(); j++){
                if (Utils.contains(forest, frequentItemSet.get(j))){
                    isFreq = true;
                    break;
                }
            }
            if (isFreq) res.add(i + 1);
        }
        return res;
    }
    
    public List<Set<String>> getMostUncommonMaXFrequentPattern(double support){
        if (maxFrequentItemSet == null) maxFrequentItemSet = getMaxFrequentItemSet(support);
        List<Set<String>> res = new ArrayList<Set<String>>();
        int min = Integer.MAX_VALUE;
        for (Set<String> s : maxFrequentItemSet){
            if (frequency.get(s) < min) min = frequency.get(s);
        }
        for (Set<String> s : maxFrequentItemSet){
            if (frequency.get(s) == min) res.add(s);
        }
        return res;
    }
    
    public int getCount(Set<String> s){
        if (frequency == null || frequency.get(s) == null) return -1;
        else return frequency.get(s);
    }
}