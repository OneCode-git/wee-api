/**
 *
 */
package com.wee.util;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author chaitu
 *
 */
public class Commons {

    private static final int MAX_WORKER_ID = 32;
    private static final SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(getWorkerId(), 1); // or any workerId, datacenterId
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static int getWorkerId() {
        String podIP = System.getenv("POD_IP");
        return Math.abs(podIP.hashCode()) % MAX_WORKER_ID;
    }

    public static String genShortCode() {
        long id = snowflakeIdGenerator.nextId();
		long truncatedId = id & 0xFFFFFFFFFFFFL;
        return base62Encode(truncatedId);
    }


    private static String base62Encode(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int idx = (int) (value % 62);
            sb.append(BASE62.charAt(idx));
            value /= 62;
        }
        return sb.reverse().toString();
    }

	private static long base62Decode(String base62String) {
		long result = 0L;
		for (int i = 0; i < base62String.length(); i++) {
			result = result * 62 + BASE62.indexOf(base62String.charAt(i));
		}
		return result;
	}

    public static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // function to generate a random string of length n
    public static String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static String genHash(String url) {
        String md5 = getMd5(url + getAlphaNumericString(8));
        String base64Md5 = Base64.getEncoder().encodeToString(md5.getBytes(StandardCharsets.UTF_8));
        return base64Md5.substring(0, 9);
    }

    public static Capabilities parseUserAgent(String userAgent) throws IOException, ParseException {
        final UserAgentParser parser = new UserAgentService().loadParser(); // handle IOException and ParseException
        return parser.parse(userAgent);

    }

    /* Returns true if url is valid */
    public static boolean isValidURL(String url) {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

}
