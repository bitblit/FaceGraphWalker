package com.budandvine.graphwalker;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;


/**
 * A class for simplifying encrypting/encoding with a given key.
 * 
 * @author cweiss
 * 
 */
public class SimpleEncryptor
{
    private static final Logger    LOG    = LoggerFactory.getLogger(SimpleEncryptor.class);
    private static final String CIPHER = "AES";                                   // TripleDES
    private byte[]              encryptingKey;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    
    public SimpleEncryptor( String encryptingKeyAsString )
    {
        super();
        if( StringUtils.trimToNull(encryptingKeyAsString) == null || encryptingKeyAsString.length() != 16 )
        {
            throw new IllegalArgumentException("Key must be 16 bytes long");
        }

        this.encryptingKey = encryptingKeyAsString.getBytes(UTF8);
    }

    public String encodeToString( String clearText )
    {
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(encryptingKey, CIPHER);
            Cipher nCipher = Cipher.getInstance(CIPHER);
            nCipher.init(Cipher.ENCRYPT_MODE, keySpec);

            return new String(Hex.encode(nCipher.doFinal(clearText.getBytes(UTF8))));
        } catch( Exception e )
        {
            throw new IllegalStateException("Shouldnt happen, error on encode: (key size was " + encryptingKey.length + " and msg size was " + clearText.length() + ") " + e, e);
        }
    }

    public String decodeFromString( String hexEncryptedText )
    {
        try
        {
            byte[] encryptedText = Hex.decode(hexEncryptedText);

            SecretKeySpec keySpec = new SecretKeySpec(encryptingKey, CIPHER);
            Cipher nCipher = Cipher.getInstance(CIPHER);
            nCipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] outputData = nCipher.doFinal(encryptedText);

            return new String(outputData, UTF8);

        } catch( Exception e )
        {
            throw new IllegalStateException("Error decrypting:" + e, e);
        }
    }

    public byte[] encode( byte[] data )
    {
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(encryptingKey, CIPHER);
            Cipher nCipher = Cipher.getInstance(CIPHER);
            nCipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] rval = nCipher.doFinal(data);
            LOG.debug("Encrypted " + data.length + " bytes to " + rval.length + " bytes");
            return rval;
        } catch( Exception e )
        {
            throw new IllegalStateException("Shouldnt happen, error on encode: (key size was " + encryptingKey.length + " and msg size was " + data.length + ") " + e, e);
        }
    }

    public byte[] decode( byte[] encryptedText )
    {
        try
        {

            SecretKeySpec keySpec = new SecretKeySpec(encryptingKey, CIPHER);
            Cipher nCipher = Cipher.getInstance(CIPHER);
            nCipher.init(Cipher.DECRYPT_MODE, keySpec);

            return nCipher.doFinal(encryptedText);
        } catch( Exception e )
        {
            throw new IllegalStateException("Error decrypting:" + e, e);
        }
    }

}
