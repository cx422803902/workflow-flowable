package com.workflow.flowable.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public class ThalesTestUtil {
    private static Logger logger = LoggerFactory.getLogger(ThalesTestUtil.class);
    private static String key = "zK8Gv+ycuzumesWziXH4yA==";

    /**
     * 自动生成AES128位密钥
     */
    public static String generateKey() {
        String key = null;
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, new SecureRandom("password".getBytes()));
            SecretKey sk = kg.generateKey();
            byte[] b = sk.getEncoded();
            key = Base64.getEncoder().encodeToString(b);
        }
        catch (NoSuchAlgorithmException e) {
            logger.warn("warn", e);
        }
        return key;

    }

    /**
     * 加密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(String content) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes());

        return encrypted;
    }

    /**
     * 解密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] content) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] original = cipher.doFinal(content);
        return new String(original);
    }


    /**
     * AES加密为base 64 code
     *
     * @param content 待加密的内容
     * @return 加密后的base 64 code
     * @throws Exception //加密传String类型，返回String类型
     */
    public static String aesEncrypt(String content) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(content));
    }

    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @return 解密后的string   //解密传String类型，返回String类型
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : decrypt(Base64.getDecoder().decode(encryptStr));
    }

    public static void main(String[] args) throws Exception {
        int sum = 100;
        String s = "";
        for(int i = 0; i < 100;i ++) {
            s += "a";
        }
        long start = System.nanoTime();
        for(int i = 0; i < 1000; i++) {
            encrypt(s);
        }
        long end = System.nanoTime();
        System.out.println(end - start);
//        for(int i = 0; i < sum; i++) {
//            s += "a";
//            long start = System.nanoTime();
//            encrypt(s);
//            long end = System.nanoTime();
//            times[i] = end - start;
//            //System.out.println(generateKey());
//        }
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\test.txt")));
//        for(int i = 0; i < sum; i++) {
//            writer.write((i+1)+"\t"+times[i]+"\n");
//        }
//        writer.close();

    }

}
