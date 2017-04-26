package edu.uwm.infost325;

import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * @author Paul Burrie Some of this functionality is based off work I did in my
 *         cyber security lab in a previous semester, but it needed to be
 *         customized to fit the needs of this project.
 */
public class CryptController {

	// notes for next steps:
	// for key, use base64 characters: A-Z a-z 0-9 + /
	// max value (22 characters, last one must be A-D): /////////////////////3
	// use the Base64.Decoder class as needed to convert to byte[]

	public static void encrypt(File input, File output, byte[] raw_key, AsyncController controller) {
		try {
			SecretKeySpec key = new SecretKeySpec(raw_key, "AES");
			// Create Cipher
			Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// get cipher parameter ready
			byte[] iv = new byte[16]; // 128 bit IV
			(new SecureRandom()).nextBytes(iv);
			IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
			// Initialize Cipher with key and parameters
			aesCipher.init(Cipher.ENCRYPT_MODE, key, ivParamSpec);
			try (InputStream fis = new BufferedInputStream(new FileInputStream(input))) {
				try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(output))) {
					controller.setMaxProgress(input.length());
					// write the IV to the output file
					fos.write(iv);
					// write the encrypted file to the output file
					crypt(fis, fos, aesCipher, controller);
				}
			}
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			// crypto problem
			e.printStackTrace();
		} catch (IOException e) {
			// file problem
			e.printStackTrace();
		}
	}

	public static void decrypt(File input, File output, byte[] raw_key, AsyncController controller) {
		try {
			SecretKeySpec key = new SecretKeySpec(raw_key, "AES");
			// Create Cipher
			Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// get cipher parameter ready
			byte[] iv = new byte[16]; // 128 bit IV
			// setup file input stream
			try (InputStream fis = new BufferedInputStream(new FileInputStream(input))) {
				controller.setMaxProgress(input.length());
				// read in IV
				fis.read(iv);
				IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
				// Initialize Cipher with key and parameters
				aesCipher.init(Cipher.DECRYPT_MODE, key, ivParamSpec);
				try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(output))) {
					crypt(fis, fos, aesCipher, controller);
				}
			}
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			// crypto problem
			e.printStackTrace();
		} catch (IOException e) {
			// file problem
			e.printStackTrace();
		}
	}

	private static void crypt(InputStream fis, OutputStream fos, Cipher cipher, AsyncController controller)
			throws FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {
		byte[] buffer = new byte[512];
		int totalBytesRead = 0;
		int bytesRead = 0;
		while (!controller.isCanceled() && (bytesRead = fis.read(buffer)) > 0) {
			totalBytesRead += bytesRead;
			controller.setProgress(totalBytesRead);
			byte[] ciphertext = cipher.update(buffer, 0, bytesRead);
			if (ciphertext != null) {
				fos.write(ciphertext);
			}
		}
		if (!controller.isCanceled()) {
			byte[] ciphertext = cipher.doFinal();
			if (ciphertext != null) {
				fos.write(ciphertext);
			}
			controller.setDone();
		}
	}

}
