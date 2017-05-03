package edu.uwm.infost325;
import java.security.MessageDigest;
import java.util.Scanner;
import java.io.*;
import javax.crypto.*;
import java.util.Enumeration;


import javax.xml.bind.DatatypeConverter;
class Hash2 {
	
	public static MessageDigest Name1(InputStream i, OutputStream oS, Cipher hrm, String update)
		throws FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException{
			
			MessageDigest a = MessageDigest.getInstance("SHA-256");
			byte[] buffer = new byte[512];
			int bytesToRead = 0;
			int totalBytes;
			while ((bytesToRead = hey.read(buffer)) > 0){
				byte[] cipherLearn = hrm.update(buffer, 0, bytesToRead);
				if (cipherLearn != null){
					hello.write(cipherLearn);
					a.update(hello);
					a.digest();
				}
			}
			String hashValue = "";
				
				MessageDigest messageD1 = MessageDigest.getInstance();
				byte[] digestedBytes = messageD1.digest();
				hashValue = DatatypeConverter.printHexBinary(digestedBytes).toLowerCase();
				
			return a;
	}
	
}

