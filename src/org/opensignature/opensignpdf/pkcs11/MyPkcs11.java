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
package org.opensignature.opensignpdf.pkcs11;


import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Slot;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.Key;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;



public class MyPkcs11 {

  
  /**
   * Class Logger
   */
  private static Logger logger = Logger.getLogger(MyPkcs11.class);
  

  
  
  /**
   * PKCS11 module
   */
  private Module pkcs11Module;
  
  /**
   * Obtains an opened session ready to work with.
   * 
   * @param pkcs11libname
   * @param pin
   * @return
   * @throws TokenException
   * @throws IOException
   */
  public Session initSession(String pkcs11libname, String pin) throws TokenException, IOException {
    
    logger.info("[initSession.in]:: " + Arrays.asList(new Object[] { pkcs11libname, "<pin>" }));
    
    pkcs11Module = Module.getInstance(pkcs11libname);
    pkcs11Module.initialize(null);
    logger.debug("[initSession]:: Module ('" + pkcs11libname + "') initialized.");

    Slot[] slots = pkcs11Module.getSlotList(Module.SlotRequirement.TOKEN_PRESENT);
    logger.debug("[initSession]:: Active slots: " + slots.length);
    
    Token token = null;
    if (slots.length == 0) {
      throw new TokenException("There aren't present tokens at any slot.");
    } else {
      token = slots[0].getToken();
    }

    logger.debug("[initSession]:: Trying to open a session...");
    Session session = token.openSession(Token.SessionType.SERIAL_SESSION, Token.SessionReadWriteBehavior.RO_SESSION, null, null);
    session.login(Session.UserType.USER, pin.toCharArray());
    
    logger.info("[initSession.out]:: ");
    return session;
  }


  
  /**
   * 
   * @param algOid
   * @param digest
   * @return
   * @throws IOException
   */
  public byte[] getSignData(ObjectIdentifier algOid, byte[] digest) throws IOException {
    
    logger.info("[getSignData.in]:: " + Arrays.asList(new Object[] { algOid, digest }));
    
    DerOutputStream out = new DerOutputStream();

    DerOutputStream algS = new DerOutputStream();

    DerOutputStream oidS = new DerOutputStream();
    oidS.putOID(algOid);

    DerOutputStream nullS = new DerOutputStream();
    nullS.putNull();

    algS.putSequence(new DerValue[] { new DerValue(oidS.toByteArray()), new DerValue(nullS.toByteArray()) });

    DerOutputStream digestS = new DerOutputStream();
    digestS.putOctetString(digest);
    
    DerValue[] dv = new DerValue[] { new DerValue(algS.toByteArray()), new DerValue(digestS.toByteArray()) };
    out.putSequence(dv);
    
    logger.info("[getSignData.out]:: ");
    return out.toByteArray();
    
  }


  
  /**
   * Performs the sign operation.
   *  
   * @param session
   * @param digest
   * @param signKey
   * @return
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws TokenException
   */
  public byte[] sign(Session session, byte[] digest, Key signKey) throws NoSuchAlgorithmException, IOException, TokenException {

    logger.info("[sign.in]:: ");
    
    session.signInit(Mechanism.RSA_PKCS, signKey);
    //SHA-1 Digest Info
    //byte[] abSignature = session.sign(getSignData(new ObjectIdentifier("1.3.14.3.2.26"), digest));
    //SHA-256 Digest Info
    byte[] abSignature = session.sign(getSignData(new ObjectIdentifier("2.16.840.1.101.3.4.2.1"), digest));
    
    logger.info("[sign.out]:: ");
    return abSignature;
    
  }



  /**
   * Unload the pkcs11 module
   * 
   * @throws TokenException
   */
  public void finalizeModule() throws TokenException {
    pkcs11Module.finalize(null);
  }
  
  
}
