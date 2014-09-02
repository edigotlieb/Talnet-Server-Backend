package Utilities;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.UUID;

/**
 *
 * Defines Utilities for hashing stuff
 */
public class Hashing {

	static MessageDigest md5;

        // static init
	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
		}
	}

        /**
         * Hashes an ascii string using MD5 hash
         * 
         * @param myStr the string to hash
         * @return the hashed string in hex format
         */
	public static String MD5Hash(String myStr) {
		byte[] bytesOfMessage;
		try {                    
			bytesOfMessage = myStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException ex) {
			return myStr;
		}
		byte[] thedigest = md5.digest(bytesOfMessage);
                return toHex(thedigest);           
	}
        
        /**
         * Converts byte array to hex
         * 
         * @param in the byte array to convert
         * @return the resulting hex format string
         */
        public static String toHex(byte[] in) {
        StringBuilder out = new StringBuilder(in.length * 2);
        for (byte b: in) {
            out.append(String.format("%02X", (byte) b));
        }
        return out.toString().toLowerCase();
         }
        
        
        /**
         * creates a random string
         * 
         * @return a random string
         */
	public static String generateChallenge() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
