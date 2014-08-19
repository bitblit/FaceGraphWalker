package com.budandvine.graphwalker;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * A factory bean that can read a properties file from S3 or local locations.
 *
 * If the file is in S3, the location field should be set as follows:
 * s3:{bucket-name}/{path}
 *
 * If the location doesn't match this pattern, it will be treated as a standard spring
 * location (eg, classpath:{}, http:{}, file{} urls should still work)
 *
 * If you are using an S3 location, you MUST set the s3Client property, and the client must
 * have the authority to read the file in question.
 *
 * If you set a decryption key, the file will be decrypted using AES-256 encryption using that
 * key.  If the decryption key is not set, no decryption will be performed.
 *
 * <p/>
 * Created by chrweiss on 5/26/14.
 */
public class RemotePropertiesFactory implements FactoryBean<Properties>, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(RemotePropertiesFactory.class);

    private AmazonS3Client s3Client;
    private String location;
    private String decryptionKey;

    @Override
    public Class<?> getObjectType() {
        return Properties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (location == null) {
            throw new IllegalArgumentException("Cannot continue with null location");
        }
        if (s3Location()) {
            if (s3Client == null) {
                throw new IllegalArgumentException("S3 location but client not set");
            }
            if (s3Bucket() == null || s3Path() == null) {
                throw new IllegalArgumentException("S3 location but unparseable path (should be s3:bucket:path");
            }
        }
    }

    @Override
    public Properties getObject() throws BeansException {

        Properties rval = new Properties();
        try {
            if (s3Location()) {
                String bucket = s3Bucket();
                String path = s3Path();

                LOG.info("Loading from s3 bucket {} path {}", bucket, path);
                S3Object sob = s3Client.getObject(bucket, path);
                rval.load(decorateWithDecryptor(sob.getObjectContent()));

            } else {
                LOG.info("Not an S3 location - loading using standard resource loader");
                // Using the local override (typically for dev)
                // Delegate everything to urlresource in those cases
                Resource resource = new UrlResource(location);
                rval.load(decorateWithDecryptor(resource.getInputStream()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Couldn't load properties", e);
        }

        LOG.info("Finished loading properties, found keys: {}",rval.keySet());
        return rval;
    }

    private InputStream decorateWithDecryptor(InputStream is)
            throws IOException {
        InputStream rval = is;
        if (rval != null && decryptionKey != null) {
            LOG.info("Applying decryption layer");
            byte[] bytes = IOUtils.toByteArray(is);
            try {
                SecretKeySpec keySpec = new SecretKeySpec(decryptionKey.getBytes("UTF-8"), "AES");
                Cipher nCipher = Cipher.getInstance("AES");
                nCipher.init(Cipher.DECRYPT_MODE, keySpec);
                byte[] dec = nCipher.doFinal(bytes);
                rval = new ByteArrayInputStream(dec);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Can't happen - AES is java bundled");
            }
            catch (NoSuchPaddingException e) {
                throw new RuntimeException("Can't happen - AES is java bundled");
            }
            catch (IllegalBlockSizeException e) {
                throw new RuntimeException("Can't happen - AES is java bundled");
            }
            catch (BadPaddingException e) {
                throw new RuntimeException("Can't happen - AES is java bundled");
            } catch (InvalidKeyException ivk) {
                throw new IllegalStateException("Illegal key size - this usually means you need to install the unlimited strength JCE", ivk);
            }
        }
        return rval;
    }

    private boolean s3Location() {
        return location != null && location.toLowerCase().startsWith("s3:");
    }

    private String s3Bucket() {
        String rval = null;
        if (s3Location()) {
            int loc = location.indexOf("/", 3);
            if (loc > -1) {
                rval = location.substring(3, loc);
            }
        }
        return rval;
    }

    private String s3Path() {
        String rval = null;
        if (s3Location()) {
            int loc = location.indexOf("/", 3);
            if (loc > -1) {
                rval = location.substring(loc+1);
            }
        }
        return rval;
    }

    public void setS3Client(AmazonS3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void setDecryptionKey(String decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
