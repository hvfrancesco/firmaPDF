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
package org.opensignature.opensignpdf.tools;


import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.Object;
import iaik.pkcs.pkcs11.objects.PrivateKey;
import iaik.pkcs.pkcs11.objects.RSAPrivateKey;
import iaik.pkcs.pkcs11.objects.X509PublicKeyCertificate;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opensignature.opensignpdf.exceptions.OpenSignatureException;



/**
 * Utility to group the utility methods related to PKCS11 operations.
 * 
 * @author <a href="mailto:japaricio@accv.es">Javier Aparicio</a>
 * 
 */
public class PKCS11Util {


  /**
   * Class Logger
   */
  private static Logger logger = Logger.getLogger(PKCS11Util.class);


  public static final String NON_REPUDIATION = "nonRepudiation";
  public static final String DIGITAL_SIGNATURE = "digitalSignature";




  /**
   * Obtains the first certificate wich have the minimal requisites to be able to sign.
   * 
   * @param map
   * @return
   * @throws OpenSignatureException
   */
  public static X509PublicKeyCertificate getFirstSignCertificate(Map map) throws OpenSignatureException {

    logger.info("[getFirstSignCertificate.in]:: ");

    X509PublicKeyCertificate certObject = null;

    if (map == null) {
      throw new OpenSignatureException("There aren't certificates in the smartcard.");
    }

    List lNonRepudiation = (List) map.get(PKCS11Util.NON_REPUDIATION);
    if (!lNonRepudiation.isEmpty()) {
      certObject = (X509PublicKeyCertificate) lNonRepudiation.get(0);
    }

    if (certObject == null) {
      List lDigitalSignature = (List) map.get(PKCS11Util.DIGITAL_SIGNATURE);
      if (!lDigitalSignature.isEmpty()) {
        certObject = (X509PublicKeyCertificate) lDigitalSignature.get(0);
      }
    }

    logger.info("[getFirstSignCertificate.out]:: ");
    return certObject;

  }


  /**
   * Obtains a map with some lists of kinds of certificates classified by key
   * usage.
   * 
   * @param session
   * @throws TokenException
   * @throws IOException
   * @throws CertificateException
   */
  public static Map getCertificateMap(Session session) throws TokenException, IOException, CertificateException {

    logger.info("[getCertificateMap.in]:: ");

    Map map = new HashMap();

    List alDigitalSignature = new ArrayList();
    List alNonRepudiation = new ArrayList();

    //-- Constructing the search template
    X509PublicKeyCertificate template = new X509PublicKeyCertificate();
    
    //-- Searching certificates
    session.findObjectsInit(template);
    Object[] objects = session.findObjects(1);

    while (objects.length > 0) {
      Object object = objects[0];

      byte[] encodedCertificate = ((X509PublicKeyCertificate) object).getValue().getByteArrayValue();
      X509Certificate x509certificate = CertUtil.toX509Certificate(encodedCertificate);
      boolean[] KeyUsage = x509certificate.getKeyUsage();

      /**XXX: To review those .clone() invocations */
      if (KeyUsage[0]) {
        alDigitalSignature.add(object.clone());
      }
      if (KeyUsage[1]) {
        alNonRepudiation.add(object.clone());
      }

      objects = session.findObjects(1);
    }
    session.findObjectsFinal();

    map.put(DIGITAL_SIGNATURE, alDigitalSignature);
    map.put(NON_REPUDIATION, alNonRepudiation);

    logger.info("[getCertificateMap.out]:: ");
    return map;

  }




  /**
   * Obtains the PrivateKey reference in a smartcard for a given certificate in
   * the smartcard.
   * 
   * @param session
   * @param certObject
   * @return
   * @throws TokenException
   * @throws OpenSignatureException
   */
  public static PrivateKey findCertificatePrivateKey(Session session, X509PublicKeyCertificate certObject) throws TokenException, OpenSignatureException {

    logger.info("[findCertificatePrivateKey.in]:: ");

    PrivateKey signatureKeyObject = null;

    // -- Constructing the search template
    RSAPrivateKey template = new RSAPrivateKey();
    template.getId().setByteArrayValue(certObject.getId().getByteArrayValue());

    // -- Searching certificates
    session.findObjectsInit(template);
    Object[] objects = session.findObjects(1);

    if (objects == null || objects.length <= 0) {
      throw new OpenSignatureException("There are no valid signing certificates in the smartcard.");

    } else if (objects.length == 1) {
      signatureKeyObject = (PrivateKey) objects[0];

    } else {
      logger.debug("[findCertificatePrivateKey]:: There are more than one valid certificate for signing.");
      signatureKeyObject = (PrivateKey) objects[0];

    }

    // -- Terminating the sarch session
    session.findObjectsFinal();

    logger.info("[findCertificatePrivateKey.out]:: ");
    return signatureKeyObject;

  }



}
