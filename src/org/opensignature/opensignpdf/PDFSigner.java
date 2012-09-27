/*
 * FirmaPdf version 0.0.x Copyright (C) 2006 Antonino Iacono (ant_iacono@tin.it)
 * and Roberto Resoli
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.opensignature.opensignpdf;


import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.Key;
import iaik.pkcs.pkcs11.objects.X509PublicKeyCertificate;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opensignature.opensignpdf.exceptions.OpenSignatureException;
import org.opensignature.opensignpdf.pkcs11.MyPkcs11;
import org.opensignature.opensignpdf.tools.CertUtil;
import org.opensignature.opensignpdf.tools.FileUtils;
import org.opensignature.opensignpdf.tools.IOUtils;
import org.opensignature.opensignpdf.tools.PKCS11Util;
import org.opensignature.opensignpdf.tools.TimeStampClient;

import com.lowagie.bc.asn1.ASN1EncodableVector;
import com.lowagie.bc.asn1.ASN1InputStream;
import com.lowagie.bc.asn1.ASN1Sequence;
import com.lowagie.bc.asn1.DERConstructedSet;
import com.lowagie.bc.asn1.DERInteger;
import com.lowagie.bc.asn1.DERNull;
import com.lowagie.bc.asn1.DERObject;
import com.lowagie.bc.asn1.DERObjectIdentifier;
import com.lowagie.bc.asn1.DEROctetString;
import com.lowagie.bc.asn1.DERSequence;
import com.lowagie.bc.asn1.DERSet;
import com.lowagie.bc.asn1.DERTaggedObject;
import com.lowagie.bc.asn1.DERUTCTime;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfSignatureAppearanceOSP;
import com.lowagie.text.pdf.PdfStamperOSP;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.Image;


/**
 * Utility class to embrace all the stuff about the PDF signing process.
 * 
 * @author <a href="mailto:japaricio@accv.es">Javier Aparicio</a>
 * 
 */
public class PDFSigner {


  /**
   * Class Logger
   */
  private static Logger logger = Logger.getLogger(PDFSigner.class);




  private X509Certificate[] certificateChain = null;

  private URL serverTimestamp = null;

  private String usernameTimestamp = null;

  private String passwordTimestamp = null;

  private String typeSignatureSelected = null;

  private String fieldName = null;

  private String openOfficeSelected = null;

  private String graphicSignSelected = null;

  private String fileImgfirma = null;

  private int llx = 340;

  private int lly = 600;

  private int urx = 560;

  private int ury = 700;



  /**
   * Default constructor
   * 
   * @param chain,
   *          CA certificate chain
   * @param serverTimestamp
   * @param usernameTimestamp
   * @param passwordTimestamp
   * @throws MalformedURLException
   */
  public PDFSigner(X509Certificate[] chain, String serverTimestamp, String usernameTimestamp, String passwordTimestamp, String typeSignatureSelected,
                   String fieldName, String openOfficeSelected, String graphicSignSelected, String fileImgfirma, int llx, int lly, int urx, int ury) throws MalformedURLException {

    logger.info("[PDFSigner.in]:: "
                + Arrays.asList(new Object[] { chain, serverTimestamp, usernameTimestamp, passwordTimestamp, typeSignatureSelected, fieldName,
                                              openOfficeSelected }));

    this.certificateChain = (chain == null ? new X509Certificate[] {} : chain);
    this.serverTimestamp = ("".equals(serverTimestamp) ? null : new URL(serverTimestamp));
    this.usernameTimestamp = usernameTimestamp;
    this.passwordTimestamp = passwordTimestamp;
    this.typeSignatureSelected = typeSignatureSelected;
    this.fieldName = fieldName;
    this.openOfficeSelected = openOfficeSelected;
    this.graphicSignSelected = graphicSignSelected;
    this.fileImgfirma = fileImgfirma;

    this.llx = llx;
    this.lly = lly;
    this.urx = urx;
    this.ury = ury;

    logger.info("[PDFSigner.out]:: ");
  }




  /**
   * Method to mantain the back compatibility
   * 
   * @param mySign
   * @param session
   * @param pdfFiles
   * @param suffix
   * @param reason
   * @param signatureVisibility
   * @throws TokenException
   * @throws IOException
   * @throws CertificateException
   * @throws OpenSignatureException
   * @throws FileNotFoundException
   * @throws DocumentException
   * @throws NoSuchAlgorithmException
   * @throws ExceptionConverter
   */
  public void signPDF(MyPkcs11 mySign, Session session, File[] pdfFiles, String suffix, String reason, boolean signatureVisibility) throws TokenException,
                                                                                                                                   IOException,
                                                                                                                                   CertificateException,
                                                                                                                                   OpenSignatureException,
                                                                                                                                   FileNotFoundException,
                                                                                                                                   DocumentException,
                                                                                                                                   NoSuchAlgorithmException,
                                                                                                                                   ExceptionConverter {

    // -- Invocation with the default value for the signature date
    signPDF(mySign, session, pdfFiles, suffix, reason, signatureVisibility, null);

  }


  /**
   * Allow you to sign a PDF File with a PKCS11 session opened.
   * 
   * @param mySign
   * @param session
   * @param pdfFiles
   * @param suffix
   * @param reason
   * @param signatureVisibility
   * @param cal
   * @throws TokenException
   * @throws IOException
   * @throws CertificateException
   * @throws OpenSignatureException
   * @throws FileNotFoundException
   * @throws DocumentException
   * @throws NoSuchAlgorithmException
   * @throws ExceptionConverter
   */
  public void signPDF(	MyPkcs11 mySign, 
		  				Session session, 
		  				File[] pdfFiles, 
		  				String suffix, 
		  				String reason, 
		  				boolean signatureVisibility, 
		  				Calendar cal) 
  			throws	TokenException,
  				  	IOException,
  				  	CertificateException,
  				  	OpenSignatureException,
  				  	FileNotFoundException,
  				  	DocumentException,
  				  	NoSuchAlgorithmException,
  				  	ExceptionConverter {

    if (pdfFiles == null || mySign == null || session == null) {
      throw new OpenSignatureException("Invalid parameters.");
    }

    // -- System's date by default
    if (cal == null) {
      cal = Calendar.getInstance();
    }

    logger.info("[signPDF.in]:: " + 
    		Arrays.asList(new Object[] { "<mySign>", "<session>", Arrays.asList(pdfFiles), "<os>", reason }));

    // -- Obtaining all candidates to be a valid certificate signature
    Map map = PKCS11Util.getCertificateMap(session);

    // -- Choose the first ( depending on some criteria )
    X509PublicKeyCertificate signCertObject = PKCS11Util.getFirstSignCertificate(map);

    // -- Getting the private key for the selected certificate
    Key signCertKeyObject = PKCS11Util.findCertificatePrivateKey(session, signCertObject);

    // -- Certificate chain for the sign
    X509Certificate[] certs = new X509Certificate[this.certificateChain.length + 1];
    for (int i = 0; i < this.certificateChain.length; i++) {
      certs[1 + i] = this.certificateChain[i];
    }

    // -- Add the Sign certificate from the smartcard
    byte[] certValue = signCertObject.getValue().getByteArrayValue();
    certs[0] = CertUtil.toX509Certificate(certValue);

    for (int i = 0; i < pdfFiles.length; i++) {

      logger.info("[signPDF.in]:: Signing the file: " + pdfFiles[i].getAbsolutePath());

      try {

        // -- Check the access to the PDF
        if (!pdfFiles[i].exists() || !pdfFiles[i].canRead()) {
          throw new FileNotFoundException("The file '" + pdfFiles[i].getAbsolutePath() + "' doesn't exist.");
        }

        // -- Creating the OutputStream overwritting the file if it exists
        // previously
        File fOut = FileUtils.addSuffix(pdfFiles[i], suffix, true);
        FileOutputStream fos = new FileOutputStream(fOut);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        // -- Creating the reader
        PdfReader reader = createPDFReader(pdfFiles[i]);

        PdfStamperOSP stamper;

        if ("countersigner".equals(typeSignatureSelected)) {
          stamper = PdfStamperOSP.createSignature(reader, bos, '\0', null, true);
        } else {
          stamper = PdfStamperOSP.createSignature(reader, bos, '\0');
        }

        createSignatureAppearance(mySign, session, reason, signCertKeyObject, certs, stamper, signatureVisibility, cal);

        bos.close();
        fos.close();

      } catch (Exception e) {
        logger.warn("[signPDF]:: ", e);
      }

    }

    logger.info("[signPDF.out]:: ");

  }


  
  
  /**
   * Method to mantain the back compatibility
   * 
   * @param mySign
   * @param session
   * @param pdfFiles
   * @param suffix
   * @param reason
   * @param signatureVisibility
   * @param cal
   * @throws OpenSignatureException
   * @throws TokenException
   * @throws IOException
   * @throws CertificateException
   * @throws OpenSignatureException
   * @throws KeyStoreException
   * @throws UnrecoverableKeyException
   * @throws NoSuchAlgorithmException
   * @throws FileNotFoundException
   * @throws DocumentException
   * @throws NoSuchAlgorithmException
   * @throws ExceptionConverter
   */
  public void signPDFwithKS(KeyStore ks, String alias, String pwd, File[] pdfFiles, String suffix, String reason, boolean signatureVisibility)
  throws OpenSignatureException,
  KeyStoreException,
  NoSuchAlgorithmException,
  UnrecoverableKeyException {
    
    signPDFwithKS(ks, alias, pwd, pdfFiles, suffix, reason, signatureVisibility, null);
    
  }
  
  

  /**
   * Allow you to sign a PDF File with a PKCS11 session opened.
   * 
   * @param mySign
   * @param session
   * @param pdfFiles
   * @param suffix
   * @param reason
   * @param signatureVisibility
   * @param cal
   * @throws OpenSignatureException
   * @throws TokenException
   * @throws IOException
   * @throws CertificateException
   * @throws OpenSignatureException
   * @throws KeyStoreException
   * @throws UnrecoverableKeyException
   * @throws NoSuchAlgorithmException
   * @throws FileNotFoundException
   * @throws DocumentException
   * @throws NoSuchAlgorithmException
   * @throws ExceptionConverter
   */
  public void signPDFwithKS(KeyStore ks, String alias, String pwd, File[] pdfFiles, String suffix, String reason, boolean signatureVisibility, Calendar cal)
                                                                                                                                                            throws OpenSignatureException,
                                                                                                                                                            KeyStoreException,
                                                                                                                                                            NoSuchAlgorithmException,
                                                                                                                                                            UnrecoverableKeyException {

    if (pdfFiles == null || ks == null) {
      throw new OpenSignatureException("Invalid parameters.");
    }

    // -- System's date by default 
    if (cal == null) {
      cal = Calendar.getInstance();
    }

    logger.info("[signPDFwithKS.in]:: " + Arrays.asList(new Object[] { "<ks>", alias, Arrays.asList(pdfFiles), suffix, reason, Boolean.valueOf(signatureVisibility) }));


    if (alias == null) {

      Enumeration aliases = ks.aliases();
      while (aliases.hasMoreElements()) {
        String alTmp = (String) aliases.nextElement();
        logger.debug("[signPDFwithKS]:: alTmp: " + alTmp);

        X509Certificate x509certificate = (X509Certificate) ks.getCertificate(alTmp);
        boolean[] keyUsage = x509certificate.getKeyUsage();
        if (keyUsage != null && (keyUsage[1] || keyUsage[0])) {
          alias = alTmp;
          break;
        }

      }
    }

    logger.debug("\n\n[signPDFwithKS]:: alias: " + alias + "\n\n");
    PrivateKey key = (PrivateKey) ks.getKey(alias, pwd.toCharArray());
    Certificate[] certs = ks.getCertificateChain(alias);


    for (int i = 0; i < pdfFiles.length; i++) {

      logger.info("[signPDFwithKS]:: Signing the file: " + pdfFiles[i].getAbsolutePath());

      try {

        // -- Check the access to the PDF
        if (!pdfFiles[i].exists() || !pdfFiles[i].canRead()) {
          throw new FileNotFoundException("The file '" + pdfFiles[i].getAbsolutePath() + "' doesn't exist.");
        }

        byte signatureBytes[] = new byte[128];

        // -- Creating the OutputStream overwritting the file if it exists
        // previously
        File fOut = FileUtils.addSuffix(pdfFiles[i], suffix, true);
        FileOutputStream fos = new FileOutputStream(fOut);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        // -- Creating the reader
        PdfReader reader = createPDFReader(pdfFiles[i]);

        PdfStamperOSP stamper;

        if ("countersigner".equals(typeSignatureSelected)) {
          stamper = PdfStamperOSP.createSignature(reader, bos, '\0', null, true);
        } else {
          stamper = PdfStamperOSP.createSignature(reader, bos, '\0');
        }

        PdfSignatureAppearanceOSP sap = stamper.getSignatureAppearance();
        sap.setCrypto(null, certs, null, PdfSignatureAppearance.WINCER_SIGNED);
        sap.setReason(reason);

        if (signatureVisibility) {
          if ("countersigner".equals(typeSignatureSelected)) {
            sap.setCertified(0);
            sap.setVisibleSignature(fieldName);
          } else {
            sap.setCertified(2);
            if (!"".equals(fieldName)) {
              sap.setVisibleSignature(fieldName);
            } else {
              sap.setVisibleSignature(new com.lowagie.text.Rectangle(llx, lly, urx, ury), 1, null);
            }
          }

        }


        sap.setExternalDigest(new byte[128], new byte[20], "RSA");

        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.FT, PdfName.SIG);
        dic.put(PdfName.FILTER, new PdfName("Adobe.PPKLite"));
        dic.put(PdfName.SUBFILTER, new PdfName("adbe.pkcs7.detached"));
        if (cal != null) {
          dic.put(PdfName.M, new PdfDate(cal));
        } else {
          dic.put(PdfName.M, new PdfNull());
        }
        dic.put(PdfName.NAME, new PdfString(PdfPKCS7.getSubjectFields((X509Certificate) certs[0]).getField("CN")));
        dic.put(PdfName.REASON, new PdfString(reason));

        sap.setCryptoDictionary(dic);

        HashMap exc = new HashMap();
        exc.put(PdfName.CONTENTS, new Integer(0x5002));
        sap.preClose(exc);

        byte[] content = IOUtils.streamToByteArray(sap.getRangeStream());
        //SHA256, alias CMSSignedDataGenerator.DIGEST_SHA256,
        //        alias NISTObjectIdentifiers.id_sha256.getId(),
        //		  alias "2.16.840.1.101.3.4.2.1"
        byte[] hash = MessageDigest.getInstance("2.16.840.1.101.3.4.2.1", "BC").digest(content);

        // costruzione degli authenticated attributes
        ASN1EncodableVector signedAttributes = buildSignedAttributes(hash, cal);
        byte[] bytesForSecondHash = IOUtils.toByteArray(new DERSet(signedAttributes));

        // -- Signature generated with the private key of the KS
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        signature.update(bytesForSecondHash);
        signatureBytes = signature.sign();

        byte[] encodedPkcs7 = null;
        try {

          // Create the set of Hash algorithms
          DERConstructedSet digestAlgorithms = new DERConstructedSet();

          // Creo manualmente la sequenza di digest algos
          ASN1EncodableVector algos = new ASN1EncodableVector();
          //algos.add(new DERObjectIdentifier("1.3.14.3.2.26")); // SHA1
          //SHA-256
          algos.add(new DERObjectIdentifier("2.16.840.1.101.3.4.2.1"));
          algos.add(new DERNull());
          digestAlgorithms.addObject(new DERSequence(algos));

          // Create the contentInfo.
          ASN1EncodableVector ev = new ASN1EncodableVector();
          ev.add(new DERObjectIdentifier("1.2.840.113549.1.7.1")); // PKCS7SignedData

          DERSequence contentinfo = new DERSequence(ev);

          // Get all the certificates
          //
          ASN1EncodableVector v = new ASN1EncodableVector();
          for (int c = 0; c < certs.length; c++) {
            ASN1InputStream tempstream = new ASN1InputStream(new ByteArrayInputStream(certs[c].getEncoded()));
            v.add(tempstream.readObject());
          }

          DERSet dercertificates = new DERSet(v);

          // Create signerinfo structure.
          //
          ASN1EncodableVector signerinfo = new ASN1EncodableVector();

          // Add the signerInfo version
          //
          signerinfo.add(new DERInteger(1));

          v = new ASN1EncodableVector();
          v.add(CertUtil.getIssuer((X509Certificate) certs[0]));
          v.add(new DERInteger(((X509Certificate) certs[0]).getSerialNumber()));
          signerinfo.add(new DERSequence(v));

          // Add the digestAlgorithm
          v = new ASN1EncodableVector();
          //v.add(new DERObjectIdentifier("1.3.14.3.2.26")); // SHA1
          //SHA-256
          v.add(new DERObjectIdentifier("1.2.840.113549.1.7.1"));
          v.add(new DERNull());
          signerinfo.add(new DERSequence(v));

          // add the authenticated attribute if present
          signerinfo.add(new DERTaggedObject(false, 0, new DERSet(signedAttributes)));

          // Add the digestEncryptionAlgorithm
          v = new ASN1EncodableVector();
          v.add(new DERObjectIdentifier("1.2.840.113549.1.1.1"));// RSA
          v.add(new DERNull());
          signerinfo.add(new DERSequence(v));

          // Add the encrypted digest
          signerinfo.add(new DEROctetString(signatureBytes));

          // Add unsigned attributes (timestamp)
          if (serverTimestamp != null && !"".equals(serverTimestamp.toString())) {
            byte[] timestampHash = MessageDigest.getInstance("SHA-256").digest(signatureBytes);
            ASN1EncodableVector unsignedAttributes = buildUnsignedAttributes(timestampHash, serverTimestamp, usernameTimestamp, passwordTimestamp);
            if (unsignedAttributes != null) {
              signerinfo.add(new DERTaggedObject(false, 1, new DERSet(unsignedAttributes)));
            }
          }

          // Finally build the body out of all the components above
          ASN1EncodableVector body = new ASN1EncodableVector();
          body.add(new DERInteger(1)); // pkcs7 version, always 1
          body.add(digestAlgorithms);
          body.add(contentinfo);
          body.add(new DERTaggedObject(false, 0, dercertificates));


          // Only allow one signerInfo
          body.add(new DERSet(new DERSequence(signerinfo)));

          // Now we have the body, wrap it in it's PKCS7Signed shell
          // and return it
          //
          ASN1EncodableVector whole = new ASN1EncodableVector();
          whole.add(new DERObjectIdentifier("1.2.840.113549.1.7.2"));// PKCS7_SIGNED_DATA
          whole.add(new DERTaggedObject(0, new DERSequence(body)));

          encodedPkcs7 = IOUtils.toByteArray(new DERSequence(whole));

        } catch (Exception e) {
          throw new ExceptionConverter(e);
        }

        PdfDictionary dic2 = new PdfDictionary();

        byte out[] = new byte[0x5000 / 2];
        System.arraycopy(encodedPkcs7, 0, out, 0, encodedPkcs7.length);

        dic2.put(PdfName.CONTENTS, new PdfString(out).setHexWriting(true));
        sap.close(dic2);

        bos.close();
        fos.close();

      } catch (Exception e) {
        logger.warn("[signPDFwithKS]:: ", e);
      }

    }

    logger.info("[signPDFwithKS.out]:: ");

  }
  
  
  
  



  /**
   * @param mySign
   * @param session
   * @param reason
   * @param signCertKeyObject
   * @param certs
   * @param stamper
   * @throws IOException
   * @throws DocumentException
   * @throws NoSuchAlgorithmException
   * @throws TokenException
   * @throws ExceptionConverter
 * @throws NoSuchProviderException 
   */
  private void createSignatureAppearance(	MyPkcs11 mySign, 
		  									Session session,
		  									String reason,
		  									Key signCertKeyObject,
		  									X509Certificate[] certs,
		  									PdfStamperOSP stamper,
		  									boolean signatureVisible,
		  									Calendar cal) throws	IOException, 
		  															DocumentException,
		  															NoSuchAlgorithmException,
		  															TokenException,
		  															ExceptionConverter, NoSuchProviderException {

    logger.info("[createSignatureAppearance.in]:: ");

    byte[] signatureBytes = new byte[128];

    PdfSignatureAppearanceOSP sap = stamper.getSignatureAppearance();

    sap.setCrypto(null, certs, null, PdfSignatureAppearance.WINCER_SIGNED);
    sap.setReason(reason);

    if (signatureVisible) {
      if ("countersigner".equals(typeSignatureSelected)) {
        sap.setCertified(0);
        sap.setVisibleSignature(fieldName);
      } else {
        sap.setCertified(0);
        if ((fieldName!= null) && (!"".equals(fieldName))) {
          sap.setVisibleSignature(fieldName);
        } else {
          sap.setVisibleSignature(new com.lowagie.text.Rectangle(llx, lly, urx, ury), 1, null);
        }
      }

    }

    //aggiunta di grafico per la firma
    if ("true".equals(graphicSignSelected)) {
    	sap.setSignatureGraphic(Image.getInstance(fileImgfirma));
    	sap.setRender(2);
	}
    else {
	sap.setRender(0);
    }
    sap.setExternalDigest(new byte[128], new byte[20], "RSA");

    PdfDictionary dic = new PdfDictionary();
    dic.put(PdfName.FT, PdfName.SIG);
    dic.put(PdfName.FILTER, new PdfName("Adobe.PPKLite"));
    dic.put(PdfName.SUBFILTER, new PdfName("adbe.pkcs7.detached"));
    if (cal != null) {
      dic.put(PdfName.M, new PdfDate(cal));
    } else {
      dic.put(PdfName.M, new PdfNull());
    }
    dic.put(PdfName.NAME, new PdfString(PdfPKCS7.getSubjectFields((X509Certificate) certs[0]).getField("CN")));
    dic.put(PdfName.REASON, new PdfString(reason));

    sap.setCryptoDictionary(dic);

    HashMap exc = new HashMap();
    exc.put(PdfName.CONTENTS, new Integer(0x5002));
    sap.preClose(exc);

    byte[] content = IOUtils.streamToByteArray(sap.getRangeStream());
    byte[] hash = MessageDigest.getInstance("2.16.840.1.101.3.4.2.1", "BC").digest(content);

    // costruzione degli authenticated attributes
    ASN1EncodableVector signedAttributes = buildSignedAttributes(hash, cal);
    byte[] bytesForSecondHash = IOUtils.toByteArray(new DERSet(signedAttributes));

    byte[] secondHash = MessageDigest.getInstance("2.16.840.1.101.3.4.2.1").digest(bytesForSecondHash);


    // -- Generatting the signature
    signatureBytes = mySign.sign(session, secondHash, signCertKeyObject);

    byte[] encodedPkcs7 = null;
    try {

      // Create the set of Hash algorithms
      DERConstructedSet digestAlgorithms = new DERConstructedSet();

      // Creo manualmente la sequenza di digest algos
      ASN1EncodableVector algos = new ASN1EncodableVector();
      //algos.add(new DERObjectIdentifier("1.3.14.3.2.26")); // SHA1
      //SHA256
      algos.add(new DERObjectIdentifier("2.16.840.1.101.3.4.2.1"));
      algos.add(new DERNull());
      digestAlgorithms.addObject(new DERSequence(algos));

      // Create the contentInfo.
      ASN1EncodableVector ev = new ASN1EncodableVector();
      ev.add(new DERObjectIdentifier("1.2.840.113549.1.7.1")); // PKCS7SignedData

      DERSequence contentinfo = new DERSequence(ev);

      // Get all the certificates
      //
      ASN1EncodableVector v = new ASN1EncodableVector();
      for (int c = 0; c < certs.length; c++) {
        ASN1InputStream tempstream = new ASN1InputStream(new ByteArrayInputStream(certs[c].getEncoded()));
        v.add(tempstream.readObject());
      }

      DERSet dercertificates = new DERSet(v);

      // Create signerinfo structure.
      //
      ASN1EncodableVector signerinfo = new ASN1EncodableVector();

      // Add the signerInfo version
      //
      signerinfo.add(new DERInteger(1));

      v = new ASN1EncodableVector();
      v.add(CertUtil.getIssuer(certs[0]));
      v.add(new DERInteger(certs[0].getSerialNumber()));
      signerinfo.add(new DERSequence(v));

      // Add the digestAlgorithm
      v = new ASN1EncodableVector();
      //v.add(new DERObjectIdentifier("1.3.14.3.2.26")); // SHA1
      //SHA-256
      v.add(new DERObjectIdentifier("2.16.840.1.101.3.4.2.1"));
      v.add(new DERNull());
      signerinfo.add(new DERSequence(v));

      // add the authenticated attribute if present
      signerinfo.add(new DERTaggedObject(false, 0, new DERSet(signedAttributes)));

      // Add the digestEncryptionAlgorithm
      v = new ASN1EncodableVector();
      v.add(new DERObjectIdentifier("1.2.840.113549.1.1.1"));// RSA
      v.add(new DERNull());
      signerinfo.add(new DERSequence(v));

      // Add the encrypted digest
      signerinfo.add(new DEROctetString(signatureBytes));

      // Add unsigned attributes (timestamp)
      if (serverTimestamp != null && !"".equals(serverTimestamp.toString())) {
        byte[] timestampHash = MessageDigest.getInstance("2.16.840.1.101.3.4.2.1","BC").digest(signatureBytes);
        ASN1EncodableVector unsignedAttributes = buildUnsignedAttributes(timestampHash, serverTimestamp, usernameTimestamp, passwordTimestamp);
        if (unsignedAttributes != null) {
          signerinfo.add(new DERTaggedObject(false, 1, new DERSet(unsignedAttributes)));
        }
      }

      // Finally build the body out of all the components above
      ASN1EncodableVector body = new ASN1EncodableVector();
      body.add(new DERInteger(1)); // pkcs7 version, always 1
      body.add(digestAlgorithms);
      body.add(contentinfo);
      body.add(new DERTaggedObject(false, 0, dercertificates));


      // Only allow one signerInfo
      body.add(new DERSet(new DERSequence(signerinfo)));

      // Now we have the body, wrap it in it's PKCS7Signed shell
      // and return it
      //
      ASN1EncodableVector whole = new ASN1EncodableVector();
      whole.add(new DERObjectIdentifier("1.2.840.113549.1.7.2"));// PKCS7_SIGNED_DATA
      whole.add(new DERTaggedObject(0, new DERSequence(body)));

      encodedPkcs7 = IOUtils.toByteArray(new DERSequence(whole));

    } catch (Exception e) {
      throw new ExceptionConverter(e);
    }

    PdfDictionary dic2 = new PdfDictionary();

    byte out[] = new byte[0x5000 / 2];
    System.arraycopy(encodedPkcs7, 0, out, 0, encodedPkcs7.length);

    dic2.put(PdfName.CONTENTS, new PdfString(out).setHexWriting(true));
    sap.close(dic2);

    logger.info("[createSignatureAppearance.retorna]:: ");

  }




  /**
   * 
   * @param pdfFile
   * @return
   * @throws IOException
   * @throws DocumentException
   * @throws FileNotFoundException
   */
  private PdfReader createPDFReader(File pdfFile) throws IOException, DocumentException, FileNotFoundException {

    logger.info("[createPDFReader.in]:: " + Arrays.asList(new Object[] { pdfFile }));

    PdfReader reader;

    if ("true".equals(openOfficeSelected)) {
      String fileName = pdfFile.getPath();
      String tempFileName = fileName + ".temp";
      PdfReader documentPDF = new PdfReader(fileName);

      PdfStamperOSP stamperTemp = new PdfStamperOSP(documentPDF, new FileOutputStream(tempFileName));
      AcroFields af = stamperTemp.getAcroFields();
      af.setGenerateAppearances(true);
      PdfDictionary acro = (PdfDictionary) PdfReader.getPdfObject(documentPDF.getCatalog().get(PdfName.ACROFORM));
      acro.remove(PdfName.DR);
      HashMap fields = af.getFields();
      String key;
      for (Iterator it = fields.keySet().iterator(); it.hasNext();) {
        key = (String) it.next();
        int a = af.getFieldType(key);
        if (a == 4) {
          ArrayList widgets = af.getFieldItem(key).widgets;
          PdfDictionary widget = (PdfDictionary) widgets.get(0);
          widget.put(PdfName.FT, new PdfName("Sig"));
          widget.remove(PdfName.V);
          widget.remove(PdfName.DV);
          widget.remove(PdfName.TU);
          widget.remove(PdfName.FF);
          widget.remove(PdfName.DA);
          widget.remove(PdfName.DR);
          widget.remove(PdfName.AP);
        }
      }

      stamperTemp.close();
      documentPDF.close();
      reader = new PdfReader(pdfFile.getPath() + ".temp");

    } else {
      reader = new PdfReader(pdfFile.getPath());

    }

    logger.info("[createPDFReader.retorna]:: ");
    return reader;

  }




  /**
   * 
   * @param hash
   * @param cal
   * @return
   */
  private ASN1EncodableVector buildSignedAttributes(byte[] hash, Calendar cal) {

    logger.info("[buildSignedAttributes.in]:: " + Arrays.asList(new Object[] { hash, cal }));

    ASN1EncodableVector signedAttributes = new ASN1EncodableVector();

    // Content type
    ASN1EncodableVector v = new ASN1EncodableVector();
    v.add(new DERObjectIdentifier("1.2.840.113549.1.9.3"));// CONTENT_TYPE
    v.add(new DERSet(new DERObjectIdentifier("1.2.840.113549.1.7.1")));// PKCS7_DATA
    signedAttributes.add(new DERSequence(v));

    if (cal != null) {
      logger.debug("[buildSignedAttributes]:: cal: " + cal);
      // signing time
      v = new ASN1EncodableVector();
      v.add(new DERObjectIdentifier("1.2.840.113549.1.9.5")); // SIGNING_TIME
      v.add(new DERSet(new DERUTCTime(cal.getTime())));
      signedAttributes.add(new DERSequence(v));
    }

    // message digest
    v = new ASN1EncodableVector();
    v.add(new DERObjectIdentifier("1.2.840.113549.1.9.4"));// MESSAGE_DIGEST
    v.add(new DERSet(new DEROctetString(hash)));
    signedAttributes.add(new DERSequence(v));

    return signedAttributes;

  }



  /**
   * Builds the unauthenticated attributes (currently the timestamp only, from
   * opensignature )
   * 
   * 
   * @param hash
   * @return
   * @throws IOException
   */
  private ASN1EncodableVector buildUnsignedAttributes(byte[] hash, URL serverTimestamp, String tsUser, String tsPassword) throws IOException {

    byte[] respBytes = null;

    TimeStampClient tsc = new TimeStampClient();

    try {

      if (serverTimestamp.getPort() > 0) {
        respBytes = tsc.getTSResponse(hash, serverTimestamp);
      }

    } catch (UnknownHostException e) {
      // -- We'll retry in other way

    } catch (Throwable tr) {
      logger.warn("[buildUnsignedAttributes]:: " + tr.getMessage(), tr);
    }


    if (respBytes == null) {

      if ((tsUser != null && !"".equals(tsUser)) && (tsPassword != null && !"".equals(tsPassword))) {
        respBytes = tsc.getAuthenticatedHttpTSResponse(hash, serverTimestamp, tsUser, tsPassword);
      } else {
        respBytes = tsc.getHttpTSResponse(hash, serverTimestamp);
      }

    }

    if (respBytes == null) {
      return null;
    }

    ASN1InputStream tempstream = new ASN1InputStream(new ByteArrayInputStream(respBytes));
    ASN1EncodableVector unsignedAttributes = new ASN1EncodableVector();

    // time Stamp token : id-aa-timeStampToken da RFC3161, alias old
    // id-smime-aa-timeStampToken
    ASN1EncodableVector v = new ASN1EncodableVector();
    v.add(new DERObjectIdentifier("1.2.840.113549.1.9.16.2.14")); // id-aa-timeStampToken

    ASN1Sequence seq = (ASN1Sequence) tempstream.readObject();
    DERObject timeStampToken = (DERObject) seq.getObjectAt(1);
    v.add(new DERSet(timeStampToken));

    unsignedAttributes.add(new DERSequence(v));

    return unsignedAttributes;

  }


}
