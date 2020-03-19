import java.util.ArrayList;
import java.util.List;

public class NameUtils {

    public static boolean isClassName(String name){
        if(name==null||name.length()<1||!isAlpha(name.charAt(0))||!isCapitalized(name.charAt(0))){
            return false;
        }

        /**
         * rule: as long as the name contains one character
         * we will consider it as a class name
         */
        int times = 0;
        int cTimes = 0;
        for(char c : name.toCharArray()){
            if(isUncapitalized(c)){
                times++;
            }
            else if(isCapitalized(c)){
                cTimes++;
            }
        }

        return times >= 1 && cTimes > 1;
    }

    public static boolean isMethodName(String name){
        if(name==null||name.length()<1||!isAlpha(name.charAt(0))||isCapitalized(name.charAt(0))||isStaticVarName(name)){
            return false;
        }
        int times = 0;
        for(char c : name.toCharArray()){
            if(isCapitalized(c)){
                times++;
            }
        }
        return times>=1;
    }

    public static boolean isStaticVarName(String name){
        if(name==null||name.length()<1||name.length()<=1||!name.contains("_")){
            return false;
        }
        for(char c : name.toCharArray()){
            if(isUncapitalized(c)&&c!='_'){
                return false;
            }
        }
        return true;
    }

    public static boolean isCapitalized(char c){
        return (c>='A'&&c<='Z');
    }

    public static boolean isUncapitalized(char c){
        return (c>='a'&&c<='z');
    }

    public static boolean isAlpha(char c){
        return (isUncapitalized(c)||isCapitalized(c));
    }

    public static String[] splitCamelName(String name){
        int len = name.length();
        List<String> tokens = new ArrayList<>();
        for(int i=0; i<len;){
            if(i==0||isCapitalized(name.charAt(i))){
                int j= i+1;
                while(j<len&&isUncapitalized(name.charAt(j))){
                    j++;
                }
                tokens.add(name.substring(i, j).toLowerCase());
                i = j;
            }
            else{
                i++;
            }
        }
        int num = tokens.size();
        String[] result = new String[num];
        for(int k=0; k<num; k++){
            result[k] = tokens.get(k);
        }

        return result;
    }

    public static String[] splitStaticVarName(String name)  {
//        String[] tempRes = name.split("_");
//        List<String> tokens = new ArrayList<>();
//        for(int i=0; i<tempRes.length; i++){
//            try{
//                Integer temp = Integer.valueOf(tempRes[i]);
//            } catch (Exception e){
//                tokens.add(tempRes[i].toLowerCase());
//            }
//        }
//        int num = tokens.size();
//        String[] result = new String[num];
//        for(int k=0; k<num; k++){
//            result[k] = tokens.get(k);
//        }
        return name.split("_");
    }
}
