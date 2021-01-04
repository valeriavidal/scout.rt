package org.eclipse.scout.rt.ldap;

import org.eclipse.scout.rt.platform.service.IService;

public interface ILDAPAuthService extends IService {

  public int verify(String username, char[] password);
}
