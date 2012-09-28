package org.opensignature.opensignpdf.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import org.apache.log4j.Logger;
import org.opensignature.opensignpdf.PDFSignatureValidator;
import org.opensignature.opensignpdf.PDFSigner;
import org.opensignature.opensignpdf.tools.FileUtils;

import com.lowagie.text.DocumentException;



/**
 * TestCase over the PDFSigner class.
 * 
 * @author <a href="mailto:japaricio@accv.es">Javier Aparicio</a>
 * 
 */
public class TestPDFSigner extends TestAbstractOpensignature {

  /**
   * Loggger de clase
   */
  private static Logger logger = Logger.getLogger(TestPDFSigner.class);
  
  

  /**
   * @param name
   */
  public TestPDFSigner(String name) {
    super(name);
  }


  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    initializeTestCase();    
    
  }
  
  
  

  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  
  

  /**
   * Test method for
   * {@link org.opensignature.opensignpdf.PDFSigner#signPDF(org.opensignature.opensignpdf.pkcs11.MyPkcs11, iaik.pkcs.pkcs11.Session, java.io.File, java.io.OutputStream, java.lang.String)}.
   */
  public final void testSignPDF() {

    logger.info("[testSignPDF.in]:: ");

    File fOutDir = new File("etc/mytest");

    try {
      

      //-- PDF file to Sign
      String pdfFileName = DEFAULT_PDF_NAME;
      String signed_suffix = "_sig_pkcs11";
      
      File fPDF       = new File(fOutDir, pdfFileName);
      if ( !fPDF.exists() ){ createPDFDocument(fPDF); }
      logger.debug("[testSignPDF]:: fPDF.getAbsolutePath()      : " + fPDF.getAbsolutePath());
        
      logger.debug("[testSignPDF]:: Creating PDF Signer...");
      PDFSigner signer  = new PDFSigner( certificateChain, TSS, null, null, "author", null, "false" );
      logger.debug("[testSignPDF]:: Signing PDF field 'signature'...");
      
      signer.signPDF(pkcs11, session, new File[]{fPDF}, signed_suffix, "Test", true);
      logger.debug("[testSignPDF]:: PDF signed with PKCS11.");
      
      File pdfSigned = FileUtils.addSuffix(fPDF, signed_suffix, true); 
      PDFSignatureValidator validator = new PDFSignatureValidator();
      logger.debug("[testSignPDF]:: Is valid signature?: " + validator.validate(pdfSigned, null));
      
      
    } catch (Throwable ex) {
      logger.debug("[testSignPDF]:: ", ex);
    }

    logger.info("[testSignPDF.out]:: ");

  }
  
  
  
  /**
   * Test method for
   * {@link org.opensignature.opensignpdf.PDFSigner#signPDFwithKS()}.
   */
  /*
  public final void testSignPDFwithKS() {

    logger.info("[testSignPDFwithKS.in]:: ");

    File fOutDir = new File("etc/mytest");

    try {


      // -- PDF file to Sign
      String pdfFileName = DEFAULT_PDF_NAME;

      File fPDF = new File(fOutDir, pdfFileName);
      if ( !fPDF.exists() ){ createPDFDocument(fPDF); }
      logger.debug("[testSignPDFwithKS]:: fPDF.getAbsolutePath(): " + fPDF.getAbsolutePath());

      String ksType = "pkcs12";
      String pwd    = "8257097";
      File ksFile   = new File("etc/certs/tpruebas254w_firma.p12");
      
      KeyStore ks = KeyStore.getInstance(ksType);
      ks.load(new FileInputStream(ksFile), pwd.toCharArray());

      logger.debug("[testSignPDFwithKS]:: Creating PDF Signer...");
      PDFSigner signer = new PDFSigner(certificateChain, TSS, null, null, "author", null, "false");
      
      logger.debug("[testSignPDFwithKS]:: PDFSigner Creado");
      signer.signPDFwithKS(ks, null, pwd, new File[] { fPDF }, "_sig_pkcs12", "Test", false);

      logger.debug("[testSignPDFwithKS]:: PDF signed.");

    } catch (Throwable ex) {
      logger.debug("[testSignPDFwithKS]:: ", ex);
    }

    logger.info("[testSignPDFwithKS.out]:: ");
    

  }
  */
  
  /**
   * 
   *
   */
  
/*
  public final void testCreatePDF() {

    logger.info("[testCreatePDF.in]:: ");
    File fDir = new File("etc/mytest");
    File pdf = new File(fDir, DEFAULT_PDF_NAME);
    
    try {
      
      createPDFDocument(pdf);
      
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    logger.info("[testCreatePDF.retorna]:: ");
  }
*/
  
}
