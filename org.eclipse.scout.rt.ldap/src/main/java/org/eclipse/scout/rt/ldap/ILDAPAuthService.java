package org.eclipse.scout.rt.ldap;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface ILDAPAuthService extends IService {

  public int verify(String username, char[] password);
}
