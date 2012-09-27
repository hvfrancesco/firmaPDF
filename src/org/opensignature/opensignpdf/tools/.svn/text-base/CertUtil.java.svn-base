/*
 * FirmaPdf version 0.0.2 Copyright (C) 2006 Antonino Iacono (ant_iacono@tin.it)
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
/*
 * $Header:
 * /usr/local/cvsroot/opensignpdf/src/org/opensignature/opensignpdf/MyPkcs11.java,v
 * 1.1 2006/05/25 12:19:13 resolicvs Exp $ $Revision: 1.1 $ $Date: 2006/05/25
 * 12:19:13 $
 */
package org.opensignature.opensignpdf.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import com.lowagie.bc.asn1.ASN1Sequence;
import com.lowagie.bc.asn1.DERObject;
import com.lowagie.bc.asn1.DERTaggedObject;




/**
 * Utility class to handle the common operations with certificates.
 * 
 * @author <a href="mailto:japaricio@accv.es">Javier Aparicio</a>
 * 
 */
public class CertUtil {
  
  
  
  /**
   * Obtains the Issuer for the X509Certificate.
   *  
   * @param cert
   * @return
   * @throws CertificateEncodingException
   * @throws IOException
   */
  public static DERObject getIssuer(X509Certificate cert) throws CertificateEncodingException, IOException {
    
    byte[] abTBSCertificate = cert.getTBSCertificate();
    ASN1Sequence seq = (ASN1Sequence) IOUtils.readDERObject(abTBSCertificate);
    return (DERObject) seq.getObjectAt(seq.getObjectAt(0) instanceof DERTaggedObject ? 3 : 2);
    
  }
  
  
  
  /**
   * Builds the X509Certificate for the byte[].
   * 
   * @param encodedCertificate
   * @return
   * @throws IOException
   * @throws CertificateException
   */
  public static X509Certificate toX509Certificate( byte[] encodedCertificate ) throws IOException, CertificateException{
    
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    ByteArrayInputStream bais = new ByteArrayInputStream(encodedCertificate);
    Certificate certificate = cf.generateCertificate( new BufferedInputStream(bais) );
    bais.close();

    return (X509Certificate) certificate;
    
  }
  

}
