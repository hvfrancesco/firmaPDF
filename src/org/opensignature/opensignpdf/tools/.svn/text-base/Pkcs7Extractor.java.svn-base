/*
FirmaPdf version 0.0.2
Copyright (C) 2006 Antonino Iacono (ant_iacono@tin.it) and Roberto Resoli

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
/*
 * $Header: /usr/local/cvsroot/opensignpdf/src/org/opensignature/opensignpdf/tools/Pkcs7Extractor.java,v 1.1 2006/09/25 08:01:25 resolicvs Exp $
 * $Revision: 1.1 $
 * $Date: 2006/09/25 08:01:25 $
 */
package org.opensignature.opensignpdf.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;

public class Pkcs7Extractor {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        try {
            if(args.length < 1){
                System.out.println("Usage: EstraiPkcs7 <pdf file relative to current dir>");
                System.exit(1);
            }
            String filename = args[0];

            PdfReader reader = new PdfReader(filename);
            AcroFields af = reader.getAcroFields();
            ArrayList names = af.getSignatureNames();
            for (int k = 0; k < names.size(); ++k) {
                String name = (String) names.get(k);
                System.out.println("Signature name: " + name);
                System.out.println("Signature covers whole document: "
                        + af.signatureCoversWholeDocument(name));
                System.out.println("Document revision: " + af.getRevision(name)
                        + " of " + af.getTotalRevisions());
                // Start revision extraction
                // FileOutputStream out = new FileOutputStream("revision_" +
                // af.getRevision(name) + ".pdf");
                // byte bb[] = new byte[8192];
                // InputStream ip = af.extractRevision(name);
                // int n = 0;
                // while ((n = ip.read(bb)) > 0)
                // out.write(bb, 0, n);
                // out.close();
                // ip.close();
                // End revision extraction

                // PdfPKCS7 pk = af.verifySignature(name);

                PdfDictionary v = af.getSignatureDictionary(name);

                PdfString contents = (PdfString) PdfReader.getPdfObject(v
                        .get(PdfName.CONTENTS));
                
                // Start pkcs7 extraction
                FileOutputStream fos = new FileOutputStream(filename +"_signeddata_"
                        + name + ".pk7");
                System.out.println(k+") Estrazione pkcs7: " + filename +"_signeddata_"
                        + name + ".pk7");
                fos.write(contents.getOriginalBytes());
                fos.flush();
                fos.close();
                // End pkcs7 extraction
                
                /* Commentato per evitare dipendenze da BC
                Security.insertProviderAt(new BouncyCastleProvider(), 3);
                
                // nota: dipendenza da provider BC per "SHA1withRSA"
                PdfPKCS7 pk = new PdfPKCS7(contents.getOriginalBytes(), "BC");

                

                Calendar cal = pk.getSignDate();
                Certificate pkc[] = pk.getCertificates();
                System.out.println("Got " + pkc.length
                        + " certificates from pdf");
                System.out
                        .println("Subject of signer: "
                                + PdfPKCS7.getSubjectFields(pk
                                        .getSigningCertificate()));
                // System.out.println("Document modified: " + !pk.verify());
                // Object fails[] = PdfPKCS7.verifyCertificates(pkc, kall, null,
                // cal);
                // if (fails == null)
                // System.out.println("Certificates verified against the
                // KeyStore");
                // else
                // System.out.println("Certificate failed: " + fails[1]);
                 
                */
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
       
            
            
        } 
        
        /* decommentare se si riabilita la parte relativa a PdfPKCS7 nel main
        
        catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CRLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */

    }

    
    
}
