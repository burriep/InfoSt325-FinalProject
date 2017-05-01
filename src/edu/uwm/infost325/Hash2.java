
package edu.uwm.infost325;
import java.security.*;
import java.util.Scanner;
import java.io.*;
import javax.crypto.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;
class Hash2 {
	
	public static void Name1(InputStream hey, OutputStream hello, Cipher hrm)
		throws FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException{
			byte[] buffer = new byte[512];
			int bytesToRead = 0;
			while ((bytesToRead = hey.read(buffer)) > 0){
				byte[] cipherLearn = hrm.update(buffer, 0, bytesToRead);
				if (cipherLearn != null){
					hello.write(cipherLearn);
				}
			}
			byte[] cipherLearn = hrm.doFinal();
			if (cipherLearn != null) {
				hello.write(cipherLearn);
			}		
	}
	static public class InvalidHashException extends Exception {
        public InvalidHashException(String message) {
            super(message);
        }
        public InvalidHashException(String message, Throwable source) {
            super(message, source);
        }
    }

    @SuppressWarnings("serial")
    static public class CannotPerformOperationException extends Exception {
        public CannotPerformOperationException(String message) {
            super(message);
        }
        public CannotPerformOperationException(String message, Throwable source) {
            super(message, source);
        }
    }

    
        public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
	    public static final int SALT_BYTE_SIZE = 24;
	    public static final int HASH_BYTE_SIZE = 18;
	    public static final int PBKDF2_ITERATIONS = 64000;

	    // These constants define the encoding and may not be changed.
	    public static final int HASH_SECTIONS = 5;
	    public static final int HASH_ALGORITHM_INDEX = 0;
	    public static final int ITERATION_INDEX = 1;
	    public static final int HASH_SIZE_INDEX = 2;
	    public static final int SALT_INDEX = 3;
	    public static final int PBKDF2_INDEX = 4;

	   
	    public static String createHash(String password)
	        throws CannotPerformOperationException
	    {
	        return createHash(password.toCharArray());
	    }

	    // These constants may be changed without breaking existing hashes.
	    public static byte[] pbkdf2(char[] tells, byte[] next, int iterations, int size){
	    	next.length = size;
	    	ByteBuffer.wrap(next).asCharBuffer().put(tells);
	 
	    	for(int i=0; i<iterations; i++) {
	    	    next[i] = (byte) ((tells[i]&0xFF00)>>8); 
	    	    next[i+1] = (byte) (tells[i]&0x00FF); 
	    	}
	    	return next;
	    }
	    
	    public static String createHash(char[] password)
	        throws CannotPerformOperationException
	    {
	        // Generate a random salt
	        SecureRandom random = new SecureRandom();
	        byte[] salt = new byte[SALT_BYTE_SIZE];
	        random.nextBytes(salt);

	        // Hash the password
	        byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
	        int hashSize = hash.length;

	        // format: algorithm:iterations:hashSize:salt:hash
	        String parts = "sha1:" +
	            PBKDF2_ITERATIONS +
	            ":" + hashSize +
	            ":" +
	            toBase64(salt) +
	            ":" +
	            toBase64(hash);
	        return parts;
	    }
	    

	    public static boolean verifyPassword(String password, String correctHash)
	        throws CannotPerformOperationException, InvalidHashException
	    {
	        return verifyPassword(password.toCharArray(), correctHash);
	    }

	    public static boolean verifyPassword(char[] password, String correctHash)
	        throws CannotPerformOperationException, InvalidHashException
	    {
	        // Decode the hash into its parameters
	        String[] params = correctHash.split(":");
	        if (params.length != HASH_SECTIONS) {
	            throw new InvalidHashException(
	                "Fields are missing from the password hash."
	            );
	        }

	        // Currently, Java only supports SHA1.
	        if (!params[HASH_ALGORITHM_INDEX].  equals("sha1")) {
	            throw new CannotPerformOperationException(
	                "Unsupported hash type."
	            );
	        }

	        int iterations = 0;
	        try {
	            iterations = Integer.parseInt(params[ITERATION_INDEX]);
	        } catch (NumberFormatException ex) {
	            throw new InvalidHashException(
	                "Could not parse the iteration count as an integer.",
	                ex
	            );
	        }

	        if (iterations < 1) {
	            throw new InvalidHashException(
	                "Invalid number of iterations. Must be >= 1."
	            );
	        }


	        byte[] salt = null;
	        try {
	            salt = fromBase64(params[SALT_INDEX]);
	        } catch (IllegalArgumentException ex) {
	            throw new InvalidHashException(
	                "Base64 decoding of salt failed.",
	                ex
	            );
	        }

	        byte[] hash = null;
	        try {
	            hash = fromBase64(params[PBKDF2_INDEX]);
	        } catch (IllegalArgumentException ex) {
	            throw new InvalidHashException(
	                "Base64 decoding of pbkdf2 output failed.",
	                ex
	            );
	        }

	}
