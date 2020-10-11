package utils;

import java.util.List;

public class LSpinner_helper {
    public static int label2position(List<String> l, String str){
        for(int i=0;i<l.size();i++){
            if(l.get(i).equals(str)){
                return i;
            }
        }
        return 0;
    }
    public static int label2NearestAbovePosition(List<String> l,String str){
        try{
            int target=Integer.parseInt(str);
            for(int i=0;i<l.size();i++){
                int item=Integer.parseInt(l.get(i));
                if(item>=target)
                    return i;
            }
        }
        catch (Exception e){
        }
        return -1;
    }
}
