/*
 * Sources referenced (besides the Java language reference)
 * http://www.java2s.com/Tutorial/Java/0240__Swing/InputPopUps.htm
 */
package edu.uwm.infost325;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
	
	private final String KEY_REQUIREMENTS_DESCRIPTION = "Enter a 22 character key meeting the following requirements:\nValid characters: A-Z, a-z, /, + (not the comma character)\nThe last letter must be A, B, C, or D.";

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
	private JButton exitBtn;
	private JProgressBar progressBar;

	public BasicCryptGUI() {
		initComponents();

		// Creates the file choosers
		sourceLocation = new JFileChooser();
		destLocation = new JFileChooser();
	}

	private void initComponents() {
		progressBar = new JProgressBar();
		JPanel jPanel1 = new JPanel();
		JPanel jPanel3 = new JPanel();
		JPanel jPanel4 = new JPanel();
		sourceFileBtn = new JButton();
		destinationFileBtn = new JButton();
		sourceFileField = new JTextField();
		destinationFileField = new JTextField();
		hashFileBtn = new JButton();
		hashResultField = new JTextField();
		JSeparator jSeparator1 = new JSeparator();
		encryptBtn = new JButton();
		decryptBtn = new JButton();
		cancelBtn = new JButton();
		clearBtn = new JButton();
		aboutBtn = new JButton();
		exitBtn = new JButton();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		sourceFileBtn.setText("Source Location");
		sourceFileBtn.addActionListener((evt) -> doChooseSourceFile(evt));

		destinationFileBtn.setText("Destination Location");
		destinationFileBtn.addActionListener((evt) -> doChooseDestinationFile(evt));

		sourceFileField.setEditable(false);
		sourceFileField.setText("Source File Path");

		destinationFileField.setEditable(false);
		destinationFileField.setText("Destination File Path");

		GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
			.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(jPanel3Layout.createSequentialGroup()
			.addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
			.addComponent(sourceFileBtn, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
			.addComponent(sourceFileField, GroupLayout.PREFERRED_SIZE, 285, GroupLayout.PREFERRED_SIZE))
			.addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(jPanel3Layout.createSequentialGroup()
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(destinationFileBtn, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
			.addContainerGap(100, Short.MAX_VALUE))
			.addGroup(jPanel3Layout.createSequentialGroup()
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(destinationFileField)))));
		jPanel3Layout
			.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(sourceFileBtn, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
			.addComponent(destinationFileBtn, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(sourceFileField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(destinationFileField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap()));

		hashFileBtn.setText("Hash File");
		hashFileBtn.addActionListener((evt) -> doHashFile(evt));

		hashResultField.setEditable(false);

		GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout
			.setHorizontalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addComponent(hashFileBtn)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(hashResultField)
			.addContainerGap()));
		jPanel4Layout
			.setVerticalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(jPanel4Layout.createSequentialGroup().addGap(10, 10, 10)
			.addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(hashFileBtn).addComponent(hashResultField, GroupLayout.PREFERRED_SIZE,
			GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap(10, Short.MAX_VALUE)));

		jPanel1.setBorder(BorderFactory.createTitledBorder("Program Controls"));
		jPanel1.setToolTipText("");

		encryptBtn.setText("Encrypt");
		encryptBtn.setMaximumSize(new Dimension(93, 29));
		encryptBtn.addActionListener((evt) -> doEncryptFile(evt));

		decryptBtn.setText("Decrypt");
		decryptBtn.addActionListener((evt) -> doDecryptFile(evt));

		cancelBtn.setText("Cancel");
		cancelBtn.setMaximumSize(new Dimension(93, 29));
		cancelBtn.setMinimumSize(new Dimension(93, 29));
		cancelBtn.setPreferredSize(new Dimension(93, 29));

		clearBtn.setText("Clear");
		clearBtn.setMaximumSize(new Dimension(93, 29));
		clearBtn.setMinimumSize(new Dimension(93, 29));
		clearBtn.setPreferredSize(new Dimension(93, 29));
		clearBtn.addActionListener((evt) -> doClearFiles(evt));

		aboutBtn.setText("About");
		aboutBtn.setMaximumSize(new Dimension(93, 29));
		aboutBtn.setMinimumSize(new Dimension(93, 29));
		aboutBtn.setPreferredSize(new Dimension(93, 29));
		aboutBtn.addActionListener((evt) -> doAbout(evt));

		GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
			.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(jPanel1Layout.createSequentialGroup().addGap(38, 38, 38)
			.addComponent(encryptBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(decryptBtn)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(cancelBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(clearBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(aboutBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel1Layout
			.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(jPanel1Layout.createSequentialGroup().addGap(20, 20, 20)
			.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(encryptBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(decryptBtn)
			.addComponent(cancelBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(clearBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(aboutBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap(24, Short.MAX_VALUE)));

		exitBtn.setText("Exit");
		exitBtn.setMaximumSize(new Dimension(93, 29));
		exitBtn.setMinimumSize(new Dimension(93, 29));
		exitBtn.setPreferredSize(new Dimension(93, 29));
		exitBtn.addActionListener((evt) -> doExit(evt));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout
			.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(jSeparator1).addGroup(layout.createSequentialGroup().addGroup(layout
			.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup().addContainerGap()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(jPanel4, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			.addGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup().addContainerGap().addComponent( jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGroup(layout.createSequentialGroup().addGap(227, 227, 227)
			.addComponent(exitBtn, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)))
			.addGap(0, 0, Short.MAX_VALUE)))
			.addContainerGap()));
		layout
			.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
			.createSequentialGroup()
			.addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(exitBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		pack();
	}

	private void doExit(ActionEvent evt) {
		// Exits the program
		System.exit(0);
	}

	private void doAbout(ActionEvent evt) {
		// Opens the about frame
		JFrame frame = new aboutGUI();
		frame.setTitle("Cryptology About");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
	}

	private void doChooseSourceFile(ActionEvent evt) {
		// Provides a value for the results of the dialogue box
		int returnVal = sourceLocation.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// if the user selected a file, save the file and display the filename
			sourceFile = sourceLocation.getSelectedFile();
			sourceFileField.setText(sourceFile.getName());
		}
	}

	private void doChooseDestinationFile(ActionEvent evt) {
		// Provides a value for the results of the dialogue box
		int returnVal = destLocation.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// if the user selected a file, save the file and display the filename
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
	}

	private void doEncryptFile(ActionEvent evt) {
		if (sourceFile != null && destinationFile != null) {
			String result = JOptionPane.showInputDialog(this, KEY_REQUIREMENTS_DESCRIPTION);
			if (isValidKey(result)) {
				// TODO: encrypt file
			} else {
				JOptionPane.showMessageDialog(this, "Invalid key, please try again.");
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please select a source and destination file.");
		}
	}

	private void doDecryptFile(ActionEvent evt) {
		if (sourceFile != null && destinationFile != null) {
			String resultD = JOptionPane.showInputDialog(this, KEY_REQUIREMENTS_DESCRIPTION);
			if (isValidKey(resultD)) {
				// TODO: decrypt file
			} else {
				JOptionPane.showMessageDialog(this, "Invalid key, please try again.");
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please select a source and destination file.");
		}
	}

	private void doHashFile(ActionEvent evt) {
		if (sourceFile != null) {
			// TODO: hash file
		} else {
			JOptionPane.showMessageDialog(this, "Please select a source file.");
		}
	}

	private boolean isValidKey(String key) {
		return key != null && key.matches("^(?:[A-Za-z0-9/+]){21}?[A-D]$");
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
