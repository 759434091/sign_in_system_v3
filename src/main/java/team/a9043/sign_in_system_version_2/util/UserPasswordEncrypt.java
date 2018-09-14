package team.a9043.sign_in_system_version_2.util;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * UserPasswordEncrypt
 * 密码加密算法
 */
public class UserPasswordEncrypt {
    /**
     * 加密
     *
     * @param userOriginalPassword 原始密码
     * @return 加密密码
     */
    public static String encrypt(String userOriginalPassword) {
        if (userOriginalPassword != null) {
            String userEncryptPassword = "";
            try {
                MessageDigest mDigest = MessageDigest.getInstance("MD5");
                userEncryptPassword = Base64.encodeBase64String(mDigest.digest(userOriginalPassword.getBytes()));
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            return userEncryptPassword;
        } else {
            return null;
        }
    }
}
