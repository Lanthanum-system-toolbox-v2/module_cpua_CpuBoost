package utils;

import java.util.ArrayList;

/**
 * Created by xzr on 2018/3/2.
 */

public class GetItemFromSYSTEM {
    //这个在最后有一个空格
    public static ArrayList<String> get(String s){
        ArrayList<String> result=new ArrayList<>();
        int i=0;
        String buffer = "";
        while (true){
            String cache;

            try {

                cache = s.substring(i, i + 1);
                if (cache.equals(" ")) {
                    result.add(buffer);
                    buffer="";
                } else {
                    buffer = buffer + cache;
                }
                i++;
            }
            catch (Exception e){
               // result.add(buffer);
                break;
            }
        }
        return result;
    }
    //这个在最后莫得空格
    public static ArrayList<String> gettcp(String s){
        ArrayList<String> result=new ArrayList<>();
        int i=0;
        String buffer = "";
        while (true){
            String cache;

            try {

                cache = s.substring(i, i + 1);
                if (cache.equals(" ")) {
                    result.add(buffer);
                    buffer="";
                } else {
                    buffer = buffer + cache;
                }
                i++;
            }
            catch (Exception e){
                result.add(buffer);
                break;
            }
        }
        return result;
    }

    //不如综合一下上面俩
    public static ArrayList<String> getall(String s){
        while (s.endsWith(" "))
            s=s.substring(0,s.length()-1);

        return gettcp(s);
    }

    public static ArrayList<String> getio(String s){
        ArrayList<String> result=new ArrayList<>();
        int i=0;
        String buffer = "";
        if(!s.endsWith(" "))
            s+=" ";
        while (true){
            String cache;
            try {

                cache = s.substring(i, i + 1);
                if (cache.equals(" ")) {
                    buffer=buffer.replace("[","");
                    buffer=buffer.replace("]","");
                    result.add(buffer);
                    buffer="";
                } else {
                    buffer = buffer + cache;
                }
                i++;
            }
            catch (Exception e){
                break;
            }
        }
        return result;
    }

    public static ArrayList<String> getthp(String s){
        ArrayList<String> result=new ArrayList<>();
        int i=0;
        String buffer = "";
        while (true){
            String cache;

            try {

                cache = s.substring(i, i + 1);
                if (cache.equals(" ")) {
                    buffer=buffer.replace("[","");
                    buffer=buffer.replace("]","");
                    result.add(buffer);
                    buffer="";
                } else {
                    buffer = buffer + cache;
                }
                i++;
            }
            catch (Exception e){
                buffer=buffer.replace("[","");
                buffer=buffer.replace("]","");
                result.add(buffer);
                break;
            }
        }
        return result;
    }

    //已改进，无论末尾有没有空格
    public static String getcurrentio(String s){
        ArrayList<String> result=new ArrayList<>();
        int i=0;
        String buffer = "";
        if(!s.endsWith(" "))
            s+=" ";
        while (true){
            String cache;
            try {

                cache = s.substring(i, i + 1);
                if (cache.equals(" ")) {
                   if(buffer.startsWith("[")&&buffer.endsWith("]")){
                       buffer=buffer.replace("[","");
                       buffer=buffer.replace("]","");
                       return buffer;
                   }
                    result.add(buffer);
                    buffer="";
                } else {
                    buffer = buffer + cache;
                }
                i++;
            }
            catch (Exception e){
                // result.add(buffer);
                break;
            }
        }
        return null;
    }

    public static String getcurrentthp(String s){
        ArrayList<String> result=new ArrayList<>();
        int i=0;
        String buffer = "";
        while (true){
            String cache;

            try {

                cache = s.substring(i, i + 1);
                if (cache.equals(" ")) {
                    if(buffer.startsWith("[")&&buffer.endsWith("]")){
                        buffer=buffer.replace("[","");
                        buffer=buffer.replace("]","");
                        return buffer;
                    }
                    result.add(buffer);
                    buffer="";
                } else {
                    buffer = buffer + cache;
                }
                i++;
            }
            catch (Exception e){
                if(buffer.startsWith("[")&&buffer.endsWith("]")){
                    buffer=buffer.replace("[","");
                    buffer=buffer.replace("]","");
                    return buffer;
                }

                break;
            }
        }
        return null;
    }
}
