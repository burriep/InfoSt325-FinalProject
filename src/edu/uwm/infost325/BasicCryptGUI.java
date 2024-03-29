/*
 * Sources referenced:
 * - (Java 8 API Specification)
 * - http://www.java2s.com/Tutorial/Java/0240__Swing/InputPopUps.htm
 * - https://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html
 * - https://stackoverflow.com/questions/356671/jfilechooser-showsavedialog-how-to-set-suggested-file-name
 * - Related cryptography project Paul did in another security class (CS658 Cyber Security Lab) in a previous semester.
 * 
 * Created by: Paul Burrie, Jack Skelton, Zach Zenner
 */
package edu.uwm.infost325;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;

public class BasicCryptGUI extends JFrame {
	// Keep Eclipse Happy
	private static final long serialVersionUID = 1L;
	// Creates the file chooser objects
	private final JFileChooser sourceLocation;
	private final JFileChooser destLocation;

	private static final String KEY_REQUIREMENTS_DESCRIPTION = "Enter a 22 character key meeting the following requirements:\nValid characters: A-Z, a-z, /, + (not the comma character)\nThe last letter must be A, Q, g, or w.";
	private static final String ABOUT_MESSAGE = "Cryptology\n" + "Encrypt, decrypt, and hash files.\n\n"
			+ "Created by: Paul Burrie, Jack Skelton, Zach Zenner\n\n"
			+ "To encrypt a file, select the cleartext source file and select the destination\n"
			+ "where the encrypted file will be saved. Then click the 'encrypt' button.\n"
			+ "You will be asked to enter an encryption key. After entering a valid key,\n"
			+ "the file is encrypted and saved at the destination file location.\n\n"
			+ "For decryption, select the encrypted source file and select the destination\n"
			+ "where the decrypted file will be saved. Then click the 'decrypt' button.\n"
			+ "You will be asked to enter the decryption key. After entering a valid key,\n"
			+ "the file is decrypted and saved at the destination file location.\n\n"
			+ "AES-128 with cipher block chaining and PKCS5 padding is used for encryption.\n\n"
			+ "To calculate the hash value of a file, select the source file and\n"
			+ "click the 'hash' button. The hash will be shown in the window.\n\n"
			+ "SHA-256 is the hashing algorithm used.";
	private static final String DEFAULT_FILE_TEXT = "no file selected";
	// Creates the file objects
	public File destinationFile;
	public File sourceFile;

	private JButton sourceFileBtn;
	public JTextField sourceFileField;
	private JTextField destinationFileField;
	private JButton destinationFileBtn;
	private JButton hashFileBtn;
	private JTextField hashResultField;
	private JButton encryptBtn;
	private JButton decryptBtn;
	private JButton cancelBtn;
	private JButton clearBtn;
	private JButton aboutBtn;
	private JProgressBar progressBar;
	private JLabel statusLabel;

	private CryptController cryptController;
	private CryptReporter cryptReporter;
	private HashController hashController;
	private HashReporter hashReporter;
	private PropertyChangeListener pcl = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			if (pce.getPropertyName().equals("progress")) {
				int progress = (Integer) pce.getNewValue();
				progressBar.setValue(progress);
			}
		}
	};
	private boolean encrypt;

	public BasicCryptGUI() {
		super("Cryptology");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(600, 350);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				doExit();
			}
		});

		// Creates the file choosers
		sourceLocation = new JFileChooser();
		destLocation = new JFileChooser();
		cryptReporter = new CryptReporter();
		hashReporter = new HashReporter();

		initComponents();
		setVisible(true);
	}

	private void initComponents() {
		sourceFileBtn = new JButton("Choose Source File");
		destinationFileBtn = new JButton("Choose Destination File");
		sourceFileField = new JTextField(DEFAULT_FILE_TEXT);
		destinationFileField = new JTextField(DEFAULT_FILE_TEXT);
		hashFileBtn = new JButton("Hash File (SHA-256)");
		hashResultField = new JTextField();
		encryptBtn = new JButton("Encrypt");
		decryptBtn = new JButton("Decrypt");
		cancelBtn = new JButton("Cancel");
		clearBtn = new JButton("Clear");
		aboutBtn = new JButton("About");
		statusLabel = new JLabel("Ready");
		progressBar = new JProgressBar();

		JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);
		contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		sourceFileBtn.addActionListener((evt) -> doChooseSourceFile(evt));
		destinationFileBtn.addActionListener((evt) -> doChooseDestinationFile(evt));
		sourceFileField.setEditable(false);
		destinationFileField.setEditable(false);

		JPanel filePanel = new JPanel(new GridLayout(1, 2, 10, 0));
		JPanel sourceFilePanel = new JPanel(new BorderLayout());
		filePanel.add(sourceFilePanel);
		sourceFilePanel.add(sourceFileBtn, BorderLayout.CENTER);
		sourceFilePanel.add(sourceFileField, BorderLayout.SOUTH);
		JPanel destinationFilePanel = new JPanel(new BorderLayout());
		filePanel.add(destinationFilePanel);
		destinationFilePanel.add(destinationFileBtn, BorderLayout.CENTER);
		destinationFilePanel.add(destinationFileField, BorderLayout.SOUTH);
		add(filePanel, BorderLayout.CENTER);

		hashFileBtn.addActionListener((evt) -> doHashFile(evt));
		hashResultField.setEditable(false);

		JPanel hashPanel = new JPanel(new BorderLayout());
		hashPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		hashPanel.add(hashFileBtn, BorderLayout.WEST);
		hashPanel.add(hashResultField, BorderLayout.CENTER);

		encryptBtn.addActionListener((evt) -> doEncryptFile(evt));
		decryptBtn.addActionListener((evt) -> doDecryptFile(evt));
		cancelBtn.addActionListener((evt) -> doCancel(evt));
		cancelBtn.setEnabled(false);
		clearBtn.addActionListener((evt) -> doClear(evt));
		aboutBtn.addActionListener((evt) -> doAbout(evt));

		JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		controlsPanel.setBorder(BorderFactory.createTitledBorder("Program Controls"));
		controlsPanel.add(encryptBtn);
		controlsPanel.add(decryptBtn);
		controlsPanel.add(cancelBtn);
		controlsPanel.add(clearBtn);
		controlsPanel.add(aboutBtn);

		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(progressBar, BorderLayout.CENTER);
		statusPanel.add(statusLabel, BorderLayout.SOUTH);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(hashPanel, BorderLayout.NORTH);
		bottomPanel.add(controlsPanel, BorderLayout.CENTER);
		bottomPanel.add(statusPanel, BorderLayout.SOUTH);
		add(bottomPanel, BorderLayout.SOUTH);

		validate();
	}

	private void doExit() {
		// Exits the program
		System.exit(0);
	}

	private void doAbout(ActionEvent evt) {
		// Opens the about frame
		JOptionPane.showMessageDialog(this, ABOUT_MESSAGE);
	}

	private void doChooseSourceFile(ActionEvent evt) {
		// Provides a value for the results of the dialogue box
		int returnVal = sourceLocation.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// if the user selected a file, save the file and display the
			// filename
			sourceFile = sourceLocation.getSelectedFile();
			String sourceFileName = sourceFile.getName();
			sourceFileField.setText(sourceFileName);
			destLocation.setCurrentDirectory(sourceFile);
			if (sourceFileName.toLowerCase().endsWith(".enc")) {
				destLocation.setSelectedFile(new File(sourceFileName.substring(0, sourceFileName.length() - 4)));
			} else {
				destLocation.setSelectedFile(new File(sourceFileName + ".enc"));
			}
			clearDestination();
		}
	}

	private void doChooseDestinationFile(ActionEvent evt) {
		if (sourceFile != null) {
			// Provides a value for the results of the dialogue box
			int returnVal = destLocation.showSaveDialog(this);
	
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// if the user selected a file, save the file and display the
				// filename
				destinationFile = destLocation.getSelectedFile();
				destinationFileField.setText(destinationFile.getName());
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please select a source file first.");
		}
	}
	
	private void clearSource() {
		sourceFile = null;
		sourceFileField.setText(DEFAULT_FILE_TEXT);
	}

	private void clearDestination() {
		destinationFile = null;
		destinationFileField.setText(DEFAULT_FILE_TEXT);
	}

	private void doClear(ActionEvent evt) {
		clearSource();
		clearDestination();
		// clear hash text field
		hashResultField.setText("");
		// Reset progress bar
		progressBar.setValue(0);
		progressBar.setIndeterminate(false);
	}

	private void doEncryptFile(ActionEvent evt) {
		byte[] key = getKey();
		if (key != null) {
			encrypt = true;
			disableButtons();
			progressBar.setValue(0);
			progressBar.setIndeterminate(true);
			statusLabel.setText("Encrypting File");
			cryptController = new CryptController(sourceFile, destinationFile, key, true, cryptReporter);
			cryptController.execute();
			// update the progress bar
			cryptController.addPropertyChangeListener(pcl);
		}
	}

	private void doDecryptFile(ActionEvent evt) {
		byte[] key = getKey();
		if (key != null) {
			encrypt = false;
			disableButtons();
			progressBar.setValue(0);
			progressBar.setIndeterminate(true);
			statusLabel.setText("Decrypting File");
			cryptController = new CryptController(sourceFile, destinationFile, key, false, cryptReporter);
			cryptController.execute();
			// update the progress bar
			cryptController.addPropertyChangeListener(pcl);
		}
	}

	private void doHashFile(ActionEvent evt) {
		if (sourceFile != null) {
			disableButtons();
			progressBar.setValue(0);
			progressBar.setIndeterminate(true);
			statusLabel.setText("Hashing File");
			hashController = new HashController(sourceFile, hashReporter);
			hashController.execute();
			// update the progress bar
			hashController.addPropertyChangeListener(pcl);
		} else {
			JOptionPane.showMessageDialog(this, "Please select a source file.");
		}
	}

	private void doCancel(ActionEvent evt) {
		progressBar.setIndeterminate(true);
		if (cryptController != null) {
			if (cryptController.cancel(true)) {
				statusLabel.setText("Process Cancelled");
			} else {
				// unable to cancel
				statusLabel.setText("Unable to cancel the process...");
			}
		} else if (hashController != null) {
			if (hashController.cancel(true)) {
				statusLabel.setText("Process Cancelled");
			} else {
				// unable to cancel
				statusLabel.setText("Unable to cancel the process...");
			}
		}
	}

	private void enableButtons() {
		sourceFileBtn.setEnabled(true);
		destinationFileBtn.setEnabled(true);
		hashFileBtn.setEnabled(true);
		encryptBtn.setEnabled(true);
		decryptBtn.setEnabled(true);
		cancelBtn.setEnabled(false);
		clearBtn.setEnabled(true);
	}

	private void disableButtons() {
		sourceFileBtn.setEnabled(false);
		destinationFileBtn.setEnabled(false);
		hashFileBtn.setEnabled(false);
		encryptBtn.setEnabled(false);
		decryptBtn.setEnabled(false);
		cancelBtn.setEnabled(true);
		clearBtn.setEnabled(false);
	}

	private byte[] getKey() {
		if (sourceFile != null && destinationFile != null) {
			String result = null;
			byte[] key = null;
			do {
				result = JOptionPane.showInputDialog(this, KEY_REQUIREMENTS_DESCRIPTION);
				if (isValidKey(result)) {
					key = Base64.getDecoder().decode(result);
				} else if (result != null) {
					JOptionPane.showMessageDialog(this, "Invalid key, please try again.");
				}
			} while (result != null && key == null);
			return key;
		} else {
			JOptionPane.showMessageDialog(this, "Please select a source and destination file.");
			return null;
		}
	}

	private boolean isValidKey(String key) {
		return key != null && key.matches("^(?:[A-Za-z0-9/+]){21}?[AQgw]$");
	}

	private class CryptReporter implements WorkerReporter {
		private void cleanup() {
			cryptController.removePropertyChangeListener(pcl);
			cryptController = null;
			enableButtons();
			progressBar.setIndeterminate(false);
		}

		@Override
		public void onCancelled() {
			statusLabel.setText("The process has been cancelled...");
			cleanup();
		}

		@Override
		public void onComplete() {
			statusLabel.setText("\"" + sourceFile.getName() + "\" has been " + (encrypt ? "encrypted" : "decrypted") + " and saved at \"" + destinationFile.getName() + "\"");
			progressBar.setValue(100);
			progressBar.setIndeterminate(false);
			cleanup();
		}

		@Override
		public void onError(String message) {
			statusLabel.setText("ERROR: " + message);
			cleanup();
		}
	}

	private class HashReporter implements WorkerReporter {
		private void cleanup() {
			hashController.removePropertyChangeListener(pcl);
			hashController = null;
			enableButtons();
			progressBar.setIndeterminate(false);
		}

		@Override
		public void onCancelled() {
			statusLabel.setText("The process has been cancelled...");
			cleanup();
		}

		@Override
		public void onComplete() {
			// get the hash and show it on the screen in hexadecimal
			try {
				byte[] hash = hashController.get();
				if (hash != null) {
					progressBar.setValue(100);
					progressBar.setIndeterminate(false);
					hashResultField.setText(DatatypeConverter.printHexBinary(hash));
				} else {
					hashResultField.setText("");
				}
			} catch (InterruptedException | ExecutionException e) {
				statusLabel.setText("An error has occurred!");
			}
			statusLabel.setText("The SHA-256 hash value of \"" + sourceFile.getName() + "\" has been calculated.");
			cleanup();
		}

		@Override
		public void onError(String message) {
			statusLabel.setText("ERROR: " + message);
			cleanup();
		}
	}

	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
//		try {
//			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//				String name = info.getName();
//				if (name != null && name.equals("Nimbus")) {
//					UIManager.setLookAndFeel(info.getClassName());
//					break;
//				}
//			}
//		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
//			ex.printStackTrace();
//		}
		// Display the GUI
		new BasicCryptGUI();
	}
}
