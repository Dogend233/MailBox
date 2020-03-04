package com.tripleying.qwq.MailBox.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具
 */
public class EncryptUtil {

    /**
     * MD5
     * @param str 明码
     * @return 密码
     */
    public static String MD5(String str){
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(str.getBytes("UTF8"));
            byte s[] = m.digest();
            String result = "";
            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
            }
            return result;
	} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            System.out.println(e);
	}
        return "";
    }

}
