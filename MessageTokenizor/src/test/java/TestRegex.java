import org.junit.Test;

public class TestRegex {

    @Test
    public void testRegexReplace(){
        String line = "This change set is mainly composed of:  \\n Changes to package org.immutables.generator:  \\nModifications to AnnotationMirrors.java\\n\\tAdd line comment at printValue(AnnotationValue) method\\n\\tModify conditional expression Compiler.JAVAC.isPresent() && (value instanceof Attribute.UnresolvedClass) with (Compiler.JAVAC.isPresent() && \\\"com.sun.tools.javac.code.Attribute.UnresolvedClass\\\".equals(value.getClass().getCanonicalName())) at printValue(AnnotationValue) method\\n\\n\\n\n";
        System.out.println(line.replaceAll("\\\\t"," ")
                .replaceAll("\\\\n", " <nl> ")
                .replaceAll("\\\\\"", " "));
    }

    @Test
    public void testSplitSign(){
        String regex = "\\W+";
        String line = "This is @@@ sss bb _sdd SE_SED";
        System.out.println(line.split(regex).length);
        for(String s : line.split(regex)){
            System.out.println(s);
        }
    }

    @Test
    public void testEnglish(){
        System.out.println(MyFileUtils.isEnglish("asdfase2sags3544rg"));
        System.out.println(MyFileUtils.isEnglish("apple"));
    }

    @Test
    public void testW(){
        String line = "asdfd\"sswe 4#@\"sda ds\"sdd";
        System.out.println(line.split("\\W+").length);
    }
}
