/*******************************************************************************
 * Copyright (c) 2014-2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
scout.HorizontalGroupBoxBodyGrid = function() {
  scout.HorizontalGroupBoxBodyGrid.parent.call(this);
};
scout.inherits(scout.HorizontalGroupBoxBodyGrid, scout.AbstractGroupBoxBodyGrid);

scout.AbstractGroupBoxBodyGrid.prototype.layoutAllDynamic = function(fields) {
  var matrix = new scout.HorizontalGridMatrixGroupBox(this.getGridColumnCount());
  matrix.computeGridData(fields);
  this.gridRows = matrix.getRowCount();
};