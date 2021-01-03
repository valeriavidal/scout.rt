package org.eclipse.scout.rt.ldap;

import java.util.Map;
import java.util.Set;

import org.apache.directory.api.ldap.model.name.Dn;
import org.eclipse.scout.rt.platform.service.IService;

public interface ILDAPConnector extends IService {

  public boolean authenticate(String username, char[] password);

  public Set<Dn> getLdapGroupsForUser(Dn userDn);

  public Map<Dn, Set<Dn>> getAllLdapGroups();

}
