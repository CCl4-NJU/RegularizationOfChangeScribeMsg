import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonMsgRegularizor {
    private static String msgjsonpath = "G:\\nju.edu\\RegularizationOfChangeScribeMsg\\commitjsonmsgtransfer\\src\\main\\resources\\msg.json";
    private static String pathprefix = "G:\\DiffCommitFile\\download";
    private static String deletecommit = "G:\\DiffCommitFile\\output\\badcommit.txt";
    public static void main(String[] args){
        try {
            File file = new File(msgjsonpath);
            File dfile = new File(deletecommit);
            BufferedWriter deleteCommitWriter = new BufferedWriter(
                    new FileWriter(dfile)
            );

            String input = FileUtils.readFileToString(file, "UTF-8");
            JSONObject object = JSON.parseObject(input);
            JSONArray array = object.getJSONArray("msgs");
            int chineseCount = 0;
            for(int i=0; i<array.size(); i++){
                JSONObject item = (JSONObject) array.get(i);
                String commitName = (String)item.keySet().toArray()[0];
                String pathpostfix = commitName.replace("/commit","")
                        .replace("/","\\");
                String dirpath = pathprefix + pathpostfix;
                File realMsg = new File(dirpath+"\\realMsg.txt");

                BufferedWriter writer = new BufferedWriter(new FileWriter(realMsg));
                String commitMsg = item.getString(commitName);
                String[] tempMsgArr = commitMsg.substring(7).split("\\u2026\\s+\\u2026");
                StringBuilder finalMsg = new StringBuilder(tempMsgArr[0]);
                for(int j=1; j<tempMsgArr.length; j++){
                    finalMsg.append(" ").append(tempMsgArr[j]);
                }

                if(containChinese(finalMsg.toString())){
                    writer.close();
                    realMsg.delete();
                    chineseCount++;
                    deleteCommitWriter.write(commitName);
                    deleteCommitWriter.write("\n");
                } else{
                    writer.write(finalMsg.toString());
                    writer.close();
                }
            }
            deleteCommitWriter.close();
            System.out.println(chineseCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean containChinese(String str){
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

}
