package edu.uwm.infost325;
import java.security.MessageDigest;
import java.util.Scanner;
import java.io.*;
import javax.crypto.*;
import java.util.Enumeration;


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
		
	public static String getHash(byte[] inputByte, String algorithm){
		String hashValue = "";
		try{
			
			MessageDigest messageD1 = MessageDigest.getInstance(algorithm);
			messageD1.update(inputByte);
			byte[] digestedBytes = messageD1.digest();
			hashValue = DatatypeConverter.printHexBinary(digestedBytes).toLowerCase();
			
		}
		catch(Exception e){
			
			System.out.println("There was an error: " + e);
		}
		return hashValue;
	}
	
}

