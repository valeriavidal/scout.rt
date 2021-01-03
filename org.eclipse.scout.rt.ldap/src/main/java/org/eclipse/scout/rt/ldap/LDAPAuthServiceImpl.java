package org.eclipse.scout.rt.ldap;

import org.eclipse.scout.rt.ldap.security.AuthenticateLDAPPermission;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.security.ICredentialVerifier;
import org.eclipse.scout.rt.security.ACCESS;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class LDAPAuthServiceImpl implements ILDAPAuthService {

  private static final XLogger LOG = XLoggerFactory.getXLogger(LDAPAuthServiceImpl.class);

  public LDAPAuthServiceImpl() {
    LOG.entry();

    LOG.exit();
  }

//	@Override
//	public int verify(String username, char[] password) {
//		LOG.entry(username, "***");
//		int result = ICredentialVerifier.AUTH_FORBIDDEN;
////		AUTH_OK
////		AUTH_FORBIDDEN
////		AUTH_FAILED
////		AUTH_CREDENTIALS_REQUIRED
//
//
//		if (!ACCESS.check(new AuthenticateLDAPPermission())) {
//			LOG.debug("authorization check failed");
//			throw new VetoException("Authorization failed");
//
//		} else {
//			LOG.debug("Attempting LDAP authentication for user: {}", username);
//		}
//
//
//		LDAPCachedConnector ldap_conn = LDAPCachedConnector.getInstance();
//
////		ldap_conn.connect();
//		if ( ldap_conn.authenticate(username, password) ) {
//			result = ICredentialVerifier.AUTH_OK;
//			LOG.info("LDAP authentication OK for user: {}", username);
//
//		} else {
//			result = ICredentialVerifier.AUTH_FAILED;
//			LOG.info("LDAP authentication FAILED for user: {}", username);
//		}
//
//		// load/cache user data from LDAP: user details + roles
//
////		ldap_conn.disconnect();
//
//		return LOG.exit(result);
//	}

  @Override
  public int verify(String username, char[] password) {
    LOG.entry(username, "***");
    int result = ICredentialVerifier.AUTH_FORBIDDEN;
//		AUTH_OK
//		AUTH_FORBIDDEN
//		AUTH_FAILED
//		AUTH_CREDENTIALS_REQUIRED

    if (!ACCESS.check(new AuthenticateLDAPPermission())) {
      LOG.debug("authorization check failed");
      throw new VetoException("Authorization failed");

    }
    else {
      LOG.debug("Attempting LDAP authentication for user: {}", username);
    }

    ILDAPConnector ldap_connector = BEANS.get(ILDAPConnector.class);
    LOG.debug("LDAPConnector class: " + ldap_connector.getClass().getName());

    if (ldap_connector.authenticate(username, password)) {
      result = ICredentialVerifier.AUTH_OK;
      LOG.info("LDAP authentication OK for user: {}", username);

    }
    else {
      result = ICredentialVerifier.AUTH_FAILED;
      LOG.info("LDAP authentication FAILED for user: {}", username);
    }

    // load/cache user data from LDAP: user details + roles

    return LOG.exit(result);
  }

}
