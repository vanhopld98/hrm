package vn.com.humanresourcesmanagement.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
public class PasswordUtils {

    private PasswordUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordUtils.class);

    public static String encryptMD5(final String s) {
        final var MD5 = "MD5";
        try {
            // Create MD5 Hash
            var digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            var hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();
        } catch (Exception e) {
            LOGGER.error("[PASSWORD_UTILS][ENCODE MD5] Exception: {}", e.getMessage());
            return null;
        }
    }

}
