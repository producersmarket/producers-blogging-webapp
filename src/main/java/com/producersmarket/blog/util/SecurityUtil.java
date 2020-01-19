package com.producersmarket.blog.util;

import java.security.Provider;
import java.security.Security;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author  Dermot Doherty
 */

public class SecurityUtil {

    private static final Logger logger = LogManager.getLogger();

    /**
     * make an SHA1 hash of the password and make into a Radix 16 String
     * This is what gets stored in the database, we don't use salt as we never pass the hashed password in clear text.
     */
    public static String hashPassword(String pw) {

        java.security.MessageDigest messageDigest;

        try {

            messageDigest = java.security.MessageDigest.getInstance("SHA1");

        } catch(java.security.NoSuchAlgorithmException e) {
            logger.error("Could not load SHA1 algorithm");
            return null;
        }

        try {

            messageDigest.update(pw.getBytes("UTF8"));

        } catch(Exception e) {
            logger.error("Encoding error: " + e.getMessage());
            return null;
        }

        byte out[] = messageDigest.digest();
        java.math.BigInteger bigInteger = new java.math.BigInteger(1, out);

        return bigInteger.toString(16);
    }

    /**
     * Combine The sessionid with the password as an SHA1 to create an auth token,
     */
    public static String makeAuthToken(String id, String pw) {

        String[] inp = new String[] { id, pw };
        byte[] out = makeHash(inp);

        // Convert to HEX string (or use base64 or base32).
        java.math.BigInteger bi = new java.math.BigInteger(1, out);
        return bi.toString(16);
    }

    public static byte[] makeHash(String[] inp) {

        java.security.MessageDigest md;
        try {

            md = java.security.MessageDigest.getInstance("SHA1");

        } catch(java.security.NoSuchAlgorithmException ex) {
            logger.error("Could not load SHA1 algorithm");
            md = null;
        }

        for(String anInp: inp) md.update(anInp.getBytes());

        byte [] out = md.digest();

        return out;
    }

    public static String string2Md5Hmac(String keyString, String message)  {

        Provider sunJce = new com.sun.crypto.provider.SunJCE();
        Security.insertProviderAt(sunJce,0);
        SecretKey key = new SecretKeySpec(keyString.getBytes(), "HmacMD5");
        try {
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);
            return toHex(mac.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create signature", e);
        }
    }

    public static String string2Sha1Hmac(String keyString, String message)  {

        Provider sunJce = new com.sun.crypto.provider.SunJCE();
        Security.insertProviderAt(sunJce,0);
        SecretKey key = new SecretKeySpec(keyString.getBytes(), "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            return toHex(mac.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create signature", e);
        }
    }

    public static String toHex(byte[] byteArray) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < byteArray.length; ++i) {
            String hx = Integer.toHexString(0xFF & byteArray[i]);
            if(hx.length() == 1) hx = "0" + hx;
            sb.append(hx);
        }
        return sb.toString();
    }

}
