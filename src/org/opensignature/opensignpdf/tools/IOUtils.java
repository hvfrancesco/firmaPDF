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


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.lowagie.bc.asn1.ASN1Encodable;
import com.lowagie.bc.asn1.ASN1InputStream;
import com.lowagie.bc.asn1.ASN1OutputStream;
import com.lowagie.bc.asn1.DEREncodable;
import com.lowagie.bc.asn1.DERObject;



/**
 * Utility class with useful conversions and methods. 
 * 
 * @author <a href="mailto:japaricio@accv.es">Javier Aparicio</a>
 * 
 */
public class IOUtils {
  
  
  
  /**
   * Class Logger
   */
  private static Logger logger = Logger.getLogger(IOUtils.class);
  
  



  /**
   * Conversion method
   * 
   * @deprecated Use {@link #toByteArray(ASN1Encodable)} instead
   */
  public static byte[] getDerSequenceBytes(ASN1Encodable asnEncObject) throws IOException {
    return toByteArray(asnEncObject);
  }





  /**
   * Conversion method
   * 
   * @deprecated Use {@link #toByteArray(DEREncodable)} instead
   */
  public static byte[] asn1ToByteArray(DEREncodable derEncObject) throws IOException {
    return toByteArray(derEncObject);
  }





  /**
   * Dumps the content of the in memory Object to a byte[].
   * 
   * @param derEncObject
   * @return
   * @throws IOException
   */
  public static byte[] toByteArray(DEREncodable derEncObject) throws IOException {

    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    BufferedOutputStream bos = new BufferedOutputStream(bOut);

    ASN1OutputStream dout = new ASN1OutputStream(bos);
    dout.writeObject(derEncObject);
    dout.close();
    
    return bOut.toByteArray();
    
  }
  
  
  /**
   * Reads an DERObject from a byte[].
   * 
   * @param ab
   * @return
   * @throws IOException
   */
  public static DERObject readDERObject( byte[] ab ) throws IOException{
    
    ASN1InputStream in = IOUtils.getASN1InputStream(ab);
    DERObject obj = in.readObject();
    
    return obj;
  }
  
  
  
  /**
   * Gets an ASN1Stream from a byte[].
   * 
   * @param ab
   * @return
   */
  private static ASN1InputStream getASN1InputStream( byte[] ab ) {
    
    ByteArrayInputStream bais = new ByteArrayInputStream(ab);
    BufferedInputStream bis = new BufferedInputStream(bais);
    
    ASN1InputStream asn1is = new ASN1InputStream(bis);
    
    return asn1is;
    
  }
  
  


  /**
   * Dumps the stream content into a byte[]
   * 
   * @param stream
   * @return
   * @throws IOException
   */
  public static byte[] streamToByteArray(InputStream stream) throws IOException {
    
    if (stream == null) {
      return null;
      
    } else {
      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
      byte buffer[] = new byte[1024];
      int c = 0;
      while ((c = stream.read(buffer)) > 0) {
        byteArray.write(buffer, 0, c);
      }
      byteArray.flush();
      return byteArray.toByteArray();
    }
    
  }


  
  /**
   * Reads the full content of the file to a byte[]
   * 
   * @param filePath
   * @return
   * @throws IOException
   */
  public static byte[] readBytesFromFile(String filePath) throws IOException {

    logger.info("[readBytesFromFile.in]:: Reading Cert from file: " + filePath);

    FileInputStream fis = new FileInputStream(filePath);

    byte[] buffer = new byte[1024];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int bytesRead = 0;
    while ((bytesRead = fis.read(buffer, 0, buffer.length)) >= 0) {
      baos.write(buffer, 0, bytesRead);
    }
    fis.close();

    return baos.toByteArray();
    
  }

}
