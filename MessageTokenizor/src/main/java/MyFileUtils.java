import java.io.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.*;

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

    public static void changeMsgText2MsgJson(String msgtextPaht, String msgjsonPath) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(new File(msgtextPaht)));
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(msgjsonPath)));

        String line = null;
        writer.write("{\n");

        while((line=reader.readLine())!=null){
            String[] words = line.split("\\W+");
            StringBuilder writeLine = new StringBuilder();
            for(String word:words){
                writeLine.append(word).append(" ");
            }
            writer.write("\"");
            writer.write(writeLine.toString());
            writer.write("\"");
            writer.write(",\n");
        }

        writer.write("}");
        writer.close();
        reader.close();
    }

    public static void generateMsg(String msgTextPath, String msgTokenJsonPath, Set<String> wordSet)
    throws Exception{
        BufferedReader rawMsgReader = new BufferedReader(new FileReader(new File(msgTextPath)));
        BufferedWriter msgTokenWriter = new BufferedWriter(new FileWriter(new File(msgTokenJsonPath)));

        String line = null;

        String regex = "\\W+";

        boolean firstLine = true;
        msgTokenWriter.write("[");
        while((line=rawMsgReader.readLine())!=null){

            int totalNum = 0;
            int maxNum = 20; // max number of msg token

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

                if(!isOOV(token)){
                    wordSet.add(token.toLowerCase());
                }

                totalNum++;
                firstWord = false;
                if(totalNum >= maxNum){
                    break;
                }
            }
            dataLine.append("]");
            msgTokenWriter.write(dataLine.toString());
        }
        msgTokenWriter.write("]");

        rawMsgReader.close(); msgTokenWriter.close();
    }

    public static void generateDiffTokenAndMarkAndAtt(String diffTextPath, String diffTokenPath,
                                                      String diffMarkPath, String diffAttPath, Set<String> wordSet)
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
                    //tempTokenList.add(subConnectedVar);
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
                            if(!isOOV(tempToken.toString())){
                                wordSet.add(tempToken.toString().toLowerCase());
                            }
                        }
                    }
                    else{
                        if(tempToken.length()>=1) {
                            tempTokenList.add(tempToken.toString());

                            if(!isOOV(tempToken.toString())){
                                wordSet.add(tempToken.toString().toLowerCase());
                            }

                            tempToken = new StringBuilder();
                        }
                        //tempTokenList.add(String.valueOf(c));
                        index++;
                    }
                }
            }

            int totalNum = 1; //take <nb> in count
            int maxNum = 200; //max number of diff text token

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
                if(NameUtils.isClassName(token)||NameUtils.isMethodName(token)||NameUtils.isStaticVarName(token)){
                    if(!NameUtils.isStaticVarName(token)) {
                        String[] splitCamelWords = NameUtils.splitCamelName(token);
                        StringBuilder camelStr = new StringBuilder();
                        camelStr.append(", [\"").append(splitCamelWords[0]).append("\"");
                        for (int i = 1; i < splitCamelWords.length; i++) {
                            camelStr.append(", \"").append(splitCamelWords[i]).append("\"");
                        }
                        camelStr.append("]");
                        attLine.append(camelStr.toString());
                    } else{
                        String[] splitSVWords = NameUtils.splitStaticVarName(token);
                        StringBuilder sVarStr = new StringBuilder();
                        sVarStr.append(", [\"").append(splitSVWords[0]).append("\"");
                        for (int i = 1; i < splitSVWords.length; i++) {
                            sVarStr.append(", \"").append(splitSVWords[i]).append("\"");
                        }
                        sVarStr.append("]");
                        attLine.append(sVarStr.toString());
                    }
                } else{
                    attLine.append(", []");
                }

                totalNum++;
                if(totalNum>=maxNum){
                    break;
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

    public static void generateVariable(String diffTextPath, String msgTextPath, String variablePath
            , String numjsonPath)throws Exception{
        BufferedReader diffTextReader = new BufferedReader(new FileReader(new File(diffTextPath)));
        BufferedReader msgTextReader = new BufferedReader(new FileReader(new File(msgTextPath)));
        BufferedWriter variableWriter = new BufferedWriter(new FileWriter(new File(variablePath)));
        BufferedWriter numjsonWriter = new BufferedWriter(new FileWriter(new File(numjsonPath)));

        long nmCount = 0;
        Set<String> varSet = new HashSet<>();

        variableWriter.write("[");

        String line = null;
        String msgLine = null;
        boolean firstLine = true;

        while((line=diffTextReader.readLine())!=null&&((msgLine=msgTextReader.readLine())!=null)){
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
                    varSet.add(word.toLowerCase());
                    boolean ifContains = varKeys.contains(word);
                    if(isC){
                        if(!ifContains){
                            nmCount++;
                            varKeys.add(word);
                            varVals.add("c"+String.valueOf(cIndex));
                            cIndex++;
                        }
                    } else if(isM){
                        if(!ifContains){
                            nmCount++;
                            varKeys.add(word);
                            varVals.add("m"+String.valueOf(mIndex));
                            mIndex++;
                        }
                    } else{
                        if(!ifContains){
                            nmCount++;
                            varKeys.add(word);
                            varVals.add("v"+String.valueOf(vIndex));
                            vIndex++;
                        }
                    }
                }
            }

            msgLine = msgLine.replaceAll("\\\\t"," ")
                    .replaceAll("\\\\n", " <nl> ")
                    .replaceAll("\\\\\"", " ");

            String[] wordList2 = msgLine.split("\\W+");
            for(String word2:wordList2){
                boolean isC = NameUtils.isClassName(word2);
                boolean isM = NameUtils.isMethodName(word2);
                boolean isV = NameUtils.isStaticVarName(word2);
                if(isC||isM||isV){
                    varSet.add(word2.toLowerCase());
                    boolean ifContains = varKeys.contains(word2);
                    if(isC){
                        if(!ifContains){
                            nmCount++;
                            varKeys.add(word2);
                            varVals.add("c"+String.valueOf(cIndex));
                            cIndex++;
                        }
                    } else if(isM){
                        if(!ifContains){
                            nmCount++;
                            varKeys.add(word2);
                            varVals.add("m"+String.valueOf(mIndex));
                            mIndex++;
                        }
                    } else{
                        if(!ifContains){
                            nmCount++;
                            varKeys.add(word2);
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
        numjsonWriter.write(String.valueOf(varSet.size()));
        numjsonWriter.close();

        diffTextReader.close();
        variableWriter.close();
    }

    public static void generateWord2index(String word2indexPath, Set<String> words) throws Exception{
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(word2indexPath)));
        bufferedWriter.write("{\n");
        bufferedWriter.write("\"<eos>\": 0,\n");
        bufferedWriter.write("\"<start>\": 1,\n");
        bufferedWriter.write("\"<unkm>\": 2");
        int id = 3;
        for(String word : words){
            if(isEnglish(word)) {
                bufferedWriter.write(",\n");
                bufferedWriter.write("\"" + word + "\": " + id);
                id++;
            }
        }
        bufferedWriter.write("}");
        bufferedWriter.close();
    }

    public static boolean isLetter(char c){
        return (c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')||(c=='_');
    }

    public static boolean isOOV(String word){
        return NameUtils.isClassName(word)||NameUtils.isMethodName(word)||NameUtils.isStaticVarName(word);
    }

    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }
}
