package org.eclipse.scout.rt.ldap.util;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;

import javax.net.ssl.X509TrustManager;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class FullVerificationTrustManager implements X509TrustManager {

  private static final XLogger LOG = XLoggerFactory.getXLogger(FullVerificationTrustManager.class);

  private KeyStore ks = null;
  private String ocsp = null;
  private String trustlevel = null;

  public FullVerificationTrustManager(KeyStore ks, String ocsp, String trustlevel) {
    LOG.entry(ks, ocsp, trustlevel);

//		if (ks == null)
//			throw new IllegalArgumentException("TrustStore cannot be null");

    this.ks = ks;
    this.ocsp = ocsp;
    this.trustlevel = trustlevel;

    LOG.exit();
  }

  /**
   * Method called on the server-side for establishing trust with a client.
   */
  @Override
  public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    LOG.entry(chain, authType);
//		LOG.entry();

    Stream.of(chain).forEach(x -> LOG.debug("client cert: {} issued by: {} sn: {}",
        x.getSubjectDN(), x.getIssuerDN(), x.getSerialNumber()));

    // In this example LDAP client is acting as a client, so this method should not be called at all

    LOG.exit();
  }

  /**
   * Method called on the client-side for establishing trust with a server.
   */
  @Override
  public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    LOG.entry(chain, authType);
////		LOG.entry();
//		if (chain != null)
//			Stream.of(chain).forEach(x -> LOG.entry("server cert: {} {}", x.getSubjectDN(), x.getIssuerDN() ) );
//		LOG.entry(authType);

//		Stream.of(chain).forEach(x -> LOG.debug("server cert: {} {}", x.getSubjectDN(), x.getIssuerDN()) );

    for (X509Certificate cert : chain) {
      LOG.debug("server cert: {} issued by: {} sn: {}", cert.getSubjectDN(), cert.getIssuerDN(), cert.getSerialNumber());
      verifyCertificate(cert);
      verifyOCSP(cert);
    }

    LOG.exit();
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    LOG.entry();

    return LOG.exit(null);
  }

  private void verifyCertificate(X509Certificate cert) throws CertificateException {
    LOG.entry((cert != null) ? cert.getSubjectDN() : cert);

    switch (trustlevel) {
      case "0": {
        // TODO
        // do nothing or verify if certificate is technically correct (present, not expired)
        break;
      }

      case "1": {
        // verify if exact certificate match is found in trust store

        if (ks != null) {
          if (!PKIHelper.isCertificatePresent(cert, ks)) {
            String msg = "Certificate not trusted: " + cert.getSubjectDN();
            LOG.warn(msg);
            throw new CertificateException(msg);
          }
          else {
            LOG.debug("Certificate trusted: " + cert.getSubjectDN());
          }

        }
        else {
          String msg = "KeyStore for TrustManager is null";
          LOG.error(msg);
          throw new CertificateException(msg);
        }

        break;
      }

      case "2": {
        // TODO
        // verify if certificate is signed by one of the certificates present in the trust store (ks object) 
        break;
      }

      default: {
        String msg = "Unrecognized trust level mode: " + ocsp;
        LOG.error(msg);
        throw new CertificateException(msg);
      }
    } // switch

    LOG.exit();
  }

  private void verifyOCSP(X509Certificate cert) throws CertificateException {
    LOG.entry((cert != null) ? cert.getSubjectDN() : cert);

    switch (ocsp) {
      case "0": {
        // do nothing
        break;
      }

      case "1": {
        // mandatory check
        if (!verifyOCSPCertificate(cert)) {
          String msg = "OCSP verification failure for certificate: " + cert.getSubjectDN();
          LOG.error(msg);
          throw new CertificateException(msg);
        }
        break;
      }

      case "2": {
        // TODO
        // query cert details for ocsp data, if present -> query ocsp service
        break;
      }

      default: {
        String msg = "Unrecognized ocsp verification mode: " + ocsp;
        LOG.error(msg);
        throw new CertificateException(msg);
      }
    } // switch

    LOG.exit();
  }

  private boolean verifyOCSPCertificate(X509Certificate cert) {
    LOG.entry((cert != null) ? cert.getSubjectDN() : cert);
    boolean result = false;

    //TODO

    result = true;
    return LOG.exit(result);
  }

}
