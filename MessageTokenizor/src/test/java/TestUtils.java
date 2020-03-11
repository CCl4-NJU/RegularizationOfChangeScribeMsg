import org.junit.Assert;
import org.junit.Test;

import javax.naming.Name;

public class TestUtils {
    @Test
    public void testIsCaps(){
        Assert.assertTrue(NameUtils.isCapitalized('A'));
        Assert.assertTrue(NameUtils.isCapitalized('G'));
        Assert.assertTrue(NameUtils.isCapitalized('Z'));
        Assert.assertFalse(NameUtils.isCapitalized('a'));
        Assert.assertFalse(NameUtils.isCapitalized('z'));
        Assert.assertFalse(NameUtils.isCapitalized('t'));
    }

    @Test
    public void testClassName(){
        Assert.assertTrue(NameUtils.isClassName("HttpRequestEncoder"));
        Assert.assertFalse(NameUtils.isClassName("encodeInitialLine"));
        Assert.assertFalse(NameUtils.isClassName("MAX_VALUE"));
        Assert.assertFalse(NameUtils.isClassName("VARIABLE"));
    }

    @Test
    public void testMethodName(){
        Assert.assertFalse(NameUtils.isMethodName("HttpRequestEncoder"));
        Assert.assertTrue(NameUtils.isMethodName("encodeInitialLine"));
        Assert.assertFalse(NameUtils.isMethodName("MAX_VALUE"));
        Assert.assertFalse(NameUtils.isMethodName("VARIABLE"));
    }

    @Test
    public void testStaticVarName(){
        Assert.assertFalse(NameUtils.isStaticVarName("HttpRequestEncoder"));
        Assert.assertFalse(NameUtils.isStaticVarName("encodeInitialLine"));
        Assert.assertTrue(NameUtils.isStaticVarName("MAX_VALUE"));
        Assert.assertTrue(NameUtils.isStaticVarName("VARIABLE"));
    }

    @Test
    public void testSplitCamelName(){
        String[] strArr1 = {"http", "request", "encoder"};
        String[] strArr2 = {"get", "method"};

        String[] resArr1 = NameUtils.splitCamelName("HttpRequestEncoder");
        String[] resArr2 = NameUtils.splitCamelName("getMethod");
        Assert.assertEquals(strArr1[0], resArr1[0]);
        Assert.assertEquals(strArr1[1], resArr1[1]);
        Assert.assertEquals(strArr1[2], resArr1[2]);

        Assert.assertEquals(strArr2[0], resArr2[0]);
        Assert.assertEquals(strArr2[1], resArr2[1]);
    }

    @Test
    public void testSplitStaticVarName(){
        String[] strArr1 = {"namespace"};
        String[] strArr2 = {"utf","8u"};
        String[] strArr3 = {"max","value"};
        String[] strArr4 = {"american", "encoder", "list"};

        String[] resArr1 = NameUtils.splitStaticVarName("NAMESPACE");
        String[] resArr2 = NameUtils.splitStaticVarName("UTF_8U");
        String[] resArr3 = NameUtils.splitStaticVarName("MAX_VALUE");
        String[] resArr4 = NameUtils.splitStaticVarName("AMERICAN_ENCODER_LIST");

        for(int i=0,j=0,k=0; i<3; i++,j++,k++){
            if(j<1){
                Assert.assertEquals(strArr1[j], resArr1[j]);
                Assert.assertEquals(strArr2[j], resArr2[j]);
            }
            if(k<2){
                Assert.assertEquals(strArr3[k], resArr3[k]);
            }
            Assert.assertEquals(strArr4[i], resArr4[i]);
        }
    }
}
