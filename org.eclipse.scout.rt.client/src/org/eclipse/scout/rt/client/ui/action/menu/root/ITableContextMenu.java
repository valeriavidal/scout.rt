/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.client.ui.action.menu.root;

import org.eclipse.scout.rt.client.ui.basic.table.ITable;

/**
 * The table menu container is the invisible root container for all context menus on a table.
 */
public interface ITableContextMenu extends IContextMenu {

  @Override
  public ITable getOwner();

  void callOwnerValueChanged();
}
