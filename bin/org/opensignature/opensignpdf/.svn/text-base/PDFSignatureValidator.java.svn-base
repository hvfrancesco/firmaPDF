package org.opensignature.opensignpdf;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;



/**
 * Utility class to embrace all the stuff about the PDF signature validation
 * process.
 * 
 * @author <a href="mailto:japaricio@accv.es">Javier Aparicio</a>
 * 
 */
public class PDFSignatureValidator {


  /**
   * Loggger's class
   */
  private static Logger logger = Logger.getLogger(PDFSignatureValidator.class);


  
  /**
   * Default 
   *
   */
  public PDFSignatureValidator() {}

  

  /**
   * Validate all signatures in all revisions in the signed PDF file.
   * 
   * @param pdfSigned
   * @param ks
   * 
   * @return
   * @throws IOException
   * @throws SignatureException
   */
  public boolean validate(File pdfSigned, KeyStore ks) throws IOException, SignatureException {
    
    InputStream is = new BufferedInputStream(new FileInputStream(pdfSigned));
    return validate(is, ks);
    
  }
  
  
  
  /**
   * Validate all signatures in all revisions in the signed PDF file.
   * 
   * @param is
   * @param ks, KeyStore with the trusted certs to perform the certificate chain validation.
   *  
   * @return
   * @throws IOException
   * @throws SignatureException
   */
  public boolean validate(InputStream is, KeyStore ks) throws IOException, SignatureException {

    logger.info("[validate.entrada]:: ");

    boolean isValidSignature = true;

    if ( ks == null ){
      //-- Try to validate the certificate chain against the root 
      //-- certificates at <java.home>/lib/security/cacerts with the default provider 
      ks = PdfPKCS7.loadCacertsKeyStore();
    }

    PdfReader reader = new PdfReader(is);
    AcroFields af = reader.getAcroFields();
    ArrayList names = af.getSignatureNames();

    if ( names == null || names.size() < 1 ){
      throw new SignatureException("There are no signature fields to validate in the document.");
    }
    
    for (int k = 0; k < names.size(); ++k) {

      String name = (String) names.get(k);

      logger.debug("[validate]:: Signature name: " + name);
      logger.debug("[validate]:: Signature covers whole document: " + af.signatureCoversWholeDocument(name));
      logger.debug("[validate]:: Document revision: " + af.getRevision(name) + " of " + af.getTotalRevisions());

      // Start revision extraction
      File tmpRev = File.createTempFile("revision_", af.getRevision(name) + ".pdf", new File(System.getProperty("user.home")));
      FileOutputStream out = new FileOutputStream(tmpRev);

      byte bb[] = new byte[8192];
      InputStream ip = af.extractRevision(name);
      int n = 0;
      while ((n = ip.read(bb)) > 0) {
        out.write(bb, 0, n);
      }
      out.close();
      ip.close();
      tmpRev.deleteOnExit();

      // End revision extraction
      PdfPKCS7 pk = af.verifySignature(name);
      Calendar cal = pk.getSignDate();
      Certificate pkc[] = pk.getCertificates();

      logger.debug("[validate]:: Subject: " + PdfPKCS7.getSubjectFields(pk.getSigningCertificate()));
      
      isValidSignature = pk.verify();
      if ( !isValidSignature ){
        break;
      }

      Object fails[] = PdfPKCS7.verifyCertificates(pkc, ks, null, cal);

      if (fails != null) {
        logger.debug("[validate]:: Certificate validation failed: " + fails[1]);
        isValidSignature = false;
      }
    }
    
    //-- Closing the InputStream
    is.close();

    logger.info("[validate.out]:: " + isValidSignature);
    return isValidSignature;
  }


}
