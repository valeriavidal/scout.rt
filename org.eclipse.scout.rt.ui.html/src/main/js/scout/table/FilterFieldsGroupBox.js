/*
 * Copyright (c) 2014-2017 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
import {GroupBox} from '../index';
import {scout} from '../index';

export default class FilterFieldsGroupBox extends GroupBox {

constructor() {
  super();
  this.gridColumnCount = 1;
  this.cssClass = 'filter-fields';
}


_init(model) {
  super._init( model);
  this.filter.addFilterFields(this);
}

/**
 * @override GroupBox.js
 */
_renderProperties($parent) {
  super._renderProperties( $parent);
  this.filter.modifyFilterFields();
}

// TODO [7.0] awe: (filter) es braucht wahrscheinlich auch eine range-validierung? z.B. from muss kleiner sein als to
// Prüfen ob wir eine sequence-box dafür verwenden wollen und dafür eine client-seitige validierung impl., diese
// geschieht heute auf dem UI server. Evtl. wäre auch ein from/to validator für beliebige felder sinnvoll (auch
// ausserhalb einer sequence-box)
addFilterField(objectType, text) {
  var field = scout.create(objectType, {
    parent: this,
    label: this.session.text(text),
    statusVisible: false,
    labelWidthInPixel: 50,
    maxLength: 100,
    updateDisplayTextOnModify: true
  });
  this.addField0(field);
  return field;
}

// TODO [7.0] awe, cgu: (addField): see to-do in TileContainerBox.js
// Added '0' to the name to avoid temporarily to avoid naming conflict with FormField#addField
addField0(field) {
  this.fields.push(field);
  this._prepareFields();
}
}
