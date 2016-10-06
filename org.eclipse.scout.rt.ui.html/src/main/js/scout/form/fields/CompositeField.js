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
scout.CompositeField = function() {
  scout.CompositeField.parent.call(this);
};
scout.inherits(scout.CompositeField, scout.FormField);

/**
 * Returns an array of child-fields.
 */
scout.CompositeField.prototype.getFields = function() {
  throw new Error('Not implemented');
};

scout.CompositeField.prototype.visit = function(visitor) {
  this.getFields().forEach(function(field) {
    field.visit(visitor);
  });
  scout.CompositeField.parent.prototype.visit.call(this, visitor);
};

/**
 * Sets the given displayStyle recursively on all fields of the composite field.
 * @override FormField.js
 */
scout.CompositeField.prototype.setDisabledStyle = function(disabledStyle) {
  this.getFields().forEach(function(field) {
    field.setDisabledStyle(disabledStyle);
  });
  scout.CompositeField.parent.prototype.setDisabledStyle.call(this, disabledStyle);
};

/**
 * Enables or disables recursively all fields of the composite field.
 * @override Widget.js
 */
scout.CompositeField.prototype.setEnabled = function(enabled) {
  this.getFields().forEach(function(field) {
    field.setEnabled(enabled);
  });
  scout.CompositeField.parent.prototype.setEnabled.call(this, enabled);
};
