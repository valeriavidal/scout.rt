package org.eclipse.scout.rt.ldap.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.stream.Stream;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class PKIHelper {

  private static final XLogger LOG = XLoggerFactory.getXLogger(PKIHelper.class);

  private static PKIHelper instance = null;

  public static PKIHelper getInstance() {
    LOG.entry();

    if (instance == null) {
      instance = new PKIHelper();
    }

    return LOG.exit(instance);
  }

  public PKIHelper() {
    LOG.entry();

    LOG.exit();
  }

  public static KeyManager[] prepareKeyManagers(KeyStore ks, String password, String cert_label, String cert_password) {
    LOG.entry(ks, "***", cert_label, "***");
    KeyManager[] result = null;

    try {
      // prepare cert_password
      LOG.debug("preparing password");
      char[] cert_password_char = null;
      if (cert_password != null) {
        cert_password_char = cert_password.toCharArray();
      }
      else {
        cert_password_char = new char[0];
      }

      // get certificate key and chain
      LOG.debug("getting key and chain");
      if (ks.getCertificate(cert_label) == null) {
        throw new UnrecoverableKeyException("No certificate found with alias: " + cert_label);
      }
      Certificate[] cert_chain = ks.getCertificateChain(cert_label);
      Key cert_priv_key = ks.getKey(cert_label, cert_password_char);

      Stream.of(cert_chain).forEach(x -> LOG.debug("cert: {}", ((X509Certificate) x).getSubjectDN()));
      LOG.debug("priv key alg: {} format: {}", cert_priv_key.getAlgorithm(), cert_priv_key.getFormat());

      // create new KeyStore and add extracted certificate and key
      LOG.debug("creating new keystore");
      KeyStore newKeystore = getKeystore(ks.getType(), ks.getProvider().getName(), null, password);
      LOG.debug("adding cert to new keystore");
      newKeystore.setKeyEntry(cert_label, cert_priv_key, cert_password_char, cert_chain);

      // prepare KeyManagers
      LOG.debug("getting default KeyManagerFactory algorithm");
      String defaultAlg = KeyManagerFactory.getDefaultAlgorithm();
      LOG.debug("default KeyManagerFactory algorithm: {}", defaultAlg);
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(defaultAlg);
//			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");

      keyManagerFactory.init(newKeystore, cert_password_char);
      result = keyManagerFactory.getKeyManagers();

    }
    catch (KeyStoreException e) {
      String msg = "Error preparing KeyManagers";
      LOG.error(msg, e);

    }
    catch (UnrecoverableKeyException e) {
      String msg = "Error preparing KeyManagers";
      LOG.error(msg, e);

    }
    catch (NoSuchAlgorithmException e) {
      String msg = "Error preparing KeyManagers";
      LOG.error(msg, e);
    }

    return LOG.exit(result);
  }

  public static KeyStore getKeystore(String type, String provider, String location, String password) {
    LOG.entry(type, provider, location, "***");
    KeyStore result = null;

    try {
      KeyStore ks = null;
      if (provider == null) {
        // use default provider
        ks = KeyStore.getInstance(type);

      }
      else {
        // use specified provider
        ks = KeyStore.getInstance(type, provider);
      }

      if (location != null) {
        // we will load keystore from file
        Path path = Paths.get(location).normalize().toAbsolutePath();
        LOG.debug("keystore absolute normalized path: {}", path.toString());

        try (InputStream readStream = Files.newInputStream(path, StandardOpenOption.READ)) {
          if (password != null) {
            ks.load(readStream, password.toCharArray());

          }
          else {
            ks.load(readStream, new char[0]);
          }
        }
        LOG.debug("keystore successfully opened");

      }
      else {
        // we will create new keystore
        if (password != null) {
          ks.load(null, password.toCharArray());

        }
        else {
          ks.load(null, null);
        }
        LOG.debug("keystore successfully created");
      }

      result = ks;

    }
    catch (KeyStoreException e) {
      String msg = "Cannot open keystore: " + location;
      LOG.error(msg, e);

    }
    catch (NoSuchProviderException e) {
      String msg = "Cannot open keystore: " + location;
      LOG.error(msg, e);

    }
    catch (IOException e) {
      String msg = "Cannot open keystore: " + location;
      LOG.error(msg, e);

    }
    catch (NoSuchAlgorithmException e) {
      String msg = "Cannot open keystore: " + location;
      LOG.error(msg, e);

    }
    catch (CertificateException e) {
      String msg = "Cannot open keystore: " + location;
      LOG.error(msg, e);
    }

    return LOG.exit(result);
  }

  public static boolean isCertificatePresent(X509Certificate cert, KeyStore ks) {
    LOG.entry((cert != null) ? cert.getSubjectDN() : cert, ks);
    boolean result = false;

    if (ks != null && cert != null)
      try {
        Enumeration<String> aliases = ks.aliases();

        while (aliases.hasMoreElements()) {
          String alias = aliases.nextElement();

          if (ks.isCertificateEntry(alias)) {
            X509Certificate tmpCert = (X509Certificate) ks.getCertificate(alias);
            LOG.debug("tmpCert: {} signed by: {} sn: {}", tmpCert.getSubjectDN().toString(), tmpCert.getIssuerDN(), tmpCert.getSerialNumber());
            if (cert.equals(tmpCert)) {
              LOG.debug("found equal certificate");
              result = true;
              break;
            }
          } // if

        } // while

      }
      catch (KeyStoreException e) {
        LOG.error("Problems", e);
      }

    return LOG.exit(result);
  }

}
