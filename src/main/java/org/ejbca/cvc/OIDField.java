/*************************************************************************
 *                                                                       *
 *  CERT-CVC: EAC 1.11 Card Verifiable Certificate Library               * 
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.ejbca.cvc;

import java.util.ArrayList;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.OIDTokenizer;

/**
 * Klassen representerar Object Identifier
 * 
 * @author Keijo Kurkinen, Swedish National Police Board
 * @version $Id$
 *
 */
public class OIDField
      extends AbstractDataField {

   private String id;

   OIDField() {
      super(CVCTagEnum.OID);
   }

   /**
    * Konstruktor som tag id-v�rdet som str�ng
    * @param id
    */
   OIDField(String id) {
      this();
      this.id = id;
   }

   /**
    * Konstruktor f�r att avkoda byte-data
    * @param data
    */
   OIDField(byte[] data) {
      this();
      this.id = DERObjectIdentifier.getInstance(new DEROctetString(data)).getId();
   }

   public String getValue() {
      return id;
   }

  
   @Override
   protected byte[] getEncoded() {
      OIDTokenizer tok = new OIDTokenizer(id);

      ArrayList<byte[]> byteArray = new ArrayList<byte[]>();
      int totalLength = 0;
      
      // Om OID best�r av integers i1, i2, ..., iN
      // F�rsta byten i DER-arrayen kodas som 40*i1 + i2, d�refter i3, ..., iN
      byte[] tmpArr = toByteArray(
         Integer.parseInt(tok.nextToken()) * 40 + 
         Integer.parseInt(tok.nextToken()) );
      byteArray.add(tmpArr);
      totalLength += tmpArr.length;

      while (tok.hasMoreTokens()) {
         tmpArr = toByteArray(Long.parseLong(tok.nextToken()));
         byteArray.add(tmpArr);
         totalLength += tmpArr.length;
      }

      // Allokera svarsarray
      byte[] result = new byte[totalLength];
      int pos = 0;
      for( byte[] arr : byteArray ){
         System.arraycopy(arr, 0, result, pos, arr.length);
         pos += arr.length;
      }
      return result;
   }


   @Override
   protected String valueAsText() {
      return id;
   }

   public String toString() {
      return getValue();
   }

   @Override
   /**
    * Returnerar true om id-str�ngarna �r lika
    */
   public boolean equals(Object other){
      if( other instanceof OIDField ){
         return id.equals(((OIDField)other).getValue());
      }
      else {
         return false;
      }
   }
}