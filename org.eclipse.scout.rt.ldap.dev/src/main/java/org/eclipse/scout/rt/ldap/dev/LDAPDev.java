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

import org.eclipse.scout.rt.platform.job.Jobs;

public final class LDAPDev {

  public static void main(String[] args) throws Exception {
    Jobs.getJobManager();//XXX
    LDAPShell shell = new LDAPShell();
    //shell.ldapadd("/ext-ldif/groups.ldif");
    //shell.ldapadd("/ext-ldif/users.ldif");
    shell.ldapsearch("");
    System.exit(0);
  }
}
