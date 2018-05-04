package njit.cs634.lintang.assignments.associationRule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * <p>
 * AssociationRule
 * </p>
 *
 * @author lintang
 * @version 2016-11-18
 */
public class Main {
    
    
    private DataModel readDataIntoModel(String fileName){
        File f = new File(fileName);
        FileReader fio = null;
        BufferedReader in = null;
        DataModel dm = new DataModel();
        try{
            fio = new FileReader(f);
            in = new BufferedReader(fio);
            String line = null;
            while((line = in.readLine()) != null && line.trim().length() != 0){
                String[] v = line.split(" ");
                dm.insert(v);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fio != null){
                try {
                    fio.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return dm;
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String fileName = null;
        if (args.length == 0) fileName = "C:/software/java/hw-2/src/njit/cs634/qiyi/assignments/associationRule/forests.txt";
        //if (args.length == 0) fileName = "test.txt";
        else fileName = args[0];
        Main m = new Main();
        DataModel dm = m.readDataIntoModel(fileName);
        double support = 0.6;
        double confidence = 0.3;
        int minLength = 6;
        
        FrequentItemSet fs = new FrequentItemSet(dm);
        List<Set<String>> res = fs.getFrequentItemSet(support);
        List<Set<String>> res2 = fs.getMaxFrequentItemSet(support);
        List<Integer> res3 = fs.findFrequentRecord(support, minLength);
        AssociationRule as = new AssociationRule(dm, res);
        List<Set<String>[]> res4 = as.getSignificantAssociationRule(confidence);
        List<Set<String>> res5 = fs.getMostUncommonMaXFrequentPattern(support);
        Scanner in = new Scanner(System.in);   
        System.out.println("Please input the question number:");
        System.out.println("Enter -1 to exit:");
        int num = 0;
        while(true){
            try{
                num = in.nextInt();
                if (num == -1) {
                    System.out.println("GoodBye!");
                    break;
                }
                if (num == 1){
                    for (Set<String> rule : res){
                        for (String s : rule){
                            System.out.print(s + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("1: " + res.size());
                }
                else if (num == 2){
                    for (Set<String> rule : res2){
                        for (String s : rule){
                            System.out.print(s + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("2: " + res2.size());
                }
                else if (num == 3){
                    for (int i : res3) System.out.println(i);
                    System.out.println("3: " + res3.size());
                }
                else if (num == 4){
                    for (Set<String>[] rule : res4){
                        for (String s : rule[0]){
                            System.out.print(s + " ");
                        }
                        System.out.print("-> ");
                        for (String s : rule[1]){
                            System.out.print(s + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("4: " + res4.size());
                }
                else{
                    for (Set<String> rule : res5){
                        for (String s : rule){
                            System.out.print(s + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("5: " + res5.size());
                }
            }
            catch(InputMismatchException e){
                System.out.println("The input should be an integer");
                in.next();//discard mismatched data
            }
            catch(Exception e){
                // do nothing      
            }
        }
        in.close();
    }

}