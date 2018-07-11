package aar.zeffect.cn.okdownservice.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5值加密
 *
 * @author zzx
 */
public class MD5Crypto {
    /**
     * Md5 32位 or 16位 加密(只返回8~24之间的字符)
     *
     * @param plainText 待加密内容
     * @return 16位加密
     */
    public static String Md5_16(String plainText) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();
            int i;
            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            // Log.e("555","result: " + buf.toString());//32位的加密
            // Log.e("555","result: " + buf.toString().substring(8,24));//16位的加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (buf == null) {
            return null;
        }
        return buf.toString().substring(8, 24);
    }

    /**
     * Md5 32位 or 16位 加密（全部返回）
     *
     * @param plainText 待加密内容
     * @return 32位加密
     */
    public static String Md5_32(String plainText) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();
            int i;
            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (buf == null) {
            return "";
        }
        return buf.toString().toLowerCase();
    }

}
