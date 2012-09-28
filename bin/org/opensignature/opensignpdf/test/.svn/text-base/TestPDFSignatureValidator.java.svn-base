/**
 * 
 */
package org.opensignature.opensignpdf.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SignatureException;

import org.apache.log4j.Logger;
import org.opensignature.opensignpdf.PDFSignatureValidator;
import org.opensignature.opensignpdf.tools.FileUtils;




/**
 * TestCase over the PDFSignatureValidator class.
 * 
 * @author <a href="mailto:japaricio@accv.es">Javier Aparicio</a>
 * 
 */
public class TestPDFSignatureValidator extends TestAbstractOpensignature {

  
  /**
   * Loggger de clase
   */
  private static Logger logger = Logger.getLogger(TestPDFSignatureValidator.class);
  
  
  

  /**
   * @param name
   */
  public TestPDFSignatureValidator(String name) {
    super(name);
  }


  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    initializeTestCase();
  }


  /* (non-Javadoc)
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }


  /**
   * Test method for {@link org.opensignature.opensignpdf.PDFSignatureValidator#validate(java.io.File)}.
   */
  public final void testValidate() {
    
    logger.info("[testValidate.in]:: ");

    String pdfFileName = DEFAULT_PDF_NAME;
    String signed_suffix = "_sig_pkcs11";
    
    File workDir = new File("etc/mytest");
    File fPDF = new File(workDir, pdfFileName);
    File pdfSigned = FileUtils.addSuffix(fPDF, signed_suffix, true);
    
    PDFSignatureValidator validator = new PDFSignatureValidator();
    
    KeyStore ks = null;
    try {
      ks = KeyStore.getInstance("JKS");
      ks.load(new FileInputStream("etc/conf/trustedCerts.jks"), "XXXX".toCharArray());
    } catch (Exception e) {
      logger.warn("[testValidate]:: ", e);
    }

    
    try {
      validator.validate(fPDF, ks);
    } catch (SignatureException e) {
      logger.warn("[testValidate]:: ", e);
      assertTrue( e.getMessage().toUpperCase().startsWith("THERE ARE NO SIGNATURE") );
    } catch (IOException e) {
      logger.warn("[testValidate]:: ", e);
      fail(e.getMessage());
    }
    
    boolean isValid = false;
    try {
      isValid = validator.validate(pdfSigned, ks);
    } catch (Exception e) {
      logger.warn("[testValidate]:: ", e);
      fail(e.getMessage());
    }
    assertTrue(isValid);
    
    logger.info("[testValidate.out]:: ");
  }

}
