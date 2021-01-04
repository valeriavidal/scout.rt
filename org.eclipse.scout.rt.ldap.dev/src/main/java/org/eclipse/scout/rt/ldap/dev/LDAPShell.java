/*
 * Copyright (c) 2021 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.rt.ldap.dev;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Development tools that may be used together with <code>OpenLDAP (osixia docker-openldap)</code>
 * <p>
 * Create LDAP docker container using for scout example
 * <code>docker run -p 389:389 -p 636:636 --name helloworld-openldap-container --env LDAP_ORGANISATION="Helloworld Inc." --env LDAP_DOMAIN="helloworld.org" --env LDAP_ADMIN_PASSWORD="adminpw" -v C:/dev/workspaces/scout-ldap/org.eclipse.scout.rt/org.eclipse.scout.rt.ldap.dev/ext-ldif:/ext-ldif --detach osixia/openldap:1.4.0 --copy-service</code>
 */
public class LDAPShell {
  private final String m_commandPrefix;
  private final String m_domainDN;
  private final String m_authString;

  /**
   * Uses scout example settings
   */
  public LDAPShell() {
    this("localhost", "admin", "adminpw", "helloworld.org", "docker exec helloworld-openldap-container");
  }

  public LDAPShell(String host, String adminUser, String adminPass, String domain, String commandPrefix) {
    m_commandPrefix = commandPrefix;
    m_domainDN = Arrays.stream(domain.split("[.]")).map(s -> "dc=" + s).collect(Collectors.joining(","));
    m_authString = "-H ldap://" + host + " -x -D \"cn=" + adminUser + "," + m_domainDN + "\" -w \"" + adminPass + "\"";
  }

  public void ldapsearch(String term) throws Exception {
    if (!term.isEmpty()) {
      term += ",";
    }
    ldapExec("ldapsearch " + m_authString + " -b " + term + m_domainDN + " " + term);
  }

  public void ldapadd(String file) throws Exception {
    ldapExec("ldapadd " + m_authString + " -f " + file);
  }

  protected void ldapExec(String ldapCommand) throws InterruptedException, IOException {
    String cmd = (m_commandPrefix + " " + ldapCommand).trim();
    ProcessBuilder pb = new ProcessBuilder(cmd.split("\\s+"));
    pb.environment().putAll(System.getenv());
    Process process = pb.inheritIO().start();
    process.waitFor(10L, TimeUnit.SECONDS);
    int exitCode = process.exitValue();
    if (exitCode != 0) {
      System.out.println("ERROR: code " + exitCode);
    }
    Thread.sleep(200);
  }
}
