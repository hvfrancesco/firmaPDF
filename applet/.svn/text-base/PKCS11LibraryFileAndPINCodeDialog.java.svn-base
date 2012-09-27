import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Properties;

/**
 * Dialog for choosing PKCS#11 implementation library file and PIN code for
 * accessing the smart card. Allows the user to choose a PKCS#11 library file
 * (.dll / .so) and enter a PIN code for the smart card. The last used library
 * file name is remembered in the config file called
 * ".smart_card_signer_applet.config" located in the user's home directory in
 * order to be automatically shown the next time when the same user accesses
 * this dialog.
 * 
 * This file is part of NakovDocumentSigner digital document signing framework
 * for Java-based Web applications: http://www.nakov.com/documents-signing/
 * 
 * Copyright (c) 2005 by Svetlin Nakov - http://www.nakov.com All rights
 * reserved. This code is freeware. It can be used for any purpose as long as
 * this copyright statement is not removed or modified.
 */
public class PKCS11LibraryFileAndPINCodeDialog extends JDialog {

	private static final String CONFIG_FILE_NAME = ".smart_card_signer_applet.config";

	private static final String PKCS11_LIBRARY_FILE_NAME_KEY = "last-PKCS11-file-name";

	private JButton mBrowseForLibraryFileButton = new JButton();

	private JTextField mLibraryFileNameTextField = new JTextField();

	private JLabel mChooseLibraryFileLabel = new JLabel();

	private JTextField mPINCodeTextField = new JPasswordField();

	private JLabel mEnterPINCodeLabel = new JLabel();

	private JButton mSignButton = new JButton();

	private JButton mCancelButton = new JButton();

	private boolean mResult = false;

	/**
	 * Initializes the dialog - creates and initializes its GUI controls.
	 */
	public PKCS11LibraryFileAndPINCodeDialog() {
		// Initialize the dialog
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(426, 165));
		this.setBackground(SystemColor.control);
		this.setTitle("Select PKCS#11 library file and smart card PIN code");
		this.setResizable(false);

		// Center the dialog in the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = this.getSize();
		int centerPosX = (screenSize.width - dialogSize.width) / 2;
		int centerPosY = (screenSize.height - dialogSize.height) / 2;
		setLocation(centerPosX, centerPosY);

		// Initialize certificate keystore file label
		mChooseLibraryFileLabel
				.setText("Please select your PKCS#11 implementation library file (.dll / .so) :");
		mChooseLibraryFileLabel.setBounds(new Rectangle(10, 5, 400, 15));
		mChooseLibraryFileLabel.setFont(new Font("Dialog", 0, 12));

		// Initialize certificate keystore file name text field
		mLibraryFileNameTextField.setBounds(new Rectangle(10, 25, 315, 20));
		mLibraryFileNameTextField.setFont(new Font("DialogInput", 0, 12));
		mLibraryFileNameTextField.setEditable(false);
		mLibraryFileNameTextField.setBackground(SystemColor.control);

		// Initialize browse button
		mBrowseForLibraryFileButton.setText("Browse");
		mBrowseForLibraryFileButton.setBounds(new Rectangle(330, 25, 80, 20));
		mBrowseForLibraryFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseForLibraryButton_actionPerformed();
			}
		});

		// Initialize PIN code label
		mEnterPINCodeLabel
				.setText("Enter the PIN code to access your smart card:");
		mEnterPINCodeLabel.setBounds(new Rectangle(10, 55, 350, 15));
		mEnterPINCodeLabel.setFont(new Font("Dialog", 0, 12));

		// Initialize PIN code text field
		mPINCodeTextField.setBounds(new Rectangle(10, 75, 400, 20));
		mPINCodeTextField.setFont(new Font("DialogInput", 0, 12));

		// Initialize sign button
		mSignButton.setText("Sign");
		mSignButton.setBounds(new Rectangle(110, 105, 75, 25));
		mSignButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signButton_actionPerformed();
			}
		});

		// Initialize cancel button
		mCancelButton.setText("Cancel");
		mCancelButton.setBounds(new Rectangle(220, 105, 75, 25));
		mCancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed();
			}
		});

		// Add the initialized components into the dialog's content pane
		this.getContentPane().add(mChooseLibraryFileLabel, null);
		this.getContentPane().add(mLibraryFileNameTextField, null);
		this.getContentPane().add(mBrowseForLibraryFileButton, null);
		this.getContentPane().add(mEnterPINCodeLabel, null);
		this.getContentPane().add(mPINCodeTextField, null);
		this.getContentPane().add(mSignButton, null);
		this.getContentPane().add(mCancelButton, null);
		this.getRootPane().setDefaultButton(mSignButton);

		// Add some functionality for focusing the most appropriate
		// control when the dialog is shown
		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent windowEvent) {
				String libraryFileName = mLibraryFileNameTextField.getText();
				if (libraryFileName != null && libraryFileName.length() != 0)
					mPINCodeTextField.requestFocus();
				else
					mBrowseForLibraryFileButton.requestFocus();
			}
		});
	}

	/**
	 * Called when the "Browse" button is pressed. Shows file choose dialog and
	 * allows the user to locate a library file.
	 */
	private void browseForLibraryButton_actionPerformed() {
		JFileChooser fileChooser = new JFileChooser();
		LibraryFileFilter libraryFileFilter = new LibraryFileFilter();
		fileChooser.addChoosableFileFilter(libraryFileFilter);
		String libraryFileName = mLibraryFileNameTextField.getText();
		File directory = new File(libraryFileName).getParentFile();
		fileChooser.setCurrentDirectory(directory);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String selectedLibFile = fileChooser.getSelectedFile()
					.getAbsolutePath();
			mLibraryFileNameTextField.setText(selectedLibFile);
		}
	}

	/**
	 * Called when the sign button is pressed. Closses the dialog and sets the
	 * result flag to true to indicate that the user is confirmed the
	 * information entered in the dialog.
	 */
	private void signButton_actionPerformed() {
		mResult = true;
		this.setVisible(false);
	}

	/**
	 * Called when the cancel button is pressed. Closses the dialog and sets the
	 * result flag to false that indicates that the dialog is canceled.
	 */
	private void cancelButton_actionPerformed() {
		mResult = false;
		this.setVisible(false);
	}

	/**
	 * @return the file name with full path to it where the dialog settings are
	 *         stored.
	 */
	private String getConfigFileName() {
		String configFileName = System.getProperty("user.home")
				+ System.getProperty("file.separator") + CONFIG_FILE_NAME;
		return configFileName;
	}

	/**
	 * Loads the dialog settings from the dialog configuration file. These
	 * settings consist of a single value - the last used library file name with
	 * its full path.
	 */
	private void loadSettings() throws IOException {
		String configFileName = getConfigFileName();
		FileInputStream configFileStream = new FileInputStream(configFileName);
		try {
			Properties configProps = new Properties();
			configProps.load(configFileStream);

			// Apply setings from the config file
			String lastLibraryFileName = configProps
					.getProperty(PKCS11_LIBRARY_FILE_NAME_KEY);
			if (lastLibraryFileName != null)
				mLibraryFileNameTextField.setText(lastLibraryFileName);
			else
				mLibraryFileNameTextField.setText("");
		} finally {
			configFileStream.close();
		}
	}

	/**
	 * Saves the dialog settings to the dialog configuration file. These
	 * settings consist of a single value - the last used library file name with
	 * its full path.
	 */
	private void saveSettings() throws IOException {
		// Create a list of settings to store in the config file
		Properties configProps = new Properties();
		String currentLibraryFileName = mLibraryFileNameTextField.getText();
		configProps.setProperty(PKCS11_LIBRARY_FILE_NAME_KEY,
				currentLibraryFileName);

		// Save the settings in the config file
		String configFileName = getConfigFileName();
		FileOutputStream configFileStream = new FileOutputStream(configFileName);
		try {
			configProps.store(configFileStream, "");
		} finally {
			configFileStream.close();
		}
	}

	/**
	 * @return the library file selected by the user.
	 */
	public String getLibraryFileName() {
		String libraryFileName = mLibraryFileNameTextField.getText();
		return libraryFileName;
	}

	/**
	 * @return the PIN code entered by the user.
	 */
	public String getSmartCardPINCode() {
		String pinCode = mPINCodeTextField.getText();
		return pinCode;
	}

	/**
	 * Shows the dialog and allow the user to choose library file and enter a
	 * PIN code.
	 * 
	 * @return true if the user click sign button or false if the user cancel
	 *         the dialog.
	 */
	public boolean run() {
		try {
			loadSettings();
		} catch (IOException ioex) {
			// Loading settings failed. Default settings will be used.
		}

		setModal(true);
		this.setVisible(true);

		try {
			if (mResult) {
				saveSettings();
			}
		} catch (IOException ioex) {
			// Saving settings failed. Can not handle this problem.
		}

		return mResult;
	}

	/**
	 * File filter class, intended to accept only .dll and .so files.
	 */
	private static class LibraryFileFilter extends FileFilter {
		public boolean accept(File aFile) {
			if (aFile.isDirectory()) {
				return true;
			}

			String fileName = aFile.getName().toLowerCase();
			boolean accepted = (fileName.endsWith(".dll") || fileName
					.endsWith(".so"));
			return accepted;
		}

		public String getDescription() {
			return "PKCS#11 v2.0 ot later implementation library (.dll, .so)";
		}
	}

}
