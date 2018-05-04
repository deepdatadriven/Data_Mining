package njit.cs634.lintang.assignments.associationRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * DataModel
 * </p>
 *
 * @author lintang
 * @version 2016-11-18
 */
public class DataModel {
    private List<Set<String>> data;
    private Map<String, Integer> count;
    
    public Map<String, Integer> getCount() {
        return count;
    }
    public List<Set<String>> getData() {
        return data;
    }
    public DataModel(){
        data = new ArrayList<Set<String>>();
        count = new HashMap<String, Integer>();
    }
    //insert a line of data
    public void insert(String[] d){
        Set<String> set = new HashSet<String>();
        for (String v : d) {
            set.add(v);
            Integer i = count.get(v);
            if (i == null) count.put(v, 1);
            else count.put(v, i + 1);
        }
        data.add(set);
    }
    
    // get count of records that contain a specific pattern
    public int getCount(List<String> pattern){
        int c = 0;
        for (Set<String> set : data){
            boolean match = true;
            for (String s : pattern){
                if (!set.contains(s)){
                    match = false;
                    break;
                }
            }
            if (match) c++;
        }
        return c;
    }
}