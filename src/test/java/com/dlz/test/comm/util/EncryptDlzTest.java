package com.dlz.test.comm.util;

import com.dlz.comm.util.encry.Base64;
import com.dlz.comm.util.encry.ByteUtil;
import com.dlz.comm.util.encry.EncryptDlz;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 加密工具测试
 * 加密含有效期校验，随机数密码，混淆等
 * 支持单独无秘钥混淆加密
 *
 * @author dk 2024-03-14
 */
@Slf4j
public class EncryptDlzTest {
    String str = "中文_１２ａｂＡＢ＆＊（）_12abAB&*()_■△◎∧∨ ";
    String pass = "123";
    @Test
    public void test1(){
        EncryptDlz.MixEncryption mix = new EncryptDlz.MixEncryption(0, 0);
        for (int i = 1; i < 600000000; i++) {
            try {
                mix.getE(i);
            }catch (Exception e){
                System.out.println(i);
            }
        }
    }
    @Test
    public void testBase64(){
        byte[] decode2 = Base64.decode("vr6R67qV6LuQZDYzNGE1tJf+4ce2s7XX7+yJsJSM4Nv24IisIA==");
        System.out.println(ByteUtil.getStr(decode2));
    }
    @Test
    public void testWithJsResult1(){
        String re="１２３";
        String testRe = EncryptDlz.decryAsStr("7ad,y77T4nbe7Ry7a8kcf77Sy6", null);
        System.out.println(re.equals(testRe)+" :"+testRe);
    }
    @Test
    public void testWithJsResult2(){
        String re="１２３123abc■△＃＆◎∧∨&";
        String testRe = EncryptDlz.decryAsStr("Yo7p77kp.Ti7yea6JoyOyDbgaz4S4,7n4gMo77dp0Wi7e7yaa=InyGfz6jJT4R7c41", null);
        System.out.println(re.equals(testRe)+" :"+testRe);
    }
    @Test
    public void testJsResultWithPass(){
        String reFromJs="sr2rYbG6u49uQRpbsOW4FrHcCt+LALraDbbverQdni0QhVa,k+nnVu1-+43jiI0ulumxpue53z2UHafBgbnitie+rpiHa.9qhLl8g8";
        String pass="123";
        String testRe = EncryptDlz.decryAsStr(reFromJs, pass);
        System.out.println(str.equals(testRe)+" :"+testRe);
    }

    @Test
    public void testNopass1(){
        String encry1 = EncryptDlz.encry(str, null, 0);
        System.out.println("无密1：" + encry1);
        System.out.println("解无密1：" + EncryptDlz.decryAsStr(encry1, null));
    }

    @Test
    public void testNopass2() throws InterruptedException {
        String encry1 = EncryptDlz.encry(str, null, 5);
        System.out.println("无密30秒：" + encry1);
        System.out.println("解无密30秒：" + EncryptDlz.decryAsStr(encry1, null));
        for (int i = 0; i < 3; i++) {
            Thread.sleep(2000);
            System.out.println("解无密2-1：" + EncryptDlz.decryAsStr(encry1, null));
        }
    }
    @Test
    public void testWithpass1(){
        String encry1 = EncryptDlz.encry(str, pass, 0);
        System.out.println("有密：" + encry1);
        System.out.println("解密：" + EncryptDlz.decryAsStr(encry1, pass));
        System.out.println("解密-错误密码：" + EncryptDlz.decryAsStr(encry1, "xx"));
    }
    @Test
    public void testWithpass1_1(){
        String encry1 = "oLurDLM3-WKt8hF4orBbWbc4vFa1-mL,hOwAEOwLqTXgasWh3LyD6r-83db5879Ogb2EfuH-6OPlau5_1nhysrWO5.nt2_2jOL";
        System.out.println("有密：" + encry1);
        System.out.println("解密：" + EncryptDlz.decryAsStr(encry1, pass));
        final long l = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            EncryptDlz.decryAsStr(encry1, pass);
        }
        System.out.println("解密：" + (System.currentTimeMillis() - l));
    }
    @Test
    public void testWithpass1_2(){
        String encry1 = "oLurDLM3-WKt8hF4orBbWbc4vFa1-mL,hOwAEOwLqTXgasWh3LyD6r-83db5879Ogb2EfuH-6OPlau5_1nhysrWO5.nt2_2jOL";
//        encry1+=encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1;
//        encry1+=encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1+encry1;
        final EncryptDlz.MixEncryption fserver = new EncryptDlz.MixEncryption(0, 0);
        System.out.println("有密：" + encry1.length());
        System.out.println("解密：" + EncryptDlz.decryAsStr(encry1, pass));
    }
    @Test
    public void testWithpass2() throws InterruptedException {
        String encry1 = EncryptDlz.encry(str, pass, 50);
        System.out.println("有密-5秒：" + encry1.length()+" "+encry1);
        System.out.println("解密-5秒：" + EncryptDlz.decryAsStr(encry1, pass));
        System.out.println("解密-错误密码：" + EncryptDlz.decryAsStr(encry1, "xx"));
        for (int i = 0; i < 3; i++) {
            Thread.sleep(2000);
            System.out.println("解密-超时解密：" + EncryptDlz.decryAsStr(encry1, pass));
        }
    }
    @Test
    public void testWithpasstime() throws InterruptedException {
        String encry1 = EncryptDlz.encry(str, pass, 30);
        System.out.println("有密-5秒：" + encry1);
        System.out.println("解密-5秒：" + EncryptDlz.decryAsStr(encry1, pass));
        System.out.println("解密-错误密码：" + EncryptDlz.decryAsStr(encry1, "xx"));
        for (int i = 0; i < 3; i++) {
            Thread.sleep(2000);
            System.out.println("解密-超时解密：" + EncryptDlz.decryAsStr(encry1, pass));
        }
    }
    @Test
    public void testWithpassFromJs() throws InterruptedException {
//        String input=".eiA2,Gro774fe8L2oL8K+3D8lCAs1ty4O8pr7TdL7OH7WTD21XkIOb-_53PWORdj8HB9wwkcuwu0nKZeeeKTx3H+ByvtSkr2/INabV.226Lag2+72";
        String input="5ovfeL_x3lR7Keq7zYG7mtSOj6ZL5IA53071XOq7uGJVbua5T,nOhgGe3jpe3Jyb-bCwjbisMUWRGn58WiCrjyGuZ0yur.7P.ZmqLLi4F38DaP";
        System.out.println("解密-5秒：" + EncryptDlz.decryAsStr(input, pass));
        System.out.println("解密-错误密码：" + EncryptDlz.decryAsStr(input, "xx"));
        for (int i = 0; i < 10; i++) {
            Thread.sleep(2000);
            System.out.println("解密-超时解密：" + EncryptDlz.decryAsStr(input, pass));
        }
    }
}
