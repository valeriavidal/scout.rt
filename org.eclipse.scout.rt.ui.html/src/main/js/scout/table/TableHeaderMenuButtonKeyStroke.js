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
import {KeyStroke} from '../index';
import {keys} from '../index';

export default class TableHeaderMenuButtonKeyStroke extends KeyStroke {

constructor(field) {
  super();
  this.field = field;
  this.which = [keys.ENTER, keys.SPACE];
  this.renderingHints.render = true;
  this.stopPropagation = true;
}


handle(event) {
  this.field.doAction();
}
}
