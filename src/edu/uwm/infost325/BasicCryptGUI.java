/*
 * Sources referenced (besides the Java language reference)
 * http://www.java2s.com/Tutorial/Java/0240__Swing/InputPopUps.htm
 * https://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html
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

/**
 *
 * @author zach
 */
public class BasicCryptGUI extends JFrame {
	// Keep Eclipse Happy
	private static final long serialVersionUID = 1L;
	// Creates the file chooser objects
	private final JFileChooser sourceLocation;
	private final JFileChooser destLocation;

	private final String KEY_REQUIREMENTS_DESCRIPTION = "Enter a 22 character key meeting the following requirements:\nValid characters: A-Z, a-z, /, + (not the comma character)\nThe last letter must be A, Q, g, or w.";

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

	public BasicCryptGUI() {
		super("Question Editor");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setSize(500, 350);

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
	}

	private void initComponents() {
		sourceFileBtn = new JButton("Source Location");
		destinationFileBtn = new JButton("Destination Location");
		sourceFileField = new JTextField("Source File Path");
		destinationFileField = new JTextField("Destination File Path");
		hashFileBtn = new JButton("Hash File");
		hashResultField = new JTextField();
		encryptBtn = new JButton("Encrypt");
		decryptBtn = new JButton("Decrypt");
		cancelBtn = new JButton("Cancel");
		clearBtn = new JButton("Clear");
		aboutBtn = new JButton("About");
		statusLabel = new JLabel(" ");
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
		clearBtn.addActionListener((evt) -> doClearFiles(evt));
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
                JOptionPane.showMessageDialog(this, "Cryptology\n" + "Encrypt, decrypt, and hash files.\n\n"
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
				+ "click the 'hash' button. The hash will be shown in the window.");
	}

	private void doChooseSourceFile(ActionEvent evt) {
		// Provides a value for the results of the dialogue box
		int returnVal = sourceLocation.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// if the user selected a file, save the file and display the
			// filename
			sourceFile = sourceLocation.getSelectedFile();
			sourceFileField.setText(sourceFile.getName());
		}
	}

	private void doChooseDestinationFile(ActionEvent evt) {
		// Provides a value for the results of the dialogue box
		int returnVal = destLocation.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// if the user selected a file, save the file and display the
			// filename
			destinationFile = destLocation.getSelectedFile();
			destinationFileField.setText(destinationFile.getName());
		}
	}

	private void doClearFiles(ActionEvent evt) {
		// Resets text fields
		destinationFileField.setText("Destination File Path");
		sourceFileField.setText("Source File Path");
		hashResultField.setText("");

		// Resets File variables
		sourceFile = null;
		destinationFile = null;

		// Reset progress bar
		progressBar.setValue(0);
		progressBar.setIndeterminate(false);
	}

	private void doEncryptFile(ActionEvent evt) {
		byte[] key = getKey();
		if (key != null) {
			disableButtons();
			cryptController = new CryptController(sourceFile, destinationFile, key, true, cryptReporter);
			cryptController.execute();
			// update the progress bar
			cryptController.addPropertyChangeListener(pcl);
		}
	}

	private void doDecryptFile(ActionEvent evt) {
		byte[] key = getKey();
		if (key != null) {
			disableButtons();
			cryptController = new CryptController(sourceFile, destinationFile, key, false, cryptReporter);
			cryptController.execute();
			// update the progress bar
			cryptController.addPropertyChangeListener(pcl);
		}
	}

	private void doHashFile(ActionEvent evt) {
		if (sourceFile != null) {
			disableButtons();
			hashController = new HashController(sourceFile, hashReporter);
			hashController.execute();
			// update the progress bar
			hashController.addPropertyChangeListener(pcl);
		} else {
			JOptionPane.showMessageDialog(this, "Please select a source file.");
		}
	}

	private void doCancel(ActionEvent evt) {
		// TODO: maybe set the progress bar to indeterminate state.
                progressBar.setIndeterminate(true);
		if (cryptController != null) {
			if (cryptController.cancel(true)) {
				// cancelled successfully
			} else {
				// unable to cancel
				statusLabel.setText("Unable to cancel the process...");
			}
		} else if (hashController != null) {
			if (hashController.cancel(true)) {
				// cancelled successfully
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
		}

		@Override
		public void onCancelled() {
			statusLabel.setText("The process has been cancelled...");
			cleanup();
		}

		@Override
		public void onComplete() {
			statusLabel.setText("The file has been encrypted!");
			cleanup();
		}

		@Override
		public void onError(String message) {
			statusLabel.setText("An error has occurred!");
			cleanup();
		}
	}

	private class HashReporter implements WorkerReporter {
		private void cleanup() {
			hashController.removePropertyChangeListener(pcl);
			hashController = null;
			enableButtons();
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
					hashResultField.setText(DatatypeConverter.printHexBinary(hash));
				} else {
					// TODO: shouldn't happen in real operation
					hashResultField.setText("testing");
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO: customize catch block
				e.printStackTrace();
			}
			statusLabel.setText("The file has been hashed!");
			cleanup();
		}

		@Override
		public void onError(String message) {
			statusLabel.setText("An error has occurred!");
			cleanup();
		}
	}

	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				String name = info.getName();
				if (name != null && name.equals("Nimbus")) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception ex) {
			// TODO: show error dialog
		}
		// Display the GUI
		EventQueue.invokeLater(() -> {
			new BasicCryptGUI().setVisible(true);
		});
	}
}
