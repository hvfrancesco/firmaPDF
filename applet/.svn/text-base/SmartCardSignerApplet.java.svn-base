import iaik.pkcs.pkcs11.Session;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.security.*;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.lang.reflect.Constructor;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import java.io.BufferedInputStream;
import org.opensignature.opensignpdf.pkcs11.MyPkcs11;
import org.opensignature.opensignpdf.tools.IOUtils;
import org.opensignature.opensignpdf.PDFSigner;

/*
 * contains signing framework for Java-based Web applications:
 * http://www.nakov.com/documents-signing/
 *
 * Copyright (c) 2005 by Svetlin Nakov - http://www.nakov.com
 * All rights reserved. This code is freeware. It can be used
 * for any purpose as long as this copyright statement is not
 * removed or modified.
 */
public class SmartCardSignerApplet extends Applet {

	private static final String FILE_NAME_FIELD_PARAM = "fileNameField";

	private static final String CERT_CHAIN_FIELD_PARAM = "certificationChainField";

	private static final String SIGNATURE_FIELD_PARAM = "signatureField";

	private static final String SIGN_BUTTON_CAPTION_PARAM = "signButtonCaption";

	protected MyPkcs11 pkcs11 = null;

	protected Session session = null;

	private Button mSignButton;

	/** Timestamp Server URL */
	private static final String TSS = "http://tss.pki.gva.es:8318/tsa";

	private X509Certificate[] certificateChain = null;

	/**
	 * Initializes the applet - creates and initializes its graphical user
	 * interface. Actually the applet consists of a single button, that fills
	 * its all surface. The button's caption is taken from the applet parameter
	 * SIGN_BUTTON_CAPTION_PARAM.
	 */
	public void init() {
		String signButtonCaption = this.getParameter(SIGN_BUTTON_CAPTION_PARAM);
		mSignButton = new Button(signButtonCaption);
		mSignButton.setLocation(0, 0);
		Dimension appletSize = this.getSize();
		mSignButton.setSize(appletSize);
		mSignButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signSelectedFile();
			}
		});
		this.setLayout(null);
		this.add(mSignButton);
	}

	private void signSelectedFile() {
		try {
			// Get the file name to be signed from the form in the HTML document
			JSObject browserWindow = JSObject.getWindow(this);
			JSObject mainForm = (JSObject) browserWindow
					.eval("document.forms[0]");
			String fileNameFieldName = this.getParameter(FILE_NAME_FIELD_PARAM);
			JSObject fileNameField = (JSObject) mainForm
					.getMember(fileNameFieldName);
			String fileName = (String) fileNameField.getMember("value");

			// Perform the actual file signing

			// Show a dialog for choosing PKCS#11 implementation library and
			// smart card PIN
			PKCS11LibraryFileAndPINCodeDialog pkcs11Dialog = new PKCS11LibraryFileAndPINCodeDialog();
			boolean dialogConfirmed;
			try {
				dialogConfirmed = pkcs11Dialog.run();
			} finally {
				pkcs11Dialog.dispose();
			}

			if (dialogConfirmed) {
				String oldButtonLabel = mSignButton.getLabel();
				mSignButton.setLabel("Wait...");
				mSignButton.setEnabled(false);
				try {
					String pkcs11LibraryFileName = pkcs11Dialog
							.getLibraryFileName();
					String pinCode = pkcs11Dialog.getSmartCardPINCode();
					composeTestCertificateChain();
					File fPDF = new File(fileName);
					String typeSign = this.getParameter("typeSign");
					PDFSigner signer = new PDFSigner(certificateChain, "",
							null, null, typeSign, "");

					if (pkcs11 == null) {
						pkcs11 = new MyPkcs11();
					}

					if (session == null) {
						session = pkcs11.initSession(pkcs11LibraryFileName,
								pinCode);
					}

					signer.signPDF(pkcs11, session, new File[] { fPDF },
							"_signed", "sign-applet test", true, false);
					JOptionPane.showMessageDialog(this, fileName + " firmato");
					String signingResult = null;
					if (signingResult != null) {
						String certChainFieldName = this
								.getParameter(CERT_CHAIN_FIELD_PARAM);
						JSObject certChainField = (JSObject) mainForm
								.getMember(certChainFieldName);
						certChainField.setMember("value", "OK");
						String signatureFieldName = this
								.getParameter(SIGNATURE_FIELD_PARAM);
						JSObject signatureField = (JSObject) mainForm
								.getMember(signatureFieldName);
						signatureField.setMember("value", "OK");
					} else {
						// User canceled signing
					}
				} catch (Exception e) {
				}

				finally {
					mSignButton.setLabel(oldButtonLabel);
					mSignButton.setEnabled(true);
				}
			}
		} catch (SecurityException se) {
			se.printStackTrace();
			JOptionPane
					.showMessageDialog(
							this,
							"Unable to access the local file system.\n"
									+ "This applet should be started with full security permissions.\n"
									+ "Please accept to trust this applet when the Java Plug-In ask you.");
		} catch (JSException jse) {
			jse.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Unable to access some of the fields of the\n"
							+ "HTML form. Please check the applet parameters.");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Unexpected error: "
					+ e.getMessage());
		}
	}

	/**
	 * @throws Exception
	 * @throws IOException
	 * @throws CertificateException
	 */
	private void composeTestCertificateChain() throws Exception, IOException,
			CertificateException {

		String caCert = ""
				+ "-----BEGIN CERTIFICATE-----					\n"
				+ "MIIEGzCCAwOgAwIBAgIEPMxVlzANBgkqhkiG9w0BAQUFADBRMQswCQYDVQQGEwJF\n"
				+ "UzEfMB0GA1UEChMWR2VuZXJhbGl0YXQgVmFsZW5jaWFuYTEPMA0GA1UECxMGUEtJ\n"
				+ "R1ZBMRAwDgYDVQQDEwdDQVRFU1QxMB4XDTAyMDQyODIwMDM1M1oXDTEyMDQyNTE5\n"
				+ "MDM1M1owUTELMAkGA1UEBhMCRVMxHzAdBgNVBAoTFkdlbmVyYWxpdGF0IFZhbGVu\n"
				+ "Y2lhbmExDzANBgNVBAsTBlBLSUdWQTEQMA4GA1UEAxMHQ0FURVNUMTCCASIwDQYJ\n"
				+ "KoZIhvcNAQEBBQADggEPADCCAQoCggEBAPa5MvL05R8MJAxc3CxQo3uvqGdqF2P7\n"
				+ "rd21uerg8seDVemK7vcI6vPAzK5zR9zYWXHY8ud36tIwhSFvJlGqr1n5d3njrxAO\n"
				+ "Y5B4LlOvVyvwRIBkNvzU4uQGAUsMCpShmyugexHQllr1LUWXZPRBXkbvmSK1dtuY\n"
				+ "KhwucGH8X24AFEOsVKI/RTwYaSRoruf+Mrf7z/rjpZwAm7ds55krPz6ppjFDUoer\n"
				+ "EVS9kForEnuADQeauFwzPBFOM/k/p4J1xL4Hi8arR0X64pCBxp03eJz7bkuJMqgd\n"
				+ "NlURwQKl2yittWRGma1X6ohnk5LxMhj0SavYAjoUkx7ip5wHGveJMyMCAwEAAaOB\n"
				+ "+jCB9zA0BggrBgEFBQcBAQQoMCYwJAYIKwYBBQUHMAGGGGh0dHA6Ly9jYXRlc3Qu\n"
				+ "cGtpLmd2YS5lczASBgNVHRMBAf8ECDAGAQH/AgEAMB0GA1UdDgQWBBSaTinPNSGg\n"
				+ "ZilrdhqvGxhYhtyymDB8BgNVHSMEdTBzgBSaTinPNSGgZilrdhqvGxhYhtyymKFV\n"
				+ "pFMwUTELMAkGA1UEBhMCRVMxHzAdBgNVBAoTFkdlbmVyYWxpdGF0IFZhbGVuY2lh\n"
				+ "bmExDzANBgNVBAsTBlBLSUdWQTEQMA4GA1UEAxMHQ0FURVNUMYIEPMxVlzAOBgNV\n"
				+ "HQ8BAf8EBAMCAQYwDQYJKoZIhvcNAQEFBQADggEBALZQBKtViQVcA3LlaZiCLroz\n"
				+ "bIp1EUWCb4SEPBBSgXsGtpVcxT0AwaG8q4CHXnLybvIG2gDI1rK3/MVM1cUuX9ie\n"
				+ "WeAUN/WJq3vG9+ekD3C1FDflWHB1I9YR5B8Ew/Kb8/7ddqhMbhV8QwUHxCjVhy+5\n"
				+ "Dlhxq/dPt1jeMa/Ays94Ug1DVSXpP7Pt/SE7kn+Xl8F+QEDkBg1U7jfVJ6/cio/d\n"
				+ "eMAkwen1Cp5DSLY14aScYz25OAS4dWlO9sCnrFjl4rp8zYqsZCNMnxA64SeMfFZB\n"
				+ "1n4qsWF9/tc67P+J59alV++RpTcppFLVSvQPHiWTR8HS0EC6dkSrxCS3XwV0oJk=\n"
				+ "-----END CERTIFICATE-----";

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		byte[] caCertBytes = caCert.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(caCertBytes);
		Certificate certificate = cf
				.generateCertificate(new BufferedInputStream(bais));
		bais.close();

		certificateChain = new X509Certificate[] { (X509Certificate) certificate };
	}

	/**
	 * Exception class used for document signing errors.
	 */
	static class DocumentSignException extends Exception {
		public DocumentSignException(String aMessage) {
			super(aMessage);
		}

		public DocumentSignException(String aMessage, Throwable aCause) {
			super(aMessage, aCause);
		}
	}

}
