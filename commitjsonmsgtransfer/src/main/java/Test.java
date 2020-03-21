public class Test {

    public static String testStr = "     abc \u2026     \u2026 defg";
    public static void main(String[] args){
        System.out.println(testStr.split("\\u2026\\s+\\u2026").length);
    }
}
