package org.opensignature.opensignpdf.ui;

import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.opensignature.opensignpdf.PDFSigner;
import org.opensignature.opensignpdf.pkcs11.MyPkcs11;
import org.opensignature.opensignpdf.tools.CertUtil;
import org.opensignature.opensignpdf.tools.IOUtils;

/**
 * 
 * 
 *
 */
public class FirmaPdf extends JFrame implements ActionListener {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7757223291388612552L;

	/**
	 * Class Logger
	 */
	private static Logger logger = Logger.getLogger(FirmaPdf.class);

	private JLabel file = null;

	private JLabel modulo = null;

	private JLabel pin = null;

	private JLabel suggerimento = null;

	private JComboBox ins_typeSignature;

	private JLabel text_typeSignature;

	private String[] items_typeSignature;

	private JTextField ins_file = new JTextField();

	private JTextField ins_modulo = new JTextField();

	private JPasswordField ins_pin = new JPasswordField();

	private JLabel text_serverTimestamp = null;

	private JTextField ins_serverTimestamp = new JTextField();

	//
	private JLabel text_usrTimestamp = null;

	private JLabel text_pinTimestamp = null;

	private JTextField ins_usrTimestamp = new JTextField();

	private JPasswordField ins_pinTimestamp = new JPasswordField();

	//    

	private JButton apri_file = null;

	private JButton apri_modulo = null;

	private JButton firma = null;

	private ResourceBundle res = null;

	private JLabel text_fieldName = null;

	private JLabel text_openOffice = null;

	private JTextField ins_fieldName = new JTextField();

	private JCheckBox ins_openOffice = new JCheckBox();

	public FirmaPdf(String titolo, String pdfToSign, String cryptokiLib) {

		this.setTitle(getResString("window.title"));

		file = new JLabel((String) res.getObject("label.file"));

		modulo = new JLabel((String) res.getObject("label.cryptoki"));

		pin = new JLabel((String) res.getObject("label.pin"));

		text_serverTimestamp = new JLabel((String) res
				.getObject("label.timestampserver"));

		text_usrTimestamp = new JLabel((String) res
				.getObject("label.timestamp.user"));

		text_pinTimestamp = new JLabel((String) res
				.getObject("label.timestamp.password"));

		suggerimento = new JLabel((String) res.getObject("label.hint"));

		apri_file = new JButton((String) res.getObject("label.open"));

		apri_modulo = new JButton((String) res.getObject("label.open"));

		firma = new JButton((String) res.getObject("label.sign"));

		ins_modulo.setText((String) res.getObject("default.cryptoki"));

		ins_serverTimestamp.setText((String) res
				.getObject("default.timestampserver"));

		text_fieldName = new JLabel((String) res.getObject("label.fieldName"));

		text_typeSignature = new JLabel((String) res
				.getObject("label.typeSignature"));

		text_openOffice = new JLabel((String) res.getObject("label.openOffice"));

		items_typeSignature = new String[2];
		items_typeSignature[0] = (String) res
				.getObject("item.typeSignature.certified");
		items_typeSignature[1] = (String) res
				.getObject("item.typeSignature.approval");
		ins_typeSignature = new JComboBox(items_typeSignature);

		// -- Default pdf file
		//File fDir = new File("etc/test");
		//File fPdf = new File(fDir, "ciao.pdf");
		//ins_file.setText(fPdf.getAbsolutePath());

		JPanel pannello_principale = new JPanel();
		JPanel pannello_centro = new JPanel();

		BorderLayout layout_pp = new BorderLayout();
		GridLayout layout_pcentro = new GridLayout(9, 3);

		pannello_principale.setLayout(layout_pp);
		pannello_centro.setLayout(layout_pcentro);

		pannello_centro.add(file);
		pannello_centro.add(ins_file);
		pannello_centro.add(apri_file);

		pannello_centro.add(modulo);
		pannello_centro.add(ins_modulo);
		pannello_centro.add(apri_modulo);

		pannello_centro.add(pin);
		pannello_centro.add(ins_pin);
		// blank cell
		pannello_centro.add(new JLabel(""));

		pannello_centro.add(text_serverTimestamp);
		pannello_centro.add(ins_serverTimestamp);
		pannello_centro.add(new JPanel());

		pannello_centro.add(text_usrTimestamp);
		pannello_centro.add(ins_usrTimestamp);
		pannello_centro.add(new JLabel(" "
				+ (String) res.getObject("label.timestamp.hint")));

		pannello_centro.add(text_pinTimestamp);
		pannello_centro.add(ins_pinTimestamp);
		pannello_centro.add(new JLabel(" "
				+ (String) res.getObject("label.timestamp.hint")));

		pannello_centro.add(text_typeSignature);
		pannello_centro.add(ins_typeSignature);
		pannello_centro.add(new JLabel(" "));

		pannello_centro.add(text_fieldName);
		pannello_centro.add(ins_fieldName);
		pannello_centro.add(new JLabel(" "
				+ (String) res.getObject("label.fieldName.hint")));

		pannello_centro.add(text_openOffice);
		pannello_centro.add(ins_openOffice);
		pannello_centro.add(new JLabel(" "));

		pannello_principale.add("North", suggerimento);
		pannello_principale.add("South", firma);
		pannello_principale.add("Center", pannello_centro);

		firma.addActionListener(this);
		apri_file.addActionListener(this);
		apri_modulo.addActionListener(this);

		setContentPane(pannello_principale);
		pannello_principale.setVisible(true);

	}

	public void setModulo(String cryptokiLib) {

		ins_modulo.setText(cryptokiLib);

	}

	public void setPdfToSign(String filePath) {

		File fPdf = new File(filePath);
		if (fPdf.isFile() && fPdf.exists())
			ins_file.setText(fPdf.getAbsolutePath());
		else{
			logger.error(filePath + " inesistente.");
		    JOptionPane.showMessageDialog(null,
				"Impossibile trovare il file:\n\"" + filePath + "\"");
		 }

	}

	public void actionPerformed(ActionEvent evento) {

		java.lang.Object sorgente = evento.getSource();

		if (sorgente == apri_file) {

			JFileChooser apri_box = new JFileChooser();
			apri_box.showOpenDialog(this);
			File f = apri_box.getSelectedFile();
			String file_da_aprire = (f == null ? "" : f.getPath());
			ins_file.setText(file_da_aprire);

		} else if (sorgente == apri_modulo) {
			JFileChooser apri_box = new JFileChooser();
			apri_box.showOpenDialog(this);
			File f = apri_box.getSelectedFile();
			String modulo_da_aprire = (f == null ? "" : f.getPath());
			ins_modulo.setText(modulo_da_aprire);

		} else if (sorgente == firma) {

			String message = new String();

			String PIN = new String(ins_pin.getPassword());
			
			//Immediately clear PIN Text Field.
			ins_pin.setText("");

			/** XXX: Uncomment the following lines */
			// // Remove pin from GUI for security raesons
			// ins_pin.setText("");

			String PKCS11 = ins_modulo.getText().trim();
			String serverTimestamp = ins_serverTimestamp.getText().trim();
			String usernameTimestamp = ins_usrTimestamp.getText().trim();
			String passwordTimestamp = new String(ins_pinTimestamp
					.getPassword());
			String typeSignature[] = new String[2];

			typeSignature[0] = "author";
			typeSignature[1] = "countersigner";

			String typeSignatureSelected = typeSignature[ins_typeSignature
					.getSelectedIndex()];
			String fieldName = ins_fieldName.getText().trim();
			String openOfficeSelected = String.valueOf(ins_openOffice
					.isSelected());

			MyPkcs11 mySign = null;
			Session session = null;

			File[] pdfFiles = null;
			File selectedFile = new File(ins_file.getText().trim());

			logger.debug("[actionPerformed]:: selectedFile: "
					+ selectedFile.getAbsolutePath());

			if (selectedFile.isFile()) {

				pdfFiles = new File[] { selectedFile };

			} else {

				pdfFiles = selectedFile.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return ((name != null && name.toLowerCase().endsWith(
								".pdf")) ? true : false);
					}
				});

			}

			logger.debug("[actionPerformed]:: pdfFiles: " + pdfFiles);
			logger.debug("[actionPerformed]:: asList(pdfFiles): "
					+ Arrays.asList(pdfFiles));

			try {

				File certsDir = new File("etc/certs");
				File caCertFile = new File(certsDir, "CA.cer");
				if (!caCertFile.exists()) {
					throw new Exception("CA certificate not found.");
				}

				logger.debug("[actionPerformed]:: CA File: "
						+ caCertFile.getAbsolutePath());
				X509Certificate caCert = CertUtil.toX509Certificate(IOUtils
						.readBytesFromFile(caCertFile.getAbsolutePath()));
				X509Certificate[] certificateChain = new X509Certificate[] { caCert };
				logger.debug("[actionPerformed]:: certificateChain: "
						+ certificateChain);
				logger.debug("[actionPerformed]:: certificateChain.length: "
						+ certificateChain.length);

				if (mySign == null) {
					mySign = new MyPkcs11();
				}
				if (session == null) {
					session = mySign.initSession(PKCS11, PIN);
				}

				logger.debug("[actionPerformed]:: Signing files... ");

				// -- Signing Files
				PDFSigner signer = new PDFSigner(certificateChain,
						serverTimestamp, usernameTimestamp, passwordTimestamp,
						typeSignatureSelected, fieldName, openOfficeSelected);
				
				signer.signPDF(mySign, session, pdfFiles, "_sig",
						"openSignPDF", true);

				if (pdfFiles.length > 1) {
					message = "All files had been signed correctly.";

				} else {
					message = "The file '" + pdfFiles[0].getAbsolutePath()
							+ "' had been signed correctly.";

				}

			} catch (PKCS11Exception e) {
				Exception cause = e.getEncapsulatedException();
				cause.printStackTrace();
				message = "Error:\n" + "'" + cause.getMessage() + "'";

			} catch (Exception e) {
				e.printStackTrace();
				message = "Error:\n" + "'" + e.getMessage() + "'";

			} finally {

				try {

					// -- Setting free the session resources
					if (session != null) {
						session.closeSession();
					}
					if (mySign != null) {
						mySign.finalizeModule();
					}

				} catch (TokenException e) {
					logger
							.warn(
									"[actionPerformed]:: Error closing the PKCS11 session.",
									e);
				}

			}

			JOptionPane.showMessageDialog(null, message, "Result",
					JOptionPane.PLAIN_MESSAGE);
		}

	}

	public ResourceBundle getRes() {
		if (this.res == null)
			try {
				this.res = PropertyResourceBundle.getBundle("Resources");
			} catch (MissingResourceException mre) {
				JOptionPane.showMessageDialog(null,
						"Resource properties file not found:\n" + mre);
			}

		return this.res;
	}

	private String getResString(String key) {
		return (String) getRes().getObject(key);
	}

	public static void main(String[] args) {
		
		Security.insertProviderAt(new BouncyCastleProvider(), 3);

		PropertyConfigurator.configure("etc/conf/log4j.properties");
		JFrame applicazione = new FirmaPdf(
				"Firma PDF di Antonino Iacono e Roberto Resoli", null, null);

		if (args.length > 0) {
			((FirmaPdf) applicazione).setPdfToSign(args[0]);
			if (args.length == 2)
				((FirmaPdf) applicazione).setModulo(args[1]);
		}
		
		applicazione.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		applicazione.setSize(700, 280);

		// Center window
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		applicazione.setLocation((d.width - applicazione.getWidth()) / 2,
				(d.height - applicazione.getHeight()) / 2);

		applicazione.setVisible(true);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Impossibile impostare lo stile: " + e);
		}

	}

}
