package edu.uwm.infost325;

import java.awt.*;
import javax.swing.*;

/**
 * @author zach
 */
public class aboutGUI extends JFrame {
	/**
	 * Keep Eclipse Happy
	 */
	private static final long serialVersionUID = 1L;

	public aboutGUI() {
		this.setTitle("About Cryptology");
		this.setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(100, 100);

		String aboutText = "Cryptology\n" + "Encrypt, decrypt, and hash files.\n\n"
				+ "Created by: Paul Burrie, Jack Skelton, Zach Zenner\n\n"
				+ "To encrypt a file, select the cleartext source file and select the destination\n"
				+ "where the encrypted file will be saved. Then click the 'encrypt' button.\n"
				+ "You will be asked to enter an encryption key. After entering a valid key,\n"
				+ "the file is encrypted and saved at the destination file location.\n\n"
				+ "For decryption, select the encrypted source file and select the destination\n"
				+ "where the decrypted file will be saved. Then click the 'decrypt' button.\n"
				+ "You will be asked to enter the decryption key. After entering a valid key,\n"
				+ "the file is decrypted and saved at the destination file location.\n\n"
				+ "To calculate the hash value of a file, select the source file and\n"
				+ "click the 'hash' button. The hash will be shown in the window.";

		JOptionPane.showMessageDialog(this, aboutText, "About", JOptionPane.PLAIN_MESSAGE);
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(() -> {
			new aboutGUI();
		});
	}
}
