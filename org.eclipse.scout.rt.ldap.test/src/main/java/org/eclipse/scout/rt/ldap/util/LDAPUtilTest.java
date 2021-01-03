package org.eclipse.scout.rt.ldap.util;

import static org.junit.Assert.assertEquals;

import org.apache.directory.api.ldap.model.name.Dn;
import org.junit.Test;

public class LDAPUtilTest {

  @Test
  public void testGetDN() {

    String inputStr = "uid=scout,ou=services,dc=example,dc=net";
    Dn dn = LDAPUtil.getDn(inputStr);

    assertEquals(inputStr, dn.getName());
  }

}
