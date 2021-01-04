package org.eclipse.scout.rt.ldap;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.security.auth.Subject;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.context.RunContext;
import org.eclipse.scout.rt.platform.context.RunContexts;
import org.eclipse.scout.rt.platform.security.ICredentialVerifier;
import org.eclipse.scout.rt.platform.security.SimplePrincipal;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class LDAPCredentialVerifier implements ICredentialVerifier {

  private static final XLogger LOG = XLoggerFactory.getXLogger(LDAPCredentialVerifier.class);

  @Override
  public int verify(String username, char[] password) throws IOException {
    LOG.entry(username, "***");
    int result = AUTH_FORBIDDEN;

    LOG.debug("Attempting LDAP authentication for user: {}", username);

    // empty passwords are not allowed
    if (StringUtility.isNullOrEmpty(username) || password == null || password.length == 0) {
      result = AUTH_CREDENTIALS_REQUIRED;

    }
    else {

      Subject subject = new Subject();
      subject.getPrincipals().add(new SimplePrincipal("system_ldap_auth"));
      subject.setReadOnly();
      RunContext runContext = RunContexts.copyCurrent(true)
          .withSubject(subject);

      result = runContext.call(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          return BEANS.get(ILDAPAuthService.class).verify(username, password);
        }
      });

    } // else

    return LOG.exit(result);
  }

}
