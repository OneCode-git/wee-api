/**
 * 
 */
package com.wee.util;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;

/**
 * @author chaitu
 *
 */
public class Commons {
	
	   public static String getMd5(String input) 
	    { 
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
	   public static String getAlphaNumericString(int n) 
	    { 
	  
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
	                = (int)(AlphaNumericString.length() 
	                        * Math.random()); 
	  
	            // add Character one by one in end of sb 
	            sb.append(AlphaNumericString 
	                          .charAt(index)); 
	        } 
	  
	        return sb.toString(); 
	    }
	   
	   	public static String genHash(String url) {
	   		String md5 = getMd5(url+getAlphaNumericString(6));
	   		String base64Md5 = Base64.getEncoder().encodeToString(md5.getBytes(StandardCharsets.UTF_8));
	   		return base64Md5.substring(0, 6);
	   	}
	   
	   	public static Capabilities parseUserAgent(String userAgent) throws IOException, ParseException {
	   		final UserAgentParser parser = new UserAgentService().loadParser(); // handle IOException and ParseException
	   		return parser.parse(userAgent);

	   	}
	   	
	    /* Returns true if url is valid */
	    public static boolean isValidURL(String url) 
	    { 
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
