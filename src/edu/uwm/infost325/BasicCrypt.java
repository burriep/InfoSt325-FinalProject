package edu.uwm.infost325;

import javax.swing.JFrame;

/**
 * Final InfoSt 325 Project
 */
public class BasicCrypt {
	public static void main(String[] args) {
		JFrame frame = new BasicCryptGUI();
		frame.setTitle("Cryptology");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
