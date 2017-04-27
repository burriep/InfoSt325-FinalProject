package edu.uwm.infost325;

import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.SwingWorker;

/**
 * Some of this functionality is based off work I did in my cyber security lab
 * in a previous semester, but it needed to be customized to fit the needs of
 * this project.
 * Also referenced: https://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html
 * 
 * @author Paul Burrie
 */
public class CryptController extends SwingWorker<Void, Integer> {
	private File sourceFile;
	private File destinationFile;
	private byte[] raw_key;
	private boolean encrypt;
	private WorkerReporter reporter;
	private int sourceFileSize;
	private boolean hadError;
	private String errorMessage;

	public CryptController(File source, File destination, byte[] key, boolean encrypt, WorkerReporter reporter) {
		sourceFile = source;
		destinationFile = destination;
		raw_key = key;
		this.encrypt = encrypt;
		this.reporter = reporter;
	}

	@Override
	protected Void doInBackground() throws Exception {
		sourceFileSize = (int) sourceFile.length();
		if (encrypt) {
			encrypt(sourceFile, destinationFile);
		} else {
			decrypt(sourceFile, destinationFile);
		}
		return null;
	}

	@Override
	protected void done() {
		if (hadError) {
			reporter.onError(errorMessage);
		} else if (isCancelled()) {
			reporter.onCancelled();
		} else {
			reporter.onComplete();
		}
	}

	private void encrypt(File input, File output) {
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
					setProgress(0);
					// write the IV to the output file
					fos.write(iv);
					// write the encrypted file to the output file
					crypt(fis, fos, aesCipher);
				} catch (IOException e) {
					hadError = true;
					errorMessage = "Unable to access destination file";
				}
			} catch (IOException e) {
				hadError = true;
				errorMessage = "Unable to access source file";
			}
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			// crypto problem
			e.printStackTrace();
			hadError = true;
			errorMessage = "A problem was encountered while encrypting the file";
		}
	}

	private void decrypt(File input, File output) {
		try {
			SecretKeySpec key = new SecretKeySpec(raw_key, "AES");
			// Create Cipher
			Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// get cipher parameter ready
			byte[] iv = new byte[16]; // 128 bit IV
			// setup file input stream
			try (InputStream fis = new BufferedInputStream(new FileInputStream(input))) {
				// read in IV
				fis.read(iv);
				IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
				// Initialize Cipher with key and parameters
				aesCipher.init(Cipher.DECRYPT_MODE, key, ivParamSpec);
				try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(output))) {
					crypt(fis, fos, aesCipher);
				} catch (IOException e) {
					hadError = true;
					errorMessage = "Unable to access destination file";
				}
			} catch (IOException e) {
				hadError = true;
				errorMessage = "Unable to access source file";
			}
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			// crypto problem
			e.printStackTrace();
			hadError = true;
			errorMessage = "A problem was encountered while decrypting the file";
		}
	}

	private void crypt(InputStream fis, OutputStream fos, Cipher cipher)
			throws FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {
		byte[] buffer = new byte[512];
		int totalBytesRead = 0;
		int bytesRead = 0;
		while (!isCancelled() && (bytesRead = fis.read(buffer)) > 0) {
			totalBytesRead += bytesRead;
			setProgress(totalBytesRead * 100 / sourceFileSize);
			byte[] ciphertext = cipher.update(buffer, 0, bytesRead);
			if (ciphertext != null) {
				fos.write(ciphertext);
			}
		}
		if (!isCancelled()) {
			byte[] ciphertext = cipher.doFinal();
			if (ciphertext != null) {
				fos.write(ciphertext);
			}
			setProgress(100);
		}
	}
}