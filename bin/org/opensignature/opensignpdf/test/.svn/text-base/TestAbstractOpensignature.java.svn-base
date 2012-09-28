package org.opensignature.opensignpdf.test;

import iaik.pkcs.pkcs11.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.opensignature.opensignpdf.pkcs11.MyPkcs11;
import org.opensignature.opensignpdf.tools.CertUtil;
import org.opensignature.opensignpdf.tools.IOUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;



public abstract class TestAbstractOpensignature extends TestCase {

  
  /**
   * Loggger de clase
   */
  private static Logger logger = Logger.getLogger(TestAbstractOpensignature.class);
  
  
  
  /** Default pdf file name to work with */
  protected static final String DEFAULT_PDF_NAME = "signature.pdf";
  
  /** Default PKCS11Module Name */
  protected static final String PKCS11 = "CardOS_PKCS11.dll";
//  protected static final String PKCS11 = "incryptoki2.dll";
  
  /** PKCS11 PIN */
  /* SET UP YOUR PIN!!! */
  protected static final String PIN = null;
  
  /** Timestamp Server URL */
  protected static final String TSS = "http://tss.pki.gva.es:8318/tsa";


  
  
  //-- TEST CASE ATTRIBUTES
  protected MyPkcs11 pkcs11 = null;
  protected Session session = null;
  protected X509Certificate[] certificateChain = null;
  
  
  
  
  
  /**
   * 
   */
  public TestAbstractOpensignature() {
    super();
  }

  /**
   * @param name
   */
  public TestAbstractOpensignature(String name) {
    super(name);
  }
  
  
  
  
  /**
   * This method initilized the logger, PKCS11, etc...
   * It must be called in the setUp of the TestCase classes.
   *
   */
  protected void initializeTestCase(){
    
    //-- Logger config
    PropertyConfigurator.configure("etc/conf/log4j.properties");
    
    try {

      //-- Initializes the certificate chain for the signatures
      composeTestCertificateChain();

      if ( pkcs11 == null ){ pkcs11 = new MyPkcs11(); }
      assertNotNull("PKCS11 module can't be null.", pkcs11);
      
      if ( session == null ){ session = pkcs11.initSession(PKCS11, PIN); }
      assertNotNull("PKCS11 session can't be null.", session);
      
    }catch( Exception ex ){
      ex.printStackTrace();
    }

  }
  
  
  /**
   * @throws Exception
   * @throws IOException
   * @throws CertificateException
   */
  protected void composeTestCertificateChain() throws Exception, IOException, CertificateException {
    
    //-- Test certificate chain 
//    String[] certificates = new String[]{"rootca.cer", "ca.cer"};
    String[] certificates = new String[]{"catest1.cer"};
    
    
    File certsDir = new File("etc/certs");
    certificateChain = new X509Certificate[certificates.length];
    
    for (int i = 0; i < certificates.length; i++) {
      
      File caCertFile = new File(certsDir, certificates[i]);
      if ( !caCertFile.exists() ){ throw new Exception("Certificate '" + caCertFile.getName() + "' not found."); }
      
      logger.debug("[setUp]:: Cert File: " + caCertFile.getAbsolutePath());
      X509Certificate cert = CertUtil.toX509Certificate(IOUtils.readBytesFromFile(caCertFile.getAbsolutePath()));
      
      certificateChain[i] = cert;
    }

  }



  
  
  /**
   * Builds a test PDF Document.
   * 
   * @param fPDF
   * @throws DocumentException
   * @throws IOException
   */
  protected void createPDFDocument(File fPDF) throws DocumentException, IOException {

    logger.info("[createPDFDocument.in]:: ");

    // -- step 1: creation of a document-object
    Document document = new Document(PageSize.A4);

    // -- step 2:
    FileOutputStream fos = new FileOutputStream(fPDF);
    PdfWriter writer = PdfWriter.getInstance(document, fos);

    // step 3: we open the document
    document.open();

    // step 4:
    document.add(new Paragraph("Hello World"));
    
//    //-- step 5: adding signature fields
//    PdfAcroForm acroForm = writer.getAcroForm();
//    acroForm.addSignature("signature", 73, 705, 149, 759);
//    acroForm.addSignature("signature2", 0, 0, 149, 759);

    // step 6: we close the document
    document.close();
    
    fos.close();


    logger.info("[createPDFDocument.out]:: ");
  }
  
  

}
