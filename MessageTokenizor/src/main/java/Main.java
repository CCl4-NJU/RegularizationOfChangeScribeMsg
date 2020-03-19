import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    //file path of data extracted from ChangeScribe
    public static String CS_MSG_PATH;

    //file path of commit msg extracted from github
    public static String CT_MSG_PATH;

    //file path of json file difftext.json
    public static String DIFF_TEXT_PATH = "G:\\DiffCommitFile\\input\\difftext_manual_v1.txt";
    public static String MSG_TEXT_PATH = "G:\\DiffCommitFile\\input\\msgtext_manual_v1.txt";

    //file path of variable dictionary json file
    public static String VARIABLE_PATH;

    public static void main(String[] args){
        try {
            MyFileUtils.changeTextToJson(
                    DIFF_TEXT_PATH,
                    "G:\\nju.edu\\RegularizationOfChangeScribeMsg\\MessageTokenizor\\src\\main\\resources\\test-result\\difftext.json"
            );
            MyFileUtils.changeTextToJson(
                    MSG_TEXT_PATH,
                    "G:\\nju.edu\\RegularizationOfChangeScribeMsg\\MessageTokenizor\\src\\main\\resources\\test-result\\msgtext.json"
                    );
            MyFileUtils.generateMsg(
                    MSG_TEXT_PATH,
                    "G:\\nju.edu\\RegularizationOfChangeScribeMsg\\MessageTokenizor\\src\\main\\resources\\test-result\\msgtoken.json"
                    );
            MyFileUtils.generateDiffTokenAndMarkAndAtt(
                    DIFF_TEXT_PATH,
                    "G:\\nju.edu\\RegularizationOfChangeScribeMsg\\MessageTokenizor\\src\\main\\resources\\test-result\\difftoken.json",
                    "G:\\nju.edu\\RegularizationOfChangeScribeMsg\\MessageTokenizor\\src\\main\\resources\\test-result\\diffmark.json",
                    "G:\\nju.edu\\RegularizationOfChangeScribeMsg\\MessageTokenizor\\src\\main\\resources\\test-result\\diffatt.json"
            );
            MyFileUtils.generateVariable(
                    DIFF_TEXT_PATH,
                    MSG_TEXT_PATH,
                    "G:\\nju.edu\\RegularizationOfChangeScribeMsg\\MessageTokenizor\\src\\main\\resources\\test-result\\variable.json"
                    );
        } catch (Exception e){
            e.printStackTrace();
        }
//        testTxt2Json();
    }

//    public static void testTxt2Json(){
//        CT_MSG_PATH = "G:\\DiffCommitFile\\CoDiSumData\\msgtext_immutables.txt";
//        MSG_TOKEN_PATH = "G:\\nju.edu\\MessageTokenizor\\src\\main\\resources\\test-result\\msgToken.json";
//        MSG_TEXT_PATH = "G:\\nju.edu\\MessageTokenizor\\src\\main\\resources\\test-result\\msgText.json";
//        try {
//            MyFileUtils.generateMsg(CT_MSG_PATH, MSG_TOKEN_PATH);
//            MyFileUtils.changeTextToJson(CT_MSG_PATH, MSG_TEXT_PATH);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

}
