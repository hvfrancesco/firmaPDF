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
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.opensignature.opensignpdf.tools.Base64Utils;
import org.apache.log4j.Logger;



/**
 * Utility class to embrace the timestamping process.
 * 
 * @author <a href="mailto:japaricio@accv.es">Javier Aparicio</a>
 * 
 */
public class TimeStampClient {



  /**
   * Class Loggger
   */
  private static Logger logger = Logger.getLogger(TimeStampClient.class);


  /** Flag to activate extra class debug */
  public static boolean DEBUG = true;



  /**
   * Default builder
   */
  public TimeStampClient() {
  }




  /**
   * 
   * @param hash
   * @param serverTimestamp
   * @return
   * @throws IOException
   * @throws UnknownHostException
   */
  public byte[] getTSResponse(byte[] hash, URL serverTimestamp) throws UnknownHostException, IOException {

    logger.info("[getTSResponse.in]:: " + Arrays.asList(new Object[] { hash, serverTimestamp }));

    byte[] respBytes = null;


    logger.debug("[getTSResponse]:: Connecting to Timestamping Server '" + serverTimestamp + "' ...");
    Socket openHomeConnection = new Socket(serverTimestamp.toString(), serverTimestamp.getPort());

    try {

      byte[] buf = new byte[] { 0, 0, 0, 0x2c, 0, 0x30, 0x29, 2, 1, 1, 0x30, 0x21, 0x30, 9, 6, 5, 0x2b, 0x0e, 3, 2, 0x1a, 5, 0, 4, 0x14, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, -1 };

      if (DEBUG) {
        logger.debug("[getTSResponse]:: Hash to transmit: ");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 20; i++) {
          sb.append(Integer.toHexString(hash[i]));
          sb.append(" : ");
        }
        logger.debug(sb);
      }

      System.arraycopy(hash, 0, buf, 25, 20);

      OutputStream out = new BufferedOutputStream(openHomeConnection.getOutputStream());
      out.write(buf);
      out.flush();
      out.close();
      openHomeConnection.shutdownOutput();

      byte[] buffer = new byte[1024];
      InputStream is = new BufferedInputStream(openHomeConnection.getInputStream());
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int bytesRead = 0;
      while ((bytesRead = is.read(buffer, 0, buffer.length)) >= 0) {
        baos.write(buffer, 0, bytesRead);
      }

      baos.flush();
      byte[] respBytes2 = baos.toByteArray();
      baos.close();


      byte[] respBytes3 = new byte[respBytes2.length - 5];
      System.arraycopy(respBytes2, 5, respBytes3, 0, respBytes3.length);


      respBytes = respBytes3;
      logger.debug("[getTSResponse]:: got " + respBytes.length + " bytes.");

      if (DEBUG) {
        if (respBytes.length < 500) {
          for (int i = 0; i < respBytes.length; i++) {
            logger.debug("[getTSResponse]:: " + (char) respBytes[i]);
          }
        }
      }

      out.close();
      is.close();
      openHomeConnection.close();

    } catch (IOException e) {
      logger.error("[getTSResponse]:: Error getting the TS response.", e);
    }

    logger.info("[getTSResponse.out]:: ");
    return respBytes;

  }




  /**
   * 
   * @param hash
   * @param serverTimestamp
   * @return
   */
  public byte[] getHttpTSResponse(byte[] hash, URL serverTimestamp) {

    logger.info("[getHttpTSResponse.in]:: " + Arrays.asList(new Object[] { hash, serverTimestamp }));

    byte[] respBytes = null;

    logger.debug("Connecting to Timestamping Server: '" + serverTimestamp + "' ...");

    byte[] buf = new byte[] { 0x30, 0x29, 2, 1, 1, 0x30, 0x21, 0x30, 9, 6, 5, 0x2b, 0x0e, 3, 2, 0x1a, 5, 0, 4, 0x14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                             0, 0, 0, 0, 0, 0, 1, 1, -1 };

    if (DEBUG) {
      logger.debug("Hash to transmit: ");
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < 20; i++) {
        sb.append(Integer.toHexString(hash[i]));
        sb.append(" : ");
      }
      logger.debug(sb);
    }

    System.arraycopy(hash, 0, buf, 20, 20);

    Hashtable reqProperties = new Hashtable();
    reqProperties.put("Content-Type", "application/timestamp-query");
    reqProperties.put("Content-Length", String.valueOf(buf.length));

    respBytes = rawHttpPost(serverTimestamp, reqProperties, buf);

    logger.debug("got " + respBytes.length + " bytes.");

    if (DEBUG) {
      if (respBytes.length < 500) {
        StringBuffer sbTmp = new StringBuffer();
        for (int i = 0; i < respBytes.length; i++) {
          sbTmp.append(respBytes[i]);
        }
        logger.debug(sbTmp.toString());
      }
    }

    return respBytes;

  }




  /**
   * 
   * @param hash
   * @param serverTimestamp
   * @param user
   * @param password
   * @return
   */
  public byte[] getAuthenticatedHttpTSResponse(byte[] hash, URL serverTimestamp, String user, String password) {

    logger.info("[getAuthenticatedHttpTSResponse.in]:: " + Arrays.asList(new Object[] { hash, serverTimestamp, user, "<password>" }));

    byte[] respBytes = null;

    logger.debug("Connecting to Timestamping Server ...");

    byte[] buf = new byte[] { 0x30, 0x27, 2, 1, 1, 0x30, 0x1f, 0x30, 7, 6, 5, 0x2b, 0x0e, 3, 2, 0x1a, 4, 0x14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                             0, 0, 0, 0, 1, 1, -1 };

    if (DEBUG) {
      logger.debug("Hash to transmit: ");
      StringBuffer sbTmp = new StringBuffer();
      for (int i = 0; i < 20; i++) {
        sbTmp.append(Integer.toHexString(hash[i]));
        sbTmp.append(" : ");
      }
      logger.debug(sbTmp.toString());
    }

    System.arraycopy(hash, 0, buf, 18, 20);

    Hashtable reqProperties = new Hashtable();

    String userPassword = user + ":" + password;
    reqProperties.put("Authorization", "Basic " + new String(Base64Utils.base64Encode(userPassword.getBytes())));

    reqProperties.put("Content-Type", "application/timestamp-query");
    reqProperties.put("Content-Transfer-Encoding", "base64");

    String reqString = new String(Base64Utils.base64Encode(buf));

    reqProperties.put("Content-Length", String.valueOf(reqString.length()));

    respBytes = rawHttpPost(serverTimestamp, reqProperties, reqString.getBytes());

    logger.debug("got " + respBytes.length + " bytes.");

    if (DEBUG) {
      if (respBytes.length < 500) {
        StringBuffer sbTmp = new StringBuffer();
        for (int i = 0; i < respBytes.length; i++) {
          sbTmp.append(respBytes[i]);
        }
        logger.debug(sbTmp.toString());
      }
    }

    return respBytes;

  }



  /**
   * TODO: - MUST BE COMMENTED!! - Could be a good idea to use Apache
   * HttpClient?
   * 
   * 
   * 
   * @param serverTimeStamp
   * @param reqProperties
   * @param postData
   * @return
   */
  private byte[] rawHttpPost(URL serverTimeStamp, Hashtable reqProperties, byte[] postData) {

    logger.info("[rawHttpPost.in]:: " + Arrays.asList(new Object[] { serverTimeStamp, reqProperties, postData }));

    URLConnection urlConn;
    DataOutputStream printout;
    DataInputStream input;

    byte[] responseBody = null;

    try {

      // URL connection channel.
      urlConn = serverTimeStamp.openConnection();

      // Let the run-time system (RTS) know that we want input.
      urlConn.setDoInput(true);

      // Let the RTS know that we want to do output.
      urlConn.setDoOutput(true);

      // No caching, we want the real thing.
      urlConn.setUseCaches(false);

      // Set request properties
      Iterator iter = reqProperties.entrySet().iterator();

      while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry) iter.next();

        urlConn.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
      }


      logger.debug("POSTing to: " + serverTimeStamp + " ...");

      // Send POST output.


      printout = new DataOutputStream(urlConn.getOutputStream());

      printout.write(postData);
      printout.flush();
      printout.close();


      // Get response data.
      input = new DataInputStream(urlConn.getInputStream());

      byte[] buffer = new byte[1024];
      int bytesRead = 0;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
        baos.write(buffer, 0, bytesRead);
      }

      input.close();

      responseBody = baos.toByteArray();

    } catch (MalformedURLException me) {
      logger.warn("[rawHttpPost]:: ", me);

    } catch (IOException ioe) {
      logger.warn("[rawHttpPost]:: ", ioe);
    }

    return responseBody;
  }


  /**
   * TODO: Is it in use? If the answer is not, we can remove it.
   * 
   * @param hash
   * @return
   * @deprecated
   */
  public byte[] getOpensignatureTSResponse(byte[] hash) {

    String reqString = "%30%27%02%01%01%30%1f%30%07%06%05%2b%0e%03%02%1a%04%14";

    char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    for (int i = 0; i < 20; i++) {
      char hi_nib = hex[(hash[i] & 0xF0) >> 4];
      char lo_nib = hex[(hash[i] & 0x0F)];
      reqString = reqString + "%" + hi_nib + lo_nib;
    }

    reqString = reqString + "%01%01%FF";

    byte[] respBytes = null;

    try {
      logger.debug("Connecting to Opensignature ...");

      URL openHome = new URL("http://opensignature.sourceforge.net/cgi-bin/ts.pl?tsq=" + reqString);
      URLConnection openHomeConnection = openHome.openConnection();

      InputStream fis = openHomeConnection.getInputStream();
      byte[] buffer = new byte[1024];
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int bytesRead = 0;
      while ((bytesRead = fis.read(buffer, 0, buffer.length)) >= 0) {
        baos.write(buffer, 0, bytesRead);
      }
      respBytes = baos.toByteArray();

      logger.debug("got " + respBytes.length + " bytes.");

    } catch (MalformedURLException e) {
      logger.debug("Malformed URL Exception " + e);

    } catch (IOException e) {
      logger.debug("IO Exception " + e);
    }

    return respBytes;
  }


}
