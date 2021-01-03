package org.eclipse.scout.rt.ldap.security;

import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.security.AbstractPermission;

public class AuthenticateLDAPPermission extends AbstractPermission {

  private static final long serialVersionUID = 1L;

  public static final String NAME = "auth.ldap.authenticate";

  public AuthenticateLDAPPermission() {
    super(NAME);
  }

  @Override
  public String getAccessCheckFailedMessage() {
    return TEXTS.get("YouAreNotAllowedToAuthenticateLDAP");
  }
}
