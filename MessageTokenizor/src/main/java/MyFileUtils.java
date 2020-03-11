import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFileUtils {
    public static void changeTextToJson(String textpath, String jsonpath) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(textpath)));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(jsonpath)));
        List<String> elementLines = new ArrayList<>();
        String line = null;
        while((line=bufferedReader.readLine())!=null){
            elementLines.add(line);
        }
        bufferedWriter.write("[");

        boolean firstLine = true;
        for(String element : elementLines){
            if(!firstLine){
                bufferedWriter.write(", ");
                bufferedWriter.write("\n");
            }
            bufferedWriter.write("\"");
            bufferedWriter.write(element);
            bufferedWriter.write("\"");

            if(firstLine){
                firstLine = false;
            }
        }

        bufferedWriter.write("]");

        bufferedReader.close();
        bufferedWriter.close();
    }

    public static void generateMsg(String msgTextPath, String msgTokenJsonPath)
    throws Exception{
        BufferedReader rawMsgReader = new BufferedReader(new FileReader(new File(msgTextPath)));
        BufferedWriter msgTokenWriter = new BufferedWriter(new FileWriter(new File(msgTokenJsonPath)));

        String line = null;

        String regex = "\\W+";
        boolean firstLine = true;
        msgTokenWriter.write("[");
        while((line=rawMsgReader.readLine())!=null){
            String[] tokensList = line.split(regex);
            StringBuilder dataLine = new StringBuilder();
            if(!firstLine){
                dataLine.append(",\n");
            }
            firstLine = false;
            boolean firstWord = true;
            dataLine.append("[");

            for(String token : tokensList){
                if(token.length()<1){
                    continue;
                }
                if(!firstWord){
                    dataLine.append(", ");
                }
                dataLine.append("\"").append(token).append("\"");

                firstWord = false;
            }
            dataLine.append("]");
            msgTokenWriter.write(dataLine.toString());
        }
        msgTokenWriter.write("]");

        rawMsgReader.close(); msgTokenWriter.close();
    }

    public static void generateDiffTokenAndMarkAndAtt(String diffTextPath, String diffTokenPath,
                                                      String diffMarkPath, String diffAttPath)
            throws Exception{
        BufferedReader diffTextReader = new BufferedReader(new FileReader(new File(diffTextPath)));
        BufferedWriter diffTokenWriter = new BufferedWriter(new FileWriter(new File(diffTokenPath)));
        BufferedWriter diffMarkWriter = new BufferedWriter(new FileWriter(new File(diffMarkPath)));
        BufferedWriter diffAttWriter = new BufferedWriter(new FileWriter(new File(diffAttPath)));

        diffTokenWriter.write("[");
        diffMarkWriter.write("[");
        diffAttWriter.write("[");

        String line = null;
        //need to make sure what \t refer to
        boolean firstLine = true;
        while((line=diffTextReader.readLine())!=null){
            StringBuilder dataLine = new StringBuilder();
            StringBuilder markLine = new StringBuilder();
            StringBuilder attLine = new StringBuilder();

            //pre-process 1 : replace all trans word to either space or <nl>
            line = line.replaceAll("\\\\t"," ")
                    .replaceAll("\\\\n", " <nl> ")
                    .replaceAll("\\\\\"", " ");

            String[] connectedVars = line.split("\\s+");
            List<String> tempTokenList = new ArrayList<>();

            for(String subConnectedVar : connectedVars){
                if(subConnectedVar.equals("<nl>")){
                    tempTokenList.add(subConnectedVar);
                    continue;
                }
                int len = subConnectedVar.length();
                int index = 0;
                StringBuilder tempToken = new StringBuilder();
                for(;index < len;){
                    char c = subConnectedVar.charAt(index);
                    if(isLetter(c)){
                        tempToken.append(c);
                        index++;
                        if(index==len){
                            tempTokenList.add(tempToken.toString());
                        }
                    }
                    else{
                        if(tempToken.length()>=1) {
                            tempTokenList.add(tempToken.toString());
                            tempToken = new StringBuilder();
                        }
                        tempTokenList.add(String.valueOf(c));
                        index++;
                    }
                }
            }

            if(!firstLine){
                dataLine.append(",\n");
                markLine.append(",\n");
                attLine.append(",\n");
            }
            firstLine = false;
            dataLine.append("[\"<nb>\"");
            markLine.append("[0");
            attLine.append("[[]");
            for(String token : tempTokenList){
                dataLine.append(", \"").append(token).append("\"");
                markLine.append(", 0");
                if(NameUtils.isClassName(token)||NameUtils.isMethodName(token)){
                    String[] splitCamelWords = NameUtils.splitCamelName(token);
                    StringBuilder camelStr = new StringBuilder();
                    camelStr.append(", [\"").append(splitCamelWords[0]).append("\"");
                    for(int i=1; i<splitCamelWords.length; i++){
                        camelStr.append(", \"").append(splitCamelWords[i]).append("\"");
                    }
                    camelStr.append("]");
                    attLine.append(camelStr.toString());
                } else{
                    attLine.append(", []");
                }
            }
            dataLine.append("]");
            markLine.append("]");
            attLine.append("]");

            diffTokenWriter.write(dataLine.toString());
            diffMarkWriter.write(markLine.toString());
            diffAttWriter.write(attLine.toString());
        }
        diffTokenWriter.write("]");
        diffMarkWriter.write("]");
        diffAttWriter.write("]");

        diffTextReader.close();
        diffTokenWriter.close();
        diffMarkWriter.close();
        diffAttWriter.close();
    }

    public static void generateVariable(String diffTextPath, String variablePath)throws Exception{
        BufferedReader diffTextReader = new BufferedReader(new FileReader(new File(diffTextPath)));
        BufferedWriter variableWriter = new BufferedWriter(new FileWriter(new File(variablePath)));

        variableWriter.write("[");

        String line = null;
        boolean firstLine = true;

        while((line=diffTextReader.readLine())!=null){
            StringBuilder tempGroup = new StringBuilder();

            if(!firstLine){
                tempGroup.append(",\n");
            }
            firstLine = false;

            List<String> varKeys = new ArrayList<>();
            List<String> varVals = new ArrayList<>();
            int cIndex=0; int mIndex=0; int vIndex=0;

            line = line.replaceAll("\\\\t"," ")
                    .replaceAll("\\\\n", " <nl> ")
                    .replaceAll("\\\\\"", " ");

            String[] wordList = line.split("\\W+");
            for(String word:wordList){
                boolean isC = NameUtils.isClassName(word);
                boolean isM = NameUtils.isMethodName(word);
                boolean isV = NameUtils.isStaticVarName(word);
                if(isC||isM||isV){
                    boolean ifContains = varKeys.contains(word);
                    if(isC){
                        if(!ifContains){
                            varKeys.add(word);
                            varVals.add("c"+String.valueOf(cIndex));
                            cIndex++;
                        }
                    } else if(isM){
                        if(!ifContains){
                            varKeys.add(word);
                            varVals.add("m"+String.valueOf(mIndex));
                            mIndex++;
                        }
                    } else{
                        if(!ifContains){
                            varKeys.add(word);
                            varVals.add("v"+String.valueOf(vIndex));
                            vIndex++;
                        }
                    }
                }
            }

            int len = varKeys.size();
            if(len<=0){
                tempGroup.append("{}");
            }else{
                tempGroup.append("{\"").append(varKeys.get(0)).append("\": \"")
                        .append(varVals.get(0)).append("\"");
                for(int i=1; i< len; i++){
                    tempGroup.append(", ")
                            .append("\"").append(varKeys.get(i)).append("\": \"")
                            .append(varVals.get(i)).append("\"");
                }
                tempGroup.append("}");
            }

            variableWriter.write(tempGroup.toString());
        }
        variableWriter.write("]");

        diffTextReader.close();
        variableWriter.close();
    }

    public static boolean isLetter(char c){
        return (c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9');
    }
}
